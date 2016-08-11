package com.xiaozhejun.meitu.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 调用花瓣美女API所得到的json结果
 * Created by yangzhe on 16-8-11.
 */
public class HuabanMeinvJsonResult {
    public @SerializedName("pins") List<HuabanMeinv> huabanMeinvList;
}
