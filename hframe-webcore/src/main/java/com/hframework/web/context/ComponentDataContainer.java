package com.hframework.web.context;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hframework.base.bean.MapWrapper;
import com.hframework.beans.controller.Pagination;
import com.hframework.common.util.*;
import com.hframework.web.config.bean.Component;
import com.hframework.web.config.bean.DataSet;
import com.hframework.web.config.bean.component.*;
import com.hframework.web.config.bean.dataset.Enum;
import com.hframework.web.config.bean.dataset.Field;
import com.hframework.web.config.bean.dataset.Rel;
import com.hframework.web.config.bean.mapper.Mapping;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * User: zhangqh6
 * Date: 2016/5/26 23:40:40
 */
public class ComponentDataContainer {

    private static final List<String> iconPool = Lists.newArrayList("icon-adjust",
            "icon-asterisk",
            "icon-ban-circle",
            "icon-bar-chart",
            "icon-barcode",
            "icon-beaker",
            "icon-beer",
            "icon-bell",
            "icon-bell-alt",
            "icon-bolt",
            "icon-book",
            "icon-bookmark",
            "icon-bookmark-empty",
            "icon-briefcase",
            "icon-bullhorn",
            "icon-calendar",
            "icon-camera",
            "icon-camera-retro",
            "icon-certificate",
            "icon-check",
            "icon-check-empty",
            "icon-circle",
            "icon-circle-blank",
            "icon-cloud",
            "icon-cloud-download",
            "icon-cloud-upload",
            "icon-coffee",
            "icon-cog",
            "icon-cogs",
            "icon-comment",
            "icon-comment-alt",
            "icon-comments",
            "icon-comments-alt",
            "icon-credit-card",
            "icon-dashboard",
            "icon-desktop",
            "icon-download",
            "icon-download-alt",
            "icon-edit",
            "icon-envelope",
            "icon-envelope-alt",
            "icon-exchange",
            "icon-exclamation-sign",
            "icon-external-link",
            "icon-eye-close",
            "icon-eye-open",
            "icon-facetime-video",
            "icon-fighter-jet",
            "icon-film",
            "icon-filter",
            "icon-fire",
            "icon-flag",
            "icon-folder-close",
            "icon-folder-open",
            "icon-folder-close-alt",
            "icon-folder-open-alt",
            "icon-food",
            "icon-gift",
            "icon-glass",
            "icon-globe",
            "icon-group",
            "icon-hdd",
            "icon-headphones",
            "icon-heart",
            "icon-heart-empty",
            "icon-home",
            "icon-inbox",
            "icon-info-sign",
            "icon-key",
            "icon-leaf",
            "icon-laptop",
            "icon-legal",
            "icon-lemon",
            "icon-lightbulb",
            "icon-lock",
            "icon-unlock",
            "icon-magic",
            "icon-magnet",
            "icon-map-marker",
            "icon-minus",
            "icon-minus-sign",
            "icon-mobile-phone",
            "icon-money",
            "icon-move",
            "icon-music",
            "icon-off",
            "icon-ok",
            "icon-ok-circle",
            "icon-ok-sign",
            "icon-pencil",
            "icon-picture",
            "icon-plane",
            "icon-plus",
            "icon-plus-sign",
            "icon-print",
            "icon-pushpin",
            "icon-qrcode",
            "icon-question-sign",
            "icon-quote-left",
            "icon-quote-right",
            "icon-random",
            "icon-refresh",
            "icon-remove",
            "icon-remove-circle",
            "icon-remove-sign",
            "icon-reorder",
            "icon-reply",
            "icon-resize-horizontal",
            "icon-resize-vertical",
            "icon-retweet",
            "icon-road",
            "icon-rss",
            "icon-screenshot",
            "icon-search",
            "icon-share",
            "icon-share-alt",
            "icon-shopping-cart",
            "icon-signal",
            "icon-signin",
            "icon-signout",
            "icon-sitemap",
            "icon-sort",
            "icon-sort-down",
            "icon-sort-up",
            "icon-spinner",
            "icon-star",
            "icon-star-empty",
            "icon-star-half",
            "icon-tablet",
            "icon-tag",
            "icon-tags",
            "icon-tasks",
            "icon-thumbs-down",
            "icon-thumbs-up",
            "icon-time",
            "icon-tint",
            "icon-trash",
            "icon-trophy",
            "icon-truck",
            "icon-umbrella",
            "icon-upload",
            "icon-upload-alt",
            "icon-user",
            "icon-user-md",
            "icon-volume-off",
            "icon-volume-down",
            "icon-volume-up",
            "icon-warning-sign",
            "icon-wrench",
            "icon-zoom-in",
            "icon-zoom-out");

    private static final Logger logger = LoggerFactory.getLogger(ComponentDataContainer.class);

    private Map<String ,JsonSegmentParser> elements = new HashMap<String, JsonSegmentParser>();

    private List<Event> allEvent = new ArrayList<Event>();
    //每一行开始(如：添加复选框)
    private List<EventElement> beforeOfRowList = new ArrayList<EventElement>();
    //每一行结尾(如：添加操作图标)
    private List<EventElement> endOfRowList = new ArrayList<EventElement>();
    //组件结尾(如：提交按钮)
    private List<EventElement> beforeOfCompList = new ArrayList<EventElement>();
    //组件结尾(如：提交按钮)
    private List<EventElement> endOfCompList = new ArrayList<EventElement>();
    //每一行某一列（如：点击名称进行超链接）
    private List<EventElement> elementOfCompList = new ArrayList<EventElement>();
    //每一行某一列（如：点击名称进行超链接）
    private Map<String, EventElement> elementOfRowMap = new HashMap<String, EventElement>();

    private Map<String, JsonSegmentParser> runtimeDataMap = new HashMap<String, JsonSegmentParser>();
    private String json;
    private JSONObject jsonObject;

    public ComponentDataContainer(Component component, com.hframework.web.config.bean.pagetemplates.Element pageElementDesc, List<Event> eventList, Map<String, Event> eventStore, String pageComponentEventExtend) {
        if(component == null) {
            return;
        }
        for (Element element : component.getElementList()) {
            if(EnumUtils.compare(ComponentElementType.characters, element.getType())) {
                elements.put(element.getId(), new CharacterJsonSegmentParser(element,component.getType()));
            }else if(EnumUtils.compare(ComponentElementType.enums, element.getType())) {
                elements.put(element.getId(), new EnumJsonSegmentParser(element, component.getType()));
            }else if(EnumUtils.compare(ComponentElementType.object, element.getType())) {
                elements.put(element.getId(), new ObjectJsonSegmentParser(element, component.getType()));
            }else if(EnumUtils.compare(ComponentElementType.objects, element.getType())) {
                elements.put(element.getId(), new ObjectsJsonSegmentParser(element,component.getType()));
            }else if(EnumUtils.compare(ComponentElementType.objectTree, element.getType())) {
                elements.put(element.getId(), new ObjectTreeJsonSegmentParser(this,element,component.getType()));
            }else if(EnumUtils.compare(ComponentElementType.array, element.getType())) {
                elements.put(element.getId(), new ArrayJsonSegmentParser(element, component.getType()));
            }else if(EnumUtils.compare(ComponentElementType.array2, element.getType())) {
                elements.put(element.getId(), new Array2JsonSegmentParser(element,component.getType()));
            }
        }

        allEvent = new ArrayList<Event>();
        if(!"absolute_false".equals(pageElementDesc.getEventExtend()) &&!"absolute_false".equals(pageComponentEventExtend)){
            allEvent.addAll(component.getBaseEvents().getEventList());
        }

        //组件自身事件
        if(!"false".equals(pageElementDesc.getEventExtend()) &&!"false".equals(pageComponentEventExtend)
                && !"absolute_false".equals(pageElementDesc.getEventExtend()) &&!"absolute_false".equals(pageComponentEventExtend)) {
            allEvent.addAll(component.getEvents().getEventList());
        }

        //页面模板中组件的事件
        if(!"false".equals(pageComponentEventExtend) && pageElementDesc.getEvents() != null) {
            allEvent.addAll(pageElementDesc.getEvents().getEventList());
        }

        //页面中所带事件
        if(eventList != null && eventList.size() > 0) {
            allEvent.addAll(eventList);
        }

        for (Event event : allEvent) {
            if(StringUtils.isNotBlank(event.getRel())) {
                String rel = event.getRel();
                Event event1 = eventStore.get(rel);
                if(event1 == null) {
                    logger.warn("event rel error : [rel = " + rel + " ]");
                    continue;
                }
                setEventDefaultDefinition(event , event1);
            }
            if(EnumUtils.compare(ComponentEventAnchor.BOFR, event.getAttach().getAnchor())) {
                beforeOfRowList.add(new EventElement(event));
            }else if(EnumUtils.compare(ComponentEventAnchor.EOFR, event.getAttach().getAnchor())) {
                endOfRowList.add(new EventElement(event));
            }else if(EnumUtils.compare(ComponentEventAnchor.EOFC, event.getAttach().getAnchor())) {
                endOfCompList.add(new EventElement(event));
            }else if(EnumUtils.compare(ComponentEventAnchor.BOFC, event.getAttach().getAnchor())) {
                beforeOfCompList.add(new EventElement(event));
            }else {
                elementOfCompList.add(new EventElement(event));
            }

            AppendElement appendElement = event.getSource().getAppendElement();
            if(appendElement != null && appendElement.getType() !=null) {
                if(EnumUtils.compare(ComponentEventAnchor.BOFR, event.getSource().getScope())) {
                    beforeOfRowList.add(new EventElement(appendElement));
                }else if(EnumUtils.compare(ComponentEventAnchor.EOFR, event.getSource().getScope())) {
                    endOfRowList.add(new EventElement(appendElement));
                }else if(EnumUtils.compare(ComponentEventAnchor.EOFC, event.getSource().getScope())) {
                    endOfCompList.add(new EventElement(appendElement));
                }else if(EnumUtils.compare(ComponentEventAnchor.BOFC, event.getSource().getScope())) {
                    beforeOfCompList.add(new EventElement(appendElement));
                }else {
                    elementOfCompList.add(new EventElement(appendElement));
                }
            }
        }
    }

    public static void setEventDefaultDefinition(Event targetEvent, Event storeEvent) {
        if(targetEvent == null || storeEvent == null) {
            return ;
        }


        if(targetEvent.getAttach() == null && storeEvent.getAttach() != null) {
            targetEvent.setAttach(storeEvent.getAttach());
        }

        if(targetEvent.getSourceOrigin() == null && storeEvent.getSourceOrigin() != null) {
            targetEvent.setSource(storeEvent.getSourceOrigin());
        }

        if(targetEvent.getEffectList() == null && storeEvent.getEffectList() != null) {
            targetEvent.setEffectList(storeEvent.getEffectList());
        }

        if(targetEvent.getPreHandleList() == null && storeEvent.getPreHandleList() != null) {
            targetEvent.setPreHandleList(storeEvent.getPreHandleList());
        }

        if(targetEvent.getId() == null && storeEvent.getId() != null) {
            targetEvent.setId(storeEvent.getId());
        }

        if(targetEvent.getType() == null && storeEvent.getType() != null) {
            targetEvent.setType(storeEvent.getType());
        }
        if(targetEvent.getName() == null && storeEvent.getName() != null) {
            targetEvent.setName(storeEvent.getName());
        }

        if(targetEvent.getDescription() == null && storeEvent.getDescription() != null) {
            targetEvent.setDescription(storeEvent.getDescription());
        }
    }

    public void addMappingAndDataSetDescriptor(Mapping mapping, ComponentDescriptor componentDescriptor, DataSetDescriptor dataSetDescriptor, boolean isBaseElement) {
        if(isBaseElement) {
            if(elements.containsKey(mapping.getId())) {
                elements.get(mapping.getId()).setDataSetDescriptorAndMapping(dataSetDescriptor, mapping);
            }
        }else {
            peddingEventElement(beforeOfRowList, mapping, dataSetDescriptor,componentDescriptor);
            peddingEventElement(endOfRowList, mapping, dataSetDescriptor,componentDescriptor);
            peddingEventElement(endOfCompList, mapping, dataSetDescriptor,componentDescriptor);
            peddingEventElement(beforeOfCompList, mapping, dataSetDescriptor,componentDescriptor);
            peddingEventElement(elementOfCompList, mapping, dataSetDescriptor, componentDescriptor);
        }
    }

    public void setElementOfRowMap(){
        for (EventElement eventElement : elementOfCompList) {
            if(eventElement.getAnchorName().contains("_")) {
                eventElement.setAnchorName(JavaUtil.getJavaVarName(eventElement.getAnchorName()));
            }
            if(eventElement.getParams() == null) {
                System.out.println(1);
            }
            if(eventElement.getParams() != null && eventElement.getParams().contains("_")) {
                String value = JavaUtil.getJavaVarName(eventElement.getParams());
                eventElement.setParams(value + "={" + value + "}");
            }


            elementOfRowMap.put(eventElement.getAnchorName(), eventElement);
        }
    }

    public static void peddingEventElement(List<EventElement> eventElementList, Mapping mapping, DataSetDescriptor dataSetDescriptor,ComponentDescriptor componentDescriptor) {

//        if(componentDescriptor.getComponent().getId().equals("dynTree")) {
//            System.out.println("1");
//        }

        String value = mapping.getValue();
        List<String> varList = RegexUtils.findVarList(value);

        if(varList.size() > 0) {
            String propertyName = getReallyProperty(varList.get(0), dataSetDescriptor);
            if(propertyName != null) {
                value = propertyName;
            }else {
                for (String var : varList) {
                    DataSet dataSet = dataSetDescriptor.getDataSet();
                    if("id".equals(var)) {
                        value = JavaUtil.getJavaVarName(value.replace("${" + var + "}",dataSetDescriptor.getKeyField() != null ? dataSetDescriptor.getKeyField().getCode() : (dataSet.getEventObjectCode() + "_" + var)));
                    }else if("name".equals(var)) {
                        value = JavaUtil.getJavaVarName(value.replace("${" + var + "}", dataSetDescriptor.getNameField() != null ? dataSetDescriptor.getNameField().getCode() : (dataSet.getEventObjectCode() + "_" + var)));
                    }else if("DS".equals(var)) {
                        value = value.replace("${" + var + "}", dataSet.getEventObjectCode());
                    }else if(var != null && var.endsWith("ByAjax")) {//"createByAjax".equals(var) || "updateByAjax".equals(var)|| "deleteByAjax".equals(var)
                        value = value.replace("${" + var + "}", /*JavaUtil.getJavaVarName(dataSet.getModule())
                        + "/" + */JavaUtil.getJavaVarName(dataSet.getEventObjectCode()) + "/" + var);
                    }else if(var != null && var.contains(":")) {

                        String type = var.substring(0,var.indexOf(":"));
                        String endChars = var.substring(var.indexOf(":") + 1);
//                ComponentDescriptor componentDescriptor = WebContext.get(type);
                        if(componentDescriptor != null && componentDescriptor.getPageDescriptor() != null && componentDescriptor.getPageDescriptor().
                                getComponentDescriptorBy(type) != null) {
                            DataSet relDataSet = componentDescriptor.getPageDescriptor().
                                    getComponentDescriptorBy(type).getDataSetDescriptor().getDataSet();
                            value = value.replace("${" + var + "}", relDataSet.getEventObjectCode() + "_" + endChars);
                        }else {
                            value = value.replace("${" + var + "}", dataSet.getEventObjectCode() + "_" + var);
                        }


                    }else {//create, edit , detail, batchDelete
                        value = value.replace("${" + var + "}", dataSet.getEventObjectCode() + "_" + var);
                    }
                }
            }
        }




        for (EventElement eventElement : eventElementList) {
            eventElement.setAction(eventElement.getAction() == null ? null :
                    eventElement.getAction().replace("${" + mapping.getId() + "}", value));
            eventElement.setComponent(eventElement.getComponent() == null ? null :
                    eventElement.getComponent().replace("${" + mapping.getId() + "}", value));
            String tmpValue = value;
            if("DS".equals(mapping.getId())) {
                if(eventElement.getParams() != null) {
                    String params = eventElement.getParams();
                    params = params.replace("=${" + mapping.getId() + "}", "=" + tmpValue + "");
                    params = params.replace("${" + mapping.getId() + "}", tmpValue + "=" + tmpValue + "");
                    eventElement.setParams(params);
                }
            }else {
                if (tmpValue.contains("_")) {
                    tmpValue = JavaUtil.getJavaVarName(value);
                }
                if(eventElement.getParams() != null) {
                    String params = eventElement.getParams();
                    params = params.replace("=${" + mapping.getId() + "}", "={" + tmpValue + "}");
                    params = params.replace("${" + mapping.getId() + "}", tmpValue + "={" + tmpValue + "}");
                    eventElement.setParams(params);
                }
            }


            eventElement.setAnchorName(eventElement.getAnchorName() == null ? null :
                    eventElement.getAnchorName().replace("${" + mapping.getId() + "}", tmpValue));

        }
    }

    private static String getReallyProperty(String val, DataSetDescriptor dataSetDescriptor) {
        if("KEY_FIELD".equals(val) && dataSetDescriptor.getKeyField() != null) {
            return dataSetDescriptor.getKeyField().getCode();
        }else if("NAME_FIELD".equals(val) && dataSetDescriptor.getNameField() != null) {
            return dataSetDescriptor.getNameField().getCode();
        }else if("P_KEY_FIELD".equals(val) && dataSetDescriptor.getSelfDependPropertyName() != null) {
            return dataSetDescriptor.getSelfDependPropertyName();
        }
        return null;
    }

    public ComponentDataContainer getDataInstance(Object data) {
        if (data instanceof List) {//暂无该情况
            List list = (List) data;
        } else if (data instanceof Map) {//list方法返回
            Map<String, Object> map = (Map) data;
            if(map.containsKey("pagination")) {
                map.put("pager", getPagers((Pagination) map.get("pagination")));
            }
            if(runtimeDataMap.containsKey("${pager}")) {
                runtimeDataMap.get("${pager}").clear();
            }

            if(map.containsKey("list")) {
                JsonSegmentParser jsonSegmentParser = runtimeDataMap.get("${data}");
                if (jsonSegmentParser instanceof Array2JsonSegmentParser) {
                    Array2JsonSegmentParser segmentParser = (Array2JsonSegmentParser) jsonSegmentParser;
                    JSONArray columns = (JSONArray) this.elements.get("columns").getJsonObject();
                    segmentParser.setData(map.get("list"),columns);
                }
                map.remove("list");
            }

            if (map.containsKey("helperData")) {
                JsonSegmentParser jsonSegmentParser = runtimeDataMap.get("${helperData}");
                if (jsonSegmentParser instanceof Array2JsonSegmentParser) {
                    Array2JsonSegmentParser segmentParser = (Array2JsonSegmentParser) jsonSegmentParser;
                    JSONArray columns = (JSONArray) this.elements.get("columns").getJsonObject();
                    segmentParser.setData(map.get("helperData"),columns);
                }
                map.remove("helperData");
            }

            if (map.containsKey("pager")) {
                JsonSegmentParser jsonSegmentParser = runtimeDataMap.get("${pager}");
                if (jsonSegmentParser instanceof Array2JsonSegmentParser) {
                    Array2JsonSegmentParser segmentParser = (Array2JsonSegmentParser) jsonSegmentParser;
                    segmentParser.setData(map.get("pager"), null);
                }
                map.remove("pager");
            }

            JsonSegmentParser jsonSegmentParser = runtimeDataMap.get("${data}");
            if (jsonSegmentParser instanceof ObjectTreeJsonSegmentParser) {
                ObjectTreeJsonSegmentParser segmentParser = (ObjectTreeJsonSegmentParser) jsonSegmentParser;
                if(map.size() > 0) {
                    Map map1 = transMapKeyStringType(map);
                    segmentParser.setData(map1);
                }else {
                    segmentParser.setData(new HashMap<String, Object>());
                }
            }




        } else {//详情返回
            JsonSegmentParser jsonSegmentParser = runtimeDataMap.get("${data}");
            if (jsonSegmentParser instanceof ObjectJsonSegmentParser) {
                ObjectJsonSegmentParser segmentParser = (ObjectJsonSegmentParser) jsonSegmentParser;
                JSONArray columns = null;
                if(this.elements.get("columns") != null) {
                    columns = (JSONArray) this.elements.get("columns").getJsonObject();
                }
                segmentParser.setData(data,columns);
            }
        }
        return this;
    }

    private Map transMapKeyStringType(Map<String, Object> map) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        for (Object key : map.keySet()) {
            if(key != null && !(key instanceof String)) {
                result.put(String.valueOf(key), map.get(key));
            }else {
                result.put((String)key, map.get(key));
            }
        }
        return result;
    }

    private List<Pager> getPagers(Pagination pagination) {

        int pageNo = pagination.getPageNo();
        int totalPage = pagination.getTotalPage();

        List<Pager> list = new ArrayList<Pager>();

        int pageNoStart = pageNo > 2 ? pageNo-2 : 1;

        int pageNoEnd = (pageNoStart + 4) > totalPage ? totalPage : (pageNoStart + 4);

        if(pageNoEnd - pageNoStart < 4) {
            pageNoStart =pageNoEnd- 4 > 0 ? (pageNoEnd- 4) : 1;
        }

        list.add(new Pager("上一页", pageNoStart, pageNoStart == pageNo ? "disabled" : null, pageNoStart == pageNo ? "active" : null));
        for(int i = pageNoStart; i <= pageNoEnd; i++) {
            list.add(new Pager(i + "",i,i == pageNo ? "disabled" : null ,i == pageNo ? "active" : null));
        }
        list.add(new Pager("下一页", pageNoEnd, pageNoEnd == pageNo ? "disabled" : null, pageNoEnd == pageNo ? "active" : null));

        return list;
    }

    public JSONObject getJson() {
        jsonObject = new JSONObject();
        for (String key : elements.keySet()) {
            jsonObject.put(key, elements.get(key).getJsonObject());
        }

        jsonObject.put("BOFR",beforeOfRowList.size() == 0 ? null : beforeOfRowList );
        jsonObject.put("EOFR",endOfRowList.size() == 0 ? null : endOfRowList );
        jsonObject.put("EOF",endOfCompList.size() == 0 ? null : endOfCompList );
        jsonObject.put("BOF", beforeOfCompList.size() == 0 ? null : beforeOfCompList);
        jsonObject.put("ELE", elementOfRowMap.size() == 0 ? null : elementOfRowMap);
        clearRuntimeData();

        return jsonObject;
    }

    public void clearRuntimeData(){
        for (String key : runtimeDataMap.keySet()) {
            if("${data}".equals(key)) {
                JsonSegmentParser jsonSegmentParser = runtimeDataMap.get(key);
                if (jsonSegmentParser instanceof ObjectJsonSegmentParser) {
                    ObjectJsonSegmentParser segmentParser = (ObjectJsonSegmentParser) jsonSegmentParser;
                    segmentParser.resultMap.clear();
                }
            }
        }
    }

    private List getEventJsonString(List<EventElement> eventElements) {
        List tempList = new ArrayList();
        for (EventElement eventElement : eventElements) {
            tempList.add(eventElement.getAction());
        }
        return tempList;
    }

    public class Pager{
        private String text;
        private int value;
        private String disabled;
        private String active;

        public Pager(String text, int value, String disabled, String active) {
            this.text = text;
            this.value = value;
            this.disabled = disabled;
            this.active = active;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getDisabled() {
            return disabled;
        }

        public void setDisabled(String disabled) {
            this.disabled = disabled;
        }

        public String getActive() {
            return active;
        }

        public void setActive(String active) {
            this.active = active;
        }
    }

    public interface JsonSegmentParser {

        public  boolean ok();

        public String toJson();

        public Object getJsonObject();

        public String getId();

        public void setDataSetDescriptorAndMapping(DataSetDescriptor dataSetDescriptor, Mapping mapping);

        public void clear();
    }

    public abstract class AbstractJsonSegmentParser{
        private String id;

        protected String componentType;
        protected Element element;

        protected Mapping mapping;

        protected DataSetDescriptor dataSetDescriptor;
        protected Map<String, String> elementDefaultMap = new HashMap<String, String>();

        public AbstractJsonSegmentParser(Element element, String type){
            this.element =element;
            this.componentType = type;
            if(com.hframework.common.util.StringUtils.isNotBlank(element.getDefaultValue())) {
                elementDefaultMap.put(element.getId(), element.getDefaultValue());
            }

            if(element.getElementList() != null) {
                for (Element element1 : element.getElementList()) {
                    if(com.hframework.common.util.StringUtils.isNotBlank(element1.getDefaultValue())) {
                        elementDefaultMap.put(element1.getId(), element1.getDefaultValue());
                    }
                }
            }
        }

        public abstract boolean ok();

        public abstract String toJson();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Mapping getMapping() {
            return mapping;
        }

        public void setMapping(Mapping mapping) {
            this.mapping = mapping;
        }

        public DataSetDescriptor getDataSetDescriptor() {
            return dataSetDescriptor;
        }

        public void setDataSetDescriptorAndMapping(DataSetDescriptor dataSetDescriptor, Mapping mapping){
            setDataSetDescriptor(dataSetDescriptor);
            setMapping(mapping);
            afterSetDataSetDescriptorAndMapping();

        }

        public abstract void afterSetDataSetDescriptorAndMapping();

        public void setDataSetDescriptor(DataSetDescriptor dataSetDescriptor) {
            this.dataSetDescriptor = dataSetDescriptor;
        }


    }

    public class Array2JsonSegmentParser extends AbstractJsonSegmentParser implements JsonSegmentParser{

        private String express;

        private Map<String, String> initMap = new LinkedHashMap<String, String>();

        private List<String[]> values;
        private Object data;

        public Array2JsonSegmentParser(Element element, String type) {
            super(element, type);
        }

        @Override
        public boolean ok() {
            return values != null;
        }

        @Override
        public String toJson() {
            return null;
        }

        public JSON getJsonObject() {
            JSONArray array = new JSONArray();
            if(values != null) {
                for (String[] value : values) {
                    array.add(JSONArray.toJSON(value));
                }
            }
            return array;
        }

        public void clear() {
            values = new ArrayList<String[]>();
        }

        @Override
        public void afterSetDataSetDescriptorAndMapping() {
            List<Mapping> mappingList = mapping.getMappingList();
            for (Mapping mapping1 : mappingList) {
                initMap.put(mapping1.getId(), mapping1.getValue());
            }
            express = mapping.getValue();
            runtimeDataMap.put(express,this);
        }

        public void setData(Object data, JSONArray columns) {
            this.data = data;
            if (data instanceof List) {
                values = new ArrayList<String[]>();
                List list = (List) data;
                for (Object object : list) {
                    List<String> value= new ArrayList<String>();
                    if(initMap.size() != 0) {
                        for (String string : initMap.values()) {
                            String propertyName = string.substring(2,string.length()-1);
                            try {
                                String stringVal = org.apache.commons.beanutils.BeanUtils.getProperty(object,propertyName);
                                value.add(stringVal == null ? "" : stringVal);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }else {
                        for (Object column : columns) {
                            value.add(getPropertyValue(object,
                                    JavaUtil.getJavaVarName(((JSONObject) column).getString("code")),
                                    ((JSONObject) column).getString("editType")));
                        }
//                        values.add(BeanUtils.getPropertiesArray(object));
                    }
                    values.add(value.toArray(new String[0]));
                }
            }
        }
    }

    public class ArrayJsonSegmentParser extends AbstractJsonSegmentParser implements JsonSegmentParser{


        public ArrayJsonSegmentParser(Element element, String type) {
            super(element, type);
        }

        @Override
        public boolean ok() {
            return false;
        }

        public String[] values;

        @Override
        public String toJson() {
            return null;
        }

        public Object getJsonObject() {
//            JSONArray array = new JSONArray();
//            array.addAll(Arrays.asList(values));
            return JSONArray.toJSON(values);
        }

        public void clear() {
            values = null;
        }

        @Override
        public void afterSetDataSetDescriptorAndMapping() {

        }
    }

    public class ObjectJsonSegmentParser extends AbstractJsonSegmentParser implements JsonSegmentParser{

        private Map<String, String> expressesMap= new LinkedHashMap<String, String>();

        private Map<String, String> resultMap= new LinkedHashMap<String, String>();

        private String express;

        public ObjectJsonSegmentParser(Element element,String type) {
            super(element, type);
        }

        @Override
        public boolean ok() {
            return expressesMap.size() == 0;
        }

        @Override
        public String toJson() {
            return null;
        }

        public JSONObject getJsonObject() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll(resultMap);
            return jsonObject;
        }

        public void clear() {
            resultMap= new LinkedHashMap<String, String>();
        }

        @Override
        public void afterSetDataSetDescriptorAndMapping() {
            List<Mapping> mappingList = mapping.getMappingList();
            for (Mapping mapping1 : mappingList) {
                expressesMap.put(mapping1.getId(), mapping1.getValue());
            }
            express = mapping.getValue();
            if("${data}".equals(express)) {
                runtimeDataMap.put(express,this);
            }
        }

        public void setData(Object data, JSONArray columns) {
            if (data != null) {
                if (expressesMap.size() != 0) {
                    for (String showCode : expressesMap.keySet()) {
                        String dataPropertyName = expressesMap.get(showCode);
                        String propertyName = dataPropertyName.substring(2, dataPropertyName.length() - 1);
                        String reallyProperty = getReallyProperty(propertyName, dataSetDescriptor);
                        if(reallyProperty != null) {
                            propertyName = reallyProperty;
                        }
                        resultMap.put(showCode, getPropertyValue(data, JavaUtil.getJavaVarName(propertyName), "x"));
                    }

                } else {
                    for (Object column : columns) {
                        resultMap.put(JavaUtil.getJavaVarName(((JSONObject) column).getString("code")),
                                getPropertyValue(data, JavaUtil.getJavaVarName(((JSONObject) column).getString("code")),
                                        ((JSONObject) column).getString("editType")));
                    }
                }
            }
        }


    }

    private String getPropertyValue(Object data, String propertyName, String editType) {
        try {
            if(data instanceof MapWrapper || data instanceof Map) {
                Map map = data instanceof MapWrapper ? ((MapWrapper)data).map() : (Map)data;
                Object value = map.get(propertyName);
                if(value == null){
                    return "";
                }else if(value instanceof Date) {
                    return DateUtils.getDateYYYYMMDDHHMMSS((Date)value);
                }else if(value instanceof Integer && propertyName.toLowerCase().endsWith("time") &&
                        (editType == null || "hidden".equals(editType) || "text".equals(editType))) {
                    return DateUtils.getDateYYYYMMDDHHMMSS(new Date(Long.valueOf(String.valueOf(value)) * 1000));
                }else {
                    return String.valueOf(value);
                }
            }else {
                Class<?> type = BeanUtils.findPropertyType(propertyName, data.getClass());
                if(type == Date.class) {
                    Date date = (Date) ReflectUtils.getFieldValue(data, propertyName);
                    if(date != null) {
                        return DateUtils.getDateYYYYMMDDHHMMSS(date);
                    }else {
                        return "";
                    }
                }else if(type == Integer.class && propertyName.toLowerCase().endsWith("time") &&
                        (editType == null || "hidden".equals(editType) || "text".equals(editType))) {
                    Integer date = (Integer) ReflectUtils.getFieldValue(data, propertyName);
                    if(date != null && date > 0) {
                        return DateUtils.getDateYYYYMMDDHHMMSS(new Date(Long.valueOf(date) * 1000));
                    }else {
                        return "";
                    }
                }else {
                    String stringVal = org.apache.commons.beanutils.BeanUtils.getProperty(data,propertyName);
                    return stringVal == null ? "" : stringVal;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return "";
    }

    public class ObjectsJsonSegmentParser extends AbstractJsonSegmentParser implements JsonSegmentParser{

        private String express;

        private Map<String, String> initMap= new LinkedHashMap<String, String>();

        private List<Map<String, String>> resultList= new ArrayList<Map<String, String>>();

        public ObjectsJsonSegmentParser(Element element, String type) {
            super(element, type);
        }

        @Override
        public boolean ok() {
            return resultList.size() != 0;
        }

        @Override
        public String toJson() {
            return null;
        }

        public JSONArray getJsonObject() {
            JSONArray jsonArray = new JSONArray();
            for (Map<String, String> stringStringMap : resultList) {
                jsonArray.add(JSONArray.toJSON(stringStringMap));
            }
            return jsonArray;
        }

        public void clear() {
            resultList = new ArrayList<Map<String, String>>();
        }

        @Override
        public void afterSetDataSetDescriptorAndMapping() {

            List<Mapping> mappingList = mapping.getMappingList();
            for (Mapping mapping1 : mappingList) {
                initMap.put(mapping1.getId(), mapping1.getValue());
            }
            express = mapping.getValue();
            List<String> varList = RegexUtils.findVarList(express);
            for (String var : varList) {
                DataSet dataSet = dataSetDescriptor.getDataSet();
                if("columns".equals(var)) {
                    if(dataSet.getFields() == null) {
                        System.out.println(1);
                    }
                    List<Field> fields = dataSet.getFields().getFieldList();
                    if(fields ==null) {
                        logger.warn("dataset:{} fields is empty !", dataSet.getCode());
                    }else {
                        for (Field field : fields) {
                            Map<String, String> tempMap= new LinkedHashMap<String, String>();
                            for (String key : initMap.keySet()) {
                                tempMap.put(key, getValueFromField(field, initMap.get(key), dataSetDescriptor));
                            }
                            resultList.add(tempMap);
                        }
                    }

                    float visibleColumnCount = 0;
                    for (Map<String, String> stringStringMap : resultList) {
                        if(stringStringMap.containsKey("editType") && !"hidden".equals(stringStringMap.get("editType"))) {
                            if(stringStringMap.get("width") != null) {
                                visibleColumnCount += Float.parseFloat(stringStringMap.get("width"));
                            }else {
                                visibleColumnCount++;
                            }

                        }
                    }
                    for (Map<String, String> stringStringMap : resultList) {
                        if(stringStringMap.containsKey("editType") && !"hidden".equals(stringStringMap.get("editType"))) {
                            if(stringStringMap.get("width") != null) {
                                stringStringMap.put("width",100/(visibleColumnCount + (visibleColumnCount/5 + 1))*Float.parseFloat(stringStringMap.get("width")) + "%");
                            }else {
                                stringStringMap.put("width",100/(visibleColumnCount + (visibleColumnCount/5 + 1)) + "%");
                            }
                        }
                    }
                }
            }
        }

        private String getValueFromField(Field field, String code, DataSetDescriptor dataSetDescriptor) {

            code = code.substring(2, code.length() - 1).trim();
            if("code".equals(code)) {
                return JavaUtil.getJavaVarName(field.getCode());
            }else if("name".equals(code)) {
                return field.getName();
            }else if("notNull".equals(code)) {
                return field.getNotNull();
            }else if("tipinfo".equals(code)) {
                return field.getTipinfo();
            }else if("width".equals(code)) {
                return field.getWidth() == null ? "1" : field.getWidth();
            }else if("showType".equals(code)) {
                return field.getShowType();
            }else if("editType".equals(code)) {
                if(this.componentType.startsWith("eList")){//eList组件
                    return StringUtils.isNotBlank(field.getCreateEditType()) ? field.getCreateEditType() : field.getEditType();
                }else if(this.componentType.startsWith("c")){
                    return StringUtils.isNotBlank(field.getCreateEditType()) ? field.getCreateEditType() : field.getEditType();
                }else if(this.componentType.startsWith("e")){
                    return StringUtils.isNotBlank(field.getUpdateEditType()) ? field.getUpdateEditType() : field.getEditType();
                }else{
                    return field.getEditType();
                }
//            }else if("enumClass".equals(code)  && field.getEnumClass() != null) {
//                    return field.getEnumClass().getCode();
//            }else if("entityCode".equals(code) && field.getRel() != null) {
//                return field.getRel().getEntityCode().replaceAll("/", ".");
            }else if("relColumns".equals(code)) {
                if(field.getRel() != null && StringUtils.isNotBlank(field.getRel().getRelField())) {
                    String relFields = field.getRel().getRelField();
                    String result = "";
                    for (String relField : relFields.split(",")) {
                        Field refField = dataSetDescriptor.getFields().get(relField);
                        String paramName = relField;
                        /*BUG:2018-10-08
                         * case.1 => manis2/cfg_deployment_detail.xml
                         * <field code="job_template_id" name="任务模板定义id" edit-type="select" not-null="true">
                         *   <rel entity-code="cfg_job_template_def/id/name"/>
                         *  </field>
                         *  <field code="job_id" name="任务定义id" edit-type="select" not-null="true">
                         *    <rel entity-code="cfg_job/id/name" rel-field="job_template_id"/>
                         *  </field>
                         *  job_id的级联查询拼接的relat-element为"1=1&id={jobTemplateId}",
                         *  实际上应该为job_template_id={jobTemplateId}
                         *  case.2 => chameleon/program_module_setting.xml
                         *  <field code="hfpm_program_id_PCXT" name="项目名称" edit-type="select">
                         *    <rel entity-code="hfpm_program/hfpm_program_id/hfpm_program_name"/>
                         *  </field>
                         *  <field code="hfpm_module_id_PCXT" name="模块名称" edit-type="select">
                         *    <rel entity-code="hfpm_module/hfpm_module_id/hfpm_module_name" rel-field="hfpm_program_id_PCXT"/>
                         *  </field>
                         *  hfpm_module_id_PCXT的级联查询拼接的relat-element为"1=1&hfpm_module_id_PCXT={hfpmProgramIdPCXT}",
                         */
                        if(dataSetDescriptor.getDataSet().getEntityList().size() > 0 &&
                                !dataSetDescriptor.getDataSet().getCode().equals(dataSetDescriptor.getDataSet().getEventObjectCode()) &&
                                StringUtils.isBlank(field.getRel().getUrl()) && refField != null
                                && refField.getRel() != null && StringUtils.isNotBlank(refField.getRel().getEntityCode())) {
                            String entityCode = refField.getRel().getEntityCode();
                            paramName = entityCode.substring(entityCode.indexOf("/") +1, entityCode.lastIndexOf("/"));
                        }
                        result += (("".equals(result) ? "" : "&&")
                                + paramName
                                +  "={" + JavaUtil.getJavaVarName(relField)+"}");
                    }
                    return result;
                }else {
                    return null;
                }

            }else {//${enumClass||entityCode}
                if(field.getEnumClass() != null && field.getEnumClass().getCode() != null) {
                    return field.getEnumClass().getCode();
                }
                if(field.getRel() != null && field.getRel().getEntityCode() != null) {
                    StringBuffer sb = new StringBuffer();
                    if(field.getRel().getRelList() != null && field.getRel().getRelList().size() > 0) {
                        for (Rel rel : field.getRel().getRelList()) {
                            if(rel.getEntityCode() != null) {
                                if(sb.length() != 0) {
                                    sb.append(";");
                                }
                                sb.append(rel.getEntityCode().replaceAll("/","."));
                            }
                        }
                    }

                    if(sb.length() != 0) {
                        sb.append(";").append(field.getRel().getEntityCode().replaceAll("/","."));
                    }else {
                        sb.append(field.getRel().getEntityCode().replaceAll("/","."));
                    }

//                    String entityCode = field.getRel().getEntityCode();
//                    String entityName = entityCode.substring(0,entityCode.indexOf("/"));
//                    String entityKey = entityCode.substring(entityCode.indexOf("/") + 1, entityCode.lastIndexOf("/"));
//                    String entityShowName = entityCode.substring(entityCode.lastIndexOf("/"));
                    return sb.toString();
                }

                if(field.getEnumList() != null && field.getEnumList().size() > 0) {
                    JSONObject enums = new JSONObject();
                    for (Enum anEnum : field.getEnumList()) {
                        if(StringUtils.isNotBlank(anEnum.getParent())) {
                            enums.put(anEnum.getValue(),new String[]{anEnum.getName(), anEnum.getParent()});
                        }else {
                            enums.put(anEnum.getValue(),new String[]{anEnum.getName()});
                        }

                    }

                    return "JSON:" + enums.toJSONString().replaceAll("\"","'");
                }

                if(field.getRel() != null && StringUtils.isNotBlank(field.getRel().getUrl())) {
                    return "URL:" + field.getRel().getUrl();
                }
            }
            return null;
        }
    }


    public class ObjectTreeJsonSegmentParser extends AbstractJsonSegmentParser implements JsonSegmentParser{

        private String express;

        private Map<String, String> initMap= new LinkedHashMap<String, String>();

        private List<Map<String, Object>> resultTree= new ArrayList<Map<String, Object>>();

        private ComponentDataContainer componentDataContainer;
        private String endOfRowHtml = null;

        private String endOfRowHtml(){
            if(endOfRowHtml == null) {
                synchronized (this) {
                    if(endOfRowHtml == null) {
                        endOfRowHtml = "";
                        List<EventElement> endOfCompList = componentDataContainer.endOfRowList;

                        if(endOfCompList != null && endOfCompList.size() > 0) {
                            endOfRowHtml = "<div class='dyn-tree-oper' style='float:right;display:none;'>";
                            for (EventElement eventElement : endOfCompList) {
                                endOfRowHtml += "<a " +
                                        "class=\"btn" +eventElement.getFillclass() + " hfhref\" " +
                                        "href=\"javascript:void(0)\"  " +
                                        "params=\"" + eventElement.getParams() +"\" " +
                                        "action='" + eventElement.getAction()+ "' " +
                                        "when='" + eventElement.getWhen() + "'> " + eventElement.getComponent()+" </a>";
                            }
                            endOfRowHtml += "</div>";
                            endOfRowHtml = endOfRowHtml.replaceAll("<","&lt;").replaceAll(">","&gt;");
                        }

                    }
                }
            }

            return !"eTList".equals(this.componentType) ? endOfRowHtml : "";
        }

        public ObjectTreeJsonSegmentParser(ComponentDataContainer componentDataContainer, Element element, String type) {
            super(element, type);
            this.componentDataContainer =componentDataContainer;
        }

        @Override
        public boolean ok() {
            return resultTree.size() != 0;
        }

        @Override
        public String toJson() {
            return null;
        }

        public JSONArray getJsonObject() {
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(JSONArray.toJSON(resultTree));
//            for (Map<String, Object> stringStringMap : resultTree) {
//                jsonArray.add(JSONArray.toJSON(resultTree));
//            }
            return (JSONArray) JSONArray.toJSON(resultTree);
        }

        public void clear() {
            resultTree = new ArrayList<Map<String, Object>>();
        }

        @Override
        public void afterSetDataSetDescriptorAndMapping() {

            List<Mapping> mappingList = mapping.getMappingList();
            for (Mapping mapping1 : mappingList) {
                initMap.put(mapping1.getId(), mapping1.getValue());
            }
            express = mapping.getValue();
            runtimeDataMap.put(express,this);
//            List<String> varList = RegexUtils.findVarList(express);
//            for (String var : varList) {
//                DataSet dataSet = dataSetDescriptor.getDataSet();
//                if("columns".equals(var)) {
//                    List<Field> fields = dataSet.getFields().getFieldList();
//                    for (Field field : fields) {
//                        Map<String, String> tempMap= new LinkedHashMap<String, String>();
//                        for (String key : initMap.keySet()) {
//                            tempMap.put(key, getValueFromField(field, initMap.get(key)));
//                        }
//                        resultList.add(tempMap);
//                    }
//                }
//            }
        }

        public void setData(Map<String, Object> map) {
            resultTree= new ArrayList<Map<String, Object>>();
            String rootId = map.keySet().iterator().hasNext() ? map.keySet().iterator().next() : null;
            if( !rootId.equals("-1") && (map.containsKey(-1L) || map.containsKey(-1)|| map.containsKey("-1"))) {
                rootId = "-1";
            }

            List<Object> list = (List<Object>) map.get(rootId);
            if(list != null && list.size() > 0) {
                for (Object o : list) {
                    resultTree.add(getNodeInfo(o, map));
                }
            }
            System.out.println("==============");
        }

        private Map<String, Object> getNodeInfo(Object object, Map<String, Object> dataMap) {
            Map<String, Object> map = new HashMap<String, Object>();
            if(initMap.size() != 0) {
                for (String code : initMap.keySet()) {
                    //父子节点同名，表明开始循环
                    if(mapping.getId().equals(code)) {
                        String id = (String) map.get("id");
                        if(dataMap.containsKey(id)) {
                            List<Map<String, Object>> subNodeTree= new ArrayList<Map<String, Object>>();
                            List<Object> subNodes = (List<Object>) dataMap.get(id);
                            for (Object subNode : subNodes) {
                                subNodeTree.add(getNodeInfo(subNode, dataMap));
                            }
                            map.put(code, subNodeTree);
                        }
                        continue;
                    }

                    String propertyNameExp = initMap.get(code);
                    List<String> varList = RegexUtils.findVarList(propertyNameExp);
                    if("${row}".equals(propertyNameExp)) {
                        map.put(code, object);
                    }else if(varList != null && varList.size() > 0) {
                        String resultVal = propertyNameExp;
                        for (String var : varList) {
                            String propertyName = JavaUtil.getJavaVarName(var);

                            if (object instanceof Map && ("KEY_FIELD".equals(var) || "NAME_FIELD".equals(var)) && ((Map) object).containsKey(var)) {
                            }else {
                                String reallyProperty = getReallyProperty(var, this.getDataSetDescriptor());
                                if(reallyProperty != null) {
                                    propertyName = JavaUtil.getJavaVarName(reallyProperty);
                                }
                            }


                            try {
                                String stringVal = org.apache.commons.beanutils.BeanUtils.getProperty(object,propertyName);
                                if(stringVal == null) stringVal = org.apache.commons.beanutils.BeanUtils.getProperty(object,var);
                                if(stringVal != null)  resultVal = resultVal.replace("${" + var + "}", stringVal);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        resultVal = resultVal.replaceAll("<","&lt;").replaceAll(">","&gt;");
                        if(code.equals("name")) resultVal += endOfRowHtml();
                        map.put(code, resultVal);
                    }else {
                        String propertyName = JavaUtil.getJavaVarName(propertyNameExp);
                        try {
                            String stringVal = org.apache.commons.beanutils.BeanUtils.getProperty(object,propertyName);
                            if(code.equals("name")) stringVal += endOfRowHtml();

                            if(StringUtils.isBlank(stringVal) && elementDefaultMap.containsKey(code)) {
                                if("#{RANDOM_ICON}".equals(elementDefaultMap.get(code))){
//                                    List<String> tmpIconPool = Lists.newArrayList(iconPool);
                                    stringVal = iconPool.get(Math.abs(map.toString().hashCode()) % iconPool.size());
//                                    iconPool.remove(map.toString().hashCode() % iconPool.size());
                                }
                            }

                            map.put(code, stringVal);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
            return map;
        }


    }


    public class EnumJsonSegmentParser extends AbstractJsonSegmentParser implements JsonSegmentParser {

        private String express;
        private String value;

        public EnumJsonSegmentParser(Element element, String type) {
            super(element, type);
        }

        @Override
        public boolean ok() {
            return StringUtils.isNotBlank(value);
        }

        @Override
        public String toJson() {
            return null;
        }

        public String getJsonObject() {
            return value;
        }

        public void clear() {
            value = null;
        }

        @Override
        public void afterSetDataSetDescriptorAndMapping() {
            express = mapping.getValue();
            value = mapping.getValue();
        }

        public String getExpress() {
            return express;
        }

        public void setExpress(String express) {
            this.express = express;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public class CharacterJsonSegmentParser extends AbstractJsonSegmentParser implements JsonSegmentParser {

        private String express;
        private String value;

        public CharacterJsonSegmentParser(Element element, String type) {
            super(element,type);
        }

        @Override
        public boolean ok() {
            return StringUtils.isNotBlank(value);
        }

        @Override
        public String toJson() {
            return null;
        }

        public String getJsonObject() {
            if(value == null) {
                express = mapping.getValue();
                List<String> varList = RegexUtils.findVarList(express);
                String runtimeValue = express;
                for (String var : varList) {
                    if (var.startsWith("req:")) {
                        Map request = WebContext.getDefault();
                        String requestValue = (String) request.get(var.substring(4));
                        if(StringUtils.isNotBlank(requestValue)) {
                            runtimeValue = runtimeValue.replace("${" + var + "}", requestValue);
                        }
                    }
                }
                return runtimeValue;
            }else {
                return value;
            }
        }

        public void clear() {
            value = null;
        }

        @Override
        public void afterSetDataSetDescriptorAndMapping() {
            express = mapping.getValue();
            List<String> varList = RegexUtils.findVarList(express);
            for (String var : varList) {
                if(dataSetDescriptor == null) {
                    System.out.println(dataSetDescriptor);
                }
                DataSet dataSet = dataSetDescriptor.getDataSet();
                if("code".equals(var)) {
                    express = express.replace("${code}",dataSet.getCode());
                }else if("name".equals(var)) {
                    //TODO
                    if(dataSet.getName().contains("【") && dataSet.getName().contains("】")) {
                        String relName = dataSet.getName().substring(0,dataSet.getName().lastIndexOf("【"));
                        express = express.replace("${name}",relName);
                    }else {
                        express = express.replace("${name}",dataSet.getName());
                    }


                }else if("module".equals(var)) {
                    express = express.replace("${module}",dataSet.getModule());
                }/*else if("legendCode".equals(var)) {
                    Map request = WebContext.getDefault();
                    String code = (String) request.get("code");
                    String id = (String) request.get("id");
                    express = express.replace("${legendCode}","['" +code + "_" + id + "']");
                }else if("legendName".equals(var)) {
                    Map request = WebContext.getDefault();
                    String code = (String) request.get("code");
                    String id = (String) request.get("id");
                    express = express.replace("${legendCode}","['" +code + "_" + id + "']");
                }*/
            }
            if(!express.contains("${") && !express.contains("}")) {
                value = express;
            }
        }

        public String getExpress() {
            return express;
        }

        public void setExpress(String express) {
            this.express = express;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public Map<String, JsonSegmentParser> getElements() {
        return elements;
    }

    public void setElements(Map<String, JsonSegmentParser> elements) {
        this.elements = elements;
    }

    public void addElement(JsonSegmentParser jonSegmentParser) {
        this.elements.put(jonSegmentParser.getId(), jonSegmentParser);
    }

    public static class EventElement {
        private String component;
        private String params;
        private String action;
        private String when;
        private String name;
        private String fillclass;
        private String description;
        private JSONObject conditionObject = new JSONObject(true);
        private JSONObject actionJsonObject = new JSONObject(true);

        private String anchorName;

        public EventElement(Event event) {
            description = event.getDescription();
            if(event.getSource() != null) {
                params = event.getSource().getParam();
            }
            name = event.getName();

            for (Effect effect : event.getEffectList()) {
                JSONObject subJsonObject = new JSONObject();
                subJsonObject.put("action",effect.getAction());
                subJsonObject.put("isStack",effect.getIsStack());
                try{
                    subJsonObject.put("param",JSONObject.parseObject(StringUtils.isBlank(effect.getParam()) ? "{}" : effect.getParam()).toJSONString());
                }catch (Exception e) {
                    e.printStackTrace();
                }

                subJsonObject.put("content",effect.getContent());
                subJsonObject.put("targetId",effect.getTargetId());
                actionJsonObject.put(effect.getType(),subJsonObject);
            }

            action = actionJsonObject.toJSONString();

            anchorName = event.getAttach().getAnchor();
            if(event.getAttach().getAppendElementList() != null) {
                for (AppendElement appendElement : event.getAttach().getAppendElementList()) {
                    component = parseComponent(appendElement, params, action);
                    fillclass = (String) JSONObject.parseObject(appendElement.getParam()).get("fillclass");
                }
            }

            if(event.getPreHandleList() != null) {
                for (PreHandle preHandle : event.getPreHandleList()) {
                    conditionObject.put(preHandle.getCase1(), preHandle.getWhen());
                    if(StringUtils.isNotBlank(preHandle.getThen())) {
                        if(params == null) params= "";
                        if(!"".equals(params)) params +="&";
                        params +=(preHandle.getCase1() + "=" + preHandle.getThen() + "&_" + preHandle.getCase1() + "=" + preHandle.getWhen());
                    }
                }
            }
            when = conditionObject.toJSONString();
        }

        public String getAnchorName() {
            return anchorName;
        }

        public void setAnchorName(String anchorName) {
            this.anchorName = anchorName;
        }

        public String getFillclass() {
            return fillclass;
        }

        public void setFillclass(String fillclass) {
            this.fillclass = fillclass;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getWhen() {
            return when;
        }

        public void setWhen(String when) {
            this.when = when;
        }

        public EventElement(AppendElement appendElement) {
            component = parseComponent(appendElement, appendElement.getParam(), action);
            if(StringUtils.isNotBlank(appendElement.getParam())) {
                params = appendElement.getParam();
            }
        }

        private String parseComponent(AppendElement appendElement, String params, String action) {

            if("icon".equals(appendElement.getType())) {
                JSONObject jsonObject = JSONObject.parseObject(appendElement.getParam());
                return "<i class=\"${iconclass}\"></i>"
                        .replace("${iconclass}", (String) jsonObject.get("iconclass"));
            }else if("button".equals(appendElement.getType())) {
                if(StringUtils.isBlank(action)) action = "";
                JSONObject jsonObject = JSONObject.parseObject(appendElement.getParam());
                String iconClass = StringUtils.isNotBlank(jsonObject.getString("iconclass")) ? "<i class=\"${iconclass}\"></i>"
                        .replace("${iconclass}", (String) jsonObject.get("iconclass")) : "";

                if( ((String) jsonObject.get("btnclass")).contains("switch-button")) {
                    System.out.println(1);
                }
                String extAttr = ((String) jsonObject.get("btnclass")).contains("switch-button") && action.contains("component.row.add") ? "data-toggle=\"helper-picker\"" : "";

                return ("<button  class=\"btn hfhref ${btnclass}\" onclick=\"javascript:void(0)\" " + extAttr  + " params=\"${params}\" action='${action}'  title='${text}'>${btnText}</button>")
                        .replace("${btnclass}", (String) jsonObject.get("btnclass"))
                        .replace("${text}",(String) jsonObject.get("btnText"))
                        .replace("${btnText}", iconClass + (String) jsonObject.get("btnText"))
                        .replace("${params}", StringUtils.isNotBlank(params) ? params : "")
                        .replace("${action}", action);
            }else if("checkbox".equals(appendElement.getType())) {
                return "<input type=\"checkbox\" name=\"checkIds\", value=\"\" value-key=\"" + appendElement.getParam() + "\">";
            }
            return null;
        }

        public String getComponent() {
            return component;
        }

        public void setComponent(String component) {
            this.component = component;
        }

        public String getParams() {
            return params;
        }

        public void setParams(String params) {
            this.params = params;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public enum ComponentElementType{
        characters("characters", "字符串"),
        enums("enums", "枚举"),
        object("object", "对象"),
        objects("objects","对象数组"),
        objectTree("objectTree","对象树形结构"),
        array("array","数组"),
        array2("array2", "二维数组");


        private String code;
        private String name;

        ComponentElementType(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public enum ComponentEventAnchor{
        BOFR("BOFR", "行开始"),
        EOFR("EOFR", "行结尾"),
        BOFC("BOFC", "组件开始"),
        EOFC("EOFC", "组件结尾");

        private String code;
        private String name;

        ComponentEventAnchor(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public List<Event> getAllEvent() {
        return allEvent;
    }

    public void setAllEvent(List<Event> allEvent) {
        this.allEvent = allEvent;
    }
}
