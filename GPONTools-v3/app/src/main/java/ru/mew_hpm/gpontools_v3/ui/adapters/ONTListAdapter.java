package ru.mew_hpm.gpontools_v3.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import ru.mew_hpm.gpontools_v3.R;
import ru.mew_hpm.gpontools_v3.dao.ONTInfo;

public class ONTListAdapter extends BaseAdapter {
    private final ONTListAdapterActionListener
            actionListener;

    private final LayoutInflater
            lInflater;

    private final ArrayList<ONTInfo>
            fileArrayList = new ArrayList<>();

    public ONTListAdapter(Context c, ONTListAdapterActionListener al) {
        lInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        actionListener = al;
    }

    public void refresh(Collection<ONTInfo> data) {
        fileArrayList.clear();
        fileArrayList.addAll(data);
        notifyDataSetChanged();
    }

    public int getNextONTID(String currPonPort) {
        final ArrayList<Integer> list = new ArrayList<Integer>() {{ add(-1); }};
        for (ONTInfo oi : fileArrayList) {
            if (oi.getGlobalPort().contentEquals(currPonPort)) {
                try {
                    final Integer val = Integer.parseInt(oi.getOntId(), 10);
                    list.add(val);
                } catch (NumberFormatException e) {
                }
            }
        }

        return Collections.max(list) + 1;
    }

    @Override
    public int getCount() {
        return fileArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return fileArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ONTInfo rf = fileArrayList.get(i);

        if (view == null)
            view = lInflater.inflate(R.layout.item_list_1, viewGroup, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (actionListener != null) actionListener.OnClick(rf);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (actionListener != null) actionListener.OnLongClick(rf);
                return false;
            }
        });

        ((TextView) view.findViewById(R.id.itemTitle)).setText(rf.getSerial());
        ((TextView) view.findViewById(R.id.itemSubtext)).setText(rf.getStatus());
        ((TextView) view.findViewById(R.id.itemText)).setText(rf.getEquipmentId() + "\t" + rf.getVersion() + "\t" + rf.getRssi()+"dBm");

        if ((rf.getStatus() != null) && (rf.getSerial() != null)) {
            if (rf.getSerial().contains("ELTX62") && (rf.getStatus().contains("OK")))
                ((ImageView) view.findViewById(R.id.itemIcon))
                        .setImageDrawable(new IconicsDrawable(view.getContext())
                                .icon(GoogleMaterial.Icon.gmd_router).colorRes(R.color.colorDismounted).sizeDp(48));
            else if (rf.getSerial().contains("ELTX62"))
                ((ImageView) view.findViewById(R.id.itemIcon))
                        .setImageDrawable(new IconicsDrawable(view.getContext())
                                .icon(GoogleMaterial.Icon.gmd_router).colorRes(R.color.colorMounted).sizeDp(48));
            else
                ((ImageView) view.findViewById(R.id.itemIcon))
                        .setImageDrawable(new IconicsDrawable(view.getContext())
                                .icon(GoogleMaterial.Icon.gmd_router).colorRes(R.color.colorIconGray).sizeDp(48));
        } else {
            ((ImageView) view.findViewById(R.id.itemIcon))
                    .setImageDrawable(new IconicsDrawable(view.getContext())
                            .icon(GoogleMaterial.Icon.gmd_error).colorRes(R.color.colorRedError).sizeDp(48));
        }
        return view;
    }
}
