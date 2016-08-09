package com.xiaozhejun.meitu.ui.fragment.doubanmeinv;

import com.xiaozhejun.meitu.model.MeituPicture;
import com.xiaozhejun.meitu.network.Network;
import com.xiaozhejun.meitu.network.parser.HtmlParser;
import com.xiaozhejun.meitu.ui.fragment.MeituPictureListFragment;
import com.xiaozhejun.meitu.ui.widget.ShowToast;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by yangzhe on 16-8-10.
 */
public class DoubanMeinvListFragment extends MeituPictureListFragment {

    private int mCid;

    public DoubanMeinvListFragment(){

    }

    /**
     * 设置图片的类别
     * */
    public void setCid(int cid){
        mCid = cid;
    }

    /**
     * 用于接收解析豆瓣美女网站得到的美女图片信息
     * */
    Observer<ArrayList<MeituPicture>> observer = new Observer<ArrayList<MeituPicture>>() {
        @Override
        public void onCompleted() {
            ShowToast.showLongToast(mContext,mCid + " load page " + mPage + " onCompleted()!");
            mMeituPictureListSwipeRefreshLayout.setRefreshing(false);
            mIsLoadingData = false;
            mPage++;
        }

        @Override
        public void onError(Throwable e) {
            ShowToast.showLongToast(mContext,mCid + " load page " + mPage + " onError()! " + e.toString());
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
        Observable<ResponseBody> observable;
        if(mCid == 1){     // 获取豆瓣美女"所有分类图片
            observable = Network.getDoubanMeinvService()
                    .getAllBeauties(page);
        }else{            // 获取豆瓣美女其它分类的图片
            observable = Network.getDoubanMeinvService()
                    .getBeautiesByCid(mCid,page);
        }
        observable.map(new Func1<ResponseBody, ArrayList<MeituPicture>>() {
            @Override
            public ArrayList<MeituPicture> call(ResponseBody responseBody) {
                ArrayList<MeituPicture> doubanMeinvPictureList = new ArrayList<MeituPicture>();
                try {
                    String responseBodyContent = responseBody.string();
                    doubanMeinvPictureList = HtmlParser.parseDoubanMeinvHtmlContent(responseBodyContent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return doubanMeinvPictureList;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }
}
