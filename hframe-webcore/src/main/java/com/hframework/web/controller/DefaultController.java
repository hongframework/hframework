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
import com.hframework.common.frame.ServiceFactory;
import com.hframework.common.springext.datasource.DataSourceContextHolder;
import com.hframework.common.util.*;
import com.hframework.common.util.StringUtils;
import com.hframework.common.util.collect.CollectionUtils;
import com.hframework.common.util.collect.bean.Fetcher;
import com.hframework.common.util.collect.bean.Grouper;
import com.hframework.common.util.collect.bean.Mapper;
import com.hframework.common.util.file.FileUtils;
import com.hframework.common.util.file.MyFile;
import com.hframework.common.util.message.Dom4jUtils;
import com.hframework.web.CreatorUtil;
import com.hframework.web.SessionKey;
import com.hframework.web.auth.AuthContext;
import com.hframework.web.auth.AuthServiceProxy;
import com.hframework.web.config.bean.DataSetHelper;
import com.hframework.web.config.bean.dataset.Field;
import com.hframework.web.config.bean.dataset.Node;
import com.hframework.web.config.bean.datasethelper.Mappings;
import com.hframework.web.config.bean.module.Component;
import com.hframework.web.context.*;
import com.vaadin.terminal.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.util.IoUtil;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.explorer.ExplorerApp;
import org.activiti.explorer.ui.Images;
import org.activiti.explorer.ui.custom.PrettyTimeLabel;
import org.activiti.explorer.ui.custom.UserProfileLink;
import org.apache.commons.lang3.*;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * User: zhangqh6
 * Date: 2016/5/11 0:16:16
 */
@Controller
public class DefaultController {
    private static final Logger logger = LoggerFactory.getLogger(DefaultController.class);

    private static Map<String, HandlerMethod> urlMapping = new HashMap<String, HandlerMethod>();
    @Resource
    private CommonDataService commonDataService;

    private ModelAttributeSetter modelAttributeSetter ;

    @Resource
    private ObjectMapper mvcObjectMapper;

    @Resource
    private ConfigurableWebBindingInitializer initializer;

    @Resource
    private AuthServiceProxy authServiceProxy;

//    /**
//     * 字典查询
//     * @param dataCode
//     * @param dataValues
//     * @return
//     */
//    @RequestMapping(value = "/getTexts.json")
//    @ResponseBody
//    public ResultData getTexts(@ModelAttribute("dataCode") String dataCode ,
//                               @RequestParam(value="dataValues[]",required=false) final Set<String> dataValues){
//        logger.debug("request : {}", dataCode, dataValues);
//        DataSourceContextHolder.clear();
//        try{
//            if(dataValues == null || dataValues.size() == 0) {
//                return ResultData.success();
//            }
//            List<KVBean> kvBeans = null;
//            final String[] split = dataCode.split("\\.");
//            if(split.length < 3) {
//                HfmdEnumClass_Example hfmdEnumClass_example = new HfmdEnumClass_Example();
//                hfmdEnumClass_example.createCriteria().andHfmdEnumClassCodeEqualTo(dataCode);
//                List<HfmdEnumClass> hfmdEnumClassListByExample = iHfmdEnumClassSV.getHfmdEnumClassListByExample(hfmdEnumClass_example);
//                if(hfmdEnumClassListByExample != null && hfmdEnumClassListByExample.size() > 0) {
//                    Long hfmdEnumClassId = hfmdEnumClassListByExample.get(0).getHfmdEnumClassId();
//                    HfmdEnum_Example hfmdEnum_example = new HfmdEnum_Example();
//                    hfmdEnum_example.createCriteria().andHfmdEnumClassIdEqualTo(hfmdEnumClassId);
//                    List<HfmdEnum> hfmdEnumList = iHfmdEnumSV.getHfmdEnumListByExample(hfmdEnum_example);
//                    kvBeans = CollectionUtils.from(hfmdEnumList, new Mapping<HfmdEnum, KVBean>() {
//                        public KVBean from(HfmdEnum hfmdEnum) {
//                            KVBean kvBean = new KVBean();
//                            kvBean.setValue(hfmdEnum.getHfmdEnumValue());
//                            kvBean.setText(hfmdEnum.getHfmdEnumText());
//                            return kvBean;
//                        }
//                    });
//                }
//            }else {
//                Map<String, String> dicInfo = new HashMap<String, String>(){{
//                    put("tableName", split[0]);
//                    put("keyColumn", split[1]);
//                    put("valueColumn", split[2]);
//                    if(split.length > 3) {
//                        put("extColumn", split[3]);
//
//                    }
//                    put("condition", split[1] + " in (" + Joiner.on(",").join(dataValues) + ")");
//                }};
//                kvBeans = commonDataService.selectDynamicTableDataList(dicInfo);
//            }
//            Map<String, KVBean> convert = CollectionUtils.convert(kvBeans, new Mapper<String, KVBean>() {
//                public <K> K getKey(KVBean kvBean) {
//                    return (K) kvBean.getValue();
//                }
//            });
//            return ResultData.success(convert);
//        }catch (Exception e) {
//            logger.error("error : ", e);
//            return ResultData.error(ResultCode.ERROR);
//        }
//    }
//
//    /**
//     * 字典查询
//     * @param dataCode
//     * @param dataCondition
//     * @return
//     */
//    @RequestMapping(value = "/dictionary.json")
//    @ResponseBody
//    public ResultData dictionary(@ModelAttribute("dataCode") String dataCode ,
//                                 @ModelAttribute("dataCondition") String dataCondition){
//        logger.debug("request : {}", dataCode, dataCondition);
//        DataSourceContextHolder.clear();
//        try{
//
//            final String[] split = dataCode.split("\\.");
//            if(split.length < 3) {
//                HfmdEnumClass_Example hfmdEnumClass_example = new HfmdEnumClass_Example();
//                hfmdEnumClass_example.createCriteria().andHfmdEnumClassCodeEqualTo(dataCode);
//                List<HfmdEnumClass> hfmdEnumClassListByExample = iHfmdEnumClassSV.getHfmdEnumClassListByExample(hfmdEnumClass_example);
//                if(hfmdEnumClassListByExample != null && hfmdEnumClassListByExample.size() > 0) {
//                    Long hfmdEnumClassId = hfmdEnumClassListByExample.get(0).getHfmdEnumClassId();
//                    HfmdEnum_Example hfmdEnum_example = new HfmdEnum_Example();
//                    hfmdEnum_example.createCriteria().andHfmdEnumClassIdEqualTo(hfmdEnumClassId);
//                    List<HfmdEnum> hfmdEnumList = iHfmdEnumSV.getHfmdEnumListByExample(hfmdEnum_example);
//                    List<KVBean> kvBeans = CollectionUtils.from(hfmdEnumList, new Mapping<HfmdEnum, KVBean>() {
//                        public KVBean from(HfmdEnum hfmdEnum) {
//                            KVBean kvBean = new KVBean();
//                            kvBean.setValue(hfmdEnum.getHfmdEnumValue());
//                            kvBean.setText(hfmdEnum.getHfmdEnumText());
//                            return kvBean;
//                        }
//                    });
//                    return ResultData.success(kvBeans);
//                }
//                return ResultData.error(ResultCode.RECODE_IS_NOT_EXISTS);
//            }else {
//                if(!validDataCondition(dataCondition)) {
//                    logger.warn("dataCode : [{}], dataCondition : [{}] invalid !", dataCode, dataCondition);
//                    return ResultData.success();
//                }
//                DataSetDescriptor dataSetDescriptor = WebContext.get().getOnlyDataSetDescriptor(split[0]);
//                if(dataSetDescriptor != null) {
//                    if(dataSetDescriptor.getFields().containsKey("pri")) {
//                        dataCondition += " order by pri asc";
//                    }
//                }
//
//                final String dataConditionFinal = dataCondition;
//                Map<String, String> dicInfo = new HashMap<String, String>(){{
//                    put("tableName", split[0]);
//                    put("keyColumn", split[1]);
//                    put("valueColumn", split[2]);
//                    if(split.length > 3) {
//                        put("extColumn", split[3]);
//                    }
//                    put("condition", dataConditionFinal);
//                }};
//                List<KVBean> kvBeans = commonDataService.selectDynamicTableDataList(dicInfo);
//
//                return ResultData.success(kvBeans);
//            }
//        }catch (Exception e) {
//            logger.error("error : ", e);
//            return ResultData.error(ResultCode.ERROR);
//        }
//    }

    private boolean validDataCondition(String dataCondition) {
        return !dataCondition.matches(".*=[ ]*") && !dataCondition.matches(".*=[ ]*&.*");
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

//    /**
//     * 字典查询
//     * @param dataCodes
//     * @param dataCondition
//     * @return
//     */
//    @RequestMapping(value = "/treeData.json")
//    @ResponseBody
//    public ResultData treeData(@ModelAttribute("dataCode") String dataCodes ,
//                                 @ModelAttribute("dataCondition") final String dataCondition, @ModelAttribute("dataValue") String dataValue){
//        logger.debug("request : {}", dataCodes, dataCondition);
//        JSONObject treeData = new JSONObject();
//        String dataDisplayValue = null;
//        try{
//            final String[] dataCodeArray = dataCodes.split(";");
//            Map<String, KVBean> cache = new LinkedHashMap<String, KVBean>();
//            for (String dataCode : dataCodeArray) {
//                ResultData dictionary = dictionary(dataCode, dataCondition);
//                List<KVBean> kvBeans = (List<KVBean>) dictionary.getData();
//                cache.putAll(CollectionUtils.convert(kvBeans, new Mapper<String, KVBean>() {
//                    public <K> K getKey(KVBean kvBean) {
//                        return (K) kvBean.getValue();
//                    }
//                }));
//                if(treeData.isEmpty()) {
//                    JSONArray jsonArray = new JSONArray();
//                    if(kvBeans != null) {
//                        for (KVBean kvBean : kvBeans) {
//                            JSONObject recode = new JSONObject();
//                            recode.put("code",kvBean.getValue());
//                            recode.put("address",kvBean.getText());
//                            jsonArray.add(recode);
//                        }
//                    }
//
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("", jsonArray);
//                    treeData.put("86", jsonObject);
//                }else {
//                    Map<String, List<KVBean>> group = CollectionUtils.group(kvBeans, new Grouper<String, KVBean>() {
//                        public <K> K groupKey(KVBean kvBean) {
//                            return (K) kvBean.getExtInfo();
//                        }
//                    });
//                    for (Map.Entry<String, List<KVBean>> entry : group.entrySet()) {
//                        Map<String, String> recode = new LinkedHashMap<String, String>();
//                        if(StringUtils.isBlank(entry.getKey())) {//存在垃圾数据时
//                            continue;
//                        }
//                        for (KVBean kvBean : entry.getValue()) {
//                            if(kvBean.getValue().equals(dataValue)) {
//                                dataDisplayValue = kvBean.getText();
//                                String parentId = kvBean.getExtInfo();
//                                while (cache.containsKey(parentId)) {
//                                    dataDisplayValue = cache.get(parentId).getText() + "/" + dataDisplayValue;
//                                    parentId = cache.get(parentId).getExtInfo();
//                                }
//                            }
//                            recode.put(kvBean.getValue(), kvBean.getText());
//                        }
//                        treeData.put(entry.getKey(), recode);
//                    }
//                }
//            }
//            logger.debug("result json : {}", treeData.toJSONString());
//            return ResultData.success().add("data", treeData).add("disValue", dataDisplayValue);
//        }catch (Exception e) {
//            logger.error("error : ", e);
//            return ResultData.error(ResultCode.ERROR);
//        }
//    }

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
//        mav.addObject("staticResourcePath", "/static");
//        mav.setViewName("/login");
        try {
            return gotoPage("login",null,null,null,request,response);
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
            ResultData resultData = invokeMethod(controller,"search",
                    new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath())},
                    new Object[]{po});

            if(resultData.isSuccess()) {
                Object data = resultData.getData();
                request.getSession().setAttribute(java.lang.Class.forName(defPoClass.getClassPath()).getName(),data);
                request.getSession().setAttribute(SessionKey.USER,data);
                authServiceProxy.auth(request);
            }
            return resultData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultData.error(ResultCode.UNKNOW);
    };
//    /**
//     * 数据保存
//     * @return
//     */
//    @RequestMapping(value = "/login.json")
//    @ResponseBody
//    public ResultData login(HttpServletRequest request,
//                               HttpServletResponse response){
//        WebContext.clear();
//        Component login = WebContext.get().getDefaultComponentMap().get("login");
//        Mapper mapper = WebContext.get().getMapper(login.getDataSet(), login.getId());
//        HashMap<String , String> inputs = new HashMap<String, String>();
//        if(mapper != null) {
//            List<com.hframework.web.config.bean.mapper.Mapping> mappingList = mapper.getBaseMapper().getMappingList();
//            for (com.hframework.web.config.bean.mapper.Mapping mapping : mappingList) {
//                String value = request.getParameter(mapping.getId());
//                inputs.put(mapping.getValue(), value);
//            }
//        }
//        if(!inputs.isEmpty()) {
//            String moduleCode = mapper.getDataSet().substring(0, mapper.getDataSet().indexOf("/"));
//            String dataSetCode = mapper.getDataSet().substring(mapper.getDataSet().indexOf("/") + 1);
//            try {
//                Class defPoClass = CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
//                        WebContext.get().getProgram().getCode(), moduleCode, dataSetCode);
//                Class defControllerClass = CreatorUtil.getDefControllerClass(WebContext.get().getProgram().getCompany(),
//                        WebContext.get().getProgram().getCode(), moduleCode, dataSetCode);
//                Object controller= ServiceFactory.getService(defControllerClass.getClassName().substring(0, 1).toLowerCase()
//                        + defControllerClass.getClassName().substring(1));
//                Object po = java.lang.Class.forName(defPoClass.getClassPath()).newInstance();
//                for (String propertyName : inputs.keySet()) {
//                    org.apache.commons.beanutils.BeanUtils.setProperty(po,propertyName,inputs.get(propertyName));
//                }
//                ResultData resultData = invokeMethod(controller,"search",
//                        new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath())},
//                        new Object[]{po});
//
//                if(resultData.isSuccess()) {
//                    Object data = resultData.getData();
//                    request.getSession().setAttribute(java.lang.Class.forName(defPoClass.getClassPath()).getName(),data);
//                    request.getSession().setAttribute(SessionKey.USER,data);
//                    authServiceProxy.auth(request);
//                }
//                return resultData;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return ResultData.error(ResultCode.UNKNOW);
//    };

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
                    if(dataIdStr.startsWith("DATA-SET-REL://")) {
                        String[] tableInfo = dataIdStr.substring("DATA-SET-REL://".length()).split("/");
                        final String tableName = tableInfo[0];
                        String xmlField = tableInfo[1];
                        final String keyField = tableInfo[2];
                        commonDataService.executeDBStructChange("update " + tableName + " set " + xmlField + " = " + xml + " where " + keyField + " = " + id);
                        return ResultData.success();
                    }else{
                        logger.info("write file : {} | {}", dataIdStr + "/" + id, xml);
                        FileUtils.writeFile(dataIdStr + "/" + id, xml);
                        return ResultData.success();
                    }
                }
            }


            JSONObject jsonObject = JSONObject.parseObject(dataJson, Feature.OrderedField);
            Set<String> componentIds = jsonObject.keySet();
            Object parentObject = null;
            for (String componentId : componentIds) {
                ComponentDescriptor componentDescriptor = components.get(componentId);
                if(componentDescriptor == null) {
                    for (ComponentDescriptor descriptor : components.values()) {
                        if(descriptor.getId().equals(componentId)) {
                            componentDescriptor = descriptor;
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

                String componentJsonData = null;

                Class defPoClass = CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
                        WebContext.get().getProgram().getCode(), moduleCode, eventObjectCode);
                Class defControllerClass = CreatorUtil.getDefControllerClass(WebContext.get().getProgram().getCompany(),
                        WebContext.get().getProgram().getCode(), moduleCode, eventObjectCode);
                Object controller= ServiceFactory.getService(defControllerClass.getClassName().substring(0, 1).toLowerCase() + defControllerClass.getClassName().substring(1));
                Object objects = null;
                componentJsonData = jsonObject.getString(componentId);
                logger.debug("class: {}; json: {}", defPoClass.getClassName(), componentJsonData);
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

    private Object readObjectFromJson(String jsonString, java.lang.Class<?> poClass) {
        MockHttpInputMessage inputMessage = null;
        try {
            inputMessage = new MockHttpInputMessage(jsonString.getBytes("UTF-8"));
            inputMessage.getHeaders().setContentType(new MediaType("application", "json"));
            MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
            converter.setObjectMapper(this.mvcObjectMapper);
            Object object = converter.read(poClass, inputMessage);
            return object;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

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

    public ResultData invokeMethod(Object controller, String action, java.lang.Class[] classes, Object[] objects) throws InvocationTargetException {
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

        Map<String, Object> extendData = getExtendData("/extend/" + pageCode + ".json", request);

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
//                    WebContext.get().add(getPageContextRealyParams(pageContextParams));
        WebContext.putContext(getPageContextRealyParams(pageContextParams));


        Map<String, Object> result = new LinkedHashMap<String, Object>();
        JSONObject globalDataSetRulerJsonObject = new JSONObject();
        Map<String, ComponentDescriptor> components = pageInfo.getComponents();
        for (ComponentDescriptor componentDescriptor : components.values()) {
            if(StringUtils.isBlank(componentId) || componentId.equals(componentDescriptor.getId())) {
                if(componentDescriptor.getDataSetDescriptor() == null) {
                    logger.warn("component {} is not set data set",componentDescriptor.getId());
                    continue;
                }
                if(componentDescriptor.getDataSetDescriptor().isHelperRuntime()){
                    componentDescriptor.getDataSetDescriptor().resetHelperInfo();
                }

                String moduleCode = componentDescriptor.getDataSetDescriptor().getDataSet().getModule();
                String eventObjectCode = componentDescriptor.getDataSetDescriptor().getDataSet().getEventObjectCode();
                String dataSetCode = componentDescriptor.getDataSetDescriptor().getDataSet().getCode();

                String type = componentDescriptor.getComponent().getType();
                String action = null;
                 if("eForm".equals(type) || "dForm".equals(type)) {
                    action = "detail";
                }else if("eList".equals(type)) {
                     action = "list";
                 }else if("cList".equals(type)) {
                     action = null;
                 }else if("eTList".equals(type)) {
                     action = "tree";
                 }else if(!"cForm".equals(type) && !"qForm".equals(type)) {
                     action = type;
                 }

                JSONObject jsonObject = null;
                String componentQueryString = null;
                if("pageflow".equals(componentDescriptor.getMapper().getDataAuth())) {
                    Map<String, String> pageContextParams2 = getPageContextParams(request);
//                    WebContext.get().add(getPageContextRealyParams(pageContextParams));
                    WebContext.putContext(getPageContextRealyParams(pageContextParams2));

//                        jsonObject.putAll(pageContextParams);
                    jsonObject = componentDescriptor.getJson();
                    jsonObject.put("data", pageContextParams2);
                }else if("file".equals(componentDescriptor.getDataSetDescriptor().getDataSet().getSource())) {
                    jsonObject = parseFileComponent(type, request,response, dataSetCode, module, componentDescriptor, mav);
                }else if("SYSTEM_EMPTY_DATASET".equals(dataSetCode)) {
                    jsonObject = componentDescriptor.getJson();
                }else if (extendData != null && extendData.containsKey(componentDescriptor.getDataId())) {
                    ResultData resultData = ResultData.success(extendData.get(componentDescriptor.getDataId()));
                    resetResultMessage(resultData, WebContext.get().getProgram().getCode(), moduleCode, dataSetCode, action);
                    if(resultData.isSuccess()) {

                        if("dynamicVM".equals(componentDescriptor.getId())) {
                            jsonObject = new JSONObject();
                            jsonObject.put("data",resultData.getData());
                        }else {
                            jsonObject = componentDescriptor.getJson(resultData);
                        }
                    }else {
                        jsonObject = componentDescriptor.getJson();
                        if(!(jsonObject.get("data") instanceof JSONArray)) {
                            jsonObject.put("data",JSONObject.toJSON(WebContext.get(HashMap.class.getName())));
                        }
                    }
                }else if(StringUtils.isNotBlank(action)) {

                    ResultData resultData = null;
                    Class defPoClass = CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
                            WebContext.get().getProgram().getCode(), moduleCode, eventObjectCode);
                    Class defPoExampleClass = CreatorUtil.getDefPoExampleClass(WebContext.get().getProgram().getCompany(),
                            WebContext.get().getProgram().getCode(), moduleCode, eventObjectCode);
                    Class defControllerClass = CreatorUtil.getDefControllerClass(WebContext.get().getProgram().getCompany(),
                            WebContext.get().getProgram().getCode(), moduleCode, eventObjectCode);

                    System.out.println("dataId = " + componentDescriptor.getDataId());
                    Object poExample = java.lang.Class.forName(defPoExampleClass.getClassPath()).newInstance();
                    PropertyDescriptor priPropertyDescriptor = org.springframework.beans.BeanUtils.getPropertyDescriptor(java.lang.Class.forName(defPoClass.getClassPath()), "pri");
                    PropertyDescriptor createTimePropertyDescriptor = org.springframework.beans.BeanUtils.getPropertyDescriptor(java.lang.Class.forName(defPoClass.getClassPath()), "createTime");
                    if (priPropertyDescriptor != null) {
                        ReflectUtils.invokeMethod(poExample, "setOrderByClause", new java.lang.Class[]{String.class}, new Object[]{" pri asc"});
                    }else if(createTimePropertyDescriptor != null){
                        ReflectUtils.invokeMethod(poExample, "setOrderByClause", new java.lang.Class[]{String.class}, new Object[]{" create_time desc"});
                    }

                    AuthContext authContext = authServiceProxy.getAuthContext(request);
                    if(authContext != null && authContext.getAuthManager().getAuthFunctionClass().contains(java.lang.Class.forName(defPoClass.getClassPath()))) {
                        List<Long> functionIds = authServiceProxy.getFunctionIds(request);
                        if(functionIds == null || functionIds.size() == 0) {
                            functionIds = new ArrayList<Long>(){{add(-999L);}};
                        }
                        Field keyField = componentDescriptor.getDataSetDescriptor().getKeyField();
                        Object criteria = ReflectUtils.invokeMethod(poExample, "createCriteria", new java.lang.Class[]{}, new Object[]{});
                        ReflectUtils.invokeMethod(criteria,
                                "and" + JavaUtil.getJavaClassName(keyField.getCode()) + "In",
                                new java.lang.Class[]{List.class}, new Object[]{functionIds});
                    }else if(authContext != null && authContext.getAuthManager().getAuthDataClass().contains(java.lang.Class.forName(defPoClass.getClassPath()))) {
                        Long funcId = authContext.getAuthFunctionManager().get("/" + module + "/" + pageCode + ".html");
                        List<Long> dataUnitIds = authContext.getAuthManager().getDataUnitIds(funcId);

                        Field keyField = componentDescriptor.getDataSetDescriptor().getKeyField();
                        Object criteria = ReflectUtils.invokeMethod(poExample, "createCriteria", new java.lang.Class[]{}, new Object[]{});
                        ReflectUtils.invokeMethod(criteria,
                                "and" + JavaUtil.getJavaClassName(keyField.getCode()) + "In",
                                new java.lang.Class[]{List.class}, new Object[]{dataUnitIds});
                    }else if(authContext != null){
                        List<String> relFieldNames = componentDescriptor.getDataSetDescriptor().getRelFieldCodes(authContext.getAuthManager().getAuthDataClass());
                        if(relFieldNames.size() > 0) {
                            Object criteria = ReflectUtils.invokeMethod(poExample, "createCriteria", new java.lang.Class[]{}, new Object[]{});
                            for (String relFieldName : relFieldNames) {
                                Long funcId = authContext.getAuthFunctionManager().get("/" + module + "/" + pageCode + ".html");
                                if(funcId == null) {
                                    String relPageCode = pageInfo.getPage().getRelPage();
                                    funcId = authContext.getAuthFunctionManager().get("/" + module + "/" + relPageCode + ".html");
                                }
                                List<Long> dataUnitIds = authContext.getAuthManager().getDataUnitIds(funcId);
                                ReflectUtils.invokeMethod(criteria,
                                        "and" + JavaUtil.getJavaClassName(relFieldName) + "In",
                                        new java.lang.Class[]{List.class}, new Object[]{dataUnitIds});
                            }
                        }

                    }



                    Object controller = ServiceFactory.getService(defControllerClass.getClassName().substring(0, 1).toLowerCase() + defControllerClass.getClassName().substring(1));
                    Object po = null;

                    if ("session".equals(componentDescriptor.getMapper().getDataAuth())) {
                        Object data = request.getSession().getAttribute(java.lang.Class.forName(defPoClass.getClassPath()).getName());
                        System.out.println("session data " + data);
                        if (data == null) {
//                                mav.addObject("staticResourcePath", "/static");
//                                mav.setViewName("/login");
//                                return mav;
                            return gotoPage("login",null,null,null,request,response);
                        }
                        resultData = ResultData.success(data);
                    } else if ("detail".equals(action)) {
                        po = getPoInstance(request, controller, action, new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath())});
                        Map<String, String> pageFlowParams = WebContext.get(HashMap.class.getName());
                        ReflectUtils.setFieldValue(po, pageFlowParams);
                        resultData = invokeMethod(controller, action,
                                new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath())},
                                new Object[]{po});
                        //将页面传过来的参数覆盖原先的值，主要用于具体的form表单提交时需要附带一些参数值
                        if(resultData.getData() != null) {
                            ReflectUtils.setFieldValue(resultData.getData(), pageFlowParams);
                        }
                        //这里将查询的单个对象存入线程中，别的组件在需要时可以获取想要的值，如数据集数据列智能提醒需要依赖数据集的主实体ID
                        WebContext.add(resultData.getData());
                    } else if ("tree".equals(action)) {

                        if(componentDescriptor.getDataSetDescriptor().isSelfDepend()) {
                            po = getPoInstance(request, controller, action, new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath()),
                                    java.lang.Class.forName(defPoExampleClass.getClassPath())});
                            resultData = invokeMethod(controller, action,
                                    new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath()),
                                            java.lang.Class.forName(defPoExampleClass.getClassPath())},
                                    new Object[]{po, poExample});
                        }else {
                            for (final String relFieldName : componentDescriptor.getDataSetDescriptor().getRelFieldKeyMap().keySet()) {
                                final DataSetDescriptor dataSetDescriptor = componentDescriptor.getDataSetDescriptor().getRelDataSetMap().get(componentDescriptor.getDataSetDescriptor().getRelFieldKeyMap().get(relFieldName));
                                if(dataSetDescriptor.isSelfDepend()) {
                                    String selfDependEventObjectCode = dataSetDescriptor.getDataSet().getEventObjectCode();
                                    String selfDependModule = dataSetDescriptor.getDataSet().getModule();
                                    Class selfDependDefPoClass = CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
                                            WebContext.get().getProgram().getCode(), selfDependModule, selfDependEventObjectCode);
                                    Class selfDependDefPoExampleClass = CreatorUtil.getDefPoExampleClass(WebContext.get().getProgram().getCompany(),
                                            WebContext.get().getProgram().getCode(), selfDependModule, selfDependEventObjectCode);
                                    Class selfDependDefControllerClass = CreatorUtil.getDefControllerClass(WebContext.get().getProgram().getCompany(),
                                            WebContext.get().getProgram().getCode(), selfDependModule, selfDependEventObjectCode);
                                    Object selfDependController = ServiceFactory.getService(
                                            selfDependDefControllerClass.getClassName().substring(0, 1).toLowerCase()
                                                    + selfDependDefControllerClass.getClassName().substring(1));
                                    ResultData baseResultData = invokeMethod(selfDependController, action,
                                            new java.lang.Class[]{java.lang.Class.forName(selfDependDefPoClass.getClassPath()),
                                                    java.lang.Class.forName(selfDependDefPoExampleClass.getClassPath())},
                                            new Object[]{getPoInstance(request, selfDependController, action,
                                                    new java.lang.Class[]{java.lang.Class.forName(selfDependDefPoClass.getClassPath()),
                                                    java.lang.Class.forName(selfDependDefPoExampleClass.getClassPath())})
                                                    , java.lang.Class.forName(selfDependDefPoExampleClass.getClassPath()).newInstance()});

                                    po = getPoInstance(request, controller, "list", new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath()),
                                            java.lang.Class.forName(defPoExampleClass.getClassPath()), Pagination.class});
                                    Map<String, String> pageFlowParams = WebContext.get(HashMap.class.getName());
                                    ReflectUtils.setFieldValue(po, pageFlowParams);

                                    Map<String, String> params = BeanUtils.convertMap(po, false);
                                    componentQueryString = UrlHelper.getUrlQueryString(params);
                                    System.out.println("=======> " + componentQueryString);
                                    ResultData reallyResultData = invokeMethod(controller, "list",
                                            new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath()),
                                                    java.lang.Class.forName(defPoExampleClass.getClassPath()), Pagination.class},
                                            new Object[]{po, poExample, pagination});
                                    Map baseTreeData = (Map) baseResultData.getData();
                                    final Map reallyData = CollectionUtils.convert((List) ((Map)reallyResultData.getData()).get("list"), new Mapper() {
                                        public String getKey(Object o) {
                                            return String.valueOf(ReflectUtils.getFieldValue(o, JavaUtil.getJavaVarName(relFieldName)));
                                        }
                                    });

                                    Map resultTreeData = new LinkedHashMap();
                                    for (Object parentObjectId : baseTreeData.keySet()) {
                                        String parentId = String.valueOf(parentObjectId);
                                        resultTreeData.put(parentId, CollectionUtils.fetch((List<Object>) baseTreeData.get(parentObjectId), new Fetcher<Object, Map<String, String>>() {
                                            public Map<String, String> fetch(Object o) {
                                                Map<String, String> map = BeanUtils.convertMapAndFormat(o, true);
                                                String keyPropertyValue = String.valueOf(ReflectUtils.getFieldValue(o, JavaUtil.getJavaVarName(dataSetDescriptor.getKeyField().getCode())));
                                                String namePropertyValue = String.valueOf(ReflectUtils.getFieldValue(o, JavaUtil.getJavaVarName(dataSetDescriptor.getNameField().getCode())));
                                                map.put("NAME_FIELD", namePropertyValue);
                                                map.put("KEY_FIELD", keyPropertyValue);
                                                map.remove(JavaUtil.getJavaVarName(dataSetDescriptor.getNameField().getCode()));
                                                if(reallyData.containsKey(String.valueOf(keyPropertyValue))) {
                                                    map.putAll(BeanUtils.convertMapAndFormat(reallyData.get(keyPropertyValue), true));
                                                }
                                                return map;
                                            }
                                        }));
                                    }
                                    resultData = ResultData.success(resultTreeData);
                                    break;
                                }
                            }
                        }


                    } else {
                        if (pagination.getPageNo() == 0) pagination.setPageNo(1);
                        if (pagination.getPageSize() == 0) pagination.setPageSize(10);
                        if ("eList".equals(type)) pagination.setPageSize(50);
                        po = getPoInstance(request, controller, action, new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath()),
                                java.lang.Class.forName(defPoExampleClass.getClassPath()), Pagination.class});
                        Map<String, String> pageFlowParams = WebContext.get(HashMap.class.getName());
                        ReflectUtils.setFieldValue(po, pageFlowParams);

                        Map<String, String> params = BeanUtils.convertMap(po, false);
                        componentQueryString = UrlHelper.getUrlQueryString(params);
                        System.out.println("=======> " + componentQueryString);
                        resultData = invokeMethod(controller, action,
                                new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath()),
                                        java.lang.Class.forName(defPoExampleClass.getClassPath()), Pagination.class},
                                new Object[]{po, poExample, pagination});
//                        resultData = (ResultData) ReflectUtils.invokeMethod(controller,action,
//                                new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath()),
//                                        java.lang.Class.forName(defPoExampleClass.getClassPath()), Pagination.class},
//                                new Object[]{po,poExample, pagination});

                        if (resultData.getData() instanceof Map) {
                            List helperData = getHelperData(extendData, componentDescriptor.getDataSetDescriptor(), action, defPoClass, request);
                            ((Map) resultData.getData()).put("helperData", helperData);
                        }
                    }

                        resetResultMessage(resultData, WebContext.get().getProgram().getCode(), moduleCode, dataSetCode, action);
                    if(resultData.isSuccess()) {
                        jsonObject = componentDescriptor.getJson(resultData);
                    }else {
                        jsonObject = componentDescriptor.getJson();
                        if(!(jsonObject.get("data") instanceof JSONArray)) {
                            jsonObject.put("data",JSONObject.toJSON(WebContext.get(HashMap.class.getName())));
                        }
                    }


                }else {
                    if("cList".equals(type)){
                        Class defPoClass = CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
                                WebContext.get().getProgram().getCode(), moduleCode, eventObjectCode);
                        List helperData = getHelperData(extendData, componentDescriptor.getDataSetDescriptor(), "list", defPoClass,request);
                        ResultData resultData = ResultData.success().add("helperData",helperData);
                        jsonObject = componentDescriptor.getJson(resultData);
                    }else {
                        jsonObject = componentDescriptor.getJson();
                    }

                    if(!(jsonObject.get("data") instanceof JSONArray)) {
                        jsonObject.put("data",JSONObject.toJSON(WebContext.get(HashMap.class.getName())));
                    }
                }
                if("list".equals(type) || "cList".equals(type) || "eList".equals(type) || "eTList".equals(type)) {
                    if(jsonObject.get("data") == null) {
                        jsonObject.put("data", new JSONArray());
                    }

                    if(((JSONArray) jsonObject.get("data")).size() == 0 &&  jsonObject.get("columns") != null ) {
                        jsonObject.put("dataIsEmpty","true");
                        int cnt = 0;
                        String[] defaultNullData = new String[((JSONArray) jsonObject.get("columns")).size()];
                        Map<String, String> pageFlowParams = WebContext.get(HashMap.class.getName());
                        for (Object columns : (JSONArray) jsonObject.get("columns")) {
                            if(pageFlowParams.containsKey(((JSONObject) columns).get("code"))) {
                                defaultNullData[cnt++] = pageFlowParams.get(((JSONObject) columns).get("code"));
                            }else {
                                defaultNullData[cnt++] = "";
                            }
                        }
//                        Arrays.fill(defaultNullData, "");
                        ((JSONArray) jsonObject.get("data")).add(defaultNullData);
                    }

//                    Collections.addAll(((JSONArray) jsonObject.get("data")), defaultNullData);
                }

                if("${icon}".equals(jsonObject.get("icon"))) {
                    if("list".equals(type)) {
                        jsonObject.put("icon","icon-align-justify");
                    }else if("qForm".equals(type)) {
                        jsonObject.put("icon","icon-search");
                    }else {
                        jsonObject.put("icon","icon-edit");
                    }

                }

                if(StringUtils.isNotBlank(componentDescriptor.getTitle())) {
                    jsonObject.put("title",componentDescriptor.getTitle());
                }
                jsonObject.put("showTitle",componentDescriptor.isShowTitle());

                mergeGlobalRuler(globalDataSetRulerJsonObject, componentDescriptor.getDataSetDescriptor().getDataSetRulerJsonObject());
//                jsonObject.put("icon","icon-edit");
                        jsonObject.put("ruler", componentDescriptor.getDataSetDescriptor().getDataSetRulerJsonObject().toJSONString());


                AuthContext authContext = authServiceProxy.getAuthContext(request);
                if(authContext != null) {
                    Long funcId = authContext.getAuthFunctionManager().get("/" + module + "/" + pageCode + ".html");
                    Map<AuthContext.AuthDataUnit, String> authDataUnitStringMap = authContext.getAuthManager().getEventAuth().get(funcId);
                    if(authDataUnitStringMap != null && authDataUnitStringMap.size() > 0) {
                        removeNonAuthEvent(authDataUnitStringMap, jsonObject, "EOFR");
                        removeNonAuthEvent(authDataUnitStringMap, jsonObject, "EOF");
                        removeNonAuthEvent(authDataUnitStringMap, jsonObject, "BOF");
                    }
                }


                jsonObject.put("helper",componentDescriptor.getDataSetDescriptor().getDynamicHelper());
                jsonObject.put("dc",componentDescriptor.getDataSetDescriptor().getDataSet().getCode());

                jsonObject.put("vmpath", StringUtils.isNotBlank(componentDescriptor.getComponent().getPath()) ?
                        componentDescriptor.getComponent().getPath() : componentDescriptor.getPath());
                jsonObject.put("isDefaultComponent", componentDescriptor.isDefaultComponent());
                jsonObject.put("module",module);
                jsonObject.put("page",pageCode);
                jsonObject.put("param", componentQueryString);
                jsonObject.put("component", componentDescriptor.getId());
                jsonObject.put("container", componentDescriptor.getElement().getContainer());
                jsonObject.put("group", componentDescriptor.getElement().getElementGroup());
                if("treeChart".equals(componentDescriptor.getId())) {
                    jsonObject.put("id", "-1");
                    jsonObject.put("name","根节点");
                }

                String key = componentDescriptor.getId();
                if(result.containsKey(key)) {
                    key = componentDescriptor.getId() + "|" + componentDescriptor.getDataSetDescriptor().getDataSet().getCode() + "|" + componentDescriptor.getDataId();
                }
                System.out.println("=====>" + key + " : " + jsonObject.toJSONString());
                result.put(key, jsonObject);
            }
        }

        if(request.getRequestURI() != null && !request.getRequestURI().contains("login.html") && !request.getRequestURI().contains("logout.html")) {
            mav.addObject("loginFowardUrl", request.getRequestURI()+"?" + request.getQueryString());
        }else {
            mav.addObject("loginFowardUrl","/index.html");

        }
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
            mav.setViewName(pageInfo.getPageTemplate().getPath().substring(0,pageInfo.getPageTemplate().getPath().indexOf(".vm")));
        }

        return mav;

    }

    private JSONObject parseFileComponent(String type, final HttpServletRequest request, HttpServletResponse response,
                                    String dataSetCode, String module, ComponentDescriptor componentDescriptor,
                                    ModelAndView mav) throws Throwable {
        JSONObject jsonObject = null;
        if("container".equals(type)) {
            String dataIdStr = componentDescriptor.getDataId();
            if(StringUtils.isNotBlank(dataIdStr)) {
                Element rootElement = null;
                String xmlContent = null;
                if(StringUtils.isNotBlank(request.getParameter("id"))) {
                    if(dataIdStr.startsWith("DATA-SET-REL://")){
                        String[]  tableInfo = dataIdStr.substring("DATA-SET-REL://".length()).split("/");
                        final String tableName = tableInfo[0];
                        String xmlField = tableInfo[1];
                        final String keyField = tableInfo[2];
                        Map<String, Object> map = commonDataService.selectDynamicTableDataOne(new HashMap() {{
                            put("tableName", tableName);
                            put("condition", keyField + " =" + request.getParameter("id"));
                        }});

                        if(map.get(xmlField) != null) {
                            if(map.get(xmlField).getClass().isArray()){
                                xmlContent = new String((byte[])map.get(xmlField));
                            }else {
                                xmlContent = String.valueOf(map.get(xmlField));
                            }
                        }

                    }else {
                        String filePath = dataIdStr + "/" + request.getParameter("id");
                        xmlContent =  FileUtils.readFile(filePath);
                    }

                    xmlContent = invokeEmbedPart(xmlContent);

                    if(StringUtils.isNotBlank(xmlContent)) {
                        rootElement = Dom4jUtils.getDocumentByContent(xmlContent).getRootElement();
                    }
                }
                if(rootElement == null) {
                    rootElement = new DefaultElement("a");
                }

                DataSetContainer rootContainer = createRootContainer(componentDescriptor, rootElement, module);

                String helperDataXml = componentDescriptor.getDataSetDescriptor().getHelperDataXml();
                if(StringUtils.isBlank(helperDataXml)) helperDataXml = "<xml></xml>";
                Element helperElement = Dom4jUtils.getDocumentByContent(helperDataXml).getRootElement();
                DataSetContainer helperContainer = createRootContainer(componentDescriptor, helperElement, module);

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

//                            WebContext.put("fileContent", rootContainer);
//                            WebContext.put("subContext", true);
                System.out.println(rootContainer);

//                            XmlNode xmlNodes = BeanGeneratorUtil.parseXmlNodeData();
//                            xmlNodes.settingNodeCode();
////                            IDataSet iDataSet = groupXmlNodes(xmlNodes);
//
//                            System.out.println(xmlNodes);
            }

            jsonObject = componentDescriptor.getJson();
            ModelAndView subResult = gotoPage(module, componentDescriptor.getDataSetDescriptor().getDataSet().getCode() + "#",
                    null, null, "true", request, response);
            jsonObject.put("modelMap",subResult.getModelMap());
            jsonObject.put("view",subResult.getViewName());
//                        jsonObject.put("data",WebContext.get("fileContent"));
        }else if(StringUtils.isNotBlank(componentDescriptor.getDataId())) {
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

            jsonObject = componentDescriptor.getJson(ResultData.success(group));
        }else {
            jsonObject = componentDescriptor.getJson();
        }

        if(!(jsonObject.get("data") instanceof JSONArray)) {
            jsonObject.put("data",JSONObject.toJSON(WebContext.get(HashMap.class.getName())));
        }

        jsonObject.put("dataSet", dataSetCode.contains("#") ?dataSetCode.substring(dataSetCode.indexOf("#") + 1) : dataSetCode);
        return jsonObject;
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

    private DataSetContainer createRootContainer(ComponentDescriptor componentDescriptor, Element rootElement, String module) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
//        DataSetContainer rootContainer = (DataSetContainer) org.apache.commons.beanutils.BeanUtils.cloneBean(
//                componentDescriptor.getDataSetDescriptor().getDateSetStruct());
        //TODO 需要确认深度拷贝是否完整
        DataSetContainer rootContainer = (DataSetContainer) componentDescriptor.getDataSetDescriptor().getDateSetStruct().cloneBean();
        DataSetGroup rootDataSetGroup = new DataSetGroup();
        rootContainer.addDataGroup(rootDataSetGroup);
        rootDataSetGroup.setNode(rootContainer.getNode());
        rootDataSetGroup.setElementList(CollectionUtils.copy(rootContainer.getElementList()));


        setDataSetContainerValue(rootDataSetGroup, rootElement);

        setDataSetInstanceComponentData(rootContainer, module, componentDescriptor.getDataSetDescriptor().getDataSet().getCode(), "");
        return rootContainer;
    }

    private void removeNonAuthEvent(Map<AuthContext.AuthDataUnit, String> authDataUnitStringMap, JSONObject jsonObject, String eventType) {
        JSONArray events = jsonObject.getJSONArray(eventType);
        JSONArray newArray = new JSONArray();
        if(events == null) return ;
        for (Object event : events) {
            String name = ((JSONObject) event).getString("name");
            boolean contain = false;
            for (Map.Entry<AuthContext.AuthDataUnit, String> authDataUnitStringEntry : authDataUnitStringMap.entrySet()) {
                if(("," + authDataUnitStringEntry.getValue() + ",").contains("," + name + ",")) {
                    contain = true;
                    break;
                }
            }

            if(contain) {
                newArray.add(event);
            }
        }
        jsonObject.put(eventType, newArray);
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

    private void setDataSetInstanceComponentData(DataSetContainer rootContainer, String module,String parentDataSetCode, String subPageCode) {
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

    private void setDataSetInstanceComponentData(PageDescriptor subPageInfo, final DataSetInstance dataSet) {
        for (ComponentDescriptor descriptor : subPageInfo.getComponents().values()) {
            if(descriptor.isDefaultComponent()) continue;
            if(descriptor.getDataSetDescriptor() != null) {
                String tempDataSetCode = descriptor.getDataSetDescriptor().getDataSet().getCode();
                tempDataSetCode = tempDataSetCode.contains("#") ? tempDataSetCode.substring(tempDataSetCode.indexOf("#") + 1) : tempDataSetCode;
                if(tempDataSetCode.equals( dataSet.getNode().getPath())) {
                    if(dataSet.isOne() || dataSet.getOne() != null) {
                        dataSet.setComponentData(descriptor.getJson(ResultData.success(dataSet.getOne())));
                    }else {
                        dataSet.setComponentData(descriptor.getJson(ResultData.success(new HashMap() {{
                            put("list", dataSet.getList());
                        }})));
                        dataSet.getComponentData().put("dataIsEmpty", dataSet.getList().size()==1 &&  dataSet.getList().get(0).isEmpty());
                    }
                }
            }
        }
    }

    private void setDataSetContainerValue(DataSetGroup parentDataSetGroup, Element parentElement)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        Set<String> excludeCode = getExcludeCode(parentDataSetGroup.getElementList());

        for (IDataSet iDataSet : parentDataSetGroup.getElementList()) {//容器、组件循环
            String path = iDataSet.getNode().getPath();
            LinkedList<Element> eleList = findElementsByPath(parentElement, path); //满足条件的数据元素
            if(eleList == null || eleList.size() == 0) {
                logger.warn("xml node [{}] is not exists !", path);
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
                        setDataSetContainerValue(dataSetGroup, tarElement);
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

    private Set<String> getExcludeCode(List<IDataSet> elementList) {
        Set<String> set = new HashSet<String>();
        for (IDataSet iDataSet : elementList) {
            if (iDataSet instanceof DataSetContainer) {
                DataSetContainer dataSetContainer = (DataSetContainer) iDataSet;
                set.add(dataSetContainer.getNode().getPath());

            }
        }
        return set;
    }

    private void addRowData(Map<String, String> oneRowData, Element element, String parentPath, Set<String> excludeCode, Set<String> includeCode) {
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

    private LinkedList<Element> findElementsByPath(final Element rootElement, String path) {
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
                eleMap.put(ele.getPath().replace("/",".").substring(1), new LinkedList<Element>());
            }
            eleMap.get(ele.getPath().replace("/",".").substring(1)).add(ele);
        }
        return eleMap;
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

    private List getHelperData(Map<String, Object> extendData , DataSetDescriptor dataSetDescriptor, String action, Class targetPoClass, HttpServletRequest request) {
        Map<String, String> pageFlowParams = WebContext.get(HashMap.class.getName());
        List helperDataList = extendData == null ? null : (List) extendData.get("HELPER");
        List helperPoList = new ArrayList();
        List<DataSetHelper> dataSetHelpers = dataSetDescriptor.getDataSetHelpers();
        nextHelper : for (DataSetHelper dataSetHelper : dataSetHelpers) {
            try {

                if(extendData == null || extendData.get("HELPER") ==null) {
                    String helpModule = dataSetHelper.getHelpModule();
                    String helpDataset = dataSetHelper.getHelpDataset();
                    String helpDatascore = dataSetHelper.getHelpDatascore();

                    Class defPoClass = CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
                            WebContext.get().getProgram().getCode(), helpModule, helpDataset);
                    Class defPoExampleClass = CreatorUtil.getDefPoExampleClass(WebContext.get().getProgram().getCompany(),
                            WebContext.get().getProgram().getCode(), helpModule, helpDataset);
                    Class defControllerClass = CreatorUtil.getDefControllerClass(WebContext.get().getProgram().getCompany(),
                            WebContext.get().getProgram().getCode(), helpModule, helpDataset);
                    Object po = java.lang.Class.forName(defPoClass.getClassPath()).newInstance();
                    Object poExample= java.lang.Class.forName(defPoExampleClass.getClassPath()).newInstance();
                    Pagination pagination = new Pagination();
                    Object controller= ServiceFactory.getService(defControllerClass.getClassName().substring(0, 1).toLowerCase() + defControllerClass.getClassName().substring(1));

                    ReflectUtils.setFieldValue(po, pageFlowParams);

                    if(StringUtils.isNotBlank(helpDatascore)) {
                        Map<String, String> condition = DataSetDescriptor.getConditionMap(helpDatascore);
                        for (String propertyName : condition.keySet()) {
                            String propertyValue = condition.get(propertyName);
                            if(!propertyValue.startsWith("{") && !propertyValue.endsWith("}") ) {
                                ReflectUtils.setFieldValue(po, propertyName.trim(), propertyValue);
                                continue ;
                            }
                            propertyValue = propertyValue.substring(1, propertyValue.length() - 1);

                            //组件连带刷新时需要传入所选择的值
                            String parameterValue = request.getParameter(propertyValue);
                            if(parameterValue != null) {
                                if(StringUtils.isNotBlank(parameterValue)) {
                                    ReflectUtils.setFieldValue(po, propertyName.trim(), parameterValue);
                                }
                                continue;
                            }

                            String referDataSetCode = propertyValue.substring(0, propertyValue.indexOf("/"));
                            String referDataFiledCode = propertyValue.substring(propertyValue.indexOf("/") + 1);
                            Class referClass =
                                    CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
                                            WebContext.get().getProgram().getCode(), "hframe", referDataSetCode);
                            Object relPo = WebContext.get(java.lang.Class.forName(referClass.getClassPath()).getName());
                            if(relPo != null) {
                                Object fieldValue = ReflectUtils.getFieldValue(relPo, JavaUtil.getJavaVarName(referDataFiledCode));
                                ReflectUtils.setFieldValue(po, propertyName.trim(), fieldValue);
                            }else {
                                continue nextHelper;
                            }
                        }
                    }

                    Map<String, String> params = BeanUtils.convertMap(po, false);
                    String componentQueryString = UrlHelper.getUrlQueryString(params);
                    System.out.println("=======> " + componentQueryString);
                    PropertyDescriptor priPropertyDescriptor = org.springframework.beans.BeanUtils.getPropertyDescriptor(java.lang.Class.forName(defPoClass.getClassPath()), "pri");

                    if(priPropertyDescriptor != null) {
                        ReflectUtils.invokeMethod(poExample, "setOrderByClause", new java.lang.Class[]{String.class}, new Object[]{" pri asc"});
                    }
                    ResultData helperData = invokeMethod(controller, action,
                            new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath()),
                                    java.lang.Class.forName(defPoExampleClass.getClassPath()), Pagination.class},
                            new Object[]{po, poExample, pagination});
                    helperDataList = (List) ((Map) helperData.getData()).get("list");
                }


                for (Object helpPo : helperDataList) {
                    Object targetPo = java.lang.Class.forName(targetPoClass.getClassPath()).newInstance();
                    Mappings mappings = dataSetHelper.getMappings();
                    for (com.hframework.web.config.bean.datasethelper.Mapping mapping : mappings.getMappingList()) {
                        String helpDatasetField = mapping.getHelpDatasetField();
                        String effectDatasetField = mapping.getEffectDatasetField();
                        String express = mapping.getExpress();

                        String propertyValue = org.apache.commons.beanutils.BeanUtils.getProperty(helpPo, JavaUtil.getJavaVarName(helpDatasetField));
                        if(StringUtils.isNotBlank(express)) {
                            if(express.startsWith("*.replace(")) {
                                String originChars = express.substring("*.replace(".length(), express.indexOf(","));
                                String targetChars = express.substring(express.indexOf(",") + 1, express.indexOf(")"));
                                propertyValue = propertyValue.replace(originChars, targetChars);
                            }else {
                                propertyValue = express.contains("*") ? express.replace("*",propertyValue) : express;
                            }
                        }
                        org.apache.commons.beanutils.BeanUtils.setProperty(targetPo, JavaUtil.getJavaVarName(effectDatasetField), propertyValue);
                    }

                    ReflectUtils.setFieldValue(targetPo, pageFlowParams);
                    helperPoList.add(targetPo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return helperPoList;
    }



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

    private Object getPoInstance(HttpServletRequest request, Object controller, String action, java.lang.Class[] defPoClass) throws ClassNotFoundException {
        //                    Object po= java.lang.Class.forName(defPoClass.getClassPath()).newInstance();

        Method declaredMethod = ReflectUtils.getDeclaredMethod(controller, action, defPoClass);
        if(declaredMethod == null) {
            logger.warn("{}", controller,action,defPoClass);
        }
        if(modelAttributeSetter == null) modelAttributeSetter = new ModelAttributeSetter();
        Object po = modelAttributeSetter.resolveArgument(request, new MethodParameter(declaredMethod, 0));
        return po;
    }

    private void resetResultMessage(ResultData resultData, String programCode, String moduleCode, String dataSetCode, String action) {
        String resourceKey = programCode + "." + moduleCode + "." + dataSetCode + "." + action + resultData.getResultCode();
        resourceKey = programCode + "." + moduleCode + "." + dataSetCode + "." + action + resultData.getResultCode();
        resourceKey = action + resultData.getResultCode();
        resourceKey = resultData.getResultCode();
        resultData.setResultMessage("TODO");
    }

    public  class ModelAttributeSetter{
        private ServletModelAttributeMethodProcessor processor;

        private ModelAndViewContainer mavContainer;

        private WebDataBinderFactory binderFactory;

        public ModelAttributeSetter() {
            init();
        }

        public void init() {
            this.processor = new ServletModelAttributeMethodProcessor(false);

            //调用spring-mvc中的配置的WebBindingInitializer
            this.binderFactory = new ServletRequestDataBinderFactory(null, initializer);
            this.mavContainer = new ModelAndViewContainer();
        }

        public Object resolveArgument(HttpServletRequest request, MethodParameter methodParameter) {
            NativeWebRequest webRequest = new ServletWebRequest(request);

            try {
                return this.processor.resolveArgument(
                        methodParameter,  new ModelAndViewContainer(), webRequest, this.binderFactory);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
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

    private Map<String, Object> getExtendData(String url , HttpServletRequest request) {
        HandlerExecutionChain handler = null;
        if(urlMapping.size() == 0) {
            synchronized (DefaultController.class){
                Map<RequestMappingInfo, HandlerMethod> map = ServiceFactory.getService(RequestMappingHandlerMapping.class).getHandlerMethods();
                for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
                    RequestMappingInfo info = m.getKey();
                    HandlerMethod method = m.getValue();
                    urlMapping.put(info.getPatternsCondition().getPatterns().iterator().next(), method);
                }
            }
        }

        HandlerMethod handlerMethod = urlMapping.get(url);
        if(handlerMethod == null) {
            return null;
        }

        try {
            Object bean = ServiceFactory.getService(String.valueOf(handlerMethod.getBean()));
            Method method = handlerMethod.getMethod();
            Object result = method.invoke(bean, request);
            return (Map<String, Object>)((ResultData)result).getData();
//            String lookupPath = requestMappingHandlerMapping.getUrlPathHelper().getLookupPathForRequest(request);
//            HandlerMethod handlerMethod = requestMappingHandlerMapping.lookupHandlerMethod(lookupPath, request);
//            handler = requestMappingHandlerMapping.getHandler(request);
//            if(handler.getHandler() != null && handler.getHandler() instanceof HandlerMethod) {
//                HandlerMethod handlerMethod = (HandlerMethod) handler.getHandler();
//                Object bean = handlerMethod.getBean();
//                Method method = handlerMethod.getMethod();
//                Object result = method.invoke(bean, request);
//                return result;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 页面跳转
     * @return
     * @throws Throwable
     */
    @RequestMapping(value = "/editor-app/editor.html")
    public ModelAndView editor(@ModelAttribute("modelId") String modelId,

                               HttpServletRequest request, HttpServletResponse response) throws Throwable {
        ModelAndView mav = new ModelAndView();
        mav.addObject("modelId",modelId);
        mav.setViewName("/editor");
        return mav;
    }

    /**
     * 页面跳转
     * @return
     * @throws Throwable
     */
    @RequestMapping(value = "/diagram-viewer/forwarder.html")
    public ModelAndView diagramForwarder(@ModelAttribute("_DS") String dataSet, @ModelAttribute("_DI") String dataId,

                                         HttpServletRequest request, HttpServletResponse response) throws Throwable {
        ModelAndView mav = new ModelAndView();
        String processKey = String.valueOf(WebContext.get().getProcess(dataSet)[1]);
        ProcessDefinition processDefinition = ProcessEngines.getDefaultProcessEngine().getRepositoryService().
                createProcessDefinitionQuery().processDefinitionKey(processKey).singleResult();
        String processDefinitionId = "", processInstanceId = "";
        if(processDefinition != null) {
            processDefinitionId = processDefinition.getId();
            HistoricProcessInstance historicProcessInstance = ProcessEngines.getDefaultProcessEngine().getHistoryService()
                    .createHistoricProcessInstanceQuery().processInstanceBusinessKey(dataId)
                    .processDefinitionId(processDefinition.getId())
                    .singleResult();
            if(historicProcessInstance != null) {

                processInstanceId = historicProcessInstance.getId();
            }
        }
        mav.setViewName("redirect:/diagram-viewer/index.html?processDefinitionId="
                + processDefinitionId + "&processInstanceId=" + processInstanceId);
        return mav;
    }

    /**
     * 页面跳转
     * @return
     * @throws Throwable
     */
    @RequestMapping(value = "/diagram-viewer/index.html")
    public ModelAndView diagramViewer(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        ModelAndView mav = new ModelAndView();
//                List<HistoricProcessInstance> processInstances = historyService .createHistoricProcessInstanceQuery() .startedBy(ExplorerApp.get().getLoggedInUser().getId()) .unfinished() .list();
        String processInstanceId = request.getParameter("processInstanceId");
        if(StringUtils.isNotBlank(processInstanceId)) {
            //自己创建的未完成流程单
            List<HistoricTaskInstance> tasks = ProcessEngines.getDefaultProcessEngine().getHistoryService().createHistoricTaskInstanceQuery()
                    .processInstanceId(processInstanceId)
//                    .orderByHistoricTaskInstanceEndTime().desc()
                    .orderByHistoricTaskInstanceStartTime().desc()
                    .list();

            List<Map<String, Object>> taskDisplays = new ArrayList<Map<String, Object>>();
            if(tasks != null) {
                ServiceFactory.getService(ExplorerApp.class).onRequestStart(request, response);
                for (HistoricTaskInstance task : tasks) {

                    Map<String ,Object> item = new HashMap<String, Object>();
                    taskDisplays.add(item);

                    if(task.getEndTime() != null) {
                        item.put("finished", "/VAADIN/themes/activiti/" + new Embedded(null, Images.TASK_FINISHED_22).getSource());
                    } else {
                        item.put("finished","/VAADIN/themes/activiti/" + new Embedded(null, Images.TASK_22).getSource());
                    }

                    item.put("name", task.getName());
                    item.put("priority",task.getPriority());

                    item.put("startDate", new PrettyTimeLabel(task.getStartTime(), true));
                    item.put("endDate", new PrettyTimeLabel(task.getEndTime(), true));

                    if(task.getDueDate() != null) {
                        Label dueDateLabel = new PrettyTimeLabel(task.getEndTime(), "尚未完成", true);
                        item.put("dueDate", dueDateLabel);
                    }



                    if(task.getAssignee() != null) {
                        UserProfileLink taskAssigneeComponent = new UserProfileLink(ProcessEngines.getDefaultProcessEngine().getIdentityService(), true, task.getAssignee());
                        if(taskAssigneeComponent != null) {
                            item.put("assigneeName", ((Button) taskAssigneeComponent.getComponent(1)).getCaption());
                            item.put("assigneePhoto","data:image/gif;base64," +
                                    new BASE64Encoder().encode(IoUtil.readInputStream(((StreamResource) ((Embedded) taskAssigneeComponent.getComponent(0)).getSource()).getStreamSource().getStream(), "")));
                        }
                    }
                }
            }

            mav.addObject("tasks", taskDisplays);
        }


        mav.setViewName("/diagram-viewer/index");
        return mav;
    }

}
