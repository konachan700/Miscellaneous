package jneko;

import jnekotabs.UnconfuguredONTs;
import connections.JNekoSP;
import connections.MA4000Wrappers;
//import dbengine.sMySQLWarappers;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import xlist.SettingsComboBoxElement;
import xlist.SettingsTextBoxElement;
import xlist.GlobalYesNoButtonsElement;
import xlist.XListButtonsActionListener;

public class JNekoFormAddNewONT extends javax.swing.JFrame {
    private final ImageIcon groupIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/a2e.png"));
    private final Color ColorForBG = new Color(40, 90, 90);
    private final UnconfuguredONTs fParent;
    private final JNekoFormAddNewONT THIS = this;
    private final String SLOT, SERIAL, CHANNEL;

    @SuppressWarnings("Convert2Lambda")
    private final XListButtonsActionListener xbi = new XListButtonsActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID) {
            if (buttonID == 1101) THIS.dispose();
            if (buttonID == 1100) {
                final String vlan =    ((SettingsTextBoxElement)(xlist1.GetItem("MAINOPT", "VLAN").GetComponent())).GetValue();
                final String pmgmt =   ((SettingsTextBoxElement)(xlist1.GetItem("MAINOPT", "Profilemanagment").GetComponent())).GetValue();
                final String psvc =    ((SettingsTextBoxElement)(xlist1.GetItem("MAINOPT", "Profileservices").GetComponent())).GetValue();
                final String pmcast =  ((SettingsTextBoxElement)(xlist1.GetItem("MAINOPT", "Profilemulticast").GetComponent())).GetValue();
                
                final String username =  ((SettingsTextBoxElement)(xlist1.GetItem("MAINOPT", "USERNAME").GetComponent())).GetValue();
                final String wifi_ssid =  ((SettingsTextBoxElement)(xlist1.GetItem("MAINOPT", "WIFISSID").GetComponent())).GetValue();
                final String profile =   ((SettingsComboBoxElement)(xlist1.GetItem("MAINOPT", "ACSPROFILE").GetComponent())).GetValue();
                
                final String wifi_pass =  ((SettingsTextBoxElement)(xlist1.GetItem("ESTOPT", "WIFIpassword").GetComponent())).GetValue();
                final String pppoe_login =  ((SettingsTextBoxElement)(xlist1.GetItem("ESTOPT", "PPPoElogin").GetComponent())).GetValue();
                final String pppoe_pass =  ((SettingsTextBoxElement)(xlist1.GetItem("ESTOPT", "PPPoEpassword").GetComponent())).GetValue();
                final String admin_pass =  ((SettingsTextBoxElement)(xlist1.GetItem("ESTOPT", "Adminpassword").GetComponent())).GetValue();
                final String user_pass =  ((SettingsTextBoxElement)(xlist1.GetItem("ESTOPT", "Userpassword").GetComponent())).GetValue();
                
                @SuppressWarnings("Convert2Lambda")
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JNekoProgressDisplayClass.On();
                        if (MA4000Wrappers.CreateNewONTOnSlot(SERIAL, SLOT, vlan, pmgmt, psvc, pmcast) == 0) {
                            if (MA4000Wrappers.CreateNewONTOnACS(SERIAL, wifi_ssid, wifi_pass, 
                                    pppoe_login, pppoe_pass, vlan, admin_pass, user_pass, profile, username, SLOT) == 0) {
                                fParent.Refresh();
                            }
                        }

                        JNekoProgressDisplayClass.Off();
                        THIS.dispose();
                    }
                });
                t.start();
            }
        }
    };
        
    @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
    public JNekoFormAddNewONT(UnconfuguredONTs __parent, String PONSerial, String Slot, String Channel) {
        initComponents();
        SERIAL = PONSerial;
        SLOT = Slot;
        CHANNEL = Channel;
        
        JNekoProgressDisplayClass.AddElement(jProgressBar1);
        fParent = __parent;
        jPanel1.setBackground(ColorForBG);
        jPanel2.setBackground(ColorForBG);
        
        Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((s.width/2)-(this.getWidth()/2), (s.height/2)-(this.getHeight()/2), this.getWidth(), this.getHeight());
        
        xlist1.AddGroupHeader("INFO", ColorForBG.darker(), "Информация ONT (только чтение)", groupIcon, 0);
        xlist1.AddGroupHeader("MAINOPT", ColorForBG.darker(), "Основные настройки ONT", groupIcon, 0);
        xlist1.AddGroupHeader("ESTOPT", ColorForBG.darker(), "Дополнительные настройки ONT", groupIcon, 0);
        
        xlist1.AddItem("PONSERIAL", "INFO",  new SettingsTextBoxElement(PONSerial, "PON Serial", false));
        xlist1.AddItem("SLOT",      "INFO",  new SettingsTextBoxElement(Slot, "Slot", false));
        xlist1.AddItem("CHANNEL",   "INFO",  new SettingsTextBoxElement(Channel, "Channel", false));
        
        xlist1.AddItem("VLAN",              "MAINOPT",  new SettingsTextBoxElement("ADDONT_VLAN", "VLAN"));
        xlist1.AddItem("WIFISSID",          "MAINOPT",  new SettingsTextBoxElement("", "WIFI SSID", false));
        xlist1.AddItem("USERNAME",          "MAINOPT",  new SettingsTextBoxElement(PONSerial, "Имя пользователя", false));
        xlist1.AddItem("Profileservices",   "MAINOPT",  new SettingsTextBoxElement("ADDONT_PSVC", "Profile services"));
        xlist1.AddItem("Profilemanagment",  "MAINOPT",  new SettingsTextBoxElement("ADDONT_PMGMT", "Profile managment"));
        xlist1.AddItem("Profilemulticast",  "MAINOPT",  new SettingsTextBoxElement("ADDONT_PMCAST", "Profile multicast"));
        
        xlist1.AddItem("PPPoElogin",        "ESTOPT",  new SettingsTextBoxElement(PONSerial, "PPPoE login", false));
        xlist1.AddItem("PPPoEpassword",     "ESTOPT",  new SettingsTextBoxElement(PONSerial.toLowerCase().replace("eltx", ""), "PPPoE password", false));
        xlist1.AddItem("WIFIpassword",      "ESTOPT",  new SettingsTextBoxElement(PONSerial, "WIFI password", false));
        xlist1.AddItem("Userpassword",      "ESTOPT",  new SettingsTextBoxElement(PONSerial, "User password", false));
        xlist1.AddItem("Adminpassword",     "ESTOPT",  new SettingsTextBoxElement(PONSerial, "Admin password", false));
        
        xlist1.setBackgroundColor(ColorForBG);
        xlist1.VPBON();
        xlist1.Commit();

        @SuppressWarnings("Convert2Lambda")
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                JNekoProgressDisplayClass.On();
                final String s[] = MA4000Wrappers.GetACSProfilesList();
                if (s != null) {
                    SettingsComboBoxElement f = new SettingsComboBoxElement("", "Профиль ACS");
                    f.FillFromArray(s);
                    xlist1.AddItem("ACSPROFILE", "MAINOPT", f);
                    xlist1.AddItem("YESNOBTNS", "ESTOPT", new GlobalYesNoButtonsElement("Сохранить", "Забыть изменения", xbi));
                    xlist1.Commit();
                }
                
                JNekoProgressDisplayClass.Off();
            }
        });
        t.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        xlist1 = new xlist.XList();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));

        jPanel1.setBackground(new java.awt.Color(0, 51, 51));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new-logo-right.png"))); // NOI18N

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new-logo-left.png"))); // NOI18N

        jProgressBar1.setString("Please, wait...");
        jProgressBar1.setStringPainted(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 184, Short.MAX_VALUE)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1)
            .addComponent(jLabel2)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(xlist1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xlist1, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JProgressBar jProgressBar1;
    private xlist.XList xlist1;
    // End of variables declaration//GEN-END:variables
}
