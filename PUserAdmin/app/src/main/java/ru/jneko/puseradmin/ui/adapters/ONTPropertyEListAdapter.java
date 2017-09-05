package ru.jneko.puseradmin.ui.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import ru.jneko.puseradmin.MA4000.TransactionsMA4000v2;
import ru.jneko.puseradmin.R;

public class ONTPropertyEListAdapter extends BaseExpandableListAdapter {

    private final Context
            mContext;

    private ArrayList<Map<String, String>>
            childItems = new ArrayList<>();

    private ArrayList<ArrayList<String>>
            childItemsList = new ArrayList<>();

    private ArrayList<String>
            groups = new ArrayList<>();

    public ONTPropertyEListAdapter(Context c) {
        mContext = c;
    }

    public void addGroup(String s) {
        groups.add(s);
    }

    public void addChilds(Map<String, String> childs) {
        childItems.add(childs);

        final ArrayList<String> list = new ArrayList<>(childs.keySet());
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o, String t1) {
                return o.compareTo(t1);
            }
        });
        childItemsList.add(list);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return childItemsList.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
        return childItems.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return childItemsList.get(i).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.elist_group, null);
        }

        if (b) {
            ((ImageView) view.findViewById(R.id.imageView2))
                    .setImageDrawable(new IconicsDrawable(mContext.getApplicationContext())
                            .icon(GoogleMaterial.Icon.gmd_expand_less).color(0x555555).sizeDp(48));
        } else {
            ((ImageView) view.findViewById(R.id.imageView2))
                    .setImageDrawable(new IconicsDrawable(mContext.getApplicationContext())
                            .icon(GoogleMaterial.Icon.gmd_expand_more).color(0x555555).sizeDp(48));
        }

        TextView textGroup = (TextView) view.findViewById(R.id.textView8);
        textGroup.setText(groups.get(i));

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.ont1_fragment, null);
        }

        view.setBackgroundResource(R.drawable.border_null);

        final Map<String, String> valMap = childItems.get(i);
        final String title = childItemsList.get(i).get(i1);
        final String text = valMap.get(title);

        if (TransactionsMA4000v2.writableParams.contains(title)) {
            ((ImageView) view.findViewById(R.id.imageView))
                    .setImageDrawable(new IconicsDrawable(mContext.getApplicationContext())
                            .icon(GoogleMaterial.Icon.gmd_label).colorRes(R.color.color_writable_param).sizeDp(48));
        } else {
            ((ImageView) view.findViewById(R.id.imageView))
                    .setImageDrawable(new IconicsDrawable(mContext.getApplicationContext())
                            .icon(GoogleMaterial.Icon.gmd_label).colorRes(R.color.color_inactive_param).sizeDp(48));
        }

        TextView tw1 = (TextView) view.findViewById(R.id.textView5);
        tw1.setText(title);
        tw1.setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorONT_OK));

        TextView tw2 = (TextView) view.findViewById(R.id.textView6);
        tw2.setText(text);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }


    public String getValue(int i, int i1) {
        final Map<String, String> valMap = childItems.get(i);
        final String title = childItemsList.get(i).get(i1);
        return valMap.get(title);
    }
}
