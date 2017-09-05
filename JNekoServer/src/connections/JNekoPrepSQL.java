package connections;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class JNekoPrepSQL implements java.io.Serializable {
    private final String SQL;
    private final ArrayList<String> Params = new ArrayList<>();

    public JNekoPrepSQL(String sql) {
        SQL = sql;
    }

    public void AddParam(String p) {
        Params.add(p);
    }

    public void Execute(Connection c) throws SQLException {
        if (c == null) return;
        PreparedStatement ps = c.prepareStatement(SQL);
        for (int i=0; i<Params.size(); i++) {
            ps.setString(i+1, Params.get(i));
        }
        ps.execute();
    }
}