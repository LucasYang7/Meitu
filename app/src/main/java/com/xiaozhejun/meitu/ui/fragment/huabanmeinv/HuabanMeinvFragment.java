package com.xiaozhejun.meitu.ui.fragment.huabanmeinv;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.model.HuabanMeinv;
import com.xiaozhejun.meitu.model.HuabanMeinvJsonResult;
import com.xiaozhejun.meitu.model.MeituPicture;
import com.xiaozhejun.meitu.network.Network;
import com.xiaozhejun.meitu.ui.fragment.MeituPictureListFragment;
import com.xiaozhejun.meitu.ui.widget.ShowToast;
import com.xiaozhejun.meitu.util.Constants;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class HuabanMeinvFragment extends MeituPictureListFragment {

    private String mMax = "";

    public HuabanMeinvFragment() {
        // Required empty public constructor
    }

    /**
     * 用于接收Gank妹子图片的observer
     * */
    Observer<ArrayList<MeituPicture>> observer = new Observer<ArrayList<MeituPicture>>() {
        @Override
        public void onCompleted() {
            ShowToast.showLongToast(mContext,mType + " load page " + mPage + " onCompleted()!");
            mMeituPictureListSwipeRefreshLayout.setRefreshing(false);
            mIsLoadingData = false;
            mPage++;
        }

        @Override
        public void onError(Throwable e) {
            ShowToast.showLongToast(mContext,mType + " load page " + mPage + " onError()! " + e.toString());
            mMeituPictureListSwipeRefreshLayout.setRefreshing(false);
            mIsLoadingData = false;
        }

        @Override
        public void onNext(ArrayList<MeituPicture> meituPictures) {
            meituPictureListRecyclerViewAdapter.updateMeituPictureList(meituPictures,mPage);
        }
    };

    @Override
    protected void loadMoreMeituPicture(int page) {
        loadNewMeituPicture();
    }

    @Override
    protected void refreshMeituPicture() {
        resetMeiziData();
        mMeituPictureListSwipeRefreshLayout.setRefreshing(true);
        loadNewMeituPicture();
    }

    /**
     * 从花瓣美女下载新的图片
     * */
    public void loadNewMeituPicture(){
        unsubscribe();   // 在新的Http请求前，取消上一次Http操作中所涉及的obserable与observer之间的订阅关系
        mIsLoadingData = true;
        Observable<HuabanMeinvJsonResult> observable;
        if(mMax.equals("")){
            observable = Network.getHuabanMeinvService().getHuabanMeinvs(Constants.HUABAN_MEINV_NUMBER);
        }else{
            observable = Network.getHuabanMeinvService().getHuabanMeinvs(Constants.HUABAN_MEINV_NUMBER,
                    mMax);
        }

        subscription = observable.map(new Func1<HuabanMeinvJsonResult, ArrayList<MeituPicture>>() {
            @Override
            public ArrayList<MeituPicture> call(HuabanMeinvJsonResult huabanMeinvJsonResult) {
                List<HuabanMeinv> huabanMeinvList = huabanMeinvJsonResult.huabanMeinvList;
                mMax = huabanMeinvList.get(huabanMeinvList.size() - 1).pin_id;    // 设置下一次调用API所用的max值
                ArrayList<MeituPicture> meituPictureList = new ArrayList<MeituPicture>();
                for(HuabanMeinv huabanMeinv:huabanMeinvList){
                    MeituPicture meituPicture = new MeituPicture();
                    meituPicture.setTitle(huabanMeinv.raw_text);
                    meituPicture.setPictureUrl(huabanMeinv.getImageUrl());
                    meituPictureList.add(meituPicture);
                }
                return meituPictureList;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

}
