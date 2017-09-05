package jnekoserver;

import connections.TCPDumpParser;
import connections.TrafCounter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.CodeSource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import osagent.Backups;
import osagent.Iptables;

public class JNekoServer {
    private static TCPDumpParser jNTDP;
    
    private static ServerSocket 
            JNekoSPSocket   = null;
    private static String       
            IP              = "0.0.0.0",      
            PORT            = "4300",
            DNS_LOG_PATH    = "/server/jnekosrv/logs/";
    
    private static void _L(String s) {
        System.out.println(s);
    }
    
    private static int GetSettings() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("server.conf");
            Properties p = new Properties();
            p.load(fis);
            
            IP = p.getProperty("listen_ip", "0.0.0.0");
            PORT = p.getProperty("listen_port", "4300");
            
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
    
    @SuppressWarnings({"SleepWhileInLoop", "Convert2Lambda"})
    private static final Thread Listener = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    GetSettings();
                    JNekoSPSocket = new ServerSocket(Integer.parseInt(PORT, 10), 0, InetAddress.getByName(IP));
                    while (true) {
                        Socket xs = JNekoSPSocket.accept();
                        Thread t = new Thread(new JNekoRunnable(xs));
                        t.start();
                    }
                } catch (UnknownHostException ex) { } catch (IOException ex) { System.out.println("ERR "+ex.getMessage()); }
                try { Thread.sleep(500); } catch (InterruptedException ex) { }
            }
        }
    });
    
    @SuppressWarnings("Convert2Lambda")
    private static final ActionListener StateTimerListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            final long CurrentDate = (new Date().getTime()) / (1000 * 60 * 60 * 24),
                    LastDate = JNekoDBWrappers.GetLastMinus();
            
            if (LastDate == 0) JNekoDBWrappers.GetDailyFee();
            if (LastDate > 0) {
                if (CurrentDate > LastDate) {
                    JNekoDBWrappers.GetDailyFee();
                    
                    Backups.DNSLogToFile();
                    Backups.AllTablesToFile();
                                        
                    JNekoNetwork.genServerConfig();
                    JNekoNetwork.RestartServices();
                }
            }
            
        }
    };
    
    private static final Timer StateTimer = new Timer(1000 * 60, StateTimerListener);
    
    @SuppressWarnings("SleepWhileInLoop")
    public static void main(String[] args) {
        JNekoMySQL.Connect();
        Backups.Init();
        Iptables.FullInit();
        
        jNTDP = new TCPDumpParser(TCPDumpParser.FILTER_DNS53A);
        jNTDP.StartThread();
        TrafCounter.Start();

        Listener.start();
        StateTimer.start();
        
        JNekoNetwork.genServerConfig();
        JNekoNetwork.RestartServices();

        System.out.println("Started.\n\n");
    }
}
