package xlist;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

public class XListElementContainer extends javax.swing.JPanel {
    private final Map<String, Object> addObj = new HashMap<>();
    private Component comX = null;
    private Color normalBG = new Color(51, 51, 51), selectedBG = new Color(11, 11, 11), HColor = Color.BLACK;
    
    public void AddObject(Object o, String name) {
        addObj.put(name, o);
    }
    
    public void SetSelectedColor(Color c) {
        selectedBG = c;
    }
    
    public void SetNormalColor(Color c) {
        normalBG = c;
    }
    
    public void SetSelected(boolean state) {
        this.setBackground((state) ? selectedBG : normalBG); 
    }
    
    public Object GetObject(String name) {
        return addObj.get(name);
    }
    
    public XListElementContainer() {
        initComponents();
    }

    public void SetLabelColor(Color c) {
        HColor = c;
    }
    
    public Color GetLabelColor() {
        return HColor;
    }
    
    public void SetBGColor(Color c) {
        this.setBackground(c);
        jPanel2.setBackground(c);
    }
    
    public Color GetBGColor() {
        return this.getBackground();
    }
    
    public Component GetComponent() {
        return comX;
    }
    
    public void AddItemToContainer(Component c) {
        comX = c;
        
        jPanel2.removeAll();
        jPanel2.revalidate();
        jPanel2.repaint();
        
        jPanel2.add(c);
        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(c, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(c, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel2.revalidate();
        jPanel2.repaint();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 80, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 13, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
