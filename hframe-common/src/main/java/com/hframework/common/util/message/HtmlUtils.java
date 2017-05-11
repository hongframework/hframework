package com.hframework.common.util.message;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

/**
 * User: zhangqh6
 * Date: 2015/11/24 22:14:14
 */
public class HtmlUtils {


    /**
     * 解析HTML（来源字符串）
     * @param html
     * @return
     */
    public static Document getDocumentFromHtml(String html){
        return Jsoup.parse(html);
    }

    /**
     * 解析HTML（来源URL）
     * @param url
     * @return
     */
    public static Document getDocumentFromUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url)
                .data("query", "Java")
                .userAgent("Mozilla")
                .cookie("auth", "token")
                .timeout(5000)
                .post();
        return doc;
    }

    /**
     * 解析HTML（来源文件）
     * @param filePath
     * @return
     * @throws IOException
     */
    public static Document getDocumentFromFile(String filePath) throws IOException {
        return getDocumentFromFile(filePath,"");
    }

    /**
     * 解析HTML（来源文件）
     * @param filePath 文件路径
     * @param baseUri 基本地址
     * @return
     * @throws IOException
     */
    public static Document getDocumentFromFile(String filePath , String baseUri) throws IOException {
//        File file = new File("/tmp/input.html");
        File file = new File(filePath);
        Document doc = Jsoup.parse(file, "UTF-8", baseUri);
        return doc;
    }

    public static void main(String[] args) throws IOException {
        Document document = getDocumentFromFile(
                "E:\\myworkspace\\hframe-trunk\\hframe-manager\\src\\main\\webapp\\bootcss\\demo1.jsp");
//        System.out.println(document.outerHtml());
        Elements elements = document.getElementsByAttribute("hframe-component");
        if(!elements.isEmpty()) {
            for (Element element : elements) {
                System.out.println("hframe-component : " + element.attr("hframe-component") + "; hframe-dataset : " + element.attr("hframe-dataset") );
            }
        }

        if(!elements.isEmpty()) {
            for (Element element : elements) {
                element.after("<hframe:tree id=\"12312\" name = \"ni hao \"></hframe>" );
                element.empty();
                element.remove();
//                element.wrap("<hframe:tree id=\"12312\" name = \"ni hao \"></hframe>" );
//                System.out.println(element.outerHtml());
            }
        }

        System.out.println(document.outerHtml());
    }


}
