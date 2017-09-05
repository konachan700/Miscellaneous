package jnekotabs;

import connections.JNekoPrepSQL;
import connections.JNekoSP;
//import dbengine.sMySQLWarappers;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import jneko.JNekoFormEditTariff;
import xlist.GlobalSearchFormElement;
import xlistcustomelements.BillingTariffPlanElement;
import xlist.XListButtonsActionListener;
import xlist.XListSearchFormActionListener;

public class TariffPlans extends javax.swing.JPanel {
    private final ImageIcon groupIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/a2e.png"));
    private final Color color;
    private final TariffPlans THIS = this;
    
    @SuppressWarnings("Convert2Lambda")
    private final XListSearchFormActionListener search_listener=new XListSearchFormActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID, String searchString) {
            if ((buttonID == 1005) || (buttonID == 1007)) {
                if (searchString.length() > 0) xlist1.Search("Tarifs", searchString); else xlist1.Search("Tarifs", null);
            }
            
            if (buttonID == 1006) {
                new JNekoFormEditTariff(null, THIS).setVisible(true); 
            }
            
            if (buttonID == 1777) {
                Refresh();
            }
        }
    };
    
    @SuppressWarnings("Convert2Lambda")
    private final XListButtonsActionListener int_listener = new XListButtonsActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID) {
            if (buttonID == 1110) {
                new JNekoFormEditTariff(ID, THIS).setVisible(true); 
            }
            
            if (buttonID == 1111) {
                final int q = JOptionPane.showConfirmDialog(THIS, "Вы уверенны?", "Удалить элемент #"+ID, JOptionPane.YES_NO_OPTION);
                if (q == JOptionPane.YES_OPTION) {
                    final JNekoPrepSQL ps1 = new JNekoPrepSQL("DELETE FROM TarifPlans WHERE did=?;");
                    ps1.AddParam(ID);
                    JNekoSP.GetSP().SQLInsertUpdate(ps1);
                    Refresh();
                }
            }  
        }
    };
    
    public final void Refresh() {
        xlist1.ClearAll();
                xlist1.AddGroupHeader("SearchBlock", color, "Поиск тарифов", groupIcon, 0);
        xlist1.AddGroupHeader("Tarifs", color, "Все тарифные планы", groupIcon, 0);
        xlist1.AddItem("SF1", "SearchBlock", new GlobalSearchFormElement(true, "SF1", "SearchBlock", search_listener), "", 0);
        
        final ArrayList<Map<String,String>> stx = JNekoSP.GetSP().SQLSelect("SELECT * FROM TarifPlans ORDER BY did ASC;");
        if (stx != null) {
            for (Map<String,String> x : stx) {
                xlist1.AddItem(x.get("did"), "Tarifs", 
                        new BillingTariffPlanElement(x.get("did"), x.get("name"), x.get("uploadSpeed"), x.get("downloadSpeed"), 
                                ""+(Long.parseLong(x.get("costPerMonth"), 10)/100), int_listener), x.get("name"), 0);
            }
        } else {
           xlist1.AddItemForSimpleList("NULL", "Tarifs", "Нет данных для отображения.", xlist1.ICON_HOME);
        }

        xlist1.VPBON();
        xlist1.setBackgroundColor(color);
        xlist1.Commit(true);
    }
    
    public TariffPlans(Color c) {
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

//1	did	bigint(20)
//2	name	char(64)
//3	uploadSpeed	bigint(20)
//4	downloadSpeed	bigint(20)
//5	costPerMonth	bigint(20)
//6	state	bigint(20)