package com.xiaozhejun.meitu.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.xiaozhejun.meitu.ui.fragment.meizitu.AllFragment;
import com.xiaozhejun.meitu.ui.fragment.meizitu.HomePageFragment;
import com.xiaozhejun.meitu.ui.fragment.meizitu.JapanFragment;
import com.xiaozhejun.meitu.ui.fragment.meizitu.MMFragment;
import com.xiaozhejun.meitu.ui.fragment.meizitu.MeizituGalleryListFragment;
import com.xiaozhejun.meitu.ui.fragment.meizitu.ShareFragment;
import com.xiaozhejun.meitu.ui.fragment.meizitu.TaiwanFragment;
import com.xiaozhejun.meitu.ui.fragment.meizitu.XingGanFragment;
import com.xiaozhejun.meitu.util.Constants;

/**
 * 这是妹子图MeizituTabFragment对应的FragmentStatePagerAdapter
 * Created by yangzhe on 16-7-27.
 */
public class MeizituFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    MeizituGalleryListFragment[] meizituGalleryListFragments = new MeizituGalleryListFragment[Constants.MEIZITU_COUNT];

    public MeizituFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Log.e("postion","getItem " + position);
        switch(position){
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                if(meizituGalleryListFragments[position] == null){
                    meizituGalleryListFragments[position] = new MeizituGalleryListFragment();
                    meizituGalleryListFragments[position].setType(Constants.MEIZITU_TYPE[position]);
                }
                return meizituGalleryListFragments[position];

            case 5:
                return new ShareFragment();

            default:
                position = 0;
                if(meizituGalleryListFragments[position] == null){
                    meizituGalleryListFragments[position] = new MeizituGalleryListFragment();
                    meizituGalleryListFragments[position].setType(Constants.MEIZITU_TYPE[position]);
                }
                return meizituGalleryListFragments[position];

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
