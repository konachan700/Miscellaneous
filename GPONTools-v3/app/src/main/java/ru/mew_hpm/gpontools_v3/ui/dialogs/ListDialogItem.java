package ru.mew_hpm.gpontools_v3.ui.dialogs;

public class ListDialogItem {
    private String title;
    private ListDialogItemActionListener actionListener;

    public ListDialogItem(String dTitle, ListDialogItemActionListener al) {
        actionListener = al;
        title = dTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ListDialogItemActionListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(ListDialogItemActionListener actionListener) {
        this.actionListener = actionListener;
    }
}
