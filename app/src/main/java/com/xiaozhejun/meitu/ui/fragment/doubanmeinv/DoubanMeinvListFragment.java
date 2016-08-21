package com.xiaozhejun.meitu.ui.fragment.doubanmeinv;

import com.xiaozhejun.meitu.model.MeituPicture;
import com.xiaozhejun.meitu.network.Network;
import com.xiaozhejun.meitu.network.parser.HtmlParser;
import com.xiaozhejun.meitu.ui.fragment.MeituPictureListFragment;
import com.xiaozhejun.meitu.util.Constants;
import com.xiaozhejun.meitu.util.ShowToast;

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
    private boolean mIsResetData;
    private boolean mCanConnectToServer;

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
            ShowToast.showTestLongToast(mContext,mCid + " load page " + mPage + " onCompleted()!");
            mMeituPictureListSwipeRefreshLayout.setRefreshing(false);
            mIsLoadingData = false;
            if(mCanConnectToServer == true){
                mPage++;
            }
        }

        @Override
        public void onError(Throwable e) {
            ShowToast.showTestLongToast(mContext,mCid + " load page " + mPage + " onError()! " + e.toString());
            ShowToast.showShortToast(getActivity(),"无法连接到豆瓣美女的服务器...出现了错误:" + e.toString());
            mMeituPictureListSwipeRefreshLayout.setRefreshing(false);
            mIsLoadingData = false;
        }

        @Override
        public void onNext(ArrayList<MeituPicture> meituPictures) {
            if(meituPictures == null){
                ShowToast.showShortToast(getActivity(),"无法连接到豆瓣美女的服务器...");
            }else{
                if(meituPictures.size() > 0){
                    meituPictureListRecyclerViewAdapter.updateMeituPictureList(meituPictures,mIsResetData);
                    if(mIsResetData == true){
                        mIsResetData = false;
                    }
                }else{
                    ShowToast.showShortToast(getActivity(),"妹子被你看完啦 O(∩_∩)O哈哈~");
                }

            }

        }
    };

    @Override
    protected void resetMeiziData() {
        mIsResetData = true;
        mPage = 1;                            //test
    }

    @Override
    protected void refreshMeituPicture() {
        unsubscribe();   // 在新的Http请求前，取消上一次Http操作中所涉及的obserable与observer之间的订阅关系
        resetMeiziData();
        mMeituPictureListSwipeRefreshLayout.setRefreshing(true);
        loadMoreMeituPicture(mPage);
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
                ArrayList<MeituPicture> doubanMeinvPictureList = null;
                try {
                    String responseBodyContent = responseBody.string();
                    mCanConnectToServer = HtmlParser.canConnectToServer(responseBodyContent,
                            Constants.DOUBAN_MEINV_WEBSITE);
                    if(mCanConnectToServer == true){
                        doubanMeinvPictureList = HtmlParser.parseDoubanMeinvHtmlContent(responseBodyContent);
                    }
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
