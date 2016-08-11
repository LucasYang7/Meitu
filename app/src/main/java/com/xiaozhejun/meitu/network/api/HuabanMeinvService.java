package com.xiaozhejun.meitu.network.api;

import com.xiaozhejun.meitu.model.HuabanMeinvJsonResult;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by yangzhe on 16-8-11.
 */
public interface HuabanMeinvService {
    @GET("favorite/beauty")
    Observable<HuabanMeinvJsonResult> getHuabanMeinvs(@Query("limit") int limit,@Query("max") String max);

    @GET("favorite/beauty")
    Observable<HuabanMeinvJsonResult> getHuabanMeinvs(@Query("limit") int limit);
}
