package com.xiaozhejun.meitu.util;

/**
 * Created by yangzhe on 16-7-26.
 */
public interface Constants {

    /**
     * 用于控制是否显示Toast
     * */
    boolean SHOW_TOAST = true;

    /**
     * 用于控制是否显示自己定义的Log
     * */
    boolean SHOW_LOG = true;

    /**
     * 妹子图网站对应的妹子分类标题
     * */
    String[] MEIZITU_TITLES = {"每日更新","热门妹子","妹子推荐","性感妹子","日本妹子","台湾妹子","清纯妹子","妹子自拍"};

    /**
     * 妹子图网站对应的妹子分类
     * */
    String[] MEIZITU_TYPE = {"","hot","best","xinggan","japan","taiwan","mm","share"};

    /**
     * 妹子图网站的图片分类数目，每一个分类对应一个Fragment
     * */
    int MEIZITU_COUNT = MEIZITU_TITLES.length;

    /**
     * 豆瓣美女网站对应的妹子分类标题
     * */
    String[] DOUBAN_MEINV_TITLES = {"所有", "大胸妹", "小翘臀", "黑丝袜", "美腿控", "有颜值", "大杂烩"};
}
