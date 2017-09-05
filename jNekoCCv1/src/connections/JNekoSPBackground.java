package connections;

import datasource.JNekoServerInfo;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JLabel;

public class JNekoSPBackground {
    private static final Color 
            JNekoOKColor2       = new Color(0, 190, 0),
            JNekoFailColor1     = new Color(140, 0, 0),
            JNekoFailColor2     = new Color(190, 0, 0);
        
    private static boolean
            rtColorCheck        = true,
            DBG1                = true;
    
    private static JLabel           
            JNekoSPCallback     = null;
    private static String           
            JNekoSPCallbackText = "";

    public  static JNekoServerInfo  
            jNekoServerInfo     = new JNekoServerInfo();
    
    private static ArrayList<JNekoSPBackgroundCallback> 
            listeners = new ArrayList<>(); 
    
    private static long             
            ThreadExecTime      = 0;
    
    @SuppressWarnings({"SleepWhileInLoop", "Convert2Lambda"})
    private static final Thread rt = new Thread(new Runnable() {
        @Override
        public void run() {
            long StartTime, EndTime;
            while (true) {
                StartTime = new Date().getTime();
                
                if (JNekoSP.GetSP() != null) {
                    if (jNekoServerInfo.ReloadCurrentInfo(JNekoSP.GetSP())) {
                        if (listeners.size() > 0) {
                            for (JNekoSPBackgroundCallback jsi : listeners) {
                                if (jsi != null) jsi.OnGetServerData(jNekoServerInfo);
                            }
                        }
                        __green();
                    } else {
                        __red();
                    }
                } else {
                    __red();
                }
                
                EndTime = new Date().getTime();
                ThreadExecTime = EndTime - StartTime;
                
                try { Thread.sleep(1000); } catch (InterruptedException ex) { }
            }
        }
    }); 
    
    private static void __red() {
        if (JNekoSPCallback != null) JNekoSPCallback.setForeground((rtColorCheck) ? JNekoFailColor1 : JNekoFailColor2);
        rtColorCheck = !rtColorCheck;
        JNekoSP.DisconnectS();
        JNekoSP.ConnectS();
    }
    
    private static void __green() {
        if (JNekoSPCallback != null) {
            JNekoSPCallback.setForeground(JNekoOKColor2);
            JNekoSPCallback.setText(JNekoSPCallbackText + " " + ThreadExecTime);
        }
    }
    
    public static void Disconnect() { 
        JNekoSP.DisconnectS();
    }
    
    public static void SetCallback(JLabel __JNekoSPCallback) {
        if (__JNekoSPCallback != null) {
            JNekoSPCallback = __JNekoSPCallback;
            JNekoSPCallbackText = JNekoSPCallback.getText();
        }
    }
    
    public static void RunSupportThread() {
        JNekoSP.ConnectS();
        rt.start();
    }
    
    public static void AddServerInfoListener(JNekoSPBackgroundCallback listener) {
        if (listener != null) listeners.add(listener);
    }
}
