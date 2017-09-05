package ru.jneko.puseradmin.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import ru.jneko.puseradmin.R;

public class WIFIConfigDialog extends DialogFragment {
    public static interface ActionListener {
        public void OnCommit(String ssid, String passwd);
    }

    private ActionListener actionListener;

    private String ssid;
    private String password;

    private EditText
            wifiSSID, wifiPassword;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.wifi_opt_dialog, null);

        wifiSSID = (EditText) v.findViewById(R.id.wifiSSID);
        wifiSSID.setText(ssid);

        wifiPassword = (EditText) v.findViewById(R.id.wifiPassword);
        wifiPassword.setText(password);

        builder.setView(v)
                .setPositiveButton("Commit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (actionListener != null) actionListener.OnCommit(wifiSSID.getText().toString(), wifiPassword.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {  }
                });
        return builder.create();
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ActionListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }
}
