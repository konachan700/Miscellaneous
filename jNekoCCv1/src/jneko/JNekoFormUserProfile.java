package jneko;

import jnekotabs.MainUsersList;
import connections.JNekoPrepSQL;
import connections.JNekoSP;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import xdialogs.MsgBox;
import xlist.SettingsComboBoxElement;
import xlist.SettingsTextBoxElement;
import xlist.GlobalYesNoButtonsElement;
import xlist.XListButtonsActionListener;

public class JNekoFormUserProfile extends javax.swing.JFrame {
    private final ImageIcon groupIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/a2e.png"));
    private final Color ColorForBG = new Color(50, 39, 50);
    private final String xDID;
    private final JFrame THIS = this;
    private final MainUsersList fParent;
    
    @SuppressWarnings("Convert2Lambda")
    private final XListButtonsActionListener xbi = new XListButtonsActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID) {
            if (buttonID == 1101) THIS.dispose();
            if (buttonID == 1100) {
                final String fName  = ((SettingsTextBoxElement)(xlist1.GetItem("MAIN", "FULLNAME").GetComponent())).GetValue();
                final String fLogin = ((SettingsTextBoxElement)(xlist1.GetItem("MAIN", "XLOGIN").GetComponent())).GetValue();
                final String fPassw = ((SettingsTextBoxElement)(xlist1.GetItem("MAIN", "XPASSWD").GetComponent())).GetValue();
                final String fFlat  = ((SettingsTextBoxElement)(xlist1.GetItem("CONT", "CFLAT").GetComponent())).GetValue();
                final String fMail  = ((SettingsTextBoxElement)(xlist1.GetItem("CONT", "CEMAIL").GetComponent())).GetValue();
                final String fPhone = ((SettingsTextBoxElement)(xlist1.GetItem("CONT", "CPHONE").GetComponent())).GetValue();
                
                final String cTown      = ((SettingsComboBoxElement)(xlist1.GetItem("CONT", "CTOWN").GetComponent())).GetSelectedDID();
                final String cStreet    = ((SettingsComboBoxElement)(xlist1.GetItem("CONT", "CSTREET").GetComponent())).GetSelectedDID();
                final String cHouse     = ((SettingsComboBoxElement)(xlist1.GetItem("CONT", "CHOUSE").GetComponent())).GetSelectedDID();
                final String cTPlan     = ((SettingsComboBoxElement)(xlist1.GetItem("MAIN", "OTPLAN").GetComponent())).GetSelectedDID();

                if (xDID != null) {
                    final JNekoPrepSQL ps1 = new JNekoPrepSQL(
                          "UPDATE "
                                  + "Users "
                        + "SET "
                                  + "userFullName=?, "
                                  + "userLogin=?, "
                                  + "userPassword=?, "
                                  + "tpID=?, "
                                  + "houseID=?, "
                                  + "streetID=?, "
                                  + "townID=?, "
                                  + "flatNumber=?, "
                                  + "mobilePhone=?, "
                                  + "email=? "
                        + "WHERE "
                                  + "did=?;"
                    );
                    ps1.AddParam(fName);
                    ps1.AddParam(fLogin);
                    ps1.AddParam(fPassw);
                    ps1.AddParam(cTPlan);
                    ps1.AddParam(cHouse);
                    ps1.AddParam(cStreet);
                    ps1.AddParam(cTown);
                    ps1.AddParam(fFlat);
                    ps1.AddParam(fPhone);
                    ps1.AddParam(fMail);
                    ps1.AddParam(xDID);
                    JNekoSP.GetSP().SQLInsertUpdate(ps1);
                    //JNekoSP.GetSP().ReloadServerConfig();
                } else {
                    final JNekoPrepSQL ps1 = new JNekoPrepSQL(
                            "INSERT INTO Users VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
                    );
                    ps1.AddParam(new Date().getTime() + "");
                    ps1.AddParam(fName);
                    ps1.AddParam(fLogin);
                    ps1.AddParam(fPassw);
                    ps1.AddParam("0");
                    ps1.AddParam("0");
                    ps1.AddParam(cTPlan);
                    ps1.AddParam("0");
                    ps1.AddParam("");
                    ps1.AddParam("");
                    ps1.AddParam("");
                    ps1.AddParam("");
                    ps1.AddParam(cHouse);
                    ps1.AddParam(cStreet);
                    ps1.AddParam(cTown);
                    ps1.AddParam(fFlat);
                    ps1.AddParam(fPhone);
                    ps1.AddParam(fMail);
                    JNekoSP.GetSP().SQLInsertUpdate(ps1);
                }
                
                MsgBox.Show("Пользователь", "Данные пользователя успешно изменены!");
                
                fParent.Refresh();
                THIS.dispose();
            }
        }   
    };
    
    public JNekoFormUserProfile(String __DID, MainUsersList __parent) {
        initComponents();
        
        if (JNekoSP.GetSP() == null) {
            MsgBox.Show("Пользователь", "Нет подключения к серверу JNekoSP!");
            this.dispose();
        }
        
        xDID = __DID;
        fParent = __parent;
        
        jPanel1.setBackground(ColorForBG);
        jPanel2.setBackground(ColorForBG);
        
        final ArrayList<Map<String,String>> stx;
        final Map<String,String> st;
        
        if (__DID != null) {
            this.setTitle("User ID:"+__DID);
            stx = JNekoSP.GetSP().SQLSelect("SELECT * FROM Users WHERE did="+__DID+";");
            if (stx != null) st = stx.get(0); else st = null;
        } else {
            st = null;
        }
        
        final Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((s.width/2)-(this.getWidth()/2), (s.height/2)-(this.getHeight()/2), this.getWidth(), this.getHeight());
        
        xlist1.AddGroupHeader("MAIN", ColorForBG.darker(), "Основное", groupIcon, 0);
        xlist1.AddGroupHeader("CONT", ColorForBG.darker(), "Контакнтые данные", groupIcon, 0);
        
        if (__DID != null) xlist1.AddItem("DIDINFO",   "MAIN",  new SettingsTextBoxElement(((st != null) ? st.get("did") : ""), "ID [RO]", false));
        
        xlist1.AddItem("FULLNAME",  "MAIN",  new SettingsTextBoxElement(((st != null) ? st.get("userFullName") : ""), "Полное имя", false));
        xlist1.AddItem("XLOGIN",    "MAIN",  new SettingsTextBoxElement(((st != null) ? st.get("userLogin") : ""), "Логин для ЛК", false));
        xlist1.AddItem("XPASSWD",   "MAIN",  new SettingsTextBoxElement(((st != null) ? st.get("userPassword") : ""), "Пароль для ЛК", false));
 
        xlist1.AddItem("CTOWN",     "CONT",  new SettingsComboBoxElement(((st != null) ? st.get("townID") : null), "Город", "Towns", "svalue"));
        xlist1.AddItem("CSTREET",   "CONT",  new SettingsComboBoxElement(((st != null) ? st.get("streetID") : null), "Улица", "Streets", "svalue"));
        xlist1.AddItem("CHOUSE",    "CONT",  new SettingsComboBoxElement(((st != null) ? st.get("houseID") : null), "Дом", "Houses", "svalue"));
        xlist1.AddItem("CFLAT",     "CONT",  new SettingsTextBoxElement(((st != null) ? st.get("flatNumber") : ""), "Номер квартиры", false));
        xlist1.AddItem("CEMAIL",    "CONT",  new SettingsTextBoxElement(((st != null) ? st.get("email") : "null@localhost"), "E-Mail", false));
        xlist1.AddItem("CPHONE",    "CONT",  new SettingsTextBoxElement(((st != null) ? st.get("mobilePhone") : "+7"), "Номер телефона", false));
        
        if (__DID != null) {
            final StringBuilder sb1 = new StringBuilder();
            
            final String cTown      = ((SettingsComboBoxElement)(xlist1.GetItem("CONT", "CTOWN").GetComponent())).GetValue();
            final String cStreet    = ((SettingsComboBoxElement)(xlist1.GetItem("CONT", "CSTREET").GetComponent())).GetValue();
            final String cHouse     = ((SettingsComboBoxElement)(xlist1.GetItem("CONT", "CHOUSE").GetComponent())).GetValue();
            
            sb1.append("г.");       sb1.append(cTown);
            sb1.append(", ул.");    sb1.append(cStreet);
            sb1.append(", дом ");   sb1.append(cHouse);
            sb1.append(", кв.");    sb1.append(((st != null) ? st.get("flatNumber") : ""));
            
            xlist1.AddItem("CGENADDR",  "CONT",  new SettingsTextBoxElement(sb1.substring(0), "Полный адрес [RO]", false));
            
            int money1 = Integer.parseInt(((st != null) ? st.get("userMoney") : "0"), 10);
            if (money1 > 0) {
                final ArrayList<Map<String,String>> stx1;
                final Map<String,String> st1;
                stx1 = JNekoSP.GetSP().SQLSelect("SELECT * FROM TarifPlans WHERE did="+((st != null) ? st.get("tpID") : "0")+";");
                if (stx1 != null) st1 = stx1.get(0); else st1 = null;
                if (st1 != null) {
                    int cpm = Integer.parseInt(st1.get("costPerMonth"), 10);
                    xlist1.AddItem("CGENACT",  "CONT",  new SettingsTextBoxElement(
                            JNekoSupportClass.GetPayTime(cpm, money1), "Активен до даты [RO]", false));
                    xlist1.AddItem("CGENCCPD",  "CONT",  new SettingsTextBoxElement(
                            ((cpm / JNekoSupportClass.GetDayCountOnThisMonth()) / 100) + " рублей", 
                            "CCPD [RO]", false));
                }
            }
        }
        
        xlist1.AddItem("OTPLAN",     "MAIN", new SettingsComboBoxElement(((st != null) ? st.get("tpID") : null), "Тарифный план", "TarifPlans", "name"));
        xlist1.AddItem("YESNOBTNS1", "CONT", new GlobalYesNoButtonsElement("Сохранить", "Забыть изменения", xbi));
        xlist1.AddItem("YESNOBTNS2", "MAIN", new GlobalYesNoButtonsElement("Сохранить", "Забыть изменения", xbi));
        
        if (__DID != null) {
            xlist1.AddGroupHeader( "GPON", ColorForBG.darker(), "Сетевые параметры (только чтение)", groupIcon, 0);
            xlist1.AddItem("ELTX", "GPON",  new SettingsTextBoxElement(((st != null) ? st.get("eltx") : ""), "PON Serial", false));
            xlist1.AddItem("SLOT", "GPON",  new SettingsTextBoxElement(((st != null) ? st.get("slot") : ""), "PON Slot", false));
            xlist1.AddItem("IP",   "GPON",  new SettingsTextBoxElement(((st != null) ? st.get("ip")   : ""), "IPv4-адрес", false));
            xlist1.AddItem("MAC",  "GPON",  new SettingsTextBoxElement(((st != null) ? st.get("mac")  : ""), "MAC-адрес", false));
        }
        
        xlist1.AddGroupHeader("LASTPAYMENTS", ColorForBG.darker(), "Последние 25 платежей пользователя", groupIcon, 0);
        
        if (xDID != null) {
            if (JNekoSP.GetSP() != null) {
                final ArrayList<Map<String,String>> x = JNekoSP.GetSP().SQLSelect("SELECT * FROM Payments WHERE paymentType=1 AND userDID="+xDID+" ORDER BY did DESC;");

                if (x != null) {
                    if (x.size() > 0) {
                        final SimpleDateFormat DF = new SimpleDateFormat("HH:mm dd.MM.yyyy");
                        int counter1 = 0;
                        for (Map<String,String> xx : x) {
                            counter1++;
                            final long DiD = Long.parseLong(xx.get("did"), 10);
                            final long money = Long.parseLong(xx.get("moneyCount"), 10) / 100;

                            if (money <= 0) {
                                xlist1.AddItemForSimpleList(
                                        xx.get("did")+counter1, "LASTPAYMENTS", 
                                        "Списание: "+(-1*money)+" рублей; Дата: "+DF.format(new Date(DiD))+" ("+xx.get("comment")+")", 
                                        xlist1.ICON_MINUS);
                            } else {
                                xlist1.AddItemForSimpleList(
                                        xx.get("did")+counter1, "LASTPAYMENTS", 
                                        "Пополнение: "+money+" рублей; Дата: "+DF.format(new Date(DiD))+" ("+xx.get("comment")+")", 
                                        xlist1.ICON_PLUS);
                            }
                        }
                    } else {
                        xlist1.AddItemForSimpleList("NOITEMS", "LASTPAYMENTS", "Нет данных", groupIcon);
                    }
                } else {
                    xlist1.AddItemForSimpleList("NOITEMS", "LASTPAYMENTS", "Нет данных", groupIcon);
                }
            } else {
                xlist1.AddItemForSimpleList("NOITEMS", "LASTPAYMENTS", "Нет данных", groupIcon);
            }
        }
        
        xlist1.setBackgroundColor(ColorForBG);
        xlist1.VPBON();
        xlist1.Commit();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        xlist1 = new xlist.XList();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Профиль пользователя");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(0, 51, 0));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new-logo-right.png"))); // NOI18N

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new-logo-left.png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 432, Short.MAX_VALUE)
                .addComponent(jLabel1))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2)
            .addComponent(jLabel1)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(xlist1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xlist1, javax.swing.GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private xlist.XList xlist1;
    // End of variables declaration//GEN-END:variables
}
