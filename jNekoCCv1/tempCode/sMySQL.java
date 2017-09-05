package dbengine;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JLabel;
import javax.swing.Timer;

public class sMySQL {
    private static String       DBName = null, 
                                DBHost = null, 
                                DBUser = null, 
                                DBPass = null;
    private static Connection   SQLConnection = null;
    private static Statement    SQLStatement  = null;
    private static JLabel       StateMarker   = null;
    private static boolean      StateTimerFlag = false;
    
    private static final Color  gfColor = new Color(0, 200, 0), 
                                gtColor = new Color(0, 150, 0),
                                bColor  = new Color(150, 0, 0);
    
    @SuppressWarnings("Convert2Lambda")
    private static final ActionListener StateTimerListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            StateTimerFlag = !StateTimerFlag;
            if (GetConnectionState()) {
                if (StateMarker != null) StateMarker.setForeground((StateTimerFlag) ? gfColor : gtColor);
            } else {
                if (StateMarker != null) StateMarker.setForeground(bColor);
                Reconnect();
            }
        }
    };
    
    private static final Timer StateTimer = new Timer(1000 * 2, StateTimerListener);
    
    private static void Reconnect() {
        DBName = sSQLite.ReadAPPSettingsString("MYSQL_DBNAME");
        DBHost = sSQLite.ReadAPPSettingsString("MYSQL_HOST");
        DBUser = sSQLite.ReadAPPSettingsString("MYSQL_LOGIN");
        DBPass = sSQLite.ReadAPPSettingsString("MYSQL_PASSWORD");
        
        if ((DBName==null) || (DBHost==null) || (DBUser==null) || (DBPass==null)) return;
        final String cmdline = "jdbc:mysql://" + DBHost + "/" + DBName + "?useUnicode=true&characterEncoding=utf-8&encoding=UTF-8&user=" + DBUser + "&password=" + DBPass;
        try {
            SQLConnection = DriverManager.getConnection(cmdline);
            SQLStatement = SQLConnection.createStatement();
        } catch (SQLException e) { }
    }
    
    public static void RegisterMarker(JLabel jl) {
        if (jl == null) return;
        if (StateMarker == null) {
            StateMarker = jl;
            StateTimer.start();
        }
    }
    
    public static Statement GetStatement() {
        return SQLStatement;
    }
    
    public static Connection GetConnection() {
        return SQLConnection;
    }
    
    public static boolean GetConnectionState() {
        try {
            return SQLConnection.isValid(2);
        } catch (SQLException | NullPointerException ex) {
            return false;
        }
    }

    public static int Connect() {
        DBName = sSQLite.ReadAPPSettingsString("MYSQL_DBNAME");
        DBHost = sSQLite.ReadAPPSettingsString("MYSQL_HOST");
        DBUser = sSQLite.ReadAPPSettingsString("MYSQL_LOGIN");
        DBPass = sSQLite.ReadAPPSettingsString("MYSQL_PASSWORD");
        
        if ((DBName.trim().length() <= 1) || (DBHost.trim().length() <= 1) || (DBUser.trim().length() <= 1) || (DBPass.trim().length() <= 1)) return 1;
        
        final String cmdline = "jdbc:mysql://" + DBHost + "/" + DBName + "?useUnicode=true&characterEncoding=utf-8&encoding=UTF-8&user=" + DBUser + "&password=" + DBPass;
        try {
            SQLConnection = DriverManager.getConnection(cmdline);
            SQLStatement = SQLConnection.createStatement();
            SQLStatement.setQueryTimeout(25);
            SQLConnection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS Users("
                    + "did bigint not null, "
                    + "userFullName char(255), "
                    + "userLogin char(64), "
                    + "userPassword char(16), "
                    + "userMoney bigint, "
                    + "groupID bigint, "
                    + "tpID bigint, "
                    + "locked bigint, "
                    + "ip char(20), "
                    + "mac char(32), "
                    + "eltx char(20), "
                    + "slot char(2), "
                    + "houseID bigint, "
                    + "streetID bigint, "
                    + "townID bigint, "
                    + "flatNumber bigint, "
                    + "mobilePhone char(32), "
                    + "email char(64), "
                    + "UNIQUE(did));");
            
           SQLConnection.createStatement().executeUpdate("CREATE TABLE  if not exists Houses("
                    + "did bigint not null, "
                    + "svalue char(16), "
                    + "houseAlias char(16), "
                    + "UNIQUE(did));");
            
            SQLConnection.createStatement().executeUpdate("CREATE TABLE  if not exists Towns("
                    + "did bigint not null, "
                    + "svalue char(32), "
                    + "townAlias char(16), "
                    + "UNIQUE(did));");
            
            SQLConnection.createStatement().executeUpdate("CREATE TABLE  if not exists Streets("
                    + "did bigint not null, "
                    + "svalue char(64), "
                    + "streetAlias char(16), "
                    + "UNIQUE(did));");
            
            SQLConnection.createStatement().executeUpdate("CREATE TABLE  if not exists Payments("
                    + "did bigint not null, "
                    + "userDID bigint, "
                    + "moneyCount bigint, "
                    + "paymentType bigint, "
                    + "comment text, "
                    + "UNIQUE(did));");
            
            SQLConnection.createStatement().executeUpdate(
                    "CREATE TABLE  if not exists LastAddedONT(did bigint not null, eltx char(20), slot char(2), UNIQUE(eltx));");
            
            SQLConnection.createStatement().executeUpdate(
                    "CREATE TABLE  if not exists TarifPlans(did bigint not null, name char(64), "
                            + "uploadSpeed bigint, downloadSpeed bigint, costPerMonth bigint, state bigint, UNIQUE(did));");
            
        } catch (SQLException e) {
            __toLog("Error: MySQL ["+e.getMessage()+"]");
            return 2;
        }
        
        return 0;
    }
    
    public static int ClearTable(String table) {
        try {
            GetStatement().executeUpdate("TRUNCATE " + table);
        } catch (SQLException ex) {
            __toLog("Error: MySQL ["+ex.getMessage()+"]");
            return 1;
        }
        return 0;
    }
    
    private static void __toLog(String out) {
         System.out.print(out);
    }
}
