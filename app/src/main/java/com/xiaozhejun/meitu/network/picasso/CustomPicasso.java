package com.xiaozhejun.meitu.network.picasso;

import android.content.Context;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.xiaozhejun.meitu.util.Logcat;

import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * Created by yangzhe on 2017/8/6.
 */

public class CustomPicasso {
    private static final int MAX_CACHE_SIZE = 100;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static LinkedHashMap<String, Picasso> mPicassoLinkedHashMap;

    // 通过自定义Picasso,往HTTP HEADER中添加Referer域来绕过妹子图网站的防盗链机制
    public static Picasso getCustomePicasso(Context context, final String referer) {
        Picasso picasso;
        // 采用LRU算法来缓存Picasso对象，从而避免创建重复的Picasso对象造成OutOfMemoryError
        if (mPicassoLinkedHashMap == null) {
            //根据cacheSize和加载因子计算hashmap的capactiy，+1确保当达到cacheSize上限时不会触发hashmap的扩容
            int capacity = (int) Math.ceil(MAX_CACHE_SIZE / DEFAULT_LOAD_FACTOR) + 1;
            mPicassoLinkedHashMap = new LinkedHashMap(capacity, DEFAULT_LOAD_FACTOR, true) {
                @Override
                protected boolean removeEldestEntry(Entry eldest) {
                    return size() > MAX_CACHE_SIZE;
                }
            };
        }
        if (mPicassoLinkedHashMap.get(referer) == null) {
            Logcat.showLog("referer", referer);
            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request.Builder request = chain.request().newBuilder();
                    request.addHeader("Referer", referer);
                    return chain.proceed(request.build());
                }
            });
            // OkHttpDownloader只存在于OKHTTP2.x版本中
            picasso = new Picasso.Builder(context).downloader(new OkHttpDownloader(okHttpClient)).build();
            mPicassoLinkedHashMap.put(referer, picasso);
        } else {
            picasso = mPicassoLinkedHashMap.get(referer);
        }
        return picasso;
    }

}
