package com.xiaozhejun.meitu.network.parser;

import com.xiaozhejun.meitu.model.MeituPicture;
import com.xiaozhejun.meitu.model.MeizituGallery;
import com.xiaozhejun.meitu.util.Logcat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * 解析相册首页网页的内容，得到妹子图相册的网页数目
     * */
    public static ArrayList<Integer> parseFirstMeizituGalleryHtmlContent(String htmlContent){
        ArrayList<Integer> meizituGalleryPages = new ArrayList<Integer>();
        Document document = Jsoup.parse(htmlContent);
        Element element = document.select("span").get(10);    // 这里逻辑不正确，要改!!!
        String totalPageString = element.text();
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(totalPageString);
        while(matcher.find()){
            totalPageString = matcher.group();
        }
        Logcat.showLog("totalPages",totalPageString);
        int totalPages = Integer.parseInt(totalPageString);
        for(int i = 0; i< totalPages;i++){
            meizituGalleryPages.add(i+1);    //当page == 1时，会自动跳转到相册的首页，因此无需处理这种情况
        }
        return meizituGalleryPages;
    }

    /**
     * 解析显示妹子图照片的网页，得到该网页中所有显示的妹子图片的标题和图片链接等信息
     * */
    public static ArrayList<MeituPicture> parseMeizituGalleryHtmlContent(String htmlContent){
        ArrayList<MeituPicture> meizituPictureList = new ArrayList<MeituPicture>();
        Document document = Jsoup.parse(htmlContent);
        Element MainImageDiv = document.select("div.main-image").first();
        Elements pictureInfoElements = MainImageDiv.select("img");
        for(Element pictureInfoElement:pictureInfoElements){
            MeituPicture meituPicture = new MeituPicture();
            meituPicture.setTitle(pictureInfoElement.attr("src"));
            meituPicture.setPictureUrl(pictureInfoElement.attr("alt"));
            meizituPictureList.add(meituPicture);
        }
        return meizituPictureList;
    }

}
