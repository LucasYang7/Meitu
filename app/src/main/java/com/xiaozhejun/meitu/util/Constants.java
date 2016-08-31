package com.xiaozhejun.meitu.util;

import com.xiaozhejun.meitu.R;

/**
 * Created by yangzhe on 16-7-26.
 */
public interface Constants {

    /**
     * 用于控制是否显示用于测试目的的Toast
     * */
    boolean SHOW_TEST_TOAST = false;

    /**
     * 用于控制是否显示自己定义的Log
     * */
    boolean SHOW_LOG = false;

    /**
     * 妹子图网站对应的妹子分类标题
     * */
    String[] MEIZITU_TITLES = {"每日更新","妹子自拍","妹子推荐","热门妹子","性感妹子","日本妹子","台湾妹子","清纯妹子"};

    /**
     * 妹子图网站对应的妹子分类
     * */
    String[] MEIZITU_TYPE = {"","share","best","hot","xinggan","japan","taiwan","mm"};

    /**
     * 妹子图网站的图片分类数目，每一个分类对应一个Fragment
     * */
    int MEIZITU_COUNT = MEIZITU_TITLES.length;

    /**
     * 妹子图网站的网址，通过查看responsebody中是否有这个网址来判断能否连接上妹子图的网站
     * */
    String MEIZITU_WEBSITE = "www.mzitu.com";

    /**
     * 豆瓣美女网站对应的妹子分类标题
     * */
    String[] DOUBAN_MEINV_TITLES = {"所有", "大胸妹", "小翘臀", "黑丝袜", "美腿控", "有颜值", "大杂烩"};

    /**
     * 豆瓣美女网站每类妹子对应的分类id
     * */
    int[] DOUBAN_MEINV_CID = {1,2,6,7,3,4,5};

    /**
     * 豆瓣美女网站的图片分类数目，每一个分类对应一个Fragment
     * */
    int DOUBAN_MEINV_COUNT = DOUBAN_MEINV_TITLES.length;

    /**
     * 豆瓣美女网站的网址，通过查看responsebody中是否有这个网址来判断能否连接上豆瓣美女的网站
     * */
    String DOUBAN_MEINV_WEBSITE = "www.dbmeinv.com";

    /**
     * 每请求一次Gank Api所返回的妹子图片数目
     * */
    int GANK_MEIZI_NUMBER = 10;

    /**
     * 每请求一次花瓣美女API所返回的妹子图片数目
     * */
    int HUABAN_MEINV_NUMBER = 15;

    /**
     * sharedPreferences所对应的名字
     * */
    String MEITU_PREFS_NAME = "MeituPrefsFile";

    /**
     * 引导页面的标题
     * */
    String[] INTRO_TITLES = {"欢迎来到妹图","搜妹子","看妹子","收妹子","藏妹子"};

    /**
     * 引导页颜色
     * */
    String[] INTRO_COLORS = {"#fb7299","#00bcd4","#bb8930","#4a82ae","#4caf50"};

    /**
     * 引导页介绍内容
     * */
    int[] INTRO_DESCRIPTIONS = {R.string.guide_view_introduce_1,R.string.guide_view_introduce_2
            ,R.string.guide_view_introduce_3,R.string.guide_view_introduce_4,R.string.guide_view_introduce_5};

    /**
     * 引导页的背景图片
     * */
    int[] INTRO_IMAGES = {R.drawable.guide_view_1,R.drawable.guide_view_2,
            R.drawable.guide_view_3, R.drawable.guide_view_4,R.drawable.guide_view_5};
}
