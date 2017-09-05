package ru.mew_hpm.gpontools_v3.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.ArrayList;

public class ListDialog {
    public static void show(String title, Context c, final ArrayList<ListDialogItem> items) {
        final String[] data = new String[items.size()];
        for (int i=0; i<items.size(); i++)
            data[i] = items.get(i).getTitle();

        final AlertDialog.Builder builder2 = new AlertDialog.Builder(c);
        builder2.setTitle(title)
                .setItems(data, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final ListDialogItemActionListener al = items.get(which).getActionListener();
                        if (al != null) al.OnDialogItemClick(items.get(which));
                        dialog.dismiss();
                    }
                });
        builder2.create().show();
    }

    public static void show(String title, Context c, final ListDialogItem... items) {
        final ArrayList<ListDialogItem> itemsAL = new ArrayList<ListDialogItem>();
        for (int i=0; i<items.length; i++) {
            if (items[i] != null) itemsAL.add(items[i]);
        }
        show(title, c, itemsAL);
    }
}
