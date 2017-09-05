
package osagent;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.text.SimpleDateFormat;
import java.util.Date;
import jnekoserver.JNekoMySQL;
import jnekoserver.JNekoServer;

public class Backups {
    public static String APP_PATH = "";
    
    private static void _L(String s) {
        System.out.println(s);
    }
    
    public static void Init() {
        APP_PATH = FileIO.GetAppPath();
    }
    
    public static void DNSLogToFile() {
        File dp = new File(APP_PATH + "/logs/");
        dp.mkdirs();
        try {
            final Process tcpdumpProc = Runtime.getRuntime().exec(
                    "/usr/bin/mysqldump --user=" + JNekoMySQL.DBUser + " --password=" + JNekoMySQL.DBPass +
                            " --result-file=" + APP_PATH + "/logs/dns." + (new SimpleDateFormat("HH-mm_dd-MM-yyyy").format(new Date())) + ".log"
                            + " " + JNekoMySQL.DBName + " DNS_LOGGER");
            tcpdumpProc.waitFor();
            JNekoMySQL.ClearTable("DNS_LOGGER");
        } catch (IOException | InterruptedException ex) { _L("ERROR: " + ex.getMessage()); }
    }
    
    public static void AllTablesToFile() {
        File dx = new File(APP_PATH + "/backups/");
        dx.mkdirs();
        try {
            final Process tcpdumpProc = Runtime.getRuntime().exec(
                    "/usr/bin/mysqldump --user=" + JNekoMySQL.DBUser + " --password=" + JNekoMySQL.DBPass +
                    " --result-file=" + APP_PATH + "/backups/db." + (new SimpleDateFormat("HH-mm_dd-MM-yyyy").format(new Date())) + ".sql " + JNekoMySQL.DBName);
            tcpdumpProc.waitFor();
        } catch (IOException | InterruptedException ex) { _L("ERROR: " + ex.getMessage()); }
    } 
}
