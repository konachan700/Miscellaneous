package datasource;

import connections.JNekoSP;
import connections.MA4000Telnet;
import connections.MA4000Wrappers;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class JNekoServerInfo {
    public interface DHCPEvent {
        public void OnNewLease(String ip, String mac);
    }
    
    private final ArrayList<DHCPEvent> DHCPEvents = new ArrayList<>();
    private final ArrayList<String> DE_IPs = new ArrayList<>();
    
    // /usr/bin/mysqldump --user=jneko --password=jneko jneko DNS_LOGGER > dns.log
    /*********************************************************************************************
     * DHCP DB Section
     **********************************************************************************************/
    public class DHCPLease {
        public String IP, MAC, RAWDATA, SLOT = "", ELTX = "", USER = "", UDID = "", SYSUSR = "";
        public DHCPLease(String ip, String mac, String raw) {
            IP = ip;
            MAC = mac;
            RAWDATA = raw;
        }
    }
    public CopyOnWriteArrayList<DHCPLease> DHCPLeasesA = null;

    /*********************************************************************************************
     * Interface stat Section
     **********************************************************************************************/
//    public class IfaceData {
//        public final int RX_TX_BUFFER_COUNT = 2500;
//        public volatile long[] 
//                LastRX = new long[RX_TX_BUFFER_COUNT], 
//                LastTX = new long[RX_TX_BUFFER_COUNT]; 
//        public String IFName = "";
//        public volatile int TXCounter = 0, RXCounter = 0;
//        public volatile long TXTemp = 0, RXTemp = 0;
//        
//        public synchronized void PushTX(long txval) {
//            long[] txtemp = new long[RX_TX_BUFFER_COUNT];
//            System.arraycopy(LastTX, 0, txtemp, 1, RX_TX_BUFFER_COUNT-1);
//            LastTX = txtemp;
//            
//            if (TXCounter > 0) {
//                LastTX[0] = txval - TXTemp;
//                if (LastTX[0] < 0) LastTX[0] = 0;
//            }
//            
//            TXTemp = txval;
//            TXCounter++;
//        }
//
//        public synchronized void PushRX(long rxval) {
//            long[] rxtemp = new long[RX_TX_BUFFER_COUNT];
//            System.arraycopy(LastRX, 0, rxtemp, 1, RX_TX_BUFFER_COUNT-1);
//            LastRX = rxtemp;
//            
//            if (RXCounter > 0) {
//                LastRX[0] = rxval - RXTemp;
//                if (LastRX[0] < 0) LastRX[0] = 0;
//            }
//            
//            RXTemp = rxval;
//            RXCounter++;
//        }
//        
//        public void ResetAllCounters() {
//            for (int i=0; i<RX_TX_BUFFER_COUNT; i++) {
//                LastRX[i] = 0;
//                LastTX[i] = 0;
//            }
//            RXCounter = 0;
//            TXCounter = 0;
//        }
//    }
//    public ConcurrentHashMap<String, IfaceData> IfacesData = new ConcurrentHashMap<>();
//    public ConcurrentHashMap<String, String> MemoryData = new ConcurrentHashMap<>();
//    public String NetDevRAWData = "";
    public long ReloadCurrentInfoExecTime = 0;
        
    /*********************************************************************************************
     * MA4000 Section
     **********************************************************************************************/
    public final String MAC_PATTERN = "[A-Fa-f0-9]{1,2}\\:[A-Fa-f0-9]{1,2}\\:[A-Fa-f0-9]{1,2}\\:[A-Fa-f0-9]{1,2}\\:[A-Fa-f0-9]{1,2}\\:[A-Fa-f0-9]{1,2}";
    
    public class ONTInfo {
        public  String SLOT, ELTX, SYSUSR = "";
        private final CopyOnWriteArrayList<String> MACs = new CopyOnWriteArrayList<>();
        
        public ONTInfo(String eltx, String slot) {
            SLOT = slot;
            ELTX = eltx;
        }
        
        public void AddMAC(String mac) {
            if (mac == null) return;
            if (mac.length() < 11) return;
            if (mac.matches(MAC_PATTERN)) MACs.add(mac);
        }
        
        public synchronized CopyOnWriteArrayList<String> GetMACs() {
            return MACs;
        }
    }
    
    public ConcurrentHashMap<String, ONTInfo> ONTsInfo = new ConcurrentHashMap<>();
    
    public class UnconfONTInfo {
        public  String SLOT, ELTX, CHANNEL;
        public UnconfONTInfo (String eltx, String slot, String chan) {
            ELTX = eltx;
            SLOT = slot;
            CHANNEL = chan;
        }
    }
    public CopyOnWriteArrayList<UnconfONTInfo> UnconfONTsInfo = new CopyOnWriteArrayList<>();
    
    private String GetMAC(String arr[]) {
        for (int i=0; i<arr.length; i++) {
            if (arr[i].matches(MAC_PATTERN)) 
                if (arr[i-1].contentEquals("4096") == false) return arr[i].toUpperCase();
        }
        return null;
    }
    
    public synchronized CopyOnWriteArrayList<DHCPLease> GetLeases() {
        return DHCPLeasesA;
    }
    
    public synchronized boolean GetUnconfigureONTs() {
        if (MA4000Telnet.IsConnected() == false) return false;
        final String unconf[][] = MA4000Wrappers.GetNewONTs();
        if (unconf == null) return false;
        
        for (String uont[] : unconf) {
            UnconfONTInfo oo = new UnconfONTInfo(uont[1], uont[2], uont[3]);
            UnconfONTsInfo.add(oo);
        }
        
        return true;
    }
    
    public boolean CollectONTAndMACInfo() {
        if (MA4000Telnet.IsConnected() == false) return false;
        String ACSUsers[][] = MA4000Wrappers.GetAllONTsOnACSUsers();
        
        GetUnconfigureONTs();
        
        final ConcurrentHashMap<String, ONTInfo> oit = new ConcurrentHashMap<>();
        for (int i=0; i<8; i++) {
            if ((JNekoSQLite.ReadAPPSettingsLong("OPT_slot"+i) == 255)) {
                String ONTOnSlot[][] = MA4000Wrappers.GetAllONTsOnSlot(i);
                String MACOnSlot[][] = MA4000Wrappers.GetAllONTsMACsOnSlot(i);

                if ((ONTOnSlot != null) && (MACOnSlot != null)) {
                    for (String[] onts : ONTOnSlot) {
                        String slot = i + "";
                        String ont = onts[1].toUpperCase().trim();

                        ONTInfo oi = new ONTInfo(ont, slot);

                        for (String macs[] : MACOnSlot) {
                            if (macs[1].equalsIgnoreCase(ont)) {
                                String mac = GetMAC(macs);
                                if (mac != null) 
                                    oi.AddMAC(mac); 
                            }
                        }

                        for (String userline[] : ACSUsers) {
                            if (userline.length == 3)
                                if (userline[2].equalsIgnoreCase(ont)) oi.SYSUSR = userline[1];
                        }

                        oit.put(ont, oi);
                    }
                }
            }
        }
        
        synchronized (ONTsInfo) {    
            ONTsInfo.clear();
            ONTsInfo.putAll(oit); 
        }
        return true;
    }
    
    public void RegisterDHCPEventListener(DHCPEvent li) {
        if (li != null) DHCPEvents.add(li);
    }
    
    public boolean ReloadCurrentInfo(JNekoSP sp) {
        final long StartTime = new Date().getTime();
        
//        final byte[] meminfo = sp.GetFile("/proc/meminfo");
//        if (meminfo == null) return false;
        
//        final byte[] net_dev = sp.GetFile("/proc/net/dev");
//        if (net_dev == null) return false;
        
        final String remotePath = JNekoSQLite.ReadAPPSettingsString("JNSP_SDHCPDB");
        if (remotePath.length() <= 1) return false;
        
        final byte[] dhcpBuf = sp.GetFile(remotePath);
        if (dhcpBuf == null) return false;
        
        final ArrayList<Map<String,String>> stx = JNekoSP.GetSP().SQLSelect(
                      "SELECT "
                    + "  Users.*, "
                    + "  Towns.svalue    AS TownString, "
                    + "  Streets.svalue  AS StreetString, "
                    + "  Houses.svalue   AS HouseNumber "
                    + "FROM "
                    + "  Users "
                    + "LEFT JOIN Streets ON (Users.streetID=Streets.did) "
                    + "LEFT JOIN Towns   ON (Users.townID=Towns.did) "
                    + "LEFT JOIN Houses  ON (Users.houseID=Houses.did) "
                    + "ORDER BY Users.did DESC;"
        );
        
        if (DHCPLeasesA == null) DHCPLeasesA = new CopyOnWriteArrayList<>();
        DHCPLeasesA.clear();
        
        final String dhcpStr = new String(dhcpBuf);
        final String[] dhcpArr1 = dhcpStr.split("\\}");
        for (String a : dhcpArr1) {
            String[] dhcpArr2 = a.replace("\r", "").split("\n");
            String mac = "", ip = "", user = "";
            for (String b : dhcpArr2) {
                if ((b.contains("lease")) && (b.contains("{"))) ip = b.replace("{", "").replace("lease", "").trim();
                if ((b.contains("hardware ethernet")) && (b.contains(";"))) mac = b.replace(";", "").replace("hardware ethernet", "").trim().toUpperCase();
            }
            
            if ((mac.length() >= 11) && (ip.length() >= 7)) {
                synchronized (ONTsInfo) {
                    DHCPLease dl = new DHCPLease(ip, mac, a);
                    if (ONTsInfo.size() > 0) {
                        Set<String> keyset = ONTsInfo.keySet();
                        for (String key : keyset) {
                            CopyOnWriteArrayList<String> macs = ONTsInfo.get(key).GetMACs();
                            if (macs.size() > 0) {
                                for (String lmac : macs) {
                                    if (mac.equalsIgnoreCase(lmac)) {
                                        dl.ELTX = ONTsInfo.get(key).ELTX;
                                        dl.SLOT = ONTsInfo.get(key).SLOT;
                                        dl.SYSUSR = ONTsInfo.get(key).SYSUSR;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                
                    /* Неактивного долгое время пользователя в списке маков на МА4000 может и не быть, потому достаем его парамерты из профиля */
                    if (stx != null) {
                        for (Map<String,String> st : stx) {
                            if (st.get("mac").toUpperCase().equalsIgnoreCase(mac)) {
                                dl.USER = st.get("userFullName");
                                dl.UDID = st.get("did");
                                dl.ELTX = st.get("eltx");
                                dl.SLOT = st.get("slot");
                            }
                        }
                    }

                    GetLeases().add(dl);
                }
                //DE_IPs.add(ip);
            }
        }
        
        if (GetLeases().size() <= 0) return false;
        
        final ArrayList<String> tempA = new ArrayList<>();
        if (DE_IPs.size() > 0) {
            for (DHCPLease dlx : GetLeases()) {
                boolean f = true;
                for (String aaa : DE_IPs) {
                    if (aaa.contentEquals(dlx.IP)) f = false;
                }
                if (f) tempA.add(dlx.IP);
            }
            
            for (String sxx : tempA) {
                System.err.println("New IP detected: "+sxx);
            }
        }
        
        DE_IPs.clear();
        for (DHCPLease dlx : GetLeases()) {
            DE_IPs.add(dlx.IP);
        }
        
        
        //DE_IPs
        
//        NetDevRAWData = new String(net_dev, 0, net_dev.length);
//        final String[] NetDevArray1 = NetDevRAWData.replace("\r", "").split("\n");
//        if (NetDevArray1.length <= 2) return false;
//        
//        int SkipCounter = 0;
//        //  face |bytes    packets errs drop fifo frame compressed multicast         |bytes    packets errs drop fifo colls carrier compressed
//        //  wan0: 1352629381978 1407864053    0   53    0     0          0         0 1210670112175 1117802859    0    0    0     0       0          0
//        //    0        1          2          3    4     5     6          7         8      9           10 
//        for (String lineX : NetDevArray1) {
//            if (SkipCounter > 1) {
//                String NetDevArray2[] = lineX.trim().replaceAll("\\s{2,}", " ").split(" ");
//                long ReceiveBytes = 0, TransmitBytes = 0;
//                try {
//                    ReceiveBytes = Long.parseLong(NetDevArray2[1], 10);
//                    TransmitBytes = Long.parseLong(NetDevArray2[9], 10);
//                    //System.err.println("ReceiveBytes="+ReceiveBytes+"; NetDevArray2[1]="+NetDevArray2[1]);
//                } catch (Exception e) { }
//                
//                if ((ReceiveBytes > 0) && (TransmitBytes > 0)) {
//                    String ifname = NetDevArray2[0].replace(":", "").trim();
//                    
//                    if (IfacesData.containsKey(ifname)) {
//                        IfaceData ID = IfacesData.get(ifname);
//                        ID.PushRX(ReceiveBytes);
//                        ID.PushTX(TransmitBytes); 
//                        IfacesData.put(ifname, ID);
//                    } else {
//                        IfaceData ID = new IfaceData();
//                        ID.IFName = ifname;
//                        ID.PushRX(ReceiveBytes);
//            SkipCounter++;
//        }
        
//        final String MemoryInfo = new String(meminfo, 0, meminfo.length);
//        final String[] MemoryInfoArray = MemoryInfo.replace("\r", "").split("\n");
//        if (MemoryInfoArray.length < 1) return false;
//        
//        for (String lineX : MemoryInfoArray) {
//            String[] MArray = lineX.split("\\:");
//            if (MArray.length == 2) {
//                MemoryData.put(MArray[0], MArray[1]);
//            }
//        }
        
        final long ReturnTime = new Date().getTime();
        ReloadCurrentInfoExecTime = ReturnTime - StartTime;

        return true;
    }

    public JNekoServerInfo() { }
}
