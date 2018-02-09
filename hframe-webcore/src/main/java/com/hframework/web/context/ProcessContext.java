package com.hframework.web.context;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.hframework.base.service.CommonDataService;
import com.hframework.common.frame.ServiceFactory;
import com.hframework.common.monitor.ConfigMonitor;
import com.hframework.common.monitor.Monitor;
import com.hframework.common.monitor.MonitorListener;
import com.hframework.common.util.JavaUtil;
import com.hframework.common.util.collect.CollectionUtils;
import com.hframework.common.util.collect.bean.Fetcher;
import com.hframework.common.util.collect.bean.Grouper;
import com.hframework.common.util.collect.bean.Mapper;
import com.hframework.web.config.bean.component.*;
import com.hframework.web.config.bean.dataset.Field;
import com.hframework.web.config.bean.mapper.Mapping;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.persistence.entity.SuspensionState;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.explorer.ui.process.DefaultProcessDefinitionFilter;
import org.activiti.explorer.util.XmlUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.*;

import static com.hframework.web.context.ComponentDataContainer.*;

/**
 * Created by zhangquanhong on 2018/2/3.
 */
public class ProcessContext {

    private static Logger logger = LoggerFactory.getLogger(ProcessContext.class);

    private ProcessMonitor processMonitor;

    private Map<String, String> processXmlCache = new HashMap<String, String>();

    private Map<String, ProcessInfo> processCache = new HashMap<String, ProcessInfo>();

    private static  Map<String, String> chineseAndButtonMapping;
    static {
        chineseAndButtonMapping = new HashMap<String, String>();
        chineseAndButtonMapping.put(null,"#EOFR.confirm.play");
        chineseAndButtonMapping.put("","#EOFR.confirm.play");
        chineseAndButtonMapping.put("启动","#EOFR.confirm.play");
        chineseAndButtonMapping.put("暂停", "#EOFR.confirm.pause");
        chineseAndButtonMapping.put("恢复", "#EOFR.confirm.play");
        chineseAndButtonMapping.put("关闭", "#EOFR.confirm.stop");
        chineseAndButtonMapping.put("process-diagram", "#EOFR.openPage.process.diagram");
        chineseAndButtonMapping.put("process-start", "#EOFR.confirm.process.start");
        chineseAndButtonMapping.put("process-approval", "#EOFR.pageFwd.process.approval");

    }

    public Map<String, String> getProcessXmlCache() {
        return processXmlCache;
    }

    public Map<String, ProcessInfo> getProcessCache() {
        return processCache;
    }
    public ProcessInfo getProcessInfo(String procKey) {
        return processCache.get(procKey);
    }

    public ProcessContext() {
        processMonitor = new ProcessMonitor(this);
    }


    public static class ProcessInfo{
        private String dataSetCode;
        private String dataSetKeyCode;
        private String processDataFieldCode;

        private BpmnModel bpmnModel;
        private ProcessDefinitionImage processDefinition;
        private String workflowExtAuthJson;
        private Map<String, ProcessNodeInfo> processNodeInfoMap;

        private Map<String, List<Event>> workflowEOFCEvents;
        private Map<String, List<ComponentDataContainer.EventElement>> workflowEOFCEventElements;

        private List<Event> workflowEOFREvents;
        private List<ComponentDataContainer.EventElement> workflowEOFREventElements;

        public ProcessInfo(ProcessDefinitionImage processDefinition, BpmnModel bpmnModel, Map<String, List<Object>> workflowExtAuth) {
            this.bpmnModel = bpmnModel;
            this.processDefinition = processDefinition;
            dataSetCode = processDefinition.getKey();
            processDataFieldCode = getDataFieldCodeFromDataSet(dataSetCode);
            dataSetKeyCode = getDataKeyFieldCodeFromDataSet(dataSetCode);
            initProcessNodeInfoMap(workflowExtAuth);

            this.workflowEOFREvents = new ArrayList<Event>();
            this.workflowEOFCEvents = new HashMap<String, List<Event>>();

            parseWorkflowEvents(bpmnModel, processDataFieldCode,  workflowEOFREvents, workflowEOFCEvents);
            workflowEOFREventElements = getElementsFromEvents(workflowEOFREvents, "qList");

            workflowEOFCEventElements = new HashMap<String, List<EventElement>>();
            for (String nodeKey : workflowEOFCEvents.keySet()) {
                workflowEOFCEventElements.put(nodeKey, getElementsFromEvents(workflowEOFCEvents.get(nodeKey), "eForm"));
            }
        }

        public void initProcessNodeInfoMap(Map<String, List<Object>> workflowExtAuth){
            String newJson = JSONObject.toJSONString(workflowExtAuth);
            if(newJson != null && !newJson.equals(workflowExtAuthJson)) {
                synchronized (this) {
                    if(newJson != null && !newJson.equals(workflowExtAuthJson)) {
                        Map<String, ProcessNodeInfo> processNodeInfoMap = new HashMap<String, ProcessNodeInfo>();
                        for (String nodeKey : workflowExtAuth.keySet()) {
                            processNodeInfoMap.put(nodeKey.matches("value-\\d+") ? nodeKey.replaceFirst("value-", "").trim() : nodeKey, buildProcessNodeInfo(workflowExtAuth.get(nodeKey)));
                        }
                        this.processNodeInfoMap = processNodeInfoMap;
                    }
                    workflowExtAuthJson = newJson;
                }

            }

        }

        public List<ComponentDataContainer.EventElement>  getElementsFromEvents(List<Event> events, String componentId){
                List<EventElement> eventElements = CollectionUtils.fetch(events, new Fetcher<Event, EventElement>() {
                    public EventElement fetch(Event event) {
                        Map<String, Event> eventStore = WebContext.get().getEvents();
                        if (StringUtils.isNotBlank(event.getRel())) {
                            String rel = event.getRel();
                            Event event1 = eventStore.get(rel);
                            if (event1 == null) {
                                logger.warn("event rel error : [rel = " + rel + " ]");
                            }
                            ComponentDataContainer.setEventDefaultDefinition(event, event1);
                        }
                        return new EventElement(event);
                    }
                });
            peddingEventElement(eventElements, componentId);
            return eventElements;
        }

        private void peddingEventElement(List<EventElement> workflowEventElements, String componentId) {
            DataSetDescriptor onlyDataSetDescriptor = WebContext.get().getOnlyDataSetDescriptor(dataSetCode);
            com.hframework.web.config.bean.Mapper mapper = WebContext.get().getMapper(dataSetCode, componentId);
            List<Mapping> mappingList1 = mapper.getEventMapper().getMappingList();
            for (Mapping mapping : mappingList1) {
                ComponentDataContainer.peddingEventElement(workflowEventElements, mapping, onlyDataSetDescriptor, null);
            }

        }

        private String getDataFieldCodeFromDataSet(String dataSetCode) {
            DataSetDescriptor onlyDataSetDescriptor = WebContext.get().getOnlyDataSetDescriptor(dataSetCode);
            Field workflowStatusField = onlyDataSetDescriptor.getWorkflowStatusField();
            if(workflowStatusField == null) {
                throw new RuntimeException("data set " + dataSetCode + " ] 's workflow status field is not exists !");
            }
            return JavaUtil.getJavaVarName(workflowStatusField.getCode());
        }
        private String getDataKeyFieldCodeFromDataSet(String dataSetCode) {
            DataSetDescriptor onlyDataSetDescriptor = WebContext.get().getOnlyDataSetDescriptor(dataSetCode);
            return JavaUtil.getJavaVarName(onlyDataSetDescriptor.getKeyField().getCode());
        }

        private void parseWorkflowEvents(BpmnModel bpmnModel, String dataFieldCode, List<Event> eOFREvents, Map<String, List<Event>> eOFCEvents) {
            Collection<FlowElement> flowElements = bpmnModel.getProcesses().get(0).getFlowElements();

            Event workflowStartEvent = new Event();
            StartEvent startEvent = new StartEvent();
            for (FlowElement flowElement : flowElements) {
                if (flowElement instanceof SequenceFlow) {
                    SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
                    Event event;
                    if(sequenceFlow.getSourceRef().equals(startEvent.getId())) {
                        event = workflowStartEvent;
                        event.setRel(chineseAndButtonMapping.get("process-start"));
                    }else {
                        event = new Event();
                        event.setRel(chineseAndButtonMapping.get(sequenceFlow.getName()));
                    }

                    String when = sequenceFlow.getSourceRef().matches("value-\\d+") ? sequenceFlow.getSourceRef().replaceFirst("value-","").trim() : "";
                    String then = sequenceFlow.getTargetRef().matches("value-\\d+") ? sequenceFlow.getTargetRef().replaceFirst("value-","").trim() : "";
                    PreHandle preHandle = new PreHandle();
                    preHandle.setCase1(String.valueOf(dataFieldCode));
                    preHandle.setWhen(when);
                    preHandle.setThen(then);
                    event.setPreHandleList(Lists.newArrayList(preHandle));
                    eOFREvents.add(event);

                    Event workflowEvent = getRuntimeEvent(sequenceFlow, when, then);
                    if(!eOFCEvents.containsKey(when)) eOFCEvents.put(when, new ArrayList<Event>());
                    eOFCEvents.get(when).add(workflowEvent);

                }else if(flowElement instanceof StartEvent) {
                    startEvent = (StartEvent) flowElement;
                }
            }

            Event event = new Event();
            event.setRel(chineseAndButtonMapping.get("process-approval"));
            eOFREvents.add(event);

            event = new Event();
            event.setRel(chineseAndButtonMapping.get("process-diagram"));
            eOFREvents.add(event);
        }

        private Event getRuntimeEvent(SequenceFlow sequenceFlow, String when, String then) {
            Event workflowEvent = new Event();
            workflowEvent.setName("confirm.process.approval");
            workflowEvent.setDescription(sequenceFlow.getName());

            AppendElement appendElement = new AppendElement();
            appendElement.setType("button");
            appendElement.setParam("{btnclass:\"btn-primary\",btnText:\" " + sequenceFlow.getName() + " \"}");
            Attach attach = new Attach();
            attach.setAnchor("EOFC");
            attach.setAppendElementList(Lists.newArrayList(appendElement));
            workflowEvent.setAttach(attach);
            Source source = new Source();
            source.setParam("thisForm");
            source.setScope("EOC");
            workflowEvent.setSource(source);

            List<Effect> effectList = new ArrayList<Effect>();

            Event templateApprovalEvent = WebContext.get().getEvents().get("#EOFC.confirm.process.approval");
            for (Effect effect : templateApprovalEvent.getEffectList()) {
                Effect newEffect = new Effect();

                newEffect.setType(effect.getType());
                newEffect.setContent(effect.getContent());
                if("ajaxSubmit".equals(effect.getType())) {
                    newEffect.setAction(effect.getAction() + "&_" + JavaUtil.getJavaVarName(processDataFieldCode) + "=" + when + "&" + JavaUtil.getJavaVarName(processDataFieldCode) + "=" + then);
                }else{
                    newEffect.setAction(effect.getAction());
                }
                effectList.add(newEffect);
            }
            workflowEvent.setEffectList(effectList);
            return workflowEvent;
        }

        public ProcessNodeInfo buildProcessNodeInfo(List<Object> objects){
            ProcessNodeInfo processNodeInfo = new ProcessNodeInfo();
            for (Object configObject : objects) {
                Map<String, String> config = (Map<String, String>)configObject;
                String authType = config.get("AUTH_TYPE_");
                String authTarget = config.get("AUTH_TARGET_");
                String authValue = config.get("AUTH_VALUE_");
                if("role".equals(authType)) {
                    processNodeInfo.setNodeRole(authTarget);
                }else if("comment".equals(authType)) {
                    processNodeInfo.setAllowComment("1".equals(authValue)? true : false);
                }else if("field".equals(authType)) {
                    if("1".equals(authValue)){
                        processNodeInfo.addAllowField(authTarget);
                    }else {
                        processNodeInfo.addForbidField(authTarget);
                    }
                }
            }
            return processNodeInfo;
        }

        public String getName() {
            return processDefinition.getName();
        }
        public String getKey() {
            return processDefinition.getKey();
        }
        public String getCategory() {
            return processDefinition.getCategory();
        }
        public String getId() {
            return processDefinition.getId();
        }

        public BpmnModel getBpmnModel() {
            return bpmnModel;
        }

        public ProcessDefinitionImage getProcessDefinition() {
            return processDefinition;
        }

        public Map<String, ProcessNodeInfo> getProcessNodeInfoMap() {
            return processNodeInfoMap;
        }

        public Map<String, List<Event>> getWorkflowEOFCEvents() {
            return workflowEOFCEvents;
        }

        public Map<String, List<EventElement>> getWorkflowEOFCEventElements() {
            return workflowEOFCEventElements;
        }

        public List<Event> getWorkflowEOFREvents() {
            return workflowEOFREvents;
        }

        public List<EventElement> getWorkflowEOFREventElements() {
            return workflowEOFREventElements;
        }

        public String getDataSetCode() {
            return dataSetCode;
        }

        public String getProcessDataFieldCode() {
            return processDataFieldCode;
        }

        public String getDataSetKeyCode() {
            return dataSetKeyCode;
        }
    }


    public static class ProcessMonitor implements MonitorListener<List<ProcessDefinitionImage>>{
        private static Logger logger = LoggerFactory.getLogger(ProcessMonitor.class);

        private ConfigMonitor<List<ProcessDefinitionImage>> config = null;
        private ConfigMonitor<Map<String, List<Object>>> extAuthConfigs = null;
        private ProcessContext processContext;

        public ProcessMonitor(ProcessContext processContext){
            this.processContext = processContext;
            config =  new ConfigMonitor<List<ProcessDefinitionImage>>(2) {
                @Override
                public List<ProcessDefinitionImage> fetch() throws Exception {
                    RepositoryService repositoryService = ProcessEngines.getDefaultProcessEngine().getRepositoryService();
                    final ProcessDefinitionQuery query = new DefaultProcessDefinitionFilter().getQuery(repositoryService);
                    List<ProcessDefinition> processDefinitions = query.listPage(0, Integer.MAX_VALUE);
                    return CollectionUtils.fetch(processDefinitions, new Fetcher<ProcessDefinition, ProcessDefinitionImage>() {
                        public ProcessDefinitionImage fetch(ProcessDefinition p) {
                            return new ProcessDefinitionImage(p.getId(), p.getKey(), p.getName(), p.getVersion(), p.getCategory(), p.getDeploymentId(), p.getResourceName(), p.getTenantId(), p.getDiagramResourceName());
                        }
                    });
                }
            };
            config.addListener(this);

            extAuthConfigs = new ConfigMonitor<Map<String, List<Object>>>(3) {
                @Override
                public Map<String, List<Object>> fetch() throws Exception {
                    return WorkflowUtils.getWorkflowAllExtAuth(ServiceFactory.getService(CommonDataService.class));
                }
            };
            extAuthConfigs.addListener(this);
            try {
                config.start();
                extAuthConfigs.start();
            } catch (Exception e) {
                logger.error(ExceptionUtils.getFullStackTrace(e));
            }
        }

        public void onEvent(Monitor monitor) throws ClassNotFoundException, Exception {
            if(monitor.getObject() instanceof List) {
                List<ProcessDefinitionImage> processDefinitionList = (List<ProcessDefinitionImage>) monitor.getObject();
                Map<String, ProcessDefinitionImage> newProcessDefinitionMap = CollectionUtils.convert(processDefinitionList, new Mapper<String, ProcessDefinitionImage>() {
                    public <K> K getKey(ProcessDefinitionImage processDefinition) {
                        return (K) processDefinition.getId();
                    }
                });

                for (String processId : newProcessDefinitionMap.keySet()) {
                    if(!processContext.getProcessXmlCache().containsKey(processId)){
                        addNewProcess(newProcessDefinitionMap.get(processId));
                    }
                }
            }else {
                Map<String, List<Object>> extAuths = (Map<String, List<Object>>) monitor.getObject();
                for (String procKey : extAuths.keySet()) {
                    Map<String, List<Object>> configMap = CollectionUtils.group(extAuths.get(procKey), new Grouper() {
                        public String groupKey(Object configObject) {
                            Map<String, String> config = (Map<String, String>) configObject;
                            return config.get("TASK_KEY_");
                        }
                    });
                    if(processContext.getProcessCache().containsKey(procKey)) {
                        processContext.getProcessCache().get(procKey).initProcessNodeInfoMap(configMap);
                    }

                }

            }

        }

        private void addNewProcess(ProcessDefinitionImage processDefinition) {
            try {
                BpmnModel bpmnModel = getBpmnModelByProcessDefinition(processDefinition);
                CommonDataService service = ServiceFactory.getService(CommonDataService.class);
                Map<String, List<Object>> workflowExtAuth = WorkflowUtils.getWorkflowExtAuth(service, processDefinition.getCategory(), processDefinition.getName(), processDefinition.getKey());
                ProcessInfo processInfo = new ProcessInfo(processDefinition, bpmnModel, workflowExtAuth);
                processContext.getProcessCache().put(processInfo.getKey(), processInfo);
            } catch (XMLStreamException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private BpmnModel getBpmnModelByProcessDefinition(ProcessDefinitionImage processDefinition) throws XMLStreamException {
            RepositoryService repositoryService = ProcessEngines.getDefaultProcessEngine().getRepositoryService();
            final InputStream definitionStream = repositoryService.getResourceAsStream(
                    processDefinition.getDeploymentId(), processDefinition.getResourceName());
            XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
            XMLStreamReader xtr = xif.createXMLStreamReader(definitionStream);
            BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);
            return bpmnModel;
        }
    }

    public static class ProcessNodeInfo{
        private String nodeKey;
        private String nodeRole;
        private Set<String> allowFields;
        private Set<String> forbidFields;
        private boolean allowComment;
        private  Map<String, List<Object>> workflowExtAuth;

        public String getNodeKey() {
            return nodeKey;
        }

        public void setNodeKey(String nodeKey) {
            this.nodeKey = nodeKey;
        }

        public String getNodeRole() {
            return nodeRole;
        }

        public void setNodeRole(String nodeRole) {
            this.nodeRole = nodeRole;
        }

        public Set<String> getAllowFields() {
            return allowFields;
        }

        public void setAllowFields(Set<String> allowFields) {
            this.allowFields = allowFields;
        }

        public Set<String> getForbidFields() {
            return forbidFields;
        }

        public void setForbidFields(Set<String> forbidFields) {
            this.forbidFields = forbidFields;
        }

        public Map<String, List<Object>> getWorkflowExtAuth() {
            return workflowExtAuth;
        }

        public void setWorkflowExtAuth(Map<String, List<Object>> workflowExtAuth) {
            this.workflowExtAuth = workflowExtAuth;
        }

        public boolean isAllowComment() {
            return allowComment;
        }

        public void setAllowComment(boolean allowComment) {
            this.allowComment = allowComment;
        }

        public void addAllowField(String field) {
            if(allowFields == null) allowFields = new HashSet<String>();
            allowFields.add(field);
        }

        public void addForbidField(String field) {
            if(forbidFields == null) forbidFields = new HashSet<String>();
            forbidFields.add(field);
        }
    }

    public static class ProcessDefinitionImage{
        protected String id;
        protected String name;
        protected String key;
        protected int revision = 1;
        protected int version;
        protected String category;
        protected String deploymentId;
        protected String resourceName;
        protected String tenantId = ProcessEngineConfiguration.NO_TENANT_ID;
        protected String diagramResourceName;
        protected boolean isGraphicalNotationDefined;
        protected int suspensionState = SuspensionState.ACTIVE.getStateCode();
        protected boolean isIdentityLinksInitialized = false;

        public ProcessDefinitionImage(String id, String key, String name, int version, String category, String deploymentId,
                                      String resourceName, String tenantId, String diagramResourceName) {
            this.id = id;
            this.key = key;
            this.name = name;
//            this.revision = revision;
            this.version = version;
            this.category = category;
            this.deploymentId = deploymentId;
            this.resourceName = resourceName;
            this.tenantId = tenantId;
            this.diagramResourceName = diagramResourceName;
//            this.isGraphicalNotationDefined = isGraphicalNotationDefined;
//            this.suspensionState = suspensionState;
//            this.isIdentityLinksInitialized = isIdentityLinksInitialized;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getRevision() {
            return revision;
        }

        public void setRevision(int revision) {
            this.revision = revision;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getDeploymentId() {
            return deploymentId;
        }

        public void setDeploymentId(String deploymentId) {
            this.deploymentId = deploymentId;
        }

        public String getResourceName() {
            return resourceName;
        }

        public void setResourceName(String resourceName) {
            this.resourceName = resourceName;
        }

        public String getTenantId() {
            return tenantId;
        }

        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }

        public String getDiagramResourceName() {
            return diagramResourceName;
        }

        public void setDiagramResourceName(String diagramResourceName) {
            this.diagramResourceName = diagramResourceName;
        }

        public boolean isGraphicalNotationDefined() {
            return isGraphicalNotationDefined;
        }

        public void setIsGraphicalNotationDefined(boolean isGraphicalNotationDefined) {
            this.isGraphicalNotationDefined = isGraphicalNotationDefined;
        }

        public int getSuspensionState() {
            return suspensionState;
        }

        public void setSuspensionState(int suspensionState) {
            this.suspensionState = suspensionState;
        }

        public boolean isIdentityLinksInitialized() {
            return isIdentityLinksInitialized;
        }

        public void setIsIdentityLinksInitialized(boolean isIdentityLinksInitialized) {
            this.isIdentityLinksInitialized = isIdentityLinksInitialized;
        }
    }

}
