package ru.jneko.puseradmin.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import ru.jneko.puseradmin.R;

public class ONTFragment extends Fragment {
    private GoogleMaterial.Icon iconA;
    private int colorA;
    private String titleA, textA;
    private View.OnClickListener action = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.ont1_fragment, container, false);

        if (action != null) {
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action.onClick(v);
                }
            });

            ll.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                        ll.setBackgroundResource(R.drawable.border_2);
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                        ll.setBackgroundResource(R.drawable.border_1);
                    return false;
                }
            });
        }

        ImageView im = (ImageView) ll.findViewById(R.id.imageView);
        im.setImageDrawable(new IconicsDrawable(this.getActivity().getApplicationContext()).icon(iconA).color(colorA).sizeDp(48));

        TextView tw1 = (TextView) ll.findViewById(R.id.textView5);
        tw1.setText(titleA);

        TextView tw2 = (TextView) ll.findViewById(R.id.textView6);
        tw2.setText(textA);

        return ll;
    }

    public static ONTFragment newInstance(GoogleMaterial.Icon icon, int color, String title, String text) {
        ONTFragment f = new ONTFragment();
        f.setIcon(icon, color);
        f.setTitle(title);
        f.setText(text);
        return f;
    }

    public void setOnClickListener(View.OnClickListener al) {
        action = al;
    }

    public void setIcon(GoogleMaterial.Icon icon, int color) {
        iconA = icon;
        colorA = color;
    }

    public void setTitle(String t) {
        titleA = t;
    }

    public void setText(String t) {
        textA = t;
    }
}
