package com.xiaozhejun.meitu.util;

/**
 * Created by yangzhe on 16-7-26.
 */
public interface Constants {

    /**
     * 用于控制是否显示用于测试目的的Toast
     * */
    boolean SHOW_TEST_TOAST = true;

    /**
     * 用于控制是否显示自己定义的Log
     * */
    boolean SHOW_LOG = true;

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
}
