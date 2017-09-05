package xlist;

import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;

public interface XListElementActionListener {
    public final static int MOUSE_CLICK = 1, MOUSE_PRESSED = 2, MOUSE_RELEASED = 3, MOUSE_ENTERED = 4, MOUSE_EXITED = 5; 
    
    public void OnItemClick(String GID, String ID, Component c, MouseEvent e);
    public void OnHeaderClick(String GID, JLabel c, boolean isVisible, MouseEvent e, int Type);
}
