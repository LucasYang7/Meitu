package com.xiaozhejun.meitu.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.model.MeituPicture;
import com.xiaozhejun.meitu.network.Network;
import com.xiaozhejun.meitu.network.parser.HtmlParser;
import com.xiaozhejun.meitu.ui.widget.ShowToast;
import com.xiaozhejun.meitu.util.Logcat;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ShowMeizituGalleryActivity extends AppCompatActivity {

    private final String TAG = "ShowMeizituGalleryActivity";
    protected Subscription mSubscription;   // 用于解除Obserable与Observer之间的订阅关系，防止内存泄露
    private ArrayList<Integer> meituPageList = new ArrayList<Integer>();  //妹子图相册对应的网页页数
    private String groupId;
    private String title;

    // 测试用。。。
    private ArrayList<MeituPicture> meituPictureList = new ArrayList<MeituPicture>();
    private TextView textView;
    // 测试用。。。

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_meizitu_gallery);

        Bundle bundle = getIntent().getExtras();
        groupId = bundle.getString("groupId");
        title = bundle.getString("title");

        textView = (TextView)findViewById(R.id.testTextViewInMeizituGallery);
        textView.setText(title + "\n" + groupId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // 为toolbar左边的返回按钮添加事件监听
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // finish方法和直接按返回键退出Activity的效果相同，都会触发onDestroy()回调函数
                ShowMeizituGalleryActivity.this.finish();
            }
        });

        beginDownLoad(groupId);

    }

    // 负责处理获取相册网页页数的事件
    Observer<ArrayList<Integer>> observerPages = new Observer<ArrayList<Integer>>() {
        @Override
        public void onCompleted() {
            ShowToast.showShortToast(ShowMeizituGalleryActivity.this,"完成获取相册网页页数的操作");
            // 开始下载相册中的每张妹子图片
            downLoadingPicture(groupId);
        }

        @Override
        public void onError(Throwable e) {
            ShowToast.showShortToast(ShowMeizituGalleryActivity.this,"获取网页页数操作失败"
                    + e.toString());
        }

        @Override
        public void onNext(ArrayList<Integer> integers) {
            meituPageList.addAll(integers);
        }
    };

    // 负责处理接收妹子图照片信息的事件
    Observer<ArrayList<MeituPicture>> observerMeituPictures = new Observer<ArrayList<MeituPicture>>() {
        @Override
        public void onCompleted() {
            ShowToast.showShortToast(ShowMeizituGalleryActivity.this,"完成获取相册信息的操作");
            StringBuilder information = new StringBuilder();
            for(MeituPicture meituPicture:meituPictureList){
                information.append(meituPicture.getTitle() + " " + meituPicture.getPictureUrl() + "\n");
                Logcat.showLog(TAG,meituPicture.getTitle() + " " + meituPicture.getPictureUrl());
            }
            textView.setText(information.toString());
        }

        @Override
        public void onError(Throwable e) {
            ShowToast.showShortToast(ShowMeizituGalleryActivity.this,"获取相册信息操作失败"
                    + e.toString());
        }

        @Override
        public void onNext(ArrayList<MeituPicture> meituPictures) {
            meituPictureList.addAll(meituPictures);
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
                .flatMap(new Func1<Integer, Observable<ArrayList<MeituPicture>>>() {
                    @Override
                    public Observable<ArrayList<MeituPicture>> call(Integer page) {
                        Observable<ArrayList<MeituPicture>> observable = Network.getMeizituService()
                                .getPictureWithGroupIdInPage(groupId2,page)
                                .map(new Func1<ResponseBody, ArrayList<MeituPicture>>() {
                                    @Override
                                    public ArrayList<MeituPicture> call(ResponseBody responseBody) {
                                        ArrayList<MeituPicture> meituPictures = new ArrayList<MeituPicture>();
                                        try {
                                            String responseBodyContent = responseBody.string();
                                            meituPictures = HtmlParser.parseMeizituGalleryHtmlContent(responseBodyContent);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        return meituPictures;
                                    }
                                });
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
