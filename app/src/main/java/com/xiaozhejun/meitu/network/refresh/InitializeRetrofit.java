package com.xiaozhejun.meitu.network.refresh;

import com.xiaozhejun.meitu.model.GankMeiziJsonResult;
import com.xiaozhejun.meitu.model.HuabanMeinvJsonResult;
import com.xiaozhejun.meitu.network.Network;
import com.xiaozhejun.meitu.util.Constants;
import com.xiaozhejun.meitu.util.Logcat;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 在进行正式的Http请求之前，先初始化Retrofit
 * Created by yangzhe on 16-8-28.
 */
public class InitializeRetrofit {
    // 使用单例模式
    private static InitializeRetrofit INSTANCE = new InitializeRetrofit();

    private InitializeRetrofit(){

    }

    public static InitializeRetrofit getInstance(){
        return INSTANCE;
    }

    /**
     * 用于接收妹子图的网页内容
     * */
    Observer<ResponseBody> observerMeizitu = new Observer<ResponseBody>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Logcat.showLog("InitializeRetrofit","observerMeizitu:\t" + e.toString());
        }

        @Override
        public void onNext(ResponseBody responseBody) {
            try {
                String responseBodyString = responseBody.string();
                Logcat.showLog("InitializeRetrofit","observerMeizitu:\t" + responseBodyString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 用于接收豆瓣美女的网页内容
     * */
    Observer<ResponseBody> observerDoubanMeinv = new Observer<ResponseBody>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Logcat.showLog("InitializeRetrofit","observerDoubanMeinv:\t" + e.toString());
        }

        @Override
        public void onNext(ResponseBody responseBody) {
            try {
                String responseBodyString = responseBody.string();
                Logcat.showLog("InitializeRetrofit","observerDoubanMeinv:\t" + responseBodyString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    Observer<GankMeiziJsonResult> observerGankMeizi = new Observer<GankMeiziJsonResult>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Logcat.showLog("InitializeRetrofit","observerGankMeizi:\t" + e.toString());
        }

        @Override
        public void onNext(GankMeiziJsonResult gankMeiziJsonResult) {
            Logcat.showLog("InitializeRetrofit","observerGankMeizi:\t" + gankMeiziJsonResult.toString());
        }
    };

    Observer<HuabanMeinvJsonResult> observerHuabanMeinv = new Observer<HuabanMeinvJsonResult>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Logcat.showLog("InitializeRetrofit","observerHuabanMeinv:\t" + e.toString());
        }

        @Override
        public void onNext(HuabanMeinvJsonResult huabanMeinvJsonResult) {
            Logcat.showLog("InitializeRetrofit","observerHuabanMeinv:\t" + huabanMeinvJsonResult.toString());
        }
    };

    public void initRetrofitClient(){
        initMeizituRetrofitClient();
        initDoubanMeinvRetrofitClient();
        initGankMeiziRetrofitClient();
        initHuabanMeinvRetrofitClient();
    }

    public void initMeizituRetrofitClient(){
        Network.getMeizituService()
                .getSearchResult("cake")
                .subscribeOn(Schedulers.io())              //指定产生事件的线程
                .observeOn(AndroidSchedulers.mainThread()) //指定消费事件的线程
                .subscribe(observerMeizitu);
    }

    public void initDoubanMeinvRetrofitClient(){
        Network.getDoubanMeinvService()
                .getAllBeauties(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observerDoubanMeinv);
    }

    public void initGankMeiziRetrofitClient(){
        Network.getGankMeiziService()
                .getGankMeizis(Constants.GANK_MEIZI_NUMBER,1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observerGankMeizi);
    }

    public void initHuabanMeinvRetrofitClient(){
        Network.getHuabanMeinvService()
                .getHuabanMeinvs(Constants.HUABAN_MEINV_NUMBER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observerHuabanMeinv);
    }
}
