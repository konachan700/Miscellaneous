package jnekoserver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class JNekoFS {
    
    
    public static void WriteFileSingleEx(byte[] buffer, JNekoRunnable ref) {
        byte[] filename = new byte[ref.FILENAME_MAXLEN];
        for (int i=0; i<filename.length; i++) filename[i] = ' ';
        System.arraycopy(buffer, 2, filename, 0, ref.FILENAME_MAXLEN);

        final String path = new String(filename).trim(); 
        try {
            final FileOutputStream fos = new FileOutputStream(path);
            fos.write(buffer, ref.FILENAME_MAXLEN+2, buffer.length-(ref.FILENAME_MAXLEN+2)); 
            fos.close();
            ref.CryptWrite("JNEKO_OK");
        } catch (FileNotFoundException ex) {
            ref.CryptWrite("ERR: Cannot write file '"+path+"'.");
        } catch (IOException ex) {
            ref.CryptWrite("ERR: Cannot write file '"+path+"', i/o error detected.");
        }
    }
    
    public static void ReadFileSingleEx(byte[] buffer, JNekoRunnable ref) {
        String FileName = new String(buffer, 2, buffer.length-2);
        File f1 = new File(FileName);
        if (f1.exists() && f1.canRead()) {
            if (f1.length() <= (ref.MAX_FILE_SIZE-ref.MFS_CORR)) {
                FileInputStream fis;
                try {
                    fis = new FileInputStream(FileName);
                    int b;
                    ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                    baos1.write(42);
                    baos1.write(10);
                    while(true) {
                        b = fis.read();
                        if (b == -1) break;
                        baos1.write(b); 
                    }
                    ref.CryptWrite(baos1);
                    fis.close();
                } catch (FileNotFoundException ex) {
                    ref.CryptWrite("ERR:[FileNotFoundException]: File '"+FileName+"' not found, ex.getMessage()="+ex.getMessage());
                } catch (IOException ex) {
                    ref.CryptWrite("ERR:[IOException]: File '"+FileName+"' IO error, ex.getMessage()="+ex.getMessage());
                }
            } else {
                ref.CryptWrite("ERR: File '"+FileName+"' is too big for single packet reading - use another command (41).");
            }
        } else {
            ref.CryptWrite("ERR: File '"+FileName+"' not found.");
        }
    }
}
