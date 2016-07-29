package com.xiaozhejun.meitu.ui.fragment.meizitu;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.adapter.MeizituRecyclerViewAdapter;
import com.xiaozhejun.meitu.model.MeizituGallery;
import com.xiaozhejun.meitu.network.Network;
import com.xiaozhejun.meitu.ui.fragment.BaseFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Observable;
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
    static List<MeizituGallery> meizituGalleryList = new ArrayList<MeizituGallery>();

    //test 测试能否显式html文件
    //TextView textView;

    Observer<List<MeizituGallery>> observer = new Observer<List<MeizituGallery>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            xingganSwipeRefreshLayout.setRefreshing(false);
            Log.e(TAG,"onError()!");
            Log.e(TAG, e.toString());
        }

        @Override
        public void onNext(List<MeizituGallery> meizituGalleryList) {
            xingganSwipeRefreshLayout.setRefreshing(false);
            XingGanFragment.meizituGalleryList = meizituGalleryList;  //更新XingGanFragment的妹子图数据
            meizituRecyclerViewAdapter.notifyDataSetChanged();
        }
    };

    /**
     * 从网站上下载性感妹子的图片信息
     * */
    public void RefreshXingganMeiziData(){
        xingganSwipeRefreshLayout.setRefreshing(true);
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
    //test

    public XingGanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context context = container.getContext();
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_xing_gan, container, false);
        // 设置RecyclerView
        xingganRecyclerView = (RecyclerView)view.findViewById(R.id.xingganRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        meizituRecyclerViewAdapter = new MeizituRecyclerViewAdapter(context);
        meizituRecyclerViewAdapter.setMeizituGalleryList(meizituGalleryList);
        xingganRecyclerView.setHasFixedSize(true);
        xingganRecyclerView.setLayoutManager(linearLayoutManager);  //设置RecyclerView的布局
        xingganRecyclerView.setAdapter(meizituRecyclerViewAdapter); //设置RecyclerView的适配器
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

    @Override
    public void onRefresh() {
        RefreshXingganMeiziData();
    }
}
