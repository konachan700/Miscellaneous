package jneko;

import datasource.JNekoSQLite;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class JNekoStart {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            return;
        }
        
        JNekoProgressDisplayClass.Start();
        JNekoSQLite.Connect();
        
        JNekoFormMain f = new JNekoFormMain();
        f.setVisible(true);
    }
}
