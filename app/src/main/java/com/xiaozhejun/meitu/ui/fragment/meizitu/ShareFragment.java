package com.xiaozhejun.meitu.ui.fragment.meizitu;


import android.support.v4.app.Fragment;

import com.xiaozhejun.meitu.model.MeituPicture;
import com.xiaozhejun.meitu.network.Network;
import com.xiaozhejun.meitu.network.parser.HtmlParser;
import com.xiaozhejun.meitu.ui.fragment.MeituPictureListFragment;
import com.xiaozhejun.meitu.util.Constants;
import com.xiaozhejun.meitu.util.ShowToast;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShareFragment extends MeituPictureListFragment {

    boolean isResetData;
    boolean mCanConnectToServer;

    public ShareFragment() {
        // Required empty public constructor
    }


    /**
     * 用于获取"美女自拍"的页数信息
     * */
    Observer<Integer> observerPage = new Observer<Integer>() {
        @Override
        public void onCompleted() {
            mMeituPictureListSwipeRefreshLayout.setRefreshing(false);
            if(mPage > 0){
                ShowToast.showTestShortToast(getActivity(),"妹子自拍完成获取相册网页页数的操作 mPage = " + mPage);
                // 开始下载"美女自拍"的第一个相册
                loadMoreMeituPicture(mPage);
            }else{
                ShowToast.showShortToast(getActivity(),"无法连接到妹子图服务器，妹子自拍获取网页页数操作失败");
            }
        }

        @Override
        public void onError(Throwable e) {
            mMeituPictureListSwipeRefreshLayout.setRefreshing(false);
            ShowToast.showShortToast(getActivity(),"无法连接到妹子图服务器，妹子自拍获取网页页数操作失败"
                    + e.toString());
        }

        @Override
        public void onNext(Integer page) {
            if(page == null){
                mPage = -1;
            }else{
                mPage = page.intValue();                 // 获取“妹子自拍”分类图片的网页总数
            }
            //mPage = 3;              // test
        }
    };

    /**
     * 用于获取"美女自拍"的各张图片的信息
     * */
    Observer<ArrayList<MeituPicture>> observerPictures = new Observer<ArrayList<MeituPicture>>() {
        @Override
        public void onCompleted() {
            ShowToast.showTestLongToast(mContext,"load page " + mPage + " onCompleted()!");
            mMeituPictureListSwipeRefreshLayout.setRefreshing(false);
            mIsLoadingData = false;
            if(mCanConnectToServer == true){
                mPage--;
            }
        }

        @Override
        public void onError(Throwable e) {
            ShowToast.showTestLongToast(mContext,"load page " + mPage + " onError()! " + e.toString());
            ShowToast.showShortToast(getActivity(),"出现了错误:" + e.toString() + " 无法连接到妹子图服务器，获取妹子图片失败...");
            mMeituPictureListSwipeRefreshLayout.setRefreshing(false);
            mIsLoadingData = false;
        }

        @Override
        public void onNext(ArrayList<MeituPicture> meituPictures) {
            if(meituPictures == null){
                ShowToast.showShortToast(getActivity(),"无法连接到妹子图服务器，获取妹子图片失败...");
            }else{
                meituPictureListRecyclerViewAdapter.updateMeituPictureList(meituPictures,isResetData);
                if(isResetData == true)
                    isResetData = false;
            }
        }
    };

    @Override
    protected void resetMeiziData() {
        isResetData = true;              // 重新加载“妹子自拍”的图片信息
    }

    @Override
    protected void refreshMeituPicture() {
        unsubscribe();   // 在新的Http请求前，取消上一次Http操作中所涉及的obserable与observer之间的订阅关系
        resetMeiziData();
        mMeituPictureListSwipeRefreshLayout.setRefreshing(true);
        subscription = Network.getMeizituService()
                .getPictureInSelfiePage()
                .map(new Func1<ResponseBody, Integer>() {
                    @Override
                    public Integer call(ResponseBody responseBody) {
                        Integer pages = null;
                        try {
                            String responseBodyContent = responseBody.string();
                            if(HtmlParser.canConnectToServer(responseBodyContent, Constants.MEIZITU_WEBSITE)){
                                pages = HtmlParser.parseFirstMeizituSelfieHtmlContent(responseBodyContent);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return pages;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observerPage);
    }

    @Override
    protected void loadMoreMeituPicture(int page) {
        if(page > 0){
            unsubscribe();   // 在新的Http请求前，取消上一次Http操作中所涉及的obserable与observer之间的订阅关系
            mIsLoadingData = true;
            subscription = Network.getMeizituService()
                    .getPictureInSelfiePages(page)
                    .map(new Func1<ResponseBody, ArrayList<MeituPicture>>() {

                        @Override
                        public ArrayList<MeituPicture> call(ResponseBody responseBody) {
                            ArrayList<MeituPicture> meizituSelfieList = null;
                            try {
                                String responseBodyContent = responseBody.string();
                                mCanConnectToServer = HtmlParser.canConnectToServer(responseBodyContent,Constants.MEIZITU_WEBSITE);
                                if(mCanConnectToServer == true){
                                    meizituSelfieList = HtmlParser.parseMeizituSelfieHtmlContent(responseBodyContent);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return meizituSelfieList;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observerPictures);
        }else{
            ShowToast.showShortToast(getActivity(),"妹子被你看完啦 O(∩_∩)O哈哈~");
        }

    }


}
