package jnekotabs;

import connections.JNekoSP;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import javax.swing.Timer;

public class TrafStatDisplayCurrent extends javax.swing.JPanel {
    private Map<String, ArrayDeque<Long>> CurrTraf = null;
    private ArrayList<String> IfList = null;
    private String currIf = "";
    
    @SuppressWarnings("Convert2Lambda")
    private final ActionListener TrafTimerListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            __add_last();
        }
    };
    private final Timer StateTimer = new Timer(1000, TrafTimerListener);
    
    private long[] __delta(ArrayDeque<Long> in) {
        long
                lastVal = 0,
                currentVal = 0;
        int
                counter = 0;
        long[]
                retval = new long[in.size()];
        
        for (Long val : in) {
            currentVal = val.longValue();
            if (counter > 0) {
                retval[counter - 1] = lastVal - currentVal;
            }
            lastVal = currentVal;
            counter++;
        }
        return retval;
    }
    
    private void __add_last() {
        Map<String, Long> currVal = (Map<String, Long>) JNekoSP.GetSP().CryptReadObject(60);
        if (currVal != null) {
            Set<String> xx = currVal.keySet();
            for (String x : xx) {
                Long l = currVal.get(x);
                CurrTraf.get(x).addFirst(l);
                CurrTraf.get(x).removeLast();
                xDisplay1.setDeltaArrayTx(__delta(CurrTraf.get(currIf + ":TX"))); 
                xDisplay1.setDeltaArrayRx(__delta(CurrTraf.get(currIf + ":RX"))); 
                xDisplay1.repaint();
            }
        }
    }
    
    private void __restart() {
        if (StateTimer.isRunning()) { 
            StateTimer.stop();
        }
        
        CurrTraf = (Map<String, ArrayDeque<Long>>) JNekoSP.GetSP().CryptReadObject(59);
        IfList   = (ArrayList<String>)             JNekoSP.GetSP().CryptReadObject(58);
        if ((CurrTraf != null) && (IfList != null)) {
            jComboBox1.removeAllItems();
            for (String key : IfList) {
                jComboBox1.addItem(key); 
            }
            currIf = jComboBox1.getSelectedItem().toString();
            
            xDisplay1.setDeltaArrayTx(__delta(CurrTraf.get(currIf + ":TX"))); 
            xDisplay1.setDeltaArrayRx(__delta(CurrTraf.get(currIf + ":RX"))); 
            xDisplay1.repaint();
            
            StateTimer.start();
        }
    }
    
    public TrafStatDisplayCurrent(Color c) {
        initComponents();
        this.setBackground(c);
        jPanel1.setBackground(c);
        
        __restart();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        xDisplay1 = new xgraphics.XDisplay();
        jComboBox1 = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();

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
            .addGap(0, 337, Short.MAX_VALUE)
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

        jComboBox1.setBackground(new java.awt.Color(254, 254, 254));
        jComboBox1.setForeground(new java.awt.Color(1, 1, 1));
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(4, 8, 2));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/view-refresh_1.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 511, Short.MAX_VALUE)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        if (StateTimer.isRunning()) currIf = jComboBox1.getSelectedItem().toString();
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden

    }//GEN-LAST:event_formComponentHidden

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        __restart();
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JPanel jPanel1;
    private xgraphics.XDisplay xDisplay1;
    // End of variables declaration//GEN-END:variables
}
