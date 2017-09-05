package connections;

import datasource.JNekoSQLite;
import java.io.IOException;
import java.util.ArrayList;

public class MA4000Wrappers {
    public static String lastCommand = "";
    private static final String ELTXSerialPattern = ".+[Ee][Ll][Tt][Xx][\\da-zA-Z]{8}.+";
    
    @SuppressWarnings("SleepWhileInLoop")
    public static void CommandExecNoRead(String comm, String waitFor) throws MA4000Exception {
        lastCommand = comm;
        
        if (MA4000Telnet.IsConnected() == false) throw new MA4000Exception("Not connected");
        while (MA4000Telnet.IsLocked()) {
            try { Thread.sleep(50); } catch (InterruptedException ex) { throw new MA4000Exception("CommandExecNoRead InterruptedException"); }
        }
        
        if ((MA4000Telnet.telnetIS_MA4000 == null) || (MA4000Telnet.telnetOS_MA4000 == null) || (MA4000Telnet.telnetPW_MA4000 == null)) 
            throw new MA4000Exception("NPE on streams");
        
        if (MA4000Telnet.buffer_MA4000 != null) MA4000Telnet.buffer_MA4000.delete(0, MA4000Telnet.buffer_MA4000.length());
        try { MA4000Telnet.telnetIS_MA4000.reset(); } catch (IOException ex) { }
        
        MA4000Telnet.WriteTo(comm);
        if (MA4000Telnet.WaitFor(waitFor) == 1) throw new MA4000Exception("Connection error, command:["+comm+"], wait:["+waitFor+"]");
    }
    
    @SuppressWarnings("SleepWhileInLoop")
    public static String CommandExecWithRead(String comm, String waitFor) throws MA4000Exception {
        lastCommand = comm;
        
        if (MA4000Telnet.IsConnected() == false) throw new MA4000Exception("Not connected");
        while (MA4000Telnet.IsLocked()) {
            try { Thread.sleep(50); } catch (InterruptedException ex) { throw new MA4000Exception("CommandExecNoRead InterruptedException"); }
        }

        if ((MA4000Telnet.telnetIS_MA4000 == null) || (MA4000Telnet.telnetOS_MA4000 == null) || (MA4000Telnet.telnetPW_MA4000 == null)) 
            throw new MA4000Exception("NPE on streams");
        
        if (MA4000Telnet.buffer_MA4000 != null) MA4000Telnet.buffer_MA4000.delete(0, MA4000Telnet.buffer_MA4000.length());
        
        MA4000Telnet.WriteTo(comm);
        if (MA4000Telnet.WaitFor(waitFor) == 1) throw new MA4000Exception("Connection error, command:["+comm+"], wait:["+waitFor+"]");
        
        return MA4000Telnet.buffer_MA4000.substring(0); 
    }
    
    private static ArrayList<String> __splitNL(String buffer, String pattern) {
        String s[] = buffer.replace("\r", "").split("\n");
        final ArrayList<String> al = new ArrayList<>();
        for (String s1 : s) {
            if (s1.matches(pattern)) al.add(s1);
        }
        return al;
    }
    
    private static String[] __ArrayListTo1xStringArray(ArrayList<String> in) {
        final String rets[] = new String[in.size()];
        for (int i=0; i<in.size(); i++) rets[i] = in.get(i);
        return rets;
    }
    
    private static String[][] __ArrayListTo2xStringArray(ArrayList<String> in) {
        String rets[][] = new String[in.size()][];
        for (int i=0; i<in.size(); i++) {
            rets[i] = in.get(i).trim().replaceAll("\\s{2,}", " ").split(" ");
        }
        return rets;
    }
    
    public static String[][] GetNewONTs() {
        if (MA4000Telnet.IsConnected() == false) return null;
        try {
            CommandExecNoRead("express", ")>"); 
            final String buf = CommandExecWithRead("show ont unconfigured all", "(express)>");
            if (buf == null) return null;
            
            ArrayList<String> al = __splitNL(buf, ELTXSerialPattern);
            String rets[][] = __ArrayListTo2xStringArray(al);
            CommandExecNoRead("top", ">");
            
            return rets;
        } catch (MA4000Exception ex) {
            __toLog("ERROR: "+lastCommand+"; err="+ex.getMessage());
            return null;
        }
    }
    
    public static String[] GetACSProfilesList() {
        if (MA4000Telnet.IsConnected() == false) return null;
        try {
            CommandExecNoRead("acs", ")>"); 
            CommandExecNoRead("profile", ")>"); 
            final String buf = CommandExecWithRead("show list", ")>");
            if (buf == null) return null;  
        
            ArrayList<String> al = new ArrayList<>();
            final String arr1[] = buf.replace("\r", "").split("\n");
            for (String buf2 : arr1) {
                String arr2[] = buf2.trim().replaceAll("\\s{2,}", " ").split(" ");
                if (arr2.length > 2) {
                    if (arr2[0].matches("[0-9]{1,3}[\\:]")) {
                        al.add(arr2[1]);
                    }
                }
            }
            
            final String rets[] = __ArrayListTo1xStringArray(al);
            CommandExecNoRead("top", ">");
            
            return rets;
        } catch (MA4000Exception ex) {
            __toLog("ERROR: "+lastCommand+"; err="+ex.getMessage());
            return null;
        }
    }
    
    public static String[][] GetAllONTsOnSlot(int slot) {
        if (MA4000Telnet.IsConnected() == false) return null;
        try {
            CommandExecNoRead("slot "+slot, ")>"); 
            CommandExecNoRead("pon", ")>"); 
            
            final String buf = CommandExecWithRead("show ont list", ")>");
            if (buf == null) return null;
            
            ArrayList<String> al = __splitNL(buf, ELTXSerialPattern);
            String rets[][] = __ArrayListTo2xStringArray(al);
            CommandExecNoRead("top", ">");
            
            return rets;
        } catch (MA4000Exception ex) {
            __toLog("ERROR: "+lastCommand+"; err="+ex.getMessage());
            return null;
        }
    }
    
    public static String[][] GetAllONTsMACsOnSlot(int slot) {
        if (MA4000Telnet.IsConnected() == false) return null;
        try {
            CommandExecNoRead("slot "+slot, ")>"); 
            CommandExecNoRead("pon", ")>"); 
            
            final String buf = CommandExecWithRead("show mac", ")>");
            if (buf == null) return null;
            
            ArrayList<String> al = __splitNL(buf, ELTXSerialPattern);
            String rets[][] = __ArrayListTo2xStringArray(al);
            CommandExecNoRead("top", ">");
            
            return rets;
        } catch (MA4000Exception ex) {
            __toLog("ERROR: "+lastCommand+"; err="+ex.getMessage());
            return null;
        }
    }
    
    public static String[][] GetAllONTsOnACSUsers() {
        if (MA4000Telnet.IsConnected() == false) return null;
        try {
            CommandExecNoRead("acs", ")>"); 
            CommandExecNoRead("user", ")>"); 
            
            final String buf = CommandExecWithRead("show list", ")>");
            if (buf == null) return null;
            
            ArrayList<String> al = new ArrayList<>();
            final String arr1[] = buf.replace("\r", "").split("\n");
            for (String buf2 : arr1) {
                String arr2[] = buf2.trim().replaceAll("\\s{2,}", " ").split(" ");
                if (arr2.length >= 1) {
                    if (arr2[0].trim().matches("[0-9]{1,4}[\\:]")) {
                        al.add(buf2);
                    }
                }
            }

            String rets[][] = __ArrayListTo2xStringArray(al);
            CommandExecNoRead("top", ">");
            
            return rets;
        } catch (MA4000Exception ex) {
            __toLog("ERROR: "+lastCommand+"; err="+ex.getMessage());
            return null;
        }
    }
    
    public static String[][] GetAllONTsOnACS() {
        if (MA4000Telnet.IsConnected() == false) return null;
        try {
            CommandExecNoRead("acs", ")>"); 
            CommandExecNoRead("ont", ")>"); 
            
            final String buf = CommandExecWithRead("show list all", ")>");
            if (buf == null) return null;
            
            ArrayList<String> al = __splitNL(buf, ELTXSerialPattern);
            String rets[][] = __ArrayListTo2xStringArray(al);
            CommandExecNoRead("top", ">");
            
            return rets;
        } catch (MA4000Exception ex) {
            __toLog("ERROR: "+lastCommand+"; err="+ex.getMessage());
            return null;
        }
    }
    
    public static String[][] GetONTACSAndUserConfig(String ID, int type) {
        if (MA4000Telnet.IsConnected() == false) return null;
        try {
            CommandExecNoRead("acs", ")>"); 
            
            if (type==0) CommandExecNoRead("ont", ")>");
            if (type==1) CommandExecNoRead("user", ")>");
            
            if (type==0) CommandExecNoRead("ont " + ID, ")>");
            if (type==1) CommandExecNoRead("user " + ID, ")>");
                        
            final String buf = CommandExecWithRead("show conf", ")>");
            if (buf == null) return null;
            
            ArrayList<String> al = new ArrayList<>();
            final String arr1[] = buf.replace("\r", "").split("\n");
            
            String pattern = "";
            if (type==0) pattern = ".+acs-ont-serial.+";
            if (type==1) pattern = ".+acs-user-subscriber.+";
            
            for (String s : arr1) {
                if ((s.contains("=")) && (!s.matches(pattern))) {
                    String sx[] = s.split("=", 1);
                    if (sx.length >= 1) {
                        al.add(s.trim().replace("\"", ""));
                    }
                }
            }
            
            final String[][] rets = new String[al.size()][];
            for (int i=0; i<al.size(); i++) rets[i] = al.get(i).split("=");
            
            CommandExecNoRead("top", ">");
            return rets;
        } catch (MA4000Exception ex) {
            __toLog("ERROR: "+lastCommand+"; err="+ex.getMessage());
            return null;
        }
    }
    
    public static int RemoveUser(String user) {
        try {
            CommandExecNoRead("acs", ")>");
            CommandExecNoRead("user", ")>");
            CommandExecNoRead("enable", ")#");
            CommandExecNoRead("delete user "+user, ")#");
            CommandExecNoRead("commit", ")#");
            CommandExecNoRead("disable", ")>");
            CommandExecNoRead("top", ">");
        } catch (MA4000Exception ex) {
            __toLog("ERROR: "+lastCommand+"; err="+ex.getMessage());
            return 1;
        }
        return 0;
    }
    
    public static int RemoveONT(String PONSerial, String Slot) {
        String[][] conf = GetONTACSAndUserConfig(PONSerial, 0);
        if (conf == null) return 1;
        
        String username = null;
        for (String s[] : conf) {
            if (s[0].trim().contains("Subscriber")) {
                username = s[1].trim();
                if (username.length() <= 1) username = null;
                break;
            }
        }
        
        if (username != null) {
            try {
                CommandExecNoRead("acs", ")>");
                CommandExecNoRead("user", ")>");
                CommandExecNoRead("user "+username, ")>");
                CommandExecNoRead("enable", ")#");
                CommandExecNoRead("set pon_serial \"\"", ")#");
                CommandExecNoRead("commit", ")#");
                CommandExecNoRead("disable", ")>");
                CommandExecNoRead("top", ">");
            } catch (MA4000Exception ex) {
                __toLog("ERROR: "+lastCommand+"; err="+ex.getMessage());
                return 2;
            }
        }
        
        try {
            CommandExecNoRead("acs", ")>");
            CommandExecNoRead("ont", ")>");
            CommandExecNoRead("enable", ")#");
            CommandExecNoRead("delete ont "+PONSerial, ")#");
            CommandExecNoRead("commit", ")#");
            CommandExecNoRead("disable", ")>");
            CommandExecNoRead("top", ">");
            
            CommandExecNoRead("slot "+Slot, ")>");
            CommandExecNoRead("pon", ")>");
            CommandExecNoRead("enable", ")#");
            CommandExecNoRead("delete ont "+PONSerial, ")#");
            CommandExecNoRead("do commit", ")#");
            CommandExecNoRead("do confirm", ")#");
            CommandExecNoRead("disable", ")>");
            CommandExecNoRead("top", ">");
        } catch (MA4000Exception ex) {
                __toLog("ERROR: "+lastCommand+"; err="+ex.getMessage());
                return 3;
        }
        return 0;
    }
    
    
    
    public static int CreateNewONTOnACS(String ont, String wifi_ssid, String wifi_pass, String pppoe_login, String pppoe_pass, 
                                                            String vlan, String admin_pass, String user_pass, String profile, String username, String slot) {
        if (MA4000Telnet.IsConnected() == false) return 1;
        try {
            CommandExecNoRead("acs", ")>"); 
            CommandExecNoRead("user", ")>"); 
            CommandExecNoRead("enable", ")#"); 
            CommandExecNoRead("add user \""+username+"\"", ")#"); 
            CommandExecNoRead("user \""+username+"\"", ")#"); 
            CommandExecNoRead("set pon_serial \""+ont+"\"", ")#"); 
            CommandExecNoRead("set profile \""+profile+"\"", ")#"); 
            CommandExecNoRead("set internet_vlanid "+vlan+"", ")#"); 
            
            if (wifi_ssid.trim().length() > 1) {
                CommandExecNoRead("set wifi_ssid \""+wifi_ssid+"\"", ")#"); 
                CommandExecNoRead("set wifi_password \""+wifi_pass+"\"", ")#"); 
                CommandExecNoRead("set wifi_enable enable", ")#"); 
                CommandExecNoRead("set wifi_encoding 11i", ")#"); 
            }
            
            CommandExecNoRead("set ppp_login \""+pppoe_login+"\"", ")#"); 
            CommandExecNoRead("set ppp_password \""+pppoe_pass+"\"", ")#"); 
            CommandExecNoRead("set admin_password \""+admin_pass+"\"", ")#"); 
            CommandExecNoRead("set user_password \""+user_pass+"\"", ")#"); 
            CommandExecNoRead("commit", ")#"); 
            CommandExecNoRead("top", "#"); 
            CommandExecNoRead("slot "+slot, ")#"); 
            CommandExecNoRead("pon", ")#"); 
            CommandExecNoRead("ont_sn "+ont, ")#"); 
            CommandExecNoRead("default", ")#"); 
            CommandExecNoRead("disable", ")>"); 
            CommandExecNoRead("top", ">"); 
            return 0;
        } catch (MA4000Exception ex) {
            __toLog("ERROR: "+lastCommand+"; err="+ex.getMessage());
            return 1;
        }
    }
    
    /* Хотя это может и неправильно, но создаем ONT мы на всех слотах сразу - биллинг заточен под офисы, а там перезды дело частое
       и иной раз клиента надо переключать. Чтобы не было возни с пересозданием клиентов, создаем его сразу везде. 
       Емкость корзины огромна и лишняя запись никак не помешает */
    public static int CreateNewONTOnSlot(String ont, String slot, String vlan, String pmgmt, String psvc, String pmcast) {
        if (MA4000Telnet.IsConnected() == false) return 1;
        try {
            for (int i=0; i<8; i++) {
                if ((JNekoSQLite.ReadAPPSettingsLong("OPT_slot"+i) == 255)) {
                    CommandExecNoRead("slot "+i, ")>");
                    CommandExecNoRead("pon", ")>");
                    CommandExecNoRead("enable", ")#");
                    CommandExecNoRead("add ont "+ont, ")#");
                    CommandExecNoRead("ont_sn "+ont, ")#");
                    CommandExecNoRead("set description \"JNEKOCC\"", ")#");
                    CommandExecNoRead("set services_override 0 enable true", ")#");
                    CommandExecNoRead("set services_override 0 customer_vid "+vlan, ")#");
                    CommandExecNoRead("set profile_services "+psvc, ")#");
                    CommandExecNoRead("set profile_management "+pmgmt, ")#");
                    CommandExecNoRead("set profile_multicast "+pmcast, ")#");
                    CommandExecNoRead("do commit", ")#");
                    CommandExecNoRead("do confirm", ")#");
                    CommandExecNoRead("disable", ")>");
                    CommandExecNoRead("top", ">");
                }
            }
            return 0;
        } catch (MA4000Exception ex) {
            __toLog("ERROR: "+lastCommand+"; err="+ex.getMessage());
            return 1;
        }
    }
    
    public static int ONTDefault(String ont, String slot) {
        if (MA4000Telnet.IsConnected() == false) return 1;
        try {
            CommandExecNoRead("slot "+slot, ")>"); 
            CommandExecNoRead("pon", ")>"); 
            CommandExecNoRead("enable", ")#");
            CommandExecNoRead("ont_sn "+ont, ")#"); 
            CommandExecNoRead("default", ")#"); 
            CommandExecNoRead("disable", ")>"); 
            CommandExecNoRead("top", ">"); 
            return 0;
        } catch (MA4000Exception ex) {
            __toLog("ERROR: "+lastCommand+"; err="+ex.getMessage());
            return 1;
        }
    }
    
    
    
    public static int CorrectONTProfile() {
        if (MA4000Telnet.IsConnected() == false) return 1;
        try {
            for (int i=0; i<2; i++) {
                String[][] onts = GetAllONTsOnSlot(i);
                CommandExecNoRead("enable", "#");
                CommandExecNoRead("slot "+i, "#");
//                CommandExecNoRead("acs", ")#");
//                CommandExecNoRead("ont", ")#");

                for (String[] ontline : onts) {
                    String ont = ontline[1].toUpperCase().trim();
                    if (ont.startsWith("ELTX0800")) {
                        CommandExecNoRead("update firmware ont "+ont+" ntp-rg-r2.12.2.253.fw.bin", ")#");
//                        CommandExecNoRead("ont " + ont, ")#");
//                        CommandExecNoRead("set profile ntp2-v772", ")#");
//                        CommandExecNoRead("commit", ")#");
//                        CommandExecNoRead("exit", ")#");
                        System.err.println("ONT OK: "+ont);
                    }
                }
                
                CommandExecNoRead("top", "#");
                CommandExecNoRead("disable", ">"); 
            }
        } catch (MA4000Exception ex) {
            __toLog("ERROR: "+lastCommand+"; err="+ex.getMessage());
            return 1;
        }
        return 0;
    }
    
    public static int DelStIPFromONTProfile() {
        if (MA4000Telnet.IsConnected() == false) return 1;
        try {
            for (int i=0; i<2; i++) {
                String[][] onts = GetAllONTsOnSlot(i);
                CommandExecNoRead("enable", "#");
                CommandExecNoRead("acs", ")#");
                CommandExecNoRead("ont", ")#");

                for (String[] ontline : onts) {
                    String ont = ontline[1].toUpperCase().trim();
                    CommandExecNoRead("ont "+ont, ")#");
                    String b2 = CommandExecWithRead("show property", ")#");
                    if (b2 != null) {
                        if ((b2.contains("WANIPConnection.1.SubnetMask")) || 
                                (b2.contains("WANIPConnection.1.DefaultGateway")) || 
                                (b2.contains("WANIPConnection.1.ExternalIPAddress"))) {
                            CommandExecNoRead("delete property InternetGatewayDevice.WANDevice.5.WANConnectionDevice.1.WANIPConnection.1.DefaultGateway", ")#");
                            CommandExecNoRead("delete property InternetGatewayDevice.WANDevice.5.WANConnectionDevice.1.WANIPConnection.1.ExternalIPAddress", ")#");
                            CommandExecNoRead("delete property InternetGatewayDevice.WANDevice.5.WANConnectionDevice.1.WANIPConnection.1.SubnetMask", ")#");
                            CommandExecNoRead("commit", ")#");
                            System.err.println("ONT OK: "+ont);
                        }
                    }
                    CommandExecNoRead("exit", ")#");
                }
                
                CommandExecNoRead("top", "#");
                CommandExecNoRead("disable", ">"); 
            }
        } catch (MA4000Exception ex) {
            __toLog("ERROR: "+lastCommand+"; err="+ex.getMessage());
            return 1;
        }
        return 0;
    }
    
    
    //            if (type==0) {
//                final String b2 = CommandExecWithRead("show property", ")#");
//                if (b2 != null) {
//                    if ((b2.contains("WANIPConnection")) && (b2.contains("DefaultGateway")))
//                        CommandExecNoRead("delete property InternetGatewayDevice.WANDevice.5.WANConnectionDevice.1.WANIPConnection.1.DefaultGateway", ")#");
//                    
//                }
//                        
//                        
//                        /*
//                        
//                        MA4000(acs-ont-serial='ELTX0801A07C')# delete property InternetGatewayDevice.WANDevice.5.WANConnectionDevice.1.WANIPConnection.1.DefaultGateway
//Ok
//MA4000(acs-ont-serial='ELTX0801A07C')# 
//Ok
//MA4000(acs-ont-serial='ELTX0801A07C')# 
//Ok
//
//                        */
//            }
    
    
    
    
    
    public static int tempMigrateTo772(String ont, String slot, String user) {
        if (MA4000Telnet.IsConnected() == false) return 1;
        try {
            CommandExecNoRead("slot "+slot, ")>"); 
            CommandExecNoRead("pon", ")>"); 
            CommandExecNoRead("enable", ")#");
            CommandExecNoRead("ont_sn "+ont, ")#"); 
            CommandExecNoRead("set services_override 0 enable true", ")#");
            CommandExecNoRead("set services_override 0 customer_vid 772", ")#"); 
            CommandExecNoRead("do commit", ")#");
            CommandExecNoRead("do confirm", ")#");
            CommandExecNoRead("top", "#");
            CommandExecNoRead("acs", "#");
            CommandExecNoRead("user", ")#");
            CommandExecNoRead("user "+user, ")#");
            CommandExecNoRead("set internet_vlanid 772", ")#");
            CommandExecNoRead("set profile wifi-v772", ")#");
            CommandExecNoRead("commit", ")#");
            CommandExecNoRead("disable", ")>"); 
            CommandExecNoRead("top", ">"); 
            return 0;
        } catch (MA4000Exception ex) {
            __toLog("ERROR: "+lastCommand+"; err="+ex.getMessage());
            return 1;
        }
    }
    
    private static void __toLog(String out) {
         System.out.print(out);
    }
}
