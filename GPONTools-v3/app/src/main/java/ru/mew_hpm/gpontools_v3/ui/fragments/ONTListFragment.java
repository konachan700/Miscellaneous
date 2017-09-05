package ru.mew_hpm.gpontools_v3.ui.fragments;

import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.widget.ListView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import ru.mew_hpm.gpontools_v3.R;
import ru.mew_hpm.gpontools_v3.dao.ONTInfo;
import ru.mew_hpm.gpontools_v3.dao.SSHCommand;
import ru.mew_hpm.gpontools_v3.dao.SSHCommandEventListener;
import ru.mew_hpm.gpontools_v3.ssh.SSHHelper;
import ru.mew_hpm.gpontools_v3.ui.adapters.ONTListAdapter;
import ru.mew_hpm.gpontools_v3.ui.adapters.ONTListAdapterActionListener;
import ru.mew_hpm.gpontools_v3.ui.dialogs.ListDialog;
import ru.mew_hpm.gpontools_v3.ui.dialogs.ListDialogItem;
import ru.mew_hpm.gpontools_v3.ui.dialogs.ListDialogItemActionListener;

@EFragment(R.layout.fragment_ont_list)
public class ONTListFragment extends Fragment implements ONTListAdapterActionListener {
    @ViewById
    ListView ontList;

    private WaiterEventListener
            eventListener = null;

    private ONTListAdapter
            ontListAdapter = null;

    private volatile int
            ponPortsCount = 4;

    private final ArrayList<ONTInfo>
            oiList = new ArrayList<ONTInfo>();

    @ViewById
    FloatingActionButton
            floatingActionButton;

    @AfterViews
    void initThis() {
        ontListAdapter = new ONTListAdapter(this.getContext(), this);
        ontList.setAdapter(ontListAdapter);
        floatingActionButton.setImageDrawable(new IconicsDrawable(this.getContext()).icon(GoogleMaterial.Icon.gmd_more_vert).colorRes(R.color.colorFloatIcon).sizeDp(48));
    }

    public void refresh() {
        oiList.clear();
        if (eventListener != null) eventListener.OnWaitStart();

        final SSHHelper ssh = SSHHelper.get("ssh_main_connection");
        ssh.exec(new ArrayList<SSHCommand>() {{
            for (int i=0; i<ponPortsCount; i++) {
                final int globalPort = i;
                add(new SSHCommand("show interface ont "+i+" connected", new SSHCommandEventListener() {
                    @Override
                    public void OnCmdExecResult(SSHCommand cmd) {
                        if (cmd.getCmdOut() == null) return;
                        final String[] lines = cmd.getCmdOut().split("\n");
                        for (String line : lines) {
                            if (ONTInfo.isDataValid(line)) {
                                Log.d("## DEBUG ##", "valid line = "+line);
                                final ONTInfo oi = new ONTInfo(line);
                                oi.setGlobalPort(globalPort+"");
                                oiList.add(oi);
                            }
                        }
                        ontListAdapter.refresh(oiList);
                        Log.d("##NEXT FREE ID##", "ID="+ontListAdapter.getNextONTID(globalPort+""));
                        if (eventListener != null) eventListener.OnWaitStop();
                    }
                }));
            }
        }});
    }

    @Click(R.id.floatingActionButton)
    void OnFloatButtonClick() {
        ListDialog.show("", this.getContext(),
                    new ListDialogItem("Refresh list", new ListDialogItemActionListener() {
                        @Override
                        public void OnDialogItemClick(ListDialogItem di) {
                            refresh();
                        }
                    })
                );
    }

    @Override
    public void OnLongClick(final ONTInfo item) {
        if (item == null) return;
        if (item.getStatus().contentEquals("UNACTIVATED")) {
            ListDialog.show("", this.getContext(),
                    new ListDialogItem("Auto add ONT", new ListDialogItemActionListener() {
                        @Override
                        public void OnDialogItemClick(ListDialogItem di) {
                            addONT(item);
                        }
                    })
                    );
        } else if (item.getStatus().contentEquals("OK")) {
            ListDialog.show("", this.getContext(),
                    new ListDialogItem("Set cross-connect profile", new ListDialogItemActionListener() {
                        @Override
                        public void OnDialogItemClick(ListDialogItem di) {

                        }
                    }),
                    new ListDialogItem("Set dba profile", new ListDialogItemActionListener() {
                        @Override
                        public void OnDialogItemClick(ListDialogItem di) {

                        }
                    }),
                    new ListDialogItem("Set ports profile", new ListDialogItemActionListener() {
                        @Override
                        public void OnDialogItemClick(ListDialogItem di) {

                        }
                    })
            );
        }
    }

    @Override
    public void OnClick(ONTInfo item) {

    }

    public WaiterEventListener getEventListener() {
        return eventListener;
    }

    public void setEventListener(WaiterEventListener eventListener) {
        this.eventListener = eventListener;
    }

    private void addONT(final ONTInfo item) {
        if (eventListener != null) eventListener.OnWaitStart();

        final SSHHelper ssh = SSHHelper.get("ssh_main_connection");
        ssh.exec(new ArrayList<SSHCommand>() {{
            add(new SSHCommand("configure terminal", null));
            add(new SSHCommand("interface ont "+item.getGlobalPort()+"/"+ontListAdapter.getNextONTID(item.getGlobalPort()), null));
            add(new SSHCommand("password 0000000000", null));
            add(new SSHCommand("serial "+item.getSerial(), null));
            add(new SSHCommand("profile ports NTU1", null));
            add(new SSHCommand("service 0 profile cross-connect NTU1", null));
            add(new SSHCommand("service 0 profile dba internet", null));
            add(new SSHCommand("do commit", null));
            add(new SSHCommand("exit", null));
            add(new SSHCommand("exit", new SSHCommandEventListener() {
                @Override
                public void OnCmdExecResult(SSHCommand cmd) {
                    if (eventListener != null) eventListener.OnWaitStop();
                }
            }));
        }});
    }
}
