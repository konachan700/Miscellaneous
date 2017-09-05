package jlistextention;

import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

public class LI_UsersAndGroups extends javax.swing.JPanel {
    private int                       ItemIndex = 0;
    private final Map<String, Object> addObj = new HashMap<>();
    private final ImageIcon lockedIcon  = new javax.swing.ImageIcon(getClass().getResource("/icons_32x32/user-locked.png")),
                            normalIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_32x32/user-normal.png"));
    private long did = -1;
    
    public LI_UsersAndGroups(String name, String money, String address, boolean locked, int iconIndex) {
        initComponents();
        if (iconIndex == 0) SetLocked(locked, iconIndex);
        switch (iconIndex) {
            case 1:
                jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_32x32/bungalow.png")));
                break;
            case 2:
                jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_32x32/community-help.png")));
                break;
        }
        
        jLabel2.setText(money);
        jLabel3.setText(name);
        jLabel4.setText(address);
    }
    
    public void SetDID(long _did) {
        did = _did;
    }
    
    public long GetDID() {
        return did;
    }
    
    public final void SetLocked(boolean sel, int iconIndex) {
        if ((sel) && (iconIndex == 0)) {
            jLabel1.setIcon(lockedIcon);
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
    
    public void AddObject(Object o, String name) {
        addObj.put(name, o);
    }
    
    public Object GetObject(String name) {
        return addObj.get(name);
    }
    
    public void SetIndex(int i) {
        ItemIndex = i;
    }
    
    public javax.swing.JLabel GetMoney() {
        return jLabel2;
    }

    public javax.swing.JLabel GetTitle() {
        return jLabel3;
    }

    public javax.swing.JLabel GetText() {
        return jLabel4;
    }

    public javax.swing.JLabel GetIcon() {
        return jLabel1;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(102, 102, 102));
        setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_32x32/user-normal.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(204, 204, 0));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("9000");

        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setFont(new java.awt.Font("Liberation Sans", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 153));
        jLabel3.setText("Иванов Иван Иванович");

        jLabel4.setFont(new java.awt.Font("Liberation Sans", 0, 10)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Адрес адрес адрес");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel2)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)))
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
