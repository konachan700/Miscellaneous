package osagent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Processes {
    public static final String
            LOG_FILE = "./logs/Processes.log"
    ;
    
    private static void _L(String s) {
        if (FileIO.AppendToTextFile("INFO: " + LOG_FILE, s + "\n") > 0) System.out.println(s);
    }
    
    public static void RunCommand(String command) {
        RunCommand(command, true);
    }
    
    public static String RunCommandX(String command) {
        return RunCommand(command, false);
    }
    
    private static String RunCommand(String c, boolean toFile) {
        if (FileIO.AppendToTextFile(LOG_FILE, "run# [" + c + "]\n") > 0) System.out.println(c);
        try {
            final Process p = Runtime.getRuntime().exec(c);
            final InputStream 
                    is = p.getInputStream(),
                    es = p.getErrorStream();
            p.waitFor();
            
            final ByteArrayOutputStream 
                    stdin  = new ByteArrayOutputStream(),
                    stderr = new ByteArrayOutputStream();
            
            int b=0;
            while (true) {
                b = is.read();
                if (b != -1) stdin.write(b); else break;
            }
            
            while (true) {
                b = es.read();
                if (b != -1) stderr.write(b); else break;
            }
            
            is.close();
            es.close();
            
            final String retval;
            if (toFile) {
                retval = null;
                if (stdin.size() > 0) 
                    if (FileIO.AppendToTextFile(LOG_FILE, "\tstdout: [" + stdin.toString() + "]\n") > 0) System.out.println(stdin.toString());
                if (stderr.size() > 0) 
                    if (FileIO.AppendToTextFile(LOG_FILE, "\tstderr: [" + stderr.toString() + "]\n") > 0) System.out.println(stderr.toString());
            } else {
                retval = stdin.toString() + "\n" + stderr.toString();
            }
            
            stdin.close();
            stderr.close();
            
            return retval;
        } catch (IOException | InterruptedException ex) { _L(ex.getMessage()); } 
        return null;
    }
}
