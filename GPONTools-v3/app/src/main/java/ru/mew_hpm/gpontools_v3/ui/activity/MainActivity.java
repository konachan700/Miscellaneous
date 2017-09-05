package ru.mew_hpm.gpontools_v3.ui.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import java.util.HashMap;
import java.util.Map;

import ru.mew_hpm.gpontools_v3.R;
import ru.mew_hpm.gpontools_v3.dao.SSHServerData;
import ru.mew_hpm.gpontools_v3.ssh.SSHHelper;
import ru.mew_hpm.gpontools_v3.ssh.SSHHelperEventListener;
import ru.mew_hpm.gpontools_v3.ui.fragments.ONTListFragment;
import ru.mew_hpm.gpontools_v3.ui.fragments.ONTListFragment_;
import ru.mew_hpm.gpontools_v3.ui.fragments.SSHServersFragment;
import ru.mew_hpm.gpontools_v3.ui.fragments.SSHServersFragmentEventListener;
import ru.mew_hpm.gpontools_v3.ui.fragments.SSHServersFragment_;
import ru.mew_hpm.gpontools_v3.ui.fragments.WaiterEventListener;
import ru.mew_hpm.gpontools_v3.ui.fragments.WaiterFragment;
import ru.mew_hpm.gpontools_v3.ui.fragments.WaiterFragment_;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements SSHServersFragmentEventListener, SSHHelperEventListener, WaiterEventListener {
    private final MainActivity THIS = this;

    boolean runFirst = true,
            waitOn = false;

    private SSHHelper
            ssh = null;

    private final SSHServersFragment
            sshServersSelector = new SSHServersFragment_();

    private final WaiterFragment
            waiterFragment = new WaiterFragment_();

    private final ONTListFragment
            ontListFragment = new ONTListFragment_();

    private String
            currentFragment = null,
            lastFragment = null;

    private final Map<String, Fragment> fragments = new HashMap<String, Fragment>() {{
        put("server_list", sshServersSelector);
        put("onts_list", ontListFragment);
        put("wait", waiterFragment);
    }};

    private void showFragment(String name) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        for (Fragment f : fragments.values()) {
            fragmentTransaction.hide(f);
        }

        if (fragments.containsKey(name)) {
            fragmentTransaction.show(fragments.get(name));
            currentFragment = name;
        }

        fragmentTransaction.commit();
    }

    private void showWait(boolean wait) {
        if (wait) {
            lastFragment = currentFragment;
            showFragment("wait");
        } else {
            showFragment(lastFragment);
        }
    }

    @AfterViews
    void initThis() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!runFirst) return;

        if (savedInstanceState == null) {
            waiterFragment.create();

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            for (Fragment f : fragments.values()) {
                fragmentTransaction.add(R.id.rootContainer, f);
                fragmentTransaction.hide(f);
            }
            fragmentTransaction.commit();

            showFragment("server_list");

            sshServersSelector.setActionListener(this);

            ssh = SSHHelper.add(this, this, "ssh_main_connection");
            SSHHelper.addEventsListener("ssh_main_connection", "rootListener", this);

            runFirst = false;
            ontListFragment.setEventListener(this);
        }
    }

    @Override
    public void OnItemClick(SSHServerData sshd) {
        if (sshd == null) return;

        showWait(true);

        ssh.setSshPass(sshd.getPassword());
        ssh.setSshUser(sshd.getUsername());
        ssh.setSshAddr(sshd.getHost());
        ssh.setSshPort(sshd.getPort());
        ssh.setConnectDisabled(false);
    }

    @Override
    public void OnConnect(String name) {
        showWait(false);

        showFragment("onts_list");
        ontListFragment.refresh();


        /*Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();

        ssh.exec(new ArrayList<SSHCommand>() {{
            add(new SSHCommand("show version", new SSHCommandEventListener() {
                @Override
                public void OnCmdExecResult(SSHCommand cmd) {
                    Toast.makeText(THIS, cmd.getCmdOut(), Toast.LENGTH_LONG).show();
                }
            }));
        }});*/


    }

    @Override
    public void OnError(String threadName, String elName, Exception e) {
        showWait(false);
        Toast.makeText(this, "Connection error: " + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void OnDisconnect(String name) {
        showWait(false);
        showFragment("server_list");
        Toast.makeText(this, "Connection broken or closed.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void OnProgress(String text) {

    }

    @Override
    public void OnWaitStart() {
        showWait(true);
    }

    @Override
    public void OnWaitStop() {
        showWait(false);
    }
}
