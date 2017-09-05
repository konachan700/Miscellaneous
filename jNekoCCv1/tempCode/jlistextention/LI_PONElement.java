package jlistextention;

//import javax.swing.JButton;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;


public class LI_PONElement extends javax.swing.JPanel {
//    public static final int ITEM_NEW    = 1,
//                            ITEM_EXIST  = 2,
//            
//                            ITEM_BUTTON_ADD     = 1,
//                            ITEM_BUTTON_DEL     = 2,
//                            ITEM_BUTTON_INFO    = 3;
    
//    public interface ListItemActionListener {
//        public void Action(int ButtonIndex, int ItemIndex, java.awt.event.ActionEvent evt);
//    }
    
//    private ListItemActionListener    AL        = null;
    private int                       ItemIndex = 0;
    private final Map<String, Object> addObj = new HashMap<>();
    private final ImageIcon hoverIcon  = new javax.swing.ImageIcon(getClass().getResource("/icons_32x32/view-process-all-tree-yellow.png")),
                            normalIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_32x32/view-process-all-tree.png"));
        
    public LI_PONElement(String Title, String subTitle, String Text) {
//    public LI_PONElement(int Type, String Title, String subTitle, String Text) {
        initComponents();
        jLabel2.setText(Title);
        jLabel3.setText(Text);
        jLabel4.setText(subTitle);

//        if (Type == ITEM_NEW) {
//            jButton1.setVisible(false);
//            jButton2.setVisible(false);
//        }
    }
    
    public void AddObject(Object o, String name) {
        addObj.put(name, o);
    }
    
    public Object GetObject(String name) {
        return addObj.get(name);
    }
    
    public void SetIndex(int i) {
        ItemIndex = i;
    }
    
    public void SetHover(boolean sel) {
        if (sel) {
            jLabel1.setIcon(hoverIcon);
        } else {
            jLabel1.setIcon(normalIcon);
        }    
    }
    
    public void SetSelected(boolean sel) {
        if (sel) {
           this.setBackground(new java.awt.Color(22, 22, 22));
        } else {
           this.setBackground(((ItemIndex % 2) == 1) ? new java.awt.Color(102, 102, 102) : new java.awt.Color(82, 82, 82));
        }
    }
    
    public javax.swing.JLabel GetTitle() {
        return jLabel2;
    }

    public javax.swing.JLabel GetSubTitle() {
        return jLabel4;
    }

    public javax.swing.JLabel GetText() {
        return jLabel3;
    }

    public javax.swing.JLabel GetIcon() {
        return jLabel1;
    }
    
//    public ListItemActionListener GetCallback() {
//        return AL;
//    }
    
//    public void SetCallback(ListItemActionListener liae) {
//        AL = liae;
//        if (AL == null) return;
//
//        jButton3.addActionListener((java.awt.event.ActionEvent evt) -> {
//            AL.Action(ITEM_BUTTON_ADD, ItemIndex, evt);
//        });
//
//        jButton2.addActionListener((java.awt.event.ActionEvent evt) -> {
//            AL.Action(ITEM_BUTTON_INFO, ItemIndex, evt);
//        });
//
//        jButton1.addActionListener((java.awt.event.ActionEvent evt) -> {
//            AL.Action(ITEM_BUTTON_DEL, ItemIndex, evt);
//        });
//    }
    
//    public JButton getButtonInfo() {
//        return jButton2;
//    }
//    
//    public JButton getButtonAdd() {
//        return jButton3;
//    }
//    
//    public JButton getButtonDel() {
//        return jButton1;
//    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(102, 102, 102));
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_32x32/view-process-all-tree.png"))); // NOI18N

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Monospaced", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 153));
        jLabel2.setText("ELTX00000000");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Monospaced", 0, 10)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Text - text - text");

        jLabel4.setFont(new java.awt.Font("Monospaced", 0, 10)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(204, 255, 204));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Subtitle");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3))
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    // End of variables declaration//GEN-END:variables
}
