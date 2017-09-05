package jnekotabs;

import connections.JNekoSP;
import connections.JNekoSPBackground;
import datasource.JNekoSQLite;
import datasource.JNekoServerInfo;
//import dbengine.sMySQLWarappers;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import jneko.JNekoFormDoPayment;
import jneko.JNekoFormUserProfile;
import xlist.GlobalSearchFormElement;
import xlist.XListButtonsActionListener;
import xlist.XListSearchFormActionListener;
import xlistcustomelements.BillingUserMainElement;

public class MainUsersList extends javax.swing.JPanel {
    private final ImageIcon groupIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/a2e.png"));
//    private final ImageIcon offlineIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_32x32/UserIcon-offline.png"));
//    private final ImageIcon onlineIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_32x32/UserIcon-Online.png"));
    
    private final boolean isLocked;
    private final Color color;
    private final MainUsersList THIS = this;
    
    @SuppressWarnings("Convert2Lambda")
    private final XListButtonsActionListener int_listener = new XListButtonsActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID) {
            if (buttonID == 1005) {
                new JNekoFormUserProfile(ID, THIS).setVisible(true);
            }
            
            if (buttonID == 1004) {
                new JNekoFormDoPayment(ID, THIS).setVisible(true);
            }
                        
            if (buttonID == 1006) {
                final int q = JOptionPane.showConfirmDialog(THIS, "Вы уверенны?", "Удалить элемент #"+ID, JOptionPane.YES_NO_OPTION);
                if (q == JOptionPane.YES_OPTION) {
//                    sMySQLWarappers.DeleteFromTable("Users", ID);
//                    JNekoSP sp = new JNekoSP();
//                    sp.DeleteFromTable("Users", ID);
//                    sp.CloseConnection();
//                    Refresh();
                }
            } 
        }
    };
    
    @SuppressWarnings("Convert2Lambda")
    private final XListSearchFormActionListener search_listener=new XListSearchFormActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID, String searchString) {
            if ((buttonID == 1005) || (buttonID == 1007)) {
                if (searchString.length() > 0) xlist1.Search("USERS_LIST", searchString); else xlist1.Search("USERS_LIST", null);
            }
            
            if (buttonID == 1006) {
                new JNekoFormUserProfile(null, THIS).setVisible(true);
            }
            
            if (buttonID == 1777) {
                Refresh();
            }
        }
    };
    
    public final void Refresh() {
        xlist1.ClearAll();
        xlist1.AddGroupHeader("SEARCHBOX", color, "Поиск по списку пользователей", groupIcon, 0);
        final GlobalSearchFormElement sf = new GlobalSearchFormElement(true, "SF1", "SEARCHBOX", search_listener);
        xlist1.AddItem("SF1", "SEARCHBOX", sf, "", 0);
        xlist1.AddGroupHeader("USERS_LIST", color, "Список "+((isLocked) ? "заблокированных" : "всех")+" пользователей", groupIcon, 0);
        
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
                    + (isLocked ? " WHERE (Users.userMoney<0 OR Users.locked=127) " : "")
                    + "ORDER BY Users.did DESC;"
        );
        if (stx != null) {
            final ImageIcon offlineIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_32x32/UserIcon-Default.png"));
            final ImageIcon onlineIcon  = new javax.swing.ImageIcon(getClass().getResource("/icons_32x32/UserIcon-Online.png"));
            final ImageIcon noMoneyIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_32x32/UserIcon-NoMoney.png"));
            boolean IsDeleteAllow = (JNekoSQLite.ReadAPPSettingsLong("AllowDeletingUsers") == 255);
            int counter_active = 0;
            
            for (Map<String,String> x : stx) {
                if ((x.get("userFullName").length() > 3) && (x.get("userFullName").contains("#") == false)) counter_active++;
                
                String uaddr = (x.get("flatNumber").equals("0") && x.get("HouseNumber").equals("0")) ? 
                        "Тестовый стенд в офисе" :
                        ("г."+x.get("TownString")+", ул."+x.get("StreetString")+ ", дом "+x.get("HouseNumber")+", кв."+x.get("flatNumber"));
                String ueltx = (x.get("eltx").length()>1) ? x.get("eltx") + " Slot " + x.get("slot") : "";
                String uip = (ueltx.length() > 1) ? x.get("ip") : "";
                long money = Long.parseLong(x.get("userMoney"), 10) / 100;
                
                final BillingUserMainElement fe = new BillingUserMainElement(
                        x.get("userFullName"), money+"", uaddr, ueltx, uip, isLocked, 0, int_listener, x.get("did"), "USERS_LIST", IsDeleteAllow, 0);
                
                if ((money >= 0) && (isLocked == false)) {
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
                xlist1.AddItem(x.get("did"), "USERS_LIST", fe, 
                        x.get("userFullName") + " " + x.get("ip") + " " + x.get("mac") + " " + x.get("mobilePhone") + " " + x.get("did") +
                                " " + x.get("eltx") + " " + x.get("flatNumber") + " " + x.get("HouseNumber") + " " + x.get("StreetString") 
                        , 0); 
            }
            xlist1.GetHeader("USERS_LIST").setText(xlist1.GetHeader("USERS_LIST").getText() + " (" + counter_active + ")");
        } else {
            xlist1.AddItemForSimpleList("NULL", "USERS_LIST", "Нет данных для отображения.", xlist1.ICON_HOME);
        }
        xlist1.Commit(true);
    }
    
    public MainUsersList(Color c, boolean _isLocked) {
        initComponents();
        xlist1.VPBON();
        xlist1.setBackgroundColor(c); 
        isLocked = _isLocked;
        color = c;
        Refresh();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        xlist1 = new xlist.XList();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xlist1, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xlist1, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private xlist.XList xlist1;
    // End of variables declaration//GEN-END:variables
}
