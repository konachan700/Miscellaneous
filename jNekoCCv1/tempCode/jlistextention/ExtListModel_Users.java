package jlistextention;

import java.util.ArrayList;
import javax.swing.AbstractListModel;

public class ExtListModel_Users extends AbstractListModel<LI_UsersAndGroups> {
    private final ArrayList<LI_UsersAndGroups> eli;

    public ExtListModel_Users() {
        eli = new ArrayList<>();
    }

    public ExtListModel_Users(ExtListModel_Users e) {
        eli = new ArrayList<>(e.eli);
    }

    @Override
    public int getSize() {
        return eli.size();
    }

    public ArrayList<LI_UsersAndGroups> GetAllItems() {
        return eli;
    }

    public void Clear() {
        eli.clear();
    }

    @Override
    public LI_UsersAndGroups getElementAt(int index) {
        return eli.get(index);
    }

    public LI_UsersAndGroups getCurrent() {
        final int c = eli.size();
        if (c < 1) return null;
        return eli.get(c-1);
    }

    public void AddItem(String name, String money, String address, boolean locked, int iconIndex) {
        LI_UsersAndGroups e = new LI_UsersAndGroups(name, money, address, locked, iconIndex);
        e.SetIndex(eli.size()); 
        eli.add(e);
    }

    public void Commit() {
        this.fireContentsChanged(this, 0, eli.size()-1);
    }

    public void AddItem(LI_UsersAndGroups e) {
        eli.add(e);
        this.fireContentsChanged(this, 0, eli.size()-1);
    }
}