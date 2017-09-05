package dbengine;

import dbengine.sMySQL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import jneko.JNekoEventListener;
import xlist.BillingUserMainElement;
import xlist.XListButtonsActionListener;

/****
 * 
 * Этот класс - временная затычка для сборки каркаса программы. Уже в бета-релизе никакого прямого обращения к БД из клиента быть не должно.
 * Класс MySQL так же будет выпилен в релизе, поскольку прямой коннект к БД это лол для подобного класса программ, так даже индусы не делают.
 * 
 * @author lain
 */

public class sMySQLWarappers {
    
//    private static JNekoEventListener jnel = null;
//    
//    public static void SetEventListener(JNekoEventListener __jnel) {
//        jnel = __jnel;
//    }
    
//    public static void xDoPaymentsClass1(String DID, long moneyCount, String moneyComment, boolean minus) {
//        if (moneyCount <= 0) return;
//        try {
//            PreparedStatement ps = sMySQL.GetConnection().prepareStatement(
//                    "UPDATE Users SET userMoney=userMoney"+((minus) ? "-" : "+")+moneyCount+" WHERE did="+DID+";");
//            ps.execute();
//            
//            PreparedStatement ps2 = sMySQL.GetConnection().prepareStatement("INSERT INTO Payments VALUES(?, ?, ?, 1, ?);");
//            ps2.setLong(1, new Date().getTime());
//            ps2.setString(2, DID);
//            ps2.setLong(3, ((minus) ? (-1 * moneyCount) : moneyCount));
//            ps2.setString(4, moneyComment);
//            ps2.execute();
//            if (jnel != null) jnel.OnDoPaymentsClass1(DID, moneyCount, moneyComment, minus); 
//        } catch (SQLException ex) {
//            Logger.getLogger(sMySQL.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
//    public static void xGetUsersList(xlist.XList list, boolean showOnlyLocked, String GID, XListButtonsActionListener listener) {
//        xGetUsersList(list, showOnlyLocked, GID, listener, 0);
//    }
//    
//    public static void xGetUsersList(xlist.XList list, boolean showOnlyLocked, String GID, XListButtonsActionListener listener, int type) {
//        try {
//            boolean IsDeleteAllow = (sSQLite.ReadAPPSettingsLong("AllowDeletingUsers") == 255);
//            ResultSet rs = sMySQL.GetStatement().executeQuery(""
//                    + "SELECT "
//                    + "  Users.*, "
//                    + "  Towns.svalue    AS TownString, "
//                    + "  Streets.svalue  AS StreetString, "
//                    + "  Houses.svalue   AS HouseNumber "
//                    + "FROM "
//                    + "  Users "
//                    + "LEFT JOIN Streets ON (Users.streetID=Streets.did)"
//                    + "LEFT JOIN Towns   ON (Users.townID=Towns.did) "
//                    + "LEFT JOIN Houses  ON (Users.houseID=Houses.did) "
//                    + (showOnlyLocked ? " WHERE (Users.userMoney<0 OR Users.locked=127) " : "")
//                    + " ORDER BY Users.did ASC");
//            if (rs != null) {
//                while (rs.next()) {
//                    String ss = rs.getString("userFullName") + "\n" +
//                            rs.getLong("userMoney") + "\n" +
//                            rs.getString("TownString") + "\n" +
//                            rs.getString("StreetString") + "\n" +
//                            rs.getString("HouseNumber") + "\n" + rs.getString("mobilePhone")+ "\n" +
//                            rs.getString("flatNumber") + "\n" + rs.getString("email")+ "\n" +
//                            rs.getString("eltx") + "\n" + rs.getString("ip")+ "\n" + rs.getString("mac")+ "\n" + rs.getString("userLogin");
//                    
//                    String uaddr = "г."+rs.getString("TownString")+", ул."+rs.getString("StreetString")+ ", дом "+rs.getString("HouseNumber")+", кв."+rs.getString("flatNumber");
//                    String ueltx = (rs.getString("eltx") != null) ? ((rs.getString("eltx").length() > 1) ? rs.getString("eltx") + " Slot " + rs.getString("slot") : "") : "";
//                    String uip = (ueltx.length() > 1) ? rs.getString("ip") : "";
//                    
//                    //fUsersListElement(String name, String money, String address, boolean locked, int iconIndex)
//                    BillingUserMainElement fe = new BillingUserMainElement(rs.getString("userFullName"), (rs.getLong("userMoney")/100)+"",
//                            uaddr, ueltx, uip, 
//                            (rs.getLong("userMoney") < 0), 0, listener, rs.getString("did"), GID, IsDeleteAllow, type);
//                    //AddItem(String ID, String GID, Component element, String tags, int SortIndex)
//                    list.AddItem(rs.getString("did"), GID, fe, ss, 0);
//                }
//                list.Commit(true);
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(sMySQL.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
//    public static void xSetUserIPData(String DID, String eltx, String slot, String ip, String mac) {
//        try {
//            PreparedStatement ps = sMySQL.GetConnection().prepareStatement(
//                      "UPDATE "
//                              + "Users "
//                    + "SET "
//                              + "ip=?, "
//                              + "mac=?, "
//                              + "eltx=?, "
//                              + "slot=? "
//                    + "WHERE "
//                              + "did=?;");
//            
//            ps.setString(1, ip);
//            ps.setString(2, mac);
//            ps.setString(3, eltx);
//            ps.setString(4, slot);
//            ps.setLong(5, Long.parseLong(DID, 10));
//            ps.execute();
//            if (jnel != null) jnel.OnSetUserIPData(DID, eltx, slot, ip, mac);
//        } catch (SQLException ex) {
//            Logger.getLogger(sMySQL.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
//    public static void xSetUserInfo(String DID, String userFullName, String userLogin, String userPassword, String groupID, 
//            String tpID, String eltx, String houseID, String streetID, String townID, String flatNumber, String mobilePhone, String email) {
//        try {
//            PreparedStatement ps = sMySQL.GetConnection().prepareStatement(
//                      "UPDATE "
//                              + "Users "
//                    + "SET "
//                              + "userFullName=?, "
//                              + "userLogin=?, "
//                              + "userPassword=?, "
//                              + "groupID=?, "
//                              + "tpID=?, "
//                              + "houseID=?, "
//                              + "streetID=?, "
//                              + "townID=?, "
//                              + "flatNumber=?, "
//                              + "mobilePhone=?, "
//                              + "email=? "
//                    + "WHERE "
//                              + "did=?;");
//            
//            ps.setString(1, userFullName);
//            ps.setString(2, userLogin);
//            ps.setString(3, userPassword);
//            ps.setString(4, groupID);
//            ps.setString(5, tpID);
//            ps.setString(6, houseID);
//            ps.setString(7, streetID);
//            ps.setString(8, townID);
//            ps.setString(9, flatNumber);
//            ps.setString(10, mobilePhone);
//            ps.setString(11, email);
//            ps.setLong(12, Long.parseLong(DID, 10));
//            ps.execute();
//            if (jnel != null) jnel.OnSetUserInfo(DID, userFullName, userLogin, userPassword, groupID, tpID, eltx, houseID, streetID, townID, flatNumber, mobilePhone, email);
//        } catch (SQLException ex) {
//            Logger.getLogger(sMySQL.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
//    public static void xAddNewUser(String DID, String userFullName, String userLogin, String userPassword, String groupID, 
//            String tpID, String eltx, String houseID, String streetID, String townID, String flatNumber, String mobilePhone, String email) {
//        try {
//            PreparedStatement ps = sMySQL.GetConnection().prepareStatement("INSERT INTO Users VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
//            ps.setLong(1, Long.parseLong(DID, 10));
//            ps.setString(2, userFullName);
//            ps.setString(3, userLogin);
//            ps.setString(4, userPassword);
//            ps.setString(5, "0");
//            ps.setString(6, "0");
//            ps.setString(7, tpID);
//            ps.setString(8, "0");
//            ps.setString(9, "");
//            ps.setString(10, "");
//            ps.setString(11, "");
//            ps.setString(12, "");
//            ps.setString(13, houseID);
//            ps.setString(14, streetID);
//            ps.setString(15, townID);
//            ps.setString(16, flatNumber);
//            ps.setString(17, mobilePhone);
//            ps.setString(18, email);
//            if (jnel != null) jnel.OnAddNewUser(DID, userFullName, userLogin, userPassword, groupID, tpID, eltx, houseID, streetID, townID, flatNumber, mobilePhone, email);
//        } catch (SQLException ex) {
//            Logger.getLogger(sMySQL.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
//    public static void xAddEditTarif(String DID, String tname, String u, String d, String cpm, String state) {
//        try {
//            if (DID != null) {
//                PreparedStatement ps = sMySQL.GetConnection().prepareStatement(
//                                  "UPDATE TarifPlans "
//                                + "SET name=?, uploadSpeed=?, downloadSpeed=?, costPerMonth=?, state=? "
//                                + "WHERE did=?;");
//                ps.setString(1, tname);
//                ps.setString(2, u);
//                ps.setString(3, d);
//                ps.setString(4, cpm);
//                ps.setString(5, state);
//                ps.setString(6, DID);
//                ps.execute();
//                if (jnel != null) jnel.OnTarifUpdated(DID, tname, u, d, cpm, state); 
//            } else {
//                PreparedStatement ps = sMySQL.GetConnection().prepareStatement("INSERT INTO TarifPlans VALUES(?, ?, ?, ?, ?, ?);");
//                ps.setLong(1, new Date().getTime());
//                ps.setString(2, tname);
//                ps.setString(3, u);
//                ps.setString(4, d);
//                ps.setString(5, cpm);
//                ps.setString(6, state);
//                ps.execute();
//                if (jnel != null) jnel.OnTarifCreated(tname, u, d, cpm, state); 
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(sMySQL.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
//    public static void xGetHTSList(xlist.XList model, String tableName, String GID, XListButtonsActionListener listener) {
//        try {
//            ResultSet rs = sMySQL.GetStatement().executeQuery("SELECT * FROM "+tableName+" ORDER BY did ASC;");
//            if (rs != null) {
//                while (rs.next()) {
//                    model.AddHSTElement(rs.getString(1), GID, rs.getString(2), tableName, listener);
//                }
//                model.Commit(true);
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(sMySQL.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
//    public static void xUpdateHTS(String tableName, String ID, String value, String alias) {
//        try {
//            PreparedStatement ps = sMySQL.GetConnection().prepareStatement("UPDATE "+tableName+" SET svalue=?, salias=? WHERE did=?;");
//            ps.setString(1, value);
//            ps.setString(2, alias);
//            ps.setLong(3, Long.parseLong(ID, 10));
//            ps.execute();
//            if (jnel != null) jnel.OnUpdateHTS(tableName, ID, value, alias); 
//        } catch (SQLException ex) {
//            Logger.getLogger(sMySQL.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
//    public static void xDeleteHTS(String tableName, String ID) {
//        try {
//            PreparedStatement ps = sMySQL.GetConnection().prepareStatement("DELETE FROM "+tableName+" WHERE did=?;");
//            ps.setLong(1, Long.parseLong(ID, 10));
//            ps.execute();
//            if (jnel != null) jnel.OnDeleteHTS(tableName, ID); 
//        } catch (SQLException ex) {
//            Logger.getLogger(sMySQL.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
//    public static void xAddHTS(String tableName, String value, String alias) {
//        try {
//            PreparedStatement ps = sMySQL.GetConnection().prepareStatement("INSERT INTO "+tableName+" VALUES(?, ?, ?);");
//            ps.setLong(1, new Date().getTime());
//            ps.setString(2, value);
//            ps.setString(3, alias);
//            ps.execute();
//            if (jnel != null) jnel.OnAddHTS(tableName, value, alias); 
//        } catch (SQLException ex) {
//            Logger.getLogger(sMySQL.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

//    public static String[][] GetFullTable_SQL(String sql) {
//        try {
//            ResultSet rs = sMySQL.GetStatement().executeQuery(sql);
//            if (rs != null) {
//                ArrayList<String[]> al = new ArrayList<>();
//                while (rs.next()) {
//                    String[] sa = new String[rs.getMetaData().getColumnCount()];
//                    for (int i=0; i<sa.length; i++) sa[i] = rs.getString(i+1);
//                    al.add(sa);
//                }
//                
//                String sa2[][] = new String[al.size()][];
//                for (int i=0; i<al.size(); i++) sa2[i] = al.get(i); 
//                
//                return sa2;
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(sMySQL.class.getName()).log(Level.SEVERE, null, ex);
//            return null;
//        }
//        return null;  
//    }
    
//    public static String[][] GetFullTable(String tableName) {
//        return GetFullTable(tableName, 0);
//    }
//    
//    public static String[][] GetFullTable(String tableName, long limit) {
//        return GetFullTable_SQL("SELECT * FROM "+tableName+((limit>0) ? (" ORDER BY did DESC LIMIT 0,"+limit) : "")+";");
//    }
//    
//    public static String[][] GetPaymentsInfoForUser(String DID, long limit) {
//        return GetFullTable_SQL("SELECT * FROM Payments WHERE userDID="+DID+" ORDER BY did DESC LIMIT 0,"+limit+";");
//    }
    
//    public static String[][] GetPaymentsAll(long limit) {
//        return GetFullTable_SQL(
//                  "SELECT Payments.*, Users.userFullName AS uname "
//                + "FROM Payments "
//                          + " LEFT JOIN Users ON (Users.did=Payments.userDID) "
//                + "ORDER BY did DESC "
//                + "LIMIT 0,"+limit+";"); 
//    }
    
//    public static String[] GetFullRowByDID(String tableName, String DID) {
//        try {
//            ResultSet rs = sMySQL.GetStatement().executeQuery("SELECT * FROM "+tableName+" WHERE did="+DID+";");
//            if (rs != null) {
//                rs.next();
//                final int Count = rs.getMetaData().getColumnCount();
//                if (Count <= 0) return null;
//                String s[] = new String[Count+1];
//                for (int i=0; i<Count; i++) s[i+1] = rs.getString(i+1);
//                return s;
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(sMySQL.class.getName()).log(Level.SEVERE, null, ex);
//            return null;
//        }
//        return null;
//    }
    
//    public static void DeleteFromTable(String tableName, String DID) {
//        try {
//            PreparedStatement ps = sMySQL.GetConnection().prepareStatement("DELETE FROM "+tableName+" WHERE did="+DID+";");
//            ps.execute();
//        } catch (SQLException ex) {
//            Logger.getLogger(sMySQL.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
//    public static void AddToLastAddedONT(String ont, String slot) {
//        if (slot.length() > 2) return;
//        try {
//            PreparedStatement ps = sMySQL.GetConnection().prepareStatement("INSERT INTO LastAddedONT VALUES(?, ?, ?);");
//            ps.setLong(1, new Date().getTime()); 
//            ps.setString(2, ont.toUpperCase().trim());
//            ps.setString(3, slot.trim());
//            ps.execute();
//        } catch (SQLException ex) { }
//    }
}