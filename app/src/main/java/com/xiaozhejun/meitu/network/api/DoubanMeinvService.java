package com.xiaozhejun.meitu.network.api;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 豆瓣美女Http操作所对应的接口
 * 每调用一次该接口中的方法就相当于进行了一次Http请求操作
 * Created by yangzhe on 16-8-9.
 */
public interface DoubanMeinvService {

    // 获取"所有"美女的信息
    @GET("dbgroup/show.htm")
    Observable<ResponseBody> getAllBeauties(@Query("pager_offset") int pager_offset);

    // 根据cid获取各个分类美女的信息，例如：大胸妹，小翘臀...
    @GET("dbgroup/show.htm")
    Observable<ResponseBody> getBeautiesByCid(@Query("cid") int cid,@Query("pager_offset") int pager_offset);
}
