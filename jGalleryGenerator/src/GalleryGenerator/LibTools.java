package GalleryGenerator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import javax.swing.JTextArea;

public class LibTools {
    
    ArrayList<String> LogList = new ArrayList<String>();
    JTextArea callback_log = null;
    
    public LibTools() {}
    
    public void logSetCallback(JTextArea a) {
        callback_log = a;
    }
    
    public void logAdd(String log) {
        Calendar c = GregorianCalendar.getInstance();
        c.setTime(new Date());
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
                
        String l = "[" + formatter.format(c.getTime()) + "] " + log;
        LogList.add(l);
        
        if (LogList.size() > 8) {
            LogList.remove(0);
        }
        
        if (callback_log != null) {
            callback_log.setText("");
            for (int i=(LogList.size() - 1); i>=0; i--) {
                callback_log.setText(callback_log.getText() + "\r\n" + LogList.get(i));
            }
        }
    }
    
    
    public String textGetInMarker(String text, String beginMarker, String endMarker) {
        int a = text.indexOf(beginMarker);
        if (a < 1) {
            return null;
        }
        
        int b = text.indexOf(endMarker, a + beginMarker.length());
        if (b < 1) {
            return null;
        }
        
        return text.substring(a + beginMarker.length(), b);
    }
    
    public String textRusToTranslit(String text) {
        String[] _from = {"а","б","в","г","д","е","ё", "ж","з","и","й","ъ","к","л","м","н","о","п","р","с","т","у","ф","х","ц", "ч", "ш", "щ", "ь","ы","ю", "я" ,"э"};
        String[] _to   = {"a","b","v","g","d","e","yo","g","z","i","y","" ,"k","l","m","n","o","p","r","s","t","u","f","h","tz","ch","sh","sh","" ,"i","yu","ya","e"};
        for (int i=0; i<_from.length; i++) {
            text = text.replace(_from[i], _to[i]);
            text = text.replace(_from[i].toUpperCase(), _to[i].toUpperCase());
        }
        return text;
    }
    
    public String textDelLastSlash(String s) {
        String sl = "";
        if ((s.trim().endsWith("\\")) || (s.trim().endsWith("/"))) {
            sl = s.substring(0, s.length()-1);
        }
        return sl;
    }
    
    public String textMD5(String str) {
        MessageDigest md5 ;        
        StringBuffer hexString = new StringBuffer();
        try {                    
            md5 = MessageDigest.getInstance("md5");
            md5.reset();
            md5.update(str.getBytes());    
            byte messageDigest[] = md5.digest();
            for (int i = 0; i < messageDigest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }                                                                         
        } 
        catch (NoSuchAlgorithmException e) {                        
            return e.toString();
        }
        return hexString.toString();
    }
    
    public String textSlash(String path) {
        if (path.length() < 1) { 
            return path; 
        }
        if ((path.endsWith("/") == false) && ((path.startsWith("/")) || (path.startsWith("./")))) {
            path += "/";
        } else if (((path.startsWith("/") == false) && (path.startsWith("./") == false)) && (path.endsWith("\\") == false)) {
            path += "\\";
        }
        return path;
    }
    
    public void fileWriteNew(String filename, String content) {
        File f = new File(filename);
        f.delete();
        try {
            RandomAccessFile raf = new RandomAccessFile(filename, "rw");
            raf.write(content.getBytes());
            raf.close();
            
        } catch (FileNotFoundException ex) { 
            System.out.println("Error [write_file]: File Not Found Exception [" + filename + "]"); 
        } catch (IOException ex) { 
            System.out.println("Error [write_file]: IO Exception [" + filename + "]"); 
        }
    }
    
    public void fileWriteToEOF(String filename, String content) {
        try {
            RandomAccessFile raf = new RandomAccessFile(filename, "rw");
            if (raf.length() > 0) { raf.seek(raf.length()); }
            raf.write(content.getBytes());
            raf.close();
            
        } catch (FileNotFoundException ex) { 
            System.out.println("Error [write_file]: File Not Found Exception [" + filename + "]"); 
        } catch (IOException ex) { 
            System.out.println("Error [write_file]: IO Exception [" + filename + "]"); 
        }
    }
    
    public String fileReadAll(String filename) {
        try {
            RandomAccessFile raf = new RandomAccessFile(filename, "r");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int ch = 0;
            while (ch >= 0) {
                ch = raf.read();
                if (ch >= 0) { 
                    baos.write(ch); 
                } else {
                    break;
                }
            };
            raf.close();
            return baos.toString();
        } catch (FileNotFoundException ex) { 
            System.out.println("Error: File Not Found Exception [" + filename + "]"); 
        } catch (IOException ex) { 
            System.out.println("Error: IO Exception [" + filename + "]"); 
        }
        return "";
    }
}
