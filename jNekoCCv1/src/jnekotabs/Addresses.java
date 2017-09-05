package jnekotabs;

import connections.JNekoPrepSQL;
import connections.JNekoSP;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import xlistcustomelements.BillingHSTMainElement;
import xlistcustomelements.BillingHSTAddNewElement;
import xlist.XListButtonsActionListener;

public class Addresses extends javax.swing.JPanel {
    private final Color color;
    private final String title;
    private final Addresses THIS = this;
    private String xTable;
    private final XListButtonsActionListener int_listener = new XListButtonsActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID) {
            final String table, value;
            switch (buttonID) {
                case 1001:
                    final int q = JOptionPane.showConfirmDialog(THIS, "Вы уверенны?","Удалить элемент #"+ID, JOptionPane.YES_NO_OPTION);
                    if (q == JOptionPane.YES_OPTION) {
                        table = ((BillingHSTMainElement)(xlist1.GetItem(GID, ID).GetComponent())).GetTable();
                        
                        final JNekoPrepSQL ps1 = new JNekoPrepSQL("DELETE FROM "+table+" WHERE did=?;");
                        ps1.AddParam(ID);
                        JNekoSP.GetSP().SQLInsertUpdate(ps1);
                        
                    } else table = null;
                    break;
                case 1002:
                    table = ((BillingHSTMainElement)(xlist1.GetItem(GID, ID).GetComponent())).GetTable();
                    value = ((BillingHSTMainElement)(xlist1.GetItem(GID, ID).GetComponent())).GetValue();
                    
                    final JNekoPrepSQL ps2 = new JNekoPrepSQL("UPDATE "+table+" SET svalue=? WHERE did=?;");
                    ps2.AddParam(value);
                    ps2.AddParam(ID);
                    JNekoSP.GetSP().SQLInsertUpdate(ps2);
                    
                    break;
                case 1003:
                    table = ((BillingHSTAddNewElement)(xlist1.GetItem(GID, ID).GetComponent())).GetTable();
                    value = ((BillingHSTMainElement)(xlist1.GetItem(GID, ID).GetComponent())).GetValue();
                    
                    final JNekoPrepSQL ps3 = new JNekoPrepSQL("INSERT INTO "+table+" VALUES (?, ?, \"null\");");
                    ps3.AddParam(new Date().getTime() + "");
                    ps3.AddParam(value);
                    JNekoSP.GetSP().SQLInsertUpdate(ps3);

                    break;
                default:
                    table = null;
                    break;
            }
            if (table != null) Refresh(table);
        }
    };
    
    private final ImageIcon groupIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/a2e.png"));
    
    public void Refresh(String Table) {
        xlist1.ClearAll();
        
        xlist1.AddGroupHeader("HST_ADD", color, "Добавить новый элемент в список", groupIcon, 0);
        xlist1.AddItem("ADDNEW", "HST_ADD", new BillingHSTAddNewElement("ADDNEW", "HST_ADD", color, Table, int_listener), "", 0);
        
        xlist1.AddGroupHeader("HST_LIST", color, title, groupIcon, 0);
        
        final ArrayList<Map<String,String>> stx = JNekoSP.GetSP().SQLSelect("SELECT * FROM "+Table+" ORDER BY did DESC;");
        if (stx != null) {
            for (Map<String,String> x : stx) {
                xlist1.AddHSTElement(x.get("did"), "HST_LIST", x.get("svalue"), Table, int_listener);
            }
        } else {
           xlist1.AddItemForSimpleList("NULL", "HST_LIST", "Нет данных для отображения.", xlist1.ICON_HOME);
        }
        xlist1.Commit(true);
    }
    
    public void Refresh() {
        Refresh(xTable);
    }
    
    public Addresses(Color c, String Table, String Title) {
        initComponents();
        xlist1.VPBON();
        color = c;
        title = Title;
        xlist1.setBackgroundColor(c); 
        xTable = Table;
        Refresh(Table);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        xlist1 = new xlist.XList();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xlist1, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xlist1, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private xlist.XList xlist1;
    // End of variables declaration//GEN-END:variables
}
