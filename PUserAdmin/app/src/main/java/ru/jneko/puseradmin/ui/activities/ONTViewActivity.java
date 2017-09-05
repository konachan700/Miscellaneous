package ru.jneko.puseradmin.ui.activities;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TabHost;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;

import java.util.HashMap;
import java.util.Map;

import ru.jneko.puseradmin.MA4000.CommandsMA4000v2;
import ru.jneko.puseradmin.MA4000.TransactionsMA4000v2;
import ru.jneko.puseradmin.R;
import ru.jneko.puseradmin.dao.ONTInfo;
import ru.jneko.puseradmin.dao.ONTMACInfo;
import ru.jneko.puseradmin.ui.adapters.ONTDisplayInfoAdapter;
import ru.jneko.puseradmin.ui.adapters.ONTPropertyEListAdapter;
import ru.jneko.puseradmin.ui.dialogs.IntegerInputDialog;
import ru.jneko.puseradmin.ui.dialogs.SOInputDialog;
import ru.jneko.puseradmin.ui.dialogs.StringInputDialog;
import ru.jneko.puseradmin.ui.dialogs.WIFIConfigDialog;
import ru.jneko.puseradmin.ui.dialogs.XDialogActionListener;
import ru.jneko.puseradmin.ui.dialogs.YesNoDialog;
import ru.jneko.puseradmin.ui.fragments.ONTFragment;
import ru.jneko.puseradmin.ui.fragments.SpacerFragment;

public class ONTViewActivity extends AppCompatActivity implements XDialogActionListener {
    private final ONTViewActivity THIS = this;

    private ExpandableListView
            allOptionsTree;

    private ONTPropertyEListAdapter
            elistAdapter;

    private TabHost
            tabHost;

    private ListView
            summaryList;

    private ONTDisplayInfoAdapter
            summaryListAdapter;

    private CommandsMA4000v2
            ma4000v2;

    private Map<String, ONTInfo>
            ontInfoList;

    private ONTFragment
            resetToDefaults, changeInternetVlan, changeWifiData, changeProfile, removeONT, changeIP, removeIP, btnPing, btnConsole;

    private ONTInfo
            currentONT;

    private IntegerInputDialog
            intDialog = new IntegerInputDialog();

    private StringInputDialog
            stringDialog = new StringInputDialog();

    private AlertDialog
            yesNoDialog, enableDisableDialog;

    private int
            selectedIndex = 0;

    private SOInputDialog
            soInputDialog = new SOInputDialog();

    private Map<String, String> fillMap() {
        final Map<String, String> sMap = new HashMap<String, String>() {{
            //put("Channel", currentONT.getCmdShowStateInfo().get("Channel"));
            put("Equipment ID", currentONT.getCmdShowStateInfo().get("Equipment ID"));
            put("Version", currentONT.getCmdShowStateInfo().get("Version"));
            //put("State", currentONT.getCmdShowStateInfo().get("State"));
            put("ONT distance", currentONT.getCmdShowStateInfo().get("ONT distance"));
            put("Tx power", currentONT.getCmdShowStateInfo().get("Tx power"));
            put("Rx power", currentONT.getCmdShowStateInfo().get("Rx power"));
            put("RSSI", currentONT.getCmdShowStateInfo().get("RSSI"));
            put("Temperature", currentONT.getCmdShowStateInfo().get("Temperature"));

            put("Ont id", currentONT.getCmdShowConfigInfo().get("Ont id"));

            put("Subscriber ID", currentONT.getCmdShowConfigUserAcsInfo().get("Subscriber ID"));
            put("Profile", currentONT.getCmdShowConfigUserAcsInfo().get("Profile"));
            put("Admin password", currentONT.getCmdShowConfigUserAcsInfo().get("admin_password"));
            put("Wifi ssid", currentONT.getCmdShowConfigUserAcsInfo().get("wifi_ssid"));
            put("Wifi password", currentONT.getCmdShowConfigUserAcsInfo().get("wifi_password"));
            put("Internet vlanid", currentONT.getCmdShowConfigUserAcsInfo().get("internet_vlanid"));

            put("Last contact", currentONT.getCmdShowConfigONTAcsInfo().get("Last contact"));
            put("URL", currentONT.getCmdShowConfigONTAcsInfo().get("URL"));

            for (ONTMACInfo mac : currentONT.getMACs()) {
                put("MAC (svid " + mac.getSvid() + ")", mac.getOntMAC());
            }
        }};
        return sMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ont_main);

        ma4000v2 = CommandsMA4000v2.getParserFromPool("ma4000v2parser");
        if (ma4000v2 == null) return;
        ontInfoList = ma4000v2.getOntInfoList();
        currentONT = ontInfoList.get(ma4000v2.getCurrentONTID());

        elistAdapter = new ONTPropertyEListAdapter(this);
        elistAdapter.addGroup("State");
        elistAdapter.addChilds(currentONT.getCmdShowStateInfo());
        elistAdapter.addGroup("Config");
        elistAdapter.addChilds(currentONT.getCmdShowConfigInfo());
        elistAdapter.addGroup("ACS User");
        elistAdapter.addChilds(currentONT.getCmdShowConfigUserAcsInfo());
        elistAdapter.addGroup("ACS ONT");
        elistAdapter.addChilds(currentONT.getCmdShowConfigONTAcsInfo());

        allOptionsTree = (ExpandableListView) findViewById(R.id.rootONTTree);
        allOptionsTree.setAdapter(elistAdapter);
        allOptionsTree.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                route1((String) elistAdapter.getChild(i, i1), (String) elistAdapter.getValue(i, i1));
                return false;
            }
        });

        final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("")
                .setItems(new String[] {"True", "False"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        yesNoDialog = builder1.create();

        final AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("")
                .setItems(new String[] {"Enable", "Disable"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        enableDisableDialog = builder2.create();

        getSupportActionBar().setTitle(ma4000v2.getCurrentONTID());

        intDialog.setActionListener(this);
        stringDialog.setActionListener(this);
        soInputDialog.setActionListener(this);

        tabHost = (TabHost) findViewById(R.id.tabhosONT);
        tabHost.setup();

        TabHost.TabSpec tabSpec;
        tabSpec = tabHost.newTabSpec("tab1State");
        tabSpec.setIndicator("Summary");
        tabSpec.setContent(R.id.tab1Summary);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tab2Config");
        tabSpec.setIndicator("Actions");
        tabSpec.setContent(R.id.tab2Actions);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tab3ACSUSER");
        tabSpec.setIndicator("Details");
        tabSpec.setContent(R.id.tab3AllSettings);
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTabByTag("tab1State");

        summaryListAdapter = new ONTDisplayInfoAdapter(this, fillMap());

        summaryList = (ListView) findViewById(R.id.summaryList);
        summaryList.setAdapter(summaryListAdapter);

        resetToDefaults = ONTFragment.newInstance(GoogleMaterial.Icon.gmd_refresh, Color.BLACK, "Reset", "Reset ONT parameters to default");
        changeInternetVlan = ONTFragment.newInstance(GoogleMaterial.Icon.gmd_perm_data_setting, Color.BLACK, "Change VLAN", "Change internet VLAN");
        changeWifiData = ONTFragment.newInstance(GoogleMaterial.Icon.gmd_wifi_lock, Color.BLACK, "Change WIFI params", "Change WIFI ssid and password");
        //changeProfile = ONTFragment.newInstance(GoogleMaterial.Icon.gmd_toc, Color.BLACK, "Change profile", "Change acs ont profile");
        removeONT = ONTFragment.newInstance(GoogleMaterial.Icon.gmd_remove_circle, Color.RED, "Remove ONT", "Remove ONT from MA4000");

        changeIP = ONTFragment.newInstance(GoogleMaterial.Icon.gmd_add, Color.BLACK, "Set IP", "Assign static ip for ONT");
        removeIP = ONTFragment.newInstance(GoogleMaterial.Icon.gmd_clear, Color.BLACK, "Remove IP", "Remove static IP from ONT");

        btnPing  = ONTFragment.newInstance(GoogleMaterial.Icon.gmd_import_export, Color.BLACK, "Ping", "Ping google.com from ONT");
        btnConsole = ONTFragment.newInstance(GoogleMaterial.Icon.gmd_desktop_windows, Color.BLACK, "Console", "Open ONT console");

        resetToDefaults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YesNoDialog.show(THIS, "Reset ONT "+ma4000v2.getCurrentONTID()+" to factory defaults?", new YesNoDialog.ActionListener() {
                    @Override
                    public void OnYesClicked() {
                        TransactionsMA4000v2.resetONTToDefaults(THIS, ma4000v2.getCurrentONTID(), ma4000v2.getCurrentSlot());
                    }
                });
            }
        });

        changeInternetVlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final IntegerInputDialog iid = new IntegerInputDialog();
                iid.setTitle("Please, enter new VLAN (1-4095) for ONT "+ma4000v2.getCurrentONTID());
                iid.setValue("1");
                iid.setActionListener(new XDialogActionListener() {
                    @Override
                    public void OnCommit(String title, String value) {
                        int vlan;
                        try {
                            vlan = Integer.parseInt(value);
                            if ((vlan < 1) || (vlan > 4095)) return;
                        } catch (NumberFormatException e) {
                            return;
                        }

                        TransactionsMA4000v2.setVlan(THIS, ma4000v2.getCurrentUser(), ma4000v2.getCurrentONTID(), vlan, ma4000v2.getCurrentSlot());
                    }

                    @Override
                    public void OnCancel() { }
                });
            }
        });

        changeWifiData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final WIFIConfigDialog wd = new WIFIConfigDialog();
                wd.setActionListener(new WIFIConfigDialog.ActionListener() {
                    @Override
                    public void OnCommit(String ssid, String passwd) {
                        if ((ssid.trim().length() < 2) || (passwd.trim().length() < 8)) {
                            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(THIS);
                            dlgAlert.setMessage("The wifi ssid minimal length is 2, wifi password minimal length is 8; exit with no changes.");
                            dlgAlert.setTitle("WIFI configuration error.");
                            dlgAlert.setPositiveButton("OK", null);
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();
                            return;
                        }
                        TransactionsMA4000v2.setWIFIParams(THIS, ma4000v2.getCurrentUser(), ssid, passwd);
                    }
                });
                wd.show(getSupportFragmentManager(), "wifiConf");
            }
        });

        /*changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

        removeONT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final StringInputDialog sid = new StringInputDialog();
                sid.setTitle("For remove ONT "+ma4000v2.getCurrentONTID()+" type YES");
                sid.setValue("");
                sid.setActionListener(new XDialogActionListener() {
                    @Override
                    public void OnCommit(String title, String value) {
                        if (value.contains("YES")) {
                            TransactionsMA4000v2.removeONT(THIS, ma4000v2.getCurrentUser(), ma4000v2.getCurrentONTID(), ma4000v2.getSlots());
                        }
                    }

                    @Override
                    public void OnCancel() { }
                });
                sid.show(getSupportFragmentManager(), "typeYES");
            }
        });

        changeIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        removeIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnPing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnConsole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.sbActions, SpacerFragment.newInstance("ONT service"));
        fragmentTransaction.add(R.id.sbActions, resetToDefaults);
        fragmentTransaction.add(R.id.sbActions, changeInternetVlan);
        fragmentTransaction.add(R.id.sbActions, changeWifiData);
        //fragmentTransaction.add(R.id.sbActions, changeProfile);
        fragmentTransaction.add(R.id.sbActions, removeONT);
        fragmentTransaction.add(R.id.sbActions, SpacerFragment.newInstance("IP service"));
        fragmentTransaction.add(R.id.sbActions, changeIP);
        fragmentTransaction.add(R.id.sbActions, removeIP);
        fragmentTransaction.add(R.id.sbActions, SpacerFragment.newInstance("ONT diagnostic"));
        fragmentTransaction.add(R.id.sbActions, btnPing);
        fragmentTransaction.add(R.id.sbActions, btnConsole);
        fragmentTransaction.commit();
    }

    private void route1(String field, String value) {
        if (!TransactionsMA4000v2.writableParams.contains(field)) return;

        switch (TransactionsMA4000v2.getType(field)) {
            case TEXT:
                stringDialog.setTitle(field);
                stringDialog.setValue(value);
                stringDialog.show(getSupportFragmentManager(), "textDialog");
                break;
            case TRUE_FALSE:
                yesNoDialog.setTitle(field);
                yesNoDialog.show();
                break;
            case INT:
                intDialog.setTitle(field);
                intDialog.setValue(value);
                intDialog.show(getSupportFragmentManager(), "intDialog");
                break;
            case ACTION_TELNET:

                break;
            case ENABLE_DISABLE:
                enableDisableDialog.setTitle(field);
                enableDisableDialog.show();
                break;
            case SERVICE_OVERRIDE:
                soInputDialog.setTitle(field);
                soInputDialog.show(getSupportFragmentManager(), "soDialog");
                break;
        }
    }

    @Override
    public void OnCommit(String title, String value) {
        TransactionsMA4000v2.write(this, title, value);
    }

    @Override
    public void OnCancel() {

    }
}
