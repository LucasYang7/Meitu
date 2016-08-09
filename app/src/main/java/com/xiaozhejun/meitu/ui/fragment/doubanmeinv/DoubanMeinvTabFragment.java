package com.xiaozhejun.meitu.ui.fragment.doubanmeinv;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.adapter.DoubanMeinvFragmentStatePagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoubanMeinvTabFragment extends Fragment {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    public DoubanMeinvTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_douban_meinv, container, false);
        // 获取DoubanMeinvTabFragment中的ViewPager，并为其添加适配器
        mViewPager = (ViewPager)view.findViewById(R.id.viewPagerInDoubanMeinv);
        mViewPager.setAdapter(new DoubanMeinvFragmentStatePagerAdapter(getFragmentManager()));
        // 获取DoubanMeinvTabFragment中的TabLayout，并为其绑定ViewPager
        mTabLayout = (TabLayout)view.findViewById(R.id.tabLayoutInDoubanMeinv);
        mTabLayout.setupWithViewPager(mViewPager);
        return view;
    }

}
