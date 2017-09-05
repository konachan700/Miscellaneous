package ru.jneko.puseradmin.ui.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import ru.jneko.puseradmin.R;

public class ONTDisplayInfoAdapter extends BaseAdapter {
    private final Map<String, String>
            ontInfoList;

    private final ArrayList<String>
            ontList;

    private final LayoutInflater
            lInflater;

    public ONTDisplayInfoAdapter(Context c, Map<String, String> ontInfo) {
        lInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ontInfoList = ontInfo;
        ontList = new ArrayList(ontInfo.keySet());
        if (ontList.size() > 1)
            Collections.sort(ontList, new Comparator<String>() {
                @Override
                public int compare(String o, String t1) {
                    return o.compareTo(t1);
                }
            });
    }

    public void refresh() {
        ontList.clear();
        ontList.addAll(ontInfoList.keySet());

        if (ontList.size() > 1)
            Collections.sort(ontList, new Comparator<String>() {
                @Override
                public int compare(String o, String t1) {
                    return o.compareTo(t1);
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
        ((TextView) view.findViewById(R.id.textView6)).setText(ontInfoList.get(ont));

        //if (TransactionsMA4000v2.writableParams.contains(ont))
        //    ((TextView) view.findViewById(R.id.textView5)).setTextColor(ContextCompat.getColor(view.getContext(), R.color.color_writable_param));
        //else
        ((TextView) view.findViewById(R.id.textView5)).setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorONT_OK));

        ((ImageView) view.findViewById(R.id.imageView))
                .setImageDrawable(new IconicsDrawable(view.getContext())
                        .icon(GoogleMaterial.Icon.gmd_assessment).colorRes(R.color.color_inactive_param).sizeDp(48));

        return view;
    }
}
