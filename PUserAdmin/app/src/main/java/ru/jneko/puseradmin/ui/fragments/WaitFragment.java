package ru.jneko.puseradmin.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import ru.jneko.puseradmin.R;

public class WaitFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.wait_fragment, container, false);
        return ll;
    }
}
