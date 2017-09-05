package jneko;

import jnekotabs.TariffPlans;
import connections.JNekoPrepSQL;
import connections.JNekoSP;
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

public class JNekoFormEditTariff extends javax.swing.JFrame {
    private final ImageIcon groupIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/a2e.png"));
    private final Color ColorForBG = new Color(40, 40, 80);
    private final String xDID;
    private final JFrame THIS = this;
    private final TariffPlans fParent;
    
    @SuppressWarnings("Convert2Lambda")
    private final XListButtonsActionListener xbi = new XListButtonsActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID) {
            if (buttonID == 1101) THIS.dispose();
            if (buttonID == 1100) {
                final String fName = ((SettingsTextBoxElement)(xlist1.GetItem("MAIN", "TNAME").GetComponent())).GetValue();
                final String fUpl = ((SettingsTextBoxElement)(xlist1.GetItem("MAIN", "TUPL").GetComponent())).GetValue();
                final String fDwl = ((SettingsTextBoxElement)(xlist1.GetItem("MAIN", "TDOWNL").GetComponent())).GetValue();
                final String fCPM = ((SettingsTextBoxElement)(xlist1.GetItem("MAIN", "TCPM").GetComponent())).GetValue();
                final boolean fDS = ((SettingsCheckBoxElement)(xlist1.GetItem("MAIN", "TOLD").GetComponent())).GetValue();
                final long fCPMl  = Long.parseLong(fCPM, 10) * 100;
                
                if (xDID == null) {
                    final JNekoPrepSQL ps1 = new JNekoPrepSQL(
                            "INSERT INTO TarifPlans VALUES(?, ?, ?, ?, ?, ?);"
                    );
                    ps1.AddParam(new Date().getTime() + "");
                    ps1.AddParam(fName);
                    ps1.AddParam(fUpl);
                    ps1.AddParam(fDwl);
                    ps1.AddParam(fCPMl+"");
                    ps1.AddParam(((fDS) ? "1" : "0"));
                    JNekoSP.GetSP().SQLInsertUpdate(ps1);
                } else {
                    final JNekoPrepSQL ps1 = new JNekoPrepSQL(
                                  "UPDATE TarifPlans "
                                + "SET name=?, uploadSpeed=?, downloadSpeed=?, costPerMonth=?, state=? "
                                + "WHERE did=?;"
                    );
                    ps1.AddParam(fName);
                    ps1.AddParam(fUpl);
                    ps1.AddParam(fDwl);
                    ps1.AddParam(fCPMl+"");
                    ps1.AddParam(((fDS) ? "1" : "0"));
                    ps1.AddParam(xDID);
                    JNekoSP.GetSP().SQLInsertUpdate(ps1);
                }
                fParent.Refresh();
                THIS.dispose();
            }
        }
    };

    public JNekoFormEditTariff(String __DID, TariffPlans __parent) {
        initComponents();
        xDID = __DID;
        fParent = __parent;
        jPanel1.setBackground(ColorForBG);
        jPanel2.setBackground(ColorForBG);
        
        Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((s.width/2)-(this.getWidth()/2), (s.height/2)-(this.getHeight()/2), this.getWidth(), this.getHeight());
        
        final ArrayList<Map<String,String>> stx;
        final Map<String,String> st;
        if (xDID != null) {
            stx = JNekoSP.GetSP().SQLSelect("SELECT * FROM TarifPlans WHERE did="+__DID+";");
            if (stx != null) st = stx.get(0); else st = null;
        } else {
            st = null;
        }
        
        xlist1.AddGroupHeader("MAIN", ColorForBG.darker(), "Параметры тарифного плана", groupIcon, 0);
        
        xlist1.AddItem("TNAME", "MAIN",  new SettingsTextBoxElement(((st != null) ? st.get("name") : ""), "Название тарифного плана", false));
        xlist1.AddItem("TDOWNL", "MAIN",  new SettingsTextBoxElement(((st != null) ? st.get("downloadSpeed") : "10000"), "Скорость скачивания (кбит/с)", false));
        xlist1.AddItem("TUPL", "MAIN",  new SettingsTextBoxElement(((st != null) ? st.get("uploadSpeed") : "2500"), "Скорость отдачи (кбит/с)", false));
        xlist1.AddItem("TCPM", "MAIN",  new SettingsTextBoxElement(((st != null) ? ""+(Long.parseLong(st.get("costPerMonth"), 10)/100) : ""), "Месячная стоимость", false));
        xlist1.AddItem("TOLD", "MAIN",  new SettingsCheckBoxElement("Закрытый ТП (переход на него запрещен)", ((st != null) ? st.get("state").contains("1") : false))); 
        
        xlist1.AddItem("YESNOBTNS", "MAIN", new GlobalYesNoButtonsElement("Сохранить изменения", "Закрыть окно", xbi));
        
        xlist1.setBackgroundColor(ColorForBG);
        xlist1.Commit();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        xlist1 = new xlist.XList();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new-logo-left.png"))); // NOI18N

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new-logo-right.png"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 362, Short.MAX_VALUE)
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
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(xlist1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xlist1, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE))
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
