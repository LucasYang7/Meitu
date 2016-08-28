package com.xiaozhejun.meitu.ui.fragment.meizitu;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.adapter.MeizituGalleryListRecyclerViewAdapter;
import com.xiaozhejun.meitu.model.MeizituGallery;
import com.xiaozhejun.meitu.network.Network;
import com.xiaozhejun.meitu.network.parser.HtmlParser;
import com.xiaozhejun.meitu.ui.activity.ShowMeizituGalleryActivity;
import com.xiaozhejun.meitu.ui.fragment.BaseFragment;
import com.xiaozhejun.meitu.ui.widget.MeituRecyclerView;
import com.xiaozhejun.meitu.util.Constants;
import com.xiaozhejun.meitu.util.ShowToast;
import com.xiaozhejun.meitu.util.Logcat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeizituGalleryListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout meizituSwipeRefreshLayout;
    private MeituRecyclerView meizituRecyclerView;
    private MeizituGalleryListRecyclerViewAdapter meizituRecyclerViewAdapter;
    private boolean mIsLoadingData;    // 判断能否请求新的网页，加载更多妹子的图片
    private boolean mCanConnectToServer;
    private boolean mIsAutoRefresh = true;    // 判断是否自动刷新数据
    private int mPage;       //表示妹子图片相册链接后面的分页
    private int mTotalPages = Integer.MAX_VALUE; //表示妹子图某类相册所对应的网页总页数，总页数初始值为整型数的最大值
    private String mType;    //表示妹子图片所属的类型，例如：日本妹子，性感妹子等
    private Context mContext;

    public MeizituGalleryListFragment() {
        // Required empty public constructor
    }

    /**
     * 标记妹子图片相册所属的分类
     * */
    public void setType(String type){
        mType = type;
    }

    /**
     * 执行下拉SwipeRefreshLayout进行刷新操作时，要清空原有的数据
     * */
    public void resetMeiziData(){
        mPage = 1;                     // 重新加载首页的妹子图信息
        mIsLoadingData = true;     // 在加载完首页的数据前，不能再加载新的妹子数据
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = container.getContext();
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_meizitu_gallery_list, container, false);
        // 设置RecyclerView
        meizituRecyclerView = (MeituRecyclerView) view.findViewById(R.id.meizituGalleryListRecyclerView);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        meizituRecyclerViewAdapter = new MeizituGalleryListRecyclerViewAdapter(meizituRecyclerView); //绑定meizituRecyclerViewAdapter和meizituRecyclerView
        meizituRecyclerViewAdapter.initMeizituGalleryList(null);
        meizituRecyclerView.setHasFixedSize(true);
        meizituRecyclerView.setLayoutManager(staggeredGridLayoutManager);  //设置RecyclerView的布局
        meizituRecyclerView.setAdapter(meizituRecyclerViewAdapter); //设置RecyclerView的适配器
        meizituRecyclerView.addOnScrollListener(new MeituRecyclerView.OnVerticalScrollListener() {
            @Override
            public void onBottom() {
                ShowToast.showTestShortToast(mContext,mType + " 已经滑到底部了" + "page " + mPage);
                if(mIsLoadingData == false){
                    ShowToast.showTestShortToast(mContext,mType + " 正在加载第" + mPage + "页的妹子数据");
                    loadMoreMeizituGalleryData(mPage);
                }
            }
        }); // 为RecyclerView添加滑动事件监听
        meizituRecyclerView.setOnItemClickListener(new MeituRecyclerView.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int postion) {
                MeizituGallery meizituGallery = meizituRecyclerViewAdapter.getMeizituGallery(postion);
                ShowToast.showTestLongToast(mContext, meizituGallery.getObjectInformation());
                // 跳转到展示相册图片的Activity
                Bundle bundle = new Bundle();
                bundle.putString("groupId",meizituGallery.getGroupId());
                bundle.putString("title",meizituGallery.getTitle());
                Intent intent = new Intent(getActivity(), ShowMeizituGalleryActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        // 设置SwipeRefreshLayout
        meizituSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.meizituGalleryListSwipeRefreshLayout);
        meizituSwipeRefreshLayout.setOnRefreshListener(this);
        meizituSwipeRefreshLayout.setColorSchemeResources(R.color.colorSwipeRefresh);
        if(mIsAutoRefresh == true){
            meizituSwipeRefreshLayout.post(new Runnable() { // 设置SwipeRefreshLayout自动刷新
                @Override
                public void run() {
                    refreshMeizituGalleryData();
                }
            });
        }
        return view;
    }

    // 对应SwipeRefreshLayout的刷新事件监听
    @Override
    public void onRefresh() {
        refreshMeizituGalleryData();
    }

    /**
     * 用于获取妹子图某类相册的网页页数信息
     * */
    Observer<Integer> observerPages = new Observer<Integer>() {
        @Override
        public void onCompleted() {
            meizituSwipeRefreshLayout.setRefreshing(false);
            mIsLoadingData = false;
            if(mCanConnectToServer == true){
                loadMoreMeizituGalleryData(mPage);         // 加载某类妹子图相册首页中的相册信息
                ShowToast.showTestShortToast(mContext,"妹子图 " + mType + " 的网页总数为" + mTotalPages);
            }
        }

        @Override
        public void onError(Throwable e) {
            meizituSwipeRefreshLayout.setRefreshing(false);
            mIsLoadingData = false;
            ShowToast.showShortToast(mContext,"无法连接到妹子图的服务器... 妹子图 " + mType
                    + " 的获取网页总数信息失败!");
        }

        @Override
        public void onNext(Integer totalPages) {
            if(totalPages == null){
                ShowToast.showShortToast(mContext,"无法连接到妹子图的服务器... 妹子图 " + mType
                        + " 的获取网页总数信息失败!");
            }else{
                mTotalPages = totalPages.intValue();
            }
        }
    };

    /**
     * 用于获取妹子图相册的图片列表信息
     * */
    Observer<List<MeizituGallery>> observerGalleries = new Observer<List<MeizituGallery>>() {
        @Override
        public void onCompleted() {
            ShowToast.showTestShortToast(mContext,mType + " load page " + mPage + " onCompleted()!");
            meizituSwipeRefreshLayout.setRefreshing(false);
            mIsLoadingData = false;
            if(mCanConnectToServer == true) {
                mPage++;
            }
        }

        @Override
        public void onError(Throwable e) {
            meizituSwipeRefreshLayout.setRefreshing(false);
            mIsLoadingData = false;
            ShowToast.showShortToast(mContext,"无法连接到妹子图的服务器..."
                    + mType + " load page " + mPage + " onError()! " + e.toString());
        }

        @Override
        public void onNext(List<MeizituGallery> meizituGalleryList) {
            if(meizituGalleryList == null){
                ShowToast.showShortToast(mContext,"无法连接到妹子图的服务器...");
            }else{
                if(meizituGalleryList.size() == 0){
                    ShowToast.showShortToast(mContext,"获取到了妹子图相册，但是里面没有图片...");
                }else{
                    meizituRecyclerViewAdapter.updateMeizituGalleryList(meizituGalleryList,mPage);
                    // test meizituGalleryList start
                    for(MeizituGallery meizituGallery:meizituGalleryList){
                        Logcat.showLog("MeizituGallery",meizituGallery.getObjectInformation());
                    }
                    // test meizituGalleryList end
                }
            }
        }
    };

    /**
     * 刷新从妹子图网站上下载的妹子相册信息，获取妹子图相册所占用的网页页数信息
     * */
    public void refreshMeizituGalleryData(){
        meizituSwipeRefreshLayout.setRefreshing(true);
        resetMeiziData();       // 清空妹子相册信息
        unsubscribe();
        subscription = Network.getMeizituService()
                .getPictureType(mType)
                .map(new Func1<ResponseBody, Integer>() {

                    @Override
                    public Integer call(ResponseBody responseBody) {
                        Integer totalPages = null;
                        try {
                            String responseBodyContent = responseBody.string();
                            mCanConnectToServer = HtmlParser.canConnectToServer(responseBodyContent, Constants.MEIZITU_WEBSITE);
                            if(mCanConnectToServer == true){
                                totalPages = HtmlParser.parseFirstMeizituGalleryListHtmlContent(responseBodyContent);
                            }else{ //连上了wifi热点，但是无法访问Internet
                                Logcat.showLog("canConnectToServerLog","无法访问www.mzitu.com");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return totalPages;
                    }
                })
                .subscribeOn(Schedulers.io())              //指定产生事件的线程
                .observeOn(AndroidSchedulers.mainThread()) //指定消费事件的线程
                .subscribe(observerPages);
    }

    /**
     * 向下滑动时，从网上加载第page页的妹子信息
     * */
    public void loadMoreMeizituGalleryData(int page){
        if(page <= mTotalPages){
            unsubscribe();   // 在新的Http请求前，取消上一次Http操作中所涉及的obserable与observer之间的订阅关系
            mIsLoadingData = true;                   // 在加载完第page页的妹子数据前，不能加载新的数据
            subscription = Network.getMeizituService()
                    .getPictureTypeInPage(mType,page)
                    .map(new Func1<ResponseBody, List<MeizituGallery>>() {

                        @Override
                        public List<MeizituGallery> call(ResponseBody responseBody) {
                            ArrayList<MeizituGallery> meizituGalleryList = null;
                            try {
                                String responseBodyContent = responseBody.string();
                                mCanConnectToServer = HtmlParser.canConnectToServer(responseBodyContent,Constants.MEIZITU_WEBSITE);
                                if(mCanConnectToServer == true){
                                    meizituGalleryList = HtmlParser.parseMeizituGalleryListHtmlContent(responseBodyContent);
                                }else{ //连上了wifi热点，但是无法访问Internet
                                    Logcat.showLog("canConnectToServerLog","无法访问www.mzitu.com");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return meizituGalleryList;
                        }
                    })
                    .subscribeOn(Schedulers.io())              //指定产生事件的线程
                    .observeOn(AndroidSchedulers.mainThread()) //指定消费事件的线程
                    .subscribe(observerGalleries);
        }else{
            ShowToast.showShortToast(mContext,"妹子被你看完啦 O(∩_∩)O哈哈~");
        }
    }
}
