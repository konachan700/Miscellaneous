package jnekoonts;

import dbengine.sSQLite;
import jlistextention.LI_UsersAndGroups;
import jlistextention.LI_PONElement;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import jlistextention.ExtListModel_Users;
import jlistextention.ExtListModel_PON;
import jlistextention.ExtListRenderer;

public class FListONTs extends javax.swing.JFrame {
    private final ExtListRenderer           rend       = new ExtListRenderer();
    private final ExtListModel_PON          model      = new ExtListModel_PON();
    private       ExtListModel_PON          tempModel  = null;
    private final ExtListModel_Users        modelU     = new ExtListModel_Users();
    private final ExtListModel_Users        tempModelU = new ExtListModel_Users();
    private final Z_MA4000Engine            xMA4000     = new Z_MA4000Engine();
    private final JFrame                    This        = this;
    private int                             xComboIndex = -1, 
                                            uComboIndex = -1,
                                            xUsersTabIndex = -1;
    
    public FListONTs() {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) { }
        
        initComponents();
        
        JNekoProgressDisplayClass.AddElement(jProgressBar1);
//        Z_DBMySQL.RegisterMarker(jLabel14); 
        
        sSQLite.APPSettings_RegisterActionListener(jCheckBox10, "LiveSearch");
        sSQLite.APPSettings_RegisterActionListener(jCheckBox12, "LiveUsersSearch");
        sSQLite.APPSettings_RegisterActionListener(jCheckBox9, "ShowAllONTInACSList");
        sSQLite.APPSettings_RegisterActionListener(jCheckBox11, "No_M_Menu");
        
        sSQLite.APPSettings_RegisterActionListener(jCheckBox1, "Slot0_Enable");
        sSQLite.APPSettings_RegisterActionListener(jCheckBox2, "Slot1_Enable");
        sSQLite.APPSettings_RegisterActionListener(jCheckBox3, "Slot2_Enable");
        sSQLite.APPSettings_RegisterActionListener(jCheckBox4, "Slot3_Enable");
        sSQLite.APPSettings_RegisterActionListener(jCheckBox5, "Slot4_Enable");
        sSQLite.APPSettings_RegisterActionListener(jCheckBox6, "Slot5_Enable");
        sSQLite.APPSettings_RegisterActionListener(jCheckBox7, "Slot6_Enable");
        sSQLite.APPSettings_RegisterActionListener(jCheckBox8, "Slot7_Enable");
        
        sSQLite.APPSettings_RegisterActionListener(jTextField6, "MySQL_Host");
        sSQLite.APPSettings_RegisterActionListener(jTextField7, "MySQL_Username");
        sSQLite.APPSettings_RegisterActionListener(jPasswordField2, "MySQL_Password");
        sSQLite.APPSettings_RegisterActionListener(jTextField5, "MySQL_DBName");
        
        sSQLite.APPSettings_RegisterActionListener(jTextField2, "MA4000_Host");
        sSQLite.APPSettings_RegisterActionListener(jTextField1, "MA4000_Username");
        sSQLite.APPSettings_RegisterActionListener(jPasswordField1, "MA4000_Password");
        sSQLite.APPSettings_RegisterActionListener(jTextField3, "MA4000_Port");
        
        sSQLite.APPSettings_RegisterActionListener(jTextField9, "GW_Host");
        sSQLite.APPSettings_RegisterActionListener(jTextField8, "GW_Username");
        sSQLite.APPSettings_RegisterActionListener(jPasswordField3, "GW_Password");
        sSQLite.APPSettings_RegisterActionListener(jTextField10, "GW_Port");
        
        if ((jTextField5.getText().length() > 1) && (jTextField6.getText().length() > 1) && (jTextField7.getText().length() > 1)) {
//            Z_DBMySQL.Connect();
//            Z_DBMySQL.Connect(jTextField5.getText(), jTextField7.getText(), new String(jPasswordField2.getPassword()), jTextField6.getText());
//            
//            FileReader fis;
//            try {
//                fis = new FileReader("/var/run/media/lain/openSUSE-12.3-NET-x86_640110/bkp221114.csv");
//                BufferedReader b = new BufferedReader(fis);
//
//                String s = "";
//                while ((s=b.readLine())!=null){
//                    String sa[] = s.split(";");
//                    PreparedStatement ps = Z_DBMySQL.GetConnection().prepareStatement("INSERT INTO Users VALUES(?, ?, ?, '', ?, 0, 0, 0, '', '', '', '', 0, 0, 0, 0, '', '');");
//                    ps.setLong(1, new Date().getTime() + Long.parseLong(sa[0], 10));
//                    ps.setString(2, sa[3]);
//                    ps.setString(3, sa[1]);
//                    ps.setLong(4, Long.parseLong(sa[5].replaceAll("[\\.][0-9]{1,5}", ""), 10)*100);
//                    ps.execute();
//                }
//                b.close();
//                fis.close();
//            } catch (IOException ex) {
//                JOptionPane.showMessageDialog(null, "Ошибка: невозможно записать конфигурацию БД.");
//            } catch (SQLException ex) {
//                Logger.getLogger(FListONTs.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
        
        Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((s.width/2)-(this.getWidth()/2), (s.height/2)-(this.getHeight()/2), this.getWidth(), this.getHeight());
        
        jList1.setCellRenderer(rend); 
        jList1.setModel(model); 
        
        jList2.setCellRenderer(rend); 
        jList2.setModel(modelU);
        
        jPanel9.setVisible(false);
        jPanel12.setVisible(false);

        __telnetConnect();
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jPopupMenu2 = new javax.swing.JPopupMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem7 = new javax.swing.JMenuItem();
        jPopupMenu3 = new javax.swing.JPopupMenu();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel10 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jComboBox1 = new javax.swing.JComboBox();
        jPanel7 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jTextField4 = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jCheckBox10 = new javax.swing.JCheckBox();
        jCheckBox9 = new javax.swing.JCheckBox();
        jCheckBox11 = new javax.swing.JCheckBox();
        jPanel8 = new javax.swing.JPanel();
        jComboBox2 = new javax.swing.JComboBox();
        jPanel6 = new javax.swing.JPanel();
        jTextField11 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jCheckBox12 = new javax.swing.JCheckBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jLabel19 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jPasswordField1 = new javax.swing.JPasswordField();
        jLabel4 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jCheckBox6 = new javax.swing.JCheckBox();
        jCheckBox7 = new javax.swing.JCheckBox();
        jCheckBox8 = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jPasswordField2 = new javax.swing.JPasswordField();
        jButton3 = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jTextField8 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jPasswordField3 = new javax.swing.JPasswordField();
        jButton4 = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        xlist1 = new xlist.XList();
        jButton10 = new javax.swing.JButton();

        jPopupMenu1.setBackground(new java.awt.Color(255, 255, 255));

        jMenuItem1.setText("ELTX00000000 [Slot 0]");
        jMenuItem1.setEnabled(false);
        jPopupMenu1.add(jMenuItem1);
        jPopupMenu1.add(jSeparator1);

        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/run-build-install-root.png"))); // NOI18N
        jMenuItem2.setText("Сбросить настройки на стандартные для профиля");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem2);

        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/documentinfo-inverted.png"))); // NOI18N
        jMenuItem3.setText("Полная информация");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem3);
        jPopupMenu1.add(jSeparator2);

        jMenuItem5.setForeground(new java.awt.Color(204, 0, 0));
        jMenuItem5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/application-exit-inverted.png"))); // NOI18N
        jMenuItem5.setText("[M] Пересоздать ONT по шаблону");
        jPopupMenu1.add(jMenuItem5);

        jMenuItem4.setForeground(new java.awt.Color(204, 0, 0));
        jMenuItem4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/kdenlive-add-text-clip-inverted.png"))); // NOI18N
        jMenuItem4.setText("[M] Соxранить в список последних добавленных    ");
        jMenuItem4.setActionCommand("[M] Соxранить в список последних добавленных");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem4);

        jMenuItem6.setText("ELTX00000000 Slot:0");
        jMenuItem6.setEnabled(false);
        jPopupMenu2.add(jMenuItem6);
        jPopupMenu2.add(jSeparator3);

        jMenuItem7.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/kdenlive-add-text-clip-inverted.png"))); // NOI18N
        jMenuItem7.setText("Создать ONT    ");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jPopupMenu2.add(jMenuItem7);

        jMenuItem8.setText("Пользователи: все, имеющиеся в системе");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jPopupMenu3.add(jMenuItem8);

        jMenuItem9.setText("Пользователи: только заблокированные");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jPopupMenu3.add(jMenuItem9);
        jPopupMenu3.add(jSeparator4);

        jMenuItem10.setText("jMenuItem10");
        jPopupMenu3.add(jMenuItem10);

        jMenuItem11.setText("jMenuItem11");
        jPopupMenu3.add(jMenuItem11);

        jMenuItem12.setText("jMenuItem12");
        jPopupMenu3.add(jMenuItem12);

        jMenuItem13.setText("jMenuItem13");
        jPopupMenu3.add(jMenuItem13);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("jNeko Control Center");
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(204, 204, 255));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logo-xx2.png"))); // NOI18N

        jProgressBar1.setFont(new java.awt.Font("Monospaced", 1, 12)); // NOI18N
        jProgressBar1.setString("Please, wait...");
        jProgressBar1.setStringPainted(true);

        jLabel10.setFont(new java.awt.Font("Monospaced", 1, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(153, 153, 153));
        jLabel10.setText("MA4000");

        jLabel14.setFont(new java.awt.Font("Monospaced", 1, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(153, 153, 153));
        jLabel14.setText("MySQL");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel10)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Monospaced", 1, 12)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jList1.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.setDoubleBuffered(true);
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jList1MousePressed(evt);
            }
        });
        jScrollPane2.setViewportView(jList1);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "--выберите категорию--", "ONT: Новые, которых нет в системе", "ONT: Все зарегистрированные устройства", "ONT: Последние добавленные в систему (отчет)", "ACS: Элементы профилей (для удобства, со справкой по каждому)", "ACS: Профили создания устройств" }));
        jComboBox1.setEnabled(false);
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/view-refresh.png"))); // NOI18N
        jButton2.setText("Обновить");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel8.setBackground(new java.awt.Color(204, 204, 204));
        jLabel8.setFont(new java.awt.Font("Liberation Sans", 1, 48)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(153, 153, 153));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("###");

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        jTextField4.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        jTextField4.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jTextField4CaretUpdate(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/edit-find.png"))); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jCheckBox10.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBox10.setFont(new java.awt.Font("Liberation Sans", 0, 10)); // NOI18N
        jCheckBox10.setText("Живой поиск (ресурсоемко)");

        jCheckBox9.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBox9.setFont(new java.awt.Font("Liberation Sans", 0, 10)); // NOI18N
        jCheckBox9.setForeground(new java.awt.Color(204, 51, 0));
        jCheckBox9.setText("Показывать все ONT");

        jCheckBox11.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBox11.setFont(new java.awt.Font("Liberation Sans", 0, 10)); // NOI18N
        jCheckBox11.setForeground(new java.awt.Color(204, 51, 0));
        jCheckBox11.setText("Показать М-меню");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jTextField4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6))
            .addComponent(jCheckBox10, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
            .addComponent(jCheckBox9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jCheckBox11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox11))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 243, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 628, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("GPON", new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/kudesigner.png")), jPanel2); // NOI18N

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        jComboBox2.setBackground(new java.awt.Color(204, 204, 204));
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- выберите категорию --", "Пользователи: все", "Пользователи: только заблокированные", "Адреса: список домов", "Адреса: список улиц", "Адреса: список городов" }));
        jComboBox2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox2ItemStateChanged(evt);
            }
        });

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        jTextField11.setFont(new java.awt.Font("Liberation Sans", 0, 14)); // NOI18N
        jTextField11.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jTextField11CaretUpdate(evt);
            }
        });

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/edit-find.png"))); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jCheckBox12.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBox12.setFont(new java.awt.Font("Liberation Sans", 0, 10)); // NOI18N
        jCheckBox12.setText("Живой поиск (ресурсоемко)");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jTextField11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jCheckBox12)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jTextField11)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox12))
        );

        jList2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList2MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jList2);

        jLabel19.setFont(new java.awt.Font("Liberation Sans", 1, 36)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(204, 204, 204));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("###");

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));

        jLabel20.setFont(new java.awt.Font("Liberation Sans", 0, 10)); // NOI18N
        jLabel20.setText("Значение параметра");

        jLabel21.setFont(new java.awt.Font("Liberation Sans", 0, 10)); // NOI18N
        jLabel21.setText("Alias (можно не заполнять)");

        jTextField13.setText("default");

        jButton7.setFont(new java.awt.Font("Liberation Sans", 0, 10)); // NOI18N
        jButton7.setText("Сохранить");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setFont(new java.awt.Font("Liberation Sans", 0, 10)); // NOI18N
        jButton8.setText("Создать новый");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTextField12)
            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
            .addComponent(jTextField13)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addComponent(jButton8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton7))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton7)
                    .addComponent(jButton8)))
        );

        jButton9.setForeground(new java.awt.Color(204, 0, 0));
        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/get-hot-new-stuff.png"))); // NOI18N
        jButton9.setText("Выберите категорию");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 556, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jButton9)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 142, Short.MAX_VALUE)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Пользователи", new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/system-run.png")), jPanel8); // NOI18N

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Параметры MA4000"));

        jLabel2.setFont(new java.awt.Font("Monospaced", 1, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("IP");

        jLabel3.setFont(new java.awt.Font("Monospaced", 1, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Пароль");

        jTextField1.setText("jservice");

        jLabel4.setFont(new java.awt.Font("Monospaced", 1, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Логин");

        jTextField2.setText("192.168.33.2");

        jLabel5.setFont(new java.awt.Font("Monospaced", 1, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Порт");

        jTextField3.setText("23");

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/view-refresh.png"))); // NOI18N
        jButton1.setText("Проверить подключение");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField3))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPasswordField1))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Установленные слоты"));

        jCheckBox1.setSelected(true);
        jCheckBox1.setText("0");
        jCheckBox1.setOpaque(false);

        jCheckBox2.setSelected(true);
        jCheckBox2.setText("1");
        jCheckBox2.setOpaque(false);

        jCheckBox3.setText("2");
        jCheckBox3.setOpaque(false);

        jCheckBox4.setText("3");
        jCheckBox4.setOpaque(false);

        jCheckBox5.setText("4");
        jCheckBox5.setOpaque(false);

        jCheckBox6.setText("5");
        jCheckBox6.setOpaque(false);

        jCheckBox7.setText("6");
        jCheckBox7.setOpaque(false);

        jCheckBox8.setText("7");
        jCheckBox8.setOpaque(false);

        jLabel6.setFont(new java.awt.Font("Monospaced", 1, 10)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 0, 0));
        jLabel6.setText("Ахтунг! Неправильная установка этих флажков приведет к зависанию, их корректность не всегда проверяется!");

        jLabel7.setFont(new java.awt.Font("Monospaced", 1, 10)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 0, 0));
        jLabel7.setText("Если у тебя физически нет железки в слоте, то от установки этих флажков она не появится!");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox8))
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox1)
                    .addComponent(jCheckBox2)
                    .addComponent(jCheckBox3)
                    .addComponent(jCheckBox4)
                    .addComponent(jCheckBox5)
                    .addComponent(jCheckBox6)
                    .addComponent(jCheckBox7)
                    .addComponent(jCheckBox8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Параметры MySQL (MariaDB)"));

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Имя БД");

        jTextField5.setText("jneko");

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Хост БД");

        jTextField6.setText("192.168.33.1");

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Логин");

        jTextField7.setText("jneko");

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Пароль");

        jPasswordField2.setText("jneko");

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/view-refresh.png"))); // NOI18N
        jButton3.setText("Проверить подключение");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField5)
                    .addComponent(jTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(jTextField7)
                    .addComponent(jPasswordField2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addContainerGap())
        );

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Параметры сервера [GW]"));

        jTextField8.setText("jservice");

        jLabel18.setFont(new java.awt.Font("Monospaced", 1, 12)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("Порт");

        jTextField9.setText("192.168.33.2");

        jLabel17.setFont(new java.awt.Font("Monospaced", 1, 12)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("IP");

        jTextField10.setText("23");

        jLabel15.setFont(new java.awt.Font("Monospaced", 1, 12)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Логин");

        jLabel16.setFont(new java.awt.Font("Monospaced", 1, 12)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Пароль");

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/view-refresh.png"))); // NOI18N
        jButton4.setText("Проверить подключение");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField10))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField9))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPasswordField3))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jPasswordField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(181, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Настройки", new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/system-run.png")), jPanel3); // NOI18N

        jPanel13.setBackground(java.awt.SystemColor.control);

        jButton10.setText("jButton10");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(xlist1, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(91, 91, 91)
                .addComponent(jButton10)
                .addContainerGap(427, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton10)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(xlist1, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("tab4", jPanel13);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1))
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Тарифные планы");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        __refresh();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void __addACSONT() {
        Thread t = new Thread(() -> {
            ArrayList<Z_MA4000Engine.ACSONTUsers> alx = xMA4000.GetACSONTUsers();
            if (alx == null) {
                JOptionPane.showMessageDialog(this, "Ошибка подключения к MA4000.");
                return;
            }
            
            ArrayList<Z_MA4000Engine.SlotsMACsONTs> axx = xMA4000.GetSlotsMACsONTs();
            if (axx == null) {
                JOptionPane.showMessageDialog(this, "Ошибка подключения к MA4000.");
                return;
            }
            
            ArrayList<Z_MA4000Engine.ACSONT> al = xMA4000.GetACSONT();
            if (al != null) {
                synchronized(model) {
                    for (Z_MA4000Engine.ACSONT ai : al) {
                        Z_MA4000Engine.ACSONTUsers alnz = null;
                        for (Z_MA4000Engine.ACSONTUsers az : alx) { // вложенные циклы со сравнением строк врядли способствуют быстроте выполнения,
                            if (az.eltx.equalsIgnoreCase(ai.eltx)) { // но переделывать лень
                                alnz = az;
                                break;
                            }
                        }    
                        
                        Z_MA4000Engine.SlotsMACsONTs bxx = null;
                        for (Z_MA4000Engine.SlotsMACsONTs xx : axx) {
                            if (xx.eltx.equalsIgnoreCase(ai.eltx)) {
                                bxx = xx;
                                break;
                            }
                        }
                        
                        
                        boolean flag1;
                        if ((jCheckBox9.isSelected())) {
                            flag1 = true;
                        } else {
                            flag1 = (alnz != null) && (bxx != null);
                        }
                        
                        if (flag1) {
                            model.AddItem(
                                        ai.eltx, 
                                        "IP:[" + ((ai.ip.matches("[0-9]{1,3}[\\.][0-9]{1,3}[\\.][0-9]{1,3}[\\.][0-9]{1,3}")) ? ai.ip + "];" : "Неизвестен];") +
                                                ((bxx != null) ? " MAC:["+bxx.mac.split(",")[0]+"];" : ""), 
                                        /*"HW: "+ai.hwName + "; "+ */
                                        "Slot: " + ((bxx != null) ? bxx.slot : "?") + "; " +
                                            ((alnz != null) ? "User: "+alnz.username + "; " : "")
                                        );

                            if (alnz != null) model.getCurrent().AddObject(alnz, "ACSONTUsers");
                            if (bxx != null) model.getCurrent().AddObject(bxx, "SlotsMACsONTs");
                            if ((alnz != null) && (bxx != null)) {
                                StringBuilder sb = new StringBuilder();
                                sb.append(ai.eltx); sb.append("\n");
                                sb.append(ai.firmware); sb.append("\n");
                                sb.append(ai.hwName); sb.append("\n");
                                sb.append(ai.ip); sb.append("\n");
                                sb.append(ai.profile); sb.append("\n");
                                sb.append(bxx.mac); sb.append("\n");
                                sb.append(bxx.vlan); sb.append("\n");
                                sb.append(alnz.username); sb.append("\n");
                                String s = new String(sb);
                                model.getCurrent().AddObject(s, "StringSearch");
                            }
                            model.getCurrent().AddObject(ai, "ACSONT");
                        }
                    }
                    model.Commit();
                    tempModel = new ExtListModel_PON(model);
                    jPanel9.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка подключения к MA4000.");
            }
            
            jComboBox1.setEnabled(true);
            JNekoProgressDisplayClass.Off();
            jLabel8.setText(""+model.getSize());
        });
        t.start();
    }
    
    private void __addNewONT() {
        Thread t = new Thread(() -> {
            ArrayList<Z_MA4000Engine.UnconfiguredONT> al = xMA4000.GetUnconfiguredONT();
            if (al != null) {
                synchronized(model) {
                    for (Z_MA4000Engine.UnconfiguredONT ai : al) {
                        model.AddItem(ai.eltx, "Slot: "+ai.slot, "Channel: "+ai.channel);
                        model.getCurrent().AddObject(ai, "UnconfiguredONT");
                    }
                    model.Commit();
                    tempModel = new ExtListModel_PON(model);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка подключения к MA4000.");
            }
            jComboBox1.setEnabled(true);
            JNekoProgressDisplayClass.Off();
            jLabel8.setText(""+model.getSize());
        });
        t.start();
    }
    
    private void __refresh() {
        if (Z_TelnetEngine.IsConnected() == false) {
            jLabel10.setForeground(Color.RED);
            return;
        }
        jPanel9.setVisible(false); 
        
        switch (jComboBox1.getSelectedIndex()) {
            case 0:
                model.Clear();
                model.Commit();
                tempModel = null;
                break;
            case 1:
                model.Clear();
                model.Commit();
                
                JNekoProgressDisplayClass.On();
                jComboBox1.setEnabled(false);
                __addNewONT();
                break;
            case 2:
                model.Clear();
                model.Commit();
                
                JNekoProgressDisplayClass.On();
                jComboBox1.setEnabled(false);
                __addACSONT();
                break;
            case 3:
                model.Clear();
                model.Commit();
                
                try {
                    sSQLite.intGetLastAddedONTsList(model, tempModel); 
                    jLabel8.setText(""+model.getSize());
                } catch (SQLException ex) {
                    Logger.getLogger(FListONTs.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
        } 
    }
    
    
    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        if (jComboBox1.getSelectedIndex() == xComboIndex) return;
        xComboIndex = jComboBox1.getSelectedIndex();
        
        __refresh();
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private int __telnetConnect() {
        jButton1.setEnabled(false);
        jComboBox1.setEnabled(false);
        JNekoProgressDisplayClass.On();
        Thread t = new Thread(() -> {
            final String    pass    =  new String(jPasswordField1.getPassword()),
                            login   = jTextField1.getText().trim(),
                            host    = jTextField2.getText().trim();
                  String    port    = jTextField3.getText().trim();
            
            if ((port.length() <= 0) || (port.length() > 5)) port = "23";
            
            if ((pass.length() < 2) || (login.length() < 2) || (host.length() < 2)) {
                JOptionPane.showMessageDialog(This, "Данные введены неверно, одно из полей пустое.");
                JNekoProgressDisplayClass.Off();
                jButton1.setEnabled(true);
                jLabel10.setForeground(Color.RED);
                return;
            }
            
            if (Z_TelnetEngine.Connect(host, port, login, pass) != 0) {
                JOptionPane.showMessageDialog(This, "Ошибка подключения к МА4000.");
                JNekoProgressDisplayClass.Off();
                jButton1.setEnabled(true);
                jLabel10.setForeground(Color.RED);
                return;
            }
            
            jLabel10.setForeground(Color.green);
            jComboBox1.setEnabled(true); 
            JNekoProgressDisplayClass.Off();
        });
        t.start();
        
        return 0;
    }

    private void jList1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MousePressed
        if (model.getSize() < 1) return;
        final int index = jList1.getSelectedIndex();
        if (index < 0) return;
        
        if (evt.isPopupTrigger()) {
            switch (jComboBox1.getSelectedIndex()) {
            case 1:
                try {
                    final String ont = ((Z_MA4000Engine.UnconfiguredONT)(model.getElementAt(jList1.getSelectedIndex()).GetObject("UnconfiguredONT"))).eltx;
                    final String slot = ((Z_MA4000Engine.UnconfiguredONT)(model.getElementAt(jList1.getSelectedIndex()).GetObject("UnconfiguredONT"))).slot;
                    jMenuItem6.setText(ont.toUpperCase() + " Slot:" + slot);
                } catch (NullPointerException e) {
                    return;
                }
                
                jPopupMenu2.show(evt.getComponent(), evt.getX(), evt.getY());
                break;
            case 2:
                try {
                    final String ont = ((Z_MA4000Engine.ACSONT)(model.getElementAt(jList1.getSelectedIndex()).GetObject("ACSONT"))).eltx;
                    final String slot = ((Z_MA4000Engine.SlotsMACsONTs)(model.getElementAt(jList1.getSelectedIndex()).GetObject("SlotsMACsONTs"))).slot;
                    jMenuItem1.setText(ont.toUpperCase() + " Slot:" + slot); 
                } catch (NullPointerException e) {
                    return;
                }
                
                jMenuItem4.setVisible(jCheckBox11.isSelected());
                jMenuItem5.setVisible(jCheckBox11.isSelected());
                jSeparator2.setVisible(jCheckBox11.isSelected());
                
                jPopupMenu1.show(evt.getComponent(), evt.getX(), evt.getY());
                break;
            case 3:
                
                break;
            }
        } else {
            jPopupMenu1.setVisible(false);
            jPopupMenu2.setVisible(false);
        }
    }//GEN-LAST:event_jList1MousePressed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        final int q = JOptionPane.showConfirmDialog(this, "Вы уверенны?", "Reset ONT to default", JOptionPane.YES_NO_OPTION);
        if (q == JOptionPane.YES_OPTION) {
            Thread t = new Thread(() -> {
                JNekoProgressDisplayClass.On();
                try {
                    final String ont = ((Z_MA4000Engine.ACSONT)(model.getElementAt(jList1.getSelectedIndex()).GetObject("ACSONT"))).eltx;
                    final String slot = ((Z_MA4000Engine.SlotsMACsONTs)(model.getElementAt(jList1.getSelectedIndex()).GetObject("SlotsMACsONTs"))).slot;
                    final int x = xMA4000.ONTDefault(ont, slot);
                    if (x != 0) {
                        JNekoProgressDisplayClass.Off();
                        JOptionPane.showMessageDialog(this, "Ошибка подключения к MA4000.");
                        return;
                    }
                    JNekoProgressDisplayClass.Off();
                    __refresh();
                } catch (NullPointerException e) {
                    JNekoProgressDisplayClass.Off();
                    JOptionPane.showMessageDialog(this, "NPE. Вероятно, попытка сбросить неактивную или мертвую ONT.");
                }
            });
            t.start();
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        if (model.getSize() < 1) return;

        if (Z_TelnetEngine.IsConnected() == false) {
            jLabel10.setForeground(Color.RED);
            return;
        }
        JNekoProgressDisplayClass.On();

        Thread t = new Thread(() -> {
            final String ont = ((Z_MA4000Engine.ACSONT)(model.getElementAt(jList1.getSelectedIndex()).GetObject("ACSONT"))).eltx; // потенциальный NPE
            final String s = xMA4000.GetRawACSONTInfo(ont);
            if (s == null) {
                JOptionPane.showMessageDialog(this, "Ошибка подключения к MA4000.");
                return;
            }

            JNekoProgressDisplayClass.Off();

            FInfoAboutONT f = new FInfoAboutONT(s);
            f.setVisible(true);
        });
        t.start();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        try {
            FAddNewONT f = new FAddNewONT();
            f.SetInfo(
                    ((Z_MA4000Engine.UnconfiguredONT)(model.getElementAt(jList1.getSelectedIndex()).GetObject("UnconfiguredONT"))).eltx,
                    ((Z_MA4000Engine.UnconfiguredONT)(model.getElementAt(jList1.getSelectedIndex()).GetObject("UnconfiguredONT"))).slot,
                    ((Z_MA4000Engine.UnconfiguredONT)(model.getElementAt(jList1.getSelectedIndex()).GetObject("UnconfiguredONT"))).channel,
                    xMA4000
            );
            f.setVisible(true);
        } catch (NullPointerException e) { }
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        try {
            PreparedStatement ps = sSQLite.gConnection.prepareStatement("INSERT INTO 'ONT_LastAdded' VALUES(?, ?, ?);");
            ps.setLong(1, new Date().getTime());
            ps.setString(2, ((Z_MA4000Engine.SlotsMACsONTs)(model.getElementAt(jList1.getSelectedIndex()).GetObject("SlotsMACsONTs"))).eltx);
            ps.setString(3, ((Z_MA4000Engine.SlotsMACsONTs)(model.getElementAt(jList1.getSelectedIndex()).GetObject("SlotsMACsONTs"))).slot);
            ps.execute();
            JOptionPane.showMessageDialog(this, "Успешно добавлено!.");
        } catch (SQLException | NullPointerException ex) {
            //if (gDEBUG) Logger.getLogger(Z_DBSQLite.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Ошибка при добавлении: " + ex.getMessage());
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void __search() {
        if (tempModel == null) return;
        if (tempModel.getSize() <= 0) return;
        
        final String fstr = jTextField4.getText().trim().toLowerCase();
        if (fstr.length() < 1) {
            model.Clear();
            tempModel.GetAllItems().stream().forEach((a) -> {
                model.AddItem(a);
            });
            model.Commit();
            jLabel8.setText(""+model.getSize());
            return;
        }
        
        final ArrayList<LI_PONElement> al = new ArrayList<>();
        tempModel.GetAllItems().stream().forEach((a) -> {
            try {
                String s = ((String)(a.GetObject("StringSearch"))).toLowerCase();
                if (s.contains(fstr)) {
                    al.add(a);
                }
            } catch (NullPointerException ex) { }
        });
        
        model.Clear();
        al.stream().forEach((a) -> {
            model.AddItem(a);
        });
        model.Commit();
        jLabel8.setText(""+model.getSize());
    }
    
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        __search();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jTextField4CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTextField4CaretUpdate
        if (jCheckBox10.isSelected()) __search();
    }//GEN-LAST:event_jTextField4CaretUpdate

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        final int connTelnet = (Z_TelnetEngine.IsConnected() == true) ? 0 : __telnetConnect();
        if (connTelnet == 0) {
            jLabel10.setForeground(Color.green);
//            __save_conf();
        } else {
            jLabel10.setForeground(Color.RED);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void __refreshUsers12(boolean lock) {
         
    }
    
    private void __refreshUsers() {
//        try {
//            switch (jComboBox2.getSelectedIndex()) {
//                case 0:
//                    modelU.Clear();
//                    modelU.Commit();
//                    tempModelU.Clear();
//                    break;
////                case 1:
////                    Z_DBMySQL.intGetUsersList(modelU, tempModelU, false); 
////                    jLabel19.setText(modelU.getSize()+" / "+tempModelU.getSize());
////                    break;
//                case 2:
//                    Z_DBMySQL.intGetUsersList(modelU, tempModelU, true); 
//                    jLabel19.setText(modelU.getSize()+" / "+tempModelU.getSize());
//                    break;
//                case 3:
//                    Z_DBMySQL.intGetHTSList("Houses", "дом ", modelU, tempModelU); 
//                    jLabel19.setText(modelU.getSize()+" / "+tempModelU.getSize());
//                    jPanel12.setVisible(true);
//                    break;
//                case 4:
//                    Z_DBMySQL.intGetHTSList("Streets", "ул.", modelU, tempModelU); 
//                    jLabel19.setText(modelU.getSize()+" / "+tempModelU.getSize());
//                    jPanel12.setVisible(true);
//                    break;
//                case 5:
//                    Z_DBMySQL.intGetHTSList("Towns", "город ", modelU, tempModelU); 
//                    jPanel12.setVisible(true);
//                    jLabel19.setText(modelU.getSize()+" / "+tempModelU.getSize());
//                    break;
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(FListONTs.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
    
    private void jComboBox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox2ItemStateChanged
        if (jComboBox2.getSelectedIndex() == uComboIndex) return;
        uComboIndex = jComboBox2.getSelectedIndex();
        
        jPanel12.setVisible(false);
        __refreshUsers();
    }//GEN-LAST:event_jComboBox2ItemStateChanged

    private void __searchUsers() {
        if (tempModelU == null) return;
        if (tempModelU.getSize() <= 0) return;
        
        final String fstr = jTextField11.getText().trim().toLowerCase();
        if (fstr.length() < 1) {
            modelU.Clear();
            tempModelU.GetAllItems().stream().forEach((a) -> {
                modelU.AddItem(a);
            });
            modelU.Commit();
            jLabel19.setText(modelU.getSize()+" / "+tempModelU.getSize());
            return;
        }
        
        final ArrayList<LI_UsersAndGroups> al = new ArrayList<>();
        tempModelU.GetAllItems().stream().forEach((a) -> {
            try {
                String s = ((String)(a.GetObject("StringSearch"))).toLowerCase();
                if (s.contains(fstr)) {
                    al.add(a);
                }
            } catch (NullPointerException ex) { }
        });
        
        modelU.Clear();
        al.stream().forEach((a) -> {
            modelU.AddItem(a);
        });
        modelU.Commit();
        jLabel19.setText(modelU.getSize()+" / "+tempModelU.getSize());
    }
        
    private void jTextField11CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTextField11CaretUpdate
        if (jCheckBox12.isSelected()) __searchUsers();
    }//GEN-LAST:event_jTextField11CaretUpdate

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        __searchUsers();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jList2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList2MouseClicked
//        if (modelU.getSize() < 1) return;
//        
//        final int index = jList2.getSelectedIndex();
//        if (index < 0) return;
//        
//        final int cat = jComboBox2.getSelectedIndex();
//        if ((cat >= 3) && (cat <= 5)) {
//            final String tname = (String) (modelU.getElementAt(index).GetObject("TableName"));
//            if (tname == null) return;
//
//            try {
//                String s[] = Z_DBMySQL.intGetHTS(tname, modelU.getElementAt(index).GetDID());
//                if (s == null) return;
//                jTextField12.setText(s[0]);
//                jTextField13.setText(s[1]);  
//            } catch (SQLException ex) {
//                Logger.getLogger(FListONTs.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
        
    }//GEN-LAST:event_jList2MouseClicked

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
//        if (jTextField12.getText().trim().length() < 1) return;
//        if (jTextField13.getText().trim().length() < 1) return;
//        
//        if (modelU.getSize() < 1) return;
//        
//        final int index = jList2.getSelectedIndex();
//        if (index < 0) return;
//        
//        final int cat = jComboBox2.getSelectedIndex();
//        if ((cat >= 3) && (cat <= 5)) {
//            final String tname = (String) (modelU.getElementAt(index).GetObject("TableName"));
//            if (tname == null) return;
//            try {
//                Z_DBMySQL.intUpdateHTS(tname, modelU.getElementAt(index).GetDID(), jTextField12.getText().trim(), jTextField13.getText().trim());
//                __refreshUsers();
//            } catch (SQLException ex) {
//                Logger.getLogger(FListONTs.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
//        if (jTextField12.getText().trim().length() < 1) return;
//        if (jTextField13.getText().trim().length() < 1) return;
//        
//        final int cat = jComboBox2.getSelectedIndex();
//        String tname;
//        if (cat == 3) tname = "Houses";
//        else if (cat == 4) tname = "Streets"; 
//        else if (cat == 5) tname = "Towns";
//        else return;
//        
//        try {
//            Z_DBMySQL.intAddHTS(tname, jTextField12.getText().trim(), jTextField13.getText().trim());
//            __refreshUsers();
//            JOptionPane.showMessageDialog(this, "Успешно добавлено!");
//        } catch (SQLException ex) {
//            Logger.getLogger(FListONTs.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
//        try {
//            Z_DBMySQL.intGetUsersList(modelU, tempModelU, false);
//            jLabel19.setText(modelU.getSize()+" / "+tempModelU.getSize());
//            xUsersTabIndex = 1;
//        } catch (SQLException ex) {
//            Logger.getLogger(FListONTs.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
//        try {
//            Z_DBMySQL.intGetUsersList(modelU, tempModelU, true);
//            jLabel19.setText(modelU.getSize()+" / "+tempModelU.getSize());
//            xUsersTabIndex = 2;
//        } catch (SQLException ex) {
//            Logger.getLogger(FListONTs.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        
        xlist1.AddGroupHeader("users", new Color(200, 255, 200), "Пользователи", new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/get-hot-new-stuff.png")), 3);
        xlist1.AddGroupHeader("sssss", new Color(255, 255, 200), "sadbvsfdb", new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/get-hot-new-stuff.png")), 5);
        
        xlist1.AddItemForSelector("1", "users", "Все", "all", 0);
        xlist1.AddItemForSelector("2", "users", "ВYt dct", "all", 0);
        
        xlist1.AddItemForSelector("3", "sssss", "sdBGFMgdm SDB", "all", 0);
        
        xlist1.Commit();
        
    }//GEN-LAST:event_jButton10ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox10;
    private javax.swing.JCheckBox jCheckBox11;
    private javax.swing.JCheckBox jCheckBox12;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JCheckBox jCheckBox8;
    private javax.swing.JCheckBox jCheckBox9;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JPasswordField jPasswordField2;
    private javax.swing.JPasswordField jPasswordField3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu jPopupMenu2;
    private javax.swing.JPopupMenu jPopupMenu3;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private xlist.XList xlist1;
    // End of variables declaration//GEN-END:variables
}
