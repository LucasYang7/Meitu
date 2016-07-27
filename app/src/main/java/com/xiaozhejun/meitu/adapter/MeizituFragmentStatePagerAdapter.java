package com.xiaozhejun.meitu.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.xiaozhejun.meitu.ui.fragment.meizitu.AllFragment;
import com.xiaozhejun.meitu.ui.fragment.meizitu.HomePageFragment;
import com.xiaozhejun.meitu.ui.fragment.meizitu.JapanFragment;
import com.xiaozhejun.meitu.ui.fragment.meizitu.MMFragment;
import com.xiaozhejun.meitu.ui.fragment.meizitu.ShareFragment;
import com.xiaozhejun.meitu.ui.fragment.meizitu.TaiwanFragment;
import com.xiaozhejun.meitu.ui.fragment.meizitu.XingGanFragment;
import com.xiaozhejun.meitu.util.Constants;

/**
 * 这是妹子图MeizituTabFragment对应的FragmentStatePagerAdapter
 * Created by yangzhe on 16-7-27.
 */
public class MeizituFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    public MeizituFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return new HomePageFragment();

            case 1:
                return new XingGanFragment();

            case 2:
                return new JapanFragment();

            case 3:
                return new TaiwanFragment();

            case 4:
                return new MMFragment();

            case 5:
                return new ShareFragment();

            case 6:
                return new AllFragment();

            default:
                return new HomePageFragment();

        }
    }

    @Override
    public int getCount() {
        return Constants.MEIZITU_COUNT;
    }

    /**
     * 返回ViewPager中的某页对应的title
     * */
    @Override
    public CharSequence getPageTitle(int position) {
        return Constants.MEIZITU_TITLES[position];
    }
}
