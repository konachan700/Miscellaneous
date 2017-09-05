
package botconfigz;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class RS232 {
    private volatile static boolean 
            xColor = false;
    
    private final static Color 
            aGColor = new Color(170, 250, 170), 
            bGColor = new Color(120, 200, 120),
            aRColor = new Color(255, 170, 170), 
            bRColor = new Color(200, 170, 170);
    
    private static int counterZ = 0;
    private static class SP_Listener implements SerialPortEventListener {
        @Override
        public void serialEvent(SerialPortEvent spe) {
            if (spe.isRXCHAR()) {
                try { 
                    System.err.print(SP.readHexString());
                    counterZ++;
                    if (counterZ >= 64) {
                        counterZ = 0;
                        System.err.println(" ");
                    }
                } catch (SerialPortException ex) {
                    Logger.getLogger(RS232.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    @SuppressWarnings("Convert2Lambda")
    private static void _red() {
        try {
            SwingUtilities.invokeAndWait(new Runnable(){
                @Override
                public void run(){
                    СontrolComponent.setForeground(xColor ? aRColor : bRColor);
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) { }
    }
    
    @SuppressWarnings("Convert2Lambda")
    private static void _green() {
        try {
            SwingUtilities.invokeAndWait(new Runnable(){
                @Override
                public void run(){
                    СontrolComponent.setForeground(xColor ? aGColor : bGColor);
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) { }
    }
    
    @SuppressWarnings({"SleepWhileInLoop", "Convert2Lambda"})
    private static final Thread control_t = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (SP == null) {
                    _red();
                } else {
                    if (SP.isOpened()) 
                        _green();
                    else
                        _red();
                }

                xColor = !xColor;
                try { Thread.sleep(333); } catch (InterruptedException ex) { }
            }
        }
    });

    private static SerialPort SP = null;
    private static final SP_Listener listener = new SP_Listener();
    private static JLabel СontrolComponent;
    
    public static int Init(String port, int speed) {
        if (SP != null) {
            try {
                SP.closePort();
                SP = null;
            } catch (SerialPortException ex) {
                SP = null;
                //Logger.getLogger(RS232.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //SerialPort.
        if ((port.contains("tty")) || (port.contains("COM"))) {
            SP = new SerialPort(port);
        } else {
            return 3;
        }

        try {
            SP.openPort();
            SP.setParams(38400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            SP.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            SP.addEventListener(listener); 
            
        } catch (SerialPortException ex) {
            Logger.getLogger(RS232.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        }
        
        return 0;
    } 
    
    public static void writeCmd16b(ByteArrayOutputStream data, int CMDL, int CMDH) {
        /*
            #define USART_CMD_H                 13
            #define USART_CMD_L                 14
            #define USART_CMD_XOR_CHECKSUM	15
        */
        final int count = data.size(), qlen = 16;
        if (count > (qlen-3)) {
            throw new RuntimeException("Out of RS232 buffer size ("+(qlen-3)+" bytes)");
        }
        
        for (int i=count; i<qlen; i++) data.write(0);
        
        byte[] datab = data.toByteArray();
        datab[qlen-3] = (byte) (CMDH & 0xff);
        datab[qlen-2] = (byte) (CMDL & 0xff);
        
        byte crc = 0;
        for (int i=0; i<(qlen-1); i++) crc = (byte) (crc ^ datab[i]);
        
        datab[qlen-1] = (byte) (crc & 0xff);
        
        System.out.println(Arrays.toString(datab)); 
        
        if (SP != null) {
            try {
                if (SP.isOpened()) SP.writeBytes(datab);
            } catch (SerialPortException ex) {
                Logger.getLogger(RS232.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void Write8(byte b) {
        if (SP != null) {
            try {
                if (SP.isOpened()) {
                    SP.writeByte(b);
                    SP.purgePort(SerialPort.PURGE_TXCLEAR | SerialPort.PURGE_RXCLEAR);
                }
            } catch (SerialPortException ex) {
                Logger.getLogger(RS232.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void RunThread(JLabel controlComponent) {
        СontrolComponent = controlComponent;
        control_t.start();
    }
}
