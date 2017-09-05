
package jnekoserver;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class JNekoDBWrappers {
    private static Connection CONN = JNekoMySQL.GetConnection();

    private static void WriteStringToBAOS_N(ByteArrayOutputStream in, String str) {
        in.write(str.getBytes(), 0, str.getBytes().length);
        in.write('\n');
    }
    
    private static void WriteStringToBAOS(ByteArrayOutputStream in, String str) {
        in.write(str.getBytes(), 0, str.getBytes().length);
    }
    
    private static long IPToLong(String ip) {
        String ipa[] = ip.trim().split("\\.");
        if (ipa.length < 4) return -1;
        
        long ippart = 0, ipres = 0;
        for (int i=0; i<4; i++) {
            ippart = Long.parseLong(ipa[i], 10);
            ipres += ippart * Math.pow(256, 3 - i);
        }
        
        return ipres;
    }
    
    public static void AddDNSRecordToLogTable(String __did, String __srcIP, String __destIP, String domain) {
        if (CONN == null) return;
        long srcIP = IPToLong(__srcIP), destIP = IPToLong(__destIP), did = Long.parseLong(__did, 10);
        if ((srcIP < 0) || (destIP < 0)) return;
        
        try {
            PreparedStatement ps = CONN.prepareStatement("INSERT INTO DNS_LOGGER VALUES(?, ?, ?, ?);");
            ps.setLong(1, did);
            ps.setLong(2, srcIP);
            ps.setLong(3, destIP);
            ps.setString(4, domain); 
            ps.execute();
        } catch (SQLException ex) { }
    }
    
    public static void ChangeUserInfo(String DID, String userFullName, String userLogin, String userPassword, String groupID, 
            String tpID, String eltx, String houseID, String streetID, String townID, String flatNumber, String mobilePhone, String email) {
        if (CONN == null) return;
        try {
            PreparedStatement ps = CONN.prepareStatement(
                          "UPDATE "
                                  + "Users "
                        + "SET "
                                  + "userFullName=?, "
                                  + "userLogin=?, "
                                  + "userPassword=?, "
                                  + "groupID=?, "
                                  + "tpID=?, "
                                  + "houseID=?, "
                                  + "streetID=?, "
                                  + "townID=?, "
                                  + "flatNumber=?, "
                                  + "mobilePhone=?, "
                                  + "email=? "
                        + "WHERE "
                                  + "did=?;");

                ps.setString(1, userFullName);
                ps.setString(2, userLogin);
                ps.setString(3, userPassword);
                ps.setString(4, groupID);
                ps.setString(5, tpID);
                ps.setString(6, houseID);
                ps.setString(7, streetID);
                ps.setString(8, townID);
                ps.setString(9, flatNumber);
                ps.setString(10, mobilePhone);
                ps.setString(11, email);
                ps.setLong(12, Long.parseLong(DID, 10));
                ps.execute();
            } catch (SQLException ex) { }
    }
    
    public static void AddNewUser(String DID, String userFullName, String userLogin, String userPassword, String groupID, 
            String tpID, String eltx, String houseID, String streetID, String townID, String flatNumber, String mobilePhone, String email) {
        if (CONN == null) return;
        try {
            PreparedStatement ps = CONN.prepareStatement("INSERT INTO Users VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            ps.setLong(1, Long.parseLong(DID, 10));
            ps.setString(2, userFullName);
            ps.setString(3, userLogin);
            ps.setString(4, userPassword);
            ps.setString(5, "0");
            ps.setString(6, "0");
            ps.setString(7, tpID);
            ps.setString(8, "0");
            ps.setString(9, "");
            ps.setString(10, "");
            ps.setString(11, "");
            ps.setString(12, "");
            ps.setString(13, houseID);
            ps.setString(14, streetID);
            ps.setString(15, townID);
            ps.setString(16, flatNumber);
            ps.setString(17, mobilePhone);
            ps.setString(18, email);
            ps.execute();
        } catch (SQLException ex) { }
    }
    
    public static void DoPaymentsClass1(String DID, long moneyCount, String moneyComment, boolean minus) {
        if (CONN == null) return;
        if (moneyCount <= 0) return;
        try {
            PreparedStatement ps = CONN.prepareStatement(
                    "UPDATE Users SET userMoney=userMoney"+((minus) ? "-" : "+")+moneyCount+" WHERE did="+DID+";");
            ps.execute();
            
            PreparedStatement ps2 = CONN.prepareStatement("INSERT INTO Payments VALUES(?, ?, ?, 1, ?);");
            ps2.setLong(1, new Date().getTime());
            ps2.setString(2, DID);
            ps2.setLong(3, ((minus) ? (-1 * moneyCount) : moneyCount));
            ps2.setString(4, moneyComment);
            ps2.execute();
        } catch (SQLException ex) { }
    }
    
    public static void SetUserIPData(String DID, String eltx, String slot, String ip, String mac) {
        if (CONN == null) return;
        try {
            PreparedStatement ps = CONN.prepareStatement(
                      "UPDATE "
                              + "Users "
                    + "SET "
                              + "ip=?, "
                              + "mac=?, "
                              + "eltx=?, "
                              + "slot=? "
                    + "WHERE "
                              + "did=?;");
            
            ps.setString(1, ip.trim());
            ps.setString(2, mac.trim());
            ps.setString(3, eltx.trim());
            ps.setString(4, slot.trim());
            ps.setLong(5, Long.parseLong(DID, 10));
            ps.execute();
        } catch (SQLException ex) { }
    }
    
    public static void AddToLastAddedONT(String ont, String slot) {
        if (CONN == null) return;
        if (slot.length() > 2) return;
        try {
            PreparedStatement ps = CONN.prepareStatement("INSERT INTO LastAddedONT VALUES(?, ?, ?);");
            ps.setLong(1, new Date().getTime()); 
            ps.setString(2, ont.toUpperCase().trim());
            ps.setString(3, slot.trim());
            ps.execute();
        } catch (SQLException ex) { }
    }
    
    public static void DeleteFromTable(String tableName, String DID) {
        if (CONN == null) return;
        try {
            PreparedStatement ps = CONN.prepareStatement("DELETE FROM "+tableName+" WHERE did="+DID+";");
            ps.execute();
        } catch (SQLException ex) { }
    }
    
    public static void AddHTS(String tableName, String value, String alias) {
        if (CONN == null) return;
        try {
            PreparedStatement ps = CONN.prepareStatement("INSERT INTO "+tableName+" VALUES(?, ?, ?);");
            ps.setLong(1, new Date().getTime());
            ps.setString(2, value);
            ps.setString(3, alias);
            ps.execute();
        } catch (SQLException ex) { }
    }
    
    public static void UpdateHTS(String tableName, String ID, String value, String alias) {
        if (CONN == null) return;
        try {
            PreparedStatement ps = CONN.prepareStatement("UPDATE "+tableName+" SET svalue=? WHERE did=?;");
            ps.setString(1, value);
            //ps.setString(2, alias);
            ps.setLong(2, Long.parseLong(ID, 10));
            ps.execute();
        } catch (SQLException ex) { }
    }
    
    public static void AddEditTarif(String DID, String tname, String u, String d, String cpm, String state) {
        if (CONN == null) return;
        try {
            if (DID.length() > 1) {
                PreparedStatement ps = CONN.prepareStatement(
                                  "UPDATE TarifPlans "
                                + "SET name=?, uploadSpeed=?, downloadSpeed=?, costPerMonth=?, state=? "
                                + "WHERE did=?;");
                ps.setString(1, tname);
                ps.setString(2, u);
                ps.setString(3, d);
                ps.setString(4, cpm);
                ps.setString(5, state);
                ps.setString(6, DID);
                ps.execute();
            } else {
                PreparedStatement ps = CONN.prepareStatement("INSERT INTO TarifPlans VALUES(?, ?, ?, ?, ?, ?);");
                ps.setLong(1, new Date().getTime());
                ps.setString(2, tname);
                ps.setString(3, u);
                ps.setString(4, d);
                ps.setString(5, cpm);
                ps.setString(6, state);
                ps.execute();
            }
        } catch (SQLException ex) { }
    }
    
    public static void GetHTSList(JNekoRunnable xr, String tableName, String GID) {
        if (CONN == null) return;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ResultSet rs = CONN.createStatement().executeQuery("SELECT * FROM "+tableName+" ORDER BY did ASC;");
            if (rs != null) {
                while (rs.next()) {
                    WriteStringToBAOS(baos, rs.getString(1).replace("||", " ") + "||" + rs.getString(2).replace("||", " ") + "\n");
                }
                xr.CryptWrite(baos);
            }
        } catch (SQLException ex) { }
    }
    
    public static void GetFullRowByDID(JNekoRunnable xr, String tableName, String DID) {
        if (CONN == null) return;
        try {
            ResultSet rs = CONN.createStatement().executeQuery("SELECT * FROM "+tableName+" WHERE did="+DID+";");
            if (rs != null) {
                rs.next();
                final int Count = rs.getMetaData().getColumnCount();
                if (Count <= 0) return;
                String s = "";
                for (int i=0; i<Count; i++) s += rs.getString(i+1).replace("||", " ") + "\n";
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                WriteStringToBAOS(baos, s);
                xr.CryptWrite(baos);
            }
        } catch (SQLException ex) { }
    }
    
    private static String __GetFullTable_SQL(String sql) {
        try {
            ResultSet rs = CONN.createStatement().executeQuery(sql);
            if (rs != null) {
                String s = "";
                while (rs.next()) {
                    int x = rs.getMetaData().getColumnCount();
                    for (int i=0; i<x; i++) s += rs.getString(i+1).replace("||", " ") + "||";
                    s += "\n";
                }
                return s;
            }
        } catch (SQLException ex) {
            return null;
        }
        return null;  
    }
    
    public static void GetFullTable_SQL(JNekoRunnable xr, String sql) {
        final String s = __GetFullTable_SQL(sql);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        WriteStringToBAOS(baos, s);
        xr.CryptWrite(baos);
    }
    
    public static void GetPaymentsAll(JNekoRunnable xr, String limit) {
        GetFullTable_SQL(xr, 
                  "SELECT Payments.*, Users.userFullName AS uname "
                + "FROM Payments "
                          + " LEFT JOIN Users ON (Users.did=Payments.userDID) "
                + "ORDER BY did DESC "
                + "LIMIT 0,"+limit+";"); 
    }
    
    public static void GetPaymentsInfoForUser(JNekoRunnable xr, String DID, String limit) {
        GetFullTable_SQL(xr, "SELECT * FROM Payments WHERE userDID="+DID+" ORDER BY did DESC LIMIT 0,"+limit+";");
    }
    
    public static void GetFullTable(JNekoRunnable xr, String tableName, String __limit) {
        long limit = Long.parseLong(__limit, 10);
        GetFullTable_SQL(xr, "SELECT * FROM "+tableName+((limit>0) ? (" ORDER BY did DESC LIMIT 0,"+limit) : "")+";");
    }
    
    public static void GetUsersList(JNekoRunnable xr, String __showOnlyLocked) {
        boolean showOnlyLocked = __showOnlyLocked.contains("1");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
            ResultSet rs = CONN.createStatement().executeQuery(""
                    + "SELECT "
                    + "  Users.*, "
                    + "  Towns.svalue    AS TownString, "
                    + "  Streets.svalue  AS StreetString, "
                    + "  Houses.svalue   AS HouseNumber "
                    + "FROM "
                    + "  Users "
                    + "LEFT JOIN Streets ON (Users.streetID=Streets.did)"
                    + "LEFT JOIN Towns   ON (Users.townID=Towns.did) "
                    + "LEFT JOIN Houses  ON (Users.houseID=Houses.did) "
                    + (showOnlyLocked ? " WHERE (Users.userMoney<0 OR Users.locked=127) " : "")
                    + " ORDER BY Users.did DESC");
            if (rs != null) {
                String s = "";
                while (rs.next()) {
                    int x = rs.getMetaData().getColumnCount();
                    for (int i=0; i<x; i++) s += rs.getString(i+1).replace("||", " ") + "||"; 
                    s += "\n";
                }
                WriteStringToBAOS(baos, s);
                xr.CryptWrite(baos);
            }
        } catch (SQLException ex) { }
    }
    
    public static void GetIfaceList(JNekoRunnable xr) {
        try {
            ResultSet rs = CONN.createStatement().executeQuery("SELECT DISTINCT ifaceID FROM TrafStatLastHour ORDER BY ifaceID DESC");
            if (rs != null) {
                String s = "";
                while (rs.next()) {
                    s += rs.getString("ifaceID") + "||";
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                WriteStringToBAOS(baos, s);
                xr.CryptWrite(baos);
            }
        } catch (SQLException ex) { } 
    }
    
    
    
    
    
    
    
    
    
    private static void __addGLMRow(long __money, long __tpid) {
        try {
            PreparedStatement ps2 = CONN.prepareStatement("INSERT INTO DailyMoneyLog VALUES(0, ?, ?, ?);");
            final long date = (new Date().getTime()) / (1000 * 60 * 60 * 24);
            ps2.setLong(1, date);
            ps2.setLong(2, __money);
            ps2.setLong(3, __tpid);
            ps2.execute();
        } catch (SQLException ex) { } 
    }
    
    public static long GetLastMinus() {
        if (CONN == null) return 0;
        try {
            ResultSet rs = CONN.createStatement().executeQuery("SELECT did FROM DailyMoneyLog ORDER BY did DESC LIMIT 1;");
            if (rs != null) {
                if (rs.next()) {
                    return rs.getLong("did");
                } else {
                    __addGLMRow(0, 0);
                    return 0;
                }
            } else {
                __addGLMRow(0, 0);
            }
        } catch (SQLException ex) { } 
        return 0;
    }
    
    public static void GetDailyFee() {
        Calendar c = new GregorianCalendar();
        c.setTime(new Date());
        final long md = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        try {
            ResultSet rs = CONN.createStatement().executeQuery("SELECT * FROM TarifPlans;");
            if (rs != null) {
                while (rs.next()) {
                    long feeMonth = rs.getLong("costPerMonth");
                    long dailyFee = feeMonth / md;
                    System.out.println("Daily fee: "+(dailyFee/100)+"; TP: \""+rs.getString("name")+"\"; Day in month: "+md+";");
                    
                    PreparedStatement ps = CONN.prepareStatement(
                            "UPDATE Users SET userMoney=userMoney-? WHERE userMoney>=0 AND locked=0 AND tpID=?;");
                    ps.setLong(1, dailyFee);
                    ps.setLong(2, rs.getLong("did")); 
                    ps.execute();
                    
                    __addGLMRow(dailyFee, rs.getLong("did"));
                }
                
            }
        } catch (SQLException ex) { } 
    }
}
