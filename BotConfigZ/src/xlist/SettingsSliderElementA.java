package xlist;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class SettingsSliderElementA extends javax.swing.JPanel {
    private int min=0, max=65535, init=0, xaddr=8;
    private final XListSliderElementActionListener li;
    private final String ID, GID, TYPE;
    private boolean isAction = false;
    
    private final ActionListener alt = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            if (isAction)
                if (li != null) li.OnStateChange(ID, GID, xaddr, min, max, init, TYPE, jSlider1.getValue());
            isAction = false;
        }
    };
    
    private final Timer tmr = new Timer(100, alt);
    
    public void SetValue(int val) {
        jTextField1.setText(val + "");
    }
    
    public String GetTextValue() {
        return  jTextField1.getText().trim();
    }
    
    public int GetValue() {
        return Integer.parseInt(jTextField1.getText().trim(), 10);
    }

    public SettingsSliderElementA(String _ID, String _GID, String title, int _addr, int value, int _min, int _max, String type, XListSliderElementActionListener _li) {
        initComponents();
        ID = _ID;
        GID = _GID;
        TYPE = type;
        xaddr = _addr;
        jLabel1.setText(title); 
        jTextField1.setText(String.valueOf(value)); 
        max = _max;
        min = _min;
        init = value;
        jSlider1.setMaximum(max);
        jSlider1.setMinimum(min);
        jSlider1.setValue(value);
        li = _li;
        tmr.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jSlider1 = new javax.swing.JSlider();

        setBackground(new java.awt.Color(51, 51, 51));

        jLabel1.setFont(new java.awt.Font("Liberation Sans", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Number");

        jTextField1.setEditable(false);
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

        jButton1.setBackground(new java.awt.Color(0, 0, 0));
        jButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton1.setForeground(new java.awt.Color(153, 255, 153));
        jButton1.setText("+1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(0, 0, 0));
        jButton2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 204, 204));
        jButton2.setText("-1");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jSlider1.setBackground(new java.awt.Color(0, 0, 0));
        jSlider1.setForeground(new java.awt.Color(255, 255, 204));
        jSlider1.setOpaque(false);
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
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
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton2)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void jTextField1CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTextField1CaretUpdate

    }//GEN-LAST:event_jTextField1CaretUpdate

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int a = jSlider1.getValue();
        if (a >= min) {
            jSlider1.setValue(a-1);
            jTextField1.setText(jSlider1.getValue()+"");
            if (li != null) li.OnStateChange(ID, GID, xaddr, min, max, init, TYPE, jSlider1.getValue());
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int a = jSlider1.getValue();
        if (a <= max) {
            jSlider1.setValue(a+1);
            jTextField1.setText(jSlider1.getValue()+"");
            if (li != null) li.OnStateChange(ID, GID, xaddr, min, max, init, TYPE, jSlider1.getValue());
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /* Такой подход нужен, чтобы не дергать событие изменения значения каждый раз, когда перетягиваем мышкой слайдер. 
        Событие вполне достаточно ловить 3раза в секунду, а то и реже */ 
    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
        jTextField1.setText(jSlider1.getValue()+"");
        isAction = true;
        //System.out.print(" ::addr="+xaddr+":: ");
        //if (li != null) li.OnStateChange(ID, GID, xaddr, min, max, init, TYPE, jSlider1.getValue());
    }//GEN-LAST:event_jSlider1StateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
