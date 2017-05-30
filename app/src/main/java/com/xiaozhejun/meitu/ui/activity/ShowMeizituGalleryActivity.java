package com.xiaozhejun.meitu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.adapter.MeituPictureListRecyclerViewAdapter;
import com.xiaozhejun.meitu.model.MeituPicture;
import com.xiaozhejun.meitu.model.MeizituPicturePage;
import com.xiaozhejun.meitu.network.Network;
import com.xiaozhejun.meitu.network.parser.HtmlParser;
import com.xiaozhejun.meitu.ui.widget.MeituRecyclerView;
import com.xiaozhejun.meitu.util.ShowToast;
import com.xiaozhejun.meitu.util.Logcat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ShowMeizituGalleryActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private MeituRecyclerView mPictureRecyclerView;
    private MeituPictureListRecyclerViewAdapter mPictureRecyclerViewAdapter;
    protected Subscription mSubscription;   // 用于解除Obserable与Observer之间的订阅关系，防止内存泄露
    private ArrayList<Integer> meituPageList = new ArrayList<Integer>();  //妹子图相册对应的网页页数
    private ArrayList<Integer> mAlreadyDownloadPageList = new ArrayList<>(); //已经下载好的妹子图所对应的网页
    private ArrayList<MeituPicture> meituPictureList = new ArrayList<MeituPicture>(); //已经下载好的妹子图片信息
    private int mCurrentPage;    // 当前正在解析的妹子图片网页的页数
    private String groupId;
    private String title;
    // 测试用。。。
    private final String TAG = "ShowMeizituGalleryActivity";
    // 测试用。。。

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_meizitu_gallery);

        Bundle bundle = getIntent().getExtras();
        groupId = bundle.getString("groupId");
        title = bundle.getString("title");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("妹子图 " + groupId);
        // 为toolbar左边的返回按钮添加事件监听
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // finish方法和直接按返回键退出Activity的效果相同，都会触发onDestroy()回调函数
                ShowMeizituGalleryActivity.this.finish();
            }
        });

        mProgressBar = (ProgressBar)findViewById(R.id.progressBarInGallery);
        mProgressBar.setVisibility(View.VISIBLE);

        // 初始化PictureRecyclerView
        setupRecyclerView();
        // 开始下载图片
        beginDownLoad(groupId);

    }

    public void setupRecyclerView(){
        mPictureRecyclerView = (MeituRecyclerView)findViewById(R.id.meituPictureRecyclerView);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mPictureRecyclerViewAdapter = new MeituPictureListRecyclerViewAdapter(mPictureRecyclerView,true);
        mPictureRecyclerViewAdapter.initMeituPictureList(null);
        mPictureRecyclerView.setHasFixedSize(true);
        mPictureRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mPictureRecyclerView.setAdapter(mPictureRecyclerViewAdapter);
        mPictureRecyclerView.addOnScrollListener(new MeituRecyclerView.OnVerticalScrollListener(){

            @Override
            public void onBottom() {
                ShowToast.showShortToast(ShowMeizituGalleryActivity.this,"已经没有照片啦");
            }
        });
        mPictureRecyclerView.setOnItemClickListener(new MeituRecyclerView.OnItemClickListener(){

            @Override
            public void onItemClick(View view, int postion) {
                // ShowToast.showTestShortToast(ShowMeizituGalleryActivity.this,"点击了第"+(postion + 1)+"张图片!");
                // 跳转到PhotoViewActivity
                Bundle bundle = new Bundle();
                bundle.putInt("position",postion);
                bundle.putBoolean("isDownload",false);
                bundle.putParcelableArrayList("meituPictureList",meituPictureList);
                Intent intent = new Intent(ShowMeizituGalleryActivity.this,PhotoViewActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    // 负责处理获取相册网页页数的事件
    Observer<ArrayList<Integer>> observerPages = new Observer<ArrayList<Integer>>() {
        @Override
        public void onCompleted() {
            ShowToast.showTestShortToast(ShowMeizituGalleryActivity.this,"完成获取相册网页页数的操作");
            // 开始下载相册中的每张妹子图片
            downLoadingPicture(groupId);
        }

        @Override
        public void onError(Throwable e) {
            mProgressBar.setVisibility(View.INVISIBLE);
            ShowToast.showShortToast(ShowMeizituGalleryActivity.this,"获取相册的网页页数操作失败:"
                    + e.toString());
        }

        @Override
        public void onNext(ArrayList<Integer> integers) {
            meituPageList.addAll(integers);
        }
    };

    // 负责处理接收妹子图照片信息的事件
    Observer<MeizituPicturePage> observerMeituPictures = new Observer<MeizituPicturePage>() {
        @Override
        public void onCompleted() {
            Logcat.showLog("observerMeituPictures", "onCompleted mAlreadyDownloadPageList = "
                    + mAlreadyDownloadPageList.toString());
            meituPageList.removeAll(mAlreadyDownloadPageList);  // 刪除所有已经下载好的网页
            Logcat.showLog("observerMeituPictures", "onCompleted After removeAll mAlreadyDownloadPageList"
                    + " meituPageList = " + meituPageList.toString());
            mAlreadyDownloadPageList.clear();
            if (meituPageList.isEmpty()) {
                //按照图片url从小到大进行排序
                Collections.sort(meituPictureList, new Comparator<MeituPicture>() {
                    @Override
                    public int compare(MeituPicture meituPicture1, MeituPicture meituPicture2) {
                        return meituPicture1.getPictureUrl()
                                .compareToIgnoreCase(meituPicture2.getPictureUrl());
                    }
                });
                //删除meituPictureList中的重复元素
                LinkedHashSet<MeituPicture> meituPictureLinkedHashSet = new LinkedHashSet<>(meituPictureList);
                meituPictureList.clear();
                meituPictureList = new ArrayList<>(meituPictureLinkedHashSet);
                //隐藏进度条，更新妹子图适配器中的数据
                mProgressBar.setVisibility(View.INVISIBLE);
                mPictureRecyclerViewAdapter.updateMeituPictureList(meituPictureList, 1);
            } else {
                downLoadingPicture(groupId);  // 继续下载相册中剩余妹子的图片
            }
            // test start
            ShowToast.showTestShortToast(ShowMeizituGalleryActivity.this,"完成获取相册信息的操作");
            StringBuilder information = new StringBuilder();
            for(MeituPicture meituPicture:meituPictureList){
                information.append(meituPicture.getTitle() + " " + meituPicture.getPictureUrl() + "\n");
                Logcat.showLog(TAG,meituPicture.getTitle() + " " + meituPicture.getPictureUrl());
            }
            // test end
        }

        @Override
        public void onError(Throwable e) {
            Logcat.showLog("observerMeituPictures",e.toString() + "onError mCurrentPage = " + mCurrentPage);
            if (e.toString().contains("HTTP 514")) { // 针对 HTTP 514 错误进行特殊处理
                Logcat.showLog("observerMeituPictures", "onError mAlreadyDownloadPageList = "
                        + mAlreadyDownloadPageList.toString());
                meituPageList.removeAll(mAlreadyDownloadPageList);  // 刪除所有已经下载好的网页
                Logcat.showLog("observerMeituPictures","onError After removeAll mAlreadyDownloadPageList"
                        + " meituPageList = " + meituPageList.toString());
                mAlreadyDownloadPageList.clear();
                // 获取HTTP 514所对应的url，然后根据url的最后一个字段确定出现514错误的相册页面，
                // 然后将该页面重新添加到meituPageList中!!!
                if(meituPageList.contains(mCurrentPage) == false){
                    meituPageList.add(mCurrentPage);
                }
                Logcat.showLog("observerMeituPictures","onError After add(mCurrentPage)"
                        + " meituPageList = " + meituPageList.toString());
                if (meituPageList.isEmpty()) {         // todo:这里的if语句可以去掉
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mPictureRecyclerViewAdapter.updateMeituPictureList(meituPictureList, 1);
                } else {
                    downLoadingPicture(groupId);  // 继续下载相册中剩余妹子的图片
                }
            }else{
                ShowToast.showShortToast(ShowMeizituGalleryActivity.this,"获取相册信息操作失败,发生了错误:" +
                        e.toString());
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onNext(MeizituPicturePage meizituPicturePage) {
            meituPictureList.addAll(meizituPicturePage.getMeizituPictureList());
            mAlreadyDownloadPageList.add(meizituPicturePage.getPage());
            Logcat.showLog("observerMeituPictures", "onNext mCurrentPage = " + mCurrentPage
                    + " meizituPicturePage.getPage() = " + meizituPicturePage.getPage());
        }
    };

    /**
     * 解析妹子图相册的首页得到相册网页页数，开始下载妹子图的图片
     * */
    public void beginDownLoad(String groupId){
        unsubscribe();
        mSubscription = Network.getMeizituService()
                .getPictureWithGroupId(groupId)
                .map(new Func1<ResponseBody, ArrayList<Integer>>() {
                    @Override
                    public ArrayList<Integer> call(ResponseBody responseBody) {
                        ArrayList<Integer> pageList = null;
                        try {
                            String responseBodyContent = responseBody.string();
                            pageList = HtmlParser.parseFirstMeizituGalleryHtmlContent(responseBodyContent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return pageList;
                    }
                })
                .subscribeOn(Schedulers.io())              //指定产生事件的线程
                .observeOn(AndroidSchedulers.mainThread()) //指定消费事件的线程
                .subscribe(observerPages);
    }

    /**
     * 执行下载图片的操作
     * 这是本App中最牛逼的一段程序!!!
     * 写于2016-08-04
     * */
    public void downLoadingPicture(String groupId){
        final String groupId2 = groupId;
        unsubscribe();
        Observable.from(meituPageList)
                .flatMap(new Func1<Integer, Observable<MeizituPicturePage>>() {
                    @Override
                    public Observable<MeizituPicturePage> call(Integer page) {
                        Observable<MeizituPicturePage> observable = Network.getMeizituService()
                                .getPictureWithGroupIdInPage(groupId2,page)
                                .map(new Func1<ResponseBody, MeizituPicturePage>() {
                                    @Override
                                    public MeizituPicturePage call(ResponseBody responseBody) {
                                        MeizituPicturePage meizituPicturePage = new MeizituPicturePage();
                                        try {
                                            String responseBodyContent = responseBody.string();
                                            meizituPicturePage = HtmlParser
                                                    .parseMeizituGalleryHtmlContent(responseBodyContent);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        return meizituPicturePage;
                                    }
                                });
                        mCurrentPage = page;//标记当前正在处理的网页，需要用到这个变量来标记onError()的网页
                        return observable;
                    }
                })
                .subscribeOn(Schedulers.io())              //指定产生事件的线程
                .observeOn(AndroidSchedulers.mainThread()) //指定消费事件的线程
                .subscribe(observerMeituPictures);
    }

    /**
     * 解除Subscriber与Observable之间的订阅关系
     * */
    protected void unsubscribe(){
        if(mSubscription != null && !mSubscription.isUnsubscribed()){
            mSubscription.unsubscribe();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unsubscribe();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unsubscribe();
    }
}
