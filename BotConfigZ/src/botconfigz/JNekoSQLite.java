package botconfigz;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

public class JNekoSQLite {   
    public static Statement     gStatement      = null;
    public static Connection    gConnection     = null;
    public static boolean       gDEBUG          = true;

    public static int Connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            gConnection = DriverManager.getConnection("jdbc:sqlite:jnekoZ.db");
            gStatement = gConnection.createStatement();
            gStatement.setQueryTimeout(25);
            gStatement.executeUpdate("CREATE TABLE  if not exists 'APP_StringSettings'(name char(32), value char(64), UNIQUE(name));");
            gStatement.executeUpdate("CREATE TABLE  if not exists 'APP_LongSettings'(name char(32), value bigint, UNIQUE(name));");   
            gStatement.executeUpdate("CREATE TABLE  if not exists 'APP_i2c_devs'(did bigint, zname char(32), ztype char(32), i2c_addr bigint);");
            gStatement.executeUpdate("CREATE TABLE  if not exists 'APP_i2c_regs_groups'(did bigint, zname char(32), zstate bigint);");
            gStatement.executeUpdate("CREATE TABLE  if not exists 'APP_i2c_regs'(did bigint, gid bigint, zname char(32), zaddr bigint, zmin bigint, zmax bigint, zinit bigint, ztype char(16), zstate bigint, zindex bigint);");
            gStatement.executeUpdate("CREATE TABLE  if not exists 'APP_i2c_regs_links'(did bigint, regid bigint, devid bigint);");
        } catch (SQLException | ClassNotFoundException ex) {
            if (gDEBUG) Logger.getLogger(JNekoSQLite.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        }
        return 0;
    }
    
    public static Map<String, ArrayList<String>> GetTableFull(String tableName) {
        return GetTableFull(tableName, null);
    }
    
    public static Map<String, ArrayList<String>> GetTableFull(String tableName, String where) {
        final Map<String, ArrayList<String>> data = new LinkedHashMap<>();
        
        try {
            final PreparedStatement ps = gConnection.prepareStatement("SELECT * FROM '"+tableName+"'"+((where==null) ? "" : (" WHERE "+where))+";");
            final ResultSet rs = ps.executeQuery();
            if (rs != null) {
                int count = rs.getMetaData().getColumnCount();
                for (int i=0; i<count; i++) {
                    final String tname = rs.getMetaData().getColumnLabel(i+1);
                    final ArrayList<String> row = new ArrayList<>();
                    data.put(tname, row);
                }

                while (rs.next()) {
                    for (int i=0; i<count; i++) {
                        final String tname = rs.getMetaData().getColumnLabel(i+1);
                        data.get(tname).add(rs.getString(i+1));
                    }
                }
                
                return data;
            }
        } catch (SQLException ex) {
            Logger.getLogger(JNekoSQLite.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public static int ClearTable(String table) {
        if ((gStatement == null) || (gConnection == null)) return 2;
        try {
            gStatement.executeUpdate("TRINCATE '"+table+"';");
        } catch (SQLException ex) {
            if (gDEBUG) Logger.getLogger(JNekoSQLite.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        }
        return 0;
    }
    
    private static void __writeAPPSettingsLong(String optName, long value) {
        if ((gStatement == null) || (gConnection == null)) return;
        
        try {
            PreparedStatement ps = gConnection.prepareStatement("INSERT INTO 'APP_LongSettings' VALUES(?, ?);");
            ps.setString(1, optName);
            ps.setLong(2, value); 
            ps.execute();
        } catch (SQLException ex) {
            try {
                PreparedStatement ps = gConnection.prepareStatement("UPDATE 'APP_LongSettings' SET value=? WHERE name=?;");
                ps.setLong(1, value);
                ps.setString(2, optName);
                ps.execute();
            } catch (SQLException ex1) {
                if (gDEBUG) System.err.println("ERROR: "+ex1.getMessage());
            }
        }
    }
    
    public static long ReadAPPSettingsLong(String optName) {
        if ((gStatement == null) || (gConnection == null)) return -1;
        
        try {
            PreparedStatement ps = gConnection.prepareStatement("SELECT * FROM 'APP_LongSettings' WHERE name=?;");
            ps.setString(1, optName); 
            ResultSet rs = ps.executeQuery();
            if (rs != null) {
                final long retval = rs.getLong("value");
                return retval;
            }
        } catch (SQLException ex) {
            if (gDEBUG) System.err.println("ERROR: "+ex.getMessage());
            return -1;
        }
        return -1;
    }
    
    public static void WriteAPPSettingsString(String optName, String value) {
        if ((gStatement == null) || (gConnection == null) || (optName == null) || (value == null)) return;
        
        try {
            PreparedStatement ps = gConnection.prepareStatement("INSERT INTO 'APP_StringSettings' VALUES(?, ?);");
            ps.setString(1, optName);
            ps.setString(2, value); 
            ps.execute();
        } catch (SQLException ex) {
            try {
                PreparedStatement ps = gConnection.prepareStatement("UPDATE 'APP_StringSettings' SET value=? WHERE name=?;");
                ps.setString(1, value);
                ps.setString(2, optName);
                ps.execute();
            } catch (SQLException ex1) {
                if (gDEBUG) System.err.println("ERROR: "+ex1.getMessage());
            }
        }
    }
    
    public static String ReadAPPSettingsString(String optName) {
        if ((gStatement == null) || (gConnection == null) || (optName == null)) return "";
        
        try {
            PreparedStatement ps = gConnection.prepareStatement("SELECT * FROM 'APP_StringSettings' WHERE name=?;");
            ps.setString(1, optName); 
            ResultSet rs = ps.executeQuery();
            if (rs != null) {
                final String retval = rs.getString("value");
                return retval;
            }
        } catch (SQLException ex) {
            if (gDEBUG) System.err.println("ERROR: "+ex.getMessage());
            return "";
        }
        return "";
    }
    
    public static void APPSettings_RegisterActionListener(JCheckBox component, String optName) {
        if ((component == null) || (optName == null)) return;
        component.setSelected((ReadAPPSettingsLong(optName) == 255));
        component.addActionListener(new java.awt.event.ActionListener() {
            private final String x = optName;
            private final JCheckBox l = component;
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                __writeAPPSettingsLong(optName, (l.isSelected() ? 255 : 127));
            }
        });
    }
    
    public static void APPSettings_RegisterActionListener(JTextField component, String optName) {
        if ((component == null) || (optName == null)) return;
        component.setText(ReadAPPSettingsString(optName));
        component.addCaretListener(new javax.swing.event.CaretListener() {
            private final String x = optName;
            private final JTextField l = component;
            @Override
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                WriteAPPSettingsString(x, l.getText());
            }
        });
    }

    public static long MD5Special(String s) {
        MessageDigest m;
        long Dig;
        try {
            m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(s.getBytes());

            byte[] digest = m.digest();
            BigInteger b = new BigInteger(1, digest);
            Dig = b.longValue();
            Dig = (Dig < 0) ? -1 * Dig : Dig;

        } catch (NoSuchAlgorithmException ex) {
            if (gDEBUG) Logger.getLogger(JNekoSQLite.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return Dig;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static void WAddNewI2CDevice(String name, String type, long addr) {
        if ((gStatement == null) || (gConnection == null)) return;
        try {
            PreparedStatement ps = gConnection.prepareStatement("INSERT INTO 'APP_i2c_devs' VALUES(?, ?, ?, ?);");
            ps.setLong(1, new Date().getTime());
            ps.setString(2, name);
            ps.setString(3, type); 
            ps.setLong(4, addr); 
            ps.execute();
        } catch (SQLException ex) {
            if (gDEBUG) Logger.getLogger(JNekoSQLite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void WDeleteI2CDevice(String did) {
        if ((gStatement == null) || (gConnection == null)) return;
        try {
            PreparedStatement ps = gConnection.prepareStatement("DELETE FROM 'APP_i2c_devs' WHERE did=?;");
            ps.setString(1,did);
            ps.execute();
        } catch (SQLException ex) {
            if (gDEBUG) Logger.getLogger(JNekoSQLite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void WAddNewI2CRegGroup(String name) {
        if ((gStatement == null) || (gConnection == null)) return;
        try {
            PreparedStatement ps = gConnection.prepareStatement("INSERT INTO 'APP_i2c_regs_groups' VALUES(?, ?, ?);");
            ps.setLong(1, new Date().getTime());
            ps.setString(2, name);
            ps.setLong(3, 1); 
            ps.execute();
        } catch (SQLException ex) {
            if (gDEBUG) Logger.getLogger(JNekoSQLite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void WDeleteI2CRegGroup(String did) {
        if ((gStatement == null) || (gConnection == null)) return;
        try {
            PreparedStatement ps = gConnection.prepareStatement("UPDATE 'APP_i2c_regs_groups' SET zstate=0 WHERE did=?;");
            ps.setString(1,did);
            ps.execute();
        } catch (SQLException ex) {
            if (gDEBUG) Logger.getLogger(JNekoSQLite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void WAddNewI2CReg(String name, String type, int addr, long min, long max, long init, String gid, long zindex) {
        if ((gStatement == null) || (gConnection == null)) return;
        try {
            //did bigint, gid bigint, zname char(32), zaddr bigint, zmin bigint, zmax bigint, zinit bigint, ztype char(16), zstate bigint
            PreparedStatement ps = gConnection.prepareStatement("INSERT INTO 'APP_i2c_regs' VALUES(?, ?, ?, ?, ?, ?, ?, ?, 1, ?);");
            ps.setLong(1, new Date().getTime());
            ps.setString(2, gid);
            ps.setString(3, name);
            ps.setInt(4, addr); 
            ps.setLong(5, min);
            ps.setLong(6, max);
            ps.setLong(7, init);
            ps.setString(8, type); 
            ps.setLong(9, zindex);
            ps.execute();
        } catch (SQLException ex) {
            if (gDEBUG) Logger.getLogger(JNekoSQLite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void WDeleteI2CReg(String did) {
        if ((gStatement == null) || (gConnection == null)) return;
        try {
            PreparedStatement ps = gConnection.prepareStatement("UPDATE 'APP_i2c_regs' SET zstate=0 WHERE did=?;");
            ps.setString(1,did);
            ps.execute();
        } catch (SQLException ex) {
            if (gDEBUG) Logger.getLogger(JNekoSQLite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void WIndexI2CReg(String did, int index) {
        if ((gStatement == null) || (gConnection == null)) return;
        try {
            PreparedStatement ps = gConnection.prepareStatement("UPDATE 'APP_i2c_regs' SET zindex=? WHERE did=?;");
            ps.setInt(1,index); 
            ps.setString(2,did);
            ps.execute();
        } catch (SQLException ex) {
            if (gDEBUG) Logger.getLogger(JNekoSQLite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void WAddNewI2CLink(String devid, String regid) {
        if ((gStatement == null) || (gConnection == null)) return;
        try {
            //did bigint, regid bigint, devid bigint
            PreparedStatement ps = gConnection.prepareStatement("INSERT INTO 'APP_i2c_regs_links' VALUES(?, ?, ?);");
            ps.setLong(1, new Date().getTime());
            ps.setString(2, regid);
            ps.setString(3, devid);
            ps.execute();
        } catch (SQLException ex) {
            if (gDEBUG) Logger.getLogger(JNekoSQLite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
//    public static void WDeleteI2CLinkByName(String name) {
//        if ((gStatement == null) || (gConnection == null)) return;
//        try {
//            PreparedStatement ps = gConnection.prepareStatement("DELETE FROM 'APP_i2c_devs' WHERE did=?;");
//            ps.setString(1,did);
//            ps.execute();
//        } catch (SQLException ex) {
//            if (gDEBUG) Logger.getLogger(JNekoSQLite.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
    //APP_i2c_regs_links
    //APP_i2c_regs
    
    
    
    
    
    
    
}
