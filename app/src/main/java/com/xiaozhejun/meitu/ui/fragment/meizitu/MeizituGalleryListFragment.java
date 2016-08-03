package com.xiaozhejun.meitu.ui.fragment.meizitu;


import android.content.Context;
import android.content.Intent;
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
import com.xiaozhejun.meitu.network.parser.HtmlParser;
import com.xiaozhejun.meitu.ui.activity.ShowMeizituGalleryActivity;
import com.xiaozhejun.meitu.ui.fragment.BaseFragment;
import com.xiaozhejun.meitu.ui.widget.MeituRecyclerView;
import com.xiaozhejun.meitu.ui.widget.ShowToast;
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
    private MeizituRecyclerViewAdapter meizituRecyclerViewAdapter;
    private boolean mCanAddNewMeizitu;    // 判断能否请求新的网页，加载更多妹子的图片
    private int mPage;      //表示妹子图片相册链接后面的分页
    private String mType;   //表示妹子图片所属的类型，例如：日本妹子，性感妹子等
    private Context mContext;       // 测试用

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
        mCanAddNewMeizitu = false;     // 在加载完首页的数据前，不能再加载新的妹子数据
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
        meizituRecyclerViewAdapter = new MeizituRecyclerViewAdapter(meizituRecyclerView); //绑定meizituRecyclerViewAdapter和meizituRecyclerView
        meizituRecyclerViewAdapter.initMeizituGalleryList(null);
        meizituRecyclerView.setHasFixedSize(true);
        meizituRecyclerView.setLayoutManager(staggeredGridLayoutManager);  //设置RecyclerView的布局
        meizituRecyclerView.setAdapter(meizituRecyclerViewAdapter); //设置RecyclerView的适配器
        meizituRecyclerView.addOnScrollListener(new MeituRecyclerView.OnVerticalScrollListener() {
            @Override
            public void onBottom() {
                ShowToast.showShortToast(mContext,mType + " 已经滑到底部了" + "page " + mPage);
                if(mCanAddNewMeizitu == true){
                    ShowToast.showShortToast(mContext,mType + " 正在加载第" + mPage + "页的妹子数据");
                    AddNewMeizituGalleryData(mPage);
                }
            }
        }); // 为RecyclerView添加滑动事件监听
        meizituRecyclerView.setOnItemClickListener(new MeituRecyclerView.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int postion) {
                MeizituGallery meizituGallery = meizituRecyclerViewAdapter.getMeizituGallery(postion);
                ShowToast.showLongToast(mContext, meizituGallery.getObjectInformation());
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
        meizituSwipeRefreshLayout.post(new Runnable() { // 设置SwipeRefreshLayout自动刷新
            @Override
            public void run() {
                refreshMeizituGalleryData();
            }
        });
        return view;
    }

    // 对应SwipeRefreshLayout的刷新事件监听
    @Override
    public void onRefresh() {
        refreshMeizituGalleryData();
    }

    // observer是一个匿名内部类对象
    Observer<List<MeizituGallery>> observer = new Observer<List<MeizituGallery>>() {
        @Override
        public void onCompleted() {
            ShowToast.showLongToast(mContext,mType + " load page " + mPage + " onCompleted()!");
            meizituSwipeRefreshLayout.setRefreshing(false);
            mCanAddNewMeizitu = true;
            mPage++;
        }

        @Override
        public void onError(Throwable e) {
            meizituSwipeRefreshLayout.setRefreshing(false);
            mCanAddNewMeizitu = true;
            ShowToast.showLongToast(mContext,mType + " load page " + mPage + " onError()! " + e.toString());
        }

        @Override
        public void onNext(List<MeizituGallery> meizituGalleryList) {
            meizituRecyclerViewAdapter.updateMeizituGalleryList(meizituGalleryList,mPage);
            // test meizituGalleryList start
            for(MeizituGallery meizituGallery:meizituGalleryList){
                //Log.e("MeizituGallery",meizituGallery.getObjectInformation());
                Logcat.ShowLog("MeizituGallery",meizituGallery.getObjectInformation());
            }
            // test meizituGalleryList end
        }
    };

    /**
     * 刷新从妹子图网站上下载的妹子相册信息
     * */
    public void refreshMeizituGalleryData(){
        meizituSwipeRefreshLayout.setRefreshing(true);
        resetMeiziData();       // 清空妹子相册信息
        unsubscribe();
        subscription = Network.getMeizituService()
                .getPictureType(mType)
                .map(new Func1<ResponseBody, List<MeizituGallery>>() {

                    @Override
                    public List<MeizituGallery> call(ResponseBody responseBody) {
                        ArrayList<MeizituGallery> meizituGalleryList = null;
                        try {
                            String responseBodyContent = responseBody.string();
                            meizituGalleryList = HtmlParser.parseMeizituGalleryListHtmlContent(responseBodyContent);
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
    public void AddNewMeizituGalleryData(int page){
        unsubscribe();   // 在新的Http请求前，取消上一次Http操作中所涉及的obserable与observer之间的订阅关系
        mCanAddNewMeizitu = false;                   // 在加载完第page页的妹子数据前，不能加载新的数据
        subscription = Network.getMeizituService()
                .getPictureTypeInPage(mType,page)
                .map(new Func1<ResponseBody, List<MeizituGallery>>() {

                    @Override
                    public List<MeizituGallery> call(ResponseBody responseBody) {
                        ArrayList<MeizituGallery> meizituGalleryList = null;
                        try {
                            String responseBodyContent = responseBody.string();
                            meizituGalleryList = HtmlParser.parseMeizituGalleryListHtmlContent(responseBodyContent);
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
}
