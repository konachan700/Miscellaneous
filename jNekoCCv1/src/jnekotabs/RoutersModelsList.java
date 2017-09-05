package jnekotabs;

import connections.JNekoPrepSQL;
import connections.JNekoSP;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import xlist.GlobalSearchFormElement;
import xlist.XListButtonsActionListener;
import xlist.XListSearchFormActionListener;
import xlistcustomelements.BillingUserMainElement;
import xlistcustomelements.RoutersModelsAddNewElement;
import xlistcustomelements.RoutersModelsElement;

public class RoutersModelsList extends javax.swing.JPanel {
    private final ImageIcon groupIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/a2e.png"));
    private Color color;
    private final RoutersModelsList THIS = this;
    
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
                        final JNekoPrepSQL ps1 = new JNekoPrepSQL("DELETE FROM Routers_ModelList WHERE did=?;");
                        ps1.AddParam(ID);
                        JNekoSP.GetSP().SQLInsertUpdate(ps1);
                    }
                    break;
                case 1002:
                    serial = ((RoutersModelsElement)(xlist1.GetItem(GID, ID).GetComponent())).GetSerial();
                    title  = ((RoutersModelsElement)(xlist1.GetItem(GID, ID).GetComponent())).GetTitle();
                    
                    final JNekoPrepSQL ps2 = new JNekoPrepSQL("UPDATE Routers_ModelList SET title=?, serialpart=? WHERE did=?;");
                    ps2.AddParam(title);
                    ps2.AddParam(serial);
                    ps2.AddParam(ID);
                    JNekoSP.GetSP().SQLInsertUpdate(ps2);
                    
                    break;
                case 1003:
                    serial = ((RoutersModelsAddNewElement)(xlist1.GetItem(GID, ID).GetComponent())).GetSerial();
                    title  = ((RoutersModelsAddNewElement)(xlist1.GetItem(GID, ID).GetComponent())).GetTitle();
                    
                    final JNekoPrepSQL ps3 = new JNekoPrepSQL("INSERT INTO Routers_ModelList VALUES (?, ?, ?);");
                    ps3.AddParam(new Date().getTime() + "");
                    ps3.AddParam(serial);
                    ps3.AddParam(title);
                    JNekoSP.GetSP().SQLInsertUpdate(ps3);

                    break;
            }
            Refresh();
        }
    };
        
    public void Refresh() {       
        xlist1.ClearAll();
        xlist1.AddGroupHeader("SearchBlock", color, "Поиск моделей роутеров", groupIcon, 0);
        xlist1.AddGroupHeader("RBlock", color, "Список типов роутеров", groupIcon, 0);
        
        xlist1.AddItem("SF1", "SearchBlock", new GlobalSearchFormElement(false, "SF1", "SearchBlock", search_listener), "", 0);
        xlist1.AddItem("SF2", "SearchBlock", new RoutersModelsAddNewElement("SF2", "SearchBlock", color, int_listener), "", 0);
        
        
        if (JNekoSP.GetSP() == null) {
            xlist1.AddItemForSimpleList("NOITEMS", "RBlock", "Нет подключения к системе JNekoSP", groupIcon);
            xlist1.VPBON();
            xlist1.setBackgroundColor(color); 
            xlist1.Commit(true);
            return;
        }

        final ArrayList<Map<String,String>> x = JNekoSP.GetSP().SQLSelect("SELECT * FROM Routers_ModelList ORDER BY did DESC;");
        if (x != null) {
            for (Map<String,String> xx : x) {
                final long DiD = Long.parseLong(xx.get("did"), 10);
                final RoutersModelsElement bmi = new RoutersModelsElement(
                        xx.get("did"), "RBlock", color, xx.get("title"), xx.get("serialpart"), int_listener
                );
                xlist1.AddItem(xx.get("did"), "RBlock", bmi, xx.get("title") + xx.get("serialpart"), 0);
            }
        }
        
        xlist1.VPBON();
        xlist1.setBackgroundColor(color); 
        xlist1.Commit(true);
    }
    
    public RoutersModelsList(Color c) {
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
