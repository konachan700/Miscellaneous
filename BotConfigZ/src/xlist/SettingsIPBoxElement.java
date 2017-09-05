package xlist;

import botconfigz.JNekoSQLite;
import java.awt.Color;
import javax.swing.Icon;

public class SettingsIPBoxElement extends javax.swing.JPanel {
//    private boolean RegAL = true;
    private final Icon
            OK_ICON = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/MsgBox_Element_YES_OK.png")),
            ERR_ICON = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/MsgBox_Element_NO.png"));
    
//    public SettingsIPBoxElement(String defaultText, String title) {
////        RegAL = regAL;
//        initComponents();
//        jLabel1.setText(title); 
//        jTextField1.setText(defaultText);
//    }
    
    public String GetTextValue() {
        if (IsValid())
            return  jTextField1.getText().trim() + "." + 
                    jTextField2.getText().trim() + "." + 
                    jTextField3.getText().trim() + "." + 
                    jTextField4.getText().trim();
        else 
            return null;
    }
    
    public long GetLongValue() {
        if (IsValid())
            return IPToLong(GetTextValue());
        else
            return 0;
    }
    
    private long IPToLong(String ip) {
        String ipa[] = ip.trim().split("\\.");
        if (ipa.length < 4) return -1;
        
        long ippart = 0, ipres = 0;
        for (int i=0; i<4; i++) {
            ippart = Long.parseLong(ipa[i], 10);
            ipres += ippart * Math.pow(256, 3 - i);
        }
        
        return ipres;
    }
    
    public SettingsIPBoxElement(String title, String ip) {
        initComponents();
        jLabel1.setText(title); 
        if (ip != null) {
            final String[] ipx = ip.split("");
            if (ipx.length > 3) {
                jTextField1.setText(ipx[0]);
                jTextField2.setText(ipx[1]);
                jTextField3.setText(ipx[2]);
                jTextField4.setText(ipx[3]);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(51, 51, 51));

        jLabel1.setFont(new java.awt.Font("Liberation Sans", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("IP address");

        jTextField1.setBackground(new java.awt.Color(254, 254, 254));
        jTextField1.setFont(new java.awt.Font("Liberation Mono", 1, 12)); // NOI18N
        jTextField1.setForeground(new java.awt.Color(1, 1, 1));
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField1.setText("255");
        jTextField1.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jTextField1CaretUpdate(evt);
            }
        });

        jTextField2.setBackground(new java.awt.Color(254, 254, 254));
        jTextField2.setFont(new java.awt.Font("Liberation Mono", 1, 12)); // NOI18N
        jTextField2.setForeground(new java.awt.Color(1, 1, 1));
        jTextField2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField2.setText("255");
        jTextField2.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jTextField2CaretUpdate(evt);
            }
        });

        jTextField3.setBackground(new java.awt.Color(254, 254, 254));
        jTextField3.setFont(new java.awt.Font("Liberation Mono", 1, 12)); // NOI18N
        jTextField3.setForeground(new java.awt.Color(1, 1, 1));
        jTextField3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField3.setText("255");
        jTextField3.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jTextField3CaretUpdate(evt);
            }
        });

        jTextField4.setBackground(new java.awt.Color(254, 254, 254));
        jTextField4.setFont(new java.awt.Font("Liberation Mono", 1, 12)); // NOI18N
        jTextField4.setForeground(new java.awt.Color(1, 1, 1));
        jTextField4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField4.setText("255");
        jTextField4.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jTextField4CaretUpdate(evt);
            }
        });

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/MsgBox_Element_YES_OK.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 90, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    public boolean IsValid() {
        final boolean x = IsValid(jTextField1) && IsValid(jTextField2) && IsValid(jTextField3) && IsValid(jTextField4);
        jLabel2.setIcon((x) ? OK_ICON : ERR_ICON);
        return x;
    }
    
    private boolean IsValid(javax.swing.JTextField t) {
        final String s = t.getText().trim(); //.replaceAll("[^0-9]{1,5}", "");
        try {
            final int i = Integer.parseInt(s, 10);
            if ((i > 255) || (i < 0)) {
                t.setForeground(Color.red);
//                jLabel2.setIcon(ERR_ICON);
                return false;
            } else {
                t.setForeground(Color.BLACK);
//                jLabel2.setIcon(OK_ICON);
                return true;
            }
        } catch (Exception e) {
            t.setForeground(Color.red);
//            jLabel2.setIcon(ERR_ICON);
            return false;
        }
    }
    
    private void jTextField1CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTextField1CaretUpdate
        IsValid(jTextField1);
    }//GEN-LAST:event_jTextField1CaretUpdate

    private void jTextField2CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTextField2CaretUpdate
        IsValid(jTextField2);
    }//GEN-LAST:event_jTextField2CaretUpdate

    private void jTextField3CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTextField3CaretUpdate
        IsValid(jTextField3);
    }//GEN-LAST:event_jTextField3CaretUpdate

    private void jTextField4CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTextField4CaretUpdate
        IsValid(jTextField4); 
    }//GEN-LAST:event_jTextField4CaretUpdate

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    // End of variables declaration//GEN-END:variables
}
