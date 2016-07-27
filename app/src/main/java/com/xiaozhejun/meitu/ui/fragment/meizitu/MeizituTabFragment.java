package com.xiaozhejun.meitu.ui.fragment.meizitu;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.adapter.MeizituFragmentStatePagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeizituTabFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    public MeizituTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_meizitu_tab, container, false);
        //获取MeizituTabFragment中的ViewPager，并为其添加适配器
        mViewPager = (ViewPager) view.findViewById(R.id.viewPagerInMeiziTu);
        mViewPager.setAdapter(new MeizituFragmentStatePagerAdapter(getFragmentManager()));
        //获取MeizituTabFragment中的TabLayout，并为其绑定ViewPager
        mTabLayout = (TabLayout)view.findViewById(R.id.tabLayoutInMeiziTu);
        mTabLayout.setupWithViewPager(mViewPager);
        return view;
    }

}
