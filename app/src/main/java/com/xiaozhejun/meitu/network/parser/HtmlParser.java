package com.xiaozhejun.meitu.network.parser;

import com.xiaozhejun.meitu.model.MeizituGallery;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by yangzhe on 16-8-2.
 */
public class HtmlParser {

    /**
     * 使用jsoup解析相册列表所在网页的html文档获取妹子图相册的详细信息
     * */
    public static ArrayList<MeizituGallery> parseMeizituGalleryListHtmlContent(String htmlContent){
        ArrayList<MeizituGallery> meizituGalleryList = new ArrayList<MeizituGallery>();
        Document document = Jsoup.parse(htmlContent);
        Elements galleryElements = document.select("ul#pins > li");
        for(Element galleryElement:galleryElements){
            // 获取html中各个相册属性所对应的元素
            MeizituGallery meizituGallery = new MeizituGallery();
            Element aElement = galleryElement.select("a").first();
            Element imgElement = galleryElement.select("img").first();
            Element timeElement = galleryElement.select("span.time").first();
            Element viewElement = galleryElement.select("span.view").first();
            // 将html中的元素转换为相册对应的属性值
            meizituGallery.setGalleryUrl(aElement.attr("href"));
            meizituGallery.setGroupId(aElement.attr("href"));
            meizituGallery.setTitle(imgElement.attr("alt"));
            meizituGallery.setPictureUrl(imgElement.attr("data-original"));
            meizituGallery.setTime(timeElement.text());
            meizituGallery.setViewTimes(viewElement.text());
            // 将相册信息添加到相册列表中
            meizituGalleryList.add(meizituGallery);
        }
        return meizituGalleryList;
    }

}
