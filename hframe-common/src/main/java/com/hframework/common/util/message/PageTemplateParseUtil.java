package com.hframework.common.util.message;

import com.hframework.common.util.message.bean.DefaultTreeImpl;
import com.hframework.common.util.message.bean.PageContainer;
import com.hframework.common.util.RegexUtils;
import com.hframework.common.util.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * User: zhangqh6
 * Date: 2015/11/29 8:48:48
 */
public class PageTemplateParseUtil {

    private static final String[] TAG_STYLE = new String[]{"div","nav", "script","link","footer"};
    private static final String[] TAG_DATA = new String[]{"ul","ol","li","h1","h2","h3","h4","h5","a","form","input","p","table","span","img","i"};
    private static final String[] TAG_WEAKLY_DATA = new String[]{"button"};

    private static final String[] CLASS_GRID = new String[]{"row","row-fluid",/*"col-md-*",*/"col-md-","span",};
    private static final String[] CLASS_COMPONENT = new String[]{"navbar*","side*bar","brand","footer","home","footer"};


    public static boolean isStyleTag(String tagName) {
        return Arrays.asList(TAG_STYLE).contains(tagName);
//        return Arrays.binarySearch(TAG_STYLE, tagName.toLowerCase()) > -1 ? true : false;
    }

    public static boolean isDataTag(String tagName) {
        return Arrays.asList(TAG_DATA).contains(tagName);
//        return Arrays.binarySearch(TAG_DATA, tagName.toLowerCase()) > -1 ? true : false;
    }

    public static boolean isWeaklyDataTag(String tagName) {
        return Arrays.asList(TAG_WEAKLY_DATA).contains(tagName);
//        return Arrays.binarySearch(TAG_WEAKLY_DATA, tagName.toLowerCase()) > -1 ? true : false;
    }


    public static boolean isGridClass(Set<String> classNameSet) {
        if(classNameSet != null) {
            for (String className : classNameSet) {
                for (String classGrid : CLASS_GRID) {
                    if(className.contains(classGrid)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static int getGridNo(Set<String> classNameSet) {
        if(isGridClass(classNameSet)) {
            if(classNameSet != null) {
                for (String className : classNameSet) {
                    for (String classGrid : CLASS_GRID) {
                        if(className.contains(classGrid)) {
                            String[] strings = RegexUtils.find(className, "\\d+");
                            if(strings.length > 0) {
                                return Integer.valueOf(strings[0]);
                            }else {
                                System.out.println("getGridNo -->" + classGrid);
                                return 0;
                            }
                        }
                    }
                }
            }
        }

        return -1;
    }

    public static boolean isComponeElement(Set<String> classNameSet,  String id, String tagName) {
        if(classNameSet != null) {
            for (String className : classNameSet) {
                for (String classComponent : CLASS_COMPONENT) {
                    if(className.matches(".*" + classComponent.replaceAll("\\*",".*") + ".*")) {
                        return true;
                    }
                }
            }
        }

        if(StringUtils.isNotBlank(id)) {
            for (String classComponent : CLASS_COMPONENT) {
                if(id.matches(".*" + classComponent.replaceAll("\\*",".*") + ".*")) {
                    return true;
                }
            }
        }

        if(StringUtils.isNotBlank(tagName)) {
            for (String classComponent : CLASS_COMPONENT) {
                if(tagName.matches(".*" + classComponent.replaceAll("\\*",".*") + ".*")) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String getComponetType(Set<String> classNameSet,String id, String tagName) {
        if(classNameSet != null) {
            for (String className : classNameSet) {
                for (String classComponent : CLASS_COMPONENT) {
                    if(className.matches(".*" + classComponent.replaceAll("\\*",".*") + ".*")) {
                        return classComponent;
                    }
                }
            }
        }

        if(StringUtils.isNotBlank(id)) {
            for (String classComponent : CLASS_COMPONENT) {
                if(id.matches(".*" + classComponent.replaceAll("\\*",".*") + ".*")) {
                    return classComponent;
                }
            }
        }

        if(StringUtils.isNotBlank(tagName)) {
            for (String classComponent : CLASS_COMPONENT) {
                if(tagName.matches(".*" + classComponent.replaceAll("\\*",".*") + ".*")) {
                    return classComponent;
                }
            }
        }
        return null;
    }

    public static void setElementHfId(Document document) {
        Element body = document.body();
        setElementHfId(body, "1");
    }

    private static void setElementHfId(Element element, String id) {

        element.attr("hf-id", id);
        Elements elements = element.children();
        if(elements != null) {
            for (int i = 0; i < elements.size(); i++) {
                setElementHfId(elements.get(i), id +  (i+1));
            }
        }

    }

    public static void main(String[] args) throws IOException {
//        Document document = HtmlUtils.getDocumentFromFile(
//                "e:\\my-doc\\09 哆啦梦项目\\网页模板\\moban480\\index.html");
        Document document = HtmlUtils.getDocumentFromFile(
                "e:/my-doc/09 哆啦梦项目/网页模板/chahua3223-高/index.html");
        new PageContainer(document.body()).parse();

        setElementHfId(document);

//        System.out.println(document.outerHtml());
//        Elements elements = document.getElementsByAttribute("hframe-component");
//        if(!elements.isEmpty()) {
//            for (Element element : elements) {
//                System.out.println("hframe-component : " + element.attr("hframe-component") + "; hframe-dataset : " + element.attr("hframe-dataset") );
//            }
//        }
//
//        if(!elements.isEmpty()) {
//            for (Element element : elements) {
//                element.after("<hframe:tree id=\"12312\" name = \"ni hao \"></hframe>" );
//                element.empty();
//                element.remove();
////                element.wrap("<hframe:tree id=\"12312\" name = \"ni hao \"></hframe>" );
////                System.out.println(element.outerHtml());
//            }
//        }

        System.out.println(document.outerHtml());

        print(document);

        String treeXml = getTreeXml(document);
        System.out.println("------------------------------------------");

        System.out.println(treeXml);
    }

    public static String getTreeXml(Document document) {
        Element body = document.body();

        List<DefaultTreeImpl> list = new ArrayList<DefaultTreeImpl>();
        getTreeImpl(body, list,"-1",false);

        return DefaultTreeImpl.toXMLString(list);

    }

    public static String getElementSimpleXml(Element element) {
        Elements elements = element.children();
        String result = "";
        if(elements != null) {
            for (Element element1 : elements) {
                result += " " + element1.tagName() + "[" + element1.ownText() +"]";
                result += getElementSimpleXml(element1);
            }
        }

        return result;

    }

    private static void getTreeImpl(Element element, List<DefaultTreeImpl> list, String pId, boolean isComponent) {
        String text = element.tagName();
        if(StringUtils.isNotBlank(element.ownText())) {
            text += " : " + element.ownText();
        }
        String id = element.attr("hf-id");
        if(element.hasClass("hf-repeat")) {
            return ;
        }

        if(element.hasClass("hf-datatag")) {
            text += "=>" + element.attr("hf-count");
        }

        boolean isCurComponent = element.hasClass("hframe-component");

        if(!isComponent) {
            isComponent = isCurComponent;
        }
//        if(isComponent) {
//            text = ("" + text);
//        }

        if ("a".equals(element.tagName())) {
            text += getElementSimpleXml(element);
        }

        list.add(new DefaultTreeImpl(id, pId, id, text, !isComponent, isCurComponent, null, null, null, null, null));

        if ("a".equals(element.tagName())) {
            return;
        }

        Elements elements = element.children();
        if(elements != null) {
            for (int i = 0; i < elements.size(); i++) {
                getTreeImpl(elements.get(i), list, id, isComponent);
            }
        }

    }

    public static void print(Document document) {
        System.out.println("------------------------------------------");
        Element body = document.body();
//        System.out.println(" [ " + body.tagName() + " ] " + body.ownText());
        printSub(body, 1);
    }

    public static void printSub(Element element, int level) {
        String s = "";
        if(element.hasClass("hframe-component")) {
            s = "";
        }

        if(element.hasClass("hf-datatag")) {
            s = "=>" + element.attr("hf-count");
        }

        if(element.hasClass("hf-repeat")) {
            s = "N";
            return ;
        }

        System.out.println(getPadding(level)  + element.tagName() + " : " + element.ownText() + s);
        Elements elements = element.children();
        if(elements != null) {
            for (Element element1 : elements) {
                if("a".equals(element1.tagName())) {
                    System.out.print(getPadding(level + 1)  + element1.tagName() + " : " + element1.ownText());
                    printOnline(element1, (level + 1));
                    System.out.println();
                }else {
                    printSub(element1, (level+1));
                }
            }
        }


    }

    public static void printOnline(Element element, int level) {
        Elements elements = element.children();
        if(elements != null) {
            for (Element element1 : elements) {
                System.out.print(" " + element1.tagName() + "[" + element1.ownText() +"]");
                printOnline(element1, (level+1));
            }
        }

    }

    public static String getPadding(int level) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < level; i++) {
            sb.append("  ");
        }
        return sb.append("|-").toString();

    }

}
