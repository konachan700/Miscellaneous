package jnekoserver;

import connections.TrafCounter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import osagent.Iptables;

public class JNekoRunnable implements Runnable  {
    private Socket                  JNekoSPSocket   = null;
    private String                  KEY1            = "jKEYneko109#*UUWkla";
    private InputStream             IS              = null;
    private OutputStream            OS              = null;
    private ByteArrayOutputStream   ReadBuffer      = null;
    private boolean                 DBG1            = false;
    
    public final int 
            MAX_FILE_SIZE    = (1024*1024*8),
            MFS_CORR         = 1024, 
            MAX_TIMEOUT_ERR  = 3, /* Это узкое место для DoS сервиса, потому что можно легко выбрать все доступные подключения */
            SOCKET_TIMEOUT   = 15,
            FILENAME_MAXLEN  = 120,
            RAW_READ_TIMEOUT = 15; /* Это тоже */
    
    private int
            TimeoutCounter   = 0;
    
    private void _L(String s) {
        System.out.println(s);
    }
    
    public JNekoRunnable(Socket s) {
        JNekoSPSocket = s;
        System.out.println("Thread started, ip="+s.getInetAddress().getHostAddress());
        try {
            IS = s.getInputStream();
            OS = s.getOutputStream();
        } catch (IOException ex) {
            IS = null;
            OS = null;
        }
    }
    
    private String[] _toArr1(byte[] buf) {
        final String xbuf2 = new String(buf, 2, buf.length-2);
        final String[] xa2 = xbuf2.split("\n");
        if (xa2 == null) { if (DBG1) _L("run(); xa2 is null;"); return null; }
        return xa2;
    }
    
    private void __run_proc_1() {
        if ((IS == null) || (OS == null)) return;
        final int retval = CryptRead();
        if (retval == 0) {
            final byte[] buffer = ReadBuffer.toByteArray();
            final int fb = buffer[0];
            switch (fb) {
                case 42: // чтение и запись мелких файлов
                    int exCommandFS = buffer[1];
                    switch (exCommandFS) {
                        case 10:
                            JNekoFS.ReadFileSingleEx(buffer, this);
                            break;
                        case 11:
                            JNekoFS.WriteFileSingleEx(buffer, this);
                            break;
                    }
                    break;
                case 64:
                    final String SQLR = new String(buffer, 1, buffer.length-1);
                    if (SQLR.length() > 10) {
                        final ArrayList<Map<String,String>> sqldata = JNekoMySQL.ExecSQL_Select(SQLR);
                        if (sqldata != null) {
                            CryptWriteObject(sqldata);
                        }
                    }
                    break;
                case 63:
                    JNekoMySQL.ExecSQL_InsertUpdate(buffer, 1, buffer.length-1);
                    break;
                case 62:
                    JNekoDBWrappers.GetIfaceList(this);
                    break;
                case 61:
                    JNekoNetwork.genServerConfig();
                    JNekoNetwork.RestartServices();
                    break;
                case 60:
                    CryptWriteObject(TrafCounter.GetLastRXTX());
                    break;
                case 59:
                    CryptWriteObject(TrafCounter.GetAllData());
                    break;
                case 58:
                    CryptWriteObject(TrafCounter.GetIfList());
                    break;
                    
                case 99:
                    
                    break;
            }
        } else {
            if (DBG1) _L("run(); IO Error;");
        }

        if (TimeoutCounter > MAX_TIMEOUT_ERR) {
            if (DBG1) _L("run(); Multiple socket reading timeouts, exit;");
            return;
        }

        if ((JNekoSPSocket.isClosed()) || (JNekoSPSocket.isInputShutdown()) || (JNekoSPSocket.isOutputShutdown())) {
            if (DBG1) _L("run(); Socket closed, exit;");
            return;
        }
    }

    @Override
    public void run() {
        while (true) {
            // В этом месте, если сюда запихнуть весь код, идет медленная утечка памяти, 1%-2% от 4гб за сутки, хз почему.
            // Если код вынести в отдельную функцию, то все ок.
            __run_proc_1();
        }
    }

    private int CryptWriteObject(Object x) {
        try {
            final ByteArrayOutputStream   sqlbaos = new ByteArrayOutputStream();
            final ObjectOutputStream      sqloos  = new ObjectOutputStream(sqlbaos);
            sqloos.writeObject(x);
            sqloos.flush();
            final int rv = CryptWrite(sqlbaos);
            sqloos.close();
            sqlbaos.close();
            return rv;
        } catch(Exception ex) {
            _L("WriteObject()-Error: "+ex.getMessage());
            return 1;
        }
    }
    
    public int CryptWrite(String s) {
        try {
            final ByteArrayOutputStream in = new ByteArrayOutputStream();
            in.write(43); 
            in.write(s.getBytes(), 0, s.length());
            final int retval = CryptWrite(in);
            in.close();
            return retval;
        } catch(Exception ex) {
            _L("CryptWrite(String): "+ex.getMessage());
            return 1;
        }
    }
    
    public int CryptWrite(ByteArrayOutputStream in) {
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
    
    private ByteArrayOutputStream RawRead() {
        try { 
            JNekoSPSocket.setSoTimeout(SOCKET_TIMEOUT);
        } catch (SocketException ex) {
            if (DBG1) _L("RawRead() JNekoSPSocket.setSoTimeout() error, ex.getMessage()="+ex.getMessage());
            return null;
        }
        
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        int b;
        long counter = 0, maxcounter = MAX_FILE_SIZE, d1 = (new Date().getTime()) + (1000 * RAW_READ_TIMEOUT);

        while (true) {
            try {
                if (JNekoSPSocket.isClosed()) {
                    if (DBG1) _L("RawRead() socket already closed.");
                    return null;
                }
                if (IS.available() > 0) b = IS.read(); else b = -1;
                if (b >= 0) {
                    ba.write(b); 
                    counter++;
                    TimeoutCounter = 0;
                } else {
                    try { Thread.sleep(5); } catch (InterruptedException ex) { }
                }
            } catch (SocketTimeoutException ex) { b = -1; TimeoutCounter++; } 
              catch (IOException ex) { b = -1; } 
            
            long d2 = new Date().getTime();
            if (d1 < d2) {
                if (DBG1) _L("RawRead() timeout error.");
                TimeoutCounter++; 
                return null;
            }    
            
            if (b >= 0) {
                if (counter >= maxcounter) { 
                    //if (DBG1) _L("RawRead() returned "+counter+" bytes of raw data.");
                    return ba;
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
                    System.arraycopy(header, 6, len, 0, 10);
                    try {
                        long contentlen = Long.parseLong(new String(len).replace("Z", ""), 10);
                        //if (DBG1) _L("RawRead() contentlen: ["+contentlen+"]"); 

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
    }

    public int CryptRead() {
        try {
            ByteArrayOutputStream raw = RawRead();
            if (raw == null) {
                raw.close();
                if (DBG1) _L("CryptRead() error: null raw data.");
                return 1;
            }

            byte[] braw = raw.toByteArray();
            raw.close();
            byte[] sign = new byte[6], len = new byte[10], md5 = new byte[16], crypted = new byte[braw.length-32];

            System.arraycopy(braw, 0,  sign,    0, 6);
            System.arraycopy(braw, 6,  len,     0, 10);
            System.arraycopy(braw, 16, md5,     0, 16);
            System.arraycopy(braw, 32, crypted, 0, braw.length-32);

            final long contentlen = Long.parseLong(new String(len).replace("Z", ""), 10);
            if (contentlen != crypted.length) {
                if (DBG1) _L("CryptRead() error: contentlen="+contentlen+"; crypted.length="+crypted.length);
                return 2;
            }

            final byte[] md5_gen = MD5(crypted);
            for (int i=0; i<16; i++) if (md5_gen[i] != md5[i]) {
                if (DBG1) _L("CryptRead() error: MD5 incorrect.");
                return 3;
            }
        
            final byte[] uncrypt = AESDecrypt(crypted, KEY1.getBytes());
            if (uncrypt == null) {
                if (DBG1) _L("CryptRead() error: AESDecrypt return a error.");
                return 4;
            }

            if (ReadBuffer != null) ReadBuffer.close();
            ReadBuffer = new ByteArrayOutputStream();
            ReadBuffer.write(uncrypt, 0, uncrypt.length);
        } catch (Exception ex) { 
            if (DBG1) _L("CryptRead() error: "+ex.getMessage()); 
            return 1000; 
        }
        return 0;
    }

    private byte[] MD5(byte[] unsafe) {
        final MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(unsafe);
            return md.digest();
        } catch (NoSuchAlgorithmException ex) { }
        return null;
    }

    private byte[] AESDecrypt(byte[] value, byte[] password) {
        try {
            final byte[] pwd = Arrays.copyOf(MD5(password), 16);
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
    
    private byte[] AESEnrypt(byte[] value, byte[] password) {
        try {
            final byte[] pwd = Arrays.copyOf(MD5(password), 16);
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
}
