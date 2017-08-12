package com.xiaozhejun.meitu.network.parser;

import com.xiaozhejun.meitu.model.MeituPicture;
import com.xiaozhejun.meitu.model.MeizituGallery;
import com.xiaozhejun.meitu.model.MeizituPicturePage;
import com.xiaozhejun.meitu.util.Logcat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
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
     * 通过解析http请求返回的网页，判断是否能够访问到妹子图片所在的目标网站
     */
    public static boolean canConnectToServer(String htmlContent, String url) {
        boolean isConnected = htmlContent.contains(url);
        return isConnected;
    }

    /**
     * 使用jsoup解析某个妹子图相册的首页，获取到该类相册所拥有的总的网页页数信息
     */
    public static int parseFirstMeizituGalleryListHtmlContent(String htmlContent) {
        int totalPages = Integer.MAX_VALUE;
        Document document = Jsoup.parse(htmlContent);
        Element navLinksDivElement = document.select("div.nav-links").first();
        if (navLinksDivElement != null) {
            totalPages = 1;
            Pattern pattern = Pattern.compile("\\d+");    //匹配整数的正则表达式
            Elements pageNumbersAElements = navLinksDivElement.select("a.page-numbers");
            for (Element pageNumbersAElement : pageNumbersAElements) {
                Matcher matcher = pattern.matcher(pageNumbersAElement.text());
                while (matcher.find()) {
                    int temp = Integer.parseInt(matcher.group());
                    if (temp > totalPages) {
                        totalPages = temp;
                    }
                }
            }
        }
        return totalPages;
    }

    /**
     * 使用jsoup解析相册列表所在网页的html文档获取妹子图相册的详细信息
     */
    public static ArrayList<MeizituGallery> parseMeizituGalleryListHtmlContent(String htmlContent) {
        ArrayList<MeizituGallery> meizituGalleryList = new ArrayList<MeizituGallery>();
        Document document = Jsoup.parse(htmlContent);
        Element headElement = document.select("head").first();
        Element refererElement = headElement.select("link[rel=canonical]").first();
        String referer = refererElement.attr("href");
        Elements galleryElements = document.select("ul#pins > li");
        for (Element galleryElement : galleryElements) {
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
            meizituGallery.setReferer(referer);
            // 将相册信息添加到相册列表中
            meizituGalleryList.add(meizituGallery);
        }
        return meizituGalleryList;
    }

    /**
     * 解析相册首页的网页内容，得到妹子图相册的网页页数信息
     */
    public static ArrayList<Integer> parseFirstMeizituGalleryHtmlContent(String htmlContent) {
        // 解析妹子图相册首页内容，得到该相册所占有的网页总数
        Document document = Jsoup.parse(htmlContent);
        String totalPageString = "1";
        int totalPages = 1;
        Logcat.showLog("totalPages", "(1) totalPages = " + totalPages);
        Pattern pattern = Pattern.compile("\\d+");    //匹配整数的正则表达式
        Element pageNaviDiv = document.select("div.pagenavi").first();
        Elements spanElements = pageNaviDiv.select("span");
        for (Element spanElement : spanElements) {
            Matcher matcher = pattern.matcher(spanElement.text());
            while (matcher.find()) {
                totalPageString = matcher.group();
                int temp = Integer.parseInt(totalPageString);
                if (temp > totalPages) {
                    totalPages = temp;
                }
            }
        }
        Logcat.showLog("totalPages", "(2) totalPages = " + totalPages);
        // 使用ArrayList存储各个网页所对应的页数信息，便于执行后面的flatMap操作
        ArrayList<Integer> meizituGalleryPages = new ArrayList<Integer>();
        for (int i = 0; i < totalPages; i++) {
            meizituGalleryPages.add(i + 1);  //当page == 1时，会自动跳转到相册的首页，因此无需处理这种情况
        }
        return meizituGalleryPages;
    }

    /**
     * 解析显示妹子图照片的网页，得到该网页中所有显示的妹子图片的标题和图片链接等信息
     */
    public static MeizituPicturePage parseMeizituGalleryHtmlContent(String htmlContent) {
        MeizituPicturePage meizituPicturePage = new MeizituPicturePage();
        int page = 1;
        ArrayList<MeituPicture> meizituPictureList = new ArrayList<MeituPicture>();
        Document document = Jsoup.parse(htmlContent);
        // 获取名为description的meta节点中的内容，也就是妹子图相册中某张网页的描述信息
        Element descriptionMeta = document.select("meta[name=description]").first();
        String descriptionContent = descriptionMeta.attr("content");
        Logcat.showLog("parseMeizituGalleryHtmlContent", descriptionContent);
        // 通过正则表达式解析出网页的页数数字
        Pattern pattern = Pattern.compile("第.*页");    //匹配"第X页"的正则表达式
        Matcher matcher = pattern.matcher(descriptionContent);
        while (matcher.find()) {
            String pageString = matcher.group();
            Logcat.showLog("parseMeizituGalleryHtmlContent", "字符串 = " + pageString);
            Pattern pageNumberPattern = Pattern.compile("\\d+");
            Matcher pageNumberMatcher = pageNumberPattern.matcher(pageString);
            while (pageNumberMatcher.find()) {
                String pageNumberString = pageNumberMatcher.group();
                page = Integer.parseInt(pageNumberString);
                Logcat.showLog("parseMeizituGalleryHtmlContent", "数字 = " + page);
            }
        }
        // 获取妹子图相册中某张网页中所包含的图片链接信息，注意一张网页可能包含多张图片
        Element headElement = document.select("head").first();
        Element refererElement = headElement.select("link[rel=canonical]").first();
        String referer = refererElement.attr("href");
        Element MainImageDiv = document.select("div.main-image").first();
        Elements pictureInfoElements = MainImageDiv.select("img");
        for (Element pictureInfoElement : pictureInfoElements) {
            MeituPicture meituPicture = new MeituPicture();
            meituPicture.setTitle(pictureInfoElement.attr("alt"));
            meituPicture.setPictureUrl(pictureInfoElement.attr("src"));
            meituPicture.setReferer(referer);
            meizituPictureList.add(meituPicture);
        }
        meizituPicturePage.setPage(page);
        meizituPicturePage.setMeizituPictureList(meizituPictureList);
        return meizituPicturePage;
    }

    /**
     * 解析豆瓣美女网站的网页，得到该网页中的美女图片的信息
     */
    public static ArrayList<MeituPicture> parseDoubanMeinvHtmlContent(String htmlContent) {
        ArrayList<MeituPicture> doubanMeinvPictureList = new ArrayList<MeituPicture>();
        Document document = Jsoup.parse(htmlContent);
        Elements elements = document.select("div.img_single");
        for (Element element : elements) {
            Element elementImg = element.select("img.height_min").first();
            MeituPicture meituPicture = new MeituPicture();
            meituPicture.setTitle(elementImg.attr("title"));
            //meituPicture.setPictureUrl(elementImg.attr("src"));
            String pictureUrl = elementImg.attr("src");
            Logcat.showLog("pictureUrl", "1 " + pictureUrl);
            pictureUrl = pictureUrl.replace("bmiddle", "large"); // bmiddle -> large
            Logcat.showLog("pictureUrl", "2 " + pictureUrl);
            meituPicture.setPictureUrl(pictureUrl);
            doubanMeinvPictureList.add(meituPicture);
        }
        return doubanMeinvPictureList;
    }

    /**
     * 解析妹子图"美女自拍"页面，获取该类妹子图片所占有的网页数目
     */
    public static int parseFirstMeizituSelfieHtmlContent(String htmlContent) {
        int page = 0;
        Document document = Jsoup.parse(htmlContent);
        Element element = document.select("span.page-numbers.current").first();
        String pageText = element.text();
        page = Integer.parseInt(pageText);
        return page;
    }

    /**
     * 解析妹子图"美女自拍"其余页面的妹子图片信息
     */
    public static ArrayList<MeituPicture> parseMeizituSelfieHtmlContent(String htmlContent) {
        ArrayList<MeituPicture> meizituSelfiePictureList = new ArrayList<MeituPicture>();
        Document document = Jsoup.parse(htmlContent);
        Element mainContentDiv = document.select("div.main-content").first();
        Elements pictureElements = mainContentDiv.select("img");
        for (Element pictureElement : pictureElements) {
            MeituPicture meituPicture = new MeituPicture();
            meituPicture.setTitle(pictureElement.attr("alt"));
            meituPicture.setPictureUrl(pictureElement.attr("src"));
            meizituSelfiePictureList.add(meituPicture);
        }
        return meizituSelfiePictureList;
    }

}
