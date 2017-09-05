package xdialogs;

import jneko.*;
import connections.MA4000Wrappers;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import xlist.SettingsTextBoxElement;
import xlist.XListSearchFormActionListener;

public class ONTInfo extends javax.swing.JFrame {
    private final ImageIcon groupIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/a2e.png"));
    private final Color ColorForBG = new Color(40, 40, 80);
    private final ONTInfo THIS = this;
    
    @SuppressWarnings("Convert2Lambda")
    private final XListSearchFormActionListener search_listener=new XListSearchFormActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID, String searchString) {
            if ((buttonID == 1005) || (buttonID == 1007)) {
                if (searchString.length() > 0) xList1.Search("USERS_LIST", searchString); else xList1.Search("USERS_LIST", null);
            }
        }
    };

    public ONTInfo(String __ONT, int type) {
        initComponents();
        jPanel1.setBackground(ColorForBG);
        jPanel2.setBackground(ColorForBG);
        JNekoProgressDisplayClass.AddElement(jProgressBar1);
        
        Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((s.width/2)-(this.getWidth()/2), (s.height/2)-(this.getHeight()/2), this.getWidth(), this.getHeight());
        
        switch (type) {
            case 0:
                xList1.AddGroupHeader("MAIN", ColorForBG.darker(), "ONT ACS options (read only)", groupIcon, 0);
                xList1.setBackgroundColor(ColorForBG);
                xList1.VPBON();
                xList1.Commit();
                Thread t = new Thread(() -> {
                    JNekoProgressDisplayClass.On();
                    String sbsc = null;
                    String x[][] = MA4000Wrappers.GetONTACSAndUserConfig(__ONT, 0);
                    if (x != null) {
                        for (String sa[] : x) {
                            if ((sa[0].contains("Subscriber")) && (sa[0].trim().length() > 1)) sbsc = sa[1].trim();
                            xList1.AddItem(sa[0].trim(), "MAIN",  new SettingsTextBoxElement(sa[1].trim(), sa[0].trim(), false));
                        }
                    }

                    if (sbsc != null) {
                        if (sbsc.trim().length() > 1) {
                            xList1.AddGroupHeader("ADDT", ColorForBG.darker(), "ONT user options (read only)", groupIcon, 0);
                            String z[][] = MA4000Wrappers.GetONTACSAndUserConfig(sbsc, 1);
                            if (z != null) {
                                for (String sa[] : z) {
                                    xList1.AddItem(sa[0].trim(), "ADDT",  new SettingsTextBoxElement(sa[1].trim(), sa[0].trim().replace("_", " "), false));
                                }
                            }
                        }
                    }

                    xList1.Commit();
                    JNekoProgressDisplayClass.Off();
                });
                t.start();
                break;
            case 1:
                Thread tx = new Thread(() -> {
                    JNekoProgressDisplayClass.On();
                
                    xList1.AddGroupHeader("ADDT", ColorForBG.darker(), "ONT user options (read only)", groupIcon, 0);
                    xList1.setBackgroundColor(ColorForBG);
                    xList1.VPBON();
                    xList1.Commit();
                
                    String z[][] = MA4000Wrappers.GetONTACSAndUserConfig(__ONT, 1);
                    if (z != null) {
                        for (String sa[] : z) {
                            xList1.AddItem(sa[0].trim(), "ADDT",  new SettingsTextBoxElement(sa[1].trim(), sa[0].trim().replace("_", " "), false));
                        }
                        xList1.Commit();
                        JNekoProgressDisplayClass.Off();
                    }
                });
                tx.start();
                break;
        }
    }
    
    public static void Show(String __ONT, int type) {
        final ONTInfo jn = new ONTInfo(__ONT, type);
        jn.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        xList1 = new xlist.XList();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new-logo-left.png"))); // NOI18N

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new-logo-right.png"))); // NOI18N

        jProgressBar1.setString("Please, wait...");
        jProgressBar1.setStringPainted(true);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 110, Short.MAX_VALUE)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1)
            .addComponent(jLabel2)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xList1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xList1, javax.swing.GroupLayout.DEFAULT_SIZE, 596, Short.MAX_VALUE)
                .addContainerGap())
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
    private javax.swing.JProgressBar jProgressBar1;
    private xlist.XList xList1;
    // End of variables declaration//GEN-END:variables
}
