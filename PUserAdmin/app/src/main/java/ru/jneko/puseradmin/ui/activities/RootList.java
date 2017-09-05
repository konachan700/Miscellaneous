package ru.jneko.puseradmin.ui.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import ru.jneko.puseradmin.MA4000.CommandsMA4000v2;
import ru.jneko.puseradmin.R;
import ru.jneko.puseradmin.dao.ONTInfo;
import ru.jneko.puseradmin.ui.adapters.ONTFragmentAdapter;

public class RootList extends AppCompatActivity {
    private enum ParserAction {
        REFRESH_ONT_LIST,
        GET_SINGLE_ONT_INFO,
        NO_ACTION
    }

    private volatile String
            currentONTID = null;

    private volatile ParserAction
            parserAction = ParserAction.NO_ACTION;

    private final RootList
            THIS = this;

    private ListView
            lvMain;

    private ImageButton
            btnSearch, btnClear;

    private EditText
            searchText;

    private TextView
            textView7;

    private ONTFragmentAdapter
            adapter;

    private LinearLayout
            waiter, containerMain;

    private final Map<String, ONTInfo>
            ontInfoList = new HashMap<>();

    private volatile boolean
            refreshState = false,
            stopState = true;

    private class UsrBgWorker extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            while (stopState) {
                if (refreshState) {
                    refreshState = false;

                    final CommandsMA4000v2 ma4000v2 = CommandsMA4000v2.getParserFromPool("ma4000v2parser");
                    if (ma4000v2 == null) return null;

                    switch (parserAction) {
                        case REFRESH_ONT_LIST:
                            synchronized (ontInfoList) {
                                ontInfoList.clear();
                                ma4000v2.refreshONTList();
                                ontInfoList.putAll(ma4000v2.getOntInfoList());
                            }

                            THIS.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.refresh();
                                    containerMain.setVisibility(View.VISIBLE);
                                    waiter.setVisibility(View.GONE);
                                    textView7.setText("ONT total count: " + adapter.getCount());
                                }
                            });
                            break;
                        case GET_SINGLE_ONT_INFO:
                            ma4000v2.setCurrentONTID(currentONTID);
                            ma4000v2.refreshCurrentONTInfo();
                            ma4000v2.refreshCurrentACSInfo();

                            THIS.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    containerMain.setVisibility(View.VISIBLE);
                                    waiter.setVisibility(View.GONE);

                                    Intent intent = new Intent(THIS, ONTViewActivity.class);
                                    startActivity(intent);
                                }
                            });
                            break;
                    }

                    parserAction = ParserAction.NO_ACTION;
                }
            }
            return null;
        }
    }

    private UsrBgWorker
            worker;

    private void listrefresh() {
        searchText.setText("");
        adapter.setFilterString("");
        containerMain.setVisibility(View.GONE);
        waiter.setVisibility(View.VISIBLE);

        parserAction = ParserAction.REFRESH_ONT_LIST;
        refreshState = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_refresh_action) {
            listrefresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getSupportActionBar().hide();
        getSupportActionBar().setTitle("All ONT list");

        setContentView(R.layout.activity_root_list);

        containerMain = (LinearLayout) findViewById(R.id.containerMain);
        btnSearch = (ImageButton) findViewById(R.id.searchButton);
        btnClear = (ImageButton) findViewById(R.id.searchClearButton);
        searchText = (EditText) findViewById(R.id.searchTextBox);
        textView7 = (TextView) findViewById(R.id.textView7);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText.setText("");
                adapter.setFilterString("");
                adapter.refresh();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.setFilterString(searchText.getText().toString().trim());
                adapter.refresh();
            }
        });

        lvMain = (ListView) findViewById(R.id.lvMain);
        adapter = new ONTFragmentAdapter(this, ontInfoList);
        lvMain.setAdapter(adapter);
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentONTID = (String) adapter.getItem(i);
                if (currentONTID == null) return;

                containerMain.setVisibility(View.GONE);
                waiter.setVisibility(View.VISIBLE);

                parserAction = ParserAction.GET_SINGLE_ONT_INFO;
                refreshState = true;
            }
        });

        waiter = (LinearLayout) findViewById(R.id.waitFrag1);

        if (savedInstanceState == null) {
            worker = new UsrBgWorker();
            worker.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            listrefresh();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopState = false;
        worker.cancel(true);
    }
}
