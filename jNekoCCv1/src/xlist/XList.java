package xlist;

import xlistcustomelements.BillingHSTMainElement;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class XList extends javax.swing.JPanel {
    public final ImageIcon ICON_ALLUSERS        = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/icon_s-users.png"));
    public final ImageIcon ICON_LOCKEDUSERS     = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/icon-locked.png"));
    public final ImageIcon ICON_PAYMENTS        = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/icon_payments.png"));
    public final ImageIcon ICON_TARIF           = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/icon_tp.png"));
    public final ImageIcon ICON_HOME            = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/icon_home.png"));
    public final ImageIcon ICON_TOWN            = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/icon_town.png"));
    public final ImageIcon ICON_MINUS           = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/icon_edit-delete.png"));
    public final ImageIcon ICON_PLUS            = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/icon_edit-create.png"));
    public final ImageIcon ICON_TRAF            = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/office-chart-area-stacked.png"));
    
    
    
    private Point CustomScrollMPPoint = null;
    
    private String selectedID = null, selectedGID = null;
    
    private final BoxLayout oBoxLayout;
    private XListElementActionListener ActionListener = null; 
    private final XListElementActionListener InternalActionListener = new XListElementActionListener() {
        @Override
        public void OnItemClick(String GID, String ID, Component c, MouseEvent e) {
            if (Enabled == false) return;
            if (c instanceof GlobalMainMenuElement) {
                final Set<String> h = ListHeaders.keySet();
                for (String sh : h) {
                    Set<String> el = ListElements.get(sh).keySet();
                    for (String sel : el) {
                        if (c.equals(ListElements.get(sh).get(sel).GetComponent())) {
                            selectedID = sel;
                            selectedGID = sh;
                        }
                        
                        ListElements.get(sh).get(sel).SetSelected(c.equals(ListElements.get(sh).get(sel).GetComponent()));  
                        ((GlobalMainMenuElement)(ListElements.get(sh).get(sel).GetComponent())).SetSelected(false); 
                    }
                }
                
                GlobalMainMenuElement msl = (GlobalMainMenuElement) c;
                msl.SetSelected(true); 
            }
            
            if (ActionListener != null) ActionListener.OnItemClick(GID, ID, c, e); 
        }

        @Override
        public void OnHeaderClick(String GID, JLabel c, boolean isVisible, MouseEvent e, int Type) { 
            if (Enabled == false) return;
            if (ActionListener != null) ActionListener.OnHeaderClick(GID, c, isVisible, e, Type); 
        }
    };
    
    private final Map<String, JLabel> ListHeaders = new LinkedHashMap<>(); 
    //private final Map<String, xListElementContainer> ListHeaders = new HashMap<>();
    
    private final Map<String, Map<String, XListElementContainer>> ListElements = new LinkedHashMap<>();
    private final Map<String, Map<String, String>> ListTags = new LinkedHashMap<>();
    
    private Color SelectionColor = new Color(33, 33, 33), mainBackgroundColor = new Color(55, 55, 55);

    private final JPanel jPanelX2;
    
    
    private String CurrentSelectedGroup = null;
    private boolean CommitOption_MainSelector = false;
    private boolean Enabled = true;
    
    private int ScrollPosition = 0;
    
    public XList() {
        initComponents();
        /* В этом месте был аттракцион "сделай форму в визуальном редакторе". Охуительный аттракцион.
            Потому как поведение этого самого редактора непонятно совершенно. А руками писать интерфейс лень, хотя и надо было бы. */
        jScrollPane1.getVerticalScrollBar().setUnitIncrement(33); 
        jScrollPane1.getVerticalScrollBar().setMaximumSize(new Dimension(0, 0));
        jScrollPane1.getVerticalScrollBar().setMinimumSize(new Dimension(0, 0));
        jScrollPane1.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        jScrollPane1.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            final float min = e.getAdjustable().getMinimum(),
                        max = e.getAdjustable().getMaximum() - e.getAdjustable().getVisibleAmount(),
                        val = e.getAdjustable().getValue(),
                        h01 = jScrollPane1.getHeight() - jLabel1.getHeight();
            
            final float precent1 = val / (max - min); 
            float endval = (h01 * precent1);

            if (endval < 0) endval = 0;
            ScrollPosition = Math.round(endval);
            jLabel1.setLocation(0, ScrollPosition);
            
            //System.err.println("Scroll="+Math.round(endval));
        });

        jPanel3.setVisible(false);
        jScrollPane1.getVerticalScrollBar().setValue(0);
        //jLabel1.setLocation(0,0);
        
        jPanelX2 = jPanel2; /* Это надо для того, чтобы можно было в дальнейшем изменить имена панелей, не трогая остальной код */
        oBoxLayout = new BoxLayout(jPanelX2, BoxLayout.PAGE_AXIS);
        jPanelX2.setLayout(oBoxLayout);
        jPanelX2.revalidate();
        jPanelX2.repaint();
    }
    
    @Override
    public void setEnabled(boolean enable) {
        Enabled = enable;
        if (jScrollPane1 != null) jScrollPane1.setEnabled(enable);
        if (jPanel1 != null) jPanel1.setEnabled(enable);
        if (jPanelX2 != null) jPanelX2.setEnabled(enable);
        if (jPanel3 != null) jPanel3.setEnabled(enable);
        if (jLabel1 != null) jLabel1.setEnabled(enable);
        super.setEnabled(enable);
    }
    
    public void setBackgroundColor(Color c) {
        if (jScrollPane1 != null) jScrollPane1.setBackground(c);
        if (jPanel1 != null) jPanel1.setBackground(c);
        if (jPanelX2 != null) jPanelX2.setBackground(c);
        if (jPanel3 != null) jPanel3.setBackground(c.darker());
        if (jLabel1 != null) jLabel1.setBackground(c.brighter());
        this.setBackground(c); 
    }
    
    public String GetSelectedID() {
        return selectedID;
    }
    
    public String GetSelectedGID() {
        return selectedGID;
    }
    
    public void SetSelectionColor(Color c) {
        SelectionColor = c;
    }
    
    public Color GetSelectionColor() {
        return SelectionColor;
    }

    public void SetActionListener(XListElementActionListener al) {
        if (al == null) throw new NullPointerException("Cannot add null action listener");
        ActionListener = al;
    }
    
    public XListElementActionListener GetActionListener() {
        return ActionListener;
    }
    
    private JLabel __addColoredSpacer(int h, String sGroup) {
        JLabel l1 = new JLabel();
        l1.setBackground(ListHeaders.get(sGroup).getBackground());
        l1.setOpaque(true); 
        l1.setMinimumSize(new Dimension(1, h));
        l1.setMaximumSize(new Dimension(Short.MAX_VALUE, h));
        l1.setPreferredSize(new Dimension(1, h));
        return l1;
    }
    
    public void Commit() {
        Commit(false, null, null);
    }
    
    public void Commit(boolean stripped) {
        Commit(stripped, null, null);
    }
    
    private void __commit1(String sGroup, boolean stripped, String searchGID, String searchString, String IgnoredGID) {
        boolean x = false;
        if (IgnoredGID != null)
            if (sGroup.contentEquals(IgnoredGID)) return;
        
        ListHeaders.get(sGroup).setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        ListHeaders.get(sGroup).setMinimumSize(new Dimension(1,1));
        jPanelX2.add(ListHeaders.get(sGroup));
        Map<String, XListElementContainer> ElementsMap = ListElements.get(sGroup);
        Set<String> sElementKeys = ListElements.get(sGroup).keySet(); 
        for (String sElement : sElementKeys) {
            ElementsMap.get(sElement).setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
            ElementsMap.get(sElement).setMinimumSize(new Dimension(1,1));
            ElementsMap.get(sElement).SetSelectedColor(ListHeaders.get(sGroup).getBackground().darker()); 
            if (x) {
                ElementsMap.get(sElement).GetComponent().setBackground(ElementsMap.get(sElement).getBackground().darker()); 
                ElementsMap.get(sElement).setBackground(ElementsMap.get(sElement).getBackground().darker());
            } 
            if (stripped) x = !x;
            jPanelX2.add(ElementsMap.get(sElement));

            ElementsMap.get(sElement).setVisible(true);
            if ((searchGID != null) && (searchString != null)) {
                if (sGroup.toLowerCase().contentEquals(searchGID.toLowerCase())) { 
                    String tag = ListTags.get(sGroup).get(sElement); 
                    if (tag != null) ElementsMap.get(sElement).setVisible(tag.toLowerCase().contains(searchString.toLowerCase()));
                }
            }
        }
        jPanelX2.add(__addColoredSpacer(3, sGroup));
        jPanelX2.add(Box.createRigidArea(new Dimension(0,5)));
    }
    
    public void Search(String searchGID, String searchString) {
        if (ListHeaders.get(searchGID) == null) throw new NullPointerException("Group "+searchGID+" not exist in xlist!");
        Set<String> sElementKeys = ListElements.get(searchGID).keySet(); 
        for (String sElement : sElementKeys) {
            ListElements.get(searchGID).get(sElement).setVisible(true);
            if (searchString != null) {
                String tag = ListTags.get(searchGID).get(sElement);
                if (tag != null) ListElements.get(searchGID).get(sElement).setVisible(tag.toLowerCase().contains(searchString.toLowerCase()));
            }
        }
    }
    
    public void Commit(boolean stripped, String searchGID, String searchString) {
        jPanelX2.removeAll();
        
        if (CommitOption_MainSelector) {
            if (CurrentSelectedGroup != null) {
                __commit1(CurrentSelectedGroup, stripped, searchGID, searchString, null);
                __hideGroup(CurrentSelectedGroup, true);
            }
        }
        
        final Set<String> gSet = ListHeaders.keySet();
        for (String sGroup : gSet) {  /* Ох не нравиться мне код вот тут, как-то некрасиво... Но пока работает - трогать не буду. */
            __commit1(sGroup, stripped, searchGID, searchString, ((CommitOption_MainSelector) ? CurrentSelectedGroup : null));
            if ((CommitOption_MainSelector) && (sGroup.contentEquals(CurrentSelectedGroup) == false)) __hideGroup(sGroup, false);
        }

        jPanelX2.revalidate();
        jPanelX2.repaint();
    }
    
    public void SetCommitOption_MainSelector() {
        CommitOption_MainSelector = true;
    }
    
    public void SetCommitOption_MainSelectorDefGroup(String g) {
        CurrentSelectedGroup = g;
    }

    public JLabel GetHeader(String ID) {
        return ListHeaders.get(ID);
    }
    
    public void AddGroupHeader(String ID, Color c, String text, ImageIcon icon, int SortIndex) {
        JLabel l1 = new JLabel();
        l1.setBackground(c);
        l1.setOpaque(true);
        l1.setText(text);
        l1.setIcon(icon);
        l1.setBorder(BorderFactory.createLineBorder(c, 8)); 
        l1.setAlignmentX(JLabel.LEFT_ALIGNMENT); 
        l1.setAlignmentY(JLabel.CENTER); 
        l1.setForeground(Color.YELLOW);//c.darker().darker().darker()); 
        l1.setFont(new java.awt.Font("Liberation Sans", Font.PLAIN, 12)); 

        l1.addMouseListener(new MouseListener(){
            private final String _ID = ID;
            private boolean IsVisible = false;
            private final JLabel l2 = l1;
            
            @Override
            public void mouseClicked(MouseEvent e) {
                CurrentSelectedGroup = _ID;
                __hideGroup(_ID, IsVisible);
                IsVisible = !IsVisible;
                Commit();
                if (ActionListener != null) ActionListener.OnHeaderClick(_ID, l2, IsVisible, e, XListElementActionListener.MOUSE_CLICK); 
            }

            @Override
            public void mousePressed(MouseEvent e) { 
                if (ActionListener != null) ActionListener.OnHeaderClick(_ID, l2, IsVisible, e, XListElementActionListener.MOUSE_PRESSED);
            }

            @Override
            public void mouseReleased(MouseEvent e) { 
                if (ActionListener != null) ActionListener.OnHeaderClick(_ID, l2, IsVisible, e, XListElementActionListener.MOUSE_RELEASED);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (ActionListener != null) ActionListener.OnHeaderClick(_ID, l2, IsVisible, e, XListElementActionListener.MOUSE_ENTERED);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (ActionListener != null) ActionListener.OnHeaderClick(_ID, l2, IsVisible, e, XListElementActionListener.MOUSE_EXITED);
            } 
        });
        
        ListHeaders.put(ID, l1);
        ListElements.put(ID, new LinkedHashMap<>());
        ListTags.put(ID, new LinkedHashMap<>());
    }
    
    public void VPBON() {
        jPanel3.setVisible(true);
        //jScrollPaneX1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    }
    
    public void AddItem(String ID, String GID, XListElementContainer element, String tags, int SortIndex) {
        if (ListHeaders.get(GID) == null) throw new NullPointerException("Group "+GID+" not exist in xlist!");
        ListElements.get(GID).put(ID, element);
        ListTags.get(GID).put(ID, tags);
    }
    
    public void AddItem(Component element, String ID, String GID) {
        this.AddItem(ID, GID, element, "NOTAGS", 0);
    }
    
    public void AddItem(String ID, String GID, Component element) {
        this.AddItem(ID, GID, element, "NOTAGS", 0);
    }
    
    public void AddItem(String ID, String GID, Component element, String tags, int SortIndex) {
        if (ListHeaders.get(GID) == null) throw new NullPointerException("Group "+GID+" not exist in xlist!");
        XListElementContainer el = new XListElementContainer();
        el.SetBGColor(mainBackgroundColor);
        el.SetLabelColor(ListHeaders.get(GID).getBackground());
        el.AddItemToContainer(element); 
        ListElements.get(GID).put(ID, el);
        ListTags.get(GID).put(ID, tags);
    }
    
    public void AddItemForSelector(String ID, String GID, String title, String tags, ImageIcon icon) {
        if (ListHeaders.get(GID) == null) throw new NullPointerException("Group "+GID+" not exist in xlist!");
        GlobalMainMenuElement elx = new GlobalMainMenuElement(GID, ID, title, icon);
        elx.SetSelectedBackgroundColor(ListHeaders.get(GID).getBackground().darker()); 
        elx.SetActionListener(InternalActionListener); 
        this.AddItem(ID, GID, elx, tags, 0);
    }
    
    public void AddItemForSimpleList(String ID, String GID, String title, ImageIcon icon) {
        AddItemForSimpleList(ID, GID, title, icon, "");
    }
    
    public void AddItemForSimpleList(String ID, String GID, String title, ImageIcon icon, String tags) {
        if (ListHeaders.get(GID) == null) throw new NullPointerException("Group "+GID+" not exist in xlist!");
        GlobalMainMenuElement elx = new GlobalMainMenuElement(GID, ID, title);
        elx.SetSelectedBackgroundColor(ListHeaders.get(GID).getBackground().darker()); 
        elx.SetIcon(icon);
        this.AddItem(ID, GID, elx, tags, 0);
    }
    
    public void AddItemForSelector(String ID, String GID, String title, String tags, int SortIndex) {
        if (ListHeaders.get(GID) == null) throw new NullPointerException("Group "+GID+" not exist in xlist!");
        GlobalMainMenuElement elx = new GlobalMainMenuElement(GID, ID, title);
        elx.SetSelectedBackgroundColor(ListHeaders.get(GID).getBackground().darker()); 
        elx.SetActionListener(InternalActionListener); 
        this.AddItem(ID, GID, elx, tags, SortIndex);
    }
    
    public void AddSettingsElementText(String ID, String GID, String title) {
        if (ListHeaders.get(GID) == null) throw new NullPointerException("Group "+GID+" not exist in xlist!");
        SettingsTextBoxElement sit = new SettingsTextBoxElement(GID+"_"+ID, title);
        this.AddItem(ID, GID, sit, title, 0);
    }
    
    public void AddHSTElement(String ID, String GID, String title, String table, XListButtonsActionListener listener) {
        if (ListHeaders.get(GID) == null) throw new NullPointerException("Group "+GID+" not exist in xlist!");
        BillingHSTMainElement sit = new BillingHSTMainElement(ID, GID, mainBackgroundColor, table, title, listener, false);
        this.AddItem(ID, GID, sit, title, 0);
    }

    public XListElementContainer GetItem(String GID, String ID) {
        return ListElements.get(GID).get(ID);
    }
    
    public String GetItemTag(String GID, String ID) {
        return ListTags.get(GID).get(ID);
    }
    
    public void ClearAll() {
        ListElements.clear();
        ListTags.clear();
        ListHeaders.clear();
        jPanelX2.removeAll();
        jPanelX2.revalidate();
        jPanelX2.repaint();
    }
    
    private void __hideGroup(String GID, boolean visible) {
        if (ListHeaders.get(GID) == null) throw new NullPointerException("Group "+GID+" not exist in xlist!");
        Set<String> gSet = ListElements.get(GID).keySet();
        for (String s : gSet) {
            ListElements.get(GID).get(s).setVisible(visible); 
        }
        //Commit();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 0, 0));
        setForeground(new java.awt.Color(0, 0, 0));
        setPreferredSize(new java.awt.Dimension(221, 4));

        jScrollPane1.setBackground(new java.awt.Color(0, 0, 0));
        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 189, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 270, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 26, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel1);

        jPanel3.setBackground(new java.awt.Color(0, 0, 0));
        jPanel3.setForeground(new java.awt.Color(0, 0, 0));

        jLabel1.setBackground(new java.awt.Color(51, 51, 51));
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setOpaque(true);
        jLabel1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jLabel1MouseMoved(evt);
            }
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jLabel1MouseDragged(evt);
            }
        });
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel1MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel1MouseReleased(evt);
            }
        });
        jLabel1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                jLabel1ComponentMoved(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 9, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 97, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MousePressed
        CustomScrollMPPoint = evt.getPoint();
    }//GEN-LAST:event_jLabel1MousePressed

    private void jLabel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseReleased

    }//GEN-LAST:event_jLabel1MouseReleased

    private void jLabel1MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseMoved
        
    }//GEN-LAST:event_jLabel1MouseMoved

    private void jLabel1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseDragged
        float y1 = evt.getY() - CustomScrollMPPoint.y + jLabel1.getY(),
              s1 = (jScrollPane1.getHeight()-jLabel1.getHeight());
        y1 = (y1 > 0) ? y1 : 0;
        y1 = (y1 < s1) ? y1 : s1;
        
        final float yprecent = y1 / s1;
        final float pbmax = (jScrollPane1.getVerticalScrollBar().getMaximum() - jScrollPane1.getVerticalScrollBar().getMinimum()) - jScrollPane1.getVerticalScrollBar().getVisibleAmount();
        jScrollPane1.getVerticalScrollBar().setValue(Math.round(pbmax * yprecent));
    }//GEN-LAST:event_jLabel1MouseDragged

    private void jLabel1ComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jLabel1ComponentMoved
        jLabel1.setLocation(0, ScrollPosition);
    }//GEN-LAST:event_jLabel1ComponentMoved

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
