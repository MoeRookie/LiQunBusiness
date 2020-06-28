package com.liqun.liqunbusiness.view.discory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.liqun.liqunbusiness.R;

public class DiscoryFragment extends Fragment {
    private static final String TAG = "DiscoryFragment";

    public static Fragment newInstance() {
        DiscoryFragment fragment = new DiscoryFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mine_layout, null);
        ((TextView)rootView.findViewById(R.id.textView)).setText(TAG);
        return rootView;
    }
}
