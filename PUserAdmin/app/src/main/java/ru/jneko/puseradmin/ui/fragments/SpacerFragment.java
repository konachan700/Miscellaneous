package ru.jneko.puseradmin.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.jneko.puseradmin.R;

public class SpacerFragment extends Fragment {
    private String titleA;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.spacer, container, false);

        TextView tw1 = (TextView) ll.findViewById(R.id.textView7);
        tw1.setText(titleA);

        return ll;
    }

    public static SpacerFragment newInstance(String title) {
        SpacerFragment f = new SpacerFragment();
        f.setTitle(title);
        return f;
    }

    public void setTitle(String t) {
        titleA = t;
    }
}
