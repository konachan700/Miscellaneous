package ru.mew_hpm.gpontools_v3.ui.fragments;

import android.app.Fragment;
import android.widget.EditText;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import ru.mew_hpm.gpontools_v3.R;
import ru.mew_hpm.gpontools_v3.ssh.SSHHelper;
import ru.mew_hpm.gpontools_v3.ssh.SSHHelperEventListener;

@EFragment(R.layout.fragment_waiter)
public class WaiterFragment extends Fragment implements SSHHelperEventListener {
    @ViewById
    TextView waitTitle;

    @ViewById
    EditText logText;

    @AfterViews
    void initThis() {
        logText.setFocusable(false);
        logText.setClickable(true);
    }

    public void create() {
        SSHHelper.addEventsListener("ssh_main_connection", "WaiterFragment", this);
    }

    @Override
    public void OnConnect(String name) {

    }

    @Override
    public void OnError(String threadName, String elName, Exception e) {

    }

    @Override
    public void OnDisconnect(String name) {

    }

    @Override
    public void OnProgress(String text) {
        if (logText != null) logText.setText(text);
    }
}
