package GalleryGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;

public class FTPUploader {
    
    private int         DisconnectCounter   = 0;
    private int         FilesCounter        = 0;
    private int         DirsCounter         = 0;
    private long        CurrentFileSize     = 0;
    
    private String      LocalDirectory      = "";
    private String      RemoteDirectory     = "";
    private String      FTPHost             = "";
    private String      FTPUser             = "";
    private String      FTPPassword         = "";
    private String      CurrentFile         = "";
    private String      CurrentDir          = "";
    
    private boolean     disconnect          = false;
    private boolean     forceReconnect      = false;
    
    private FTPClient ftp;
    private LibTools  lt = new LibTools();
    
    private JProgressBar callback_jpb_dir = null;
    private JProgressBar callback_jpb_all = null;
    
    private ArrayList<String> directories;
    
    public FTPUploader() {}

    private void __calcDirs(String dir) {
        File f = new File(dir), g;
        String list[] = f.list();
        for (int i=0; i<list.length; i++) {
            g = new File(lt.textSlash(dir) + list[i]);
            if (g.isDirectory()) {
                DirsCounter++;
                directories.add(lt.textSlash(dir).replace(lt.textSlash(LocalDirectory), "") + list[i]); 
                __calcDirs(lt.textSlash(dir) + list[i]);
            } else {
                if ((list[i].endsWith(".jpg")) || (list[i].endsWith(".gif")) 
                                               || (list[i].endsWith(".png")) || (list[i].endsWith(".jpeg")) ) {
                    FilesCounter++;
                }  
            }
            if (callback_jpb_all != null) {
                callback_jpb_all.setString("Directories: " + DirsCounter + "; Images: " + FilesCounter); 
            }
        }
        callback_jpb_all.setString("");
    }
    
    private void calcDirs() {
         directories = new ArrayList<String>();
         FilesCounter = 0;
         DirsCounter = 0;
         __calcDirs(LocalDirectory);
    }
    
    private void waitForReconnection() {
        if (callback_jpb_all != null) {
            callback_jpb_all.setString("[w] waiting for reconnect..."); 
        }
        forceReconnect = true;
        while (forceReconnect) {
            if (disconnect) {
                return;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex1) { }  
        }
    }
    
    private void sendFiles(String vDir) {
        if (callback_jpb_dir != null) {
            callback_jpb_dir.setMinimum(0);
            callback_jpb_dir.setStringPainted(true); 
            callback_jpb_dir.setValue(0); 
        }
        
        File f = new File(lt.textSlash(LocalDirectory) + vDir), g;
        String list[] = f.list();
        if (callback_jpb_dir != null) {
            callback_jpb_dir.setMaximum(list.length - 1); 
        }
        
        for (int i=0; i<list.length; i++) {
            if (disconnect) {
                break;
            }
            g = new File(lt.textSlash(lt.textSlash(LocalDirectory) + vDir) + list[i]);
            if (g.canRead() && g.isFile()) {
                if ((list[i].endsWith(".gif")) || (list[i].endsWith(".jpeg")) 
                                           || (list[i].endsWith(".jpg")) || (list[i].endsWith(".png"))) {
                    CurrentFileSize = g.length();
                    CurrentFile = list[i];
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(lt.textSlash(lt.textSlash(LocalDirectory) + vDir) + list[i]);
                        System.out.println("LFILE: " + lt.textSlash(lt.textSlash(LocalDirectory) + vDir) + list[i]);
                    } catch (IOException ex) {
                        Logger.getLogger(FTPUploader.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    try { 
                        if (callback_jpb_dir != null) {
                            callback_jpb_dir.setValue(i); 
//                            callback_jpb_dir.setString(i + " of " + (list.length-1));
                        }
                        
//                        ftp.setFileTransferMode(FTP.BINARY_FILE_TYPE);
                        System.out.println("RFILE: " + lt.textSlash(lt.textSlash(RemoteDirectory) + vDir) + list[i]);
                        
                        ftp.storeFile(lt.textSlash(lt.textSlash(RemoteDirectory) + vDir) + list[i], fis); 
                        
                    } catch (FTPConnectionClosedException ex) {
                        i--;
                        if (fis != null) {
                            try { fis.close(); } catch (IOException ex1) { }
                        }
                        waitForReconnection();
                    } catch (IOException ex) {
                        //Logger.getLogger(FTPUploader.class.getName()).log(Level.SEVERE, null, ex);
                        
                        i--;
                        if (fis != null) {
                            try { fis.close(); } catch (IOException ex1) { }
                        }
                        waitForReconnection();
                    }
                    
                    try {
                        fis.close();
                    } catch (IOException ex) {
                        Logger.getLogger(FTPUploader.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
       if (callback_jpb_dir != null) {
            callback_jpb_dir.setString(""); 
            callback_jpb_dir.setValue(0); 
        } 
    }
    
    private void setCopyStreamListener() {
        ftp.setCopyStreamListener(new CopyStreamListener() {
            public void bytesTransferred(CopyStreamEvent event) {
                
            }
            
            public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
                if (callback_jpb_dir != null) {
                    callback_jpb_dir.setString("[file: " + CurrentFile.toLowerCase() + "] - [" + totalBytesTransferred + " bytes of " + CurrentFileSize + "]");
                }
            }
        });
    }
    
    private Runnable nThread = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (ftp.isConnected() == false) {
                    try {
                        DisconnectCounter++;
                        ftp.connect(FTPHost);
                        if (ftp.login(FTPUser, FTPPassword)) {
                            if (ftp.changeWorkingDirectory(RemoteDirectory) == false) {
                                if (callback_jpb_all != null) {
                                    callback_jpb_all.setString("[e] Нет прав на папку или она удалена"); 
                                }
                                forceReconnect = false;
                                disconnect = true;
                                return;
                            }
                            ftp.enterLocalPassiveMode();
                            setCopyStreamListener();
                            forceReconnect = false;
                        } else {
                            if (callback_jpb_all != null) {
                                callback_jpb_all.setString("[e] Неверный логин или пароль"); 
                            }
                            forceReconnect = false;
                            disconnect = true;
                            return;
                        }
                    } catch (IOException ex) {
                        if (forceReconnect) {
                            try { Thread.sleep(3000); } catch (InterruptedException ex1) { }
                        }
                    }
                }
                try {
                    for (int im=0; im<30; im++) {
                        Thread.sleep(100);
                        if (forceReconnect) {
                            im = 31;
                        }
                    }
                    
                    if (disconnect) {
                        return;
                    }
                } catch (InterruptedException ex) { }
            }
        }
    };

    
    public void setCallbackPB(JProgressBar all, JProgressBar dir) {
        callback_jpb_all = all;
        callback_jpb_dir = dir;
    }
    
    public boolean setLocalDirectory(String dir) {
        File f = new File(dir);
        if (f.isDirectory()) {
            LocalDirectory = dir;
            return true;
        }
        return false;
    }
    
    public void setRemoteDirectory(String dir) {
        RemoteDirectory = dir;
    }
    
    public boolean connect(String host, String username, String password) {
        if (callback_jpb_all != null) {
            callback_jpb_all.setStringPainted(true); 
        }
        disconnect = false;
        DisconnectCounter = 0;
        ftp = new FTPClient();
        try {
            ftp.connect(host);
            if (ftp.login(username, password) == false) {
                if (callback_jpb_all != null) {
                    callback_jpb_all.setString("Неверный логин или пароль"); 
                }
                ftp.disconnect();
                return false;
            }
            if (ftp.changeWorkingDirectory(RemoteDirectory) == false) {
                if (callback_jpb_all != null) {
                    callback_jpb_all.setString("Нет прав на директорию или она не существует"); 
                }
                ftp.logout();
                ftp.disconnect();
                return false;
            }
            ftp.enterLocalPassiveMode();
            setCopyStreamListener();
            
            FTPHost = host;
            FTPUser = username;
            FTPPassword = password;
            
            new Thread(nThread).start(); 
            
            return true;
        } catch (SocketException ex) {
            if (callback_jpb_all != null) {
                callback_jpb_all.setString("Нет подключения"); 
            }
            //Logger.getLogger(FTPUploader.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(FTPUploader.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public void send() {
        if (callback_jpb_all != null) {
            callback_jpb_all.setMinimum(0);
            callback_jpb_all.setStringPainted(true); 
            callback_jpb_all.setValue(0); 
        }
        calcDirs();
        
        if (callback_jpb_all != null) {
            callback_jpb_all.setMaximum(DirsCounter); 
        }
        
        for (int i=0; i<directories.size(); i++) {
            try {
                if (callback_jpb_all != null) {
                    callback_jpb_all.setValue(i);
                    callback_jpb_all.setString("[Directory: " + directories.get(i) + "] - [" + (i+1) + " of " + DirsCounter + "]");
                }
                
                ftp.makeDirectory(lt.textSlash(RemoteDirectory) + directories.get(i));
                sendFiles(directories.get(i));
                
                if (disconnect) {
                    return;
                }
            } catch (FTPConnectionClosedException ex) {
                i--;
                waitForReconnection();
            } catch (IOException ex) {
                i--;
                waitForReconnection();
            }
        }
        
        if (callback_jpb_all != null) {
            callback_jpb_all.setValue(0);
            callback_jpb_all.setString("");
        }
    }
    
    public void stop() {
        disconnect = true;
    }
    
    public void disconnect() {
        disconnect = true;
        try {
            ftp.logout();
            ftp.disconnect();
        } catch (IOException ex) {
            Logger.getLogger(FTPUploader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
