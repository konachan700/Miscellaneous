package osagent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import jnekoserver.JNekoMySQL;

public class Iptables {
    public static final ConcurrentHashMap<String, Integer> 
            FWMarks = new ConcurrentHashMap<>();
    
    public static final ArrayList<String>
            UsersScripts = new ArrayList<>();
    
    public static final int
            CONNLIMIT_PRIV_PORTS_MAXIMUM    = 64,
            CONNLIMIT_PRIV_PORTS_FOR_LOCKED = 16,
            IP_MIN_LEN                      = 7,
            FWMARK_MIN                      = 10,
            FWMARK_MAX                      = 60000,
            FWMARK_START_VALUE              = 100
            ;
    
    private static final String 
            IPTABLES_BIN                    = "/usr/sbin/iptables ",
            PRIV_UDP_PORTS                  = "53,67,68", // UDP-порты, которым выделяем высокий приоритет
            PRIV_TCP_PORTS                  = "53,67,68,80,443,8080", // TCP-порты, которым выделяем высокий приоритет
            TCP_PORTS_FOR_LOCK              = "80,443,8080", // порты HTTP/HTTPS, которые переадресовываем для отображения заглушки
            LOCKED_TCP_PORTS_REDIRECT_TO    = "80", // порт, куда переадресует заблокированных пользователей для выдачи заглушки "нет денег"
            IPTABLES_LOG_FILE               = "./logs/iptables.log",
            TC_BIN                          = "/usr/sbin/tc",
            IFACE_NAME                      = "lan1",
            SCRIPT_PATH                     = "./scripts/",
            USER_SCRIPT_PATH                = "./scripts/users/",
            TEMPLATE_SCRIPT_PATH            = "./scripts/templates/"
            ;

    private static final StringBuilder ScriptBuffer = new StringBuilder();
    
    private static void _L(String s) {
        if (FileIO.AppendToTextFile(IPTABLES_LOG_FILE, s + "\n") > 0) System.out.println(s);
    }
    
    public static synchronized void FullInit() {
        FWMarks.clear();
        UsersScripts.clear();
        
        RegenerateForAllNonLocked();
        RegenerateForAllLocked();
        
        final String AppPath = FileIO.GetAppPath();
        
        final StringBuilder sb1 = new StringBuilder();
        sb1.append("#! /bin/sh\n");
        sb1.append("cat ").append(AppPath).append(TEMPLATE_SCRIPT_PATH.substring(1)).append("iptables.conf | iptables-restore\n\n");
        for (String name : UsersScripts) {
            sb1.append(AppPath).append(name.substring(1)).append("\n");
        }
        
        FileIO.WriteToTextFile(SCRIPT_PATH + "fullReload.sh", sb1.substring(0));
    }

    
    
    private static void PushCommand(String cmd_line) {
        ScriptBuffer.append(cmd_line);
        ScriptBuffer.append("\n");
    }
    
    private static void InitScript(String udid, String fwmark, String name) {
        ScriptBuffer.delete(0, ScriptBuffer.length());
        ScriptBuffer.append("#! /bin/sh\n");
        ScriptBuffer.append("##FWMARK:").append(fwmark).append("\n");
        ScriptBuffer.append("##UDID:").append(udid).append("\n\n");
        ScriptBuffer.append("echo \"Running script for user [").append(name).append("]...\"\n\n");
    }
    
    private static void CommitScript(String udid) {
        final String FileName = USER_SCRIPT_PATH + "usr_" + udid + ".sh";
        
        final File epath = new File(USER_SCRIPT_PATH);
        epath.mkdirs();
        
        FileIO.WriteToTextFile(FileName, ScriptBuffer.substring(0));
        
        File f1 = new File(FileName);
        f1.setExecutable(true);
        
        UsersScripts.add(FileName);
    }
    
    private static void RegenerateForAllLocked() {
        final String locked_users[][] = JNekoMySQL.GetFullTable_SQL("SELECT * FROM Users WHERE (Users.userMoney<0 OR Users.locked>0) ORDER BY did ASC;");
        if (locked_users != null) {
            int counter = FWMARK_START_VALUE;
            for (String[] lu : locked_users) {
                if (lu[8].length() >= IP_MIN_LEN) {
                    CreateForUser(lu[0], lu[8], counter, true, "", "", lu[1]);
                    counter = counter + 10;
                }
            }
        }
    }
    
    private static void RegenerateForAllNonLocked() {
        final String locked_users[][] = JNekoMySQL.GetFullTable_SQL(
                  "SELECT "
                          + "Users.*, TarifPlans.uploadSpeed, TarifPlans.downloadSpeed "
                + "FROM "
                          + "Users "
                + "LEFT JOIN TarifPlans ON (TarifPlans.did=Users.tpID) "
                + "WHERE "
                          + "(Users.userMoney>=0 AND Users.locked=0) "
                + "ORDER BY Users.did ASC;");
        if (locked_users != null) {
            int counter = FWMARK_START_VALUE;
            for (String[] lu : locked_users) {
                if (lu[8].length() >= IP_MIN_LEN) {
                    CreateForUser(lu[0], lu[8], counter, false, lu[18], lu[19], lu[1]);
                    counter = counter + 10;
                }
            }
        }
    }
    
    private static void CreateForUser(String udid, String ip, int fwmark10, boolean locked, String upspeed, String downspeed, String name) {
        if (ip.length() < IP_MIN_LEN) return;
        if ((fwmark10 < FWMARK_MIN) || (fwmark10 > FWMARK_MAX)) return;
        if ((fwmark10 % 10) > 0) return;
        
        InitScript(udid, String.valueOf(fwmark10), name);
        FWMarks.put(udid, new Integer(fwmark10));
        
        final String 
                CHAIN_NAME          = "USR_" + udid;
        
        final String 
                hex_default_class       = Integer.toHexString(fwmark10),
                hex_prio_class          = Integer.toHexString(fwmark10 + 1),
                default_class           = String.valueOf(fwmark10),
                prio_class              = String.valueOf(fwmark10 + 1),
                default_subclass        = String.valueOf(fwmark10 + 2),
                prio_subclass           = String.valueOf(fwmark10 + 3);
        
        /* В данном случае из-за невозможности определить, существовали ли старые правила (а вдруг сервер падал?) тут мы удаляем все и заново заводим. */
        /* Не оптимально, зато гарантия на отсутствие "хвостов". */
        PushCommand(IPTABLES_BIN + "-t mangle -D FORWARD -d " + ip + "/32 -j " + CHAIN_NAME);
        PushCommand(IPTABLES_BIN + "-t mangle -F " + CHAIN_NAME);
        PushCommand(IPTABLES_BIN + "-t mangle -X " + CHAIN_NAME);
        
        PushCommand(IPTABLES_BIN + "-t filter -D FORWARD -d " + ip + "/32 -j " + CHAIN_NAME);
        PushCommand(IPTABLES_BIN + "-t filter -F " + CHAIN_NAME);
        PushCommand(IPTABLES_BIN + "-t filter -X " + CHAIN_NAME);
        
        PushCommand(IPTABLES_BIN + "-t nat -D PREROUTING -s " + ip + "/32 -p tcp -m multiport --dports " + TCP_PORTS_FOR_LOCK + " -j REDIRECT --to-ports " + LOCKED_TCP_PORTS_REDIRECT_TO);
        
        PushCommand(IPTABLES_BIN + "-t mangle -N " + CHAIN_NAME);
        PushCommand(IPTABLES_BIN + "-t mangle -A FORWARD -d " + ip + "/32 -j " + CHAIN_NAME);
        
        PushCommand(IPTABLES_BIN + "-t filter -N " + CHAIN_NAME);
        PushCommand(IPTABLES_BIN + "-t filter -I FORWARD 1 -d " + ip + "/32 -j " + CHAIN_NAME);

        if (locked) {
            PushCommand(IPTABLES_BIN + "-t filter -A " + CHAIN_NAME + " -p tcp -m tcp --dport 0:1024 -m connlimit --connlimit-above " + CONNLIMIT_PRIV_PORTS_FOR_LOCKED + " --connlimit-mask 32 --connlimit-saddr -j REJECT --reject-with tcp-reset");
            PushCommand(IPTABLES_BIN + "-t nat -A PREROUTING  -s " + ip + "/32 -p tcp -m multiport --dports " + TCP_PORTS_FOR_LOCK + " -j REDIRECT --to-ports " + LOCKED_TCP_PORTS_REDIRECT_TO);
            PushCommand(IPTABLES_BIN + "-t filter -A " + CHAIN_NAME + " -j ACCEPT");
        } else {
            PushCommand(IPTABLES_BIN + "-t filter -A " + CHAIN_NAME + " -p tcp -m tcp --dport 0:1024 -m connlimit --connlimit-above " + CONNLIMIT_PRIV_PORTS_MAXIMUM + " --connlimit-mask 32 --connlimit-saddr -j REJECT --reject-with tcp-reset");
            PushCommand(IPTABLES_BIN + "-t filter -A " + CHAIN_NAME + " -j ACCEPT");
            PushCommand(IPTABLES_BIN + "-t mangle -A " + CHAIN_NAME + " -j MARK --set-xmark 0x" + hex_default_class + "/0xffffffff");
            PushCommand(IPTABLES_BIN + "-t mangle -A " + CHAIN_NAME + " -p udp -m multiport --sports " + PRIV_UDP_PORTS + " -j MARK --set-xmark 0x" + hex_prio_class + "/0xffffffff");
            PushCommand(IPTABLES_BIN + "-t mangle -A " + CHAIN_NAME + " -p tcp -m multiport --sports " + PRIV_TCP_PORTS + " -j MARK --set-xmark 0x" + hex_prio_class + "/0xffffffff");
            PushCommand(IPTABLES_BIN + "-t mangle -A " + CHAIN_NAME + " -p icmp -j MARK --set-xmark 0x" + hex_prio_class + "/0xffffffff");
            
            PushCommand(TC_BIN + " class add dev ifb0 parent 1:1 classid 1:" + default_class + " htb rate 1mbit ceil " + upspeed + "kbit burst 8k");
            PushCommand(TC_BIN + " filter add dev ifb0 parent 1:0 protocol ip prio 1 u32 match ip src " + ip + " classid 1:" + default_class);
            
            PushCommand(TC_BIN + " class add dev " + IFACE_NAME + " parent 1:1 classid 1:" + default_class + " htb rate 1mbit ceil " + downspeed + "kbit burst 8k");
            PushCommand(TC_BIN + " class add dev " + IFACE_NAME + " parent 1:" + default_class + " classid 1:" + default_subclass + " htb rate 1mbit ceil " + downspeed + "kbit burst 8k");
            PushCommand(TC_BIN + " class add dev " + IFACE_NAME + " parent 1:" + default_class + " classid 1:" + prio_subclass + " htb rate 1mbit ceil " + downspeed + "kbit burst 8k");
            PushCommand(TC_BIN + " filter add dev " + IFACE_NAME + " parent 1:0 protocol ip prio 0 handle " + prio_class + " fw classid 1:" + prio_subclass);
            PushCommand(TC_BIN + " filter add dev " + IFACE_NAME + " parent 1:0 protocol ip prio 1 handle " + default_class + " fw classid 1:" + default_subclass);           
        }
        
        CommitScript(udid);
    }
}
