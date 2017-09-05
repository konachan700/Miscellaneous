package ru.jneko.puseradmin.ui.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import com.jcraft.jsch.Session;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;

import ru.jneko.puseradmin.MA4000.CommandsMA4000v2;
import ru.jneko.puseradmin.R;
import ru.jneko.puseradmin.connect.SSHWorker;
import ru.jneko.puseradmin.db.Database;
import ru.jneko.puseradmin.ui.fragments.ONTFragment;
import ru.jneko.puseradmin.ui.fragments.SpacerFragment;

public class MainActivity extends AppCompatActivity implements  SSHWorker.SSHWorkerResult {
    public final MainActivity
            THIS = this;

    private ONTFragment
            fConnect, fDisconnect, fAllONTs, fPleaseWait;

    private EditText
            sshAddr, sshPort, sshUser, sshPass, sshLogs;

    private boolean
            connectedTimer = true;

    private SharedPreferences
            settings;

    private TabHost
            tabHost;

    private CommandsMA4000v2
            ma4000v2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Database.init(this);

        tabHost = (TabHost) findViewById(R.id.tabhost1);
        tabHost.setup();

        TabHost.TabSpec tabSpec;
        tabSpec = tabHost.newTabSpec("tab1");
        tabSpec.setIndicator("Main page");
        tabSpec.setContent(R.id.tabConnect);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tab2");
        tabSpec.setIndicator("MA4000");
        tabSpec.setContent(R.id.tabMA4000);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tab3");
        tabSpec.setIndicator("Billing");
        tabSpec.setContent(R.id.tabBilling);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tab4");
        tabSpec.setIndicator("Logs");
        tabSpec.setContent(R.id.editText2);
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTabByTag("tab1");

        if (savedInstanceState == null) {
            SSHWorker.addWorkerToPool(THIS, "ma4000ssh");
            ma4000v2 = CommandsMA4000v2.addParserToPool("ma4000v2parser");

            fConnect = ONTFragment.newInstance(GoogleMaterial.Icon.gmd_cloud_done, Color.BLACK, "Connect", "Connect to remote ssh server");
            fDisconnect = ONTFragment.newInstance(GoogleMaterial.Icon.gmd_cloud_off, Color.BLACK, "Disconnect", "Disconnect from remote ssh server");
            fAllONTs = ONTFragment.newInstance(GoogleMaterial.Icon.gmd_view_module, Color.BLACK, "All ONT list", "Edit ONTs settings");
            fPleaseWait = ONTFragment.newInstance(GoogleMaterial.Icon.gmd_graphic_eq, Color.GRAY, "Connecting...", "Please, wait.");

            fConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sshAddr.getText().length() <= 1) return;
                    if (sshUser.getText().length() <= 1) return;
                    if (sshPass.getText().length() <= 1) return;

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.hide(fDisconnect);
                    fragmentTransaction.hide(fConnect);
                    fragmentTransaction.show(fPleaseWait);
                    fragmentTransaction.commit();

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("sshAddr", sshAddr.getText().toString());
                    editor.putString("sshPort", sshPort.getText().toString());
                    editor.putString("sshUser", sshUser.getText().toString());
                    editor.putString("sshPass", sshPass.getText().toString());
                    editor.commit();

                    final SSHWorker sshWX = SSHWorker.getWorkerFromPool("ma4000ssh");
                    if (sshWX != null) {
                        sshWX.setSshAddr(sshAddr.getText().toString());
                        sshWX.setSshPort(Integer.valueOf(sshPort.getText().toString(), 10));
                        sshWX.setSshUser(sshUser.getText().toString());
                        sshWX.setSshPass(sshPass.getText().toString());
                        sshWX.tryConnect();
                    }
                }
            });

            fDisconnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final SSHWorker sshWX = SSHWorker.getWorkerFromPool("ma4000ssh");
                    if (sshWX != null) sshWX.disconnect();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.show(fConnect);
                    fragmentTransaction.hide(fDisconnect);
                    fragmentTransaction.commit();
                }
            });

            fAllONTs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(THIS, RootList.class);
                    startActivity(intent);


                    //ma4000v2.init();
                }
            });

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.container_x1, SpacerFragment.newInstance("Connect"));
            fragmentTransaction.add(R.id.container_x1, fConnect);
            fragmentTransaction.add(R.id.container_x1, fDisconnect);
            fragmentTransaction.add(R.id.container_x1, fPleaseWait);
            fragmentTransaction.add(R.id.container_x1, SpacerFragment.newInstance("MA4000 admin"));
            fragmentTransaction.add(R.id.container_x1, fAllONTs);
            fragmentTransaction.hide(fDisconnect);
            fragmentTransaction.hide(fPleaseWait);
            fragmentTransaction.commit();
        }

        settings = getSharedPreferences("PUserAdmin", 0);

        sshAddr = (EditText) findViewById(R.id.inp_ssh_address);
        sshPort = (EditText) findViewById(R.id.inp_ssh_port);
        sshUser = (EditText) findViewById(R.id.inp_ssh_username);
        sshPass = (EditText) findViewById(R.id.inp_ssh_password);
        sshLogs = (EditText) findViewById(R.id.editText2);

        sshAddr.setText(settings.getString("sshAddr", "192.168.1.1"));
        sshPort.setText(settings.getString("sshPort", "22"));
        sshUser.setText(settings.getString("sshUser", "root"));
        sshPass.setText(settings.getString("sshPass", ""));

        sshLogs.setFocusable(false);
        sshLogs.setClickable(true);

        final SSHWorker sshWX = SSHWorker.getWorkerFromPool("ma4000ssh");
        if (sshWX != null) {
            sshWX.addActionListener(this);
            sshWX.execute();
        }
    }

    @Override
    public void Connected(Session s) {
        Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.show(fDisconnect);
        fragmentTransaction.hide(fConnect);
        fragmentTransaction.hide(fPleaseWait);
        fragmentTransaction.commit();



        /*final SSHWorker sshWX = SSHWorker.getWorkerFromPool("ma4000ssh");
        if (sshWX != null) {
            try {
                sshWX.getSshOutputStream().write("express\r\n".getBytes());
                sshWX.getSshOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    @Override
    public void ConnectError(Exception e) {
        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.show(fConnect);
        fragmentTransaction.hide(fDisconnect);
        fragmentTransaction.hide(fPleaseWait);
        fragmentTransaction.commit();
    }

    @Override
    public void ConnectedTimer() {
        connectedTimer = !connectedTimer;
        //buttonDisconnect.setTextColor((connectedTimer) ? Color.BLACK : Color.GREEN);
    }

    @Override
    public void OnTextReceived(Session s, String text, String currLine) {
        final String line = currLine;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sshLogs.append(line);
            }
        });
    }
}
