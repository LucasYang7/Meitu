package com.xiaozhejun.meitu.network.api;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 妹子图Http所对应的Retrofit接口
 * 每调用一次该接口中的某个方法，就进行一次Http请求
 * Created by yangzhe on 16-7-28.
 */
public interface MeizituService {

    // 获取"首页"图片中第1页的网页信息
    @GET("/")
    Observable<ResponseBody> getPictureInHomePage();

    // 获取"首页"图片中第page页的网页信息
    @GET("page/{page}")
    Observable<ResponseBody> getPictureInHomePages(@Path("page") int page);

    // 获取type类图片中的第1页的网页信息
    @GET("{type}")
    Observable<ResponseBody> getPictureType(@Path("type") String pictureType);

    // 获取type类图片中的第page页的网页信息
    @GET("{type}/page/{page}")
    Observable<ResponseBody> getPictureTypeInPage(@Path("type") String pictureType,
                                                  @Path("page") int page);

    // 获取groupId所对应的相册中第一张图片所在的网页信息，该页包含了相册图片总数的信息
    @GET("{groupId}")
    Observable<ResponseBody> getPictureWithGroupId(@Path("groupId") String groupId);

    // 获取groupId所对应的相册中第page张图片所在的网页信息，该页包含了相册图片总数的信息
    @GET("{groupId}/{page}")
    Observable<ResponseBody> getPictureWithGroupIdInPage(@Path("groupId") String groupId,
                                                         @Path("page") int page);

    // 访问"妹子自拍"的首页信息
    @GET("share")
    Observable<ResponseBody> getPictureInSelfiePage();

    // 访问“妹子自拍”其余页面的信息
    @GET("share/comment-page-{page}#comments")
    Observable<ResponseBody> getPictureInSelfiePages(@Path("page") int page);

    // 搜索妹子图网站中的相册
    // @GET("search/{search}")
    // Observable<ResponseBody> getSearchResult(@Path("search") String search);

    // 搜索妹子图网站中的相册
    @GET("/")
    Observable<ResponseBody> getSearchResult(@Query("s") String search);

    // 展示妹子网站的搜索结果
    @GET("search/{search}/page/{page}")
    Observable<ResponseBody> getSearchResultInPages(@Path("search") String search,
                                                    @Path("page") int page);
}
