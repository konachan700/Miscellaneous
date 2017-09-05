package jnekotabs;

import connections.JNekoSP;
import java.awt.Color;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import xlist.GlobalSearchFormElement;
import xlist.XListSearchFormActionListener;
import xlistcustomelements.BillingUserMainElement;

public class Last100Payments extends javax.swing.JPanel {
    private final ImageIcon groupIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/a2e.png"));
    private Color color;
    
    @SuppressWarnings("Convert2Lambda")
    private final XListSearchFormActionListener search_listener=new XListSearchFormActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID, String searchString) {
            if ((buttonID == 1005) || (buttonID == 1007)) {
                if (searchString.length() > 0) xlist1.Search("PaymentsBlock", searchString); else xlist1.Search("PaymentsBlock", null);
            }
            
            if (buttonID == 1777) {
                Refresh();
            }
        }
    };
        
    public void Refresh() {       
        xlist1.ClearAll();
        xlist1.AddGroupHeader("SearchBlock", color, "Поиск платежей", groupIcon, 0);
        xlist1.AddGroupHeader("PaymentsBlock", color, "Платежи (100 последних)", groupIcon, 0);
        
        xlist1.AddItem("SF1", "SearchBlock", new GlobalSearchFormElement(false, "SF1", "SearchBlock", search_listener), "", 0);
        
        if (JNekoSP.GetSP() == null) {
            xlist1.AddItemForSimpleList("NOITEMS", "PaymentsBlock", "Нет подключения к системе JNekoSP", groupIcon);
            xlist1.VPBON();
            xlist1.setBackgroundColor(color); 
            xlist1.Commit(true);
            return;
        }

        final ArrayList<Map<String,String>> x = JNekoSP.GetSP().SQLSelect("SELECT Payments.*, Users.userFullName "
                + "FROM Payments LEFT JOIN Users ON (Users.did=Payments.userDID) WHERE Payments.paymentType=1 ORDER BY did DESC LIMIT 0,3000;");

        final StringBuilder sb = new StringBuilder();
        
        final SimpleDateFormat DF = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        if (x != null) {
            for (Map<String,String> xx : x) {
                final long money = Long.parseLong(xx.get("moneyCount"), 10) / 100;
                final String moneyX = (money < 0) ? "Списание" : "Пополнение";
                final long DiD = Long.parseLong(xx.get("did"), 10);
                final BillingUserMainElement bmi = new BillingUserMainElement(
                        xx.get("userFullName"), money+"", xx.get("comment"), moneyX, DF.format(new Date(DiD)), (money < 0), 0, null, "ID", "PaymentsBlock", false, 2
                );
                xlist1.AddItem(xx.get("did"), "PaymentsBlock", bmi, 
                        xx.get("userFullName") + " " + DF.format(new Date(DiD)) + " " + xx.get("comment"), 0);
                
                sb.append(moneyX).append("\t").append(money).append("\t").append(DF.format(new Date(DiD))).append("\t").append(xx.get("userFullName"))
                        .append("\t").append(xx.get("comment")).append("\r\n");
            }
        }
        
        try {
            Files.deleteIfExists(FileSystems.getDefault().getPath("./report.txt"));
            Files.write(FileSystems.getDefault().getPath("./report.txt"), sb.toString().getBytes());
        } catch (IOException ex) {
            Logger.getLogger(Last100Payments.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        xlist1.VPBON();
        xlist1.setBackgroundColor(color); 
        xlist1.Commit(true);
    }
    
    public Last100Payments(Color c) {
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
