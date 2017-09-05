package jneko;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Z_MA4000Engine {
    public class UnconfiguredONT {
        public String   eltx, slot, channel;
        public UnconfiguredONT(String _eltx, String _slot, String _channel) {
            eltx    = _eltx;
            slot    = _slot;
            channel = _channel;
        }
    }
    
    public class ACSONT {
        public String   eltx, profile, hwName, firmware, ip, lastTime;
        public ACSONT(String _eltx, String _profile, String _hwName, String _firmware, String _ip, String _lastTime) {
            eltx = _eltx;
            profile = _profile;
            hwName = _hwName;
            firmware = _firmware;
            ip = _ip;
            lastTime = _lastTime;
        }
    }
    
    public class ACSONTUsers {
        public String   eltx, username;
        public ACSONTUsers(String _eltx, String _username) {
            eltx = _eltx;
            username = _username;
        }
    }
    
    public class SlotsMACsONTs {
        public String   eltx, slot, channel, vlan, mac;
        public SlotsMACsONTs(String _eltx, String _slot, String _channel, String _vlan, String _mac) {
            eltx    = _eltx;
            slot    = _slot;
            channel = _channel;
            vlan    = _vlan;
            mac     = _mac;
        }
    }
    
    private final       String      ELTXSerialPattern   = "[Ee][Ll][Tt][Xx][\\da-zA-Z]{8}";
    private             boolean     MA4000Debug         = true;
    public static final boolean     Slots[] = new boolean[] {true, true, false, false, false, false, false, false};
    
    /*********************************************************************************************************/
    /* АХТУНГ: НАДО СРОЧНО ОТРЕФАКТОРИТЬ ЭТОТ МОДУЛЬ И МОДУЛЬ ТЕЛНЕТА, СДЕЛАВ НОРМАЛЬНУЮ ОБРАБОТКУ ОШИБОК!!! */
    /* А ТО ДАЖЕ У САМОГО АВТОРА ВОЗНИКАЕТ СТОЙКОЕ ЖЕЛАНИЕ УЕХАТЬ В ИНДИЮ.                                   */
    /*********************************************************************************************************/
    
    public Z_MA4000Engine() {}
    
    public int CreateNewONTOnSlot(String ont, String slot, String vlan, String pmgmt, String psvc, String pmcast) {
        if (Z_TelnetEngine.IsConnected() == false) return 1;
        try {
            Z_TelnetEngine.CommandExecNoRead("slot "+slot, ")>");
            Z_TelnetEngine.CommandExecNoRead("pon", ")>");
            Z_TelnetEngine.CommandExecNoRead("enable", ")#");
            Z_TelnetEngine.CommandExecNoRead("add ont "+ont, ")#");
            Z_TelnetEngine.CommandExecNoRead("ont_sn "+ont, ")#");
            Z_TelnetEngine.CommandExecNoRead("set description \"JNEKOCC\"", ")#");
            Z_TelnetEngine.CommandExecNoRead("set services_override 0 enable true", ")#");
            Z_TelnetEngine.CommandExecNoRead("set services_override 0 customer_vid "+vlan, ")#");
            Z_TelnetEngine.CommandExecNoRead("set profile_services "+psvc, ")#");
            Z_TelnetEngine.CommandExecNoRead("set profile_management "+pmgmt, ")#");
            Z_TelnetEngine.CommandExecNoRead("set profile_multicast "+pmcast, ")#");
            Z_TelnetEngine.CommandExecNoRead("do commit", ")#");
            Z_TelnetEngine.CommandExecNoRead("do confirm", ")#");
            Z_TelnetEngine.CommandExecNoRead("disable", ")>");
            Z_TelnetEngine.CommandExecNoRead("top", ">");
            return 0;
        } catch (TelnetExtraException ex) {
            __toLog("ERROR: "+Z_TelnetEngine.GetLastCommand());
            //Logger.getLogger(Z_MA4000Engine.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        }
    }
    
    public int CreateNewONTOnACS(String ont, String wifi_ssid, String wifi_pass, String pppoe_login, String pppoe_pass, 
                                                            String vlan, String admin_pass, String user_pass, String profile, String username, String slot) {
        if (Z_TelnetEngine.IsConnected() == false) return 1;
        try {
            Z_TelnetEngine.CommandExecNoRead("acs", ")>"); 
            Z_TelnetEngine.CommandExecNoRead("user", ")>"); 
            Z_TelnetEngine.CommandExecNoRead("enable", ")#"); 
            Z_TelnetEngine.CommandExecNoRead("add user \""+username+"\"", ")#"); 
            Z_TelnetEngine.CommandExecNoRead("user \""+username+"\"", ")#"); 
            Z_TelnetEngine.CommandExecNoRead("set pon_serial \""+ont+"\"", ")#"); 
            Z_TelnetEngine.CommandExecNoRead("set profile \""+profile+"\"", ")#"); 
            Z_TelnetEngine.CommandExecNoRead("set internet_vlanid "+vlan+"", ")#"); 
            
            if (wifi_ssid.trim().length() > 1) {
                Z_TelnetEngine.CommandExecNoRead("set wifi_ssid \""+wifi_ssid+"\"", ")#"); 
                Z_TelnetEngine.CommandExecNoRead("set wifi_password \""+wifi_pass+"\"", ")#"); 
                Z_TelnetEngine.CommandExecNoRead("set wifi_enable enable", ")#"); 
                Z_TelnetEngine.CommandExecNoRead("set wifi_encoding 11i", ")#"); 
            }
            
            Z_TelnetEngine.CommandExecNoRead("set ppp_login \""+pppoe_login+"\"", ")#"); 
            Z_TelnetEngine.CommandExecNoRead("set ppp_password \""+pppoe_pass+"\"", ")#"); 
            Z_TelnetEngine.CommandExecNoRead("set admin_password \""+admin_pass+"\"", ")#"); 
            Z_TelnetEngine.CommandExecNoRead("set user_password \""+user_pass+"\"", ")#"); 
            Z_TelnetEngine.CommandExecNoRead("commit", ")#"); 
            Z_TelnetEngine.CommandExecNoRead("top", "#"); 
            Z_TelnetEngine.CommandExecNoRead("slot "+slot, ")#"); 
            Z_TelnetEngine.CommandExecNoRead("pon", ")#"); 
            Z_TelnetEngine.CommandExecNoRead("ont_sn "+ont, ")#"); 
            Z_TelnetEngine.CommandExecNoRead("default", ")#"); 
            Z_TelnetEngine.CommandExecNoRead("disable", ")>"); 
            Z_TelnetEngine.CommandExecNoRead("top", ">"); 
            return 0;
        } catch (TelnetExtraException ex) {
            //Logger.getLogger(Z_MA4000Engine.class.getName()).log(Level.SEVERE, null, ex);
            __toLog("ERROR: "+Z_TelnetEngine.GetLastCommand());
            return 1;
        }
    }
    
    public int ONTDefault(String ont, String slot) {
        if (Z_TelnetEngine.IsConnected() == false) return 1;
        try {
            Z_TelnetEngine.CommandExecNoRead("slot "+slot, ")>"); 
            Z_TelnetEngine.CommandExecNoRead("pon", ")>"); 
            Z_TelnetEngine.CommandExecNoRead("enable", ")#");
            Z_TelnetEngine.CommandExecNoRead("ont_sn "+ont, ")#"); 
            Z_TelnetEngine.CommandExecNoRead("default", ")#"); 
            Z_TelnetEngine.CommandExecNoRead("disable", ")>"); 
            Z_TelnetEngine.CommandExecNoRead("top", ">"); 
            return 0;
        } catch (TelnetExtraException ex) {
            //Logger.getLogger(Z_MA4000Engine.class.getName()).log(Level.SEVERE, null, ex);
            __toLog("ERROR: "+Z_TelnetEngine.GetLastCommand());
            return 1;
        }
    }

    public ArrayList<ACSONTUsers> GetACSONTUsers() {
        if (Z_TelnetEngine.IsConnected() == false) return null;
        ArrayList<ACSONTUsers> list = new ArrayList<>();
        try {
            Z_TelnetEngine.CommandExecNoRead("acs", ">");
            Z_TelnetEngine.CommandExecNoRead("user", ">");

            final String buf1 = Z_TelnetEngine.CommandExecWithRead("show list", ")>");
            if (buf1 == null) {
                __toLog("ERROR: CommandExecWithRead - show list");
                return null;
            }

            final String arr1[] = buf1.replace("\r", "").split("\n");
            for (String buf2 : arr1) {
                String arr2[] = buf2.trim().replaceAll("\\s{2,}", " ").split(" ");
                if (arr2.length > 2) {
                    if (arr2[2].matches(ELTXSerialPattern)) {
                        list.add(new ACSONTUsers(arr2[2], arr2[1]));
                    }
                }
            }

            Z_TelnetEngine.CommandExecNoRead("top", ">");
        } catch (TelnetExtraException ex) {
            Logger.getLogger(Z_MA4000Engine.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        if (list.size() <= 0) return null;
        return list;
    }
    
    public ArrayList<SlotsMACsONTs> GetSlotsMACsONTs() {
        if (Z_TelnetEngine.IsConnected() == false) return null;
        ArrayList<SlotsMACsONTs> list = new ArrayList<>();
        
        for (int i=0; i<Slots.length; i++) {
            //System.err.println("Slots["+i+"]=="+Slots[i]);
            
            if (Slots[i]==true) {
                if (Z_TelnetEngine.CommandExecNoReadNE("slot "+i, ">") != 0) {
                    __toLog("ERROR: CommandExecNoRead - slot");
                    return null;
                }
                
                if (Z_TelnetEngine.CommandExecNoReadNE("pon", ">") != 0) {
                    __toLog("ERROR: CommandExecNoRead - pon");
                    break;
                }
                
                String buf1 = Z_TelnetEngine.CommandExecWithRead("show mac", ")>");
                if (buf1 == null) {
                    __toLog("ERROR: CommandExecWithRead - show mac");
                    break;
                }
                
                ArrayList<String> a1 = new ArrayList<>(), a2 = new ArrayList<>();
                String arr1[] = buf1.replace("\r", "").split("\n");
                for (String buf2 : arr1) {
                    String arr2[] = buf2.trim().replaceAll("\\s{2,}", " ").split(" ");
                    if (arr2.length > 3) {
                        if (arr2[1].matches(ELTXSerialPattern)) {
                            a1.add(buf2);
                            a2.add(arr2[1]);
                        }
                    }
                }
                
                if ((a1.size() <= 0) || (a2.size() <= 0)) return null;
                
                Set<String> set1 = new HashSet<>(a2);
                for (String _item : set1) {
                    String macs = "";
                    for (String line : a1) {
                        if (line.toLowerCase().contains(_item.toLowerCase())) {
                            String arr2[] = line.trim().replaceAll("\\s{2,}", " ").split(" ");
                            if (arr2.length > 3) {
                                if (arr2[1].matches(ELTXSerialPattern)) {
                                    if (macs.toUpperCase().contains(arr2[arr2.length-1].toUpperCase().trim()) == false) {
                                        macs += arr2[arr2.length-1].toUpperCase().trim() + " ";
                                    }
                                }
                            }
                        }
                    }
                    if (macs.length() > 1) {
                        list.add(new SlotsMACsONTs(_item, i+"", "255", "255", macs.trim().replace(" ", ",")));
                        //System.err.println("ont="+_item+"; mac="+macs+"; slot="+i);
                    }
                }
                
                if (Z_TelnetEngine.CommandExecNoReadNE("top", ">") != 0) {
                    __toLog("ERROR: CommandExecNoRead - top");
                    break;
                }
            }
        }
        
        if (list.size() <= 0) return null;
        return list;
    }
    
    public String GetRawACSONTInfo(String ONT) {
        if (Z_TelnetEngine.IsConnected() == false) return null;
        if (ONT.matches(ELTXSerialPattern) == false) return null;
        
        StringBuilder sbd = new StringBuilder();
        
        if (Z_TelnetEngine.CommandExecNoReadNE("acs", ">") != 0) {
            __toLog("ERROR: CommandExecNoRead - /acs");
            return null;
        }
        
        if (Z_TelnetEngine.CommandExecNoReadNE("ont", ">") != 0) {
            __toLog("ERROR: CommandExecNoRead - /acs/ont");
            return null;
        }
        
        final String buf1 = Z_TelnetEngine.CommandExecWithRead("show full "+ONT, "ont)>");
        if (buf1 == null) {
            __toLog("ERROR: CommandExecWithRead - show full");
            return null;
        }
        
        final int bIndex = buf1.indexOf("Ont parameters:");
        sbd.append(buf1.replaceAll("[Mm][Aa][0-9]{4}(.+)[\\>]", "").substring(bIndex));
        sbd.append("\n\n\n");
        
        final String strOwner = "Subscriber =";
        final int ownerBI = buf1.indexOf(strOwner) + strOwner.length();
        final int ownerEI = buf1.indexOf("\n", ownerBI);
        
        final String ow;
        if ((ownerBI > 0) && (ownerEI > 0)) ow = buf1.substring(ownerBI, ownerEI).replace("\r", "").replace("\"", "").replace("=", "").trim(); else ow = "";
        
        if ((ownerBI > 0) && (ownerEI > 0) && (ow.length() > 1)) {
            if (Z_TelnetEngine.CommandExecNoReadNE("top", ">") != 0) {
                __toLog("ERROR: CommandExecNoRead - top");
                return null;
            }
            
            if (Z_TelnetEngine.CommandExecNoReadNE("acs", ">") != 0) {
                __toLog("ERROR: CommandExecNoRead - /acs");
                return null;
            }
            
            if (Z_TelnetEngine.CommandExecNoReadNE("user", ">") != 0) {
                __toLog("ERROR: CommandExecNoRead - /acs/user");
                return null;
            }
            
            final String buf2 = Z_TelnetEngine.CommandExecWithRead("show user "+ow, ")>");
            if (buf2 == null) {
                __toLog("ERROR: CommandExecWithRead - show conf");
                return null;
            }
            
            final int bIndex2 = buf2.indexOf("Information about subscriber");
            sbd.insert(0, buf2.replaceAll("[Mm][Aa][0-9]{4}(.+)[\\>]", "").substring(bIndex2) + "\n\n\n");
        }
        
        if (Z_TelnetEngine.CommandExecNoReadNE("top", ">") != 0) {
            __toLog("ERROR: CommandExecNoRead - top");
            return null;
        }
        
        return sbd.substring(0).replace("\r", "").replaceAll("[\n]{3,}", "\n\n");
    }
    
    public ArrayList<String> GetACSProfiles() {
        if (Z_TelnetEngine.IsConnected() == false) return null;
        ArrayList<String> list = new ArrayList<>();
        
        if (Z_TelnetEngine.CommandExecNoReadNE("acs", ">") != 0) {
            __toLog("ERROR: CommandExecNoRead - acs");
            return null;
        }
        
        if (Z_TelnetEngine.CommandExecNoReadNE("profile", ">") != 0) {
            __toLog("ERROR: CommandExecNoRead - profile");
            return null;
        }
        
        final String buf1 = Z_TelnetEngine.CommandExecWithRead("show list", ")>");
        if (buf1 == null) {
            __toLog("ERROR: CommandExecWithRead - show list");
            return null;
        }
        
        final String arr1[] = buf1.replace("\r", "").split("\n");
        for (String buf2 : arr1) {
            String arr2[] = buf2.trim().replaceAll("\\s{2,}", " ").split(" ");
            if (arr2.length > 2) {
                if (arr2[0].matches("[1-9]{1,3}[\\:]")) {
                    list.add(arr2[1]);
                }
            }
        }
        
        if (Z_TelnetEngine.CommandExecNoReadNE("top", ">") != 0) {
            __toLog("ERROR: CommandExecNoRead - top");
            return null;
        }

        if (list.size() <= 0) return null;
        return list;
    }
    
    //MA4000(slot-0-pon)> show ont list all channel
    public ArrayList<String> GetSlotPONAll(int slot) {
        if (Z_TelnetEngine.IsConnected() == false) return null;
        if ((slot < 0) || (slot > 7)) return null;
        
        ArrayList<String> list = new ArrayList<>();
        
        if (Z_TelnetEngine.CommandExecNoReadNE("slot "+slot, ">") != 0) {
            __toLog("ERROR: CommandExecNoRead - /slot");
            return null;
        }
        
        if (Z_TelnetEngine.CommandExecNoReadNE("pon", ">") != 0) {
            __toLog("ERROR: CommandExecNoRead - /slot/pon");
            return null;
        }
        
        final String buf1 = Z_TelnetEngine.CommandExecWithRead("show ont list all channel", ")>");
        if (buf1 == null) {
            __toLog("ERROR: CommandExecWithRead - show ont list all channel");
            return null;
        }
        
        final String arr1[] = buf1.replace("\r", "").split("\n");
        for (String buf2 : arr1) {
            String arr2[] = buf2.trim().replaceAll("\\s{2,}", " ").split(" ");
            if (arr2.length > 5) {
                if (arr2[1].matches(ELTXSerialPattern)) {
                    list.add(arr2[1]);
                }
            }
        }
        
        if (Z_TelnetEngine.CommandExecNoReadNE("top", ">") != 0) {
            __toLog("ERROR: CommandExecNoRead - top");
            return null;
        }

        if (list.size() <= 0) return null;
        return list;
    }
    
    
    
    public ArrayList<ACSONT> GetACSONT() {
        if (Z_TelnetEngine.IsConnected() == false) return null;
        ArrayList<ACSONT> list = new ArrayList<>();
        
        if (Z_TelnetEngine.CommandExecNoReadNE("acs", ">") != 0) {
            __toLog("ERROR: CommandExecNoRead - /acs");
            return null;
        }
        
        if (Z_TelnetEngine.CommandExecNoReadNE("ont", ">") != 0) {
            __toLog("ERROR: CommandExecNoRead - /acs/ont");
            return null;
        }
        
        final String buf1 = Z_TelnetEngine.CommandExecWithRead("show list sort lastcontact", "ont)>");
        if (buf1 == null) {
            __toLog("ERROR: CommandExecWithRead - show list sort lastcontact");
            return null;
        }
        
        final String arr1[] = buf1.replace("\r", "").split("\n");
        for (String buf2 : arr1) {
            String arr2[] = buf2.trim().replaceAll("\\s{2,}", " ").split(" ");
            if (arr2.length > 5) {
                if (arr2[1].matches(ELTXSerialPattern)) {
                    list.add(new ACSONT(arr2[1], arr2[2], arr2[3], arr2[4], 
                            arr2[5].replace("http:", "").replace("/", "").replaceAll("[\\:][0-9]{1,7}", ""), 
                            arr2[6]));
                }
            }
        }
        
        if (Z_TelnetEngine.CommandExecNoReadNE("top", ">") != 0) {
            __toLog("ERROR: CommandExecNoRead - top");
            return null;
        }

        if (list.size() <= 0) return null;
        return list;
    }
    
    
    public ArrayList<UnconfiguredONT> GetUnconfiguredONT() { // OK
        if (Z_TelnetEngine.IsConnected() == false) return null;
        
        ArrayList<UnconfiguredONT> list = new ArrayList<>();
        
        if (Z_TelnetEngine.CommandExecNoReadNE("express", "(express)>") != 0) {
            __toLog("ERROR: CommandExecNoRead - express");
            return null;
        }
        
        final String buf1 = Z_TelnetEngine.CommandExecWithRead("show ont unconfigured all", "(express)>");
        if (buf1 == null) {
            __toLog("ERROR: CommandExecWithRead - show ont unconfigured all");
            return null;
        }
        
        final String arr1[] = buf1.replace("\r", "").split("\n");
        for (String buf2 : arr1) {
            String arr2[] = buf2.trim().replaceAll("\\s{2,}", " ").split(" ");
            if (arr2.length > 3) {
                if (arr2[1].matches(ELTXSerialPattern)) {
                    list.add(new UnconfiguredONT(arr2[1], arr2[2], arr2[3]));
                }
            }
        }
        
        if (Z_TelnetEngine.CommandExecNoReadNE("top", ">") != 0) {
            __toLog("ERROR: CommandExecNoRead - top");
            return null;
        }
        
        if (list.size() <= 0) return null;
        return list;
    }
    
    private void __toLog(String out) {
         if (MA4000Debug) System.out.print(out);
    }
}
