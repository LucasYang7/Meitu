package com.xiaozhejun.meitu.network.api;

import com.xiaozhejun.meitu.model.GankMeiziJsonResult;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * 调用GankMeizi API所对应的Retrofit接口
 * 每调用一次该接口中的某个方法，就进行一次Http请求
 * Created by yangzhe on 16-8-8.
 */
public interface GankMeiziService {
    @GET("data/福利/{number}/{page}")
    Observable<GankMeiziJsonResult> getGankMeizis(@Path("number") int number,@Path("page") int page);
}
