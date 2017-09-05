package xlist;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

public class GlobalMainMenuElement extends javax.swing.JPanel {
    private XListElementActionListener xlistener = null;
    private final String GID, ID;
    private final Map<String, Object> addObj = new HashMap<>();
    private Color normal = new Color(255, 255, 255), selected = new Color(255, 255, 0);
    private Color normalBG = new Color(51, 51, 51), selectedBG = new Color(11, 11, 11);
    
    public void AddObject(Object o, String name) {
        addObj.put(name, o);
    }
    
    public Object GetObject(String name) {
        return addObj.get(name);
    }
    
    public void SetSelected(boolean state) {
        this.setBackground((state) ? selectedBG : normalBG); 
    }
    
    public GlobalMainMenuElement(String _GID, String _ID, String title, ImageIcon icon) {
        initComponents();
        ID = _ID;
        GID = _GID;
        jLabel2.setText(title); 
        jLabel1.setIcon(icon);
    }
    
    public GlobalMainMenuElement(String _GID, String _ID, String title) { 
        initComponents();
        ID = _ID;
        GID = _GID;
        jLabel2.setText(title); 
    }
    
    public void SetBackgroundColor(Color c) {
        this.setBackground(c);
    }
    
    public void SetSelectedBackgroundColor(Color c) {
        selectedBG = c;
    }
    
    public void SetNormalBackgroundColor(Color c) {
        normalBG = c;
    }
    
    public void SetUnhoverLabelColor(Color c) {
        normal = c;
    }
    
    public void SetHoverLabelColor(Color c) {
        selected = c;
    }

    public void SetActionListener(XListElementActionListener al) {
        xlistener = al;
    }
    
    public void SetIcon(ImageIcon ii) {
        jLabel1.setIcon(ii); 
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(51, 51, 51));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                formMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                formMouseEntered(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/zz1q2.png"))); // NOI18N
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel1MouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel1MouseEntered(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("title");
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel2MouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel2MouseEntered(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        if (xlistener != null) xlistener.OnItemClick(GID, ID, this, evt);
    }//GEN-LAST:event_jLabel2MouseClicked

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        if (xlistener != null) xlistener.OnItemClick(GID, ID, this, evt);
    }//GEN-LAST:event_jLabel1MouseClicked

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        if (xlistener != null) xlistener.OnItemClick(GID, ID, this, evt);
    }//GEN-LAST:event_formMouseClicked

    private void formMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseEntered
        jLabel2.setForeground(selected);
    }//GEN-LAST:event_formMouseEntered

    private void formMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseExited
        jLabel2.setForeground(normal);
    }//GEN-LAST:event_formMouseExited

    private void jLabel2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseEntered
        jLabel2.setForeground(selected);
    }//GEN-LAST:event_jLabel2MouseEntered

    private void jLabel1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseEntered
        jLabel2.setForeground(selected);
    }//GEN-LAST:event_jLabel1MouseEntered

    private void jLabel2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseExited
        jLabel2.setForeground(normal);
    }//GEN-LAST:event_jLabel2MouseExited

    private void jLabel1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseExited
        jLabel2.setForeground(normal);
    }//GEN-LAST:event_jLabel1MouseExited

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
