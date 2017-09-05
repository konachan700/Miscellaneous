package xdialogs;

import connections.JNekoPrepSQL;
import connections.JNekoSP;
import connections.JNekoSPBackground;
import datasource.JNekoServerInfo;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.ImageIcon;
import xlist.GlobalSearchFormElement;
import xlist.SettingsTextBoxElement;
import xlist.XListButtonsActionListener;
import xlist.XListSearchFormActionListener;
import xlistcustomelements.BillingUserMainElement;

public class ConnectONT extends javax.swing.JFrame {
    private final ConnectONT THIS = this;
    
    @SuppressWarnings("Convert2Lambda")
    private final XListSearchFormActionListener search_listener=new XListSearchFormActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID, String searchString) {
            if ((buttonID == 1005) || (buttonID == 1007)) {
                if (searchString.length() > 0) xList1.Search("USERS_LIST", searchString); else xList1.Search("USERS_LIST", null);
            }
        }
    };
    
    @SuppressWarnings("Convert2Lambda")
    private final XListButtonsActionListener int_listener = new XListButtonsActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID) {
            if (buttonID == 1014) {
                String ip = ((SettingsTextBoxElement)(xList1.GetItem("MAIN", "XXIP").GetComponent())).GetValue();
                String mac = ((SettingsTextBoxElement)(xList1.GetItem("MAIN", "XXMAC").GetComponent())).GetValue();
                String slot = ((SettingsTextBoxElement)(xList1.GetItem("MAIN", "XXSLOT").GetComponent())).GetValue();
                String serial = ((SettingsTextBoxElement)(xList1.GetItem("MAIN", "XXPONSERIAL").GetComponent())).GetValue();

                final JNekoPrepSQL ps1 = new JNekoPrepSQL("UPDATE Users SET ip=?, mac=?, slot=?, eltx=? WHERE did=?;");
                ps1.AddParam(ip);
                ps1.AddParam(mac);
                ps1.AddParam(slot);
                ps1.AddParam(serial);
                ps1.AddParam(ID);
                JNekoSP.GetSP().SQLInsertUpdate(ps1);
                JNekoSP.GetSP().ReloadServerConfig();
                        
                MsgBox.Show("Пользователь", "Привязка роутера "+serial+" к пользователю #"+ID+" осуществлена успешно.");
                THIS.dispose();
            }
        }
    };

    public ConnectONT(String ip, String mac, String ont, String slot, String user, String sysuser) {
        initComponents();
        
        final Color ColorForBG = jPanel1.getBackground();
        final ImageIcon groupIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/a2e.png"));
        
        xList1.setBackgroundColor(ColorForBG);
        xList1.VPBON();
        Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((s.width/2)-(this.getWidth()/2), (s.height/2)-(this.getHeight()/2), this.getWidth(), this.getHeight());
        
        xList1.AddGroupHeader("MAIN", ColorForBG.darker(), "Прикрепить ONT к пользователю в биллинге", groupIcon, 0);
        
        xList1.AddItem("XXIP", "MAIN",  new SettingsTextBoxElement(ip, "IP", false));
        xList1.AddItem("XXMAC", "MAIN",  new SettingsTextBoxElement(mac, "MAC", false));
        xList1.AddItem("XXSLOT", "MAIN",  new SettingsTextBoxElement(slot, "Slot", false));
        xList1.AddItem("XXPONSERIAL", "MAIN",  new SettingsTextBoxElement(ont, "PON serial", false));

        xList1.AddGroupHeader("USEARCH", ColorForBG.darker(), "Найти пользователя", groupIcon, 0);
        xList1.AddItem("SF1", "USEARCH", new GlobalSearchFormElement(false, "SF1", "USEARCH", search_listener), "", 0);

        xList1.AddGroupHeader("USERS_LIST", ColorForBG.darker(), "Список всех доступных пользователей", groupIcon, 0);
        
        final ArrayList<Map<String,String>> stx = JNekoSP.GetSP().SQLSelect(
                      "SELECT "
                    + "  Users.*, "
                    + "  Towns.svalue    AS TownString, "
                    + "  Streets.svalue  AS StreetString, "
                    + "  Houses.svalue   AS HouseNumber "
                    + "FROM "
                    + "  Users "
                    + "LEFT JOIN Streets ON (Users.streetID=Streets.did) "
                    + "LEFT JOIN Towns   ON (Users.townID=Towns.did) "
                    + "LEFT JOIN Houses  ON (Users.houseID=Houses.did) "
                    + "ORDER BY Users.did DESC;"
        );
        if (stx != null) {
            final ImageIcon offlineIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_32x32/UserIcon-Default.png"));
            final ImageIcon onlineIcon  = new javax.swing.ImageIcon(getClass().getResource("/icons_32x32/UserIcon-Online.png"));
            final ImageIcon noMoneyIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_32x32/UserIcon-NoMoney.png"));

            for (Map<String,String> x : stx) {
                String uaddr = (x.get("flatNumber").equals("0") && x.get("HouseNumber").equals("0")) ? 
                        "Тестовый стенд в офисе" :
                        ("г."+x.get("TownString")+", ул."+x.get("StreetString")+ ", дом "+x.get("HouseNumber")+", кв."+x.get("flatNumber"));
                String ueltx = (x.get("eltx").length()>1) ? x.get("eltx") + " Slot " + x.get("slot") : "";
                String uip = (ueltx.length() > 1) ? x.get("ip") : "";
                long money = Long.parseLong(x.get("userMoney"), 10) / 100;
                
                final BillingUserMainElement fe = new BillingUserMainElement(
                        x.get("userFullName"), money+"", uaddr, ueltx, uip, false, 0, int_listener, x.get("did"), "USERS_LIST", false, 1);
                
                if (money >= 0) {
                    fe.SetIcon(offlineIcon);
                    ConcurrentHashMap<String, JNekoServerInfo.ONTInfo> oi = JNekoSPBackground.jNekoServerInfo.ONTsInfo;
                    if (oi != null) {
                        Set<String> oikeys = oi.keySet();
                        for (String oikey : oikeys) {
                            JNekoServerInfo.ONTInfo oielement = oi.get(oikey);
                            if (x.get("eltx").trim().equalsIgnoreCase(oielement.ELTX)) {
                                fe.SetIcon(onlineIcon); 
                            }
                        }
                    }
                } else {
                    fe.SetIcon(noMoneyIcon);
                }
                xList1.AddItem(x.get("did"), "USERS_LIST", fe, x.get("userFullName"), 0); 
            }
        } else {
            xList1.AddItemForSimpleList("NULL", "USERS_LIST", "Нет данных для отображения.", xList1.ICON_HOME);
        }
        xList1.Commit();
    }
    
    public static void ShowNew(String ip, String mac, String ont, String slot, String user, String sysuser) {
        ConnectONT jn = new ConnectONT(ip, mac, ont, slot, user, sysuser);
        jn.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        xList1 = new xlist.XList();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Назначить ONT пользователю");

        jPanel1.setBackground(new java.awt.Color(53, 28, 44));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new-logo-right.png"))); // NOI18N

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new-logo-left.png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 331, Short.MAX_VALUE)
                .addComponent(jLabel1))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xList1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xList1, javax.swing.GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private xlist.XList xList1;
    // End of variables declaration//GEN-END:variables
}
