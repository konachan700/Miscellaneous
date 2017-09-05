package jnekotabs;

import connections.JNekoPrepSQL;
import connections.JNekoSP;
import connections.JNekoSPBackground;
import connections.MA4000Wrappers;
import datasource.JNekoServerInfo;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import jneko.JNekoProgressDisplayClass;
import xdialogs.ConnectONT;
import xdialogs.ONTInfo;
import xdialogs.MsgBox;
import xlist.GlobalSearchFormElement;
import xlist.XListSearchFormActionListener;
import xlistcustomelements.JNekoONTElementA;
import xlistcustomelements.JNekoONTElementAListener;

public class AllOnlineONTs extends javax.swing.JPanel {
    private final Color color;
    private final ImageIcon groupIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/a2e.png"));
    
    private final JNekoONTElementAListener ButtonsListener = new JNekoONTElementAListener() {
        @Override
        public void OnButtonClick(int Button, String ip, String mac, String ont, String slot, String user, String sysuser) {
            switch (Button) {
                case JNekoONTElementA.BUTTON_ONT_SET_FOR_USER:
                    ConnectONT.ShowNew(ip, mac, ont, slot, user, sysuser); 
                    break;
                case JNekoONTElementA.BUTTON_ONT_RESET:
                    final int res1 = MsgBox.ShowYesNo("Reset to defaults", "Сбросить все настройки устройства "+ont+" до установленных на ACS-сервере?");
                    if (res1 == MsgBox.BUTTON_YES) {
                        Thread t = new Thread(() -> {
                            JNekoProgressDisplayClass.On();
                            boolean bresult = (MA4000Wrappers.ONTDefault(ont, slot) == 0);
                            JNekoProgressDisplayClass.Off();
                            MsgBox.Show("Reset to defaults", "Сброс до стандартных настроек "+((bresult) ? "произведен успешно" : "не произведен")+".");
                        });
                        t.start();
                    }
                    break;
                case JNekoONTElementA.BUTTON_ONT_DELETE:
                    // тут пока не удаление, а миграция на 772 vlan
//                    final int res3 = MsgBox.ShowYesNo("Миграция", "Переместить устройство "+ont+"/"+sysuser+" на новый сервис?");
//                    if (res3 == MsgBox.BUTTON_YES) {
//                        Thread t = new Thread(() -> {
//                            JNekoProgressDisplayClass.On();
//                            boolean bresult1 = (MA4000Wrappers.tempMigrateTo772(ont, slot, sysuser) == 0); 
//                            try { Thread.sleep(1000 * 45); } catch (InterruptedException ex) { }
//                            boolean bresult2 = (MA4000Wrappers.ONTDefault(ont, slot) == 0);
//                            JNekoProgressDisplayClass.Off();
//                            MsgBox.Show("Миграция", "Перемещение "+((bresult1 && bresult2) ? "произведено успешно" : "не произведено")+".");
//                        });
//                        t.start();
//                    }
                    break;
                case JNekoONTElementA.BUTTON_ONT_DEL_FOR_USER:
                    final int res2 = MsgBox.ShowYesNo("Disconnect", "Отсоединить "+ont+" от пользователя "+user+"?");
                    if (res2 == MsgBox.BUTTON_YES) {
                        final JNekoPrepSQL ps1 = new JNekoPrepSQL("UPDATE Users SET ip=\"\", mac=\"\", slot=\"\", eltx=\"\" WHERE userFullName=?;");
                        ps1.AddParam(user);
                        JNekoSP.GetSP().SQLInsertUpdate(ps1);
                        
                        MsgBox.Show("Пользователь", "Отвязка роутера "+ont+" от пользователя #"+user+" осуществлена успешно.");
                    }
                    break;
                case JNekoONTElementA.BUTTON_ONT_INFO:
                    ONTInfo.Show(ont, 0);
                    break;
            }
        }
    };
    
    @SuppressWarnings("Convert2Lambda")
    private final XListSearchFormActionListener SearchListener = new XListSearchFormActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID, String searchString) {
            if (buttonID == 1777) {
                Thread t = new Thread(() -> {
                    JNekoProgressDisplayClass.On();
                
                    JNekoSPBackground.jNekoServerInfo.CollectONTAndMACInfo();
                    try {
                        SwingUtilities.invokeAndWait(() -> {
                            Refresh();
                        });
                    } catch (InterruptedException | InvocationTargetException ex) { }
                
                    JNekoProgressDisplayClass.Off();
                });
                t.start();
            }
            
            if ((buttonID == 1005) || (buttonID == 1007)) {
                if (searchString.length() > 0) xList1.Search("ALLONTS", searchString); else xList1.Search("ALLONTS", null);
            }
            
            if (buttonID == 1006) {
//                Thread t = new Thread(() -> {
//                    JNekoProgressDisplayClass.On();
//                    MA4000Wrappers.CorrectONTProfile();
//                    JNekoProgressDisplayClass.Off();
//                });
//                t.start();
            }
        }
    };
    
    public AllOnlineONTs(Color c) {
        initComponents();
        this.setBackground(c);
        xList1.setBackgroundColor(c);
        xList1.VPBON();
        color = c;
        Refresh();
    }

    private String ref_eltx, ref_slot, ref_ip, ref_mac, ref_user, ref_sysusr;
    public void Refresh() {
        xList1.ClearAll();
        xList1.AddGroupHeader("SLOTSEL", color, "Поиск", groupIcon, 0);
        xList1.AddItem("SF1", "SLOTSEL", new GlobalSearchFormElement(true, "SF1", "SEARCHBOX", SearchListener), "", 0);
        xList1.AddGroupHeader("ALLONTS", color, "Полный список ONT", xList1.ICON_PAYMENTS, 0);
        
        ConcurrentHashMap<String, JNekoServerInfo.ONTInfo> oi = JNekoSPBackground.jNekoServerInfo.ONTsInfo;
        Set<String> oikeys = oi.keySet();
        if (oi.size() <= 0) {
            xList1.AddItemForSimpleList("NOITEMS", "ALLONTS", "Нет данных для отображения.", groupIcon);
            xList1.Commit(true);
            return;
        }
            
        Thread t = new Thread(() -> {
            JNekoProgressDisplayClass.On();
            
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
                        + "ORDER BY Users.did DESC;");

            CopyOnWriteArrayList<JNekoServerInfo.DHCPLease> dhcpleases = JNekoSPBackground.jNekoServerInfo.GetLeases();

            for (String oikey : oikeys) {
                JNekoServerInfo.ONTInfo oielement = oi.get(oikey);
                ref_eltx    = oielement.ELTX;
                ref_slot    = oielement.SLOT;
                ref_ip      = "";
                ref_mac     = (oielement.GetMACs().size() > 0) ? oielement.GetMACs().get(0) : "";
                ref_user    = "";
                ref_sysusr  = oielement.SYSUSR;

                for (JNekoServerInfo.DHCPLease lease : dhcpleases) {
                    if (lease.ELTX.equalsIgnoreCase(ref_eltx)) {
                        ref_ip      = lease.IP;
                        ref_mac     = lease.MAC;
                        ref_user    = lease.USER;
                        ref_eltx    = lease.ELTX;
                        ref_slot    = lease.SLOT;
                    }
                }

                if ((ref_ip.length() < 7) && (stx != null)) {
                    for (Map<String,String> stxe : stx) {
                        if (stxe.get("eltx").contentEquals(ref_eltx)) {
                            ref_ip      = stxe.get("ip");
                            ref_mac     = stxe.get("mac");
                            ref_user    = stxe.get("userFullName");
                            break;
                        }
                    }
                }

                /* Этот закомментированный блок используется для перегенерации файла dhcpd.leases в случае его случайного удаления (например, откатом виртуалки) */
//                final String xxxx = 
//                          "lease " + ref_ip + " {"
//                        + "\n\tstarts 5 2015/01/23 20:00:00;"
//                        + "\n\tends 6 2024/01/01 20:00:00;"
//                        + "\n\tbinding state active;" 
//                        + "\n\tnext binding state free;" 
//                        + "\n\trewind binding state free;"
//                        + "\n\thardware ethernet " + ref_mac + ";\n}\n";
//                if (ref_ip.length() > 7) System.err.println(xxxx);
                
                
                final String SearchTags = ref_ip + " " + ref_mac + " " + ref_user + " " + ref_eltx + " " + ref_slot + " " + ref_sysusr;

                try {
                    SwingUtilities.invokeAndWait(() -> {
                        JNekoONTElementA ontelement = new JNekoONTElementA(ref_eltx, ref_slot, ref_ip, ref_mac, ref_user, ref_sysusr, ButtonsListener);
                        xList1.AddItem(ref_eltx, "ALLONTS", ontelement, SearchTags, 0);
                    });
                } catch (InterruptedException | InvocationTargetException ex) { }
            }

            try {
                SwingUtilities.invokeAndWait(() -> {
                    xList1.Commit(true);
                });
            } catch (InterruptedException | InvocationTargetException ex) { }

            JNekoProgressDisplayClass.Off();
        });
        t.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        xList1 = new xlist.XList();

        setBackground(new java.awt.Color(1, 1, 1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xList1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(xList1, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private xlist.XList xList1;
    // End of variables declaration//GEN-END:variables
}
