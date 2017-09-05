package ru.jneko.puseradmin.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import ru.jneko.puseradmin.R;

public class StringInputDialog extends DialogFragment {
    private String
            title;

    private String
            value;

    private XDialogActionListener
            actionListener;

    private EditText
            textBox;

    private TextView
            infoMessage;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.string_dialog, null);

        textBox = (EditText) v.findViewById(R.id.stringDialogValue);
        textBox.setText(value);

        infoMessage = (TextView) v.findViewById(R.id.stringDialogTitle);
        infoMessage.setText(title);

        builder.setView(v)
                .setPositiveButton("Commit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        value = textBox.getText().toString();
                        actionListener.OnCommit(title, value);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        actionListener.OnCancel();
                    }
                });
        return builder.create();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public XDialogActionListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(XDialogActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
