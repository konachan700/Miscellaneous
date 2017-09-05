package jnekoserver;

import connections.JNekoPrepSQL;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

public class JNekoMySQL {
    public static String                DBName = null, 
                                        DBHost = null, 
                                        DBUser = null, 
                                        DBPass = null;
    private static Connection           SQLConnection = null;
    private static Statement            SQLStatement  = null;
    private static ArrayList<String>    mSQL = new ArrayList<>();
    
    @SuppressWarnings("Convert2Lambda")
    private static final ActionListener StateTimerListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            if (GetConnectionState()==false) {
                Reconnect();
            }
        }
    };
    
    private static final Timer StateTimer = new Timer(1000 * 2, StateTimerListener);
    
    private static void _L(String s) {
        System.out.println(s);
    }
    
    private static int GetSettings() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("server.conf");
            Properties p = new Properties();
            p.load(fis);
            
            DBName = p.getProperty("mysql_dbname", "jneko");
            DBHost = p.getProperty("mysql_dbhost", "localhost");
            DBUser = p.getProperty("mysql_dbuser", "jneko");
            DBPass = p.getProperty("mysql_dbpass", "jneko");
            
            fis.close();
        } catch (IOException ex) {
            _L("GetSettings() IOException 1: "+ex.getMessage());
            return 1;
        } finally {
            try {
                if (fis != null) fis.close();
            } catch (IOException ex) {
                _L("GetSettings() IOException 2: "+ex.getMessage());
                return 1;
            }
        }
        return 0;
    }
    
    private static void Reconnect() {
        if (GetSettings() != 0) return;
        final String cmdline = "jdbc:mysql://" + DBHost + "/" + DBName + "?useUnicode=true&characterEncoding=utf-8&encoding=UTF-8&user=" + DBUser + "&password=" + DBPass;
        try {
            SQLConnection = DriverManager.getConnection(cmdline);
            SQLStatement = SQLConnection.createStatement();
        } catch (SQLException e) { 
            _L("Reconnect() SQLException: "+e.getMessage());
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
        if (GetSettings() != 0) return 1;
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
            
            SQLConnection.createStatement().executeUpdate(
                    "CREATE TABLE if not exists TrafStatM(did bigint not null, ifaceID char(8), transmit bigint, receive bigint);");
            
            SQLConnection.createStatement().executeUpdate(
                    "CREATE TABLE if not exists DailyMoneyLog(id bigint not null primary key auto_increment, did bigint not null, moneyflag bigint, tpid bigint);");

            SQLConnection.createStatement().executeUpdate(
                    "CREATE TABLE  if not exists DNS_LOGGER(did integer not null, srcip integer, dstip integer, domain char(64));");

            SQLConnection.createStatement().executeUpdate(
                    "CREATE TABLE if not exists Routers_ModelList(did integer not null, serialpart char(12), title char(64));");
            
            StateTimer.start();
        } catch (SQLException e) {
            _L("Connect() SQLException: "+e.getMessage());
            return 2;
        }
        
        return 0;
    }
    
    public static void MultiInsertInit(String table) {
        mSQL.clear();
        mSQL.add("INSERT INTO " + table + " VALUES ");
    }
    
    public static void MultiInsert(String sql) {
        mSQL.add(sql);
    }
    
    public static void MultiInsertCommit() {
        final StringBuilder sb = new StringBuilder();
        final int strCount = mSQL.size();
        
        for (int i=0; i<strCount; i++) {
            if ((i==0) ||  (i==(strCount-1)))
                sb.append(mSQL.get(i));
            else 
                sb.append(mSQL.get(i)+",");
        }
        sb.append(";");
        
        final String sql = sb.substring(0);
        
        try {
            SQLConnection.createStatement().execute(sql);
        } catch (SQLException ex) {
            _L("MultiInsertCommit() SQLException: "+ex.getMessage());
        }
    }
    
    public static int ClearTable(String table) {
        try {
            GetStatement().executeUpdate("TRUNCATE " + table);
        } catch (SQLException ex) {
            _L("ClearTable() SQLException: "+ex.getMessage());
            return 1;
        }
        return 0;
    }
    
    public static ArrayList<Map<String, String>> ExecSQL_Select(String sql) {
        try {
            ResultSet rs = GetStatement().executeQuery(sql);
            if (rs != null) {
                final ArrayList<Map<String, String>> xx = new ArrayList<Map<String, String>>();
                while (rs.next()) {
                    int ccount = rs.getMetaData().getColumnCount();
                    Map<String, String> zz = new HashMap<String, String>();
                    for (int i=0; i<ccount; i++) {
                        zz.put(rs.getMetaData().getColumnLabel(i+1), rs.getString(i+1));
                    }
                    xx.add(zz);
                }
                return xx;
            }
        } catch (SQLException ex) {
            _L("GetFullTable_SQL() SQLException: "+ex.getMessage());
            return null;
        }
        return null;  
    }
    
    public static void ExecSQL_InsertUpdate(byte[] buffer, int start, int count) {
        try {
            final ByteArrayInputStream bais = new ByteArrayInputStream(buffer, start, count);
            final ObjectInputStream ois = new ObjectInputStream(bais);
            JNekoPrepSQL ps = (JNekoPrepSQL) ois.readObject();
            if (ps != null) ps.Execute(SQLConnection);
        } catch (IOException | ClassNotFoundException | SQLException ex) {
            _L("ExecSQL_InsertUpdate() error: "+ex.getMessage()+ "; CL: "+ex.getClass().getName());
        }
    }

    public static String[][] GetFullTable_SQL(String sql) {
        try {
            final ResultSet rs = GetStatement().executeQuery(sql);
            if (rs != null) {
                final ArrayList<String[]> al = new ArrayList<>();
                while (rs.next()) {
                    String[] sa = new String[rs.getMetaData().getColumnCount()];
                    for (int i=0; i<sa.length; i++) sa[i] = rs.getString(i+1);
                    al.add(sa);
                }
                
                String sa2[][] = new String[al.size()][];
                for (int i=0; i<al.size(); i++) sa2[i] = al.get(i); 
                
                return sa2;
            }
        } catch (SQLException ex) {
            _L("GetFullTable_SQL() SQLException: "+ex.getMessage());
            return null;
        }
        return null;  
    }
   
}
