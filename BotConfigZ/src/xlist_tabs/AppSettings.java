package xlist_tabs;

import botconfigz.JNekoSQLite;
import botconfigz.RS232;
import java.awt.Color;
import javax.swing.ImageIcon;
import xlist.GlobalYesNoButtonsElement;
import xlist.XListButtonsActionListener;

public class AppSettings extends javax.swing.JPanel {
    private final ImageIcon ICON_CATEGORY    = new javax.swing.ImageIcon(getClass().getResource("/icons16/category.png"));
    private final ImageIcon ICON_STAR        = new javax.swing.ImageIcon(getClass().getResource("/icons16/star.png"));
    
    @SuppressWarnings("Convert2Lambda")
    private final XListButtonsActionListener xbi = new XListButtonsActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID) {
            final String COMName = JNekoSQLite.ReadAPPSettingsString("COMx_PORTNUM");
            final String COMSpeed = JNekoSQLite.ReadAPPSettingsString("COMx_SPEED");
            RS232.Init((COMName == null) ? "COM1" : COMName, (COMSpeed == null) ? 115200 : (COMSpeed.length()<3) ? 115200 : Integer.parseInt(COMSpeed, 10));
        }
    };
            
    public AppSettings(Color c) {
        initComponents();
        
        xList1.ClearAll();
        
        xList1.AddGroupHeader("COMx", c, "Настройки COM-порта", ICON_CATEGORY, 0);
        xList1.AddSettingsElementText("PORTNUM", "COMx", "COM-порт");
        xList1.AddSettingsElementText("SPEED", "COMx", "Скорость");
        xList1.AddItem("OKBTN", "COMx", new GlobalYesNoButtonsElement("Применить", null, xbi));
        xList1.VPBON();
        xList1.setBackgroundColor(c); 
        xList1.Commit();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        xList1 = new xlist.XList();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xList1, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xList1, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private xlist.XList xList1;
    // End of variables declaration//GEN-END:variables
}
