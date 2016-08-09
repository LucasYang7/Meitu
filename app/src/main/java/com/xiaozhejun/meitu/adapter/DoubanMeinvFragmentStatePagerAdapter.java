package com.xiaozhejun.meitu.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.xiaozhejun.meitu.ui.fragment.doubanmeinv.DoubanMeinvListFragment;
import com.xiaozhejun.meitu.util.Constants;
import com.xiaozhejun.meitu.util.Logcat;

/**
 * Created by yangzhe on 16-8-10.
 */
public class DoubanMeinvFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    DoubanMeinvListFragment[] doubanMeinvListFragments = new DoubanMeinvListFragment[Constants.DOUBAN_MEINV_COUNT];

    public DoubanMeinvFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Logcat.showLog("position","getItem " + position);
        if(doubanMeinvListFragments[position] == null){
            doubanMeinvListFragments[position] = new DoubanMeinvListFragment();
            doubanMeinvListFragments[position].setCid(Constants.DOUBAN_MEINV_CID[position]);
        }
        return doubanMeinvListFragments[position];
    }

    @Override
    public int getCount() {
        return Constants.DOUBAN_MEINV_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Constants.DOUBAN_MEINV_TITLES[position];
    }
}
