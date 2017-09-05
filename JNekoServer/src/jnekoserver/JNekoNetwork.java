package jnekoserver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import osagent.Iptables;

public class JNekoNetwork {
    private static String 
            TEMPLATES_DHCPD_CONF    = "templates/dhcpd.conf",
            TEMPLATES_JNEKO_TAG     = "##JNEKO-INCLUDE-STATIC##",
            ORIGIN_DHCPD_CONF       = "/etc/dhcpd.conf", // НА РЕАЛЬНОМ СЕРВЕРЕ ДОБАВИТЬ НАЧАЛЬНЫЙ СЛЕШ !!!
            
            TEMPLATES_IPTABLES      = "templates/iptables",
            TEMPLATES_REDIRECT_TAG  = "##JNEKO_IPTABLE_REDIRECT_LOCKED##",
            TEMPLATES_IPTABLES_TAG  = "##JNEKO_IPTABLE_SETMARK##",
            TEMPLATES_IPT_FILTER    = "##JNEKO_IPTABLE_FILTER##",
            ORIGIN_IPTABLES         = "/etc/sysconfig/iptables", // НА РЕАЛЬНОМ СЕРВЕРЕ ДОБАВИТЬ НАЧАЛЬНЫЙ СЛЕШ !!!
            
            TEMPLATES_TC_QDISC_TAG  = "##JNEKO_TC_QDISC_TAG##",
            TEMPLATES_TC_FILTER_TAG = "##JNEKO_TC_FILTER_TAG##",
            TEMPLATES_TC_SCRIPT     = "templates/tc.sh",
            ORIGIN_TC_SCRIPT        = "./jnekotc"
            ;
    
    private static boolean 
            UPLOAD_LIMIT_SET        = true,
            DOWNLOAD_LIMIT_SET      = true;
    
    private static byte[] md5_latest = null;
    
//    @SuppressWarnings("Convert2Lambda")
//    private static final ActionListener NetworkStatListener = new ActionListener() {
//        @Override
//        public void actionPerformed(ActionEvent evt) {
//            if (JNekoMySQL.GetConnectionState() == false) return;
//            
//            String buffer = ReadTextFile("/proc/net/dev");
//            if (buffer == null) return;
//            
//            int ifCount = 0;
//            String sa1[] = buffer.replace("\r", "").split("\n");
//            String SQLINSERT = "INSERT INTO TrafStatLastHour VALUES ";
//            for (String s1 : sa1) {
//                if ((s1.contains("ackets") == false) && (s1.contains("eceive") == false) && (s1.contains("ransmit") == false)) {
//                    String sa2[] = s1.trim().replaceAll("\\s{2,}", " ").split(" ");
//                    long ReceiveBytes = 0, TransmitBytes = 0;
//                    try {
//                        ReceiveBytes = Long.parseLong(sa2[1]);
//                        TransmitBytes = Long.parseLong(sa2[9]);
//                    } catch (Exception e) { }
//                
//                    if ((ReceiveBytes > 0) && (TransmitBytes > 0) && (sa2[0].contains("lo") == false)) {
//                        ifCount++;
//                        SQLINSERT += "("+(new Date().getTime())+", \""+sa2[0].replace(":", "").trim()+"\", "+TransmitBytes+", "+ReceiveBytes+"), ";
//                    }
//                }
//            }
//            
//            SQLINSERT = SQLINSERT.substring(0, SQLINSERT.length()-2) + ";";
//            if (ifCount > 0) {
//                try {
//                    JNekoMySQL.GetConnection().createStatement().executeUpdate(SQLINSERT); 
//                    JNekoMySQL.GetConnection().createStatement().executeUpdate(
//                            "DELETE FROM TrafStatLastHour WHERE did<"+((new Date().getTime()) - (1000*60*60))+";"
//                    );
//                } catch (SQLException ex) { }
//            }
//        }
//    };
    
//    private static final Timer StateTimer = new Timer(1000, NetworkStatListener);
    
//    public static void NetStatTimerStart() {
//        StateTimer.start();
//    }
    
    
    
    private static int GetSettings() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("server.conf");
            Properties p = new Properties();
            p.load(fis);
            
            UPLOAD_LIMIT_SET = p.getProperty("upload_unlimited", "NO").contains("NO"); 
            DOWNLOAD_LIMIT_SET = p.getProperty("download_unlimited", "NO").contains("NO"); 
            
            fis.close();
        } catch (IOException ex) {
            _L("GetSettings() IOException 1: "+ex.getMessage());
            return 1;
        } finally {
            try {
                if (fis != null) fis.close();
            } catch (IOException ex) {
                _L("GetSettings() IOException 2: "+ex.getMessage());
                return 1;
            }
        }
        return 0;
    }
    
    public static void genServerConfig() {
        GetSettings();
        final String saa[][] = JNekoMySQL.GetFullTable_SQL(
                  "SELECT "
                          + "Users.*, TarifPlans.uploadSpeed, TarifPlans.downloadSpeed "
                + "FROM "
                          + "Users "
                + "LEFT JOIN TarifPlans ON (TarifPlans.did=Users.tpID) "
                + "WHERE "
                          + "(Users.userMoney>=0 AND Users.locked=0) "
                + "ORDER BY Users.did ASC;");
        if (saa == null) return;
        
        
        
        final String locked_users[][] = JNekoMySQL.GetFullTable_SQL("SELECT * FROM Users WHERE (Users.userMoney<0 OR Users.locked>0) ORDER BY did ASC;");
        final String locked_users_ipt; 
        if (locked_users != null) {
            final StringBuilder locked_users_ipt_sb = new StringBuilder();
            for (String[] lu : locked_users) {
                if (lu[8].length() >= 7) locked_users_ipt_sb.append("-A PREROUTING -s "+lu[8]+"/32 -p tcp -m multiport --dports 80,443,8080 -j REDIRECT --to-ports 80\n");
            }
            locked_users_ipt = locked_users_ipt_sb.substring(0);
        } else {
            locked_users_ipt = "";
        }

//        boolean change = false;
//        final StringBuilder sb = new StringBuilder();
//        for (String[] sa : saa) {
//            sb.append(sa[8]);  //ip
//            sb.append(sa[9]);  //mac
//        }
//
//        final byte[] md5_current = MD5(sb.substring(0).getBytes());
//        if (MD5Equals(md5_latest, md5_current) == false) {
//            try {
//                //Runtime.getRuntime().exec("/etc/init.d/iptables stop");
//                Runtime.getRuntime().exec("/etc/init.d/jnekotc stop");
//            } catch (IOException ex) { 
//                _L("Runtime.getRuntime().exec() stop error: "+ex.getMessage());
//            }
            
            String dhcpMain = ReadTextFile(TEMPLATES_DHCPD_CONF);
            if (dhcpMain == null) { _L("genDHCPConfig(): file '"+TEMPLATES_DHCPD_CONF+"' not found;"); return; }
            if (dhcpMain.contains(TEMPLATES_JNEKO_TAG) == false) { _L("genDHCPConfig(): file '"+TEMPLATES_DHCPD_CONF+"' not contain JNeko include tag;"); return; }
            
            String iptablesMain = ReadTextFile(TEMPLATES_IPTABLES);
            if (iptablesMain == null) { _L("genDHCPConfig(): file '"+TEMPLATES_IPTABLES+"' not found;"); return; }
            if (iptablesMain.contains(TEMPLATES_IPTABLES_TAG) == false) { _L("genDHCPConfig(): file '"+TEMPLATES_IPTABLES+"' not contain JNeko include tag;"); return; }
            
            String tcMain = ReadTextFile(TEMPLATES_TC_SCRIPT);
            if (tcMain == null) { _L("genDHCPConfig(): file '"+TEMPLATES_TC_SCRIPT+"' not found;"); return; }
            if (tcMain.contains(TEMPLATES_TC_QDISC_TAG) == false) { _L("genDHCPConfig(): file '"+TEMPLATES_TC_SCRIPT+"' not contain JNeko include tag;"); return; }
            if (tcMain.contains(TEMPLATES_TC_FILTER_TAG) == false) { _L("genDHCPConfig(): file '"+TEMPLATES_TC_SCRIPT+"' not contain JNeko include tag;"); return; }

            final StringBuilder 
                    dhcp_static         = new StringBuilder(),
                    iptables_set_mark   = new StringBuilder(),
                    iptables_filter     = new StringBuilder(),
                    tc_filter           = new StringBuilder(), 
                    tc_class            = new StringBuilder();
            
            int counter = 30;
            for (String[] sa : saa) {
                if ((sa[8].length() >= 7) && (sa[9].length() >= 11)) {
                    if ((sa[8].trim().startsWith("172.27.")) || (sa[8].trim().startsWith("10.")) || (sa[8].trim().startsWith("192.168."))) {
                        dhcp_static.append("\thost USR" + sa[8].trim().replace(".", "X") + "X {\n");
                        dhcp_static.append("\t\tfixed-address " + sa[8] + ";\n");
                        dhcp_static.append("\t\thardware ethernet " + sa[9] + ";\n");
                        dhcp_static.append("\t}\n\n");
                    } else {
                        // тут необходимо обрабатывать выделенные IP, но пока не нужно
                    }
                    
                    /* АХТУНГ! ИНДУССКИЙ КОД ВО ВСЕЙ КРАСЕ. */
                    /* особенно в месте где  StringBuilder.append(xxx+yyy+zzz) - это бессмысленно, но если делать "как правильно", будент нечитаемая каша в коде */
                    /* Впоследствии, как пройдет "боевой тест", это место будет вынесено в универсальный шаблон, а код окультурен путем удаления индусятины. */
                    counter++;
                    if (DOWNLOAD_LIMIT_SET) {
                        int 
                                default_class       = counter,
                                prio_class          = counter + 1,
                                default_subclass    = counter + 2,
                                prio_subclass       = counter + 3;

                        /* маркировка транзитных пакетов из мира к пользователю */
                        iptables_set_mark.append("-A FORWARD -d " + sa[8] + "/32 -j MARK --set-xmark 0x0" + Integer.toHexString(default_class) + "/0xffffffff\n");
                        /* Маркировка tcp-пакетов с важных портов */
                        iptables_set_mark.append("-A FORWARD -d " + sa[8] + "/32 -p udp -m multiport --sports 53,67,68 -j MARK --set-xmark 0x0" + Integer.toHexString(prio_class) + "/0xffffffff\n");
                        iptables_set_mark.append("-A FORWARD -d " + sa[8] + "/32 -p tcp -m multiport --sports 53,67,68,80,443,8080 -j MARK --set-xmark 0x0" + Integer.toHexString(prio_class) + "/0xffffffff\n");
                        iptables_set_mark.append("-A FORWARD -d " + sa[8] + "/32 -p icmp -j MARK --set-xmark 0x0" + Integer.toHexString(prio_class) + "/0xffffffff\n");
                        /* Своеобразная защита от хрумера и некоторых парсеров, которые некоторые дятлы запускают без VPN */
                        /* В секции download потому, что входящую мало кто станет делать безлимитной в отличии от исходящей */
                        //iptables_filter.append("-A FORWARD -s " + sa[8] + "/32 -p tcp -m tcp --dport 0:1024 -m connlimit --connlimit-above 64 --connlimit-mask 32 --connlimit-saddr -j REJECT --reject-with tcp-reset\n");
                        
                        tc_class.append("\t$TCBIN class add dev $JNEKOIF parent 1:1 classid 1:" + default_class + " htb rate 1mbit ceil " + sa[19] + "kbit burst 8k\n");
                        tc_class.append("\t$TCBIN class add dev $JNEKOIF parent 1:" + default_class + " classid 1:" + default_subclass + " htb rate 1mbit ceil " + sa[19] + "kbit burst 8k\n");
                        tc_class.append("\t$TCBIN class add dev $JNEKOIF parent 1:" + default_class + " classid 1:" + prio_subclass + " htb rate 1mbit ceil " + sa[19] + "kbit burst 8k\n");
                        
                        tc_filter.append("\t$TCBIN filter add dev $JNEKOIF parent 1:0 protocol ip prio 0 handle " + prio_class + " fw classid 1:" + prio_subclass + "\n");
                        tc_filter.append("\t$TCBIN filter add dev $JNEKOIF parent 1:0 protocol ip prio 1 handle " + default_class + " fw classid 1:" + default_subclass + "\n\n");
                    }
                    
                    counter=counter+4;
                    if (UPLOAD_LIMIT_SET) {
                        //iptables_set_mark.append("-A FORWARD -s " + sa[8] + "/32 -j MARK --set-xmark 0x0" + Integer.toHexString(counter) + "/0xffffffff\n");
                        tc_class.append("\t$TCBIN class add dev ifb0 parent 1:1 classid 1:" + counter + " htb rate 1mbit ceil " + sa[18] + "kbit burst 8k\n");
                        //tc_filter.append("\t$TCBIN filter add dev ifb0 parent 1:0 protocol ip prio 3 handle " + counter + " fw classid 1:" + counter + "\n");
                        tc_class.append("\t$TCBIN filter add dev ifb0 parent 1:0 protocol ip prio 1 u32 match ip src " + sa[8] + " classid 1:" + counter + "\n");
                        // ебаныйжтынахуй. ограничение одновременно исходящей и входящей оказалось не таким уж и простым делом.
                    }  
                }
            }
            
            dhcpMain = dhcpMain.replace(TEMPLATES_JNEKO_TAG, dhcp_static.substring(0));
            if (WriteTextFile(ORIGIN_DHCPD_CONF, dhcpMain) != 0) { _L("genDHCPConfig(): file '"+ORIGIN_DHCPD_CONF+"' cannot be written;"); return; }

            iptablesMain = iptablesMain
                    .replace(TEMPLATES_IPTABLES_TAG, iptables_set_mark.substring(0))
                    .replace(TEMPLATES_IPT_FILTER, iptables_filter.substring(0))
                    .replace(TEMPLATES_REDIRECT_TAG, locked_users_ipt); 
            if (WriteTextFile(ORIGIN_IPTABLES, iptablesMain) != 0) { _L("genDHCPConfig(): file '"+ORIGIN_IPTABLES+"' cannot be written;"); return; }
            
            tcMain = tcMain.replace(TEMPLATES_TC_QDISC_TAG, tc_class.substring(0)).replace(TEMPLATES_TC_FILTER_TAG, tc_filter.substring(0));
            if (WriteTextFile(ORIGIN_TC_SCRIPT, tcMain) != 0) { _L("genDHCPConfig(): file '"+ORIGIN_TC_SCRIPT+"' cannot be written;"); return; }
            
            File f1 = new File(ORIGIN_TC_SCRIPT);
            f1.setExecutable(true);
            
//            md5_latest = md5_current.clone();
//        }
    }
    
    public static void RestartServices() {
        System.out.println("RestartServices..."); 
        try {
            Process p = Runtime.getRuntime().exec("./jnekotc restart");
            InputStream is = p.getInputStream();
            InputStream es = p.getErrorStream();
            p.waitFor();
            
            int b=0;
            while (true) {
                b = is.read();
                if (b != -1) System.out.print((char)b); else break;
            }
            
            while (true) {
                b = es.read();
                if (b != -1) System.err.print((char)b); else break;
            }
            
            is.close();
            es.close();
            
            System.out.println("RestartServices OK"); 
            
//            Iptables.RegenerateForAllLocked();
            
        } catch (IOException | InterruptedException ex) { 
            _L("Runtime.getRuntime().exec() start error: "+ex.getMessage());
        }
    }
    
    private static int WriteTextFile(String path, String data) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            fos.write(data.getBytes(), 0, data.getBytes().length);
            fos.close();
            return 0;
        } catch (IOException ex) {
            _L("WriteTextFile() IOException 1: "+ex.getMessage());
            return 1;
        } finally {
            try {
                if (fos != null) fos.close();
            } catch (IOException ex) {
                _L("WriteTextFile() IOException 2: "+ex.getMessage());
                return 1;
            }
        }
    }

    private static String ReadTextFile(String path) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            int b=0;
            final ByteArrayOutputStream ba = new ByteArrayOutputStream();
            while (true) {
                b = fis.read();
                if (b == -1) break; else ba.write(b);
            }
            
            fis.close();
            return ba.toString();
        } catch (IOException ex) {
            _L("ReadTextFile() IOException 1: "+ex.getMessage());
            return null;
        } finally {
            try {
                if (fis != null) fis.close();
            } catch (IOException ex) {
                _L("ReadTextFile() IOException 2: "+ex.getMessage());
                return null;
            }
        }
    }
    
    private static boolean MD5Equals(byte[] hash1, byte[] hash2) {
        if ((hash1 == null) || (hash2 == null)) return false;
        if (hash1.length != hash2.length) return false;
        for (int i=0; i<hash1.length; i++) if (hash1[i] != hash2[i]) return false;
        return true;
    }
    
    private static byte[] MD5(byte[] unsafe) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(unsafe);
            return md.digest();
        } catch (NoSuchAlgorithmException ex) { }
        return null;
    }
    
    private static void _L(String s) {
        System.out.println(s);
    }
}
