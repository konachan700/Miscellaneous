package jnekotabs;

import connections.JNekoSP;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TrafStatDisplayDaily extends javax.swing.JPanel {
    private ArrayList<String> 
            IfList = null;
    private String 
            currIf = "";
    private boolean loaded = false;
    
    private void __reloadIFs() {
        loaded = false;
        
        IfList = (ArrayList<String>) JNekoSP.GetSP().CryptReadObject(58);
        if (IfList != null) {
            jComboBox1.removeAllItems();
            for (String key : IfList) {
                jComboBox1.addItem(key); 
            }
            currIf = jComboBox1.getSelectedItem().toString();
        }  
        
        loaded = true;
    }
    
    private void __reloadData() {
        if (currIf.length() <= 1) return;
        
        final ArrayList<Map<String,String>> x = JNekoSP.GetSP().SQLSelect("SELECT * FROM TrafStatM WHERE ifaceID=\"" + currIf + "\" ORDER BY did DESC LIMIT 0,1440;");
        final long[] 
                tx = new long[x.size()],
                rx = new long[x.size()];
        int counter = 0;
        
        if (x != null) {
            for (Map<String,String> xx : x) {
                tx[counter] = Long.parseLong(xx.get("transmit"), 10) / 60;
                rx[counter] = Long.parseLong(xx.get("receive"), 10) / 60;
                counter++;
            }
        }
        
        xDisplay1.setDeltaArrayTx(tx);
        xDisplay1.setDeltaArrayRx(rx);
        xDisplay1.repaint();
    }
    
    public TrafStatDisplayDaily(Color c) {
        initComponents();
        this.setBackground(c);
        jPanel1.setBackground(c);
        xDisplay1.DisableTextInfo();
        
        __reloadIFs();
        __reloadData();
        
        loaded = true;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        xDisplay1 = new xgraphics.XDisplay();
        jComboBox1 = new javax.swing.JComboBox();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        javax.swing.GroupLayout xDisplay1Layout = new javax.swing.GroupLayout(xDisplay1);
        xDisplay1.setLayout(xDisplay1Layout);
        xDisplay1Layout.setHorizontalGroup(
            xDisplay1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        xDisplay1Layout.setVerticalGroup(
            xDisplay1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 344, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xDisplay1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xDisplay1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(549, Short.MAX_VALUE)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        if (loaded) {
            currIf = jComboBox1.getSelectedItem().toString();
            __reloadData();
        }
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden

    }//GEN-LAST:event_formComponentHidden

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JPanel jPanel1;
    private xgraphics.XDisplay xDisplay1;
    // End of variables declaration//GEN-END:variables
}
