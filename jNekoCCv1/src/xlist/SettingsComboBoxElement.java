package xlist;

import connections.JNekoSP;
import java.util.ArrayList;
import java.util.Map;
//import dbengine.sMySQLWarappers;

public final class SettingsComboBoxElement extends javax.swing.JPanel {  
    ArrayList<Map<String,String>> sqlArray = null;
    ///private final String DID;
            
    public String GetValue() {
        return (String)(jComboBox1.getSelectedItem());
    }
    
    public int GetIndex() {
        return jComboBox1.getSelectedIndex();
    }
    
    public String GetSelectedDID() {
        return GetSelectedDID(0);
    }
    
    public String GetSelectedDID(int DIDPosition) {
        if (this.GetIndex() <= 0) return null;
        return (sqlArray != null) ? sqlArray.get(this.GetIndex()-1).get("did") : null;
    }
    
    public void SetSelectedIndex(int index) {
        if ((index <= 0) || (index >= jComboBox1.getItemCount())) return;
        jComboBox1.setSelectedIndex(index); 
    }
    
    public void SetSelectedIndexByDID(String DID) {
        if (sqlArray == null) return;
        for (int i=0; i<sqlArray.size(); i++) {
            if (DID.contentEquals(sqlArray.get(i).get("did"))) { 
                jComboBox1.setSelectedIndex(i+1);
                return;
            }
        }
    }
    
    public SettingsComboBoxElement(String _DID, String title) {
        initComponents();
        //DID = _DID;
        jLabel1.setText(title);
        jComboBox1.removeAllItems();
        jComboBox1.addItem("--не выбрано--"); 
    }
    
    public SettingsComboBoxElement(String _DID, String title, String tableName, String colName) {
        initComponents();
        //DID = _DID;
        jLabel1.setText(title);
        jComboBox1.removeAllItems();
        jComboBox1.addItem("--не выбрано--"); 
        FillFromSQL(tableName, colName);
        if (_DID != null) SetSelectedIndexByDID(_DID);
    }
    
    public void FillFromArray(String arr[]) {
        for (String s : arr) {
            jComboBox1.addItem(s);
        }
    }
    
    public void FillFromArray(ArrayList<String> arr) {
        for (String s : arr) {
            jComboBox1.addItem(s);
        }
    }
    
    public void FillFromSQL(String tableName, String colName) {
        sqlArray = JNekoSP.GetSP().SQLSelect("SELECT * FROM "+tableName+" ORDER BY did ASC;");
        if (sqlArray != null) {
            for (Map<String,String> x : sqlArray) {
                jComboBox1.addItem(x.get(colName));
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();

        setBackground(new java.awt.Color(51, 51, 51));

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Option text");
        jLabel1.setMaximumSize(new java.awt.Dimension(57, 15));
        jLabel1.setMinimumSize(new java.awt.Dimension(57, 15));
        jLabel1.setPreferredSize(new java.awt.Dimension(57, 15));

        jComboBox1.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, 0, 202, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
