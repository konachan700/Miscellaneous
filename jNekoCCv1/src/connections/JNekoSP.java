package connections;

import datasource.JNekoSQLite;
import datasource.JNekoServerInfo;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import xlistcustomelements.BillingUserMainElement;
import xlist.GlobalMainMenuElement;
import xlist.XListButtonsActionListener;

public class JNekoSP {
    private static  JNekoSP                 jNekoSPSt       = null;
    private         Socket                  JNekoSPSocket   = null;
    private         String       
                                            IP              = null, 
                                            PORT            = null, 
                                            KEY1            = null;
    private         InputStream             IS              = null;
    private         OutputStream            OS              = null;
    private         ByteArrayOutputStream   ReadBuffer      = null;

    private boolean DBG1 = true;
    
    public static void ConnectS() {
        jNekoSPSt = new JNekoSP();
    }
    
    public static void DisconnectS() {
        if (jNekoSPSt != null) jNekoSPSt.CloseConnection();
    }
    
    public static JNekoSP GetSP() {
        return jNekoSPSt;
    }
    
    private void _L(String s) {
        System.out.println(s);
    }

    public synchronized void OnError() {
        if (JNekoSPSocket == null) return;
        try {
            JNekoSPSocket.shutdownInput();
            JNekoSPSocket.shutdownOutput();
            JNekoSPSocket.close();
        } catch (IOException ex) { if (DBG1) _L("Connect(); connection invalid or close; ex.getMessage()="+ex.getMessage()); }
        JNekoSPSocket = null;
        IS = null;
        OS = null;
    }
    
    public synchronized void CloseConnection() {
        OnError();
    }
    
    public JNekoSP() {
        Connect();
    }
    
    public synchronized void Connect() {
        IP = JNekoSQLite.ReadAPPSettingsString("JNSP_HOST");
        PORT = JNekoSQLite.ReadAPPSettingsString("JNSP_PORT");
        if ((IP == null) || (PORT == null)) return;
        
        try {
            int port = Integer.parseInt(PORT, 10);
            if (JNekoSPSocket != null) JNekoSPSocket.close();
            JNekoSPSocket = new Socket(IP, port);
            JNekoSPSocket.setTcpNoDelay(true); 
            KEY1 = JNekoSQLite.ReadAPPSettingsString("JNSP_SKEY");
            IS = JNekoSPSocket.getInputStream();
            OS = JNekoSPSocket.getOutputStream();
        } catch (Exception ex) { 
            if (DBG1) _L("Connect(); cannot connect to remote server; ex.getMessage()="+ex.getMessage()); 
        }
    }
    
    public synchronized ByteArrayOutputStream GetReadBuffer() {
        return ReadBuffer;
    }
    
    public synchronized byte[] GetFile(String path) {
        ByteArrayOutputStream in = new ByteArrayOutputStream();
        in.write(42);
        in.write(10);
        in.write(path.getBytes(), 0, path.length());
        
        int res1 = CryptWrite(in);
        if (res1 != 0) {
            if (DBG1) _L("GetFile(); cannot write data to server;"); 
            return null;
        }
        
        res1 = CryptRead();
        if (res1 != 0) {
            if (DBG1) _L("GetFile(); cannot read data from server;"); 
            return null;
        }
        
        return ReadBuffer.toByteArray();
    }

    private int CryptWriteObject(int Code, Object x) {
        try {
            ByteArrayOutputStream sqlbaos_main = new ByteArrayOutputStream();
            sqlbaos_main.write(Code);

            final ObjectOutputStream sqloos  = new ObjectOutputStream(sqlbaos_main);
            sqloos.writeObject(x);
            sqloos.flush();

            final int rv = CryptWrite(sqlbaos_main);
            
            sqloos.close();
            sqlbaos_main.close();
            return rv;
        } catch(IOException ex) {
            _L("WriteObject()-Error: "+ex.getMessage()+ "; CL: "+ex.getClass().getName());
            return 1;
        }
    }
    
    public synchronized int CryptWrite(String s) {
        ByteArrayOutputStream in = new ByteArrayOutputStream();
        in.write(43); 
        in.write(s.getBytes(), 0, s.length());
        return CryptWrite(in);
    }
    
    private synchronized int CryptWrite(ByteArrayOutputStream in) {
        if (JNekoSPSocket == null) return 4;
        if (JNekoSPSocket.isConnected() == false) return 5;
        if (OS == null) return 6;
        if (JNekoSPSocket.isOutputShutdown()) return 7;
        
        final byte[] crypted = AESEnrypt(in.toByteArray(), KEY1.getBytes());
        final byte[] md5 = MD5(crypted);
        
        final String cryptedSizeA = ("ZZZZZZZZZZ" + crypted.length);
        final byte[] cryptedSizeB = cryptedSizeA.substring(cryptedSizeA.length()-10, cryptedSizeA.length()).getBytes();
        
        if (md5.length != 16) {
            if (DBG1) _L("CryptWrite(); strange MD5 error; md5.length="+md5.length);
            return 1;
        }
        
        if (cryptedSizeB.length != 10) {
            if (DBG1) _L("CryptWrite(); strange 'cryptedSizeB.length' error; cryptedSizeB.length="+cryptedSizeB.length);
            return 2;
        }
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[] sign = new byte[] {'J', 'N', 'E', 'K', 'O', '1'};
        
        out.write(sign, 0, 6); 
        out.write(cryptedSizeB, 0, 10);
        out.write(md5, 0, 16);
        out.write(crypted, 0, crypted.length);
        
        try {
            OS.write(out.toByteArray(), 0, out.toByteArray().length);
            OS.flush();
        } catch (IOException ex) {
            if (DBG1) _L("CryptWrite(); cannot send packet to remote server; ex.getMessage()="+ex.getMessage()); 
            return 3;
        }
        
        return 0;
    }
    
    private synchronized ByteArrayOutputStream RawRead() {
        try { 
            JNekoSPSocket.setSoTimeout(3);
        } catch (SocketException ex) {
            if (DBG1) _L("RawRead() JNekoSPSocket.setSoTimeout() error, ex.getMessage()="+ex.getMessage());
            return null;
        }
        
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        int b;
        long counter = 0, maxcounter = (1024*1024*8), d1 = (new Date().getTime()) + (1000 * 7);

        while (true) {
            try {
                b = IS.read(); 
                if (b >= 0) {
                    ba.write(b); 
                    counter++;
                }
            } catch (IOException ex) { }    
                
            if (counter >= maxcounter) { 
//                if (DBG1) _L("RawRead() returned "+counter+" bytes of raw data.");
                return ba;
            }
            
            long d2 = new Date().getTime();
            if (d1 < d2) {
                if (DBG1) _L("RawRead() timeout error.");
                return null;
            }
            
            if (ba.size() == 6) {
                byte[] header = ba.toByteArray();
                if (new String(header).contentEquals("JNEKO1") == false) { 
                    if (DBG1) _L("RawRead() No JNEKO1 signature found!"); 
                    return null; 
                }
            }
            
            if (ba.size() == 16) {
                byte[] header = ba.toByteArray();  
                byte[] len = new byte[10];
                for (int i=6; i<16; i++) len[i-6] = header[i];
                try {
                    long contentlen = Long.parseLong(new String(len).replace("Z", ""), 10);
//                    if (DBG1) _L("RawRead() contentlen: ["+contentlen+"]"); 
                    
                    if (contentlen > maxcounter) {
                        if (DBG1) _L("RawRead() (contentlen > maxcounter) incorrect size value."); 
                        return null;
                    } else maxcounter = contentlen+32;
                } catch (Exception ex) { 
                    if (DBG1) _L("RawRead() ex.getMessage()="+ex.getMessage()); 
                    return null; 
                } 
            }
        }
    }

    public synchronized int CryptRead() {
        try {
            final ByteArrayOutputStream raw = RawRead();
            if (raw == null) {
                if (DBG1) _L("CryptRead() error: null raw data.");
                return 1;
            }

            final byte[] braw = raw.toByteArray();
            byte[] sign = new byte[6], len = new byte[10], md5 = new byte[16], crypted = new byte[braw.length-32];

            for (int i=0; i<6; i++) sign[i] = braw[i];
            for (int i=6; i<16; i++) len[i-6] = braw[i];
            for (int i=16; i<32; i++) md5[i-16] = braw[i];
            for (int i=32; i<braw.length; i++) crypted[i-32] = braw[i];

            long contentlen = Long.parseLong(new String(len).replace("Z", ""), 10);
            if (contentlen != crypted.length) {
                if (DBG1) _L("CryptRead() error: contentlen="+contentlen+"; crypted.length="+crypted.length);
                return 2;
            }

            byte[] md5_gen = MD5(crypted);
            for (int i=0; i<16; i++) if (md5_gen[i] != md5[i]) {
                if (DBG1) _L("CryptRead() error: MD5 incorrect.");
                return 3;
            }
        
            byte[] uncrypt = AESDecrypt(crypted, KEY1.getBytes());
            if (uncrypt == null) {
                if (DBG1) _L("CryptRead() error: AESDecrypt return a error.");
                return 4;
            }

            ReadBuffer = new ByteArrayOutputStream();
            ReadBuffer.write(uncrypt, 0, uncrypt.length);
        } catch (Exception ex) { 
            if (DBG1) _L("CryptRead() unknown error: ex.getMessage()="+ex.getMessage()); 
            return 1000; 
        }
        return 0;
    }

    private synchronized byte[] MD5(byte[] unsafe) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(unsafe);
            return md.digest();
        } catch (NoSuchAlgorithmException ex) { }
        return null;
    }

    private synchronized byte[] AESDecrypt(byte[] value, byte[] password) {
        try {
            final byte[] pwd    = Arrays.copyOf(MD5(password), 16);
            final SecretKey key = new SecretKeySpec(pwd, "AES");
            final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "SunJCE");
            cipher.init(Cipher.DECRYPT_MODE, key);
            final byte[] decrypted = cipher.doFinal(value);
            return decrypted;
        } 
        catch (NoSuchAlgorithmException ex)         { _L("__AESDecrypt: NoSuchAlgorithmException"    ); } 
        catch (NoSuchProviderException ex)          { _L("__AESDecrypt: NoSuchProviderException"     ); } 
        catch (NoSuchPaddingException ex)           { _L("__AESDecrypt: NoSuchProviderException"     ); } 
        catch (InvalidKeyException ex)              { _L("__AESDecrypt: InvalidKeyException"         ); } 
        catch (IllegalBlockSizeException ex)        { _L("__AESDecrypt: IllegalBlockSizeException"   ); } 
        catch (BadPaddingException ex)              { _L("__AESDecrypt: BadPaddingException"         ); }
        return null;
    }
    
    private synchronized byte[] AESEnrypt(byte[] value, byte[] password) {
        try {
            final byte[] pwd    = Arrays.copyOf(MD5(password), 16);
            final SecretKey key = new SecretKeySpec(pwd, "AES");
            final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "SunJCE");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            final byte[] encrypted = cipher.doFinal(value);
            return encrypted;
        } 
        catch (NoSuchAlgorithmException ex)         { _L("__AESCrypt: NoSuchAlgorithmException"     ); } 
        catch (NoSuchPaddingException ex)           { _L("__AESCrypt: NoSuchProviderException"      ); } 
        catch (NoSuchProviderException ex)          { _L("__AESCrypt: NoSuchProviderException"      ); } 
        catch (InvalidKeyException ex)              { _L("__AESCrypt: InvalidKeyException"          ); } 
        catch (IllegalBlockSizeException ex)        { _L("__AESCrypt: IllegalBlockSizeException"    ); } 
        catch (BadPaddingException ex)              { _L("__AESCrypt: BadPaddingException"          ); }
        return null;
    }
    
    public synchronized Object CryptReadObject(int command) {
        try {
            final ByteArrayOutputStream in = new ByteArrayOutputStream();
            in.write(command);
            in.write(0);
            CryptWrite(in);
            in.close();
            
            if (CryptRead() == 0) {
                if (ReadBuffer == null) return null;
                if (ReadBuffer.size() < 1) return null;
                
                final byte[] arr = ReadBuffer.toByteArray();
                final ByteArrayInputStream bais = new ByteArrayInputStream(arr);
                final ObjectInputStream ois = new ObjectInputStream(bais);
                
                final Object retval = ois.readObject();
                
                ois.close();
                bais.close();
                
                return retval;
            }
        } catch (IOException | ClassNotFoundException ex) {
            _L("SQLSelect() error: "+ex.getMessage());
        }
        return null;
    }
    
    public synchronized ArrayList<Map<String,String>> SQLSelect(String mSQL) {
        try {
            final byte[] str = mSQL.getBytes("UTF-8");
            final ByteArrayOutputStream in = new ByteArrayOutputStream();
            in.write(64);
            in.write(str, 0, str.length);
            CryptWrite(in);
            in.close();
            
            if (CryptRead() == 0) {
                if (ReadBuffer == null) return null;
                if (ReadBuffer.size() < 1) return null;
                
                final byte[] arr = ReadBuffer.toByteArray();
                final ByteArrayInputStream bais = new ByteArrayInputStream(arr);
                final ObjectInputStream ois = new ObjectInputStream(bais);
                
                final ArrayList<Map<String,String>> retval = (ArrayList<Map<String,String>>) ois.readObject();
                
                ois.close();
                bais.close();
                
                //System.err.print(ReadBuffer.toString());
                
                return retval;
            }
        } catch (IOException | ClassNotFoundException ex) {
            _L("SQLSelect() error: "+ex.getMessage());
        }
        return null;
    }
    
    public synchronized void SQLInsertUpdate(JNekoPrepSQL ps) {
        CryptWriteObject(63, ps); 
    }

    public void GetIfListToComboBox(JComboBox c) {
        ByteArrayOutputStream in = new ByteArrayOutputStream();
        in.write(62); 
        in.write(0);

        CryptWrite(in);
        
        if (CryptRead() == 0) {
            c.removeAllItems();
            final String buf[] = ReadBuffer.toString().split("\\|\\|");
            for (String s : buf) {
                c.addItem(s);
            }
        }
    }
    
    public synchronized void ReloadServerConfig() {
        try {
            ByteArrayOutputStream in = new ByteArrayOutputStream();
            in.write(61);
            in.write(0);
            CryptWrite(in);
            in.close();
        } catch (IOException ex) {}
    } 
}
