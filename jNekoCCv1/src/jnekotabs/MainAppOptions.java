package jnekotabs;

import java.awt.Color;
import javax.swing.ImageIcon;
import xlistcustomelements.SettingsSlotsSelectorElement;
import xlist.SettingsCheckBoxElement;

public class MainAppOptions extends javax.swing.JPanel {
    private final ImageIcon groupIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/a2e.png"));
    
    public MainAppOptions(Color c) {
        initComponents();
        
        xlist1.ClearAll();
        
//        xlist1.AddGroupHeader("MYSQL", c, "Настройки сервера MySQL", groupIcon, 0);
        xlist1.AddGroupHeader("MA4000", c, "Настройки MA4000", groupIcon, 0);
        xlist1.AddGroupHeader("JNSP", c, "Настройки JNekoSP (серверная часть)", groupIcon, 0);
        xlist1.AddGroupHeader("APPCH", c, "Настройки системы", groupIcon, 0);

//        xlist1.AddSettingsElementText("LOGIN", "MYSQL", "Имя пользователя");
//        xlist1.AddSettingsElementText("DBNAME", "MYSQL", "Имя базы данных");
//        xlist1.AddSettingsElementText("PASSWORD", "MYSQL", "Пароль");
//        xlist1.AddSettingsElementText("HOST", "MYSQL", "Хост базы данных");
        
        xlist1.AddSettingsElementText("HOST", "MA4000", "IP-адрес");
        xlist1.AddSettingsElementText("PORT", "MA4000", "Порт");
        xlist1.AddSettingsElementText("USERNAME", "MA4000", "Логин");
        xlist1.AddSettingsElementText("PASSWORD", "MA4000", "Пароль");
        
        xlist1.AddSettingsElementText("HOST", "JNSP", "IP-адрес");
        xlist1.AddSettingsElementText("PORT", "JNSP", "Порт");
        xlist1.AddSettingsElementText("SKEY", "JNSP", "Ключ соединения");
        xlist1.AddSettingsElementText("SDHCPDB", "JNSP", "Путь на сервере к dhcpd.leases");
//        xlist1.AddSettingsElementText("SDHCPDSTATIC", "JNSP", "Путь на сервере к dhcpd.d/static.conf");
        
//        xlist1.AddSettingsElementText("IPSMASK", "JNSP", "Маска поиска IP (например \"10.212.\")");
        
        xlist1.AddItem("OPT1", "MA4000", new SettingsSlotsSelectorElement("OPT"), "", 0);
        
        xlist1.AddItem("CHBOX1", "APPCH", new SettingsCheckBoxElement("Разрешить удаление пользователей", "AllowDeletingUsers", true), "", 0);
        xlist1.AddItem("CHBOX2", "APPCH", new SettingsCheckBoxElement("Получать данные о трафике в фоне (может замедлить работу)", "AllowTarfStatReceive", true), "", 0);
        
        xlist1.VPBON();
        xlist1.setBackgroundColor(c); 
        xlist1.Commit();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        xlist1 = new xlist.XList();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xlist1, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xlist1, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private xlist.XList xlist1;
    // End of variables declaration//GEN-END:variables
}
