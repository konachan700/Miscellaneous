package xlist;

import java.awt.Color;
import javax.swing.Icon;

public class SettingsHexNumberBoxElement extends javax.swing.JPanel {
    private final Icon
            OK_ICON = new javax.swing.ImageIcon(getClass().getResource("/icons16/MsgBox_Element_YES_OK.png")),
            ERR_ICON = new javax.swing.ImageIcon(getClass().getResource("/icons16/MsgBox_Element_NO.png"));

    private int min=0, max=65535;
    
    public void SetIntValue(int val) {
        jTextField1.setText(Integer.toHexString(val).toUpperCase());
    }
    
    public String GetTextValue() {
        if (IsValid())
            return  jTextField1.getText().trim().toUpperCase();
        else 
            return null;
    }
    
    public int GetValue() {
        if (IsValid())
            return Integer.parseInt(jTextField1.getText().trim(), 16);
        else
            return -1;
    }

    public SettingsHexNumberBoxElement(String title, int value) {
        initComponents();
        jLabel1.setText(title); 
        jTextField1.setText(String.valueOf(value)); 
    }
    
    public SettingsHexNumberBoxElement(String title, int value, int _min, int _max) {
        initComponents();
        jLabel1.setText(title); 
        jTextField1.setText(String.valueOf(value)); 
        max = _max;
        min = _min;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(51, 51, 51));

        jLabel1.setFont(new java.awt.Font("Liberation Sans", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("HexNumber");

        jTextField1.setBackground(new java.awt.Color(254, 254, 254));
        jTextField1.setFont(new java.awt.Font("Liberation Mono", 1, 12)); // NOI18N
        jTextField1.setForeground(new java.awt.Color(1, 1, 1));
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField1.setText("FF");
        jTextField1.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jTextField1CaretUpdate(evt);
            }
        });

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/MsgBox_Element_YES_OK.png"))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 0));
        jLabel3.setText("0x");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    public boolean IsValid() {
        final boolean x = IsValid(jTextField1);
        jLabel2.setIcon((x) ? OK_ICON : ERR_ICON);
        return x;
    }
    
    private boolean IsValid(javax.swing.JTextField t) {
        final String s = t.getText().trim();
        try {
            final int i = Integer.parseInt(s, 16);
            if ((i < min) || (i > max)) {
                t.setForeground(Color.red);
                jLabel2.setIcon(ERR_ICON);
                return false;
            }
            t.setToolTipText("value="+i);
            t.setForeground(Color.BLACK);
            jLabel2.setIcon(OK_ICON);
            return true;
        } catch (Exception e) {
            t.setForeground(Color.red);
            jLabel2.setIcon(ERR_ICON);
            return false;
        }
    }
    
    private void jTextField1CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTextField1CaretUpdate
        IsValid(jTextField1);
    }//GEN-LAST:event_jTextField1CaretUpdate

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
