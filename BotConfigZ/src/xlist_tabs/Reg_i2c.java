package xlist_tabs;

import botconfigz.JNekoSQLite;
import botconfigz.LStrings;
import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import xlist.XListButtonsActionListener;

public class Reg_i2c extends javax.swing.JPanel {
    private final ImageIcon ICON_CATEGORY    = new javax.swing.ImageIcon(getClass().getResource("/icons16/category.png"));
    private final ImageIcon ICON_STAR        = new javax.swing.ImageIcon(getClass().getResource("/icons16/star.png"));
    
    private String zDID = null;
    
    private final String 
            STRING_NO_GROUPS                = "Нет ни одного регистра, создайте хотя бы один.",
            STRING_REGISTER_LIST_HEADER     = "Список регистров группы",
            STRING_ADD                      = "Добавить",
            STRING_CLEAR                    = "Очистить",
            STRING_ADD_REG_GROUP            = "Добавить регистр в группу",
            STRING_ADD_REG_GNAME            = "Имя регистра",
            STRING_ADD_REG_MIN              = "Минимальное значение",
            STRING_ADD_REG_MAX              = "Максимальное значение",
            STRING_ADD_REG_INIT             = "Стартовое значение",
            STRING_ADD_REG_TYPE             = "Тип регистра",
            STRING_ADD_REG_ADDR             = "Адрес регистра"
            ;
    
    @SuppressWarnings("Convert2Lambda")
    private final XListButtonsActionListener xbi = new XListButtonsActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID) {
            switch (buttonID) {
                case xlist.XList.GYNBE_YES_BUTTON:
                    final String    
                            name = xList1.GetSettingsTextBoxElement(LStrings.I2C_REGISTER_DEV_MAIN_GID, "i2c_name").GetValue(),
                            type = xList1.GetSettingsComboBoxElement(LStrings.I2C_REGISTER_DEV_MAIN_GID, "i2c_reg_type").GetValue();
                    final int       
                            addr = xList1.GetSettingsHexNumberBoxElement(LStrings.I2C_REGISTER_DEV_MAIN_GID, "i2c_reg_addr").GetValue();
                    final long  
                            min     = xList1.GetSettingsElementNumber(LStrings.I2C_REGISTER_DEV_MAIN_GID, "i2c_reg_min").GetLongValue(),
                            max     = xList1.GetSettingsElementNumber(LStrings.I2C_REGISTER_DEV_MAIN_GID, "i2c_reg_max").GetLongValue(),
                            init    = xList1.GetSettingsElementNumber(LStrings.I2C_REGISTER_DEV_MAIN_GID, "i2c_reg_init").GetLongValue();
                    
                    if ((name.length() <= 1) || (name.length() >= 32)) return;
                    if ((type.length() <= 1) || (type.length() >= 16)) return;
                    if (zDID == null) return;
                    
                    //(String name, String type, int addr, long min, long max, long init, String gid)
                    JNekoSQLite.WAddNewI2CReg(name, type, addr, min, max, init, zDID, 0);

                    Refresh(zDID);
                    break;
                case xlist.XList.GYNBE_NO_BUTTON: 
                    if (zDID == null) return;
                    xList1.GetSettingsTextBoxElement(LStrings.I2C_REGISTER_DEV_MAIN_GID, "i2c_name").SetValue("");
                    xList1.GetSettingsComboBoxElement(LStrings.I2C_REGISTER_DEV_MAIN_GID, "i2c_reg_type").ResetSelection();
                    Refresh(zDID);
                    break;
                case xlist.XList.SLEA_DELETE_BUTTON:
                    if (zDID == null) return;
                    final String DID = ID.replace(LStrings.I2C_REGISTER_DEV_I2C_ID_SIGN, "");
                    JNekoSQLite.WDeleteI2CReg(DID);
                    Refresh(zDID);
                    break;
                case xlist.XList.SLEA_OPTIONS_BUTTON:
                    
                    break;
            }
        }
    };
    
    public Reg_i2c(Color c) {
        initComponents();
        this.setBackground(c);
        xList1.setBackgroundColor(c);
        xList1.ClearAll();
        
        final Map<String,String> data = new LinkedHashMap<>();
        data.put(LStrings.I2C_REGISTER_TYPE_8BIT, LStrings.I2C_REGISTER_TYPE_8BIT);
        data.put(LStrings.I2C_REGISTER_TYPE_16BIT_LF, LStrings.I2C_REGISTER_TYPE_16BIT_LF);
        data.put(LStrings.I2C_REGISTER_TYPE_16BIT_HF, LStrings.I2C_REGISTER_TYPE_16BIT_HF);
        
        xList1.AddGroupHeader(LStrings.I2C_REGISTER_DEV_MAIN_GID, c, STRING_ADD_REG_GROUP, ICON_CATEGORY, 0);
        xList1.AddSettingsElementTextNS("i2c_name", LStrings.I2C_REGISTER_DEV_MAIN_GID, STRING_ADD_REG_GNAME, "");
        xList1.AddSettingsHexNumberBoxElementNS("i2c_reg_addr", LStrings.I2C_REGISTER_DEV_MAIN_GID, STRING_ADD_REG_ADDR, 0, 0, 0xFFFF);
        xList1.AddSettingsElementNumberNS("i2c_reg_min", LStrings.I2C_REGISTER_DEV_MAIN_GID, STRING_ADD_REG_MIN, 0, 0, 0xFFFF);
        xList1.AddSettingsElementNumberNS("i2c_reg_max", LStrings.I2C_REGISTER_DEV_MAIN_GID, STRING_ADD_REG_MAX, 0, 0, 0xFFFF);
        xList1.AddSettingsElementNumberNS("i2c_reg_init", LStrings.I2C_REGISTER_DEV_MAIN_GID, STRING_ADD_REG_INIT, 0, 0, 0xFFFF);
        xList1.AddSettingsElementComboBoxNS("i2c_reg_type", LStrings.I2C_REGISTER_DEV_MAIN_GID, STRING_ADD_REG_TYPE, data, 0);
        xList1.AddSettingsElement2Buttons("i2c_btns", LStrings.I2C_REGISTER_DEV_MAIN_GID, STRING_ADD, STRING_CLEAR, xbi);       
        xList1.AddGroupHeader(LStrings.I2C_REGISTER_DEV_LIST_GID, c, STRING_REGISTER_LIST_HEADER, ICON_CATEGORY, 0);
        xList1.AddItemForSimpleList(LStrings.I2C_REGISTER_DEV_LIST_NONE_ID, LStrings.I2C_REGISTER_DEV_LIST_GID, STRING_NO_GROUPS, ICON_STAR);
        xList1.VPBON();
        xList1.Commit();
    }
    
    public void Refresh(String gid) {
        zDID = gid;
        xList1.ClearGroup(LStrings.I2C_REGISTER_DEV_LIST_GID);
        
        final Map<String, ArrayList<String>> list = JNekoSQLite.GetTableFull("APP_i2c_regs", "zstate>0 AND gid="+gid);
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
                        //did bigint, gid bigint, zname char(32), zaddr bigint, zmin bigint, zmax bigint, zinit bigint, ztype char(16), zstate bigint
                        final String namex = 
                                "addr:0x"+(Integer.toHexString(Integer.parseInt(list.get("zaddr").get(i), 10)).toUpperCase())+"; min:"+list.get("zmin").get(i)+"; max:"+list.get("zmax").get(i)+"; init:"+list.get("zinit").get(i)+"; type:"+list.get("ztype").get(i)+";";
                        xList1.AddSimpleElementA(LStrings.I2C_REGISTER_DEV_I2C_ID_SIGN + list.get("did").get(i), LStrings.I2C_REGISTER_DEV_LIST_GID, namex, list.get("zname").get(i), xbi, false, true);
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
