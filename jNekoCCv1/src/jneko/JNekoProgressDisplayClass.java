package jneko;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JProgressBar;

public class JNekoProgressDisplayClass {
    public interface PDActionListener {
        public void OnStartingWait();
        public void OnStoppedWait();
    }
    
    private static final    CopyOnWriteArrayList<PDActionListener>  PDActionListeners    = new CopyOnWriteArrayList<>();
    private static final    ArrayList<JProgressBar>                 JPB                  = new ArrayList<>();
    private static          boolean                                 isVisible            = false;
    private static          int                                     WaitCounter          = 0;

    @SuppressWarnings("SleepWhileInLoop")
    private static final Thread rt = new Thread(() -> {
            while (true) {
                Increment();
                try { Thread.sleep(35); } catch (InterruptedException ex) { }
            }
    });
    
    public static void ActionListenerRegister(PDActionListener al) {
        if (al == null) return;
        PDActionListeners.add(al);
    }

    public static boolean IsWaiting() {
        return isVisible;
    }

    public static void Start() {
        rt.start();
    }
    
    public static void On() {
        isVisible = true;
        WaitCounter++;
        
//        System.err.println("On(): WaitCounter = "+WaitCounter);
        if ((PDActionListeners.size() > 0) && (WaitCounter == 1)) {
            for (PDActionListener pd : PDActionListeners) pd.OnStartingWait();
        }
    }
    
    public static void Off() {
        if ((WaitCounter > 0)) {
            WaitCounter--;
        }
        
//        System.err.println("Off(): WaitCounter = "+WaitCounter);
        if ((WaitCounter <= 0)) {
            isVisible = false;
            if (PDActionListeners.size() > 0) {
                for (PDActionListener pd : PDActionListeners) pd.OnStoppedWait();
            }
        }
    }
    
    private static void Increment() {
        if (JPB.size() <= 0) return;
        for (JProgressBar j : JPB) {
            try {
                j.setVisible(isVisible); 
                j.setValue((isVisible) ? (j.getValue()+1) : 0);
                if (j.getValue() > 98) j.setValue(0);
            } catch (Exception e) {
                JPB.remove(j);
            }
        }
    }

    public static void AddElement(JProgressBar jpb) {
        if (jpb == null) return;
        if (JPB.contains(jpb)) return;
        
        JPB.add(jpb);
    }
    
    public static void RemoveElement(JProgressBar jpb) {
        if (jpb != null) JPB.remove(jpb);
    }
}
