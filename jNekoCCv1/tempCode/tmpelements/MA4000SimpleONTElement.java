package xlistcustomelements;

import java.awt.Color;
import xlist.XListButtonsActionListener;

public class MA4000SimpleONTElement extends javax.swing.JPanel {
    private final XListButtonsActionListener xli;
    private final Color unColor = new Color(255, 210, 210);
    private String SLOT = null;
    
    public MA4000SimpleONTElement(String title, String text, String Slot, boolean delVisible, XListButtonsActionListener li) {
        initComponents();
        jLabel1.setText(title.trim());
        jLabel2.setText((Slot!=null) ? text : "Новая или неисправная ONT   ");
        xli = li;
        jButton1.setVisible((Slot!=null) & delVisible);
        jButton2.setVisible(Slot!=null);
        jButton3.setVisible(Slot!=null);
        SLOT = Slot;
    }
    
    public MA4000SimpleONTElement(String title, String text, boolean IsOK, boolean delVisible, XListButtonsActionListener li) {
        initComponents();
        jLabel1.setText(title.trim());
        jLabel2.setText((IsOK) ? text : "Новая или неисправная ONT   ");
        if (IsOK) jLabel2.setForeground(unColor); 
        xli = li;
        jButton1.setVisible(IsOK & delVisible);
        jButton2.setVisible(IsOK);
        jButton3.setVisible(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();

        setBackground(new java.awt.Color(51, 51, 51));

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/object-ungroup.png"))); // NOI18N
        jLabel1.setText("jLabel1");

        jButton1.setBackground(new java.awt.Color(51, 0, 0));
        jButton1.setForeground(new java.awt.Color(51, 0, 0));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/application-exit.png"))); // NOI18N
        jButton1.setToolTipText("Удалить");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(0, 51, 0));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/documentinfo.png"))); // NOI18N
        jButton2.setToolTipText("Показать детальную информацию");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Liberation Sans", 1, 10)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(204, 255, 153));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("jLabel2");

        jButton3.setBackground(new java.awt.Color(0, 0, 51));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/icon_payments.png"))); // NOI18N
        jButton3.setToolTipText("Сбросить настройки до дефолтных");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        xli.OnButtonClick(SLOT, jLabel1.getText(), 4001);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        xli.OnButtonClick(SLOT, jLabel1.getText(), 4002);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        xli.OnButtonClick(SLOT, jLabel1.getText(), 4003);
    }//GEN-LAST:event_jButton3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
