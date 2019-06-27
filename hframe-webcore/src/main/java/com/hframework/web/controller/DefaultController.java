package com.hframework.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.hframework.base.service.CommonDataService;
import com.hframework.beans.class0.Class;
import com.hframework.beans.controller.Pagination;
import com.hframework.beans.controller.ResultCode;
import com.hframework.beans.controller.ResultData;
import com.hframework.beans.exceptions.BusinessException;
import com.hframework.common.frame.ServiceFactory;
import com.hframework.common.springext.datasource.DataSourceContextHolder;
import com.hframework.common.util.*;
import com.hframework.common.util.collect.CollectionUtils;
import com.hframework.common.util.collect.bean.Fetcher;
import com.hframework.common.util.file.FileUtils;
import com.hframework.common.util.message.Dom4jUtils;
import com.hframework.web.CreatorUtil;
import com.hframework.web.SessionKey;
import com.hframework.web.auth.AuthServiceProxy;
import com.hframework.web.config.bean.module.Component;
import com.hframework.web.context.*;
import com.hframework.web.controller.core.*;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * User: zhangqh6
 * Date: 2016/5/11 0:16:16
 */
@Controller
public class DefaultController {
    private static final Logger logger = LoggerFactory.getLogger(DefaultController.class);

    @Resource
    private CommonDataService commonDataService;


    @Resource
    private ObjectMapper mvcObjectMapper;



    @Resource
    private AuthServiceProxy authServiceProxy;

    @Resource
    private PageExtendDataManager pageExtendDataManager;

    @Resource
    private ComponentInvokeManager componentInvokeManager;

    /**
     * 异步Ajax获取文件编辑器中HelperData（需要走ModelAndView进行视图进行渲染）
     * @return
     */
    @RequestMapping(value = "/getFileEditorHelperData.html")
    @ResponseBody
    public ModelAndView getFileEditorHelperData(HttpServletRequest request,
                                                HttpServletResponse response) throws Throwable {
        ModelAndView mav = new ModelAndView();
        String helperIndex = request.getParameter("helperIndex");
        String helperName = request.getParameter("helperName");
        String targetId = request.getParameter("targetId");
        String module = request.getParameter("module");
        String pageCode = request.getParameter("pageCode");
        if(org.apache.commons.lang3.StringUtils.isBlank(pageCode)) {
            String refererUrl = request.getHeader("referer");
            String[] refererUrlInfo = Arrays.copyOfRange(refererUrl.split("[/]+"), 2, refererUrl.split("[/]+").length);
            module = refererUrlInfo[0];
            pageCode = refererUrlInfo[1].substring(0, refererUrlInfo[1].indexOf(".html"));

        }


        logger.debug("request referer : {},{},{},{},{}", module, pageCode, targetId, helperIndex, helperName);
        PageDescriptor pageInfo = WebContext.get().getPageInfo(module, pageCode);
        Map<String, ComponentDescriptor> components = pageInfo.getComponents();

        ComponentDescriptor fileComponentDescriptor = null;
        for (ComponentDescriptor componentDescriptor : components.values()) {
            DataSetDescriptor dataSetDescriptor = componentDescriptor.getDataSetDescriptor();
            if(dataSetDescriptor != null && dataSetDescriptor.isHelperRuntime()) {
                fileComponentDescriptor = componentDescriptor;
                break;
            }
        }
        String id = targetId.substring(targetId.indexOf("#")+ 1);

        DataSetDescriptor dataSetDescriptor = fileComponentDescriptor.getDataSetDescriptor();
        dataSetDescriptor.resetHelperInfo(true, request);
        String helperDataXml = dataSetDescriptor.getHelperDataXml();
        if(com.hframework.common.util.StringUtils.isBlank(helperDataXml)) helperDataXml = "<xml></xml>";
        Element helperElement = Dom4jUtils.getDocumentByContent(helperDataXml).getRootElement();
        DataSetContainer helperContainer = FileComponentInvoker.createRootContainer(fileComponentDescriptor, helperElement, module, true);

        IDataSet targetDataGroups = helperContainer.getDataGroups().get(0).getElementMap().get(id);
        List<DataSetGroup> dataGroups = ((DataSetContainer) targetDataGroups).getDataGroups();
        List<DataSetGroup> targetDataSetGroups = new ArrayList<DataSetGroup>();
        if(helperIndex != null) {
            targetDataSetGroups.add(dataGroups.get(Integer.valueOf(helperIndex)));
        }else {
            for (DataSetGroup dataGroup : dataGroups) {
                if(dataGroup.getName() != null && dataGroup.getName().equals(helperName)) {
                    targetDataSetGroups.add(dataGroup);
                }
            }
        }
        ((DataSetContainer) targetDataGroups).setDataGroups(targetDataSetGroups);
        mav.addObject("helpCompData", targetDataGroups);

        JSONObject container = null;
        JSONObject jsonObject = fileComponentDescriptor.getJson();
        ModelAndView subResult = ServiceFactory.getService(DefaultController.class).
                gotoPage(module, fileComponentDescriptor.getDataSetDescriptor().getDataSet().getCode() + "#",
                        null, null, "true", request, response);
        Object elements = subResult.getModelMap().get("elements");
        for (Object o : ((Map) elements).values()) {
            container = (JSONObject)o;
            if(id.equals(container.getString("dataSet"))) {
                break;
            }
        }
//        ModelAndView subResult = ServiceFactory.getService(DefaultController.class).
//                gotoPage(module, targetId,
//                        null, null, "true", request, response);
        jsonObject.put("modelMap", subResult.getModelMap());
        jsonObject.put("view", subResult.getViewName());
        mav.addObject("container",container);
        mav.setViewName("component/helperData");
        return mav;
    }

    /**
     * 获取某对象详情
     * @param dataCodes
     * @param dataValue
     * @return
     */
    @RequestMapping(value = "/queryOne.json")
    @ResponseBody
    public ResultData queryOne(@ModelAttribute("dataCode") String dataCodes ,final @ModelAttribute("dataValue") String dataValue){
        logger.debug("request : {}", dataCodes, dataValue);

        String dataCode = dataCodes;
        if(dataCodes.contains(";")) {
            dataCode = dataCodes.substring(dataCodes.lastIndexOf(";") + 1);
        }
        final String[] split = dataCode.split("\\.");

        try{
            Map<String, String> dicInfo = new HashMap<String, String>(){{
                put("tableName", split[0]);
                put("condition", split[1] + " = " + dataValue);
            }};
            Map<String, Object> stringObjectMap = commonDataService.selectDynamicTableDataOne(dicInfo);

            logger.debug("result json : {}", stringObjectMap);
            return ResultData.success(stringObjectMap);
        }catch (Exception e) {
            logger.error("error : ", e);
            return ResultData.error(ResultCode.ERROR);
        }
    }




    /**
     * 数据保存
     * @return
     */
    @RequestMapping(value = "/")
    @ResponseBody
    public ModelAndView root(HttpServletRequest request,
                               HttpServletResponse response) throws Throwable {
        ModelAndView mav = new ModelAndView();
        Object attribute = request.getSession().getAttribute(SessionKey.USER);
        if(attribute != null) {
            return gotoPage("index", null, null, null, request, response);
        }else {
            return gotoPage("login", null, null, null, request, response);
        }
    }
    /**
     * 数据保存
     * @return
     */
    @RequestMapping(value = "/logout.html")
    @ResponseBody
    public ModelAndView logout(HttpServletRequest request,
                            HttpServletResponse response){
        ModelAndView mav = new ModelAndView();
        request.getSession().setAttribute("context", null);
        request.getSession().removeAttribute(SessionKey.USER);
        request.getSession().removeAttribute(SessionKey.AUTH);
        Object userKey = request.getSession().getAttribute(SessionKey.USER_KEY);
        if(userKey != null) {
            request.getSession().removeAttribute(String.valueOf(userKey));
            request.getSession().removeAttribute(SessionKey.USER_KEY);
        }
//        mav.addObject("staticResourcePath", "/static");
//        mav.setViewName("/login");
        try {
            return gotoPage("login", null, null, null, request, response);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    /**
     * 数据保存
     * @return
     */
    @RequestMapping(value = "/500.html")
    @ResponseBody
    public ModelAndView error500(HttpServletRequest request,
                               HttpServletResponse response){
        ModelAndView mav = new ModelAndView();
        mav.addObject("staticResourcePath", "/static");
        mav.setViewName("/500");
        return mav;
    }

    /**
     * 数据保存
     * @return
     */
    @RequestMapping(value = "/404.html")
    @ResponseBody
    public ModelAndView error404(HttpServletRequest request,
                                 HttpServletResponse response){
        ModelAndView mav = new ModelAndView();
        mav.addObject("staticResourcePath", "/static");
        mav.setViewName("/404");
        return mav;
    }

    /**
     * 数据保存
     * @return
     */
    @RequestMapping(value = "/login.json")
    @ResponseBody
    public ResultData login(HttpServletRequest request,
                            HttpServletResponse response){
        WebContext.clear();
        String dc = request.getParameter("dc");
        Component login = WebContext.get().getDefaultComponentMap().get("login");
        String dataSet = login.getDataSet();

        String moduleCode = dataSet.substring(0, dataSet.indexOf("/"));
        String dataSetCode = dataSet.substring(dataSet.indexOf("/") + 1);
        try {
            Class defPoClass = CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
                    WebContext.get().getProgram().getCode(), moduleCode, dataSetCode);
            Class defControllerClass = CreatorUtil.getDefControllerClass(WebContext.get().getProgram().getCompany(),
                    WebContext.get().getProgram().getCode(), moduleCode, dataSetCode);
            Object controller= ServiceFactory.getService(defControllerClass.getClassName().substring(0, 1).toLowerCase()
                    + defControllerClass.getClassName().substring(1));
            Object po = java.lang.Class.forName(defPoClass.getClassPath()).newInstance();
            Map<String, String[]> parameterMap = request.getParameterMap();
            for (String propertyName : parameterMap.keySet()) {
                if("dc".equals(propertyName) || parameterMap.get(propertyName) == null || parameterMap.get(propertyName).length == 0) continue;
                try{
                    org.apache.commons.beanutils.BeanUtils.setProperty(po,propertyName,parameterMap.get(propertyName)[0]);
                }catch (Exception e ) {
                }
            }
            ResultData resultData = invokeMethod(controller, "search",
                    new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath())},
                    new Object[]{po});

            if(resultData.isSuccess()) {
                Object data = resultData.getData();
                request.getSession().setAttribute(java.lang.Class.forName(defPoClass.getClassPath()).getName(),data);
                request.getSession().setAttribute(SessionKey.USER,data);
                request.getSession().setAttribute(SessionKey.USER_KEY, java.lang.Class.forName(defPoClass.getClassPath()).getName());
                authServiceProxy.auth(request);
            }
            return resultData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultData.error(ResultCode.UNKNOW);
    };

    /**
     * 数据保存
     * @return
     */
    @RequestMapping(value = "/deleteByAjax.json")
    @ResponseBody
    public ResultData delete(HttpServletRequest request,
                               HttpServletResponse response)  {
        try{
            String refererUrl = request.getHeader("referer");
            String[] refererUrlInfo = Arrays.copyOfRange(refererUrl.split("[/]+"), 2, refererUrl.split("[/]+").length);
            String module = refererUrlInfo[0];
            String pageCode = refererUrlInfo[1].substring(0, refererUrlInfo[1].indexOf(".html"));
            PageDescriptor pageInfo = WebContext.get().getPageInfo(module, pageCode);
            String ds = request.getParameter("_DS");
            String di = request.getParameter("_DI");
            String[] split = StringUtils.split(pageInfo.getPage().getSubDataSets(), ",");
            Set<String> cascadeDeleteDataSets = new HashSet<String>();
            if(split != null) {
                cascadeDeleteDataSets.addAll(Lists.newArrayList(split));
            }
            assetDeleteDataSet(ds, cascadeDeleteDataSets, Lists.<Object>newArrayList(di));
            deleteDataSet(ds, cascadeDeleteDataSets, Lists.<Object>newArrayList(di));
        }catch (BusinessException e) {
            e.printStackTrace();
            return ResultData.error(e.getResultCode());
        }catch (Exception e) {
            e.printStackTrace();
            return ResultData.error(ResultCode.ERROR);
        }
        return ResultData.success();
    }

    public void deleteDataSet(String deleteDataSet, Set<String> cascadeDeleteDataSets, final List<Object> deleteDataIds) throws Exception {
        final DataSetDescriptor mainDataSet = WebContext.get().getOnlyDataSetDescriptor(deleteDataSet);
        if(mainDataSet == null) {
            throw new BusinessException("data set not exists !");
        }
        String company = WebContext.get().getProgram().getCompany();
        String program = WebContext.get().getProgram().getCode();
        Map<String, Set<String>> beenUsedFieldMap = mainDataSet.getBeenUsedFieldMap();
        for (Set<String> beenUsedFields : beenUsedFieldMap.values()) {
            for (String beenUsedField : beenUsedFields) {
                String dataSet = beenUsedField.substring(0, beenUsedField.indexOf("/"));
                final String relField = beenUsedField.substring(beenUsedField.indexOf("/") + 1);
//                AuthContext.AuthUser.userClass
                final DataSetDescriptor subDataSetDescriptor = WebContext.get().getOnlyDataSetDescriptor(dataSet);
                String module = subDataSetDescriptor.getDataSet().getModule();
                List resultList = ControllerMethodInvoker.invokeSimpleListMethod(
                        company, program, module, dataSet, new HashMap<String, List<Object>>() {{
                            put(relField, deleteDataIds);
                        }}
                );
                if(resultList != null && resultList.size() > 0) {
                    if(cascadeDeleteDataSets.contains(module + "/" + dataSet)) {
                        List ids = CollectionUtils.fetch(resultList, new Fetcher() {
                            public Object fetch(Object result) {
                                return ReflectUtils.getFieldValue(result, JavaUtil.getJavaVarName(subDataSetDescriptor.getKeyField().getCode()));
                            }
                        });
                        for (final Object id : ids) {
                            ControllerMethodInvoker.invokeSimpleDelete(company, program, module, dataSet, new HashMap<String, Object>() {{
                                put(JavaUtil.getJavaVarName(subDataSetDescriptor.getKeyField().getCode()), id);
                            }});
                        }
                    }
                }
            }
        }
        String module = mainDataSet.getDataSet().getModule();
        for (final Object deleteDataId : deleteDataIds) {
            ControllerMethodInvoker.invokeSimpleDelete(company, program, module, deleteDataSet, new HashMap<String, Object>() {{
                put(JavaUtil.getJavaVarName(mainDataSet.getKeyField().getCode()), deleteDataId);
            }});
        }

    }

    public void assetDeleteDataSet(String deleteDataSet, Set<String> cascadeDeleteDataSets, final List<Object> deleteDataIds) throws Exception {
        DataSetDescriptor mainDataSet = WebContext.get().getOnlyDataSetDescriptor(deleteDataSet);
        if(mainDataSet == null) {
            throw new BusinessException("data set not exists !");
        }

        Map<String, Set<String>> beenUsedFieldMap = mainDataSet.getBeenUsedFieldMap();
        for (Set<String> beenUsedFields : beenUsedFieldMap.values()) {
            for (String beenUsedField : beenUsedFields) {
                String dataSet = beenUsedField.substring(0, beenUsedField.indexOf("/"));
                final String relField = beenUsedField.substring(beenUsedField.indexOf("/") + 1);
//                AuthContext.AuthUser.userClass
                final DataSetDescriptor subDataSetDescriptor = WebContext.get().getOnlyDataSetDescriptor(dataSet);
                String company = WebContext.get().getProgram().getCompany();
                String program = WebContext.get().getProgram().getCode();
                String module = subDataSetDescriptor.getDataSet().getModule();
                List resultList = ControllerMethodInvoker.invokeSimpleListMethod(
                        company, program, module, dataSet, new HashMap<String, List<Object>>() {{
                            put(relField, deleteDataIds);
                        }}
                );
                if(resultList != null && resultList.size() > 0) {
                    if(!cascadeDeleteDataSets.contains(module + "/" + dataSet)) {
                        throw new BusinessException(dataSet + " [ " + relField + " = " + deleteDataIds + " ] is exists ,please delete first !");
                    }else {
                        List ids = CollectionUtils.fetch(resultList, new Fetcher() {
                            public Object fetch(Object result) {
                                return ReflectUtils.getFieldValue(result, JavaUtil.getJavaVarName(subDataSetDescriptor.getKeyField().getCode()));
                            }
                        });
                        assetDeleteDataSet(dataSet, cascadeDeleteDataSets, ids);
                    }
                }
            }
        }
    }

    /**
     * 数据保存
     * @return
     */
    @RequestMapping(value = "/ajaxSubmits.json")
    @ResponseBody
    public ResultData saveData(HttpServletRequest request,
                               HttpServletResponse response){
        String refererUrl = request.getHeader("referer");
        String[] refererUrlInfo = Arrays.copyOfRange(refererUrl.split("[/]+"), 2, refererUrl.split("[/]+").length);
        String module = refererUrlInfo[0];
        String pageCode = refererUrlInfo[1].substring(0, refererUrlInfo[1].indexOf(".html"));
        logger.debug("request referer : {},{},{}", refererUrl, module, pageCode);
        try{
            PageDescriptor pageInfo = WebContext.get().getPageInfo(module, pageCode);
            Map<String, ComponentDescriptor> components = pageInfo.getComponents();

            String dataJson = getRequestPostStr(request);

            String id = request.getParameter("path");
            if(StringUtils.isBlank(id)) {
                id = request.getParameter("filePath");
            }
            if(StringUtils.isBlank(id)) {
                id = request.getParameter("id");
            }

            logger.debug("request : {}|{}", dataJson, id);

            if(components.containsKey("container") && StringUtils.isNotBlank(id)) {
                DataSetDescriptor descriptor = components.get("container").getDataSetDescriptor();
                if("file".equals(descriptor.getDataSet().getSource())) {
                    String dataIdStr = components.get("container").getDataId();

                    JSONObject jsonObject = JSONObject.parseObject(dataJson, Feature.OrderedField);
                    Document document = DocumentHelper.createDocument();
                    Element root = document.addElement(descriptor.getDataSet().getDescriptor().getNode().getCode());

                    getXml(root, jsonObject, descriptor.getVirtualContainerSubNodePath());
//                    String xml = document.asXML();
                    OutputFormat formater = OutputFormat.createPrettyPrint();
                    formater.setTrimText(false);//这里不能trimText,当前台textarea的提交带回车符号sql语句，如果trimText，\r\n都被去掉，导致与实际不符。
                    StringWriter out = new StringWriter();
                    // 注释：创建输出流
                    XMLWriter writer = new XMLWriter(out, formater);
                    // 注释：输出格式化的串到目标中，执行后。格式化后的串保存在out中。
                    writer.write(document);

                    writer.close();
                    String xml = out.toString();

                    JSONArray tmpArray = new JSONArray();
                    tmpArray.add(xml);
                    String tmpJson = tmpArray.toJSONString();
                    xml = tmpJson.substring(1, tmpJson.length() - 1);
                    if(isDynDataInfo(dataIdStr)) {
                        xml = xml.substring(1, xml.length() - 1);
                        saveDynDataInfo(dataIdStr, id, xml);
                        return ResultData.success();
                    }else{
                        logger.info("write file : {} | {}", dataIdStr + "/" + id, xml);
                        FileUtils.writeFile(dataIdStr + "/" + id, xml);
                        return ResultData.success();
                    }
                }
            }


            JSONObject jsonObject = JSONObject.parseObject(dataJson, Feature.OrderedField);
            Set<String> componentKeys = jsonObject.keySet();
            Object parentObject = null;
            for (String componentKey : componentKeys) {
                int index = 0;
                String componentId = componentKey;
                if(componentId.contains("|")) {
                    index = Integer.valueOf(componentId.substring(componentId.indexOf("|") + 1));
                    componentId = componentId.substring(0, componentId.indexOf("|"));
                }
                ComponentDescriptor componentDescriptor = components.get(componentId);
                if(componentDescriptor == null) {
                    int tmpIndex = 0;
                    for (ComponentDescriptor descriptor : components.values()) {
                        if(descriptor.getId().equals(componentId) && tmpIndex++ == index) {
                            componentDescriptor = descriptor;
                            break;
                        }
                    }

                }
                if(componentDescriptor.getDataSetDescriptor() == null) {
                    logger.warn("component {} is not set data set",componentDescriptor.getId());
                    continue;
                }


                String moduleCode = componentDescriptor.getDataSetDescriptor().getDataSet().getModule();
                String eventObjectCode = componentDescriptor.getDataSetDescriptor().getDataSet().getEventObjectCode();
                String type = componentDescriptor.getComponent().getType();
                String[] columnTableInfo = componentDescriptor.getDataSetDescriptor().getColumnTableKeyAndValue();
                if(columnTableInfo != null && StringUtils.isNotBlank(columnTableInfo[2])) {
                    moduleCode = columnTableInfo[2].trim();
                }

                String componentJsonData = jsonObject.getString(componentKey);
                if(columnTableInfo != null){//行列转换，将form数据转化为纵表结构
//                    String keyFieldName = "id";
//                    try{
//                        keyFieldName = JavaUtil.getJavaVarName(WebContext.get().getOnlyDataSetDescriptor(eventObjectCode).getKeyField().getCode());
//                    }catch (Exception e) {}

                    JSONArray transResult = new JSONArray();
                    Object json = JSONObject.parse(componentJsonData);
                    if(json instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray) json;
                        if(type.endsWith("List")) {//表明为列表数据，需要在code中增加(0)等表示
                            int count = 0;
                            JSONObject defaultValues = componentDescriptor.getWebContextDefaultJson(new HashMap());
                            for (Object oneRow : jsonArray) {

                                Map<String, JSONObject> info = transFormDataToColTableData(defaultValues, oneRow, columnTableInfo, "(" + count + ")");
                                transResult.addAll(info.values());
                                count ++;
                            }
                        }else {
                            for (Object oneRow : jsonArray) {
                                Map<String, JSONObject> info = transFormDataToColTableData(new JSONObject(), oneRow, columnTableInfo, "");
                                transResult.addAll(info.values());
                            }
                        }
                    }
                    componentJsonData = transResult.toJSONString();
                }

                Class defPoClass = CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
                        WebContext.get().getProgram().getCode(), moduleCode, eventObjectCode);
                Class defControllerClass = CreatorUtil.getDefControllerClass(WebContext.get().getProgram().getCompany(),
                        WebContext.get().getProgram().getCode(), moduleCode, eventObjectCode);
                Object controller= ServiceFactory.getService(defControllerClass.getClassName().substring(0, 1).toLowerCase() + defControllerClass.getClassName().substring(1));
                Object objects = null;

                logger.debug("class: {}; json: {}", defPoClass.getClassName(), componentJsonData);
                if(componentJsonData.equals("[]")) {
                    continue;
                }
                if("treeChart".equals(componentId)) {
                    Map<String, String> result = new LinkedHashMap<String, String>();
                    JSONObject jsonObject1 = JSONObject.parseObject(componentJsonData);
                    parseRelatMap(jsonObject1, result);
                    String[] relPropertyNames = componentDescriptor.getDataSetDescriptor().getRelPropertyNames();
                    if(relPropertyNames != null && relPropertyNames.length == 2) {
                        List tempList = new ArrayList();
                        for (Map.Entry<String, String> newRel : result.entrySet()) {
                            String keyValue = newRel.getKey();
                            String parentKeyValue = newRel.getValue();
                            Object po = java.lang.Class.forName(defPoClass.getClassPath()).newInstance();
                            ReflectUtils.setFieldValue(po, relPropertyNames[0], keyValue);
                            ReflectUtils.setFieldValue(po, relPropertyNames[1], parentKeyValue);
                            tempList.add(po);
                        }
                        objects = tempList.toArray((Object[]) Array.newInstance(java.lang.Class.forName(defPoClass.getClassPath()), 0));
                    }
                }else {
                    java.lang.Class<?> aClass = java.lang.Class.forName(defPoClass.getClassPath());
                    String[] dateKeyAndValues = RegexUtils.find(componentJsonData, "\"[^\"]+\":\"[12]\\d{3}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\"");
                    for (String dateKeyAndValue : dateKeyAndValues) {
                        String key = dateKeyAndValue.substring(1, dateKeyAndValue.indexOf(":") - 1);
                        String value = dateKeyAndValue.substring(dateKeyAndValue.indexOf(":") + 2, dateKeyAndValue.length() - 1);
                        java.lang.Class<?> propertyType = org.springframework.beans.BeanUtils.findPropertyType(key, aClass);
                        if(propertyType != Date.class) {
                            long timestamp = DateUtils.parseYYYYMMDDHHMMSS(value).getTime() / 10000;
                            componentJsonData = componentJsonData.replace(dateKeyAndValue, "\"" + key + "\":\"" + timestamp + "\"");
                        }
                    }

                    objects = readObjectsFromJson(componentJsonData, java.lang.Class.forName(defPoClass.getClassPath()));
                    if(parentObject != null) {

                    }
                }

                ResultData resultData = (ResultData) ReflectUtils.invokeMethod(controller, "batchCreate", new java.lang.Class[]{
                        Array.newInstance(java.lang.Class.forName(defPoClass.getClassPath()), 1).getClass()}, new Object[]{objects});
                logger.debug("result: {}", JSON.toJSONString(resultData));
                if(!resultData.isSuccess()) {
                    return resultData;
                }
                if(type.endsWith("Form")) {
                    parentObject = ((Object[])objects)[0];
                    WebContext.add(parentObject);
//                    WebContext.put(type, componentDescriptor);
//                    Object object = readObjectFromJson(componentJsonData, java.lang.Class.forName(defPoClass.getClassPath()));
                }
            }

            return ResultData.success(parentObject);

        }catch (Exception e) {
            logger.error("error : ", e);
            return ResultData.error(ResultCode.ERROR);
        }

//        return ResultData.error(ResultCode.UNKNOW);
    }

    private Map<String, JSONObject> transFormDataToColTableData(JSONObject defaultValues, Object oneRow, String[] columnTableInfo, String endCharts) {
        JSONObject oneRowObject = (JSONObject) oneRow;
        Map<String, JSONObject> info = new LinkedHashMap<String, JSONObject>();
        for (Map.Entry<String, Object> entry : oneRowObject.entrySet()) {
            String businessCode = entry.getKey().contains("#") ? entry.getKey().substring(0,entry.getKey().indexOf("#")) : entry.getKey();
            if(!info.containsKey(businessCode)) {
                info.put(businessCode, new JSONObject());
            }
            JSONObject newRow = info.get(businessCode);
            if(entry.getKey().contains("#")) {
                if(entry.getValue() != null && StringUtils.isNotBlank(String.valueOf(entry.getValue()))) {
                    newRow.put(entry.getKey().substring(entry.getKey().indexOf("#") + 1), entry.getValue());
                }else if(defaultValues.containsKey(entry.getKey())){
                    newRow.put(entry.getKey().substring(entry.getKey().indexOf("#") + 1), defaultValues.get(entry.getKey()));
                }
            }else {
                newRow.put(columnTableInfo[0], entry.getKey() + endCharts);
                newRow.put(columnTableInfo[1], entry.getValue());
            }
        }


        //当页面对应字段为空时，传入后台没有这个字段，因此这里需要进行不全，否则插入数据库报字段不为空错误
        for (Map.Entry<String, JSONObject> entry : info.entrySet()) {
            JSONObject columnInfo = entry.getValue();
            if(!columnInfo.containsKey(columnTableInfo[0])){
                columnInfo.put(columnTableInfo[0], entry.getKey());
                columnInfo.put(columnTableInfo[1], "");
            }
        }
        return info;
    }

    /**
     * 数据保存
     * @return
     */
    @RequestMapping(value = "/ajaxSubmit.json")
    @ResponseBody
    public ResultData saveOneData(HttpServletRequest request,
                               HttpServletResponse response){
        String refererUrl = request.getHeader("referer");
        String[] refererUrlInfo = Arrays.copyOfRange(refererUrl.split("[/]+"), 2, refererUrl.split("[/]+").length);
        String module = refererUrlInfo[0];
        String pageCode = refererUrlInfo[1].substring(0, refererUrlInfo[1].indexOf(".html"));
        logger.debug("request referer : {},{},{}", refererUrl, module, pageCode);
        try{
            PageDescriptor pageInfo = WebContext.get().getPageInfo(module, pageCode);
            Map<String, ComponentDescriptor> components = pageInfo.getComponents();


            String id = request.getParameter("id");
            String value = request.getParameter("value");

            if(StringUtils.isNotBlank(id)){
                for (ComponentDescriptor componentDescriptor : components.values()) {
                    if(!componentDescriptor.isDefaultComponent() && isDynDataInfo(componentDescriptor.getDataId())) {
                        saveDynDataInfo(componentDescriptor.getDataId(), id, value);
                        return ResultData.success();
                    }
                }
            }

        }catch (Exception e) {
            logger.error("error : ", e);
            return ResultData.error(ResultCode.ERROR);
        }

        return ResultData.error(ResultCode.UNKNOW);
    }


    private void getXml(Element root, JSONObject jsonObject, Set<String> virtualContainerSubNodePath) {
        for (String key : jsonObject.keySet()) {
            Object valueObject = jsonObject.get(key);

            if(key.contains("#")) {
                key = key.substring(key.indexOf("#") + 1);
            }

            boolean isCurElement = key.equals(root.getPath().replaceAll("/+",".").substring(1));

            String parentPath = key.contains(".") ? key.substring(0, key.lastIndexOf(".")) : "";
            String curNodeName = key.contains(".") ? key.substring(key.lastIndexOf(".") + 1) : key;

            if(isSubNodesEmpty(valueObject)) {
                continue;
            }

            Element parentElement = isCurElement ? null : getOrCreateElementIfNotExist(root, parentPath);


            if (valueObject instanceof String) {//为组件数据
                valueObject = JSON.parse((String) valueObject, Feature.OrderedField);
                if (valueObject instanceof JSONArray) {
                    dealArrayValue((JSONArray) valueObject, parentElement, curNodeName,isCurElement , root, virtualContainerSubNodePath);
                }
            }else {//为容器数据
                if (valueObject instanceof JSONArray) {

                    JSONArray array = (JSONArray) valueObject;
                    for (Object o : array) {
                        Element curElement =isCurElement ? root : parentElement.addElement(curNodeName);
                        getXml(curElement, (JSONObject) o, virtualContainerSubNodePath);
                    }
                }
            }
        }
    }

    private void dealArrayValue(JSONArray valueObject, Element parentElement, String curNodeName, boolean isCurElement, Element root, Set<String> virtualContainerSubNodePath) {
        for (Object o : valueObject) {
            if (o instanceof JSONObject) {
                JSONObject object = (JSONObject) o;

                if(isSubNodesEmpty(object)) {
                    continue;
                }
                Element curElement = isCurElement ? root : parentElement.addElement(curNodeName);
                for (Map.Entry<String, Object> entry : object.entrySet()) {
                    String entryKey = entry.getKey();
                    String entryValue = (String) entry.getValue();
                    if(StringUtils.isBlank(entryValue)) {
                        continue;
                    }
                    if (entryKey.trim().equals("#")) {//表明为内容文本
                        curElement.setText(entryValue);
                    } else if (entryKey.startsWith("#")) {//表明为属性
                        curElement.addAttribute(entryKey.substring(1), entryValue);
                    } else {//表明为子对象
                        String subPath = entryKey.contains("#") ? entryKey.substring(0, entryKey.indexOf("#")) : entryKey;

                        if(virtualContainerSubNodePath.contains(curElement.getPath() + "/" + subPath.replaceAll("\\.","/"))) {//表明内容为json需要解析为xml的节点
                            if(StringUtils.isNotBlank(entryValue)) {
                                Object parse = JSONObject.parse(entryValue);
                                if(parse instanceof  JSONArray) {
                                    dealArrayValue((JSONArray) parse, curElement, subPath, false, root, virtualContainerSubNodePath);
                                }
                            }
                        }else {
                            Element tempElement = getOrCreateElementIfNotExist(curElement, subPath);
                            if (entryKey.contains("#")) {
                                String attrName = entryKey.substring(entryKey.indexOf("#") + 1);
                                tempElement.addAttribute(attrName, entryValue);
                            } else {
                                tempElement.setText(entryValue);

                            }
                        }

                    }
                }
            }
        }
    }

    private boolean isSubNodesEmpty(Object valueObject) {

        if (valueObject instanceof String) {//为组件数据
            valueObject = JSON.parse((String) valueObject, Feature.OrderedField);
        }

        if (valueObject instanceof JSONArray) {
            JSONArray array = (JSONArray) valueObject;
            for (Object o : array) {
                if (o instanceof JSONObject) {
                    JSONObject object = (JSONObject) o;
                    Collection<Object> values = object.values();
                    for (Object value : values) {
                        if (value != null && !"".equals(value)) {
                           return false;
                        }
                    }
                }
            }
        }else if (valueObject instanceof JSONObject) {
            JSONObject object = (JSONObject) valueObject;
            Collection<Object> values = object.values();
            for (Object value : values) {
                if (value != null && !"".equals(value)) {
                    return false;
                }
            }
        }

        return true;
    }

    private Element getOrCreateElementIfNotExist(Element curElement, String path) {
        //当PATH为绝对路径时,找到最近的节点
        Element element = getElement(curElement, path);//循环迭代获取需要的元素
        if(element != null) {
            curElement = element;
            String curPath = curElement.getPath().replaceAll("/+",".").substring(1);
            if(curPath.equals(path)) {
                return curElement;
            }
//            System.out.println(curPath);
//            System.out.println(path);
            path = path.substring(path.indexOf(curPath) + curPath.length()+1);
        }
        //当PATH为相对路径
        String[] nodeNames = path.split("\\.");
        for (String nodeName : nodeNames) {
            if(curElement.getName().equals(nodeName)) {
                continue;
            }
            List nodes = curElement.elements(nodeName);
            if(nodes != null && nodes.size() > 0) {
                curElement = (Element) nodes.get(0);
            }else {
                curElement = curElement.addElement(nodeName);
            }

        }
        return curElement;
    }


    public Element getElement(Element curElement, String path) {
        if(!path.startsWith(curElement.getPath().replaceAll("/+",".").substring(1))) {
            return null;
        }
        if(path.equals(curElement.getPath().replaceAll("/+",".").substring(1))) {
            return curElement;
        }
        List elements = curElement.elements();
        for (Object element : elements) {
            if (element instanceof Element) {
                Element element1 = (Element) element;
                if(path.equals(element1.getPath().replaceAll("/+",".").substring(1))) {
                    return element1;
                }
                if(path.startsWith(element1.getPath().replaceAll("/+",".").substring(1))) {
                    return getElement(element1,path);
                }
            }
        }
        return curElement;
    }

    private void parseRelatMap(JSONObject jsonObject1, Map<String, String> result) {

        String id = jsonObject1.getString("id");
        JSONArray children = jsonObject1.getJSONArray("children");
        if(children == null) {
            return ;
        }
        Iterator<Object> iterator = children.iterator();
        while (iterator.hasNext()) {
            JSONObject next = (JSONObject) iterator.next();
            String subId = next.getString("id");
            result.put(subId,id);
            parseRelatMap(next, result);
        }
    }

//    private Object readObjectFromJson(String jsonString, java.lang.Class<?> poClass) {
//        MockHttpInputMessage inputMessage = null;
//        try {
//            inputMessage = new MockHttpInputMessage(jsonString.getBytes("UTF-8"));
//            inputMessage.getHeaders().setContentType(new MediaType("application", "json"));
//            MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//            converter.setObjectMapper(this.mvcObjectMapper);
//            Object object = converter.read(poClass, inputMessage);
//            return object;
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }

    private Object[] readObjectsFromJson(String jsonString, final java.lang.Class<?> poClass) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter() {
            @Override
            protected JavaType getJavaType(Type type, java.lang.Class<?> contextClass) {
                if (type instanceof java.lang.Class && List.class.isAssignableFrom((java.lang.Class<?>)type)) {
                    return mvcObjectMapper.getTypeFactory().constructCollectionType(ArrayList.class, poClass);
                }
                else {
                    return super.getJavaType(type, contextClass);
                }
            }
        };
        converter.setObjectMapper(this.mvcObjectMapper);
        MockHttpInputMessage inputMessage = null;
        try {
            inputMessage = new MockHttpInputMessage(jsonString.getBytes("UTF-8"));
            inputMessage.getHeaders().setContentType(new MediaType("application", "json"));
            List results = (List) converter.read(List.class, inputMessage);

            Object[] o = (Object[]) Array.newInstance(poClass, results.size());
            for (int i = 0; i < results.size(); i++) {
                o[i] = results.get(i);
            }
            return o;
//            try {
//                return ReflectUtils.invokeMethod(results, "toArray", new java.lang.Class[]{o.getClass()}, new Object[]{o});
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
//            return results.toArray(new HfmdEnum[0]);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /***
     * Get request query string, form method : post
     *
     * @param request
     * @return byte[]
     * @throws IOException
     */
    public static byte[] getRequestPostBytes(HttpServletRequest request)
            throws IOException {
        int contentLength = request.getContentLength();
        /*当无请求参数时，request.getContentLength()返回-1 */
        if(contentLength<0){
            return null;
        }
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength;) {

            int readlen = request.getInputStream().read(buffer, i,
                    contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return buffer;
    }

    /***
     * Get request query string, form method : post
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static String getRequestPostStr(HttpServletRequest request)
            throws IOException {
        byte buffer[] = getRequestPostBytes(request);
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = "UTF-8";
        }
        return new String(buffer, charEncoding);
    }

    public static ResultData invokeMethod(Object controller, String action, java.lang.Class[] classes, Object[] objects) throws InvocationTargetException {
        ResultData resultData = (ResultData) ReflectUtils.invokeMethod(controller, action, classes, objects);
        return resultData;
    }

    /**
     * 页面跳转
     * @return
     * @throws Throwable
     */
    @RequestMapping(value = "/{pageX}.html")
    public ModelAndView gotoPage(@PathVariable("pageX") String pageCode,
                                 @ModelAttribute("component") String componentId,
                                 @ModelAttribute("pagination") Pagination pagination,
                                 @ModelAttribute("isPop") String isPop,
                                 HttpServletRequest request, HttpServletResponse response) throws Throwable {
        return gotoPage("",pageCode,componentId,pagination,isPop,request,response);
    }

    /**
     * 页面跳转
     * @return
     * @throws Throwable
     */
    @RequestMapping(value = "/{moduleX}/{pageX}.html")
    public ModelAndView gotoPage(@PathVariable("moduleX") String module,@PathVariable("pageX") String pageCode,
                                 @ModelAttribute("component") String componentId,
                                 @ModelAttribute("pagination") Pagination pagination,
                                 @ModelAttribute("isPop") String isPop,
                                 HttpServletRequest request, HttpServletResponse response) throws Throwable {
        logger.debug("module: {},page: {},params: {}", module, pageCode, request.getQueryString());
        if(WebContext.get("subContext") == null) {
            WebContext.clear();
            DataSourceContextHolder.clear();
        }

        ModelAndView mav = new ModelAndView();

        PageDescriptor pageInfo = WebContext.get().getPageInfo(module, pageCode);
        if(pageInfo == null) {
            logger.error("page {} not exists", module, pageCode);
            return error404(request,response);
        }
        boolean loginRequired = !pageInfo.getPage().isNoLogin();

        Map<String, Object> extendData = pageExtendDataManager.getExtendData("/extend/" + pageCode + ".json", request, response, mav);

        Map<String, ElementDescriptor> elements = pageInfo.getElements();
        for (String key : elements.keySet()) {
            ElementDescriptor elementDescriptor = elements.get(key);
            if (elementDescriptor instanceof StringDescriptor) {
                StringDescriptor descriptor = (StringDescriptor) elementDescriptor;
                if(StringUtils.isNotBlank(descriptor.getValue())) {
                    mav.addObject(key, descriptor.getValue());
                }else {
                    mav.addObject(key, WebContext.get().getElementValue(key));
                }
            }

        }
        Map<String, ContainerDescriptor> containers = pageInfo.getContainers();
        Map<String, String> pageContextParams = getPageContextParams(request);
        WebContext.putContext(getPageContextRealyParams(pageContextParams));


        Map<String, Object> result = new LinkedHashMap<String, Object>();
        JSONObject globalDataSetRulerJsonObject = new JSONObject();
        Map<String, ComponentDescriptor> components = pageInfo.getComponents();

        try{
            for (ComponentDescriptor componentDescriptor : components.values()) {
                if(StringUtils.isNotBlank(componentId) && !componentId.equals(componentDescriptor.getId())) {
                    continue;
                }
                Object componentExtendData = extendData != null ? extendData.get(componentDescriptor.getDataId()) : null;

                JSONObject componentJsonObject = componentInvokeManager.invoke(loginRequired,
                        StringUtils.isNotBlank(componentId), componentDescriptor, componentExtendData,
                        extendData, pagination, module, pageCode, pageInfo, mav, request, response);

                if(componentJsonObject != null) {
                    String key = componentDescriptor.getId();
                    if(result.containsKey(key)) {
                        key = componentDescriptor.getId() + "|" + componentDescriptor.getDataSetDescriptor().getDataSet()
                                .getCode() + "|" + componentDescriptor.getDataId();
                    }
                    System.out.println("=====>" + key + " : " + componentJsonObject.toJSONString());
                    result.put(key, componentJsonObject);
                    mergeGlobalRuler(globalDataSetRulerJsonObject, componentDescriptor.getDataSetDescriptor().getDataSetRulerJsonObject());
                    if(componentJsonObject.containsKey("HF_BREAK_FLAG") && componentJsonObject.getBoolean("HF_BREAK_FLAG")) {
                        break;
                    }
                }

            }
        }catch (SessionExpiredException e) {
            return gotoPage("login",null,null,null,request,response);
        }catch (Exception e) {
            logger.error("component invoke error => ", e);
            return error500(request, response);
        }

        if(request.getRequestURI() != null && !request.getRequestURI().contains("login.html") && !request.getRequestURI().contains("logout.html")) {
            mav.addObject("loginFowardUrl", request.getRequestURI()+"?" + request.getQueryString());
        }else {
            mav.addObject("loginFowardUrl","/index.html");

        }

        if(StringUtils.isNotBlank(pageInfo.getPage().getExtendScript())) {
            mav.addObject("pageExtendScript", pageInfo.getPage().getExtendScript());
        }

        mav.addObject("pageTitle", pageInfo.getName());
        mav.addObject("pageTemplate", pageInfo.getPageTemplate().getId());
        mav.addObject("subDataSetNames", pageInfo.getSubDataSetNames());
        mav.addObject("module",module);
        mav.addObject("page",pageCode);
        mav.addObject("globalRuler", globalDataSetRulerJsonObject.toJSONString());
        mav.addObject("ExtMap",extendData);
        mav.addObject("elements", result);
        mav.addAllObjects(result);
        mav.addObject("isPop","true".equals(isPop)? true : false);
        mav.addObject("staticResourcePath", "/static");
        if(StringUtils.isNotBlank(componentId) && "qList".equals(componentId)) {
            mav.addObject("list",mav.getModelMap().get("qList"));
            mav.setViewName("component/queryList");

        }else if(StringUtils.isNotBlank(componentId) && "eList".equals(componentId)) {
            mav.addObject("list",mav.getModelMap().get("eList"));
            mav.setViewName("component/editList");

        }else {
            String vmPath = StringUtils.isNotBlank(pageInfo.getPage().getVmPath()) ? pageInfo.getPage().getVmPath() : pageInfo.getPageTemplate().getPath();
            mav.setViewName(vmPath.substring(0, vmPath.indexOf(".vm")));
        }

        return mav;

    }


    public static boolean isDynDataInfo(String dataIdStr) throws Exception {
        return dataIdStr !=  null && dataIdStr.startsWith("DATA-SET-REL://");
    }

    public  boolean saveDynDataInfo(String dataIdStr, String id, String content) throws Exception {
        String[] tableInfo = dataIdStr.substring("DATA-SET-REL://".length()).split("/");
        final String tableName = tableInfo[0];
        String targetField = tableInfo[1];
        final String keyField = tableInfo[2];
        commonDataService.executeDBStructChange("update " + tableName + " set " + targetField + " = '" + content.replaceAll("'", "\\\\'") + "' where " + keyField + " = " + id);
        return true;
    }


    private void mergeGlobalRuler(JSONObject globalDataSetRulerJsonObject, JSONObject dataSetRulerJsonObject) {
        for (String key : dataSetRulerJsonObject.keySet()) {
            JSONArray jsonArray = dataSetRulerJsonObject.getJSONArray(key);
            for (Object o : jsonArray) {
                if(((JSONObject) o).containsKey("scope")) {
                    if(!globalDataSetRulerJsonObject.containsKey(key)) {
                        globalDataSetRulerJsonObject.put(key, new JSONArray());
                    }
                    globalDataSetRulerJsonObject.getJSONArray(key).add(o);
                }
            }
        }
    }


//    /**
//     * 算法不适用，需要换角度解决
//     * @param xmlNodes
//     * @return
//     */
//    private IDataSet groupXmlNodes(XmlNode xmlNodes) {
//
//        List<DataSetInstance> dataSetInstances = new ArrayList<DataSetInstance>();
//        List<DataSetContainer> dataSetContainers = new ArrayList<DataSetContainer>();
//        if(xmlNodes.getAttrMap() != null && xmlNodes.getAttrMap().size() > 0) {
//            dataSetInstances.add(DataSetInstance.valueOf(xmlNodes.getAttrMap()));
//        }
//
//        List<XmlNode> childrenXmlNode = xmlNodes.getChildrenXmlNode();
//
//        if(childrenXmlNode == null|| childrenXmlNode.size() == 0) {
//            return null;
//        }
//
//
//        List<XmlNode> leafXmlNodes = new ArrayList<XmlNode>();
//        List<XmlNode> parentXmlNodes = new ArrayList<XmlNode>();
//        Map<String, List<XmlNode>> repeatXmlNodes = new HashMap<String, List<XmlNode>>();
//        XmlNodeUtil.groupXmlNode(childrenXmlNode, leafXmlNodes, repeatXmlNodes, parentXmlNodes);
//
//        //将所有leaf节点拼装为一个map对象
//        Map<String, String>  map = new HashMap<String, String>();
//        for (XmlNode subXmlNode : leafXmlNodes) {
//            String nodeName = subXmlNode.getNodeName();
//            map.put(nodeName, subXmlNode.getNodeText());
//
//            Map<String, String> attrMap = subXmlNode.getAttrMap();
//            if(attrMap != null) {
//                for (String attrName : attrMap.keySet()) {
//                    map.put(nodeName + "#" + attrName, attrMap.get(attrName));
//                }
//            }
//        }
//        dataSetInstances.add(DataSetInstance.valueOf(map));
//
//        //将单独的父节点parentXmlNodes便利获得子对象
//        for (XmlNode subXmlNode : parentXmlNodes) {
//            IDataSet result = groupXmlNodes(subXmlNode);
//            if (result instanceof DataSetInstance) {
//                dataSetInstances.add((DataSetInstance) result);
//            }else {
//                dataSetContainers.add((DataSetContainer) result);
//            }
//        }
//
//        //将所有重复节点构成一个List对象
//        for (List<XmlNode> nodes : repeatXmlNodes.values()) {
//            List result = new ArrayList();
//            for (XmlNode subXmlNode : nodes) {
//                IDataSet tmpDataSet = groupXmlNodes(subXmlNode);
//                result.add(tmpDataSet);
//            }
//            DataSetInstance mergeDataSet = mergeDataSet(result);
//            if(mergeDataSet != null) {
//                dataSetInstances.add(mergeDataSet);
//            }
//            for (Object o : result) {
//                if (o instanceof DataSetInstance) {
//                    DataSetInstance dataSetInst = (DataSetInstance) o;
//                    dataSetInstances.add(dataSetInst);
//                }else {
//                    dataSetContainers.add((DataSetContainer) o);
//                }
//            }
//
//        }
//
//        if(dataSetContainers.size() == 1) {
//            dataSetContainers.get(0).addDataAll(dataSetInstances);
//            return dataSetContainers.get(0);
//        }
//
//        if(dataSetContainers.size() == 0 && dataSetInstances.size() == 1) {
//            return dataSetInstances.get(0);
//        }
//
//        DataSetContainer dataSetContainer = new DataSetContainer();
//        dataSetContainer.setSubDataSetContainers(dataSetContainers);
//        dataSetContainer.setDatas(dataSetInstances);
//        return dataSetContainer;
//
//    }
//
//    private DataSetInstance mergeDataSet(List result1) {
//        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
//        Iterator iterator = result1.iterator();
//        while (iterator.hasNext()) {
//            Object o = iterator.next();
//            if (o instanceof DataSetInstance) {
//                DataSetInstance dataSetInst = (DataSetInstance) o;
//                if(dataSetInst.isOne()) {
//                    data.add(dataSetInst.getOne());
//                    iterator.remove();
//                }
//            }
//        }
//
//        return data.size() > 0 ? DataSetInstance.valueOf(data) : null;
//    }
//
//    public static class XmlNodeUtil {
//
//
//        public static boolean isRepeatNode(List<XmlNode> list) {
//            if(list == null || list.size() == 0) return  false;
//
//            String code = null;
//            for (XmlNode xmlNode : list) {
//                if(code == null) code = xmlNode.getNodeCode();
//                if(!code.equals(xmlNode.getNodeCode())) return false;
//            }
//            return true;
//        }
//
//        public static boolean isAllLeafNode(List<XmlNode> list) {
//            if(list == null || list.size() == 0) return  false;
//
//            Set<String> codes = new HashSet<String>();
//            for (XmlNode xmlNode : list) {
//                if(xmlNode.getChildrenXmlNode() != null && xmlNode.getChildrenXmlNode().size() > 0) {
//                    return false;
//                }
//                if(codes.contains(xmlNode.getNodeCode())) {
//                    return false;
//                }
//                codes.add(xmlNode.getNodeCode());
//            }
//            return true;
//        }
//
//        public static void groupXmlNode(List<XmlNode> list, List<XmlNode> leafXmlNodes, Map<String, List<XmlNode>> repeatXmlNodes, List<XmlNode> parentXmlNodes) {
//            if(list == null || list.size() == 0) return ;
//
//            Set<String> singleCode = new HashSet<String>();
//            Set<String> repeatCode = new HashSet<String>();
//            for (XmlNode xmlNode : list) {
//                if(singleCode.contains(xmlNode.getNodeCode())) {
//                    repeatCode.add(xmlNode.getNodeCode());
//                    singleCode.remove(xmlNode.getNodeCode());
//                }
//
//                if(!singleCode.contains(xmlNode.getNodeCode()) && !repeatCode.contains(xmlNode.getNodeCode())) {
//                    singleCode.add(xmlNode.getNodeCode());
//                }
//            }
//
//
//            for (XmlNode xmlNode : list) {
//                if(repeatCode.contains(xmlNode.getNodeCode())) {
//                    if(!repeatXmlNodes.containsKey(xmlNode.getNodeCode())) {
//                        repeatXmlNodes.put(xmlNode.getNodeCode(), new ArrayList<XmlNode>());
//                    }
//                    repeatXmlNodes.get(xmlNode.getNodeCode()).add(xmlNode);
//                }
//
//                if(xmlNode.getChildrenXmlNode() != null && xmlNode.getChildrenXmlNode().size() > 0) {
//                    continue;
//                }
//                if(singleCode.contains(xmlNode.getNodeCode())) {
//                    leafXmlNodes.add(xmlNode);
//                }
//            }
//
//            Collections.copy(parentXmlNodes,list);
//            parentXmlNodes.removeAll(leafXmlNodes);
//            for (List<XmlNode> repeat : repeatXmlNodes.values()) {
//                parentXmlNodes.removeAll(repeat);
//            }
//        }
//
//    }
//
//    public static interface IDataSet{
//
//    }
//
//    public static class DataSetContainer implements IDataSet{
//
//        public List<DataSetContainer> subDataSetContainers;
//
//        public List<DataSetInstance> datas;
//
//        public static DataSetContainer valueOf(List<DataSetInstance> datas) {
//            DataSetContainer container = new DataSetContainer();
//            container.setDatas(datas);
//            return container;
//        }
//
//        public void addContainer(DataSetContainer subContainer) {
//            if(subDataSetContainers == null) subDataSetContainers = new ArrayList<DataSetContainer>();
//            subDataSetContainers.add(subContainer);
//        }
//
//        public void addData(DataSetInstance data) {
//            if(datas == null) datas = new ArrayList<DataSetInstance>();
//            datas.add(data);
//        }
//
//        public void addDataAll(List<DataSetInstance> data) {
//            if(datas == null) datas = new ArrayList<DataSetInstance>();
//            datas.addAll(data);
//        }
//
//        public List<DataSetContainer> getSubDataSetContainers() {
//            return subDataSetContainers;
//        }
//
//        public void setSubDataSetContainers(List<DataSetContainer> subDataSetContainers) {
//            this.subDataSetContainers = subDataSetContainers;
//        }
//
//        public List<DataSetInstance> getDatas() {
//            return datas;
//        }
//
//        public void setDatas(List<DataSetInstance> datas) {
//            this.datas = datas;
//        }
//    }
//
//    public static class DataSetInstance implements IDataSet{
//
//        public List<Map<String, String>> list;
//        public Map<String, String> one;
//
//        public static DataSetInstance valueOf(Object data) {
//            DataSetInstance instance = new DataSetInstance();
//            if (data instanceof List) {
//                instance.setList((List<Map<String, String>>) data);
//            }else if (data instanceof Map) {
//                instance.setOne((Map<String, String>) data);
//            }
//            return instance;
//        }
//
//        public boolean isOne() {
//            return one != null && one.size() > 0;
//        }
//
//        public List<Map<String, String>> getList() {
//
//            return list;
//        }
//
//        public void setList(List<Map<String, String>> list) {
//            this.list = list;
//        }
//
//        public Map<String, String> getOne() {
//            return one;
//        }
//
//        public void setOne(Map<String, String> one) {
//            this.one = one;
//        }
//    }





    public static Map<String, String> getPageContextRealyParams(Map<String, String> map) {
        Map<String, String> result = new HashMap<String, String>();
        for (String key : map.keySet()) {
            if(key.endsWith("PCXT")) {
                result.put(key.substring(0,key.length()-4), map.get(key));
            }else {
                result.put(key, map.get(key));
            }

        }
        return result;
    }

    public static Map<String, String> getPageContextParams(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration parameterNames = request.getParameterNames();
       while (parameterNames.hasMoreElements()) {
           String paramName = (String)parameterNames.nextElement();
           if(paramName.endsWith("PCXT")) {
               map.put(paramName, request.getParameter(paramName));
           }else {
               map.put(paramName, request.getParameter(paramName));
           }
       }
        logger.debug("page context : {}", map);
        return map;
    }


    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @RequestMapping(value = "/all_urls.json")
    @ResponseBody
    public void list(HttpServletRequest request, HttpServletResponse response) {
        StringBuilder sb = new StringBuilder();
        sb.append("URL").append("--").append("Class").append("--").append("Function").append('\n');

        Map<RequestMappingInfo, HandlerMethod> map = ServiceFactory.getService(RequestMappingHandlerMapping.class).getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
            RequestMappingInfo info = m.getKey();
            HandlerMethod method = m.getValue();
            sb.append(info.getPatternsCondition()).append("--");
            sb.append(method.getMethod().getDeclaringClass()).append("--");
            sb.append(method.getMethod().getName()).append('\n');
        }

        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.print(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }


}
