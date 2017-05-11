package com.hframework.common.util.message.bean;

import com.hframework.common.util.message.PageTemplateParseUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

/**
 * User: zhangqh6
 * Date: 2015/11/27 19:05:05
 */
public class PageContainer {

    private Document document;

    private Element body;

    //标题
    private Element title;

    //样式
    private Elements links;

    //meta
    private Elements metas;

    //列表元素
    private List<Element> ulElements;

    //超链接
    private List<Element> aElements;

    private Set<Element> dataElements;

    //样式域
    private Map<Element, ElementExt> elementInfoMap;

    private List<GridExt> gridList = null;

    public PageContainer() {
        ulElements = new ArrayList<Element>();
        aElements = new ArrayList<Element>();
        elementInfoMap = new HashMap<Element, ElementExt>();
        dataElements = new HashSet<Element>();
        gridList = new ArrayList<GridExt>();
    }

    public PageContainer(Element body) {
        ulElements = new ArrayList<Element>();
        aElements = new ArrayList<Element>();
        elementInfoMap = new HashMap<Element, ElementExt>();
        dataElements = new HashSet<Element>();
        gridList = new ArrayList<GridExt>();
        initBodyElement(body);
    }

    //初始化body元素
    public void initBodyElement(Element body) {
        this.body = body;
        Elements allElements = body.getAllElements();
        if(!allElements.isEmpty()) {
            for (Element allElement : allElements) {
                elementInfoMap.put(allElement,new ElementExt());
            }
        }
    }

    public void parse() {
        parseType();//解析类型
        formatDataType();
        initBodyElement(body);
        parseOwner();//解析归属
        removePageOwner(body);


    }

    private void formatDataType() {
        for (Element element : getDataElements()) {
            element.addClass("hframe-component");
            distinctSubElement(element);
//            System.out.println(element.outerHtml());
//            element.after("XXXX" );
//            element.empty();
//            element.remove();
//            System.out.println("======================");
        }
    }

    private void distinctSubElement(Element element) {
        if("ul".equals(element.tagName())) {
            Elements children = element.children();
            Map<String,Element> keyElement = new HashMap<String, Element>();
            for (Element child : children) {
                StringBuffer sb = new StringBuffer();
                getElementTags(child, sb);
                String keyStr = sb.toString();
                if(!keyElement.containsKey(keyStr)) {
                    keyElement.put(keyStr,child);
                    child.addClass("hf-datatag");
                    child.attr("hf-count","1");
                }else {
                    child.addClass("hf-repeat");
                    Element element1 = keyElement.get(keyStr);
                    element1.attr("hf-count",String.valueOf(Integer.valueOf(element1.attr("hf-count")) + 1));
                }
            }

        }

    }

    private void getElementTags(Element element, StringBuffer sb) {
        distinctSubElement(element);

        sb.append(element.tagName());

        Elements children = element.children();
        if(!children.isEmpty()) {
            sb.append("[");
            for (Element child : children) {
                getElementTags(child,sb);
            }
            sb.append("]");
        }
    }

    private void removePageOwner(Element parentElement) {
        ElementExt elementExt = elementInfoMap.get(parentElement);
        if(parentElement != body) {
            if(!elementExt.isFrameElement()) {
                if(PageTemplateParseUtil.isGridClass(parentElement.classNames())) {
                    parentElement.html("TODO");
                    parentElement.attr("style","height:600px;");
//                    parentElement.remove();
                } else {
                    parentElement.empty();
                    parentElement.html("");
                    parentElement.remove();
                    return;
                }
            }
        }

        Elements children = parentElement.children();
        if(children.size() > 0) {
            for (Element child : children) {
                removePageOwner(child);
            }
        }

//        for (Map.Entry<Element, ElementExt> elementElementExtEntry : elementInfoMap.entrySet()) {
//            Element element = elementElementExtEntry.getKey();
//            ElementExt elementExt = elementElementExtEntry.getValue();
//            if(!elementExt.isFrameElement()) {
//                if(element.parent() == null) {
//                    continue;
//                }
//                if(PageTemplateParseUtil.isGridClass(element.classNames())) {
//                    element.html("TODO");
//                    element.remove();
//                } else {
//                    element.empty();
//                    element.html("");
//                    element.remove();
//                }
//            }
//        }
    }

    private void parseOwner() {
        parseGridClassElement(body, null);
        parseOwnerElement(body);
    }

    private void parseType() {
        parseTypeElement(body);
    }

    private void setWholeElementTreeType(Element element, int elementType) {
        if(elementType == ElementExt.ELEMENT_EXT_TYPE_DATA) {
            elementInfoMap.get(element).setElementDataType();
            for (Element child : element.getAllElements()) {
                elementInfoMap.get(child).setElementDataType();
            }
        }else {
            elementInfoMap.get(element).setElementStyleType();
            for (Element child : element.getAllElements()) {
                elementInfoMap.get(child).setElementStyleType();
            }
        }

    }

    private void setWholeElementTreeFrameOwner(Element element, int elementOwner) {
        if(elementOwner == ElementExt.ELEMENT_EXT_OWNER_FRAME) {
            elementInfoMap.get(element).setElementFrameOwner();
            for (Element child : element.getAllElements()) {
                elementInfoMap.get(child).setElementFrameOwner();
            }
        }else {
            elementInfoMap.get(element).setElementPageOwner();
            for (Element child : element.getAllElements()) {
                elementInfoMap.get(child).setElementPageOwner();
            }
        }
    }

    private void setParentElementFrameOwner(Element element, int elementOwner) {
        if(element == body) {
            return ;
        }
        if(elementOwner == ElementExt.ELEMENT_EXT_OWNER_FRAME) {
            elementInfoMap.get(element).setElementFrameOwner();
        }else {
            elementInfoMap.get(element).setElementPageOwner();
        }
        setParentElementFrameOwner(element.parent(),elementOwner);
    }

    private void parseTypeElement(Element firstElement) {
        Elements children = firstElement.children();
        if(!children.isEmpty()) {
            for (Element child : children) {
                String tagName = child.tagName();
                if(PageTemplateParseUtil.isStyleTag(tagName)) {
                    elementInfoMap.get(child).setElementStyleType();
                    parseTypeElement(child);
                }else if(PageTemplateParseUtil.isWeaklyDataTag(tagName)) {
                    elementInfoMap.get(child).setElementStyleType();
                    parseTypeElement(child);
                }else if(PageTemplateParseUtil.isDataTag(tagName)) {
                    elementInfoMap.get(child).setElementDataType();
                    dataElements.add(child);
                    setWholeElementTreeType(child, ElementExt.ELEMENT_EXT_TYPE_DATA);
                    //TODO 样式分解
                }else {
                    System.out.println("未知标签-->" + child.outerHtml());
//                    new Exception("未知标签-->" + tagName);
                }
            }
        }
    }

    private void parseGridClassElement(Element parentElement,GridExt parentGridExt) {

        Elements children = parentElement.children();
        if(children.size() > 0) {
            for (Element child : children) {
                Set<String> classNameSet = child.classNames();
                GridExt childGridExt = parentGridExt;
                if(PageTemplateParseUtil.isGridClass(classNameSet)) {
                    childGridExt = pushGridList(classNameSet, child, parentGridExt);
                }
                parseGridClassElement(child, childGridExt);
            }
        }
    }

    private void parseOwnerElement(Element parentElement) {

        Elements children = parentElement.children();
        if(children.isEmpty()) {
            return;
        }

        for (Element child : children) {
            Set<String> classNameSet = child.classNames();
            String id = child.id();
            String tagName = child.tagName();

            if(PageTemplateParseUtil.isComponeElement(classNameSet, id,tagName)){
                String componetType = PageTemplateParseUtil.getComponetType(classNameSet,id,tagName);
                Element element = signGridElementExt(child, componetType);
                if(element == null) {
                    element = child;
                }
//                setWholeElementTreeFrameOwner(child, ElementExt.ELEMENT_EXT_OWNER_FRAME);
                setWholeElementTreeFrameOwner(element, ElementExt.ELEMENT_EXT_OWNER_FRAME);
                setParentElementFrameOwner(element, ElementExt.ELEMENT_EXT_OWNER_FRAME);
            }

            parseOwnerElement(child);
        }
    }

    private Element signGridElementExt(Element searchElement, String componetType) {

        GridExt gridExt = findGridExtBySubElement(searchElement,this.gridList);
        if(gridExt == null && searchElement.parent() != body) {
            return signGridElementExt(searchElement.parent(), componetType);
        }else {
            if(gridExt != null) {
                gridExt.setIsFrameGrid(true);
                if(componetType.equals("navbar*")) {
                    gridExt.setHasSlideBar(true);
                }else if(componetType.equals("side*bar")) {
                    gridExt.setHasTopBar(true);
                }
                return gridExt.getElement();
            }
            return null;
        }
     }

    private GridExt findGridExtBySubElement(Element searchElement, List<GridExt> gridList) {
        if(gridList == null) {
            return null;
        }
        for (GridExt gridExt : gridList) {
            if(gridExt.getElement() == searchElement) {
                return gridExt;
            }
            return findGridExtBySubElement(searchElement,gridExt.getSubGrid());
        }



        return null;
    }

    private GridExt pushGridList(Set<String> classNameSet, Element child, GridExt parentGridExt) {

        int gridNo = PageTemplateParseUtil.getGridNo(classNameSet);
        GridExt gridExt = new GridExt(gridNo, child);
        if(parentGridExt == null) {
            gridList.add(gridExt);
        }else {
            parentGridExt.addSubGrid(gridExt);
        }
        return gridExt;
    }

    class GridExt {
        private int width;
        private List<GridExt> subGrid;
        private Element element;
        private boolean hasTopBar;
        private boolean hasSlideBar;
        private boolean isFrameGrid;



        public GridExt(int width, Element element) {
            this.width = width;
            this.element = element;
        }

        public void addSubGrid(GridExt gridExt) {
            if(subGrid == null) {
                subGrid = new ArrayList<GridExt>();
            }
            subGrid.add(gridExt);
        }

        public boolean isFrameGrid() {
            return isFrameGrid;
        }

        public void setIsFrameGrid(boolean isFrameGrid) {
            this.isFrameGrid = isFrameGrid;
        }

        public Element getElement() {
            return element;
        }

        public void setElement(Element element) {
            this.element = element;
        }

        public boolean isHasTopBar() {
            return hasTopBar;
        }

        public void setHasTopBar(boolean hasTopBar) {
            this.hasTopBar = hasTopBar;
        }

        public boolean isHasSlideBar() {
            return hasSlideBar;
        }

        public void setHasSlideBar(boolean hasSlideBar) {
            this.hasSlideBar = hasSlideBar;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public List<GridExt> getSubGrid() {
            return subGrid;
        }

        public void setSubGrid(List<GridExt> subGrid) {
            this.subGrid = subGrid;
        }
    }

    class ElementExt{
        public static final int ELEMENT_EXT_TYPE_DATA = 1;
        public static final int ELEMENT_EXT_TYPE_STYLE = 2;
        public static final int ELEMENT_EXT_OWNER_FRAME = 1;
        public static final int ELEMENT_EXT_OWENR_PAGE = 2;

        //元素类型
        private int type ;
        //元素归属
        private int owner;

//        private boolean isGrid;

        public void setElementDataType() {
            type = ELEMENT_EXT_TYPE_DATA;
        }

        public void setElementStyleType() {
            type = ELEMENT_EXT_TYPE_STYLE;
        }

        public void setElementFrameOwner() {
            owner = ELEMENT_EXT_OWNER_FRAME;
        }

        public void setElementPageOwner() {
            owner = ELEMENT_EXT_OWENR_PAGE;
        }

        public boolean hasOwner() {
            return  owner > 0 ? true : false;
        }

        public boolean isDataElement() {
            if(type == ELEMENT_EXT_TYPE_DATA) {
                return true;
            }
            return false;
        }
        public boolean isStyleElement() {
            if(type == ELEMENT_EXT_TYPE_STYLE) {
                return true;
            }
            return false;
        }
        public boolean isFrameElement() {
            if(owner == ELEMENT_EXT_OWNER_FRAME) {
                return true;
            }
            return false;
        }
        public boolean isPageElement() {
            if(owner == ELEMENT_EXT_OWENR_PAGE) {
                return true;
            }
            return false;
        }

//        public boolean isGrid() {
//            return isGrid;
//        }
//
//        public void setIsGrid(boolean isGrid) {
//            this.isGrid = isGrid;
//        }
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Element getTitle() {
        return title;
    }

    public void setTitle(Element title) {
        this.title = title;
    }

    public Elements getLinks() {
        return links;
    }

    public void setLinks(Elements links) {
        this.links = links;
    }

    public Elements getMetas() {
        return metas;
    }

    public void setMetas(Elements metas) {
        this.metas = metas;
    }

    public List<Element> getUlElements() {
        return ulElements;
    }

    public void setUlElements(List<Element> ulElements) {
        this.ulElements = ulElements;
    }

    public List<Element> getaElements() {
        return aElements;
    }

    public void setaElements(List<Element> aElements) {
        this.aElements = aElements;
    }

    public Map<Element, ElementExt> getElementInfoMap() {
        return elementInfoMap;
    }

    public void setElementInfoMap(Map<Element, ElementExt> elementInfoMap) {
        this.elementInfoMap = elementInfoMap;
    }

    public Element getBody() {
        return body;
    }

    public void setBody(Element body) {
        this.body = body;
    }

    public Set<Element> getDataElements() {
        return dataElements;
    }

    public void setDataElements(Set<Element> dataElements) {
        this.dataElements = dataElements;
    }
}
