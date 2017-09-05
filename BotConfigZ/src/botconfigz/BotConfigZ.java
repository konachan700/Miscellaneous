package botconfigz;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import xlist_tabs.AppSettings;
import xlist_tabs.Device_i2c;
import xlist_tabs.Devices_i2c;
import xlist_tabs.Reg_i2c;
import xlist_tabs.Regs_i2c;

public class BotConfigZ {
    public static Color
            xxAppConfigColor = new Color(75, 25, 25),
            xxI2CModuleColor = new Color(25, 75, 25)
            ;
    
    private static MainFormZ        CurrentMainFrm;
    public  static AppSettings      xAppSettings;
    public  static Devices_i2c      xDevices_i2c;
    public  static Regs_i2c         xRegs_i2c;
    public  static Reg_i2c          xReg_i2c;
    public  static Device_i2c       xDevice_i2c;
    private static DialogFrmZ       xDialogFrmZ;
    
    public static final Dimension screenParam = Toolkit.getDefaultToolkit().getScreenSize();
    
    public static void main(String[] args) {
        JNekoSQLite.Connect();
        
        xAppSettings    = new AppSettings(xxAppConfigColor);
        xDevices_i2c    = new Devices_i2c(xxI2CModuleColor);
        xRegs_i2c       = new Regs_i2c(xxI2CModuleColor);
        xReg_i2c        = new Reg_i2c(xxI2CModuleColor);
        xDevice_i2c     = new Device_i2c(xxI2CModuleColor);
        xDialogFrmZ     = new DialogFrmZ(xxI2CModuleColor);
        
        CurrentMainFrm = new MainFormZ();
        CurrentMainFrm.setVisible(true);
    }
    
    public static void ShowDialog(Component frame) {
        xDialogFrmZ.Show_panel(frame);
        xDialogFrmZ.setVisible(true);
    }
    
    public static MainFormZ GetMF() {
        return CurrentMainFrm;
    }
    
    public static void ToCenterScreen(JFrame window) {
        final int
                scrH = screenParam.height,
                scrW = screenParam.width
                ;
        if ((scrH * 2) > scrW) 
            window.setBounds((scrW/2)-(window.getWidth()/2), (scrH/2)-(window.getHeight()/2), window.getWidth(), window.getHeight());
        else 
            window.setBounds((scrW/4)-(window.getWidth()/2), (scrH/2)-(window.getHeight()/2), window.getWidth(), window.getHeight());
    }
    
}
