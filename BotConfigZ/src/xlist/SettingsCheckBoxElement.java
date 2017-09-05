package xlist;

import botconfigz.JNekoSQLite;

public class SettingsCheckBoxElement extends javax.swing.JPanel {
    public SettingsCheckBoxElement(String title) {
        initComponents();
        jCheckBox1.setText(title);
    }
    
    public SettingsCheckBoxElement(String title, boolean val) {
        initComponents();
        jCheckBox1.setSelected(val);
        jCheckBox1.setText(title);
    }
    
    public SettingsCheckBoxElement(String title, String ID, boolean RegAL) {
        initComponents();
        jCheckBox1.setText(title);
        if (RegAL) JNekoSQLite.APPSettings_RegisterActionListener(jCheckBox1, ID);
    }

    public boolean GetValue() {
        return jCheckBox1.isSelected();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCheckBox1 = new javax.swing.JCheckBox();

        setBackground(new java.awt.Color(51, 51, 51));

        jCheckBox1.setBackground(new java.awt.Color(51, 51, 51));
        jCheckBox1.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBox1.setText("check 001");
        jCheckBox1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jCheckBox1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jCheckBox1.setOpaque(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox1, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBox1;
    // End of variables declaration//GEN-END:variables
}
