package xlist;

//import connections.JNekoSP;
//import java.util.ArrayList;
import java.util.Map;
//import dbengine.sMySQLWarappers;

public final class SettingsComboBoxElement extends javax.swing.JPanel {  
   // ArrayList<Map<String,String>> sqlArray = null;
    ///private final String DID;
    private final Map<String,String> dataCollection;
//    private Map<String,Integer> dataIndexes;
    private final XListButtonsActionListener xli;
            
    public String GetValue() {
        return (String)(jComboBox1.getSelectedItem());
    }
    
    public String GetAssocValue() {
        return dataCollection.get(GetValue());
    }
    
    public int GetIndex() {
        return jComboBox1.getSelectedIndex();
    }
    
    public void ResetSelection() {
        jComboBox1.setSelectedIndex(0);
    }
    
    public void RemoveItemByAV(String av) {
        String a = null;
        for (String s : dataCollection.keySet()) {
            if (dataCollection.get(s).equalsIgnoreCase(av)) {
                a = s;
                break;
            }
        }
        
        if (a != null) {
            dataCollection.remove(a);
            jComboBox1.removeAllItems();
            jComboBox1.addItem("--не выбрано--"); 

            for (String datax : dataCollection.keySet()) {
                jComboBox1.addItem(datax);
            }
        }
    }
    
//    public String GetSelectedDID() {
//        return GetSelectedDID(0);
//    }
    
//    public String GetSelectedDID(int DIDPosition) {
//        if (this.GetIndex() <= 0) return null;
//        return (sqlArray != null) ? sqlArray.get(this.GetIndex()-1).get("did") : null;
//    }
    
//    public void SetSelectedIndex(int index) {
//        if ((index <= 0) || (index >= jComboBox1.getItemCount())) return;
//        jComboBox1.setSelectedIndex(index); 
//    }
//    
//    public void SetSelectedIndex(String elementName) {
//        String selItem = jComboBox1.getSelectedItem().toString();
//        
//        
//        if ((index <= 0) || (index >= jComboBox1.getItemCount())) return;
//        jComboBox1.setSelectedIndex(index); 
//    }
    
//    public void SetSelectedIndexByDID(String DID) {
//        if (sqlArray == null) return;
//        for (int i=0; i<sqlArray.size(); i++) {
//            if (DID.contentEquals(sqlArray.get(i).get("did"))) { 
//                jComboBox1.setSelectedIndex(i+1);
//                return;
//            }
//        }
//    }
    
//    public SettingsComboBoxElement(String _DID, String title) {
//        initComponents();
//        jLabel1.setText(title);
//        jComboBox1.removeAllItems();
//        jComboBox1.addItem("--не выбрано--"); 
//    }
    
    public SettingsComboBoxElement(String title, Map<String,String> data, int selectedIndex) {
        initComponents();
        
        dataCollection = data;
        jButton1.setVisible(false);

        jLabel1.setText(title);
        jComboBox1.removeAllItems();
        jComboBox1.addItem("--не выбрано--"); 
        
        for (String datax : data.keySet()) {
            jComboBox1.addItem(datax);
        }
        
        xli = null;
    }
    
    public SettingsComboBoxElement(String title, Map<String,String> data, int selectedIndex, XListButtonsActionListener li) {
        initComponents();
        
        dataCollection = data;
        xli = li;

        jLabel1.setText(title);
        jComboBox1.removeAllItems();
        jComboBox1.addItem("--не выбрано--"); 
        
        for (String datax : data.keySet()) {
            jComboBox1.addItem(datax);
        }
    }
    
    public Map<String,String> GetCollection() {
        return dataCollection;
    }
    
    
    
//    public SettingsComboBoxElement(String _DID, String title, String tableName, String colName) {
//        initComponents();
//        //DID = _DID;
//        jLabel1.setText(title);
//        jComboBox1.removeAllItems();
//        jComboBox1.addItem("--не выбрано--"); 
//        FillFromSQL(tableName, colName);
//        if (_DID != null) SetSelectedIndexByDID(_DID);
//    }
    
//    public void FillFromArray(String arr[]) {
//        for (String s : arr) {
//            jComboBox1.addItem(s);
//        }
//    }
//    
//    public void FillFromArray(ArrayList<String> arr) {
//        for (String s : arr) {
//            jComboBox1.addItem(s);
//        }
//    }
    
//    public void FillFromSQL(String tableName, String colName) {
//        sqlArray = JNekoSP.GetSP().SQLSelect("SELECT * FROM "+tableName+" ORDER BY did ASC;");
//        if (sqlArray != null) {
//            for (Map<String,String> x : sqlArray) {
//                jComboBox1.addItem(x.get(colName));
//            }
//        }
//    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();

        setBackground(new java.awt.Color(51, 51, 51));

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Option text");
        jLabel1.setMaximumSize(new java.awt.Dimension(57, 15));
        jLabel1.setMinimumSize(new java.awt.Dimension(57, 15));
        jLabel1.setPreferredSize(new java.awt.Dimension(57, 15));

        jButton1.setBackground(new java.awt.Color(0, 0, 0));
        jButton1.setForeground(new java.awt.Color(153, 255, 0));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons16/category.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBox1, 0, 129, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (xli != null) 
            xli.OnButtonClick("", "", XList.COMBO_BOX_BTN1); 
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
