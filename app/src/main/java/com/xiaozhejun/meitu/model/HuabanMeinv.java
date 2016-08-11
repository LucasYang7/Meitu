package com.xiaozhejun.meitu.model;

/**
 * 调用花瓣美女API返回的json中的一个字段
 * Created by yangzhe on 16-8-11.
 */
public class HuabanMeinv {
    public String pin_id;
    public String raw_text;
    public HBImageFile file;           // 对应图片信息的字段

    /**
     * 返回花瓣美女的url
     * */
    public String getImageUrl(){
        if(this.file.key.isEmpty() == false){
            return "http://img.hb.aicdn.com/" + this.file.key;
        }
        return "";
    }

    public static class HBImageFile{
        public int height;
        public int width;
        public String key;        // 图片对应的key，用于构建图片的url
    }
}
