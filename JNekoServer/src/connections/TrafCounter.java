package connections;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import jnekoserver.JNekoMySQL;

public class TrafCounter {
    private static final Map<String, ArrayDeque<Long>> Counters = new LinkedHashMap<>();
    private static final ArrayList<String> IfList = new ArrayList<>();
    private static final int 
            ARRAY_SIZE = 4096,
            PROC_NET_DEV_HEADER_LINES_COUNT = 2;
    private static int
            SQLCounter = 0;
        
    @SuppressWarnings("Convert2Lambda")
    private static final ActionListener TrafTimerListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            try {
                final FileInputStream fis = new FileInputStream("/proc/net/dev");
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                
                int b;
                while(true) {
                    b = fis.read();
                    if (b == -1) break;
                    baos.write(b); 
                }
                
                int lineCounter = 0;
                final String fileLines[] = baos.toString("UTF-8").replace("\r", "").split("\n");
                if (fileLines.length > PROC_NET_DEV_HEADER_LINES_COUNT) {
                    for (String line : fileLines) {
                        lineCounter++;
                        if (lineCounter > PROC_NET_DEV_HEADER_LINES_COUNT) {
                            final String[] content = line.trim().replaceAll("\\s{2,}", " ").split(" ");
                            if ((content[0].contains("lo") == false) && (content[0].contains("ifb") == false)) {
                                if ((Counters.containsKey(content[0] + ":TX")) && (Counters.containsKey(content[0] + ":RX"))) {
                                    Counters.get(content[0] + ":TX").addFirst(new Long(content[9]));
                                    if (Counters.get(content[0] + ":TX").size() > ARRAY_SIZE) Counters.get(content[0] + ":TX").removeLast();
                                    Counters.get(content[0] + ":RX").addFirst(new Long(content[1]));
                                    if (Counters.get(content[0] + ":RX").size() > ARRAY_SIZE) Counters.get(content[0] + ":RX").removeLast();
                                } else {
                                    final ArrayDeque<Long> RXBytes = new ArrayDeque<>();
                                    final ArrayDeque<Long> TXBytes = new ArrayDeque<>();
                                    RXBytes.add(new Long(content[1]));
                                    TXBytes.add(new Long(content[9]));
                                    Counters.put(content[0] + ":TX", TXBytes);
                                    Counters.put(content[0] + ":RX", RXBytes);
                                    IfList.add(content[0]);
                                }
                            }
                        }
                    }
                }
                baos.close();
                fis.close();
                
                SQLCounter++;
                if (SQLCounter > 60) {
                    StringBuilder sb;
                    for (String key : IfList) {
                        if ((Counters.get(key+":TX").size() > 60) && (Counters.get(key+":RX").size() > 60)) {
                            sb = new StringBuilder();
                            sb.append("INSERT INTO TrafStatM VALUES");
                            
                            Long[] TXA = new Long[Counters.get(key+":TX").size()];
                            TXA = Counters.get(key+":TX").toArray(TXA);
                            
                            Long[] RXA = new Long[Counters.get(key+":RX").size()];
                            RXA = Counters.get(key+":RX").toArray(RXA);

                            final long 
                                    CurrTX = (TXA[0].longValue() - TXA[59].longValue()),
                                    CurrRX = (RXA[0].longValue() - RXA[59].longValue());

                            sb.append("(").append(String.valueOf(new Date().getTime())).append(", \"").append(key).append("\", ");
                            sb.append(String.valueOf(CurrTX)).append(", ").append(String.valueOf(CurrRX)).append(");");

                            try {
                                PreparedStatement ps = JNekoMySQL.GetConnection().prepareStatement(sb.substring(0));
                                ps.execute();
                            } catch (SQLException ex) {
                                Logger.getLogger(TrafCounter.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    SQLCounter = 0;
                }
                
            } catch (IOException e) { }
        }
    };
    
    private static final Timer StateTimer = new Timer(1000, TrafTimerListener);

    public static Map<String, ArrayDeque<Long>> GetAllData() {
        return Counters;
    }
            
    public static Map<String, Long> GetLastRXTX() {
        final Map<String, Long> CurrentCounters = new LinkedHashMap<>();
        Set<String> s = Counters.keySet();
        for (String key : s) {
            CurrentCounters.put(key, Counters.get(key).getFirst());
        }
        return CurrentCounters;
    }
    
    public static ArrayList<String> GetIfList() {
        return IfList;
    }
    
    public static void Start() {
        if (StateTimer.isRunning()) return;
        StateTimer.start();
    }
}
