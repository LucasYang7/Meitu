package com.xiaozhejun.meitu.model;

import java.util.ArrayList;

/**
 * 妹子图网站相册中某一页所对应的网页信息
 * Created by yangzhe on 2017/5/30.
 */

public class MeizituPicturePage {
    public int page;      // 标记该网页在相册中的页数
    public ArrayList<MeituPicture> meizituPictureList;   // 标记该网页中的妹子图片链接

    public void setPage(int page) {
        this.page = page;
    }

    public void setMeizituPictureList(ArrayList<MeituPicture> meizituPictureList) {
        this.meizituPictureList = meizituPictureList;
    }

    public int getPage() {
        return this.page;
    }

    public ArrayList<MeituPicture> getMeizituPictureList() {
        return this.meizituPictureList;
    }
}
