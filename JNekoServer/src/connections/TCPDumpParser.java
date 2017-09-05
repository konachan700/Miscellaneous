package connections;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import jnekoserver.JNekoMySQL;

public class TCPDumpParser {
    private final String 
            SQL_PRE = "INSERT INTO DNS_LOGGER VALUES ";
    private final ArrayList<String> 
            SQL_DATA = new ArrayList<>();
    
    private final static int 
            DATA_COUNT_IN_BUFFER = 150;
    public  final static String 
            FILTER_DNS53A = "/usr/sbin/tcpdump -l -tt -i lan1 -n -nn src net 172.27.0.0/16 and dst port 53";
    
    private final String CAPT_FILTER;
    
    @SuppressWarnings({"SleepWhileInLoop", "Convert2Lambda"})
    private final Thread TCPDumpParser = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                final Process tcpdumpProc = Runtime.getRuntime().exec(CAPT_FILTER);
                final InputStream is = tcpdumpProc.getInputStream();
                while (true) {
                    PasreLine(is);
                }
            } catch (IOException ex) { }
        }
    });
    
    private final void _L(String s) {
        System.out.println(s);
    }
    
    public TCPDumpParser(String filterString) {
        CAPT_FILTER = filterString;
    }
    
    public void StartThread() {
        TCPDumpParser.start();
    }
    
    // 1420905348.533788 IP 172.27.1.20.59748 > 8.26.56.26.53: 1921+ A? accounts.google.com. (37)
    // --1-------------- 2  ---------3------- 4 ----5--------- -6--- 7 --------8------------ --9-
    private final void PasreLine(InputStream is) {
        final String line = ReadLine(is);
        if (line != null) {
            final String lineSArray[] = line.trim().replaceAll("\\s{2,}", " ").split(" ");
            if (lineSArray.length == 9) {
                if ( (lineSArray[6].equals("A?")) && (lineSArray[1].equals("IP")) && (lineSArray[3].equals(">")) ) {
                    final StringBuilder SQLR = new StringBuilder();
                    SQLR.append("(");
                    SQLR.append(String.valueOf(new Date().getTime())); 
                    SQLR.append(", ");
                    SQLR.append(String.valueOf(IPToLong(lineSArray[2])));
                    SQLR.append(", ");
                    SQLR.append(String.valueOf(IPToLong(lineSArray[4])));
                    SQLR.append(", \"");
                    
                    final String domain = lineSArray[7].replaceAll("[^A-Za-z0-9\\._-]{1,}", "");
                    final String domainWODot = domain.substring(0, (domain.length() >= 67) ? 67  : domain.length()-1);
                    
                    SQLR.append(domainWODot);
                    SQLR.append("\"");
                    
                    final int sds = SQL_DATA.size();
                    if (sds >= 0) {
                        if (sds < DATA_COUNT_IN_BUFFER) {
                            SQLR.append("), ");
                        } else {
                            SQLR.append(");");
                        }
                    }
                    
                    final String sbuf = SQLR.substring(0);
                    SQL_DATA.add(sbuf);

                    final int sde = SQL_DATA.size();
                    if (sde > DATA_COUNT_IN_BUFFER) {
                        final StringBuilder SQLR2 = new StringBuilder();
                        SQLR2.append(SQL_PRE);
                        for (String Q : SQL_DATA) {
                            SQLR2.append(Q);
                        }
                        
                        try {
                            final PreparedStatement ps = JNekoMySQL.GetConnection().prepareStatement(SQLR2.substring(0));
                            ps.execute();
                        } catch (SQLException ex) {
                            _L("PasreLine SQL Error: " + ex.getMessage() + "; SQLST: " + ex.getSQLState() + "\n\t\t" + SQLR2.substring(0));
                        }
                        
                        SQL_DATA.clear();
                    }
                }
            }
        }
    }
    
    private final long IPToLong(String ip) {
        String ipa[] = ip.trim().split("\\.");
        if (ipa.length < 4) return -1;
        
        long ippart = 0, ipres = 0;
        for (int i=0; i<4; i++) {
            ippart = Long.parseLong(ipa[i], 10);
            ipres += ippart * Math.pow(256, 3 - i);
        }
        
        return ipres;
    }

    private final String ReadLine(InputStream is) {
        int b;
        final ByteArrayOutputStream in = new ByteArrayOutputStream();
        
        while (true) {
            try {
                b = is.read();
                if (b == '\n') {
                    final String s = in.toString("UTF-8");
                    in.close();
                    return s;
                }
                if (b >= 0) in.write(b); 
            } catch (IOException ex) {
                return null;
            }
        }
    }
}
