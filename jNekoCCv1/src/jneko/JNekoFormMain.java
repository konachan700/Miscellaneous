package jneko;

import jnekotabs.UnconfuguredONTs;
import jnekotabs.TrafStatDisplayDaily;
import jnekotabs.MainUsersList;
import jnekotabs.MainAppOptions;
import jnekotabs.Last100Payments;
import jnekotabs.MA4000TelnetLogs;
import jnekotabs.RoutersModelsList;
import jnekotabs.AllOnlineONTs;
import jnekotabs.Addresses;
import jnekotabs.TariffPlans;
import connections.JNekoSPBackground;
import connections.MA4000Telnet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import jnekotabs.StaticIPList;
import jnekotabs.TrafStatDisplayCurrent;
import xlist.XListElementActionListener;

public class JNekoFormMain extends javax.swing.JFrame {
    private final    Map<String, Color>              appColor                   = new HashMap<>();
    private final    ImageIcon                       groupIcon                  = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/a2e.png"));

    private final    MA4000TelnetLogs                xxLogsMA4000               = new MA4000TelnetLogs();
    private final    JNekoProgressDisplayPanel       xxWaitPanel                = new JNekoProgressDisplayPanel();
    
    private volatile TrafStatDisplayDaily            xxLastTrafStatPanel;
    private volatile Last100Payments            xxSimpleLastPayments;
    private volatile MainAppOptions             xxAppOptionsConnect;
    private volatile MainUsersList              xxMainUsersList;
    private volatile MainUsersList              xxLockedUsersList;
    private volatile TariffPlans                xxTariPlans;
    private volatile Addresses                  xxListOfHouses;
    private volatile Addresses                  xxListOfStreets;
    private volatile Addresses                  xxListOfTowns;
    private volatile UnconfuguredONTs           xxUnconfOnt;
    private volatile AllOnlineONTs              xxAllONTs;
    private volatile RoutersModelsList          xxRoutersModels;
    private volatile StaticIPList               xxStaticIPList;
    private volatile TrafStatDisplayCurrent     xxTrafStatDisplayCurrent;
    
    // Цвет для текущей вкладки
    public  volatile Color                           xxCurrentTabColor          = null;
    private volatile Component                       currentPanel               = null;
    
    private final JNekoProgressDisplayClass.PDActionListener waitListener = new JNekoProgressDisplayClass.PDActionListener() {
        @Override
        @SuppressWarnings("Convert2Lambda")
        public void OnStartingWait() {
            try {
                SwingUtilities.invokeAndWait(new Runnable(){
                    @Override
                    public void run(){
                        xlist1.setEnabled(false);
                        if (xxCurrentTabColor != null) xxWaitPanel.setBackground(xxCurrentTabColor);
                        Show_panel(xxWaitPanel);
                    }
                });
            } catch (InterruptedException | InvocationTargetException ex) { }
        }

        @Override
        @SuppressWarnings("Convert2Lambda")
        public void OnStoppedWait() {
            try {
                SwingUtilities.invokeAndWait(new Runnable(){
                    @Override
                    public void run(){
                        if (currentPanel != null) Show_panel(currentPanel);
                        xlist1.setEnabled(true);
                    }
                });
            } catch (InterruptedException | InvocationTargetException ex) { }
        }
    };
    
    private final XListElementActionListener InternalActionListener = new XListElementActionListener() {
        @Override
        public void OnItemClick(String GID, String ID, Component c, MouseEvent e) {
            xxCurrentTabColor = xlist1.GetItem(GID, ID).GetLabelColor();

            if (JNekoProgressDisplayClass.IsWaiting()) {
                return;
            } else {
                jPanel3.setBackground(xxCurrentTabColor);
                jPanel2.setBackground(xxCurrentTabColor.darker());
                
                if (GID.contains("opt")) {
                    if (ID.contains("99")) currentPanel = xxLogsMA4000;
                    if (ID.contains("09")) currentPanel = xxAppOptionsConnect;
                    
                }

                if (GID.contains("stat")) {
                    if (ID.contains("98")) currentPanel = xxLastTrafStatPanel;
                    if (ID.contains("11")) currentPanel = xxSimpleLastPayments;
                    if (ID.contains("95")) currentPanel = xxTrafStatDisplayCurrent;
                }

                if (GID.contains("users")) {
                    if (ID.contains("01")) { xxMainUsersList.Refresh();    currentPanel = xxMainUsersList;   }
                    if (ID.contains("02")) { xxLockedUsersList.Refresh();  currentPanel = xxLockedUsersList; }
                    if (ID.contains("10")) { xxTariPlans.Refresh();        currentPanel = xxTariPlans;       }
                    if (ID.contains("03")) { xxListOfHouses.Refresh();     currentPanel = xxListOfHouses;    }
                    if (ID.contains("04")) { xxListOfStreets.Refresh();    currentPanel = xxListOfStreets;   }
                    if (ID.contains("05")) { xxListOfTowns.Refresh();      currentPanel = xxListOfTowns;     }
                    if (ID.contains("96")) { xxStaticIPList.Refresh();     currentPanel = xxStaticIPList;    }
                    
                }

                if (GID.contains("pon")) {
                    if (ID.contains("06")) currentPanel = xxUnconfOnt;
                    if (ID.contains("31")) currentPanel = xxAllONTs;
                    if (ID.contains("97")) { xxRoutersModels.Refresh(); currentPanel = xxRoutersModels; }
                }
            }
            
            Show_panel(currentPanel);
        }

        @Override
        public void OnHeaderClick(String GID, JLabel c, boolean isVisible, MouseEvent e, int Type) {
            
        }
    };
    
    public Component GetCurrentTabPanel() {
        return currentPanel;
    }
    
    public synchronized void Show_panel(Component c) {
        jPanel1.removeAll();
        jPanel1.revalidate();
        jPanel1.repaint();
        
        jPanel1.add(c);
        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(c, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(c, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel1.revalidate();
        jPanel1.repaint();
    }
    
    @Override
    public void dispose() {
        JNekoSPBackground.Disconnect();
        super.dispose();
    }
        
    public JNekoFormMain() {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) { }
        
        initComponents();
        
        Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((s.width/2)-(this.getWidth()/2), (s.height/2)-(this.getHeight()/2), this.getWidth(), this.getHeight());

        appColor.put("PON",     new Color(55, 25, 25));
        appColor.put("USERS",   new Color(25, 55, 25));
        appColor.put("STAT",    new Color(25, 25, 55));
        appColor.put("OPT",     new Color(55, 55, 25));
        
        xlist1.AddGroupHeader("users", appColor.get("USERS"), "Биллинг", groupIcon, 3);
        xlist1.AddGroupHeader("pon", appColor.get("PON"), "GPON", groupIcon, 5);
        xlist1.AddGroupHeader("stat", appColor.get("STAT"), "Статистика", groupIcon, 5);
        xlist1.AddGroupHeader("opt", appColor.get("OPT"), "Настройки программы", groupIcon, 5);
        
        xlist1.AddItemForSelector("01", "users", "Все пользователи", "all", xlist1.ICON_ALLUSERS);
        xlist1.AddItemForSelector("02", "users", "Должники", "locked", xlist1.ICON_LOCKEDUSERS);
        xlist1.AddItemForSelector("10", "users", "Тарифные планы", "tp", xlist1.ICON_TARIF);
        
        xlist1.AddItemForSelector("03", "users", "Список домов", "Houses", xlist1.ICON_HOME);
        xlist1.AddItemForSelector("04", "users", "Список улиц", "Streets", xlist1.ICON_HOME);
        xlist1.AddItemForSelector("05", "users", "Список городов", "Towns", xlist1.ICON_TOWN);
        xlist1.AddItemForSelector("96", "users", "Статические IP", "StaticIP", xlist1.ICON_PAYMENTS);
        
        xlist1.AddItemForSelector("06", "pon", "Новые абон.устройства", "all", 0);
        xlist1.AddItemForSelector("31", "pon", "Активные абон.устройства", "all", 0);
        //xlist1.AddItemForSelector("32", "pon", "Все абон.устройства", "all", 0);
        xlist1.AddItemForSelector("97", "pon", "Модели роутеров", "all", 0);
        
        xlist1.AddItemForSelector("09", "opt", "Базовые настройки", "all", 0);
        xlist1.AddItemForSelector("99", "opt", "Логи MA4000", "all", 0);
        
        xlist1.AddItemForSelector("11", "stat", "Список последних платежей", "payments", xlist1.ICON_PAYMENTS);
        xlist1.AddItemForSelector("98", "stat", "Суточный трафик", "all", xlist1.ICON_TRAF);
        xlist1.AddItemForSelector("95", "stat", "Текущий трафик", "all", xlist1.ICON_TRAF);
        
        xlist1.SetActionListener(InternalActionListener); 
        xlist1.SetCommitOption_MainSelector();
        xlist1.SetCommitOption_MainSelectorDefGroup("users");
        
        xlist1.Commit();
        
        jPanel3.setBackground(appColor.get("USERS"));
        jPanel2.setBackground(appColor.get("USERS").darker());
        xxWaitPanel.setBackground(appColor.get("USERS"));
        
        Show_panel(xxWaitPanel);
        
        JNekoSPBackground.SetCallback(jLabel4);
        JNekoSPBackground.RunSupportThread();
        
        MA4000Telnet.SetProtocolLogger(xxLogsMA4000);
        MA4000Telnet.SetCallbackLabel(jLabel3);
        
        JNekoProgressDisplayClass.ActionListenerRegister(waitListener);

        Thread telnetConn = new Thread(new Runnable() {
            @Override
            public void run() {
                JNekoProgressDisplayClass.On();
                
                MA4000Telnet.OpenConnection();
                JNekoSPBackground.jNekoServerInfo.CollectONTAndMACInfo();
                
                xxAppOptionsConnect         = new MainAppOptions(appColor.get("OPT"));
                xxRoutersModels             = new RoutersModelsList(appColor.get("PON"));
        
                xxLastTrafStatPanel         = new TrafStatDisplayDaily(appColor.get("STAT"));
                xxSimpleLastPayments        = new Last100Payments(appColor.get("STAT"));
                xxTrafStatDisplayCurrent    = new TrafStatDisplayCurrent(appColor.get("STAT"));

                xxMainUsersList             = new MainUsersList(appColor.get("USERS"), false);
                xxLockedUsersList           = new MainUsersList(appColor.get("USERS"), true);
                xxTariPlans                 = new TariffPlans(appColor.get("USERS"));
                xxListOfHouses              = new Addresses(appColor.get("USERS"), "Houses", "Список домов");
                xxListOfStreets             = new Addresses(appColor.get("USERS"), "Streets", "Список улиц");
                xxListOfTowns               = new Addresses(appColor.get("USERS"), "Towns", "Список городов");
                xxStaticIPList              = new StaticIPList(appColor.get("USERS"));

                xxUnconfOnt                 = new UnconfuguredONTs(appColor.get("PON"));
                xxAllONTs                   = new AllOnlineONTs(appColor.get("PON"));
                
                currentPanel                = xxMainUsersList;
                
                JNekoProgressDisplayClass.Off();
            }
        });
        telnetConn.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        xlist1 = new xlist.XList();
        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("JNeko Control Center");

        jPanel2.setBackground(new java.awt.Color(51, 0, 51));

        jLabel1.setBackground(new java.awt.Color(204, 255, 102));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new-logo-left.png"))); // NOI18N

        jLabel5.setBackground(new java.awt.Color(204, 255, 102));
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new-logo-right.png"))); // NOI18N

        jLabel3.setForeground(new java.awt.Color(255, 204, 204));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("MA4000");

        jLabel4.setForeground(new java.awt.Color(255, 204, 204));
        jLabel4.setText("JNekoSP");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 539, Short.MAX_VALUE)
                .addComponent(jLabel5))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel5)))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        xlist1.setBackground(new java.awt.Color(153, 153, 153));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xlist1, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(xlist1, javax.swing.GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private xlist.XList xlist1;
    // End of variables declaration//GEN-END:variables
}
