package osagent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import jnekoserver.JNekoServer;

public class FileIO {
    private static void _L(String s) {
        System.out.println(s);
    }
    
    private static int WriteToTextFile(String path, String data, boolean append) {
        try {
            final FileOutputStream fos = new FileOutputStream(path, append);
            fos.write(data.getBytes(), 0, data.getBytes().length);
            fos.close();
            return 0;
        } catch (IOException ex) {
            _L("WriteToTextFile(): "+ex.getMessage());
            return 1;
        } 
    }
    
    public  static int WriteToTextFile(String path, String data) {
        return WriteToTextFile(path, data, false);
    }
    
    public static int AppendToTextFile(String path, String data) {
        return WriteToTextFile(path, data, true);
    }
    
    public static String ReadTextFile(String path) {
        try {
            final FileInputStream fis = new FileInputStream(path);
            int b;
            final ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
            
            while(true) {
                b = fis.read();
                if (b == -1) break; else baos1.write(b); 
            }
            
            final String retval = baos1.toString();
                    
            baos1.close();
            fis.close();
            
            return retval;
        } catch (IOException ex) {
            _L(ex.getMessage());
        }
        return null;
    }
    
    public static String GetAppPath() {
        String meDir = "";
        try {
            CodeSource codeSource = JNekoServer.class.getProtectionDomain().getCodeSource();
            File mePath = new File(codeSource.getLocation().toURI().getPath());
            meDir = mePath.getParentFile().getPath();
        } catch (URISyntaxException ex) { _L("ERROR: " + ex.getMessage()); return null; }

        if (meDir.endsWith("/")) meDir = meDir.substring(0, meDir.length()-1);
        return meDir;
    } 
    
}
