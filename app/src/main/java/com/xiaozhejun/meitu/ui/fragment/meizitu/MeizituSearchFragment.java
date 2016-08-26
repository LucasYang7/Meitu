package com.xiaozhejun.meitu.ui.fragment.meizitu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.adapter.MeizituGalleryListRecyclerViewAdapter;
import com.xiaozhejun.meitu.model.MeizituGallery;
import com.xiaozhejun.meitu.network.Network;
import com.xiaozhejun.meitu.network.parser.HtmlParser;
import com.xiaozhejun.meitu.ui.activity.ShowMeizituGalleryActivity;
import com.xiaozhejun.meitu.ui.fragment.BaseFragment;
import com.xiaozhejun.meitu.ui.widget.MeituRecyclerView;
import com.xiaozhejun.meitu.util.Constants;
import com.xiaozhejun.meitu.util.Logcat;
import com.xiaozhejun.meitu.util.ShowToast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by yangzhe on 16-8-25.
 */
public class MeizituSearchFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    private TextView mNotFoundHintTextView;
    private SwipeRefreshLayout meizituSwipeRefreshLayout;
    private MeituRecyclerView meizituRecyclerView;
    private MeizituGalleryListRecyclerViewAdapter meizituRecyclerViewAdapter;
    private boolean mIsLoadingData;    // 判断能否请求新的网页，加载更多妹子的图片
    private boolean mCanConnectToServer;
    private boolean mIsAutoRefresh = false;    // 判断是否自动刷新数据
    private int mPage;       //表示妹子图片相册链接后面的分页
    private int mTotalPages = 1; //表示妹子图某类相册所对应的网页总页数，总页数初始值为整型数的最大值
    private String mSearchKeyword = ""; // 搜索妹子图网站所用的关键字
    private String mURLEncoderSearchKeyword = "";  // 经过URL编码后的搜索关键字
    private Context mContext;       // 测试用

    public MeizituSearchFragment(){
    }

    /**
     * 设置搜索妹子图网站的关键字
     * */
    public void setSearchKeyword(String keyword,String URLEncoderKeyword){
        mSearchKeyword = keyword;
        mURLEncoderSearchKeyword = URLEncoderKeyword;
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
        View view =  inflater.inflate(R.layout.fragment_meizitu_search, container, false);
        // 设置RecyclerView
        meizituRecyclerView = (MeituRecyclerView) view.findViewById(R.id.meizituSearchRecyclerView);
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
                ShowToast.showTestShortToast(mContext,mSearchKeyword + " 已经滑到底部了" + "page " + mPage);
                if(mIsLoadingData == false){
                    ShowToast.showTestShortToast(mContext,mSearchKeyword + " 正在加载第" + mPage + "页的妹子数据");
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
        meizituSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.meizituSearchSwipeRefreshLayout);
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
        // 设置未找到妹子的提示信息
        mNotFoundHintTextView = (TextView)view.findViewById(R.id.notFoundHintTextView);
        return view;
    }

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
                ShowToast.showTestShortToast(mContext,"妹子图 " + mSearchKeyword + " 的网页总数为" + mTotalPages);
            }
        }

        @Override
        public void onError(Throwable e) {
            meizituSwipeRefreshLayout.setRefreshing(false);
            mIsLoadingData = false;
            mNotFoundHintTextView.setText("对不起,没有找到与" + mSearchKeyword + "相关的妹子,发生了错误:" + e.toString());
            mNotFoundHintTextView.setVisibility(View.VISIBLE);
            meizituRecyclerViewAdapter.updateMeizituGalleryList(null,mPage);
            ShowToast.showShortToast(getActivity(),"无法连接到妹子图的服务器... 妹子图 " + mSearchKeyword
                    + " 的获取网页总数信息失败! " + e.toString());
        }

        @Override
        public void onNext(Integer totalPages) {
            if(totalPages == null){
                ShowToast.showShortToast(getActivity(),"无法连接到妹子图的服务器... 妹子图 " + mSearchKeyword
                        + " 的获取网页总数信息失败!");
            }else{
                if(totalPages.intValue() != Integer.MAX_VALUE){
                    mTotalPages = totalPages.intValue();
                }else{
                    mTotalPages = 1;
                }
            }
        }
    };

    /**
     * 用于获取妹子图相册的图片列表信息
     * */
    Observer<List<MeizituGallery>> observerGalleries = new Observer<List<MeizituGallery>>() {
        @Override
        public void onCompleted() {
            ShowToast.showTestShortToast(mContext,mSearchKeyword + " load page " + mPage + " onCompleted()!");
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
            ShowToast.showShortToast(getActivity(),"无法连接到妹子图的服务器..."
                    + mSearchKeyword + " load page " + mPage + " onError()! " + e.toString());
        }

        @Override
        public void onNext(List<MeizituGallery> meizituGalleryList) {
            if(meizituGalleryList == null){
                ShowToast.showShortToast(getActivity(),"无法连接到妹子图的服务器...");
            }else{
                if(meizituGalleryList.size() == 0){
                    //ShowToast.showShortToast(getActivity(),"对不起,没有找到与" + mSearchKeyword + "相关的妹子!");
                    mNotFoundHintTextView.setText("对不起,没有找到与" + mSearchKeyword + "相关的妹子 ╮(╯▽╰)╭");
                    mNotFoundHintTextView.setVisibility(View.VISIBLE);
                    meizituRecyclerViewAdapter.updateMeizituGalleryList(meizituGalleryList,mPage);
                }else{
                    mNotFoundHintTextView.setVisibility(View.GONE);
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

    public void refreshMeizituGalleryData() {
        meizituSwipeRefreshLayout.setRefreshing(true);
        resetMeiziData();       // 清空妹子相册信息
        unsubscribe();
        subscription = Network.getMeizituService()
                .getSearchResult(mURLEncoderSearchKeyword)
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

    public void loadMoreMeizituGalleryData(int page) {
        if(page <= mTotalPages){
            unsubscribe();   // 在新的Http请求前，取消上一次Http操作中所涉及的obserable与observer之间的订阅关系
            mIsLoadingData = true;                   // 在加载完第page页的妹子数据前，不能加载新的数据
            subscription = Network.getMeizituService()
                    .getSearchResultInPages(mURLEncoderSearchKeyword,page)
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
            ShowToast.showShortToast(getActivity(),"妹子被你看完啦 O(∩_∩)O哈哈~");
        }
    }


}
