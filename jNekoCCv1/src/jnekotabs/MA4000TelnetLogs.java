package jnekotabs;

import javax.swing.JScrollBar;
import javax.swing.text.BadLocationException;

public class MA4000TelnetLogs extends javax.swing.JPanel {
    public MA4000TelnetLogs() {
        initComponents();
    }
    
    public void L(String s) {
        jTextArea1.append(s);
        
        final JScrollBar js = jScrollPane1.getVerticalScrollBar();
        js.setValue(js.getMaximum());
        
        final int lineCount = jTextArea1.getLineCount();
        if (lineCount > 3000) {
            try {
                final int offset1 = jTextArea1.getLineEndOffset(lineCount - 3000);
                jTextArea1.setText(jTextArea1.getText().substring(offset1));
            } catch (BadLocationException ex) { }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setBackground(new java.awt.Color(51, 51, 0));

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(102, 102, 0), new java.awt.Color(102, 102, 0)));

        jTextArea1.setBackground(new java.awt.Color(51, 51, 0));
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Monospaced", 0, 10)); // NOI18N
        jTextArea1.setForeground(new java.awt.Color(255, 255, 102));
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
