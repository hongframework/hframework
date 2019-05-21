package com.hframework.web.controller.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.hframework.base.service.CommonDataService;
import com.hframework.beans.controller.ResultData;
import com.hframework.common.frame.ServiceFactory;
import com.hframework.common.util.RegexUtils;
import com.hframework.common.util.StringUtils;
import com.hframework.common.util.collect.CollectionUtils;
import com.hframework.common.util.collect.bean.Grouper;
import com.hframework.common.util.collect.bean.Mapper;
import com.hframework.common.util.file.FileUtils;
import com.hframework.common.util.file.MyFile;
import com.hframework.common.util.message.Dom4jUtils;
import com.hframework.web.config.bean.dataset.Node;
import com.hframework.web.context.*;
import com.hframework.web.controller.DefaultController;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by zhangquanhong on 2018/1/11.
 */
@Service
public class FileComponentInvoker {

    private static final Logger logger = LoggerFactory.getLogger(FileComponentInvoker.class);

    @Resource
    private CommonDataService commonDataService;


    public String getXmlContent(ComponentDescriptor componentDescriptor, HttpServletRequest request) throws Exception {
        String dataIdStr = componentDescriptor.getDataId();
        if(StringUtils.isBlank(dataIdStr) || StringUtils.isBlank(request.getParameter("id"))) {
            return null;
        }
        String xmlContent;
        if(DefaultController.isDynDataInfo(dataIdStr)){
            xmlContent = getDynDataInfo(dataIdStr, request);
        }else {
            String filePath = dataIdStr + "/" + request.getParameter("id");
            xmlContent =  FileUtils.readFile(filePath);
        }
        xmlContent = invokeEmbedPart(xmlContent);
        return xmlContent;
    }

    public JSONObject invokerXmlContainer(String xmlContent, String module, ComponentDescriptor componentDescriptor,
                                         ModelAndView mav, HttpServletRequest request, HttpServletResponse response) throws Throwable {
        //由于XML是嵌套结构，我们只需要第一层级进行数据的获取处理，后期子页面只需要进行组件信息获取即可
        if(StringUtils.isNotBlank(componentDescriptor.getDataId())) {
            Element rootElement;
            if(StringUtils.isNotBlank(xmlContent)) {
                rootElement = Dom4jUtils.getDocumentByContent(xmlContent).getRootElement();
            }else {
                rootElement = new DefaultElement("a");
            }

            DataSetContainer rootContainer = createRootContainer(componentDescriptor, rootElement, module, true);

            String helperDataXml = componentDescriptor.getDataSetDescriptor().getHelperDataXml();
            if(StringUtils.isBlank(helperDataXml)) helperDataXml = "<xml></xml>";
            Element helperElement = Dom4jUtils.getDocumentByContent(helperDataXml).getRootElement();
            DataSetContainer helperContainer = createRootContainer(componentDescriptor, helperElement, module, false);

            for (IDataSet iDataSet : rootContainer.getElementList()) {
                if (iDataSet instanceof DataSetInstance) {
                    DataSetInstance dataSet = (DataSetInstance) iDataSet;

                }
            }

            mav.addObject("helperTags", componentDescriptor.getDataSetDescriptor().getHelperTags());
            mav.addObject("helperScript",componentDescriptor.getHelperScript());
            mav.addObject("filePath", request.getParameter("id"));
            mav.addObject("fileContainer", rootContainer);
            mav.addObject("helperFileContainer", helperContainer);
            System.out.println(rootContainer);
        }


        JSONObject jsonObject = componentDescriptor.getJson();
        ModelAndView subResult = ServiceFactory.getService(DefaultController.class).
                gotoPage(module, componentDescriptor.getDataSetDescriptor().getDataSet().getCode() + "#",
                        null, null, "true", request, response);
        jsonObject.put("modelMap", subResult.getModelMap());
        jsonObject.put("view", subResult.getViewName());
        return jsonObject;
    }

    public JSONObject parseFileComponent(String type, String dataSetCode, String module, ComponentDescriptor componentDescriptor,
                                          ModelAndView mav, HttpServletRequest request, HttpServletResponse response) throws Throwable {
        JSONObject jsonObject;
        if("container".equals(type) || "container".equals(componentDescriptor.getComponent().getId())) {
            String xmlContent = getXmlContent(componentDescriptor, request);
            try {
                jsonObject = invokerXmlContainer(xmlContent, module, componentDescriptor, mav, request, response);
            }catch (Exception e) {
                logger.error("file parse error: " ,e);
                throw e;
            }
        }else if(StringUtils.isNotBlank(componentDescriptor.getDataId())) {
            ResultData resultData = invokerFileCategory(componentDescriptor);
            jsonObject = componentDescriptor.getJson(resultData);
        }else {
            jsonObject = componentDescriptor.getJson();
        }

        if(!(jsonObject.get("data") instanceof JSONArray)) {
            jsonObject.put("data",JSONObject.toJSON(WebContext.getDefault()));
        }
        jsonObject.put("dataSet", dataSetCode.contains("#") ?dataSetCode.substring(dataSetCode.indexOf("#") + 1) : dataSetCode);
        return jsonObject;
    }

    private ResultData invokerFileCategory(ComponentDescriptor componentDescriptor) {
        String path = componentDescriptor.getDataId();
        List<MyFile> allFilesFromParDirectory = FileUtils.getAllFilesFromParDirectory(
                new File(path),
                new String[]{}, new String[]{".xml"});

        Map<String, MyFile> convert = CollectionUtils.convert(allFilesFromParDirectory, new Mapper<String, MyFile>() {
            public <K> K getKey(MyFile myFile) {
                return (K) myFile.getId();
            }
        });
        for (MyFile myFile : allFilesFromParDirectory) {
            myFile.setId(myFile.getShortname());
            if (!"-1".equals(myFile.getPid())) {
                myFile.setPid(convert.get(myFile.getPid()).getShortname());
            }
        }

        Map<String, List<MyFile>> group = CollectionUtils.group(allFilesFromParDirectory, new Grouper<String, MyFile>() {
            public <K> K groupKey(MyFile myFile) {
                return (K) myFile.getPid();
            }
        });

        return ResultData.success(group);
    }

    public  String getDynDataInfo(String dataIdStr, final HttpServletRequest request) throws Exception {
        if(dataIdStr.startsWith("DATA-SET-REL://")){
            String[]  tableInfo = dataIdStr.substring("DATA-SET-REL://".length()).split("/");
            final String tableName = tableInfo[0];
            String targetField = tableInfo[1];
            final String keyField = tableInfo[2];
            Map<String, Object> map = commonDataService.selectDynamicTableDataOne(new HashMap() {{
                put("tableName", tableName);
                put("condition", keyField + " =" + request.getParameter("id"));
            }});

            if(map.get(targetField) != null) {
                if(map.get(targetField).getClass().isArray()){
                    return new String((byte[])map.get(targetField));
                }else {
                    return String.valueOf(map.get(targetField));
                }
            }
        }
        return null;
    }

    private String invokeEmbedPart(String xmlContent) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if(xmlContent == null) return null;
        String[] xmlEmbeds = RegexUtils.find(xmlContent, "<\bXML_EMBED [^<>]*\b>\b<\b/\bXML_EMBED\b>");
        for (String xmlEmbed : new HashSet<String>(Lists.newArrayList(xmlEmbeds))) {
            String[] classParts = RegexUtils.find(xmlEmbed, " class\b*=\b*\"[^\"]+\"");
            String[] methodParts = RegexUtils.find(xmlEmbed, " method\b*=\b*\"[^\"]+\"");
            String className = null, methodName = null;
            if(classParts != null && classParts.length > 0) {
                className = classParts[0].substring(0, classParts[0].length() - 1).replaceAll("class\b*=\b*\"","").trim();
            }
            if(methodParts != null && methodParts.length > 0) {
                methodName = methodParts[0].substring(0, methodParts[0].length() - 1).replaceAll("method\b*=\b*\"","").trim();
            }

            if(org.apache.commons.lang3.StringUtils.isNoneBlank(className) && org.apache.commons.lang3.StringUtils.isNoneBlank(methodName)) {
                String replaceString = String.valueOf(java.lang.Class.forName(className).getMethod(methodName, new java.lang.Class[0]).invoke(null, null));
                xmlContent = xmlContent.replace(xmlEmbed, replaceString);
            }
        }
        return xmlContent;
    }

    public static DataSetContainer createRootContainer(ComponentDescriptor componentDescriptor, Element rootElement, String module, boolean blankContainerNecessary) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
//        DataSetContainer rootContainer = (DataSetContainer) org.apache.commons.beanutils.BeanUtils.cloneBean(
//                componentDescriptor.getDataSetDescriptor().getDateSetStruct());
        //TODO 需要确认深度拷贝是否完整
        DataSetContainer rootContainer = (DataSetContainer) componentDescriptor.getDataSetDescriptor().getDateSetStruct().cloneBean();
        DataSetGroup rootDataSetGroup = new DataSetGroup();
        rootContainer.addDataGroup(rootDataSetGroup);
        rootDataSetGroup.setNode(rootContainer.getNode());
        rootDataSetGroup.setElementList(CollectionUtils.copy(rootContainer.getElementList()));


        setDataSetContainerValue(rootDataSetGroup, rootElement, blankContainerNecessary);

        setDataSetInstanceComponentData(rootContainer, module, componentDescriptor.getDataSetDescriptor().getDataSet().getCode(), "");
        return rootContainer;
    }

    private static void setDataSetInstanceComponentData(DataSetContainer rootContainer, String module,String parentDataSetCode, String subPageCode) {
        PageDescriptor subPageInfo = WebContext.get().getPageInfo(module, parentDataSetCode + "#" + (StringUtils.isNotBlank(subPageCode) ? (subPageCode + "#") : ""));

        if(rootContainer.isVirtualContainer()) {
            IDataSet tmpDataSet = rootContainer.getElementList().get(0);
            setDataSetInstanceComponentData(subPageInfo, (DataSetInstance) tmpDataSet);
        }else {
            if(rootContainer.getDataGroups() == null) return;
            for (DataSetGroup dataGroup : rootContainer.getDataGroups()) {
                for (IDataSet tmpDataSet : dataGroup.getElementList()){
                    if (tmpDataSet instanceof DataSetContainer) {
                        DataSetContainer dataSet = (DataSetContainer) tmpDataSet;
                        setDataSetInstanceComponentData(dataSet, module, parentDataSetCode, dataSet.getNode().getPath());
                    }else if (tmpDataSet instanceof DataSetInstance) {
                        final DataSetInstance dataSet = (DataSetInstance) tmpDataSet;

                        if(subPageInfo == null) {//为空表明节点虽然有子节点数据，但节点并不存在

                        }
                        setDataSetInstanceComponentData(subPageInfo, dataSet);
                    }
                }
            }
        }
    }

    private static void setDataSetContainerValue(DataSetGroup parentDataSetGroup, Element parentElement, boolean blankContainerNecessary)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        Set<String> excludeCode = getExcludeCode(parentDataSetGroup.getElementList());

        for (IDataSet iDataSet : parentDataSetGroup.getElementList()) {//容器、组件循环
            String path = iDataSet.getNode().getPath();
            LinkedList<Element> eleList = findElementsByPath(parentElement, path); //满足条件的数据元素
            if(eleList == null || eleList.size() == 0) {
                logger.warn("xml node [{}] is not exists !", path);
                if(!blankContainerNecessary) continue;
            }

            if (iDataSet instanceof DataSetContainer) {
                DataSetContainer dataSetContainer = (DataSetContainer) iDataSet;
                if(eleList == null || eleList.size() == 0) {
                    eleList.add(new DefaultElement(iDataSet.getNode().getCode()));
                }

                if(dataSetContainer.isVirtualContainer()) {
                    iDataSet = dataSetContainer.getElementList().get(0);
                }else {
                    for (Element  tarElement : eleList) {
                        DataSetGroup dataSetGroup = new DataSetGroup();
                        dataSetGroup.setNode(dataSetContainer.getNode());
                        dataSetContainer.addDataGroup(dataSetGroup);
                        dataSetGroup.setElementList(CollectionUtils.copy(dataSetContainer.getElementList()));
                        setDataSetContainerValue(dataSetGroup, tarElement, blankContainerNecessary);
                    }
                    continue;
                }
            }

            if (iDataSet instanceof DataSetInstance) {
                //如果是数据节点，表明下面必定没有重复的行信息，因为如果有重复行信息，会自动识别为一个数据集，就不会走该分支
                DataSetInstance dataSetInstance = (DataSetInstance) iDataSet;

                Set<String> includeCode = new HashSet<String>();
                List<Node> nodeList = dataSetInstance.getNode().getNodeList();
                if(nodeList != null && nodeList.size() > 0){//组件上级为VirtualContainer
                    for (Node node : nodeList) {
                        includeCode.add(node.getPath());
                    }
                }


                if(dataSetInstance.isOne() || parentDataSetGroup.getNode().getPath().equals(dataSetInstance.getNode().getPath())) {
                    Map<String, String> oneRowData = new LinkedHashMap<String, String>();
                    if(!eleList.isEmpty()) {
                        addRowData(oneRowData, eleList.get(0), "", excludeCode, includeCode);
                    }

                    if(oneRowData.size() > 0) {
                        parentDataSetGroup.setName(oneRowData.values().iterator().next());
                    }

                    dataSetInstance.setOne(oneRowData);
                }else {
                    if(dataSetInstance.getList() != null) dataSetInstance.getList().clear();
                    if(eleList != null && eleList.size() > 0) {
                        for (Element element : eleList) {
                            Map<String, String> oneRowData = new HashMap<String, String>();
                            addRowData(oneRowData, element, "", excludeCode, includeCode);
                            dataSetInstance.add(oneRowData);
                        }
                    }else {
                        dataSetInstance.add(new HashMap<String, String>());
                    }

//                    dataSetInstance.getDataSetDescriptor()

                }
            }
        }
    }

    private static void setDataSetInstanceComponentData(PageDescriptor subPageInfo, final DataSetInstance dataSet) {
        for (ComponentDescriptor descriptor : subPageInfo.getComponents().values()) {
            if(descriptor.isDefaultComponent()) continue;
            if(descriptor.getDataSetDescriptor() != null) {
                String tempDataSetCode = descriptor.getDataSetDescriptor().getDataSet().getCode();
                tempDataSetCode = tempDataSetCode.contains("#") ? tempDataSetCode.substring(tempDataSetCode.indexOf("#") + 1) : tempDataSetCode;
                if("SYSTEM_EMPTY_DATASET".equals(tempDataSetCode) || tempDataSetCode.equals( dataSet.getNode().getPath())) {
                    if(dataSet.isOne() || dataSet.getOne() != null) {
                        dataSet.setComponentData(descriptor.getJson(ResultData.success(dataSet.getOne())));
                    }else {
                        dataSet.setComponentData(descriptor.getJson(ResultData.success(new HashMap() {{
                            put("list", dataSet.getList());
                        }})));
                        dataSet.getComponentData().put("dataIsEmpty", dataSet.getList() == null || (dataSet.getList().size()==1 &&  dataSet.getList().get(0).isEmpty()));
                    }
                }
            }
        }
    }

    private static Set<String> getExcludeCode(List<IDataSet> elementList) {
        Set<String> set = new HashSet<String>();
        for (IDataSet iDataSet : elementList) {
            if (iDataSet instanceof DataSetContainer) {
                DataSetContainer dataSetContainer = (DataSetContainer) iDataSet;
                set.add(dataSetContainer.getNode().getPath());

            }
        }
        return set;
    }

    private static void addRowData(Map<String, String> oneRowData, Element element, String parentPath, Set<String> excludeCode, Set<String> includeCode) {
        Map<String, String> attrMap = getAttrMap(element);
        for (String attrCode : attrMap.keySet()) {
            oneRowData.put(parentPath + "#" + attrCode, attrMap.get(attrCode));
        }

        Map<String, Set<Element>> subElements = new HashMap<String, Set<Element>>();
        for (Object o : element.elements()) {
            Element tempElement = (Element) o;
            String tempName = tempElement.getName();
            if(excludeCode.contains(tempElement.getPath().replaceAll("/",".").substring(1))) {
                continue;
            }

            if(includeCode.contains(tempElement.getPath().replaceAll("/",".").substring(1))) {
                if(!subElements.containsKey(tempName)) subElements.put(tempName, new LinkedHashSet<Element>());
                subElements.get(tempName).add(tempElement);
                continue;
            }

            addRowData(oneRowData,tempElement, StringUtils.isNotBlank(parentPath)?"." + tempName : tempName, excludeCode, includeCode);
        }

        for (String subElementName : subElements.keySet()) {
            Set<Element> elements = subElements.get(subElementName);

            JSONArray subLines = new JSONArray();
            for (Element element1 : elements) {
                Map<String, String> newSubLine = new HashMap<String, String>();
                addRowData(newSubLine, element1, "", excludeCode, includeCode);
                subLines.add(newSubLine);
            }
            oneRowData.put(subElementName, subLines.toJSONString());
        }


        if(element.isTextOnly()) {
            oneRowData.put(StringUtils.isNotBlank(parentPath) ? parentPath: "#", element.getText());
        }

    }

    private static LinkedList<Element> findElementsByPath(final Element rootElement, String path) {
        if(rootElement.getPath().replaceAll("/",".").substring(1).equals(path)) {
            return new LinkedList<Element>(){{
                add(rootElement);
            }};
        }
        Map<String, LinkedList<Element>> elementMap = getElementMap(rootElement);
        if(elementMap.containsKey(path)) {
            return elementMap.get(path);
        }
        LinkedList<Element> result = new LinkedList<Element>();
        for (String tempPath : elementMap.keySet()) {
            if(path.startsWith(tempPath + ".")) {
                result.addAll(findElementsByPath(elementMap.get(tempPath).get(0), path));
            }
        }
        return result;
    }

    private static Map<String,String> getAttrMap(Element element) {
        Map attrMap = new LinkedHashMap();
        for (Object attr : element.attributes()) {
            Attribute attribute= (Attribute) attr;
            attrMap.put(attribute.getName(),attribute.getValue());
        }
        return attrMap;
    }

    private static Map<String,LinkedList<Element>> getElementMap(Element element) {
        Map<String,LinkedList<Element>> eleMap = new LinkedHashMap();
        //添加子节点信息
        for (Object o : element.elements()) {
            Element ele = (Element) o;
            if(!eleMap.containsKey(ele.getPath().replace("/",".").substring(1))) {
                eleMap.put(ele.getPath().replace("/", ".").substring(1), new LinkedList<Element>());
            }
            eleMap.get(ele.getPath().replace("/",".").substring(1)).add(ele);
        }
        return eleMap;
    }


}
