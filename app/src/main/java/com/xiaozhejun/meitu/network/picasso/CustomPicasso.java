package com.xiaozhejun.meitu.network.picasso;

import android.content.Context;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Created by yangzhe on 2017/8/6.
 */

public class CustomPicasso {
    // 通过自定义Picasso,往HTTP HEADER中添加Referer域来绕过妹子图网站的防盗链机制
    public static Picasso getCustomePicasso(Context context, final String referer) {
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
        Picasso picasso = new Picasso.Builder(context).downloader(new OkHttpDownloader(okHttpClient)).build();
        return picasso;
    }
}
