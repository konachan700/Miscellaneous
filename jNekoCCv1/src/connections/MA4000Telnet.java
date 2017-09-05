package connections;

import datasource.JNekoSQLite;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import jnekotabs.MA4000TelnetLogs;
import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;

public class MA4000Telnet {
    private static TelnetClient telnetClient_MA4000 = null;
    public  static InputStream  telnetIS_MA4000 = null;
    public  static OutputStream telnetOS_MA4000 = null;
    public  static PrintWriter  telnetPW_MA4000 = null;
    public  static final StringBuilder buffer_MA4000 = new StringBuilder();
    private static JLabel Callback_MA4000 = null;
    
    private static volatile boolean  Locked_MA4000 = false, 
                                     ConnectFail   = false,
                                     IdleLock      = false;
    private static volatile long     LastCurrentTime = new Date().getTime(), 
                                     TIME_COUNT = (1000 * 60 * 2);
    
    private static String MA4000_IP = "", MA4000_PORT = "", MA4000_USER = "", MA4000_PASSWORD = "";
    private static Color ConnectedColor = new Color(0, 177, 0), OfflineColor = new Color(177, 0, 0);
    
    private static MA4000TelnetLogs LogTB = null;
    
    private static final Thread telnetThread_MA4000 = new Thread(new Runnable() {
        @Override
        @SuppressWarnings("SleepWhileInLoop")
        public void run() {
            Long CurrentTime;
            while(true) {
                if ((telnetClient_MA4000.isConnected()==true) && (ConnectFail==false)) {
                    IdleLock = true;
                    CurrentTime = new Date().getTime();
                    if ((LastCurrentTime < CurrentTime) && (LastCurrentTime > 0)) {
                        WriteTo("express");
                        if (WaitFor(">") == 0) {
                            WriteTo("top");
                            if (WaitFor(">") != 0) ConnectFail = true;
                        } else {
                            ConnectFail = true;
                        }
                        
                        LastCurrentTime = new Date().getTime() + TIME_COUNT;
                    }
                    IdleLock = false;
                    if (Callback_MA4000 != null) Callback_MA4000.setForeground(ConnectedColor); 
                } else {
                    if (Callback_MA4000 != null) Callback_MA4000.setForeground(OfflineColor);
                }
                
                try { Thread.sleep(500); } catch (InterruptedException ex) { }
            }
        }
    });
    
    public static boolean IsLocked() {
        return IdleLock;
    }
    
    public static void SetProtocolLogger(MA4000TelnetLogs __LogTA) {
        LogTB = __LogTA;
    }
    
    public static boolean IsLockedConnected() {
        return Locked_MA4000;
    }
    
    public static boolean IsConnected() {
        return (telnetClient_MA4000.isConnected()==true) && (ConnectFail==false);
    }
    
    public static void OpenConnection() {
        Locked_MA4000 = true;
        
        MA4000_IP = JNekoSQLite.ReadAPPSettingsString("MA4000_HOST");
        MA4000_PORT = JNekoSQLite.ReadAPPSettingsString("MA4000_PORT");
        MA4000_USER = JNekoSQLite.ReadAPPSettingsString("MA4000_USERNAME");
        MA4000_PASSWORD = JNekoSQLite.ReadAPPSettingsString("MA4000_PASSWORD");
        
        telnetClient_MA4000 = new TelnetClient();
        TerminalTypeOptionHandler   ttopt       = new TerminalTypeOptionHandler("VT100", false, false, true, false);
        EchoOptionHandler           echoopt     = new EchoOptionHandler(true, false, true, false);
        SuppressGAOptionHandler     gaopt       = new SuppressGAOptionHandler(true, true, true, true);
        
        try {
            if (telnetClient_MA4000.isConnected()) telnetClient_MA4000.disconnect();
            
            telnetClient_MA4000.connect(MA4000_IP, Integer.parseInt(MA4000_PORT));
            telnetClient_MA4000.addOptionHandler(ttopt);
            telnetClient_MA4000.addOptionHandler(echoopt);
            telnetClient_MA4000.addOptionHandler(gaopt);
            telnetClient_MA4000.setSoTimeout(5);
        
            telnetIS_MA4000 = telnetClient_MA4000.getInputStream();
            telnetOS_MA4000 = telnetClient_MA4000.getOutputStream();
            telnetPW_MA4000 = new PrintWriter(telnetOS_MA4000, true);
        
            ConnectFail = !Login();
            if(!ConnectFail) telnetThread_MA4000.start();
        } catch (InvalidTelnetOptionException | IOException ex) {
            ConnectFail = true;
        }
        Locked_MA4000 = false;
    }
    
    public static void SetCallbackLabel(JLabel jl) {
        Callback_MA4000 = jl;
    }
    
    public static synchronized int WaitFor(String s) {
        LastCurrentTime = new Date().getTime() + TIME_COUNT;
        final long sDate = new Date().getTime();
        String sx = "";
        
        int retval = 0;
        while (true) {
            sx += ReadFrom().toLowerCase();
            if (sx.contains("next page;")) {
                WriteToWONL("  ");
                s = s.replace("next page;", "");
            } // костыль для ликвидации постраничного вывода
            
            if (sx.contains("^")) retval = 1; // костыль для обработки ошибок МА4000 - оно отдает строку назад с этим символом, если что-то пошло не так.
            if (sx.contains(s)) {
                if (sx.contains("^")) retval = 1;
                return retval;
            }
            
            long eDate = new Date().getTime();
            long aDate = sDate + (1000 * 20);
            if (eDate > aDate) {
                return 1;
            }
        }
    }
    
    public static synchronized void WriteToWONL(String s) {
        LastCurrentTime = new Date().getTime() + TIME_COUNT;
        telnetPW_MA4000.print(s);
        telnetPW_MA4000.flush();
    }
    
    private static boolean Login() {
        LastCurrentTime = new Date().getTime() + TIME_COUNT;
        if (WaitFor("login") == 1) return false;
        WriteTo(MA4000_USER);
        
        if (WaitFor("passw") == 1) return false;
        WriteTo(MA4000_PASSWORD);
        
        return WaitFor(">") != 1;
    }
    
    public static synchronized void  WriteTo(String s) {
        LastCurrentTime = new Date().getTime() + TIME_COUNT;
        telnetPW_MA4000.println(s);
        telnetPW_MA4000.flush();
    }
    
    public static synchronized String ReadFrom() {
        LastCurrentTime = new Date().getTime() + TIME_COUNT;
        ByteArrayOutputStream InputBuffer = new ByteArrayOutputStream();
        int b = 0;
        while (b >= 0) {
            try { 
                b = telnetIS_MA4000.read(); 
                if (b >= 0) InputBuffer.write(b);
            } catch (java.net.SocketTimeoutException ex) { 
                break;
            } catch (IOException ex) {
                break;
            }
        }
        
        final String out = (InputBuffer.size() <= 0) ? "" : InputBuffer.toString();
        if (out.length() > 0) {
            if (LogTB != null) LogTB.L(out); else System.out.print(out);
            buffer_MA4000.append(out);
        } 
        return out;
    }
}
