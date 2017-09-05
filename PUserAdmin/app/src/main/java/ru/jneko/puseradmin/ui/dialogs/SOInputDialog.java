package ru.jneko.puseradmin.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import ru.jneko.puseradmin.R;

public class SOInputDialog extends DialogFragment {
    private String
            title;

    private String
            value;

    private XDialogActionListener
            actionListener;

    private EditText
            numberText;

    private TextView
            infoMessage;

    private CheckBox
            enabledBox;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.service_override_dialog, null);

        numberText = (EditText) v.findViewById(R.id.soDialogInput);
        numberText.setText(value);

        infoMessage = (TextView) v.findViewById(R.id.soDialogTitle);
        infoMessage.setText(title);

        enabledBox = (CheckBox) v.findViewById(R.id.soEnabled);

        builder.setView(v)
                .setPositiveButton("Commit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        value = (enabledBox.isChecked() ? "true" : "false") + ":" + numberText.getText().toString();
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
