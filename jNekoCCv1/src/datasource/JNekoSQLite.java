package datasource;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
            gConnection = DriverManager.getConnection("jdbc:sqlite:jneko.db");
            gStatement = gConnection.createStatement();
            gStatement.setQueryTimeout(25);
            gStatement.executeUpdate("CREATE TABLE  if not exists 'ONT_LastAdded'(did bigint not null, eltx char(20), slot char(2), UNIQUE(eltx));");
            gStatement.executeUpdate("CREATE TABLE  if not exists 'APP_StringSettings'(name char(32), value char(64), UNIQUE(name));");
            gStatement.executeUpdate("CREATE TABLE  if not exists 'APP_LongSettings'(name char(32), value bigint, UNIQUE(name));");
            
//            gStatement.executeUpdate("CREATE TABLE  if not exists 'TEMP_ONTS'"
//                    + "(eltx char(32), slot char(2), mac1 char(20), mac2 char(20), UNIQUE(eltx));");
            
        } catch (SQLException | ClassNotFoundException ex) {
            if (gDEBUG) Logger.getLogger(JNekoSQLite.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        }
        return 0;
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
}
