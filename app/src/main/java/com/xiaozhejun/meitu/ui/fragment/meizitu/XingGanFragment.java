package com.xiaozhejun.meitu.ui.fragment.meizitu;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.adapter.MeizituRecyclerViewAdapter;
import com.xiaozhejun.meitu.model.MeizituGallery;
import com.xiaozhejun.meitu.network.Network;
import com.xiaozhejun.meitu.ui.fragment.BaseFragment;
import com.xiaozhejun.meitu.ui.widget.ShowToast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
public class XingGanFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "XingGanFragment";
    private SwipeRefreshLayout xingganSwipeRefreshLayout;
    private RecyclerView xingganRecyclerView;
    private MeizituRecyclerViewAdapter meizituRecyclerViewAdapter;
    private Context mContext;
    List<MeizituGallery> mMeizituGalleryList = new ArrayList<MeizituGallery>();    // 保存图片相册的信息，如果是下拉刷新，则清空meizituGalleryList。
    List<MeizituGallery> mNewMeizituGalleryList = new ArrayList<MeizituGallery>(); // 保存每次新增加的妹子图片的信息
    int page;   //记录当前最后访问的妹子相册信息所在的页数，下拉刷新时要重置为1
    boolean canAddMeizitu;   // 判断是否能加载新的妹子图相册
    public XingGanFragment() {
        // Required empty public constructor
    }

    // test 测试能否正常加载妹子相册的信息
    // observer是一个匿名内部类对象
    Observer<List<MeizituGallery>> observer = new Observer<List<MeizituGallery>>() {
        @Override
        public void onCompleted() {
            if(page == 1){
                mMeizituGalleryList.clear();      // 刷新妹子图的首页时，要清空原来的数据
            }
            mMeizituGalleryList.addAll(mNewMeizituGalleryList);  //更新XingGanFragment的妹子图数据
            meizituRecyclerViewAdapter.setMeizituGalleryList(mMeizituGalleryList);
            meizituRecyclerViewAdapter.notifyDataSetChanged();
            ShowToast.showLongToast(mContext,"load page " + page + " onCompleted()!");
            page++;   // 增大妹子图相册所在的网页页数
            canAddMeizitu = true;
        }

        @Override
        public void onError(Throwable e) {
            xingganSwipeRefreshLayout.setRefreshing(false);
            canAddMeizitu = true;
            ShowToast.showLongToast(mContext,"load page " + page + " onError()! " + e.toString());
            Log.e(TAG,"onError()!" + e.toString());
        }

        @Override
        public void onNext(List<MeizituGallery> meizituGalleryList) {
            xingganSwipeRefreshLayout.setRefreshing(false);
            mNewMeizituGalleryList = meizituGalleryList;
        }
    };

    /**
     * 刷新从妹子图网站上下载的妹子相册信息
     * */
    public void RefreshXingganMeiziData(){
        xingganSwipeRefreshLayout.setRefreshing(true);
        resetMeiziData();       // 清空妹子相册信息
        unsubscribe();
        subscription = Network.getMeizituService()
                .getPictureType("xinggan")
                .map(new Func1<ResponseBody, List<MeizituGallery>>() {

                    @Override
                    public List<MeizituGallery> call(ResponseBody responseBody) {
                        ArrayList<MeizituGallery> meizituGalleryList = null;
                        try {
                            String responseBodyContent = responseBody.string();
                            meizituGalleryList = parseHtmlContent(responseBodyContent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return meizituGalleryList;
                    }
                })
                .subscribeOn(Schedulers.io())              //指定产生事件的线程
                .observeOn(AndroidSchedulers.mainThread()) //指定消费事件的线程
                .subscribe(observer);
    }

    /**
     * 向下滑动时，从网上加载第page页的妹子信息
     * */
    public void AddXingganMeiziData(int page){
        //unsubscribe();
        canAddMeizitu = false;                   // 在加载完第page页的妹子数据前，不能加载新的数据
        subscription = Network.getMeizituService()
                .getPictureTypeInPage("xinggan",page)
                .map(new Func1<ResponseBody, List<MeizituGallery>>() {

                    @Override
                    public List<MeizituGallery> call(ResponseBody responseBody) {
                        ArrayList<MeizituGallery> meizituGalleryList = null;
                        try {
                            String responseBodyContent = responseBody.string();
                            meizituGalleryList = parseHtmlContent(responseBodyContent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return meizituGalleryList;
                    }
                })
                .subscribeOn(Schedulers.io())              //指定产生事件的线程
                .observeOn(AndroidSchedulers.mainThread()) //指定消费事件的线程
                .subscribe(observer);
    }

    /**
     * 执行下拉SwipeRefreshLayout进行刷新操作时，要清空原有的数据
     * */
    public void resetMeiziData(){
        //mMeizituGalleryList.clear();  // 这里清空妹子图的数据，如果执行下拉SwipeRefreshLayout进行刷新时，因为此时没有数据，所以会报java.lang.IndexOutOfBoundsException
        page = 1;
        canAddMeizitu = false;        // 在加载完首页的数据前，不能再加载新的妹子数据
    }

    /**
     * 使用jsoup解析html文档获取妹子图相册的详细信息
     * */
    public ArrayList<MeizituGallery> parseHtmlContent(String htmlContent){
        ArrayList<MeizituGallery> meizituGalleryList = new ArrayList<MeizituGallery>();
        Document document = Jsoup.parse(htmlContent);
        Elements galleryElements = document.select("ul#pins > li");
        for(Element galleryElement:galleryElements){
            // 获取html中各个相册属性所对应的元素
            MeizituGallery meizituGallery = new MeizituGallery();
            Element aElement = galleryElement.select("a").first();
            Element imgElement = galleryElement.select("img").first();
            Element timeElement = galleryElement.select("span.time").first();
            Element viewElement = galleryElement.select("span.view").first();
            // 将html中的元素转换为相册对应的属性值
            meizituGallery.setGalleryUrl(aElement.attr("href"));
            meizituGallery.setTitle(imgElement.attr("alt"));
            meizituGallery.setPictureUrl(imgElement.attr("data-original"));
            meizituGallery.setTime(timeElement.text());
            meizituGallery.setViewTimes(viewElement.text());
            // 将相册信息添加到相册列表中
            meizituGalleryList.add(meizituGallery);
        }
        return meizituGalleryList;
    }
    // test

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = container.getContext();
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_xing_gan, container, false);
        // 设置RecyclerView
        xingganRecyclerView = (RecyclerView)view.findViewById(R.id.xingganRecyclerView);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        meizituRecyclerViewAdapter = new MeizituRecyclerViewAdapter(mContext);
        meizituRecyclerViewAdapter.setMeizituGalleryList(mMeizituGalleryList);
        xingganRecyclerView.setHasFixedSize(true);
        xingganRecyclerView.setLayoutManager(staggeredGridLayoutManager);  //设置RecyclerView的布局
        xingganRecyclerView.setAdapter(meizituRecyclerViewAdapter); //设置RecyclerView的适配器
        xingganRecyclerView.addOnScrollListener(new OnVerticalScrollListener()); // 为RecyclerView添加滑动事件监听
        // 设置SwipeRefreshLayout
        xingganSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.xingganSwipeRefreshLayout);
        xingganSwipeRefreshLayout.setOnRefreshListener(this);
        xingganSwipeRefreshLayout.setColorSchemeResources(R.color.colorSwipeRefresh);
        xingganSwipeRefreshLayout.post(new Runnable() { // 设置SwipeRefreshLayout自动刷新
            @Override
            public void run() {
                RefreshXingganMeiziData();
            }
        });
        return view;
    }

    /**
     * 对应SwipeRefreshLayout下拉刷新的操作
     * */
    @Override
    public void onRefresh() {
        RefreshXingganMeiziData();
    }

    /**
     * 为RecyclerView设置是否到底部的事件监听
     * 通过staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions
     * 来找出最后一个完成出现的item
     * */
    class OnVerticalScrollListener extends RecyclerView.OnScrollListener{

        /**
         * 判断当前显示的item是否为RecyclerView中的最后一个item
         * */
        private boolean isLastItemDisplaying(RecyclerView recyclerView){
            if(recyclerView.getAdapter().getItemCount() != 0){
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager)
                        recyclerView.getLayoutManager();
                int [] lastCompletelyVisiblePostions = staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(null);
                int lastCompletelyVisibleItemPosition = 0;   //找到当前页面最后完整显示的item的位置 　
                for(int i = 0;i < lastCompletelyVisiblePostions.length;i++){
                    if(lastCompletelyVisiblePostions[i] > lastCompletelyVisibleItemPosition){
                        lastCompletelyVisibleItemPosition = lastCompletelyVisiblePostions[i];
                    }
                }
                if (lastCompletelyVisibleItemPosition != RecyclerView.NO_POSITION &&
                        lastCompletelyVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                    return true;
            }
            return false;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            // RecyclerView已经滑到底部且RecyclerView处于静止状态
            if(isLastItemDisplaying(recyclerView) == true && newState == RecyclerView.SCROLL_STATE_IDLE){
                //Toast.makeText(mContext,"已经滑到底部了，没有图片了。。。",Toast.LENGTH_SHORT).show();
                ShowToast.showShortToast(mContext,"已经滑到底部了" + "page " + page);
                if(canAddMeizitu == true){
                    ShowToast.showShortToast(mContext,"正在加载第" + page + "页的妹子数据");
                    AddXingganMeiziData(page);
                }
            }
        }
    }
}
