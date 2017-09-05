package jnekotabs;

import connections.JNekoPrepSQL;
import connections.JNekoSP;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import xdialogs.AddStaticIP;
import xlist.GlobalSearchFormElement;
import xlist.XListButtonsActionListener;
import xlist.XListSearchFormActionListener;
import xlistcustomelements.StaticIPElement;

public class StaticIPList extends javax.swing.JPanel {
    private final ImageIcon groupIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/a2e.png"));
    private Color color;
    private final StaticIPList THIS = this;
    
    @SuppressWarnings("Convert2Lambda")
    private final XListSearchFormActionListener search_listener=new XListSearchFormActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID, String searchString) {
            if ((buttonID == 1005) || (buttonID == 1007)) {
                if (searchString.length() > 0) xlist1.Search("RBlock", searchString); else xlist1.Search("RBlock", null);
            }
            
            if (buttonID == 1777) {
                Refresh();
            }
            
            if (buttonID == 1006) {
                AddStaticIP.ShowNew(THIS);
                Refresh();
            }
        }
    };
    
    private final XListButtonsActionListener int_listener = new XListButtonsActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID) {
            final String serial, title;
            switch (buttonID) {
                case 1001:
                    final int q = JOptionPane.showConfirmDialog(THIS, "Вы уверенны?","Удалить элемент #"+ID, JOptionPane.YES_NO_OPTION);
                    if (q == JOptionPane.YES_OPTION) {
                        final JNekoPrepSQL ps1 = new JNekoPrepSQL("DELETE FROM StaticIP WHERE did=?;");
                        ps1.AddParam(ID);
                        JNekoSP.GetSP().SQLInsertUpdate(ps1);
                    }
                    break;
            }
            Refresh();
        }
    };
        
    public void Refresh() {       
        xlist1.ClearAll();
        xlist1.AddGroupHeader("SearchBlock", color, "Поиск статических адресов", groupIcon, 0);
        xlist1.AddGroupHeader("RBlock", color, "Список адресов", groupIcon, 0);
        
        xlist1.AddItem("SF1", "SearchBlock", new GlobalSearchFormElement(true, "SF1", "SearchBlock", search_listener), "", 0);
        
        if (JNekoSP.GetSP() == null) {
            xlist1.AddItemForSimpleList("NOITEMS", "RBlock", "Нет подключения к системе JNekoSP", groupIcon);
            xlist1.VPBON();
            xlist1.setBackgroundColor(color); 
            xlist1.Commit(true);
            return;
        }

        final ArrayList<Map<String,String>> x = JNekoSP.GetSP().SQLSelect(
                  "SELECT " 
                          + "StaticIP.*, "
                          + "Users.ip AS InternalIP, "
                          + "Users.userFullName AS UserName "
                + "FROM "
                          + "StaticIP "
                          + "LEFT JOIN Users ON (StaticIP.udid=Users.did) "
                + "ORDER BY extIP DESC;"
        );
        if (x != null) {
            if (x.size() > 0) {
                for (Map<String,String> xx : x) {
                    final long cpm = Long.parseLong(xx.get("cpm"), 10) / 100;
                    StaticIPElement bmi = new StaticIPElement(
                            xx.get("did"), "RBlock", color, xx.get("UserName") + " (" + cpm + ")", xx.get("extIP") + " -> " + xx.get("InternalIP"), int_listener);
                    xlist1.AddItem(xx.get("did"), "RBlock", bmi, xx.get("UserName") + xx.get("extIP"), 0);
                }
            } else {
                xlist1.AddItemForSimpleList("NOITEMS", "RBlock", "Нет данных для отображения!", groupIcon);
            }
        }
        
        xlist1.VPBON();
        xlist1.setBackgroundColor(color); 
        xlist1.Commit(true);
    }
    
    public StaticIPList(Color c) {
        initComponents();
        color = c;
        Refresh();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        xlist1 = new xlist.XList();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xlist1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xlist1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private xlist.XList xlist1;
    // End of variables declaration//GEN-END:variables
}
