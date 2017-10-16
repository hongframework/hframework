package com.hframework.web.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.hframework.base.bean.KVBean;
import com.hframework.base.service.CommonDataService;
import com.hframework.beans.controller.ResultCode;
import com.hframework.beans.controller.ResultData;
import com.hframework.common.frame.ServiceFactory;
import com.hframework.common.frame.cache.CacheFactory;
import com.hframework.common.springext.datasource.DataSourceContextHolder;
import com.hframework.common.util.JavaUtil;
import com.hframework.common.util.ReflectUtils;
import com.hframework.common.util.RegexUtils;
import com.hframework.common.util.collect.CollectionUtils;
import com.hframework.common.util.collect.bean.Grouper;
import com.hframework.common.util.collect.bean.Mapper;
import com.hframework.common.util.collect.bean.Mapping;
import com.hframework.common.util.message.JsonUtils;
import com.hframework.web.CreatorUtil;
import com.hframework.web.auth.AuthContext;
import com.hframework.web.auth.AuthServiceProxy;
import com.hframework.web.config.bean.Program;
import com.hframework.web.config.bean.component.AppendElement;
import com.hframework.web.config.bean.component.Effect;
import com.hframework.web.config.bean.component.Event;
import com.hframework.web.context.ComponentDescriptor;
import com.hframework.web.context.DataSetDescriptor;
import com.hframework.web.context.PageDescriptor;
import com.hframework.web.context.WebContext;
import com.hframework.web.interceptor.CacheHandlerInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by zhangquanhong on 2017/2/10.
 */
@Controller("HFrameDictionaryController")
public class DictionaryController extends AbstractController{
    private static final Logger logger = LoggerFactory.getLogger(DictionaryController.class);

    @Resource
    private AuthServiceProxy authServiceProxy;

    @Resource
    private CommonDataService commonDataService;

    /**
     * 通过功能id获取所有的事件信息
      * @param request
     * @return
     */
    @RequestMapping(value = "/frame/getEventsByFuncId.json")
    @ResponseBody
    public ResultData getEventsByFuncId(HttpServletRequest request) throws Exception {

        List<Event> allEventList = new ArrayList<Event>();

        Map<String, String> parameters = parseParameterForDataCondition(request);
        WebContext webContext = WebContext.get();
        AuthContext authContext = authServiceProxy.getAuthContext(request);
        List<Class> authFunctionClass = authContext.getAuthManager().getAuthFunctionClass();

        String functionId = null;
        if(authFunctionClass != null) {
            for (Class authFunctionClass1 : authFunctionClass) {
                DataSetDescriptor dataSet = webContext.getDataSet(authFunctionClass1);
                String keyPropertyName = dataSet.getKeyField().getCode();
                if(parameters.containsKey(keyPropertyName)) {
                    functionId = parameters.get(keyPropertyName);
                    break;
                }else if(parameters.containsKey(JavaUtil.getJavaVarName(keyPropertyName))){
                    functionId = parameters.get(JavaUtil.getJavaVarName(keyPropertyName));
                    break;
                }
            }
        }

        if(StringUtils.isNoneBlank(functionId)) {
            for (Map.Entry<String, Long> funcIdAndUrl : authContext.getAuthFunctionManager().entrySet()) {
                if(funcIdAndUrl.getValue().equals(Long.valueOf(functionId))) {
                    String url = funcIdAndUrl.getKey();
                    if(url.matches("/[a-zA-Z1-9_]+/[a-zA-Z1-9_]+.html")) {
                        String module = url.substring(url.indexOf("/") + 1, url.lastIndexOf("/"));
                        String page = url.substring(url.lastIndexOf("/") + 1, url.indexOf(".html"));
                        PageDescriptor pageInfo = webContext.getPageInfo(module, page);
                        Collection<ComponentDescriptor> components = pageInfo.getComponents().values();

                        for (ComponentDescriptor component : components) {
                            if(component.getDataContainer() != null) {
                                allEventList.addAll(component.getDataContainer().getAllEvent());
                            }
                        }
                    }
                }
            }
        }

        final Set<String> keySet = new HashSet<String>();
        List<KVBean> kvBeans = CollectionUtils.from(allEventList, new Mapping<Event, KVBean>() {
            public KVBean from(Event event) {
                boolean isMod = false;
                if (event.getEffectList() != null) {
                    for (Effect effect : event.getEffectList()) {
                        if (!"component.reload".equals(effect.getType()) && !"page.reload".equals(effect.getType())) {
                            isMod = true;
                        }
                    }
                }

                if (!isMod) return null;
//                if(keySet.contains(event.getName())) {
//                    return null;
//                }
                keySet.add(event.getName());

                String text = event.getDescription();
                if(StringUtils.isBlank(text) && event.getAttach() != null && event.getAttach().getAppendElementList() != null) {
                    for (AppendElement appendElement : event.getAttach().getAppendElementList()) {
                        String param = appendElement.getParam();
                        if(StringUtils.isNoneBlank(param)) {
                            JSONObject jsonObject = JSONObject.parseObject(param);
                            if(jsonObject.containsKey("btnText")) {
                                text = jsonObject.getString("btnText");
                            }
                        }

                    }
                }



                KVBean kvBean = new KVBean();
                kvBean.setText(text);
                kvBean.setValue(event.getName());
                return kvBean;
            }
        });
        Iterator<KVBean> iterator = kvBeans.iterator();
        while (iterator.hasNext()) {
            if(iterator.next() == null) {
                iterator.remove();
            }
        }

        return ResultData.success(kvBeans);
    }

    /**
     * 字典查询
     * @param dataCodes
     * @param dataCondition
     * @return
     */
    @RequestMapping(value = "/treeData.json")
    @ResponseBody
    public ResultData treeData(@ModelAttribute("dataCode") String dataCodes ,
                               @ModelAttribute("dataCondition") final String dataCondition, @ModelAttribute("dataValue") String dataValue){
        logger.debug("request : {}", dataCodes, dataCondition);
        JSONObject treeData = new JSONObject();
        String dataDisplayValue = null;
        try{
            final String[] dataCodeArray = dataCodes.split(";");
            Map<String, KVBean> cache = new LinkedHashMap<String, KVBean>();
            int nextLevel = 1;
            for (String dataCode : dataCodeArray) {
                final int curlLevel = nextLevel ++;
                ResultData dictionary = dictionary(dataCode, dataCondition);
                List<KVBean> kvBeans = (List<KVBean>) dictionary.getData();
                cache.putAll(CollectionUtils.convert(kvBeans, new Mapper<String, KVBean>() {
                    public <K> K getKey(KVBean kvBean) {
                        return (K)(curlLevel + "_" +  kvBean.getValue());
                    }
                }));
                if(treeData.isEmpty()) {
                    JSONArray jsonArray = new JSONArray();
                    if(kvBeans != null) {
                        for (KVBean kvBean : kvBeans) {
                            JSONObject recode = new JSONObject();
                            recode.put("code",kvBean.getValue());
                            recode.put("address",kvBean.getText());
                            jsonArray.add(recode);
                        }
                    }

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("", jsonArray);
                    treeData.put("86", jsonObject);
                }else {
                    Map<String, List<KVBean>> group = CollectionUtils.group(kvBeans, new Grouper<String, KVBean>() {
                        public <K> K groupKey(KVBean kvBean) {
                            return (K) kvBean.getExtInfo();
                        }
                    });
                    for (Map.Entry<String, List<KVBean>> entry : group.entrySet()) {
                        Map<String, String> recode = new LinkedHashMap<String, String>();
                        if(com.hframework.common.util.StringUtils.isBlank(entry.getKey())) {//存在垃圾数据时
                            continue;
                        }
                        for (KVBean kvBean : entry.getValue()) {
                            if(kvBean.getValue().equals(dataValue)) {
                                dataDisplayValue = kvBean.getText();
                                String parentId = kvBean.getExtInfo();
                                int parentLevel = 1;
                                while (cache.containsKey((curlLevel - parentLevel) + "_" + parentId)) {
                                    dataDisplayValue = cache.get((curlLevel - parentLevel) + "_" +parentId).getText() + "/" + dataDisplayValue;
                                    parentId = cache.get((curlLevel - parentLevel) + "_" +parentId).getExtInfo();
                                    parentLevel++;
                                }
                            }
                            recode.put(kvBean.getValue(), kvBean.getText());
                        }
                        treeData.put(entry.getKey(), recode);
                    }
                }
            }
            logger.debug("result json : {}", treeData.toJSONString());
            return ResultData.success().add("data", treeData).add("disValue", dataDisplayValue);
        }catch (Exception e) {
            logger.error("error : ", e);
            return ResultData.error(ResultCode.ERROR);
        }
    }

    /**
     * 字典查询
     * @param dataCode
     * @param dataValues
     * @return
     */
    @RequestMapping(value = "/getTexts.json")
    @ResponseBody
    public ResultData getTexts(@ModelAttribute("dataCode") String dataCode ,
                               @RequestParam(value="dataValues[]",required=false) final Set<String> dataValues){
        logger.debug("request : {}", dataCode, dataValues);
        DataSourceContextHolder.clear();
        try{
            if(dataValues == null || dataValues.size() == 0) {
                return ResultData.success();
            }
            List<KVBean> kvBeans = null;
            final String[] split = dataCode.split("\\.");
            if(split.length < 3) {
                Program program = WebContext.get().getProgram();
                String dictionarys = program.getAuthInstance().getDictionary();
                String[] dictionaryArray =RegexUtils.split(dictionarys, "[ ]*[/]+[ ]*");

                Object dictionaryId = getIdByDataSetAndCode(dictionaryArray[0], dataCode);
                if(dictionaryId != null) {
                    String moduleCode = dictionaryArray[0].substring(0, dictionaryArray[0].indexOf("."));
                    String dataSetCode = dictionaryArray[0].substring(dictionaryArray[0].indexOf(".") + 1);

                    Class<?> parentDicPoClass = Class.forName(CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
                            WebContext.get().getProgram().getCode(), moduleCode, dataSetCode).getClassPath());
                    kvBeans = getListByDataSetAndCode(dictionaryArray[1], parentDicPoClass, dictionaryId);
                }
            }else {
                Map<String, String> dicInfo = new HashMap<String, String>(){{
                    put("tableName", split[0]);
                    put("keyColumn", split[1]);
                    put("valueColumn", split[2]);
                    if(split.length > 3) {
                        put("extColumn", split[3]);
                    }
                    put("condition", split[1] + " in (" + Joiner.on(",").join(dataValues) + ")");
                }};


                DataSetDescriptor onlyDataSetDescriptor = WebContext.get().getOnlyDataSetDescriptor(split[0]);
                String moduleCode = onlyDataSetDescriptor.getDataSet().getModule();
                String dataSetCode = onlyDataSetDescriptor.getDataSet().getCode();
                Class<?> dicServiceImplClass = Class.forName(CreatorUtil.getDefServiceImplClass(WebContext.get().getProgram().getCompany(),
                        WebContext.get().getProgram().getCode(), moduleCode, dataSetCode).getClassPath());
                Object cacheObject = CacheFactory.get(dicServiceImplClass, JsonUtils.writeValueAsString(dicInfo));
                if(cacheObject != null && (cacheObject instanceof List)) {
                    kvBeans = (List<KVBean>) cacheObject;
                }else {
                    kvBeans = commonDataService.selectDynamicTableDataList(dicInfo);
                    CacheFactory.put(dicServiceImplClass, JsonUtils.writeValueAsString(dicInfo), kvBeans);
                }

            }
            Map<String, KVBean> convert = CollectionUtils.convert(kvBeans, new Mapper<String, KVBean>() {
                public <K> K getKey(KVBean kvBean) {
                    return (K) kvBean.getValue();
                }
            });
            return ResultData.success(convert);
        }catch (Exception e) {
            logger.error("error : ", e);
            return ResultData.error(ResultCode.ERROR);
        }
    }

    /**
     * 字典查询
     * @param dataCode
     * @param dataCondition
     * @return
     */
    @RequestMapping(value = "/dictionary.json")
    @ResponseBody
    public ResultData dictionary(@ModelAttribute("dataCode") String dataCode ,
                                 @ModelAttribute("dataCondition") String dataCondition){
        logger.debug("request : {}", dataCode, dataCondition);
        DataSourceContextHolder.clear();
        try{
            final String[] split = dataCode.split("\\.");
            if(split.length < 3) {
                Program program = WebContext.get().getProgram();
                String dictionarys = program.getAuthInstance().getDictionary();
                String[] dictionaryArray =RegexUtils.split(dictionarys, "[ ]*[/]+[ ]*");

                Object dictionaryId = getIdByDataSetAndCode(dictionaryArray[0], dataCode);
                if(dictionaryId != null) {
                    String moduleCode = dictionaryArray[0].substring(0, dictionaryArray[0].indexOf("."));
                    String dataSetCode = dictionaryArray[0].substring(dictionaryArray[0].indexOf(".") + 1);

                    Class<?> parentDicPoClass = Class.forName(CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
                            WebContext.get().getProgram().getCode(), moduleCode, dataSetCode).getClassPath());
                    List<KVBean> kvBeans = getListByDataSetAndCode(dictionaryArray[1], parentDicPoClass, dictionaryId);
                    if(kvBeans != null) {
                        return ResultData.success(kvBeans);
                    }
                }
                return ResultData.error(ResultCode.RECODE_IS_NOT_EXISTS);

            }else {
                if(!validDataCondition(dataCondition)) {
                    logger.warn("dataCode : [{}], dataCondition : [{}] invalid !", dataCode, dataCondition);
                    return ResultData.success();
                }
                DataSetDescriptor dataSetDescriptor = WebContext.get().getOnlyDataSetDescriptor(split[0]);
                if(dataSetDescriptor != null) {
                    if(dataSetDescriptor.getFields().containsKey("pri")) {
                        dataCondition += " order by pri asc";
                    }
                }

                final String dataConditionFinal = dataCondition;
                Map<String, String> dicInfo = new HashMap<String, String>(){{
                    put("tableName", split[0]);
                    put("keyColumn", split[1]);
                    put("valueColumn", split[2]);
                    if(split.length > 3) {
                        put("extColumn", split[3]);
                    }
                    put("condition", dataConditionFinal);
                }};
                List<KVBean> kvBeans;
                DataSetDescriptor onlyDataSetDescriptor = WebContext.get().getOnlyDataSetDescriptor(split[0]);
                String moduleCode = onlyDataSetDescriptor.getDataSet().getModule();
                String dataSetCode = onlyDataSetDescriptor.getDataSet().getCode();
                Class<?> dicServiceImplClass = Class.forName(CreatorUtil.getDefServiceImplClass(WebContext.get().getProgram().getCompany(),
                        WebContext.get().getProgram().getCode(), moduleCode, dataSetCode).getClassPath());
                Object cacheObject = CacheFactory.get(dicServiceImplClass, JsonUtils.writeValueAsString(dicInfo));
                if(cacheObject != null) {
                    kvBeans = (List<KVBean>) cacheObject;
                }else {
                    kvBeans = commonDataService.selectDynamicTableDataList(dicInfo);
                    CacheFactory.put(dicServiceImplClass, JsonUtils.writeValueAsString(dicInfo), kvBeans);
                }

                return ResultData.success(kvBeans);
            }
        }catch (Exception e) {
            logger.error("error : ", e);
            return ResultData.error(ResultCode.ERROR);
        }
    }

    private Object getIdByDataSetAndCode(String dataSet, String dataCode) throws Exception {


        String moduleCode = dataSet.substring(0, dataSet.indexOf("."));
        String dataSetCode = dataSet.substring(dataSet.indexOf(".") + 1);

        Class<?> dicPoClass = Class.forName(CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
                WebContext.get().getProgram().getCode(), moduleCode, dataSetCode).getClassPath());
        DataSetDescriptor dicDataSet = WebContext.get().getDataSet(dicPoClass);
        Class<?> dicPoExampleClass = Class.forName(CreatorUtil.getDefPoExampleClass(WebContext.get().getProgram().getCompany(),
                WebContext.get().getProgram().getCode(), moduleCode, dataSetCode).getClassPath());
        Class<?> dicServiceImplClass = Class.forName(CreatorUtil.getDefServiceImplClass(WebContext.get().getProgram().getCompany(),
                WebContext.get().getProgram().getCode(), moduleCode, dataSetCode).getClassPath());


        Object cacheObject = CacheFactory.get(dicServiceImplClass, dataSet + "|" + dataCode);
        if(cacheObject != null ) {
            return cacheObject;
        }else {
            String codeFieldName = null;
            for (String fieldName : dicDataSet.getFields().keySet()) {
                if(fieldName.trim().toLowerCase().endsWith("code")) {
                    codeFieldName  = fieldName;
                    break;
                }
            }

            Object service = ServiceFactory.getService(dicServiceImplClass);
            final Object example = dicPoExampleClass.newInstance();

            Object newCriteria = ReflectUtils.invokeMethod(example, "createCriteria", new Class[]{}, new Object[]{});
            ReflectUtils.invokeMethod(newCriteria, "and" + JavaUtil.getJavaClassName(codeFieldName) + "EqualTo",
                    new Class[]{String.class}, new Object[]{dataCode});

            List list = (List) ReflectUtils.invokeMethod(service,
                    "get" + JavaUtil.getJavaClassName(dataSetCode)
                            + "ListByExample", new Class[]{dicPoExampleClass},new Object[]{example});

            if(list.size() > 0) {
                Object o =  ReflectUtils.getFieldValue(list.get(0), JavaUtil.getJavaVarName(dicDataSet.getKeyField().getCode()));
                CacheFactory.put(dicServiceImplClass, dataSet + "|" + dataCode, o);
                return o;
            }
        }
        return null;
    }

    private List<KVBean> getListByDataSetAndCode(String dataSet, Class parentDicPoClass, Object parentDicId) throws Exception {
        String moduleCode = dataSet.substring(0, dataSet.indexOf("."));
        String dataSetCode = dataSet.substring(dataSet.indexOf(".") + 1);

        Class<?> dicPoClass = Class.forName(CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
                WebContext.get().getProgram().getCode(), moduleCode, dataSetCode).getClassPath());
        DataSetDescriptor dicDataSet = WebContext.get().getDataSet(dicPoClass);
        Class<?> dicPoExampleClass = Class.forName(CreatorUtil.getDefPoExampleClass(WebContext.get().getProgram().getCompany(),
                WebContext.get().getProgram().getCode(), moduleCode, dataSetCode).getClassPath());
        Class<?> dicServiceImplClass = Class.forName(CreatorUtil.getDefServiceImplClass(WebContext.get().getProgram().getCompany(),
                WebContext.get().getProgram().getCode(), moduleCode, dataSetCode).getClassPath());

        Object cacheObject = CacheFactory.get(dicServiceImplClass, dataSet + "|" + parentDicPoClass.getName() + "|" + parentDicId);
        if(cacheObject != null ) {
            return (List<KVBean>) cacheObject;
        }

        String relFieldCode = dicDataSet.getRelFieldCode(parentDicPoClass);

        Object service = ServiceFactory.getService(dicServiceImplClass);
        final Object example = dicPoExampleClass.newInstance();

        Object newCriteria = ReflectUtils.invokeMethod(example, "createCriteria", new Class[]{}, new Object[]{});
        ReflectUtils.invokeMethod(newCriteria, "and" + JavaUtil.getJavaClassName(relFieldCode)+ "EqualTo",
                new Class[]{Long.class}, new Object[]{parentDicId});

        List list = (List) ReflectUtils.invokeMethod(service,
                "get" + JavaUtil.getJavaClassName(dataSetCode)
                        + "ListByExample", new Class[]{dicPoExampleClass},new Object[]{example});


        String valueField = null;
        for (String fieldName : dicDataSet.getFields().keySet()) {
            if(fieldName.trim().toLowerCase().endsWith("value")) {
                valueField  = JavaUtil.getJavaVarName(fieldName);
                break;
            }
        }
        String nameField = null;
        if(dicDataSet.getNameField() != null ) {
            nameField = JavaUtil.getJavaVarName(dicDataSet.getNameField().getCode());
        }else {
            for (String fieldName : dicDataSet.getFields().keySet()) {
                if(fieldName.trim().toLowerCase().endsWith("name") || fieldName.trim().toLowerCase().endsWith("text")) {
                    nameField  = JavaUtil.getJavaVarName(fieldName);
                    break;
                }
            }
        }

        if(list.size() > 0) {
            final String finalNameField = nameField;
            final String finalValueField = valueField;
            List<KVBean> kvBeans = CollectionUtils.from(list, new Mapping<Object, KVBean>() {
                public KVBean from(Object dictionary) {
                    KVBean kvBean = new KVBean();
                    kvBean.setValue(String.valueOf(ReflectUtils.getFieldValue(dictionary, finalValueField)));
                    kvBean.setText(String.valueOf(ReflectUtils.getFieldValue(dictionary, finalNameField)));
                    return kvBean;
                }
            });
            CacheFactory.put(dicServiceImplClass, dataSet + "|" + parentDicPoClass.getName() + "|" + parentDicId, kvBeans);
            return kvBeans;
        }

        return null;
    }

    private boolean validDataCondition(String dataCondition) {
        return !dataCondition.matches(".*=[ ]*") && !dataCondition.matches(".*=[ ]*&.*");
    }

}
