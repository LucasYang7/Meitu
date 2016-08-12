package com.xiaozhejun.meitu.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.xiaozhejun.meitu.ui.fragment.meizitu.MeizituGalleryListFragment;
import com.xiaozhejun.meitu.ui.fragment.meizitu.ShareFragment;
import com.xiaozhejun.meitu.util.Constants;
import com.xiaozhejun.meitu.util.Logcat;

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
    public Fragment getItem(int position) {            // 这里的position并不准确!!!
        //Log.e("position","getItem " + position);
        Logcat.showLog("position","getItem " + position);
        /*
        if(position < Constants.MEIZITU_COUNT - 1){    // 对应妹子图网站的各个相册页面
            if(meizituGalleryListFragments[position] == null){
                meizituGalleryListFragments[position] = new MeizituGalleryListFragment();
                meizituGalleryListFragments[position].setType(Constants.MEIZITU_TYPE[position]);
            }
            return meizituGalleryListFragments[position];
        }else{    // 妹子自拍页面单独处理
            return new ShareFragment();
        }
        */

        if(Constants.MEIZITU_TITLES[position].equals("妹子自拍") == false){
            if(meizituGalleryListFragments[position] == null){
                meizituGalleryListFragments[position] = new MeizituGalleryListFragment();
                meizituGalleryListFragments[position].setType(Constants.MEIZITU_TYPE[position]);
            }
            return meizituGalleryListFragments[position];
        }else{      // "妹子自拍"页面单独处理
            return new ShareFragment();
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
