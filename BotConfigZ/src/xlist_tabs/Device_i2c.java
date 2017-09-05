package xlist_tabs;

import botconfigz.BotConfigZ;
import botconfigz.JNekoSQLite;
import botconfigz.LStrings;
import botconfigz.RS232;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.ImageIcon;
import xlist.SettingsI2CAddressBoxElement;
import xlist.XListButtonsActionListener;
import xlist.XListSliderElementActionListener;

public class Device_i2c extends javax.swing.JPanel {
    private final ImageIcon ICON_CATEGORY    = new javax.swing.ImageIcon(getClass().getResource("/icons16/category.png"));
    private final ImageIcon ICON_STAR        = new javax.swing.ImageIcon(getClass().getResource("/icons16/star.png"));
    
    private String xxGID = "";
    private int i2c_ADDR = 0;
    
        @SuppressWarnings("Convert2Lambda")
    private final XListButtonsActionListener zbi = new XListButtonsActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID) {
            
        }
    };
    
    @SuppressWarnings("Convert2Lambda")
    private final XListButtonsActionListener xbi = new XListButtonsActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID) {
            if (xList1.GetSettingsComboBoxElement(LStrings.I2C_REGISTER_DEV_MAIN_GID, "i2c_type").GetIndex() <= 0) return;
            
            final String result = xList1.GetSettingsComboBoxElement(LStrings.I2C_REGISTER_DEV_MAIN_GID, "i2c_type").GetAssocValue();
//            if (xList1.GetItem(LStrings.I2C_REGISTER_DEV_LIST_GID, LStrings.I2C_REGISTER_DEV_I2C_ID_SIGN+result) != null) return;
            
            switch (buttonID) {
                case xlist.XList.GYNBE_YES_BUTTON:
                    JNekoSQLite.WAddNewI2CLink(xxGID, result); 
                    break;
                case xlist.XList.GYNBE_NO_BUTTON: 

                    break;
                case xlist.XList.COMBO_BOX_BTN1: 
                    BotConfigZ.xReg_i2c.Refresh(result); 
                    BotConfigZ.ShowDialog(BotConfigZ.xReg_i2c); 
                    break;
            }
            
            Refresh(xxGID);
        }
    };
    
    final XListSliderElementActionListener sl_li = new XListSliderElementActionListener() {
        @Override
        public void OnStateChange(String GID, String ID, int xaddr, int min, int max, int init, String type, int currentValue) {
            /*
                #define USART_CMD_H_I2C_PCA9685 0x01
                #define USART_CMD_H_I2C_PCA9685_DADDR 0x00
                #define USART_CMD_H_I2C_PCA9685_RADDR 0x01
                #define USART_CMD_H_I2C_PCA9685_DATAL 0x02
                #define USART_CMD_H_I2C_PCA9685_DATAH 0x03
            */
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            b.write((i2c_ADDR & 0xFF));
            b.write((xaddr & 0xFF));
            b.write((currentValue & 0xFF));
            b.write(((currentValue >> 8) & 0xFF));
            RS232.writeCmd16b(b, 0, 1); 
        }

        /**
        #define USART_CMD_H_I2C_SERVO				0x02
	#define USART_CMD_L_I2C_SERVO_SET_DEFAULT		0x01
	#define USART_CMD_L_I2C_SERVO_SET_MAX			0x02
	#define USART_CMD_L_I2C_SERVO_SET_MIN			0x03
	#define USART_CMD_L_I2C_SERVO_SET_INDEX			0x04
		#define USART_CMD_I2C_SERVO_I2C_DADDR		0x00
		#define USART_CMD_I2C_SERVO_I2C_RADDR		0x01
		#define USART_CMD_I2C_SERVO_I2C_INDEX		0x02
	#define USART_CMD_L_I2C_SERVO_SET_ALL			0x05
		#define USART_CMD_I2C_SERVO_BLOCK			0x00
		#define USART_CMD_I2C_SERVO_INDEX			0x01
		#define USART_CMD_I2C_SERVO_CONF			0x01
			#define USART_CMD_I2C_SERVO_CONF_ISADDR	1
		#define USART_CMD_I2C_SERVO_ONE_VALUE_H		0x03
		#define USART_CMD_I2C_SERVO_ONE_VALUE_L		0x04
		#define USART_CMD_I2C_SERVO_MAX_H			0x03
		#define USART_CMD_I2C_SERVO_MAX_L			0x04
		#define USART_CMD_I2C_SERVO_MIN_H			0x05
		#define USART_CMD_I2C_SERVO_MIN_L			0x06
		#define USART_CMD_I2C_SERVO_DEFAULT_H		0x07
		#define USART_CMD_I2C_SERVO_DEFAULT_L		0x08
         */
        
        @Override
        public void OnWriteIndexButtonClicked(String GID, String ID, int addr, int min, int max, int init, String type, int currentValue) {
            JNekoSQLite.WIndexI2CReg(ID.replace(LStrings.I2C_REGISTER_DEV_I2C_ID_SIGN, ""), currentValue);
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            b.write((i2c_ADDR & 0xFF));
            b.write((addr & 0xFF));
            b.write((currentValue & 0xFF));
            RS232.writeCmd16b(b, 0x04, 0x02);
        }

        @Override
        public void OnWriteMMIButtonClicked(String GID, String ID, int addr, int min, int max, int init, String type, int currentValue, int index) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            b.write((i2c_ADDR & 0xFF));
            b.write((index & 0xFF));
            b.write(1);
            b.write((max >> 8) & 0xFF);
            b.write(max & 0xFF);
            b.write((min >> 8) & 0xFF);
            b.write(min & 0xFF);
            b.write((currentValue >> 8) & 0xFF);
            b.write(currentValue & 0xFF);
            RS232.writeCmd16b(b, 0x05, 0x02);
        }
    };
    
    public Device_i2c(Color c) {
        initComponents();
        this.setBackground(c);
        xList1.setBackgroundColor(c);
    }
    
    public void Refresh(String GID) {
        xList1.ClearAll();
        xxGID = GID;
        
        final Map<String, ArrayList<String>> this_r = JNekoSQLite.GetTableFull("APP_i2c_devs", "did="+xxGID);
        i2c_ADDR = Integer.parseInt(this_r.get("i2c_addr").get(0), 10);
        
        final Map<String,String> data = new LinkedHashMap<>();
//        data.put(" -- not selected --", " -- not selected --");
        final Map<String, ArrayList<String>> list = JNekoSQLite.GetTableFull("APP_i2c_regs_groups", "zstate>0");
        if (list != null) {
            final int zcount = list.get("zname").size();
            if (zcount > 0) {
                for (int i=0; i<zcount; i++) {
                    data.put(list.get("zname").get(i), list.get("did").get(i));
                }
            }
        }
        
        xList1.AddGroupHeader(LStrings.I2C_REGISTER_DEV_MAIN_GID, this.getBackground(), "Добавить регистры", ICON_CATEGORY, 0);
        xList1.AddSettingsElementComboBoxNS("i2c_type", LStrings.I2C_REGISTER_DEV_MAIN_GID, "Группа регистров", data, 0, xbi);
        xList1.AddSettingsElement2Buttons("i2c_btns", LStrings.I2C_REGISTER_DEV_MAIN_GID, "Добавить", "Удалить", xbi);
        xList1.AddGroupHeader(LStrings.I2C_REGISTER_DEV_LIST_GID, this.getBackground(), "Список регистров ADDR#"+i2c_ADDR, ICON_CATEGORY, 0);

        //'APP_i2c_regs_links'(did bigint, regid bigint, devid bigint)
        final Map<String, ArrayList<String>> listLinks = JNekoSQLite.GetTableFull("APP_i2c_regs_links", "devid="+GID);
        if (listLinks != null)
            if (listLinks.get("regid").size() > 0) {
                for(String regid : listLinks.get("regid")) {
                    final Map<String, ArrayList<String>> listRegs = JNekoSQLite.GetTableFull("APP_i2c_regs", "zstate>0 and gid="+regid);
                    xList1.GetSettingsComboBoxElement(LStrings.I2C_REGISTER_DEV_MAIN_GID, "i2c_type").RemoveItemByAV(regid); 
                    if (listRegs != null)
                        if (listRegs.isEmpty() == false) {
                            final ArrayList<String> cols = new ArrayList<>(listRegs.keySet());
                            final int count = listRegs.get(cols.get(0)).size();
                            if (count > 0) {
                                //System.err.println("count="+count);
                                for (int i=0; i<count; i++) {
                                    // 'APP_i2c_regs'(did bigint, gid bigint, zname char(32), zaddr bigint, zmin bigint, zmax bigint, zinit bigint, ztype char(16), zstate bigint)
                                    // AddSettingsSliderElementA(String ID, String GID, String title, int value, int _min, int _max, XListSliderElementActionListener _li)
                                    xList1.AddSettingsSliderElementB(
                                            LStrings.I2C_REGISTER_DEV_I2C_ID_SIGN+listRegs.get("did").get(i), 
                                            LStrings.I2C_REGISTER_DEV_LIST_GID, 
                                            listRegs.get("zname").get(i) + " [0x" + Integer.toHexString(Integer.parseInt(listRegs.get("zaddr").get(i), 10)).toUpperCase() +"]", 
                                            Integer.parseInt(listRegs.get("zaddr").get(i), 10),
                                            Integer.parseInt(listRegs.get("zinit").get(i), 10), 
                                            Integer.parseInt(listRegs.get("zmin").get(i), 10), 
                                            Integer.parseInt(listRegs.get("zmax").get(i), 10), 
                                            listRegs.get("ztype").get(i), 
                                            Integer.parseInt(listRegs.get("zindex").get(i), 10),
                                            sl_li
                                    );
                                    
                                }
                            } else 
                                xList1.AddItemForSimpleList("i2c_none", LStrings.I2C_REGISTER_DEV_LIST_GID, "Ни одной группы не добавлено.", ICON_STAR);
                        } else
                            xList1.AddItemForSimpleList("i2c_none", LStrings.I2C_REGISTER_DEV_LIST_GID, "Ни одной группы не добавлено.", ICON_STAR);
                    else
                        xList1.AddItemForSimpleList("i2c_none", LStrings.I2C_REGISTER_DEV_LIST_GID, "Ни одной группы не добавлено.", ICON_STAR);
                }
            } else 
                xList1.AddItemForSimpleList("i2c_none", LStrings.I2C_REGISTER_DEV_LIST_GID, "Ни одной группы не добавлено.", ICON_STAR);
        else
            xList1.AddItemForSimpleList("i2c_none", LStrings.I2C_REGISTER_DEV_LIST_GID, "Ни одной группы не добавлено.", ICON_STAR);

//        xList1.AddSettingsElement2Buttons("i2c_btns_d", LStrings.I2C_REGISTER_DEV_LIST_GID, null, "Записать min/max/init в eeprom", zbi);
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
