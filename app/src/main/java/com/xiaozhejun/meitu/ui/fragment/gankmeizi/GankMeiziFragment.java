package com.xiaozhejun.meitu.ui.fragment.gankmeizi;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.model.GankMeizi;
import com.xiaozhejun.meitu.model.GankMeiziJsonResult;
import com.xiaozhejun.meitu.model.MeituPicture;
import com.xiaozhejun.meitu.network.Network;
import com.xiaozhejun.meitu.ui.fragment.MeituPictureListFragment;
import com.xiaozhejun.meitu.ui.widget.ShowToast;
import com.xiaozhejun.meitu.util.Constants;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class GankMeiziFragment extends MeituPictureListFragment {


    public GankMeiziFragment() {
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
    protected void refreshMeituPicture() {
        unsubscribe();   // 在新的Http请求前，取消上一次Http操作中所涉及的obserable与observer之间的订阅关系
        resetMeiziData();
        mMeituPictureListSwipeRefreshLayout.setRefreshing(true);
        loadMoreMeituPicture(1);
    }


    @Override
    protected void loadMoreMeituPicture(int page) {
        unsubscribe();   // 在新的Http请求前，取消上一次Http操作中所涉及的obserable与observer之间的订阅关系
        mIsLoadingData = true;
        subscription = Network.getGankMeiziService()
                .getGankMeizis(Constants.GANK_MEIZI_NUMBER,mPage)
                .map(new Func1<GankMeiziJsonResult, ArrayList<MeituPicture>>() {

                    @Override
                    public ArrayList<MeituPicture> call(GankMeiziJsonResult gankMeiziJsonResult) {
                        List<GankMeizi> gankMeiziList = gankMeiziJsonResult.gankMeiziList;
                        ArrayList<MeituPicture> meituPictureList = new ArrayList<MeituPicture>();
                        for(GankMeizi gankMeizi:gankMeiziList){
                            MeituPicture meituPicture = new MeituPicture();
                            meituPicture.title = gankMeizi.desc;
                            meituPicture.pictureUrl = gankMeizi.url;
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
