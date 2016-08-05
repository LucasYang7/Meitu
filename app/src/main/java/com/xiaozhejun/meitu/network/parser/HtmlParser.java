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
     * 解析相册首页的网页内容，得到妹子图相册的网页页数信息
     * */
    public static ArrayList<Integer> parseFirstMeizituGalleryHtmlContent(String htmlContent){
        // 解析妹子图相册首页内容，得到该相册所占有的网页总数
        Document document = Jsoup.parse(htmlContent);
        String totalPageString = "1";
        int totalPages = 1;
        Logcat.showLog("totalPages","(1) totalPages = " + totalPages);
        Pattern pattern = Pattern.compile("\\d+");    //匹配整数的正则表达式
        Element pageNaviDiv = document.select("div.pagenavi").first();
        Elements spanElements = pageNaviDiv.select("span");
        for(Element spanElement:spanElements){
            Matcher matcher = pattern.matcher(spanElement.text());
            while(matcher.find()){
                totalPageString = matcher.group();
                int temp = Integer.parseInt(totalPageString);
                if(temp > totalPages){
                    totalPages = temp;
                }
            }
        }
        Logcat.showLog("totalPages","(2) totalPages = " + totalPages);
        // 使用ArrayList存储各个网页所对应的页数信息，便于执行后面的flatMap操作
        ArrayList<Integer> meizituGalleryPages = new ArrayList<Integer>();
        for(int i = 0; i< totalPages;i++){
            meizituGalleryPages.add(i+1);  //当page == 1时，会自动跳转到相册的首页，因此无需处理这种情况
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
            meituPicture.setTitle(pictureInfoElement.attr("alt"));
            meituPicture.setPictureUrl(pictureInfoElement.attr("src"));
            meizituPictureList.add(meituPicture);
        }
        return meizituPictureList;
    }

}
