package com.hframework.web.controller.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Enums;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.hframework.base.bean.MapWrapper;
import com.hframework.beans.class0.Class;
import com.hframework.beans.controller.Pagination;
import com.hframework.beans.controller.ResultData;
import com.hframework.beans.exceptions.BusinessException;
import com.hframework.common.frame.ServiceFactory;
import com.hframework.common.util.*;
import com.hframework.web.CreatorUtil;
import com.hframework.web.auth.AuthContext;
import com.hframework.web.auth.AuthServiceProxy;
import com.hframework.web.config.bean.DataSetHelper;
import com.hframework.web.config.bean.datasethelper.Mappings;
import com.hframework.web.config.bean.module.SetValue;
import com.hframework.web.context.*;
import com.hframework.web.controller.DefaultController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.hframework.common.util.ExampleUtils.*;

/**
 * Created by zhangquanhong on 2018/1/10.
 */
@Service
public class ComponentInvokeManager {
    private static final Logger logger = LoggerFactory.getLogger(ComponentInvokeManager.class);

    @Resource
    private AuthServiceProxy authServiceProxy;

    @Resource
    private ControllerMethodInvoker controllerMethodInvoker;

    @Resource
    private FileComponentInvoker fileComponentInvoker;


    public  JSONObject invoke(boolean isRefresh, ComponentDescriptor componentDescriptor, Object componentExtendData, Map<String, Object> extendData,
                              Pagination pagination, String module, String pageCode, PageDescriptor pageInfo,
                              ModelAndView mav, HttpServletRequest request, HttpServletResponse response) throws Throwable {

        if(componentDescriptor.getDataSetDescriptor() == null) {
            logger.warn("component {} is not set data set",componentDescriptor.getId());
            return null;
        }
        if(componentDescriptor.getDataSetDescriptor().isHelperRuntime()){
            componentDescriptor.getDataSetDescriptor().resetHelperInfo(false);
        }

        String moduleCode = componentDescriptor.getDataSetDescriptor().getDataSet().getModule();
        String eventObjectCode = componentDescriptor.getDataSetDescriptor().getDataSet().getEventObjectCode();
        String dataSetCode = componentDescriptor.getDataSetDescriptor().getDataSet().getCode();
        String[] columnTableInfo = componentDescriptor.getDataSetDescriptor().getColumnTableKeyAndValue();
        if(columnTableInfo != null && StringUtils.isNotBlank(columnTableInfo[2])) {
            moduleCode = columnTableInfo[2].trim();
        }

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
        }else if(StringUtils.isNotBlank(type) && !"cForm".equals(type) && !"qForm".equals(type)) {
            action = type;
        }

        //行列转换
        String sourceAction = action;
        if(action == "detail" && columnTableInfo != null) {
            action = "list";
        }

        JSONObject jsonObject = null;
        String componentQueryString = null;
        if("pageflow".equals(componentDescriptor.getMapper().getDataAuth())) {
            Map<String, String> pageContextParams2 = DefaultController.getPageContextParams(request);
//                    WebContext.get().add(getPageContextRealyParams(pageContextParams));
            WebContext.putContext(DefaultController.getPageContextRealyParams(pageContextParams2));

//                        jsonObject.putAll(pageContextParams);
            jsonObject = componentDescriptor.getJson();
            jsonObject.put("data", pageContextParams2);
        }else if ("session".equals(componentDescriptor.getMapper().getDataAuth())) {
            Class defPoClass = CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
                    WebContext.get().getProgram().getCode(), moduleCode, eventObjectCode);
            Object data = request.getSession().getAttribute(java.lang.Class.forName(defPoClass.getClassPath()).getName());
            System.out.println("session data " + data);
            if (data == null) {
                throw new SessionExpiredException();
            }
            jsonObject = getJsonObjectByResultData(componentDescriptor, ResultData.success(data), moduleCode, dataSetCode, action);
        }else if("file".equals(componentDescriptor.getDataSetDescriptor().getDataSet().getSource())) {
            jsonObject = fileComponentInvoker.parseFileComponent(type, dataSetCode, module, componentDescriptor, mav, request,response);
        }else if("SYSTEM_EMPTY_DATASET".equals(dataSetCode)) {
            jsonObject = componentDescriptor.getJson();
            if(DefaultController.isDynDataInfo(componentDescriptor.getDataId())){
                String dynDataString = fileComponentInvoker.getDynDataInfo(componentDescriptor.getDataId(), request);
                jsonObject.put("dynDataString", dynDataString);
                mav.addObject("dynDataString", dynDataString);
                mav.addObject("objectId", request.getParameter("id"));
            }
        }else if (componentExtendData != null) {
            ResultData resultData = ResultData.success(componentExtendData);
            resetResultMessage(resultData, WebContext.get().getProgram().getCode(), moduleCode, dataSetCode, action);
            if(resultData.isSuccess()) {

                if("dynamicVM".equals(componentDescriptor.getId())) {
                    jsonObject = new JSONObject();
                    jsonObject.put("data",resultData.getData());
                }else {
                    jsonObject = componentDescriptor.getJson(resultData);
                }
                if(resultData.getData() != null && resultData.getData() instanceof Map) {
                    Map extData = (Map)resultData.getData();
                    if(extData.containsKey("_DataJson")) {
                        jsonObject.put("data", extData.get("_DataJson"));
                    }
                    if(extData.containsKey("_ColumnJson")) {
                        jsonObject.put("columns", extData.get("_ColumnJson"));
                    }
                }

            }else {
                jsonObject = componentDescriptor.getJson();
                if(!(jsonObject.get("data") instanceof JSONArray)) {
                    jsonObject.put("data",JSONObject.toJSON(WebContext.getDefault()));
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

            Object controller = ServiceFactory.getService(defControllerClass.getClassName().substring(0, 1).toLowerCase() + defControllerClass.getClassName().substring(1));
            Object poExample = getPoExample(defPoExampleClass, defPoClass, module, pageCode, componentDescriptor, pageInfo, request);
            if(isRefresh) {
                invokePoExample(poExample, request);
            }
            Object po = null;

            if ("detail".equals(action)) {
                resultData = controllerMethodInvoker.invokeDetail(defPoClass, controller, request, componentDescriptor);
                //这里将查询的单个对象存入线程中，别的组件在需要时可以获取想要的值，如数据集数据列智能提醒需要依赖数据集的主实体ID
                WebContext.add(resultData.getData());
            }else if("tree".equals(action) && componentDescriptor.getDataSetDescriptor().isSelfDepend()) {
                resultData = controllerMethodInvoker.invokeTree(poExample, defPoClass, defPoExampleClass, controller, request);
            }else if("tree".equals(action) && !componentDescriptor.getDataSetDescriptor().isSelfDepend()) {
                resultData = controllerMethodInvoker.invokeTreeByRelDataSet(poExample, pagination, defPoClass, defPoExampleClass, controller, action, componentDescriptor, request);
            }else if("list".equals(action)){
                if (pagination.getPageNo() == 0) pagination.setPageNo(1);
                if (pagination.getPageSize() == 0) pagination.setPageSize(10);
                if ("eList".equals(type)) pagination.setPageSize(50);
                if(columnTableInfo != null) pagination.setPageSize(10000);
                if(componentDescriptor.getSetValueList() != null && componentDescriptor.getSetValueList().size() > 0) {
                    List<String> keyValuePairs = new ArrayList<String>();
                    for (SetValue setValue : componentDescriptor.getSetValueList()) {
                        keyValuePairs.add(JavaUtil.getJavaVarName(setValue.getField()) + "==" + setValue.getValue());
                    }
                    poExample = ExampleUtils.parseExample(Joiner.on("&").join(keyValuePairs), poExample);
                }

                resultData = controllerMethodInvoker.invokeList(isRefresh, pagination, poExample, defPoClass, defPoExampleClass, controller, request , componentDescriptor);
                if (resultData.getData() instanceof Map) {
                    List helperData = getHelperData(extendData, componentDescriptor.getDataSetDescriptor(), action, defPoClass, request);
                    ((Map) resultData.getData()).put("helperData", helperData);
                }

            }else {
                throw new BusinessException("action [ " + action + " ] not supported! ");
            }

            if(columnTableInfo != null) {
                convertResultDataColToRow(resultData, componentDescriptor, columnTableInfo, sourceAction);
            }

            jsonObject = getJsonObjectByResultData(componentDescriptor, resultData, moduleCode, dataSetCode, action);
            if(resultData.getData() == null) {
                jsonObject.put("dataIsEmpty","true");
            }
            if("detail".equals(action) && "eForm".equals(type) && resultData.getData() == null) {
                jsonObject.put("data",JSONObject.toJSON(WebContext.getDefault()));
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
                jsonObject.put("data",componentDescriptor.getWebContextDefaultJson(WebContext.getDefault()));
            }
            jsonObject.put("dataIsEmpty","true");
        }

        if("list".equals(type) || "cList".equals(type) || "eList".equals(type) || "eTList".equals(type)) {
            if(jsonObject.get("data") == null) {
                jsonObject.put("data", new JSONArray());
            }

            if(((JSONArray) jsonObject.get("data")).size() == 0 &&  jsonObject.get("columns") != null ) {
                jsonObject.put("dataIsEmpty","true");
                int cnt = 0;
                String[] defaultNullData = new String[((JSONArray) jsonObject.get("columns")).size()];
                Map<String, String> pageFlowParams = WebContext.getDefault();
//                Class defPoClass = CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
//                        WebContext.get().getProgram().getCode(), moduleCode, eventObjectCode);
                for (Object columns : (JSONArray) jsonObject.get("columns")) {
                    String columnName = ((JSONObject) columns).getString("code");
                    String defaultValue = componentDescriptor.getDefaultValueByCode(columnName);
                    if(defaultValue != null) {
                        defaultNullData[cnt++] = defaultValue;
                    }else if(pageFlowParams.containsKey(columnName)) {
                        /* TODO
                           【BUG】父子编辑页面，传入父ID，原则上我们用子关联ID关联父ID，但当子主键ID等于父ID时，
                           那么子ID主键就赋值为父ID了。 是否父页面请求时，不在直接是主键ID，而应该是实体加主键ID
                           这里临时处理一下，遗留问题当页面中只是子页面数据编辑怎么处理？
                           同时引出新的问题当父子编辑页面，子列表某属性依赖同行某属性，而该同行属性其实又是父id，
                           如果这样临时处理，那么这里依赖找不到了，新建实体可以复现
                        */
//                        defaultNullData[cnt++] = pageFlowParams.get(((JSONObject) columns).get("code"));
                        defaultNullData[cnt++] = "";
                    }else {
                        defaultNullData[cnt++] = "";
                    }
                }
                ((JSONArray) jsonObject.get("data")).add(defaultNullData);
            }
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
        return jsonObject;
    }

    private void convertResultDataColToRow(ResultData resultData, ComponentDescriptor componentDescriptor, String[] columnTableInfo, String sourceAction) {
        //行列转换
        if(columnTableInfo != null && resultData.getData() != null &&  resultData.getData() instanceof Map) {
            Map<String, Object> map = (Map) resultData.getData();
            if(map.containsKey("list")) {
                List datas = (List)map.get("list");
                if(datas != null) {
                    if("detail".equals(sourceAction)) {
                        Map<String, Object> targetMap = new HashMap<String, Object>();
                        for (Object data : datas) {
                            targetMap.putAll(parseDataToMap(data, columnTableInfo, componentDescriptor));
                        }
                        resultData.setData(MapWrapper.warp(targetMap));
                    }else {
                        TreeMap<Integer, Map<String, Object>> targetMap = new TreeMap<Integer, Map<String, Object>>();
                        for (Object data : datas) {
                            Map<String, Object> temp = parseDataToMap(data, columnTableInfo, componentDescriptor);
                            if(!temp.isEmpty()) {
                                Integer listId = (Integer) temp.get("LIST_ROW_ID");
                                if(listId != null) {
                                    if(!targetMap.containsKey(listId)) {
                                        targetMap.put(listId, new HashMap<String, Object>());
                                    }
                                    targetMap.get(listId).putAll(temp);
                                }
                            }
                        }
                        map.put("list", Lists.newArrayList(targetMap.values()));
                    }
                }
            }
        }
    }

    private Map<String, Object> parseDataToMap(Object data, String[] columnTableInfo, ComponentDescriptor componentDescriptor) {
        Map<String, Object> tmpMap = new HashMap<String, Object>();
        Map<String, String> srcMap= BeanUtils.convertMap(data, false);
        String businessCode = srcMap.get(columnTableInfo[0]);
        int rowId = 0;
        if(businessCode.matches(".*\\(\\d+\\)")) {
            String rowIdStr = RegexUtils.find(businessCode, "\\(\\d+\\)")[0];
            rowId = Integer.valueOf(rowIdStr.substring(1, rowIdStr.length() - 1));
            businessCode = businessCode.replaceAll("\\(\\d+\\)", "");
        }
        if(checkIsNeedDataBlock(businessCode, srcMap, componentDescriptor.getDefaultValues())) {
            tmpMap.put(businessCode, srcMap.get(columnTableInfo[1]));
            tmpMap.put("LIST_ROW_ID", rowId);

            for (Map.Entry<String, String> entry : srcMap.entrySet()) {
                tmpMap.put(businessCode + "#" + entry.getKey(), entry.getValue());
            }
        }
        return tmpMap;
    }

    private boolean checkIsNeedDataBlock(String businessCode, Map<String, String> srcMap, Map<String, Object> defaultValues) {
        for (Map.Entry<String, String> entry : srcMap.entrySet()) {
            String defaultKey = businessCode + "#" + entry.getKey();
            if(defaultValues.containsKey(defaultKey) && defaultValues.get(defaultKey).equals(entry.getValue())){
                return true;
            }
        }
        return false;
    }

    private void invokePoExample(Object poExample, HttpServletRequest request) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        List<String> queryStringList = new ArrayList<String>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            String parameterValue = request.getParameter(parameterName);
            if(StringUtils.isBlank(parameterValue)) {
                continue;
            }

            if(parameterName.length() <= 3) {
                continue;
            }

            Optional<ExampleUtils.RelationOperator> optional = Enums.getIfPresent(RelationOperator.class, parameterName.substring(parameterName.length() - 3));
            if(!optional.isPresent()) {
                continue;
            }

            parameterName = parameterName.substring(0, parameterName.length() - 3);
            RelationOperator relationOperator = optional.get();
            switch (relationOperator){
                case EQU:
                case NEQ:
                case LSS:
                case LEQ:
                case GTR:
                case GEQ:
                    queryStringList.add(parameterName + ExampleUtils.getSign(relationOperator) + parameterValue);
                    break;
                case LKE:
//                case RLK:
//                case LLK:
                    if(parameterValue != null && !parameterValue.startsWith("%")) parameterValue = "%" + parameterValue;
                    if(parameterValue != null && !parameterValue.endsWith("%")) parameterValue = parameterValue + "%";

                    queryStringList.add(parameterName + ExampleUtils.getSign(relationOperator) + parameterValue);
                    break;
                default:
//                    queryStringList.add(parameterName + "=" + parameterValue);
            }
        }

        parseExample(Joiner.on("&").join(queryStringList), poExample);
    }


    private JSONObject getJsonObjectByResultData(ComponentDescriptor componentDescriptor, ResultData resultData, String moduleCode, String dataSetCode, String action) {
        JSONObject jsonObject;
        resetResultMessage(resultData, WebContext.get().getProgram().getCode(), moduleCode, dataSetCode, action);
        if(resultData.isSuccess()) {
            jsonObject = componentDescriptor.getJson(resultData);
        }else {
            jsonObject = componentDescriptor.getJson();
            if(!(jsonObject.get("data") instanceof JSONArray)) {
                /*TODO 临时注释掉，BUG:当我们编辑一对象时（eForm），如果对象存在一下属可选的子对象（eForm)，
                当下属对象从无到有且下属对象与源编辑对象拥有同样的ID名称，则默认赋值，这样新增下属对象变成了修改别的下属对象
                比如增加数据源对象，而数据源下属对象有MYSQL,HBASE,REDIS可选对象*/
//                jsonObject.put("data",JSONObject.toJSON(WebContext.getDefault()));
            }
        }
        if("list".equals(componentDescriptor.getComponent().getType()) && componentDescriptor.getDataSetDescriptor().getWorkflowStatusField() != null) {
            ProcessContext.ProcessInfo processInfo = WebContext.get().getProcessContext().getProcessInfo(componentDescriptor.getDataSetDescriptor().getDataSet().getCode());
            if(processInfo != null) {
                List<ComponentDataContainer.EventElement> workflowEvents = processInfo.getWorkflowEOFREventElements();
                JSONArray evenets = jsonObject.getJSONArray("EOFR");
                evenets.addAll(workflowEvents);
                jsonObject.put("EOFR", evenets);
            }
        }else if("eForm".equals(componentDescriptor.getId()) && componentDescriptor.getDataSetDescriptor().getWorkflowStatusField() != null) {
            ProcessContext.ProcessInfo processInfo = WebContext.get().getProcessContext().getProcessInfo(componentDescriptor.getDataSetDescriptor().getDataSet().getCode());
            if(processInfo != null) {
                String nodeKeyValue = jsonObject.getJSONObject("data").getString(processInfo.getProcessDataFieldCode());
                List<ComponentDataContainer.EventElement> workflowEvents = processInfo.getWorkflowEOFCEventElements().get(nodeKeyValue);
                if(workflowEvents != null) {
                    JSONArray evenets = jsonObject.getJSONArray("EOF");
                    if(evenets != null) {
                        evenets.addAll(workflowEvents);
                        jsonObject.put("EOF", evenets);
                    }else {
                        jsonObject.put("EOF", workflowEvents);
                    }
                }

                if(processInfo.getProcessNodeInfoMap() != null && processInfo.getProcessNodeInfoMap().containsKey(nodeKeyValue)) {
                    ProcessContext.ProcessNodeInfo processNodeInfo = processInfo.getProcessNodeInfoMap().get(nodeKeyValue);
                    JSONArray columns = jsonObject.getJSONArray("columns");
                    for (Object column : columns) {
                        JSONObject columnObject = (JSONObject)column;
                        String code = columnObject.getString("code");
                        if(processNodeInfo.getForbidFields().contains(code)){
                            columnObject.put("editType","text");
                        }
                    }
                    if(processNodeInfo.isAllowComment()){
                        JSONObject comment = new JSONObject();
                        comment.put("code", "_WFComment");
                        comment.put("name", "<span style=\"color:red;\">意见</span>");
                        comment.put("editType", "textarea");
                        columns.add(comment);
                    }
                    jsonObject.put("HF_BREAK_FLAG", true);
                }

            }
        }
        return jsonObject;
    }

    private Object getPoExample(Class defPoExampleClass, Class defPoClass, String module, String pageCode, ComponentDescriptor componentDescriptor, PageDescriptor pageInfo, HttpServletRequest request) throws Exception {
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
            com.hframework.web.config.bean.dataset.Field keyField = componentDescriptor.getDataSetDescriptor().getKeyField();
            Object criteria = ReflectUtils.invokeMethod(poExample, "createCriteria", new java.lang.Class[]{}, new Object[]{});
            ReflectUtils.invokeMethod(criteria,
                    "and" + JavaUtil.getJavaClassName(keyField.getCode()) + "In",
                    new java.lang.Class[]{List.class}, new Object[]{functionIds});
        }else if(authContext != null && authContext.getAuthManager().getAuthDataClass().contains(java.lang.Class.forName(defPoClass.getClassPath()))) {
            Long funcId = authContext.getAuthFunctionManager().get("/" + module + "/" + pageCode + ".html");
            List<Long> dataUnitIds = authContext.getAuthManager().getDataUnitIds(funcId);

            com.hframework.web.config.bean.dataset.Field keyField = componentDescriptor.getDataSetDescriptor().getKeyField();
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
        return poExample;
    }

    private void resetResultMessage(ResultData resultData, String programCode, String moduleCode, String dataSetCode, String action) {
        String resourceKey = programCode + "." + moduleCode + "." + dataSetCode + "." + action + resultData.getResultCode();
        resourceKey = programCode + "." + moduleCode + "." + dataSetCode + "." + action + resultData.getResultCode();
        resourceKey = action + resultData.getResultCode();
        resourceKey = resultData.getResultCode();
        resultData.setResultMessage("TODO");
    }

    private List getHelperData(Map<String, Object> extendData , DataSetDescriptor dataSetDescriptor, String action, Class targetPoClass, HttpServletRequest request) {
        Map<String, String> pageFlowParams = WebContext.getDefault();
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
                    ResultData helperData = DefaultController.invokeMethod(controller, action,
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


}
