package com.xiaozhejun.meitu.model;

import android.net.Uri;

/**
 * 对应妹子图的相册信息
 * Created by yangzhe on 16-7-28.
 */
public class MeizituGallery {
    public String galleryUrl;      // 相册的URL地址 对应html文档中的href
    public String title;           // 相册的标题    对应html文档中的alt
    public String time;            // 相册发布的时间 对应html文档中的time
    public String viewTimes;       // 相册查看的次数 对应html文档中的alt
    public String pictureUrl;      // 相册显示照片的URL地址 对应html文档中的data-original
    public String groupId;         // 相册所对应的id
    public String referer;         // 标记图片的来源地址

    public void setGalleryUrl(String galleryUrl) {
        this.galleryUrl = galleryUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setViewTimes(String viewTimes) {
        this.viewTimes = viewTimes;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public void setGroupId(String galleryUrl){
        Uri uri = Uri.parse(galleryUrl);
        this.groupId = uri.getLastPathSegment();
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getGalleryUrl(){
        return this.galleryUrl;
    }

    public String getTitle(){
        return this.title;
    }

    public String getTime(){
        return this.time;
    }

    public String getViewTimes(){
        return this.viewTimes;
    }

    public String getPictureUrl(){
        return this.pictureUrl;
    }

    public String getGroupId(){
        return this.groupId;
    }

    public String getReferer() {
        return referer;
    }

    public String getObjectInformation(){
        String information = "";
        information = title + " " + galleryUrl + " " + groupId + " " +  time + " " + viewTimes + " "+ referer + "\n";
        return information;
    }
}
