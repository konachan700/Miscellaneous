
package jneko;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;

class TelnetExtraException extends Exception {
    public TelnetExtraException() {}
    public TelnetExtraException(String msg) {
      super(msg);
    }
}

public class Z_TelnetEngine {
    private static final    StringBuilder    sb             = new StringBuilder();
    private static          TelnetClient     tc;
    private static          InputStream      is;
    private static          OutputStream     os;
    private static          PrintWriter      s_out;
    private static          boolean          loginState  = false, 
                                             telnetDebug = true, 
                                             telnetLock  = false;
    private static          String           lastCommand = "";
    
    public static String GetLastCommand() {
        return lastCommand;
    }
    
    public static int Connect(String ip, String port, String user, String password) {
        TerminalTypeOptionHandler   ttopt       = new TerminalTypeOptionHandler("VT100", false, false, true, false);
        EchoOptionHandler           echoopt     = new EchoOptionHandler(true, false, true, false);
        SuppressGAOptionHandler     gaopt       = new SuppressGAOptionHandler(true, true, true, true);
        
        tc = new TelnetClient();
        
        try {
            if (tc.isConnected() == false) {
                tc.connect(ip, Integer.parseInt(port));
                tc.addOptionHandler(ttopt);
                tc.addOptionHandler(echoopt);
                tc.addOptionHandler(gaopt);
                tc.setSoTimeout(5);
                loginState = false;
            }
        } catch (InvalidTelnetOptionException | IOException ex) {
            __toLog(ex.getMessage());
            __toLog("Error: Cannot connect to MA4000");
            try { tc.disconnect(); } catch (IOException ex1) { 
                __toLog(ex1.getMessage());
                __toLog("Error: Telnet connection already closed."); 
                loginState = false;
            }
            return 1;
        }
        
        is    = tc.getInputStream();
        os    = tc.getOutputStream();
        s_out = new PrintWriter(os, true);
        
        if (loginState == false) {
            if (__login(is, s_out, user, password)) {
                loginState = true;
                //telnetTimer.start();
            } else {
                __toLog("Error: Login/password error.");
                return 2;
            }
        }

        return 0;
    }
    
    public static void EnableDebugOutput(boolean d) {
        telnetDebug = d;
    }
    
    public static boolean IsConnected() {
        if (tc == null) return false;
        return tc.isConnected() & loginState;
    }
    
    public static int Disconnect() {
        if (tc.isConnected()) {
            try {
                tc.disconnect();
                loginState = false;
                //telnetTimer.stop();
            } catch (IOException ex) {
                __toLog(ex.getMessage());
                return 1;
            }
        }
        return 0;
    }
    
    public static int CommandExecNoReadNE(String comm, String waitFor) { // ЗАГЛУШКА ДЛЯ ТОГО, ЧТОБЫ НЕ РЕФАКТОРИТЬ ВСЕ. ПОТОМ УБРАТЬ.
        try {
            CommandExecNoRead(comm, waitFor);
        } catch (TelnetExtraException ex) {
            Logger.getLogger(Z_TelnetEngine.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        }
        return 0;
    }
    
    @SuppressWarnings("SleepWhileInLoop")
    public static void CommandExecNoRead(String comm, String waitFor) throws TelnetExtraException {
        lastCommand = comm;
        while (telnetLock == true) { 
            try { Thread.sleep(50); } catch (InterruptedException ex) { throw new TelnetExtraException("CommandExecNoRead InterruptedException"); }
        }
        
        if ((is == null) || (os == null) || (s_out == null)) throw new TelnetExtraException("NPE on streams");
        if (loginState == false) throw new TelnetExtraException("Not logged on");
        if (tc.isConnected() == false) throw new TelnetExtraException("Not connected");
        
        if (sb != null) sb.delete(0, sb.length());
        try { is.reset(); } catch (IOException ex) { }
        
        __writeTo(s_out, comm);
        if (__waitFor(is, waitFor) == 1) throw new TelnetExtraException("");
    }
    
    @SuppressWarnings("SleepWhileInLoop")
    public static String CommandExecWithRead(String comm, String waitFor) {
        lastCommand = comm;
        while (telnetLock == true) { 
            try { Thread.sleep(50); } catch (InterruptedException ex) { __toLog(ex.getMessage()); }
        }
        
        //timerVal_1 = new Date().getTime();
        
        if ((is == null) || (os == null) || (s_out == null)) return null;
        if (loginState == false) return null;
        if (tc.isConnected() == false) return null;
        
        if (sb != null) sb.delete(0, sb.length());
        
        __writeTo(s_out, comm);
        if (__waitFor(is, waitFor) == 1) return null;
        
        return sb.substring(0); 
    }

    private static int __waitFor(InputStream is, String s) {
        final long sDate = new Date().getTime();
        String sx = "";
        
        int retval = 0;
        while (true) {
            sx += __readFrom(is).toLowerCase();
            if (sx.contains("next page;")) {
                __writeToWONL(s_out, "  ");
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
    
    private static boolean __login(InputStream is, PrintWriter s_out, String user, String pass) {
        if (__waitFor(is, "login") == 1) return false;
        __writeTo(s_out, user);
        
        if (__waitFor(is, "passw") == 1) return false;
        __writeTo(s_out, pass);
        
        return __waitFor(is, ">") != 1;
    }
    
    private static void __writeTo(PrintWriter s_out, String s) {
        //if (telnetDebug) System.err.print(s); 
        s_out.println(s);
        s_out.flush();
    }
    
    private static void __writeToWONL(PrintWriter s_out, String s) {
        s_out.print(s);
        s_out.flush();
    }
    
    private static String __readFrom(InputStream is) {
        ByteArrayOutputStream InputBuffer = new ByteArrayOutputStream();
        int b = 0;
        while (b >= 0) {
            try { 
                b = is.read(); 
                if (b >= 0) InputBuffer.write(b);
            } catch (java.net.SocketTimeoutException ex) { 
                //__toLog(ex.getMessage());
                break;
            } catch (IOException ex) {
                //__toLog(ex.getMessage());
                break;
            }
        }
        
        final String out = (InputBuffer.size() <= 0) ? "" : InputBuffer.toString();
        if (out.length() > 0) {
            __toLog(out);
            sb.append(out);
        } 
        return out;
    }
    
     private static void __toLog(String out) {
         if (telnetDebug) System.out.print(out);
     }
}
