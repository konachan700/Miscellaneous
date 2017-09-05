package jneko;

import connections.JNekoSP;
import connections.MA4000Wrappers;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import xlistcustomelements.MA4000SimpleONTElement;
import xlist.GlobalSearchFormElement;
import xlistcustomelements.MA4000FullONTElement;
import xlistcustomelements.MA4000SlotSelectorElement;
import xlist.XListButtonsActionListener;
import xlist.XListSearchFormActionListener;

public class JNekoIncludeAllONTOnSlot extends javax.swing.JPanel {
    private final ImageIcon groupIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/a2e.png"));
    private final JNekoIncludeAllONTOnSlot THIS = this;
    private final Color color;
    private final xlist.XList XL;
    private int TypeSelector = 0, GSlot = -1;
    private final String MAC_PATTERN = "[A-Fa-f0-9]{1,2}\\:[A-Fa-f0-9]{1,2}\\:[A-Fa-f0-9]{1,2}\\:[A-Fa-f0-9]{1,2}\\:[A-Fa-f0-9]{1,2}\\:[A-Fa-f0-9]{1,2}";

    @SuppressWarnings("Convert2Lambda")
    private final XListButtonsActionListener xbi = new XListButtonsActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID) {
            if (buttonID < 4000) Refresh(buttonID-3000);
            
//            if (buttonID == 4001) {
//                
//            }
//            
//            if (buttonID == 4002) {
//                
//            }
            if (((TypeSelector == 0) || (TypeSelector == 3)) && (buttonID == 4002)) {
                if (ID == null) return;
                
                final int q = JOptionPane.showConfirmDialog(THIS, "Полностью удалить "+ID+" из системы? Вы уверенны?","Полностью удалить "+ID+" из системы?", JOptionPane.YES_NO_OPTION);
                if (q == JOptionPane.YES_OPTION) {
                   Thread t = new Thread(() -> {
                       JNekoProgressDisplayClass.On();
                       
                       if (TypeSelector == 0) MA4000Wrappers.RemoveONT(ID, GSlot+"");
                       if (TypeSelector == 3) MA4000Wrappers.RemoveUser(ID);
                       
                       JNekoProgressDisplayClass.Off();
                       JOptionPane.showConfirmDialog(THIS, "Удалено успешно (или нет)", "Удаление", JOptionPane.INFORMATION_MESSAGE);
                       Refresh(GSlot);
                    });
                    t.start();
                }
            }

            if (((TypeSelector == 0) || (TypeSelector == 1)) && (buttonID == 4003)) {
                if (GID == null) return;
                final int q = JOptionPane.showConfirmDialog(THIS, "Сбросить устройство "+ID+" до стандартных настроек? Вы уверенны?","Сбросить устройство "+ID+" до стандартных настроек?", JOptionPane.YES_NO_OPTION);
                if (q == JOptionPane.YES_OPTION) {
                    Thread t = new Thread(() -> {
                        JNekoProgressDisplayClass.On();
                        if (MA4000Wrappers.ONTDefault(ID, GID) == 0) {
                            JOptionPane.showConfirmDialog(THIS, "Сброс выполнен успешно", "Сброс настроек", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showConfirmDialog(THIS, "Сброс не выполнен", "Сброс настроек", JOptionPane.ERROR_MESSAGE);
                        }
                        JNekoProgressDisplayClass.Off();
                    });
                    t.start();
                }
            }
            
            if ((TypeSelector == 2) && (buttonID == 5002)) {
                new JNekoFormONTACSInfo(ID, 0).setVisible(true);
            }
            
            if ((TypeSelector == 3) && (buttonID == 4001)) {
                new JNekoFormONTACSInfo(ID, 1).setVisible(true);
            }
            
            if ((TypeSelector == 4) && (buttonID == 4001)) {
                new JNekoFormONTACSInfo(ID, 0, 1).setVisible(true);
            }
        }
    };
    
    @SuppressWarnings("Convert2Lambda")
    private final XListSearchFormActionListener search_listener=new XListSearchFormActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID, String searchString) {
            if ((buttonID == 1005) || (buttonID == 1007)) {
                if (searchString.length() > 0) xList1.Search("ALLONTS", searchString); else xList1.Search("ALLONTS", null);
            }
            
            if (buttonID == 1777) {
                Refresh(GSlot);
            }
        }
    };
    
    public JNekoIncludeAllONTOnSlot(Color c, xlist.XList lockSelector, int typeSelector) {
        initComponents();
        color = c;
        xList1.VPBON();
        xList1.setBackgroundColor(c); 
        XL = lockSelector;
        TypeSelector = typeSelector;
        Refresh(-1);
    }
    
    public final void Refresh(int slot) {
        xList1.ClearAll();
        xList1.Commit(true);
        
        if (TypeSelector < 2) {
            xList1.AddGroupHeader("SLOTSEL", color, "Выберите слот", groupIcon, 0);
            xList1.AddItem("SLOTSELCOMP", "SLOTSEL", new MA4000SlotSelectorElement(xbi));
        } else {
            xList1.AddGroupHeader("SLOTSEL", color, "Поиск", groupIcon, 0);
        }
        //xList1.AddGroupHeader("SLOTSEARCH", color, "Поиск ONT", groupIcon, 0);
        xList1.AddItem("SF1", "SLOTSEL", new GlobalSearchFormElement(false, "SF1", "SEARCHBOX", search_listener), "", 0);
        xList1.AddGroupHeader("ALLONTS", color, "Список всех ONT"+(((slot >= 0) && (slot < 9)) ? (" на слоте #" + slot) : ""), groupIcon, 0);
        XL.setEnabled(false);
        
        if (((slot >= 0) && (slot < 9)) || (TypeSelector >= 2)) {
            GSlot = slot;
            Thread t = new Thread(() -> {
                JNekoProgressDisplayClass.On();
                
                String sa[][] = null;
                switch (TypeSelector) {
                    case 0:
                        sa = MA4000Wrappers.GetAllONTsOnSlot(slot);
                        if (sa != null) {
                            for (String ss[] : sa) {
                                xList1.AddItem(ss[1], "ALLONTS", new MA4000SimpleONTElement(ss[1], "#"+ss[0]+"; ID:"+ss[2], (ss[5].contains("OK")) ? slot+"" : null, true, xbi), ss[1], 0);
                            } 
                        }
                        break;
                    case 1:
                        sa = MA4000Wrappers.GetAllONTsMACsOnSlot(slot);
                        if (sa != null) {
                            for (String ss[] : sa) {
                                String mac = "";
                                for (String s : ss) {
                                    if (s.matches(MAC_PATTERN)) { mac = s; break; }
                                }
                                xList1.AddItem(ss[1], "ALLONTS", new MA4000SimpleONTElement(ss[1], mac, slot+"", false, xbi), ss[1] + "\n" + mac, 0);
                            }
                        }
                        break;
                    case 2:
                        sa = MA4000Wrappers.GetAllONTsOnACS();
                        if (sa != null) {
                            for (String ss[] : sa) {
                                xList1.AddItem(ss[1], "ALLONTS", 
                                        new MA4000FullONTElement(ss[1], 
                                                ss[5].replace("http:", "").replace("/", "").replaceAll("[\\:][0-9]{1,7}", ""), ss[3],
                                                "Last online: "+ss[6]+""+ss[7], false, xbi), 
                                        ss[1] + "\n" + ss[2] + "\n" + ss[3] + "\n" + ss[4] + "\n" + ss[5], 0);
                            }
                        }
                        break;
                    case 3:
                        sa = MA4000Wrappers.GetAllONTsOnACSUsers();
                        if (sa != null) {
                            for (String ss[] : sa) {
                                String ONT = (ss.length == 3) ? ((ss[2].length() <= 1) ? "Пользователю ONT не назначена" : ss[2]) : "Пользователю ONT не назначена";
                                xList1.AddItem(ss[1]+"ID", "ALLONTS", 
                                        new MA4000SimpleONTElement(ss[1], ONT, true, true, xbi), ss[1] + "\n" + ONT, 0);
                            }
                        }
                        break;
                    case 4:
//                        sa = sMySQLWarappers.GetFullTable("LastAddedONT");
                        JNekoSP sp = new JNekoSP();
                        sa = sp.GetFullTable("LastAddedONT");
                        sp.CloseConnection();
                        if (sa != null) {
                            for (String ss[] : sa) {
                                xList1.AddItem(ss[0], "ALLONTS", new MA4000SimpleONTElement(ss[1], "Slot #"+ss[2], true, false, xbi), ss[1], 0);
                            } 
                        }
                        break;
                }

                xList1.Commit(true);
                JNekoProgressDisplayClass.Off();
                XL.setEnabled(true);
            });
            t.start();
        } else {
            xList1.AddItemForSimpleList("NOITEMS", "ALLONTS", "Нет данных для отображения, выберите другой слот.", groupIcon);
            xList1.Commit(true);
            XL.setEnabled(true);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        xList1 = new xlist.XList();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xList1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xList1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private xlist.XList xList1;
    // End of variables declaration//GEN-END:variables
}
