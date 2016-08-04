package com.xiaozhejun.meitu.model;

/**
 * 每张妹子图片所对应的信息
 * Created by yangzhe on 16-8-3.
 */
public class MeituPicture {

    public String title;
    public String pictureUrl;

    public void setTitle(String title){
        this.title = title;
    }

    public void setPictureUrl(String pictureUrl){
        this.pictureUrl = pictureUrl;
    }

    public String getTitle(){
        return title;
    }

    public String getPictureUrl(){
        return pictureUrl;
    }
}
