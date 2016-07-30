package com.xiaozhejun.meitu.network;

import com.xiaozhejun.meitu.network.api.MeizituService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 该类主要负责处理网络交互的事情
 * 例如初始化Retrofit的配置信息
 * Created by yangzhe on 16-7-28.
 */
public class Network {
    private static OkHttpClient okHttpClient = new OkHttpClient();  // okHttp客户端
    private static Converter.Factory gsonConverterFactory = GsonConverterFactory.create(); // gson转换器
    private static CallAdapter.Factory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create(); // RxJava适配器

    private static MeizituService meizituService;  //用于请求妹子图网站的Http所对应的Retrofit Service

    /**
     * 构建用于请求妹子图网站的Retrofit
     * */
    public static MeizituService getMeizituService(){
        if(meizituService == null){
            // 配置HttpLoggingInterceptor来查看Http的Log
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            // 构建带有HttpLoggingInterceptor的OkHttpClient
            OkHttpClient okHttpClientWithInterceptor = new OkHttpClient.Builder()
                    .addInterceptor(httpLoggingInterceptor)
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    //.client(okHttpClient)
                    .client(okHttpClientWithInterceptor)
                    .baseUrl("http://www.mzitu.com/")
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)  //告诉Retrofit使用RxJava来处理Http请求
                    .build();
            meizituService = retrofit.create(MeizituService.class);  // 调用create方法来实现MeizituService接口
        }
        return meizituService;
    }
}
