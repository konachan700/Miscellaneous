package jlistextention;

import java.awt.Component;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class ExtListRenderer extends JPanel implements ListCellRenderer {     
    LI_PONElement dli = new LI_PONElement("sample", "sample", "sample");

    @Override
    public Component getListCellRendererComponent(JList arg0, Object arg1, int arg2, boolean arg3, boolean arg4) {
        if (arg1 instanceof LI_PONElement) {
            LI_PONElement li = (LI_PONElement) arg1;
            li.SetSelected(arg3);
            li.SetHover(arg4); 
            return li;
        }

        if (arg1 instanceof LI_UsersAndGroups) {
            LI_UsersAndGroups li = (LI_UsersAndGroups) arg1;
            li.SetSelected(arg3); 
            return li;
        }

        return dli;
    }
}