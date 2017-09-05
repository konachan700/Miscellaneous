package jlistextention;

import java.util.ArrayList;
import javax.swing.AbstractListModel;

public class ExtListModel_PON extends AbstractListModel<LI_PONElement> {
    private final ArrayList<LI_PONElement> eli;

    public ExtListModel_PON() {
        eli = new ArrayList<>();
    }

    public ExtListModel_PON(ExtListModel_PON e) {
        eli = new ArrayList<>(e.eli);
    }

    @Override
    public int getSize() {
        return eli.size();
    }

    public ArrayList<LI_PONElement> GetAllItems() {
        return eli;
    }

    public void Clear() {
        eli.clear();
    }

    @Override
    public LI_PONElement getElementAt(int index) {
        return eli.get(index);
    }

    public LI_PONElement getCurrent() {
        final int c = eli.size();
        if (c < 1) return null;
        return eli.get(c-1);
    }

    public void AddItem(String Title, String subTitle, String Text) {
        LI_PONElement e = new LI_PONElement(Title, subTitle, Text);
        e.SetIndex(eli.size()); 
        eli.add(e);
    }

    public void Commit() {
        this.fireContentsChanged(this, 0, eli.size()-1);
    }

    public void AddItem(LI_PONElement e) {
        eli.add(e);
        this.fireContentsChanged(this, 0, eli.size()-1);
    }
}