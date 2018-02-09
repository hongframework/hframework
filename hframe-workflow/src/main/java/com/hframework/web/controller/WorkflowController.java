package com.hframework.web.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hframework.base.service.CommonDataService;
import com.hframework.beans.controller.ResultCode;
import com.hframework.beans.controller.ResultData;
import com.hframework.common.frame.ServiceFactory;
import com.hframework.common.util.JavaUtil;
import com.hframework.common.util.StringUtils;
import com.hframework.common.util.collect.CollectionUtils;
import com.hframework.common.util.collect.bean.Grouper;
import com.hframework.web.auth.AuthContext;
import com.hframework.web.auth.AuthServiceProxy;
import com.hframework.web.config.bean.dataset.Field;
import com.hframework.web.context.DataSetDescriptor;
import com.hframework.web.context.ProcessContext;
import com.hframework.web.context.WebContext;
import com.hframework.web.context.WorkflowUtils;
import com.vaadin.terminal.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.*;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.util.IoUtil;
import org.activiti.engine.repository.*;
import org.activiti.engine.task.TaskQuery;
import org.activiti.explorer.ExplorerApp;
import org.activiti.explorer.ui.Images;
import org.activiti.explorer.ui.custom.PrettyTimeLabel;
import org.activiti.explorer.ui.custom.UserProfileLink;
import org.activiti.explorer.ui.process.DefaultProcessDefinitionFilter;
import org.activiti.explorer.ui.process.simple.editor.SimpleTableEditorConstants;
import org.activiti.explorer.util.XmlUtil;
import org.activiti.rest.editor.model.ModelSaveRestResource;
import org.activiti.workflow.simple.converter.WorkflowDefinitionConversion;
import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import sun.misc.BASE64Encoder;

import javax.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.*;

/**
 * Created by zhangquanhong on 2017/1/16.
 */
@Controller
public class WorkflowController {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowController.class);
    // Services
    protected transient RepositoryService repositoryService = ProcessEngines.getDefaultProcessEngine().getRepositoryService();
    protected transient HistoryService historyService = ProcessEngines.getDefaultProcessEngine().getHistoryService();
    protected transient TaskService taskService = ProcessEngines.getDefaultProcessEngine().getTaskService();
    @javax.annotation.Resource
    private AuthServiceProxy authServiceProxy;
    @Resource
    private CommonDataService commonDataService;

    private final static List<String> DEFAULT_CODE_LIST = Lists.newArrayList("category", "name", "key", "taskKey", "taskName", "role", "comment");
    private final static String DELETE_SQL_FORMAT = "DELETE FROM act_procdef_extauth WHERE  proc_name_ = ''{0}'' and proc_key_ = ''{1}'';";
    private final static String INSERT_SQL_FORMAT = "INSERT INTO act_procdef_extauth(category_, proc_name_, proc_key_, task_key_, auth_type_, auth_target_, auth_value_, create_time_) VALUES {0};";
    private final static String SELECT_SQL_FORMAT = "SELECT * FROM act_procdef_extauth t WHERE t.CATEGORY_ =  ''{0}'' AND PROC_NAME_ =  ''{1}'' AND PROC_KEY_ =  ''{2}'';";

    /**
     * 编辑中流程定义列表
     * @return
     */
    @RequestMapping(value = "/extend/editing_procdef.json")
    @ResponseBody
    public ResultData editingProcDefList(HttpServletRequest request){
        final ModelQuery modelQuery = repositoryService.createModelQuery();
        if(StringUtils.isNotBlank(request.getParameter("id"))) {
            modelQuery.modelId(request.getParameter("id").trim());
        }
        if(StringUtils.isNotBlank(request.getParameter("name"))) {
            modelQuery.modelNameLike("%" + request.getParameter("name").trim() + "%");
        }
        if(StringUtils.isNotBlank(request.getParameter("key"))) {
            modelQuery.modelKey(request.getParameter("key").trim());
        }
        if(StringUtils.isNotBlank(request.getParameter("version"))) {
            modelQuery.modelVersion(Integer.valueOf(request.getParameter("version").trim()));
        }
        if(StringUtils.isNotBlank(request.getParameter("category"))) {
            modelQuery.modelCategoryLike("%" + request.getParameter("category").trim() + "%");
        }
        return ResultData.success(new HashMap<String,Object>(){{
            put("editingProcDefList", new HashMap<String, Object>() {{
                put("list", modelQuery.list());
            }});
        }});
    }

    /**
     * 已部署流程定义列表
     * @return
     */
    @RequestMapping(value = "/extend/deployed_procdef.json")
    @ResponseBody
    public ResultData deployedProcDefList(HttpServletRequest request){
//        ComponentFactory<ProcessDefinitionFilter> factory =
//                ExplorerApp.get().getComponentFactory(ProcessDefinitionFilterFactory.class);
//        final ProcessDefinitionFilter definitionFilter = factory.create();
        final ProcessDefinitionQuery query = new DefaultProcessDefinitionFilter().getQuery(repositoryService);
        if(StringUtils.isNotBlank(request.getParameter("id"))) {
            query.processDefinitionId(URLDecoder.decode(request.getParameter("id").trim()));
        }
        if(StringUtils.isNotBlank(request.getParameter("name"))) {
            query.processDefinitionNameLike("%" + request.getParameter("name").trim() + "%");
        }
        if(StringUtils.isNotBlank(request.getParameter("key"))) {
            query.processDefinitionKeyLike("%" + request.getParameter("key").trim() + "%");
        }
        if(StringUtils.isNotBlank(request.getParameter("version"))) {
            query.processDefinitionVersion(Integer.valueOf(request.getParameter("version").trim()));
        }
        if(StringUtils.isNotBlank(request.getParameter("category"))) {
            query.processDefinitionCategoryLike("%" + request.getParameter("category").trim() + "%");
        }
        return ResultData.success(new HashMap<String,Object>(){{
            put("deployedProcDefList", new HashMap<String, Object>() {{
                put("list", query.listPage(0, 30));
            }});
        }});
    }

    /**
     * 待处理流程实例列表
     * @return
     */
    @RequestMapping(value = "/extend/my_unfinish_procinst.json")
    @ResponseBody
    public ResultData myUnFinishProcInstfList(HttpServletRequest request, HttpServletResponse response){
//        ComponentFactory<ProcessDefinitionFilter> factory =
//                ExplorerApp.get().getComponentFactory(ProcessDefinitionFilterFactory.class);
//        final ProcessDefinitionFilter definitionFilter = factory.create();
        ServiceFactory.getService(ExplorerApp.class).onRequestStart(request, response);
        final HistoricProcessInstanceQuery query = historyService
                .createHistoricProcessInstanceQuery()
                .startedBy(ExplorerApp.get().getLoggedInUser().getId())
                .unfinished();
        if(StringUtils.isNotBlank(request.getParameter("processInstanceId"))) {
            query.processInstanceId(request.getParameter("processInstanceId").trim());
        }
        if(StringUtils.isNotBlank(request.getParameter("businessKey"))) {
            query.processInstanceBusinessKey(request.getParameter("businessKey").trim());
        }
        if(StringUtils.isNotBlank(request.getParameter("processDefinitionId"))) {
            query.processDefinitionId(request.getParameter("processDefinitionId").trim());
        }
        if(StringUtils.isNotBlank(request.getParameter("processDefinitionName"))) {
            query.processDefinitionName(request.getParameter("processDefinitionName").trim());
        }
        if(StringUtils.isNotBlank(request.getParameter("processDefinitionKey"))) {
            query.processDefinitionKey(request.getParameter("processDefinitionKey").trim());
        }
        if(StringUtils.isNotBlank(request.getParameter("processDefinitionVersion"))) {
            query.processDefinitionVersion(Integer.valueOf(request.getParameter("processDefinitionVersion").trim()));
        }
        return ResultData.success(new HashMap<String,Object>(){{
            put("deployedProcDefList", new HashMap<String, Object>() {{
                put("list", query.list());
            }});
        }});
    }

    /**
     * 待处理流程任务实例
     * @return
     */
    @RequestMapping(value = "/extend/my_unfinish_taskinst.json")
    @ResponseBody
    public ResultData myUnFinishTaskInstfList(HttpServletRequest request, HttpServletResponse response){
        ServiceFactory.getService(ExplorerApp.class).onRequestStart(request, response);
        final TaskQuery query = taskService.createTaskQuery().taskAssignee(ExplorerApp.get().getLoggedInUser().getId()).or().taskInvolvedUser(ExplorerApp.get().getLoggedInUser().getId()).orderByTaskId().asc();
        if(StringUtils.isNotBlank(request.getParameter("processDefinitionId"))) {
            query.processDefinitionId(request.getParameter("processDefinitionId").trim());
        }
        if (StringUtils.isNotBlank(request.getParameter("processInstanceId"))) {
            query.processInstanceId(request.getParameter("processInstanceId").trim());
        }
        if(StringUtils.isNotBlank(request.getParameter("businessKey"))) {
            query.processInstanceBusinessKey(request.getParameter("businessKey").trim());
        }

        if(StringUtils.isNotBlank(request.getParameter("processDefinitionName"))) {
            query.processDefinitionName(request.getParameter("processDefinitionName").trim());
        }
        if(StringUtils.isNotBlank(request.getParameter("processDefinitionKey"))) {
            query.processDefinitionKey(request.getParameter("processDefinitionKey").trim());
        }
        return ResultData.success(new HashMap<String,Object>(){{
            put("deployedProcDefList", new HashMap<String, Object>() {{
                put("list", query.listPage(0, 30));
            }});
        }});
    }


    /**
     * 流程授权
     * @return
     */
    @RequestMapping(value = "/extend/auth_procdef.json")
    @ResponseBody
    public ResultData authProcDef(HttpServletRequest request) throws Exception {
        String dataSet = request.getParameter("dataSet");
        if(StringUtils.isBlank(dataSet)) {
            return ResultData.error(ResultCode.ERROR);
        }

        DataSetDescriptor dataSetDescriptor = WebContext.get().getOnlyDataSetDescriptor(dataSet);
        if(dataSetDescriptor == null) {
            return ResultData.error(ResultCode.ERROR);
        }
        Map<String, Field> fields = Maps.filterEntries(new HashMap<String, Field>(dataSetDescriptor.getFields()), new Predicate<Map.Entry<String, Field>>() {
            public boolean apply(Map.Entry<String, Field> input) {
                String editType = input.getValue().getEditType();
                String createEditType = input.getValue().getCreateEditType();
                String updateEditType = input.getValue().getUpdateEditType();

                return !"hidden".equals(editType) && !"text".equals(editType)
                        && !(("hidden".equals(createEditType) || "text".equals(createEditType)
                            && ("hidden".equals(updateEditType) || "text".equals(updateEditType))));
            }
        });
        AuthContext authContext = authServiceProxy.getAuthContext(request);
        Class roleClass = authContext.getAuthRoleManager().roleClass;
        DataSetDescriptor roleDataSet = WebContext.get().getDataSet(roleClass);
        String roleDataCode = roleDataSet.getDataSet().getCode() + "." + roleDataSet.getKeyField().getCode() + "." + roleDataSet.getNameField().getCode();

        final JSONArray columnJsonArray = new JSONArray();
        columnJsonArray.add(getColumnObject("category", "hidden", "", "分类", null));
        columnJsonArray.add(getColumnObject("name", "hidden", "", "名称", null));
        columnJsonArray.add(getColumnObject("key", "hidden", "", "编码", null));
        columnJsonArray.add(getColumnObject("taskKey", "hidden", "", "任务节点", null));
        columnJsonArray.add(getColumnObject("taskName", "text", "", "任务节点", null));
        columnJsonArray.add(getColumnObject("role", "select", "", "角色", roleDataCode));
        Map<String, Integer> fieldIndexMap = new HashMap<String, Integer>();
        int i = 5;
        for (Field field : fields.values()) {
            fieldIndexMap.put(JavaUtil.getJavaVarName(field.getCode()), ++i);
            columnJsonArray.add(getColumnObject(JavaUtil.getJavaVarName(field.getCode()), "checkbox", "", JavaUtil.getJavaVarName(field.getName()), "DEFAULT.BOOLEAN"));
        }
        columnJsonArray.add(getColumnObject("comment", "checkbox", "", "意见", "DEFAULT.BOOLEAN"));

        final JSONArray dataJsonArray = new JSONArray();
        final ProcessDefinitionQuery query = new DefaultProcessDefinitionFilter().getQuery(repositoryService);
        query.processDefinitionKey(dataSet);
        List<ProcessDefinition> processDefinitions = query.listPage(0, 1);
        if(processDefinitions != null && processDefinitions.size() > 0) {
            ProcessDefinition processDefinition = processDefinitions.get(0);
            final String category = processDefinition.getCategory();
            final String procKey = processDefinition.getKey();
            final String procName = processDefinition.getName();

            Map<String, List<Object>> configMap = WorkflowUtils.getWorkflowExtAuth(commonDataService, category, procName, procKey);

            final InputStream definitionStream = repositoryService.getResourceAsStream(
                    processDefinition.getDeploymentId(), processDefinition.getResourceName());
            XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
            XMLStreamReader xtr = xif.createXMLStreamReader(definitionStream);
            BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);
            Collection<FlowElement> flowElements = bpmnModel.getProcesses().get(0).getFlowElements();


            for (FlowElement flowElement : flowElements) {
                if(flowElement instanceof StartEvent || flowElement instanceof EndEvent || flowElement instanceof UserTask) {
                    String taskKey = flowElement.getId();
                    String taskName = flowElement.getName();
                    String[] row = new String[columnJsonArray.size()];
                    row[0] = category;
                    row[1] = procName;
                    row[2] = procKey;
                    row[3] = taskKey;
                    row[4] = taskName;
                    row[5] = null;
                    Arrays.fill(row, 6, row.length, dataJsonArray.size() == 0 ? "1" : "0");
                    row[row.length - 1] = dataJsonArray.size() == 0 ? "0" : "1";
                    dataJsonArray.add(row);

                    List<Object> objects = configMap.get(taskKey);
                    if(objects != null) {
                        for (Object configObject : objects) {
                            Map<String, String> config = (Map<String, String>)configObject;
                            String authType = config.get("AUTH_TYPE_");
                            String authTarget = config.get("AUTH_TARGET_");
                            String authValue = config.get("AUTH_VALUE_");
                            if("role".equals(authType)) {
                                row[5] = authTarget;
                            }else if("comment".equals(authType)) {
                                row[row.length - 1] = authValue;
                            }else if("field".equals(authType)) {
                                row[fieldIndexMap.get(authTarget)] = authValue;
                            }
                        }
                    }

                }
            }
        }



        return ResultData.success(new HashMap<String,Object>(){{
            put("procDefExtAuthList", new HashMap<String, Object>() {{
                put("list", Lists.newArrayList());
                put("_ColumnJson", columnJsonArray);
                put("_DataJson", dataJsonArray);
            }});
        }});
    }

    /**
     * 复制编辑中的流程定义
     * @return
     */
    @RequestMapping(value = "/activiti/save_procdef_auth.json")
    @ResponseBody
    public ResultData saveProcdefAuth(String dataSet, HttpServletRequest request) throws Exception {
        String refererUrl = request.getHeader("referer");
        dataSet = refererUrl.substring(refererUrl.indexOf("?") + 1);
        String dataJson = DefaultController.getRequestPostStr(request);
        JSONObject jsonObject = JSONObject.parseObject(dataJson, Feature.OrderedField);
        JSONArray rows = (JSONArray) jsonObject.get("eList|0");


        StringBuffer sqlBuild = new StringBuffer();
        long currentTimeMillis = System.currentTimeMillis();
        String name = null, key = null;
        for (Object row : rows) {
            JSONObject rowObject = (JSONObject) row;
            String category = rowObject.getString("category");
            name = rowObject.getString("name");
            key = rowObject.getString("key");
            String taskKey = rowObject.getString("taskKey");
            String role = rowObject.getString("role");
            Integer comment = rowObject.getInteger("comment");

            sqlBuild.append(concatInsertValues(category, name, key, taskKey, "role", role, null ,currentTimeMillis)).append(", ");
            sqlBuild.append(concatInsertValues(category, name, key, taskKey, "comment", "comment", String.valueOf(comment), currentTimeMillis)).append(", ");
            for (String code : rowObject.keySet()) {
                if(!DEFAULT_CODE_LIST.contains(code)) {
                    sqlBuild.append(concatInsertValues(category, name, key, taskKey, "field", code, rowObject.getString(code), currentTimeMillis)).append(", ");
                }
            }
        }
        commonDataService.executeDBStructChange(MessageFormat.format(DELETE_SQL_FORMAT, name, key));
        commonDataService.executeDBStructChange(MessageFormat.format(INSERT_SQL_FORMAT, sqlBuild.substring(0, sqlBuild.length() - 2)));
        System.out.println(jsonObject);
        return ResultData.success();
    }

    public String concatInsertValues(String category, String name, String key, String taskKey, String authType, String authTarget, String authValue, long currentTimeMillis){
        return MessageFormat.format("(''{0}'',''{1}'',''{2}'',''{3}'',''{4}'',''{5}'',''{6}'',{7,number,#})", category, name, key, taskKey, authType, authTarget, authValue, null);
    }

    private Object getColumnObject(String code, String editType, String width, String name, String dataCode) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("editType", editType);
        jsonObject.put("width", width);
        jsonObject.put("name",name);
        if(StringUtils.isNotBlank(dataCode)) {
            jsonObject.put("dataCode", dataCode);
        }
        return jsonObject;
    }


    /**
     * 编辑流程定义
     * @return
     * @throws Throwable
     */
    @RequestMapping(value = "/modeler.html")
    public ModelAndView modeler(@ModelAttribute("modelId") String modelId, @ModelAttribute("dataSetId") String dataSetId,

                                HttpServletRequest request, HttpServletResponse response) throws Throwable {
        ModelAndView mav = new ModelAndView();

//        if(StringUtils.isBlank(modelId) && dataSetId != null && StringUtils.isNotBlank(dataSetId)) {
//            HfpmDataField_Example example = new HfpmDataField_Example();
//            example.createCriteria().andHfpmDataSetIdEqualTo(Long.valueOf(dataSetId)).andWorkfowModelIdIsNotNull().andWorkfowModelIdNotEqualTo("");
//            List<HfpmDataField> hfpmDataFieldListByExample = hfpmDataFieldSV.getHfpmDataFieldListByExample(example);
//            if(hfpmDataFieldListByExample != null && hfpmDataFieldListByExample.size() > 0) {
//                modelId = hfpmDataFieldListByExample.get(0).getWorkfowModelId();
//            }
//        }

        mav.addObject("modelId", modelId);

        mav.setViewName("/modeler");
        return mav;
    }

    /**
     * 复制编辑中的流程定义
     * @return
     */
    @RequestMapping(value = "/activiti/copy_procdef.json")
    @ResponseBody
    public ResultData copyModel(String id){
        Model originModel = repositoryService.getModel(id);
        Model newModelData = repositoryService.newModel();

        ObjectNode modelObjectNode = new ObjectMapper().createObjectNode();
        modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, originModel.getName());
        modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, "");

        newModelData.setMetaInfo(modelObjectNode.toString());
        newModelData.setName(originModel.getName());

        repositoryService.saveModel(newModelData);

        repositoryService.addModelEditorSource(newModelData.getId(), repositoryService.getModelEditorSource(originModel.getId()));
        repositoryService.addModelEditorSourceExtra(newModelData.getId(), repositoryService.getModelEditorSourceExtra(originModel.getId()));

        return ResultData.success();
    }

    /**
     * 导出编辑中的流程定义
     * @return
     */
    @RequestMapping(value = "/activiti/export_procdef.json")
    @ResponseBody
    public void exportModel(String id, HttpServletResponse response){
        try {
            Model modelData = repositoryService.getModel(id);
            byte[] bpmnBytes = null;
            String filename = null;
            if (SimpleTableEditorConstants.TABLE_EDITOR_CATEGORY.equals(modelData.getCategory())) {
                WorkflowDefinition workflowDefinition = ExplorerApp.get().getSimpleWorkflowJsonConverter()
                        .readWorkflowDefinition(repositoryService.getModelEditorSource(modelData.getId()));

                filename = workflowDefinition.getName();
                WorkflowDefinitionConversion conversion =
                        ExplorerApp.get().getWorkflowDefinitionConversionFactory().createWorkflowDefinitionConversion(workflowDefinition);
                bpmnBytes = conversion.getBpmn20Xml().getBytes("utf-8");
            } else {
                JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
                BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
                BpmnModel model = jsonConverter.convertToBpmnModel(editorNode);
                filename = model.getMainProcess().getId() + ".bpmn20.xml";
                bpmnBytes = new BpmnXMLConverter().convertToXML(model);
            }

            ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
            response.setHeader("content-type", "application/xml");
            response.setHeader("content-disposition", "attachment;filename="+filename);

            OutputStream out = response.getOutputStream();
            int length = 0;
            byte[] buf = new byte[1024];
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
            out.flush();
            out.close();
        } catch(Exception e) {
            logger.error("failed to export model to BPMN XML", e);
        }

    }

    /**
     * 发布编辑中的流程定义
     * @return
     */
    @RequestMapping(value = "/activiti/deploy_procdef.json")
    @ResponseBody
    public ResultData deployModel(String id){
        try {
            Model model = repositoryService.getModel(id);
            final ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(model.getId()));
            BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(modelNode);
            byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(bpmnModel);

            String processName = model.getName() + ".bpmn20.xml";
            Deployment deployment = repositoryService.createDeployment()
                    .name(model.getName())
                    .addString(processName, new String(bpmnBytes))
                    .deploy();
            return ResultData.success();
        } catch (IOException e) {
            return ResultData.error(ResultCode.ERROR);
        }
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
        mav.addObject("modelId", modelId);
        mav.setViewName("/editor");
        return mav;
    }

    /**
     * 页面跳转
     * @return
     * @throws Throwable
     */
    @RequestMapping(value = "/activiti/approval.html")
    public ModelAndView diagramForwarder(@ModelAttribute("processInstanceId") String processInstanceId, @ModelAttribute("processDefinitionId") String processDefinitionId,

                                         HttpServletRequest request, HttpServletResponse response) throws Throwable {
        ModelAndView mav = new ModelAndView();
      HistoricProcessInstance historicProcessInstance = ProcessEngines.getDefaultProcessEngine().getHistoryService()
        .createHistoricProcessInstanceQuery().processInstanceId(processInstanceId)
                .singleResult();
        String businessKey = historicProcessInstance.getBusinessKey();
        String dataSet = historicProcessInstance.getProcessDefinitionKey();
        DataSetDescriptor processInfo = WebContext.get().getOnlyDataSetDescriptor(dataSet);
        String module = processInfo.getDataSet().getModule();
        mav.setViewName("redirect:/" + module + "/"  + dataSet + "_" + "edit" + ".html?" + JavaUtil.getJavaVarName(processInfo.getKeyField().getCode()) + "="
                + businessKey);
        return mav;
    }

    /**
     * 流程审批
     * @return
     * @throws Throwable
     */
    @RequestMapping(value = "/diagram-viewer/forwarder.html")
    public ModelAndView approval(@ModelAttribute("_DS") String dataSet, @ModelAttribute("_DI") String dataId,

                                         HttpServletRequest request, HttpServletResponse response) throws Throwable {
        ModelAndView mav = new ModelAndView();
        ProcessContext.ProcessInfo processInfo = WebContext.get().getProcessContext().getProcessInfo(dataSet);
        ProcessContext.ProcessDefinitionImage processDefinition = processInfo.getProcessDefinition();
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
