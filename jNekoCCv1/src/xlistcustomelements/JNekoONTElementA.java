package xlistcustomelements;

import java.awt.Color;

public class JNekoONTElementA extends javax.swing.JPanel {
    public static final int
            BUTTON_ONT_INFO         = 0,
            BUTTON_ONT_DELETE       = 1,
            BUTTON_ONT_SET_FOR_USER = 2,
            BUTTON_ONT_RESET        = 3,
            BUTTON_ONT_DEL_FOR_USER = 4;
    private final Color 
            green_color = new Color(100, 255, 100),
            red_color = new Color(255, 100, 100);
    
    private JNekoONTElementAListener listener;
    private String ONT, SLOT, MAC, IP, USER, SYSUSER;
    
    public JNekoONTElementA(String ont, String slot, String ip, String mac, String user, String sysuser, JNekoONTElementAListener li) {
        initComponents();
        jTextField1.setText(ont);
        jTextField2.setText("Slot #"+slot);
        jTextField3.setText(mac);
        jTextField4.setText(ip);
        jTextField5.setText(user);
        jTextField6.setText(sysuser);
        
        // Этот костыль перекрашивает нечетный мак в красный, потому как на оборудовании ELTEX маки veip0.1 вроде как всегда четные, 
        // а вот если вылезает нечетный, возникают странные глюки (в какой-то момент мак становится четным, уже на постоянно). 
        // Баг это или фича, пока не знаю, у производителя незадокументирована сия шняга. Будет время - напишу им в багтрекер вопрос.
        // Для использования с другим оборудованием блок стоит убрать.
        final String[] macN = new String[] {"1", "3", "5", "7", "9", "B", "D", "F"};
        for (String macS : macN) {
            if (mac.trim().toUpperCase().endsWith(macS)) jTextField3.setForeground(red_color);
        }
        
        listener = li;
        SLOT = slot;
        MAC = mac;
        ONT = ont;
        IP = ip;
        USER = user;
        SYSUSER = sysuser;
        
        if (user.trim().length() > 1) jTextField4.setForeground(green_color);
        jButton5.setVisible(user.trim().length() > 1);
        jButton3.setVisible((ip.trim().length() > 1) && (user.trim().length() <= 1));
       
        if (jTextField1 != null) jTextField1.setBackground(this.getBackground());
        if (jTextField2 != null) jTextField2.setBackground(this.getBackground());
        if (jTextField3 != null) jTextField3.setBackground(this.getBackground());
        if (jTextField4 != null) jTextField4.setBackground(this.getBackground());
        if (jTextField5 != null) jTextField5.setBackground(this.getBackground());
        if (jTextField6 != null) jTextField6.setBackground(this.getBackground());
    }
    
    @Override
    public void setBackground(Color c) {
        super.setBackground(c);
        if (jTextField1 != null) jTextField1.setBackground(c);
        if (jTextField2 != null) jTextField2.setBackground(c);
        if (jTextField3 != null) jTextField3.setBackground(c);
        if (jTextField4 != null) jTextField4.setBackground(c);
        if (jTextField5 != null) jTextField5.setBackground(c);
        if (jTextField6 != null) jTextField6.setBackground(c);
    }
    
    public String GetOnt() {
        return jTextField1.getText();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();

        setBackground(new java.awt.Color(51, 51, 51));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_32x32/Globe.png"))); // NOI18N

        jTextField1.setEditable(false);
        jTextField1.setBackground(new java.awt.Color(36, 31, 31));
        jTextField1.setFont(new java.awt.Font("Liberation Sans", 1, 11)); // NOI18N
        jTextField1.setForeground(new java.awt.Color(254, 254, 254));
        jTextField1.setText("ELTX88998899");
        jTextField1.setBorder(null);
        jTextField1.setDoubleBuffered(true);

        jTextField2.setEditable(false);
        jTextField2.setBackground(new java.awt.Color(36, 31, 31));
        jTextField2.setFont(new java.awt.Font("Liberation Sans", 0, 11)); // NOI18N
        jTextField2.setForeground(new java.awt.Color(166, 166, 166));
        jTextField2.setText("Slot: 1");
        jTextField2.setBorder(null);
        jTextField2.setDoubleBuffered(true);

        jTextField3.setEditable(false);
        jTextField3.setBackground(new java.awt.Color(1, 1, 1));
        jTextField3.setFont(new java.awt.Font("Liberation Sans", 0, 11)); // NOI18N
        jTextField3.setForeground(new java.awt.Color(166, 166, 166));
        jTextField3.setText("a8:f9:4b:6a:ed:0e");
        jTextField3.setBorder(null);
        jTextField3.setDoubleBuffered(true);

        jTextField4.setEditable(false);
        jTextField4.setBackground(new java.awt.Color(36, 31, 31));
        jTextField4.setFont(new java.awt.Font("Liberation Sans", 1, 11)); // NOI18N
        jTextField4.setForeground(new java.awt.Color(255, 244, 0));
        jTextField4.setText("255.255.255.255");
        jTextField4.setBorder(null);
        jTextField4.setDoubleBuffered(true);

        jButton1.setBackground(new java.awt.Color(1, 1, 1));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/ONTElementA_Delete.png"))); // NOI18N
        jButton1.setToolTipText("Удалить устройство");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(1, 1, 1));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/ONTElementA_Default.png"))); // NOI18N
        jButton2.setToolTipText("Сбросить настройки до заводских");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(1, 1, 1));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/ONTElementA_ConnectToUser.png"))); // NOI18N
        jButton3.setToolTipText("Прикрепить к пользователю");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(1, 1, 1));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/ONTElementA_Info.png"))); // NOI18N
        jButton4.setToolTipText("Просмотр и изменение информации");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(1, 1, 1));
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/ONTElementA_Disconnect.png"))); // NOI18N
        jButton5.setToolTipText("Открепить от пользователя");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jTextField5.setEditable(false);
        jTextField5.setBackground(new java.awt.Color(36, 31, 31));
        jTextField5.setFont(new java.awt.Font("Liberation Sans", 0, 11)); // NOI18N
        jTextField5.setForeground(new java.awt.Color(166, 166, 166));
        jTextField5.setText("Иванов Иван Иванович");
        jTextField5.setBorder(null);
        jTextField5.setDoubleBuffered(true);

        jTextField6.setEditable(false);
        jTextField6.setBackground(new java.awt.Color(36, 31, 31));
        jTextField6.setFont(new java.awt.Font("Liberation Sans", 0, 11)); // NOI18N
        jTextField6.setForeground(new java.awt.Color(166, 166, 166));
        jTextField6.setBorder(null);
        jTextField6.setDoubleBuffered(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(jTextField4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField5, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                    .addComponent(jTextField6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        listener.OnButtonClick(BUTTON_ONT_INFO, IP, MAC, ONT, SLOT, USER, SYSUSER);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        listener.OnButtonClick(BUTTON_ONT_SET_FOR_USER, IP, MAC, ONT, SLOT, USER, SYSUSER);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        listener.OnButtonClick(BUTTON_ONT_RESET, IP, MAC, ONT, SLOT, USER, SYSUSER);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        listener.OnButtonClick(BUTTON_ONT_DELETE, IP, MAC, ONT, SLOT, USER, SYSUSER);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        listener.OnButtonClick(BUTTON_ONT_DEL_FOR_USER, IP, MAC, ONT, SLOT, USER, SYSUSER);
    }//GEN-LAST:event_jButton5ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    // End of variables declaration//GEN-END:variables
}
