package com.xiaozhejun.meitu.ui.fragment.meizitu;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaozhejun.meitu.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeizituTabFragment extends Fragment {


    public MeizituTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meizitu_tab, container, false);
    }

}
