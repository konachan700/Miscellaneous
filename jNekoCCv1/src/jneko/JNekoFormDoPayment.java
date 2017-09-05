package jneko;

import jnekotabs.MainUsersList;
import connections.JNekoPrepSQL;
import connections.JNekoSP;
//import dbengine.sMySQLWarappers;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import xlist.SettingsCheckBoxElement;
import xlist.SettingsTextBoxElement;
import xlist.GlobalYesNoButtonsElement;
import xlist.XListButtonsActionListener;

public class JNekoFormDoPayment extends javax.swing.JFrame {
    private final ImageIcon groupIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/a2e.png"));
    private final Color ColorForBG = new Color(80, 50, 50);
    private final String xDID;
    private final JFrame THIS = this;
    private final MainUsersList fParent;
   
    @SuppressWarnings("Convert2Lambda")
    private final XListButtonsActionListener xbi = new XListButtonsActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID) {
            if (buttonID == 1101) THIS.dispose();
            if (buttonID == 1100) {
                try {
                    final ArrayList<Map<String,String>> stx;
                    final Map<String,String> st;
                    stx = JNekoSP.GetSP().SQLSelect("SELECT * FROM Users WHERE did="+xDID+";");
                    if (stx != null) st = stx.get(0); else st = null;
                    int mc1 = (st != null) ? Integer.parseInt(st.get("userMoney"), 10) : 1;
                    
                    final long mc = Long.parseLong(((SettingsTextBoxElement)(xlist1.GetItem("MAIN", "MONEYCOUNT").GetComponent())).GetValue(), 10);
                    final long moneyCount = (mc < 0) ? (-1 * mc) : mc;
                    final String comment = ((SettingsTextBoxElement)(xlist1.GetItem("MAIN", "MONEYCOMMENT").GetComponent())).GetValue();
                    final boolean minus = ((SettingsCheckBoxElement)(xlist1.GetItem("MAIN", "MONEYMINUS").GetComponent())).GetValue();
                    
                    final JNekoPrepSQL ps1 = new JNekoPrepSQL("UPDATE Users SET userMoney=userMoney"+((minus) ? "-" : "+")+(moneyCount*100)+" WHERE did=?;");
                    ps1.AddParam(xDID);
                    JNekoSP.GetSP().SQLInsertUpdate(ps1);
                    
                    final JNekoPrepSQL ps2 = new JNekoPrepSQL("INSERT INTO Payments VALUES(?, ?, ?, 1, ?);");
                    ps2.AddParam(new Date().getTime() + "");
                    ps2.AddParam(xDID);
                    ps2.AddParam((moneyCount * ((minus) ? -100 : 100)) + "");
                    ps2.AddParam(comment);
                    JNekoSP.GetSP().SQLInsertUpdate(ps2);
                    
                    if (mc1 <= 0) JNekoSP.GetSP().ReloadServerConfig();
                    
                    fParent.Refresh();
                    THIS.dispose();
                } catch (Exception e) { }
            }
        }
    };

    public JNekoFormDoPayment(String __DID, MainUsersList __parent) {
        initComponents();
        xDID = __DID;
        fParent = __parent;
        jPanel1.setBackground(ColorForBG);
        jPanel2.setBackground(ColorForBG);
        
        Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((s.width/2)-(this.getWidth()/2), (s.height/2)-(this.getHeight()/2), this.getWidth(), this.getHeight());
        
        xlist1.AddGroupHeader("MAIN", ColorForBG.darker(), "Внести или списать деньги", groupIcon, 0);
        
        xlist1.AddItem("MONEYCOUNT",   "MAIN",  new SettingsTextBoxElement("0", "Количество денег", false));
        xlist1.AddItem("MONEYCOMMENT", "MAIN",  new SettingsTextBoxElement("", "Комментарий к платежу", false));
        xlist1.AddItem("MONEYMINUS",   "MAIN",  new SettingsCheckBoxElement("Это не платеж, а списание", false));
        xlist1.AddItem("YESNOBTNS",    "MAIN", new GlobalYesNoButtonsElement("Подтвердить платеж", "Забыть изменения", xbi));
        
        xlist1.setBackgroundColor(ColorForBG);
        xlist1.Commit();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        xlist1 = new xlist.XList();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new-logo-left.png"))); // NOI18N

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new-logo-right.png"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 330, Short.MAX_VALUE)
                .addComponent(jLabel2))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1)
            .addComponent(jLabel2)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xlist1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xlist1, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private xlist.XList xlist1;
    // End of variables declaration//GEN-END:variables
}
