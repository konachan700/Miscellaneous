package xlist;

import java.awt.Color;
import javax.swing.Icon;

public class SettingsNumberBoxElement extends javax.swing.JPanel {
    private final Icon
            OK_ICON = new javax.swing.ImageIcon(getClass().getResource("/icons16/MsgBox_Element_YES_OK.png")),
            ERR_ICON = new javax.swing.ImageIcon(getClass().getResource("/icons16/MsgBox_Element_NO.png"));

    private long min=0, max=65535;
    
    public void SetLongValue(long val) {
        jTextField1.setText(val + "");
    }
    
    public String GetTextValue() {
        if (IsValid())
            return  jTextField1.getText().trim();
        else 
            return null;
    }
    
    public long GetLongValue() {
        if (IsValid())
            return Long.parseLong(jTextField1.getText().trim(), 10);
        else
            return -1;
    }

    public SettingsNumberBoxElement(String title, long value) {
        initComponents();
        jLabel1.setText(title); 
        jTextField1.setText(String.valueOf(value)); 
    }
    
    public SettingsNumberBoxElement(String title, long value, long _min, long _max) {
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

        setBackground(new java.awt.Color(51, 51, 51));

        jLabel1.setFont(new java.awt.Font("Liberation Sans", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Number");

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

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/MsgBox_Element_YES_OK.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
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
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
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
            final int i = Integer.parseInt(s, 10);
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
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
