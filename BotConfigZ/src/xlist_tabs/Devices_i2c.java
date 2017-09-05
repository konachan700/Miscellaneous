package xlist_tabs;

import botconfigz.BotConfigZ;
import botconfigz.JNekoSQLite;
import botconfigz.LStrings;
import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.ImageIcon;
import xlist.SettingsI2CAddressBoxElement;
import xlist.XListButtonsActionListener;

public class Devices_i2c extends javax.swing.JPanel {
    private final ImageIcon ICON_CATEGORY    = new javax.swing.ImageIcon(getClass().getResource("/icons16/category.png"));
    private final ImageIcon ICON_STAR        = new javax.swing.ImageIcon(getClass().getResource("/icons16/star.png"));
    
    private final String DEV_I2C_ID_SIGN = LStrings.I2C_REGISTER_DEV_I2C_ID_SIGN;
    
    @SuppressWarnings("Convert2Lambda")
    private final XListButtonsActionListener xbi = new XListButtonsActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID) {
            switch (buttonID) {
                case xlist.XList.GYNBE_YES_BUTTON:
                    if (xList1.GetSettingsComboBoxElement("i2cdev", "i2c_type").GetIndex() > 0) {
                        final String 
                                name = xList1.GetSettingsTextBoxElement("i2cdev", "i2c_name").GetValue(),
                                type = xList1.GetSettingsComboBoxElement("i2cdev", "i2c_type").GetValue();
                        if ((name.length() < 2) || (name.length() > 31)) return;
                        
                        JNekoSQLite.WAddNewI2CDevice(name, type, xList1.GetSettingsI2CAddressBoxElement("i2cdev", "i2c_addr").GetLongValue());
                        xList1.GetSettingsComboBoxElement("i2cdev", "i2c_type").ResetSelection();
                        xList1.GetSettingsTextBoxElement("i2cdev", "i2c_name").SetValue("");
                        //xList1.GetSettingsElementNumber("i2cdev", "i2c_addr").SetLongValue(0);
                        __refresh();
                    }
                    
                    break;
                case xlist.XList.GYNBE_NO_BUTTON: 
                    xList1.GetSettingsComboBoxElement("i2cdev", "i2c_type").ResetSelection();
                    xList1.GetSettingsTextBoxElement("i2cdev", "i2c_name").SetValue("");
                   // xList1.GetSettingsElementNumber("i2cdev", "i2c_addr").SetLongValue(0);
                    __refresh();
                    break;
                case xlist.XList.SLEA_DELETE_BUTTON:
                    final String DID = ID.replace(DEV_I2C_ID_SIGN, "");
                    JNekoSQLite.WDeleteI2CDevice(DID);
                    __refresh();
                    break;
                case xlist.XList.SLEA_OPTIONS_BUTTON:
                    final String gDID = ID.replace(LStrings.I2C_REGISTER_DEV_I2C_ID_SIGN, "");
                    BotConfigZ.xDevice_i2c.Refresh(gDID);
                    BotConfigZ.GetMF().Show_panel(BotConfigZ.xDevice_i2c);
                    break;
            }
        }
    };
    
    public Devices_i2c(Color c) {
        initComponents();
        this.setBackground(c);
        xList1.setBackgroundColor(c);
        
        xList1.ClearAll();
        
        final Map<String,String> data = new LinkedHashMap<>();
        data.put(LStrings.I2C_DEVICES_TYPE_PCA9685, LStrings.I2C_DEVICES_TYPE_PCA9685);
        
        xList1.AddGroupHeader("i2cdev", c, "Добавить i2c устройство", ICON_CATEGORY, 0);
        xList1.AddSettingsElementTextNS("i2c_name", "i2cdev", "Имя устройства", "");
        xList1.AddSettingsI2CAddressBoxElement("i2c_addr", "i2cdev", "i2c адрес");
        xList1.AddSettingsElementComboBoxNS("i2c_type", "i2cdev", "Тип устройства", data, 0);
        xList1.AddSettingsElement2Buttons("i2c_btns", "i2cdev", "Добавить", "Очистить", xbi);
        
        xList1.AddGroupHeader("i2clist", c, "Список имеющихся устройств", ICON_CATEGORY, 0);

        __refresh();
    }
    
    private void __refresh() {
        xList1.ClearGroup("i2clist");
        
        final Map<String, ArrayList<String>> list = JNekoSQLite.GetTableFull("APP_i2c_devs");
        if (list == null) {
            xList1.AddItemForSimpleList("i2c_none", "i2clist", "Активных устройств не найдено.", ICON_STAR);
        } else {
            if (list.size() <= 0) {
                xList1.AddItemForSimpleList("i2c_none", "i2clist", "Активных устройств не найдено.", ICON_STAR);
            } else {
                final ArrayList<String> cols = new ArrayList<>(list.keySet());
                final int count = list.get(cols.get(0)).size();
                if (count <= 0) {
                    xList1.AddItemForSimpleList("i2c_none", "i2clist", "Активных устройств не найдено.", ICON_STAR);
                } else {
                    for (int i=0; i<count; i++) {
                        xList1.AddSimpleElementA(DEV_I2C_ID_SIGN + list.get("did").get(i), "i2clist", list.get("zname").get(i), list.get("ztype").get(i) + " addr:" +  list.get("i2c_addr").get(i), xbi);
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
