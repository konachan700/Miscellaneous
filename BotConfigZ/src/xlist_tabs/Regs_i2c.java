package xlist_tabs;

import botconfigz.BotConfigZ;
import botconfigz.JNekoSQLite;
import botconfigz.LStrings;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.ImageIcon;
import xlist.XListButtonsActionListener;

public class Regs_i2c extends javax.swing.JPanel {
    private final ImageIcon ICON_CATEGORY    = new javax.swing.ImageIcon(getClass().getResource("/icons16/category.png"));
    private final ImageIcon ICON_STAR        = new javax.swing.ImageIcon(getClass().getResource("/icons16/star.png"));
    
    private final String            
            STRING_NO_GROUPS                = "Нет доступных групп, создайте хотя бы одну.",
            STRING_REGISTER_LIST_HEADER     = "Список групп регистров",
            STRING_ADD                      = "Добавить",
            STRING_CLEAR                    = "Очистить",
            STRING_ADD_REG_GROUP            = "Добавить группу регистров",
            STRING_ADD_REG_GNAME            = "Имя группы регистров"
            ;
    
    @SuppressWarnings("Convert2Lambda")
    private final XListButtonsActionListener xbi = new XListButtonsActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID) {
            switch (buttonID) {
                case xlist.XList.GYNBE_YES_BUTTON:
                    final String name = xList1.GetSettingsTextBoxElement(LStrings.I2C_REGISTER_DEV_MAIN_GID, "i2c_name").GetValue();
                    if ((name.length() > 1) && (name.length() < 32)) {
                        JNekoSQLite.WAddNewI2CRegGroup(name);
                        xList1.GetSettingsTextBoxElement(LStrings.I2C_REGISTER_DEV_MAIN_GID, "i2c_name").SetValue("");
                        __refresh();
                    }
                    break;
                case xlist.XList.GYNBE_NO_BUTTON: 
                    xList1.GetSettingsTextBoxElement(LStrings.I2C_REGISTER_DEV_MAIN_GID, "i2c_name").SetValue("");
                    __refresh();
                    break;
                case xlist.XList.SLEA_DELETE_BUTTON:
                    final String DID = ID.replace(LStrings.I2C_REGISTER_DEV_I2C_ID_SIGN, "");
                    JNekoSQLite.WDeleteI2CRegGroup(DID);
                    __refresh();
                    break;
                case xlist.XList.SLEA_OPTIONS_BUTTON:
                    final String gDID = ID.replace(LStrings.I2C_REGISTER_DEV_I2C_ID_SIGN, "");
                    BotConfigZ.xReg_i2c.Refresh(gDID);
                    BotConfigZ.GetMF().Show_panel(BotConfigZ.xReg_i2c);
                    break;
            }
        }
    };
    
    public Regs_i2c(Color c) {
        initComponents();
        this.setBackground(c);
        xList1.setBackgroundColor(c);
        
        xList1.ClearAll();
        
        xList1.AddGroupHeader(LStrings.I2C_REGISTER_DEV_MAIN_GID, c, STRING_ADD_REG_GROUP, ICON_CATEGORY, 0);
        xList1.AddSettingsElementTextNS("i2c_name", LStrings.I2C_REGISTER_DEV_MAIN_GID, STRING_ADD_REG_GNAME, "");
        xList1.AddSettingsElement2Buttons("i2c_btns", LStrings.I2C_REGISTER_DEV_MAIN_GID, STRING_ADD, STRING_CLEAR, xbi);       
        xList1.AddGroupHeader(LStrings.I2C_REGISTER_DEV_LIST_GID, c, STRING_REGISTER_LIST_HEADER, ICON_CATEGORY, 0);
        
        __refresh();
    }
    
    private void __refresh() {
        xList1.ClearGroup(LStrings.I2C_REGISTER_DEV_LIST_GID);
        
        final Map<String, ArrayList<String>> list = JNekoSQLite.GetTableFull("APP_i2c_regs_groups", "zstate>0");
        if (list == null) {
            xList1.AddItemForSimpleList(LStrings.I2C_REGISTER_DEV_LIST_NONE_ID, LStrings.I2C_REGISTER_DEV_LIST_GID, STRING_NO_GROUPS, ICON_STAR);
        } else {
            if (list.size() <= 0) {
                xList1.AddItemForSimpleList(LStrings.I2C_REGISTER_DEV_LIST_NONE_ID, LStrings.I2C_REGISTER_DEV_LIST_GID, STRING_NO_GROUPS, ICON_STAR);
            } else {
                final ArrayList<String> cols = new ArrayList<>(list.keySet());
                final int count = list.get(cols.get(0)).size();
                if (count <= 0) {
                    xList1.AddItemForSimpleList(LStrings.I2C_REGISTER_DEV_LIST_NONE_ID, LStrings.I2C_REGISTER_DEV_LIST_GID, STRING_NO_GROUPS, ICON_STAR);
                } else {
                    for (int i=0; i<count; i++) {
                        xList1.AddSimpleElementA(LStrings.I2C_REGISTER_DEV_I2C_ID_SIGN + list.get("did").get(i), LStrings.I2C_REGISTER_DEV_LIST_GID, list.get("zname").get(i), "", xbi);
                    }
                }
            }
        }
        
        xList1.VPBON();
        xList1.Commit();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        xList1 = new xlist.XList();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xList1, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xList1, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private xlist.XList xList1;
    // End of variables declaration//GEN-END:variables
}
