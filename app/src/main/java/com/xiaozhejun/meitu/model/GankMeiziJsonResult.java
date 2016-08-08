package com.xiaozhejun.meitu.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 调用GankApi返回的Json结果
 * 该Json结果由两部分组成:error变量和results数组
 * Created by yangzhe on 16-8-8.
 */
public class GankMeiziJsonResult {
    public boolean error;
    public @SerializedName("results") List<GankMeizi> gankMeiziList;
}
