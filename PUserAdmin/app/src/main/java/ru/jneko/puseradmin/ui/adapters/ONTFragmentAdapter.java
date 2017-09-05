package ru.jneko.puseradmin.ui.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import ru.jneko.puseradmin.R;
import ru.jneko.puseradmin.dao.ONTInfo;

public class ONTFragmentAdapter extends BaseAdapter {
    private final Map<String, ONTInfo>
            ontInfoList;

    private final ArrayList<String>
            ontList;

    private final LayoutInflater
            lInflater;

    private String
            dataFilter = "";

    public ONTFragmentAdapter(Context c, Map<String, ONTInfo> ontInfo) {
        lInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ontInfoList = ontInfo;
        ontList = new ArrayList(ontInfo.keySet());
    }

    public void setFilterString(String filter) {
        dataFilter = filter;
    }

    public void refresh() {
        ontList.clear();

        if (dataFilter.isEmpty())
            ontList.addAll(ontInfoList.keySet());
        else {
            final Collection<ONTInfo> set = ontInfoList.values();
            for (ONTInfo val : set) {
                if (val.getStatus().getOntSerial().toLowerCase().contains(dataFilter.toLowerCase())) {
                    ontList.add(val.getStatus().getOntSerial());
                    continue;
                }

                if (val.containMAC(dataFilter)) {
                    ontList.add(val.getStatus().getOntSerial());
                    continue;
                }
            }
        }

        if (ontList.size() > 1)
            Collections.sort(ontList, new Comparator<String>() {
                @Override
                public int compare(String o, String t1) {
                    final ONTInfo ox = ontInfoList.get(o);
                    final ONTInfo tx = ontInfoList.get(t1);

                    final int compareVal1 = ox.getStatus().getStatus().compareTo(tx.getStatus().getStatus());
                    final Integer oval = (ox.getSlot()*10) + ox.getChannel(),
                            tval = (tx.getSlot()*10) + tx.getChannel();

                    return (compareVal1 == 0) ? oval.compareTo(tval): compareVal1;
                }
            });

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return ontList.size();
    }

    @Override
    public Object getItem(int i) {
        return ontList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = lInflater.inflate(R.layout.ont1_fragment, viewGroup, false);

        view.setBackgroundResource(R.drawable.border_null);

        final String ont = ontList.get(i);
        ((TextView) view.findViewById(R.id.textView5)).setText(ont);
        ((TextView) view.findViewById(R.id.textView5)).setTextColor(
                (ontInfoList.get(ont).getStatus().getStatus().contains("OK")) ?
                        ContextCompat.getColor(view.getContext(), R.color.colorONT_OK) :
                        ContextCompat.getColor(view.getContext(), R.color.colorONT_Authfail));

        final String backText =
                        "Slot: " + ontInfoList.get(ont).getSlot() + "; channel: "
                                + ontInfoList.get(ont).getChannel() + "; status: " + ontInfoList.get(ont).getStatus().getStatus() + ";";

        ((TextView) view.findViewById(R.id.textView6)).setText(backText);
        return view;
    }
}
