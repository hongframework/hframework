package com.hframework.web.context;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.hframework.common.util.EnumUtils;
import com.hframework.common.util.JavaUtil;
import com.hframework.common.util.ReflectUtils;
import com.hframework.common.util.StringUtils;
import com.hframework.common.util.collect.CollectionUtils;
import com.hframework.common.util.collect.bean.Fetcher;
import com.hframework.common.util.file.FileUtils;
import com.hframework.common.util.message.XmlUtils;
import com.hframework.web.CreatorUtil;
import com.hframework.web.config.bean.*;
import com.hframework.web.config.bean.component.Event;
import com.hframework.web.config.bean.component.PreHandle;
import com.hframework.web.config.bean.dataset.*;
import com.hframework.web.config.bean.module.Page;
import com.hframework.web.config.bean.pagetemplates.Element;
import com.hframework.web.config.bean.pagetemplates.Pagetemplate;
import com.hframework.web.context.enums.ElementType;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * User: zhangqh6
 * Date: 2016/5/11 0:31:31
 */
public class WebContext {
    private static Logger logger = LoggerFactory.getLogger(WebContext.class);

    private static WebContext context = new WebContext();

    private Map<String, List<Page>> descriptorSubPages = new HashMap<String, List<Page>>();

    private static  Map<String, String> chineseAndButtonMapping;
    {
        chineseAndButtonMapping = new HashMap<String, String>();
        chineseAndButtonMapping.put(null,"#EOFR.confirm.play");
        chineseAndButtonMapping.put("","#EOFR.confirm.play");
        chineseAndButtonMapping.put("启动","#EOFR.confirm.play");
        chineseAndButtonMapping.put("暂停", "#EOFR.confirm.pause");
        chineseAndButtonMapping.put("恢复", "#EOFR.confirm.play");
        chineseAndButtonMapping.put("关闭", "#EOFR.confirm.stop");
        chineseAndButtonMapping.put("process-diagram", "#EOFR.openPage.process.diagram");
        chineseAndButtonMapping.put("process-start", "#EOFR.confirm.process.start");


    }

    private Map<String, com.hframework.web.config.bean.module.Component> defaultComponentMap
            = new HashMap<String, com.hframework.web.config.bean.module.Component>();
    private Map<String, com.hframework.web.config.bean.module.Element> defaultElementMap
            = new HashMap<String, com.hframework.web.config.bean.module.Element>();

    private WebContextHelper contextHelper;

    //项目信息
    private Program program;
    //模块信息
    private Map<String, Module> modules = new HashMap<String, Module>();
    //组件映射信息
    private Map<String, Mapper> mappers = new HashMap<String, Mapper>();

    //页面模板信息
    private Map<String, Pagetemplate> pageTemplates = new HashMap<String, Pagetemplate>();
    //组件信息
    private Map<String, Component> components = new HashMap<String, Component>();

    private Map<String, Event> events = new HashMap<String, Event>();

    private Map<String, Map<String, PageDescriptor>> pageSetting = new HashMap<String, Map<String, PageDescriptor>>();

    private static ProcessContext processContext;

    private static DataSetDescriptor SYSTEM_EMPTY_DATASET = null;
    {
        DataSet dataSet = new DataSet();
        dataSet.setCode("SYSTEM_EMPTY_DATASET");
        dataSet.setName("");
        dataSet.setModule("");
        Fields fields = new Fields();
        fields.setFieldList(new ArrayList<Field>());
        dataSet.setFields(fields);
        SYSTEM_EMPTY_DATASET = new DataSetDescriptor(dataSet);
    }

    private Map<String, DataSetDescriptor> dataSets = new HashMap<String, DataSetDescriptor>(){{
        put("SYSTEM_EMPTY_DATASET", SYSTEM_EMPTY_DATASET);
    }};

    private Map<Class, DataSetDescriptor> dataSetCache = new HashMap<Class, DataSetDescriptor>();

    private Map<String,Object[]> processsCache = new HashMap<String, Object[]>();

    private Map<DataSetDescriptor, List<ComponentDescriptor>> dataSetComponentApplyCache = new HashMap<DataSetDescriptor, List<ComponentDescriptor>>();

    private boolean flushAuto = false;

    public WebContext() {
        this(null, null, null, true);
    }
    public WebContext(String companyCode, String programCode, String templateCode) {
        this(companyCode,programCode,templateCode, false);
    }

    public ProcessContext getProcessContext(){
        try {
            if(processContext == null) {
                synchronized (WebContext.class) {
                    if(processContext == null) {
                        Class<? extends ProcessContext> processContextClass = (Class<? extends ProcessContext>) Class.forName("com.hframework.web.context.ProcessContext");
                        processContext = processContextClass.newInstance();
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return processContext;
    }


    public WebContext(String companyCode, String programCode, String templateCode, boolean flushAuto) {
        try {
            logger.info("web context init{} ..", companyCode, programCode, templateCode);
            this.flushAuto = flushAuto;
            init(companyCode, programCode, templateCode);
            logger.info("web context init ok !", JSONObject.toJSONString(context));
        } catch (Exception e) {
            logger.error("web context init error : ", e);
        }
    }



    public void init(String companyCode, String programCode, String templateCode) throws Exception {
        if(StringUtils.isNotBlank(companyCode) && StringUtils.isNotBlank(programCode)) {
            contextHelper = new WebContextHelper(companyCode,programCode,null,templateCode);
        }else {
            contextHelper = new WebContextHelper();
        }

        //加载项目配置
        loadComponentConfig();

        //加载数据集信息
        loadDataSet();
        //加载数据集信息
        loadDataSetHelper();

        //加载数据集连带规则信息
        loadDataSetRuler();

        //加载流程
//        loadProcess();

        for (DataSetDescriptor dataSetDescriptor : dataSets.values()) {
            dataSetDescriptor.setDataSetRulers();
        }


        //页面架构数据初始化
        pageSettingInitial();

        WebContextMonitor webContextMonitor = new WebContextMonitor(this);
        webContextMonitor.addRootConfMap(DataSet.class, XmlUtils.class.getResource("/" + contextHelper.programConfigDataSetDir).getPath());
        webContextMonitor.start();

    }

    /**
     * 由于工作流的改版后，所有的bpmn配置文件直接通过管理台运行态落库与发布，因此系统启动时静态加载流程被弃用，
     * 而对应的流程可进行的操作也将调用对应页面时动态计算，不再一次性计算
     * @throws IOException
     * @throws XMLStreamException
     */
    @Deprecated
    private void loadProcess() throws IOException, XMLStreamException {
//        File dictionaryFile = null;
//        if(new File(contextHelper.programConfigRootDir).exists()) {
//            dictionaryFile = new File(contextHelper.programConfigRootDir + "/" + contextHelper.programConfigDataSetDir + "/" + "process");
//        }else {
//            dictionaryFile = new File(XmlUtils.class.getResource("/").getPath() + "/" + contextHelper.programConfigDataSetDir + "/" + "process");
//        }
        File[] fileList = XmlUtils.readValuesFromDirectory(contextHelper.programConfigRootDir, contextHelper.programConfigDataSetDir + "/" + "process",  ".xml");
        if(fileList == null) return;
        for (File file : fileList) {
            String fileName = file.getName();
            String reallyFileName = fileName.replace(".bpmn20.xml", "");
            String dataSetCode = reallyFileName.substring(0, reallyFileName.lastIndexOf("."));
            String dataFieldCode = reallyFileName.substring(reallyFileName.lastIndexOf(".") + 1);
            String processContext = FileUtils.readFile(file.getPath());
            InputStream bpmnStream = new ByteArrayInputStream(processContext.getBytes("UTF-8"));
            XMLInputFactory xif = org.activiti.explorer.util.XmlUtil.createSafeXmlInputFactory();
            InputStreamReader in = new InputStreamReader(bpmnStream, "UTF-8");
            XMLStreamReader xtr = xif.createXMLStreamReader(in);
            BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);
            Collection<FlowElement> flowElements = bpmnModel.getProcesses().get(0).getFlowElements();

            List<Event> workflowEvents = new ArrayList<Event>();
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
                    workflowEvents.add(event);
                }else if(flowElement instanceof StartEvent) {
                    startEvent = (StartEvent) flowElement;
                }
            }

            Event event = new Event();
            event.setRel(chineseAndButtonMapping.get("process-diagram"));
            workflowEvents.add(event);
            processsCache.put(dataSetCode, new Object[]{fileName, dataSetCode, dataFieldCode, flowElements, workflowEvents, workflowStartEvent});

            List<Deployment> list = ProcessEngines.getDefaultProcessEngine().getRepositoryService().
                    createDeploymentQuery().deploymentName(dataSetCode).list();
            if(list != null && list.size() > 0) {
                List<ProcessDefinition> processDefinitions = ProcessEngines.getDefaultProcessEngine().getRepositoryService().
                        createProcessDefinitionQuery().deploymentIds(new HashSet<String>(CollectionUtils.fetch(list, new Fetcher<Deployment, String>() {
                    public String fetch(Deployment deployment) {
                        return deployment.getId();
                    }
                }))).latestVersion().list();
                if(processDefinitions != null && processDefinitions.size() > 0) {
                    for (ProcessDefinition processDefinition : processDefinitions) {
                    }
                }
            }else {
                ProcessEngines.getDefaultProcessEngine().getRepositoryService().createDeployment()
                        .name(dataSetCode)
                        .addClasspathResource(contextHelper.programConfigDataSetDir + "/process/" + fileName)
                        .deploy();
            }
        }


    }

    private void loadDataSet() throws Exception {

        //加载数据源信息
        List<DataSet> dataSetList = XmlUtils.readValuesFromDirectory(contextHelper.programConfigRootDir, contextHelper.programConfigDataSetDir, DataSet.class, ".xml");
        for (DataSet dataSet : dataSetList) {
            DataSetDescriptor dataSetDescriptor = new DataSetDescriptor(dataSet);
            if(dataSet.getEntityList() != null && dataSet.getEntityList().size() > 0) {
                Entity mainEntity = dataSet.getEntityList().get(0);
                if(StringUtils.isNotBlank(mainEntity.getCode()) && StringUtils.isNotBlank(mainEntity.getValue())) {
                    dataSetDescriptor.setColumnTableKeyAndValue(new String[]{
                            JavaUtil.getJavaVarName(mainEntity.getCode().trim()),
                            JavaUtil.getJavaVarName(mainEntity.getValue().trim()),
                            mainEntity.getModule()});
                }

            }

            if(dataSet.getDescriptor() != null) {
                Map<String, DataSetDescriptor> dataSetCodeMap = new HashMap<String, DataSetDescriptor>();
                if(dataSet.getDescriptor().getFieldsList() != null) {
                    for (Fields fields :  dataSet.getDescriptor().getFieldsList()) {
                        if(StringUtils.isNotBlank(fields.getExtend())) {
                            boolean extendExists = false;
                            for (Fields otherFields :  dataSet.getDescriptor().getFieldsList()) {
                                if(fields.getExtend().equals(otherFields.getCode())) {
                                    extendExists = true;
                                    if(fields.getFieldList() != null) {
                                        fields.getFieldList().addAll(otherFields.getFieldList());
                                    }else {
                                        fields.setFieldList(Lists.newArrayList(otherFields.getFieldList()));
                                    }
                                    if(StringUtils.isBlank(fields.getName())) fields.setName(otherFields.getName());
                                    break;
                                }
                            }
                            if(!extendExists) throw new RuntimeException(fields.getCode() + "extend " + fields.getExtend() + "is not exists !");
                        }
                    }
                    for (Fields fields :  dataSet.getDescriptor().getFieldsList()) {
                        DataSet tmpDataSet = new DataSet();
                        tmpDataSet.setCode(dataSet.getCode() + "#" + fields.getCode());
                        tmpDataSet.setName(fields.getName());
                        tmpDataSet.setModule(dataSet.getModule());
                        tmpDataSet.setSource(dataSet.getSource());
                        tmpDataSet.setFields(fields);
                        DataSetDescriptor tempDataSetDescriptor = new DataSetDescriptor(tmpDataSet);
                        dataSets.put(dataSet.getModule() + "/" + dataSet.getCode() + "#" + fields.getCode(), tempDataSetDescriptor);
                        dataSetCodeMap.put(fields.getCode(), tempDataSetDescriptor);
                    }
                }

                HelperDatas helperDatas = dataSet.getDescriptor().getHelperDatas();

                if(helperDatas != null && helperDatas.getHelperDatas() != null) {
                    boolean isRuntime = false;
                    for (HelperData helpData : helperDatas.getHelperDatas()) {
                        if(StringUtils.isNotBlank(helpData.getEmbedClass())
                                && StringUtils.isNotBlank(helpData.getEmbedMethod())
                                && ("runtime".equals(helpData.getEmbedType()) || "ajax".equals(helpData.getEmbedType()))) {
                            isRuntime |= true;
                        }
                        if(isRuntime) {
                            dataSetDescriptor.setHelperRuntime(true);
                        }else{
                            dataSetDescriptor.resetHelperInfo(false);
                        }
                    }
//                    JSONObject helpTags = new JSONObject(true);
//                    DOMElement root = null;
//                    for (HelperData helpData : helperDatas.getHelperDatas()) {
//                        String targetId = helpData.getTargetId();
//                        String[] nodes = StringUtils.split(targetId, ".");
//                        if(root == null) {
//                            Map blankMap = new HashMap();
//                            root = Dom4jUtils.createElement(nodes[0], blankMap);
//                        }
//                        addElementToDomElement(root, Arrays.copyOfRange(nodes, 0, nodes.length - 1), helpData.getHelpLabels());
//                        String xml = root.asXML();
//                        dataSetDescriptor.setHelperDataXml(xml);
//
//                        JSONObject helpTag = new JSONObject(true);
//                        helpTags.put(dataSet.getCode() + "#" + targetId, helpTag);
//                        int count = 0;
//                        for (HelperLabel helperLabel : helpData.getHelpLabels()) {
//                            JSONObject helpLabel = new JSONObject(true);
//                            helpTag.put(helperLabel.getName(), helpLabel);
//                            for (int i = 0; i < helperLabel.getHelpItems().size(); i++) {
//                                helpLabel.put(helperLabel.getHelpItems().get(i).getName(), count ++);
//                            }
//                        }
//                        dataSetDescriptor.setHelperTags(helpTags);
//                    }
                }

               if(dataSet.getDescriptor().getNode() != null) {
                   Node rootNode = dataSet.getDescriptor().getNode();
                   Set<String> virtualContainerSubNodePath = new HashSet<String>();
                   rootNode.calcPath();
                   IDataSet iDataSet = calculateNodeGrid(rootNode, dataSetCodeMap, virtualContainerSubNodePath);
                   dataSetDescriptor.setDateSetStruct(iDataSet);

                   dataSetDescriptor.setVirtualContainerSubNodePath(virtualContainerSubNodePath);
                   List<Event> events = new ArrayList<Event>();
                   Page page = initFileStructPage(events, dataSet.getModule(), dataSet.getCode() + "#", rootNode, dataSet.getModule() + "/" + dataSet.getCode(), true);

                   com.hframework.web.config.bean.module.Component component = new com.hframework.web.config.bean.module.Component();
                   component.setId("qList");
                   component.setDataSet("SYSTEM_EMPTY_DATASET");
                   component.setShowTitle("false");
                   component.setEventList(events);
                   page.getComponentList().add(0, component);
                   logger.info("init File Struct Page: {} => {}", dataSet.getModule() + "/" + dataSet.getCode(), descriptorSubPages.get(dataSet.getModule() + "/" + dataSet.getCode()));;
//                   System.out.println(iDataSet);
               }
            }

            dataSets.put(dataSet.getModule() + "/" + dataSet.getCode(), dataSetDescriptor);
            com.hframework.beans.class0.Class defPoClass = CreatorUtil.getDefPoClass(program.getCompany(),
                    program.getCode(), dataSet.getModule(), dataSet.getCode());
            if(dataSet.getCode().equals(dataSet.getEventObjectCode())) {
                try{
                    Class<?> aClass = Class.forName(defPoClass.getClassPath());
                    dataSetCache.put(aClass, dataSetDescriptor);
                }catch (Exception e) {
//                    e.printStackTrace();
                }

            }else {
                //针对于查询类的dateset无需缓存
            }
        }

        for (DataSetDescriptor dataSetDescriptor : dataSetCache.values()) {
            List<Field> fieldList = dataSetDescriptor.getDataSet().getFields().getFieldList();
            for (Field field : fieldList) {
                if(field.getRel() != null && field.getRel().getEntityCode() != null) {
                    String entityCode = field.getRel().getEntityCode();
                    String dataSetCode = entityCode.substring(0, entityCode.indexOf("/"));
                    String relFieldCode = entityCode.substring(entityCode.indexOf("/") + 1, entityCode.lastIndexOf("/"));
                    DataSetDescriptor relDataSet = this.getOnlyDataSetDescriptor(dataSetCode);
                    com.hframework.beans.class0.Class relPoClass =
                            CreatorUtil.getDefPoClass(program.getCompany(),
                                    program.getCode(), relDataSet.getDataSet().getModule(), dataSetCode);
                    DataSetDescriptor relDataSetDescriptor = dataSetCache.get(Class.forName(relPoClass.getClassPath()));
                    dataSetDescriptor.addRelDataSet(field.getCode(), dataSetCode + "/" + relFieldCode, relDataSetDescriptor);
                }
            }
        }
    }



    private Page initFileStructPage(List<Event> events, String module, String pageId, Node rootNode, String parentDataSetCode, boolean addFirstTime) {
        Page page = new Page();
        page.setId(pageId.endsWith("#") ? pageId : (pageId + "#"));
        page.setPageTemplate("dynamic");
        List<com.hframework.web.config.bean.module.Component> componentList = new ArrayList<com.hframework.web.config.bean.module.Component>();
        List<Node> nodeList = rootNode.getNodeList();
        if(nodeList == null) return page;
        page.setComponentList(componentList);

        if(addFirstTime) {
            pageId = pageId +  (pageId.endsWith("#") ? "" : ".") +
                    (rootNode.getCode().endsWith("[]") ? rootNode.getCode().substring(0, rootNode.getCode().length() - 2) : rootNode.getCode());
        }

        for (Node node : nodeList) {
            addNode(componentList,events, node, module, pageId, parentDataSetCode);
        }
        if(!descriptorSubPages.containsKey(parentDataSetCode)) {
            descriptorSubPages.put(parentDataSetCode, new ArrayList<Page>());
        }
        descriptorSubPages.get(parentDataSetCode).add(page);
        return page;


    }

    private void addNode(List<com.hframework.web.config.bean.module.Component> componentList, List<Event> events, Node node, String module, String pageId, String parentDataSetCode) {
        String componentId = node.getEditor();
        String subComponentId = null;
        if(node.getEditor().contains(",")) {
            componentId = node.getEditor().split(",")[0];
            subComponentId = node.getEditor().split(",")[1];
        }
        String dataSetCode = pageId +  (pageId.endsWith("#") ? "" : ".") +
                (node.getCode().endsWith("[]") ? node.getCode().substring(0, node.getCode().length() - 2) : node.getCode());

        com.hframework.web.config.bean.module.Component component = new com.hframework.web.config.bean.module.Component();
        component.setId(componentId);
        component.setTitle(node.getName());
        component.setDataSet(module + "/" + dataSetCode);
        componentList.add(component);
        List<Node> subNodeList = node.getNodeList();
        if(subNodeList == null) return ;

        String eventKeys = node.getEvents();
        if(StringUtils.isNotBlank(eventKeys)) {
            String[] eventKeyArray = StringUtils.split(eventKeys, ",");
            for (String eventKey : eventKeyArray) {
                events.add(getEventByEventKey(eventKey, node.getName(), dataSetCode));
            }
        }
        if(StringUtils.isBlank(subComponentId)) {
            for (Node subNode : subNodeList) {
                addNode(componentList, events, subNode, module, dataSetCode, parentDataSetCode);
            }
        }else {
            events = new ArrayList<Event>();
            Page page = initFileStructPage(events, module, dataSetCode, node, parentDataSetCode, false);
            com.hframework.web.config.bean.module.Component component2 = new com.hframework.web.config.bean.module.Component();
            component2.setId(subComponentId);
            component2.setTitle(node.getName());
            component2.setDataSet(module + "/" + dataSetCode);
            component2.setEventList(events);
            page.getComponentList().add(0, component2);
        }

    }

    private Event getEventByEventKey(String eventKey, String name, String path) {

        String visible = eventKey.toUpperCase().endsWith(".SHOW") ? "show": "auto";
        String string = null;
        if(eventKey.startsWith("TOGGLE")) {
            string = "<event name=\""+ path +"\">\n" +
        "                <attach anchor=\"BOFC\">\n" +
        "                    <appendElement type=\"button\" param='{btnclass:\"btn-primary switch-button\",btnText:\"" + name + "\"}'></appendElement>\n" +
        "                </attach>\n" +
        "                <effect type=\"componentControl\" target-id=\""+ path  +"\" param='{visible:\"" + visible + "\", event:\"toggle\", show_condition:\"IS_NOT_EMPTY\"}'></effect>\n" +
        "                <effect type=\"scrollIntoView\"  target-id=\""+ path  +"\"></effect>\n" +
        "            </event>";
        }else if(eventKey.startsWith("ADD_ROW")) {
            string = "<event name=\"" + path + "\">\n" +
        "                <attach anchor=\"BOFC\">\n" +
        "                    <appendElement type=\"button\" param='{btnclass:\"btn-primary switch-button\",btnText:\"添加" + name + "\"}'></appendElement>\n" +
        "                </attach>\n" +
        "                <effect type=\"componentControl\" target-id=\"" + path + "\"  param='{visible:\"" + visible + "\", event:\"component.row.add\", show_condition:\"IS_NOT_EMPTY\"}'></effect>\n" +
        "                <effect type=\"scrollIntoView\"  target-id=\"" + path + "\"></effect>\n" +
        "            </event>";
        }
        return XmlUtils.readValue(string, Event.class);
    }

    public static boolean isList(Node node) {
        if(node.getName().endsWith("[]")) {
            return true;
        }
        return false;
    }

    public static String getNodeName(Node node) {
        if(node.getName().endsWith("[]")) {
            return node.getName().substring(0,node.getName().length()-2);
        }
        return node.getName();
    }

    private IDataSet calculateNodeGrid(Node node, Map<String, DataSetDescriptor> dataSetCodes, Set<String> virtualContainerSubNodePath) {

        DataSetInstance curDataSetInstance = null;
        List<DataSetInstance> subDataSetInstances = new ArrayList<DataSetInstance>();
        List<DataSetInstance> dataSetInstances = new ArrayList<DataSetInstance>();
        List<DataSetContainer> dataSetContainers = new ArrayList<DataSetContainer>();

        List<IDataSet> sortedDataSetObjects = new ArrayList<IDataSet>();

        if(dataSetCodes.containsKey(node.getPath())) {
            curDataSetInstance = DataSetInstance.valueOf(node);
            dataSetInstances.add(curDataSetInstance);
            sortedDataSetObjects.add(curDataSetInstance);
        }

        List<Node> nodeList = node.getNodeList();

        if(nodeList != null && nodeList.size() > 0) {
            for (Node subNode : nodeList) {
                IDataSet result = calculateNodeGrid(subNode, dataSetCodes, virtualContainerSubNodePath);
                sortedDataSetObjects.add(result);
                if (result instanceof DataSetInstance) {
                    subDataSetInstances.add((DataSetInstance) result);
                    dataSetInstances.add((DataSetInstance) result);
                }else {
                    dataSetContainers.add((DataSetContainer) result);
                }
            }
        }


        if(dataSetContainers.size() == 0) {
            if(dataSetInstances.size() == 0) {//该场景原则上不会出现
                throw new RuntimeException(node.getPath() + " or children data set not exists ");
            }else if(dataSetInstances.size() == 1) {//该场景原则上出现为叶子节点
                return dataSetInstances.get(0);
            }else {//该场景为：① 1个父节点+>= 1的子节点；② >= 2的子节点;涉及多个数据集，原则上是需要合并，所以先合并
                DataSetContainer dataSetContainer = DataSetContainer.valueOf(node, dataSetInstances);
                dataSetContainer.setElementList(sortedDataSetObjects);
                for (IDataSet sortedDataSetObject : sortedDataSetObjects) {
                    if(sortedDataSetObject.getNode().getEditor().startsWith("virtualContainer,")){
                        dataSetContainer.setVirtualContainer(true);
                        for (int i = 0; i < node.getNodeList().size(); i++) {
                            virtualContainerSubNodePath.add("/" + node.getNodeList().get(i).getPath().replaceAll("\\.", "/"));
                        }

                    }
                }
                return dataSetContainer;
//                boolean islist = false;
//                for (DataSetInstance subDataSetInstance : subDataSetInstances) {
//                    islist  = isList(subDataSetInstance.getNode()) || islist;
//                }
//
//                if(curDataSetInstance != null) {//当前节点有数据，叶子节点也有数据 ==> 合
//                    return DataSetContainer.valueOf(dataSetInstances);
//                }else {//同级别多个叶子节点 ==> 合
//                    return DataSetContainer.valueOf(dataSetInstances);
//                }

            }
        }else {
            boolean isMany = false;
            for (DataSetContainer subDataSetContainer : dataSetContainers) {//判断是否存在多个元素子容器
                isMany = isMany || subDataSetContainer.isMany();
            }

            if(isMany && dataSetContainers.size() + dataSetInstances.size() > 1) {//如果存在子容器多元素且存在两个及以上元素，需要成立新的容器
                DataSetContainer dataSetContainer = DataSetContainer.valueOf(node, dataSetInstances, dataSetContainers);
                dataSetContainer.setElementList(sortedDataSetObjects);
                return dataSetContainer;
            }else if(dataSetContainers.size() + dataSetInstances.size() == 1) {//如果只存在一个子容器且只有一个元素，重新赋值子容器元素
                if(!isMany) {
                    dataSetContainers.get(0).setNode(node);
                }
                return dataSetContainers.get(0);
            }else {//存在两个容器以上且不是都是单元素容器，合并容器为一个容器

                DataSetContainer dataSetContainer = DataSetContainer.valueOf(node, dataSetInstances);

                for (DataSetContainer subDataSetContainer : dataSetContainers) {
                    dataSetContainer.addDataAll(subDataSetContainer.getDatas());
                    dataSetContainer.addContainerAll(subDataSetContainer.getSubDataSetContainers());
                }

                for (IDataSet result : sortedDataSetObjects) {
                    if (result instanceof DataSetInstance) {
                        dataSetContainer.getElementList().add(result);
                    }else {
                        dataSetContainer.getElementList().addAll(((DataSetContainer) result).getElementList());
                    }
                }
                return dataSetContainer;
            }

        }

//        if(dataSetContainers.size() == 1) {
//            dataSetContainers.get(0).addDataAll(subDataSetInstances);
//            return dataSetContainers.get(0);
//        }
//
//        DataSetContainer dataSetContainer = new DataSetContainer();
//        dataSetContainer.setSubDataSetContainers(dataSetContainers);
//        dataSetContainer.setDatas(subDataSetInstances);
//        return dataSetContainer;

    }

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







    private void loadDataSetHelper() throws Exception {

        List<DataSetHelper> dataSetHelperList = XmlUtils.readValuesFromDirectory(contextHelper.programConfigRootDir, contextHelper.programConfigDataSetHelperDir, DataSetHelper.class, ".xml");
        for (DataSetHelper dataSetHelper : dataSetHelperList) {
            String effectModuleCode = dataSetHelper.getEffectModule();
            String effectDatasetCode = dataSetHelper.getEffectDataset();
            DataSetDescriptor dataSetDescriptor = dataSets.get(effectModuleCode + "/" + effectDatasetCode);
            dataSetDescriptor.addDataSetHelper(dataSetHelper);
        }
    }

    private void loadDataSetRuler() throws Exception {

        List<DataSetRuler> dataSetRulers = XmlUtils.readValuesFromDirectory(contextHelper.programConfigRootDir, contextHelper.programConfigDataSetRulerDir, DataSetRuler.class, ".xml");
        for (DataSetRuler dataSetRuler : dataSetRulers) {
            String module = dataSetRuler.getModule();
            String entity = dataSetRuler.getEntity();
            DataSetDescriptor dataSetDescriptor = dataSets.get(module + "/" + entity);
            dataSetDescriptor.addDataSetRuler(dataSetRuler);
        }
    }

    private void pageSettingInitial() throws Exception {
        for (String moduleCode : modules.keySet()) {
            pageSetting.put(moduleCode, new HashMap<String, PageDescriptor>());
            Module module = modules.get(moduleCode);
            List<Page> pageList = module.getPageList();
            if(pageList == null) {
                continue;
            }

            boolean useDescriptorSubPages = false;
            for (Page page : pageList) {
                for (com.hframework.web.config.bean.module.Component component : page.getComponentList()) {
                    if(descriptorSubPages.containsKey(component.getDataSet())) {
                        useDescriptorSubPages = true;
                        break;
                    }
                }
            }
            if(useDescriptorSubPages) {
                for (List<Page> pages : descriptorSubPages.values()) {
                    pageList.addAll(0, pages);
                }

            }

            for (Page page : pageList) {
                if(StringUtils.isNotBlank(page.getDataSet()) && page.getDataSet().startsWith("#")  && page.getDataSet().endsWith("#")) {
                    page.setDataSet(getValueByVar(page.getDataSet()));
                }
                if(StringUtils.isBlank(page.getId()) && StringUtils.isBlank(moduleCode)) {
                    List<com.hframework.web.config.bean.module.Component> pubComponentList = page.getComponentList();
                    for (com.hframework.web.config.bean.module.Component component : pubComponentList) {
                        if(StringUtils.isNotBlank(component.getDataSet()) && component.getDataSet().startsWith("#")  && component.getDataSet().endsWith("#")) {
                            component.setDataSet(getValueByVar(component.getDataSet()));
                        }
                        defaultComponentMap.put(component.getId(), component);
                    }
                    List<com.hframework.web.config.bean.module.Element> elementList = page.getElementList();
                    for (com.hframework.web.config.bean.module.Element element : elementList) {
                        if(StringUtils.isNotBlank(element.getValue()) && element.getValue().startsWith("#")  && element.getValue().endsWith("#")) {
                            element.setValue(getValueByVar(element.getValue()));
                        }
                        defaultElementMap.put(element.getId(),element);
                    }

                    continue;
                }

                com.hframework.web.config.bean.module.Component targetComponent = null;
                if(page.getDataSet() != null && page.getPageTemplate().equals("qlist")) {
                    String dataSet = page.getDataSet().substring(page.getDataSet().indexOf("/") + 1);
                    if(processsCache.containsKey(dataSet) ) {
                        List<com.hframework.web.config.bean.module.Component> componentList = page.getComponentList();
                        for (com.hframework.web.config.bean.module.Component component : componentList) {
                            if (component.getId().equals("qList") && component.getDataSet().equals(page.getDataSet())) {
                                targetComponent = component;
                            }
                        }

                        if (targetComponent == null) {
                            targetComponent = new com.hframework.web.config.bean.module.Component();
                            targetComponent.setId("qList");
                            targetComponent.setDataSet(page.getDataSet());
                            page.getComponentList().add(targetComponent);
                        }
                    }
                }else {
                    List<com.hframework.web.config.bean.module.Component> componentList = page.getComponentList();
                    for (com.hframework.web.config.bean.module.Component component : componentList) {
                        if (component.getId().equals("qList") && processsCache.containsKey(component.getDataSet().substring(component.getDataSet().indexOf("/") + 1)) ) {
                            targetComponent = component;
                        }
                    }
                }

                if(targetComponent != null) {
                    Object[] objects = processsCache.get(targetComponent.getDataSet().substring(targetComponent.getDataSet().indexOf("/") + 1));
                    if(targetComponent.getEventList() == null) targetComponent.setEventList(new ArrayList<Event>());
                    targetComponent.getEventList().addAll((List<Event>)objects[4]);
                }

                PageDescriptor pageDescriptor = parsePageDescriptor(page, moduleCode);
                pageSetting.get(moduleCode).put(page.getId(), pageDescriptor);
            }

        }
    }

    private String getValueByVar(String var) {
        var = var.substring(1, var.length() - 1);
        String value = var;
        if("program.name".equals(var)) {
            value = getProgram().getName();
        }else if("program.auth-instance.function".equals(var)) {
            value = getProgram().getAuthInstance().getFunction().split(",")[0].replaceAll("\\.", "/");
        }else if("program.auth-instance.user".equals(var)) {
            value = getProgram().getAuthInstance().getUser().replaceAll("\\.","/");
        }else if("program.login.data-set".equals(var)) {
            value = getProgram().getLogin().getDataSet().replaceAll("\\..", "/");
        }

        return value;
    }

    private PageDescriptor parsePageDescriptor(Page page, String moduleCode) throws Exception {
        PageDescriptor pageDescriptor = new PageDescriptor();
        pageDescriptor.setModule(moduleCode);
        pageDescriptor.setCode(page.getId());
        pageDescriptor.setName(page.getName());
        pageDescriptor.setPage(page);
        pageDescriptor.setPageTemplate(pageTemplates.get(page.getPageTemplate()));

        String[] subDataSets = StringUtils.split(page.getSubDataSets(), ",");
        List<String> subDataSetNames = new ArrayList<String>();
        if(subDataSets != null) {
            for (String subDataSet : subDataSets) {
                subDataSetNames.add(dataSets.get(subDataSet.trim()).getDataSet().getName().replaceAll("【[^【】]*】", ""));
            }
        } else if(StringUtils.isNotBlank(page.getDataSet())){
            subDataSetNames.add(dataSets.get(page.getDataSet()).getDataSet().getName().replaceAll("【[^【】]*】", ""));
        }
        pageDescriptor.setSubDataSetNames(Joiner.on(",").join(subDataSetNames));

        Stack<Pagetemplate> pageTemplateStack =  getPageTemplateStack(page.getPageTemplate(), new Stack<Pagetemplate>());
        for (Pagetemplate pageTemplate : pageTemplateStack) {
            List<Element> elementList = pageTemplate.getElementList();
            for (Element element : elementList) {
                if(EnumUtils.compareIfNullTrue(ElementType.component, element.getType())) {
                    ComponentDescriptor componentDescriptor = parseComponentDescriptor(element);
                    //如果是框架默认组件，那么在动态页面展示不用展示该内容
                    componentDescriptor.setIsDefaultComponent("default".equals(pageTemplate.getId()));
                    pageDescriptor.addComponentDescriptor(element.getId(), componentDescriptor);
                }else if(EnumUtils.compare(ElementType.string, element.getType())) {
                    pageDescriptor.addElementDescriptor(element.getId(), parseStringDescriptor(element));
                }else if(EnumUtils.compare(ElementType.container, element.getType())) {
                    pageDescriptor.addElementDescriptor(element.getId(), parseContainerDescriptor(element));
                }
            }
        }

        //获取组件级初始化信息。
        List<com.hframework.web.config.bean.module.Component> componentList = page.getComponentList();
        for (com.hframework.web.config.bean.module.Component component : componentList) {
            ComponentDescriptor componentDescriptor = pageDescriptor.getComponentDescriptor(component.getId());
            if(componentDescriptor == null) {//针对于dynamic动态模板进行处理
                Element element = new Element();
                element.setId(component.getId());
                element.setType(ElementType.component.getName());
                element.setEventExtend("false");
                if(StringUtils.isBlank(component.getDataSet())) {
                    component.setDataSet(page.getDataSet());
                }
                ComponentDescriptor componentDescriptor1 = parseComponentDescriptor(element);
                componentDescriptor1.setSetValueList(component.getSetValueList());
                pageDescriptor.addComponentDescriptor(component.getId() + "|" + component.getDataSet()+ "|" + component.getDataid(), componentDescriptor1);
            }
        }



        //获取页面级初始化信息。
        if(StringUtils.isBlank(page.getDataSet())) {
            logger.warn("no mapper exists for page level !");
        }else {
            for (ComponentDescriptor componentDescriptor : pageDescriptor.getComponents().values()) {

                Mapper mapper = null;
                if(mappers.get(page.getDataSet() + "_" + componentDescriptor.getId()) != null) {
                    mapper = mappers.get(page.getDataSet() + "_" + componentDescriptor.getId());
                }

                if(mappers.get(componentDescriptor.getId()) != null) {
                    mapper = mappers.get(componentDescriptor.getId());
                }

                if(mapper == null) {
                    logger.warn("no mapper {} exists !", page.getDataSet() + "_" + componentDescriptor.getId());
                    continue;
                }
                componentDescriptor.setMapper(mapper);
                if(dataSets.get(page.getDataSet()) == null) {
                    logger.warn("no dataset {} exists !", page.getDataSet() + "_" + componentDescriptor.getId());
                    continue;
                }
                componentDescriptor.setDataSetDescriptor(dataSets.get(page.getDataSet()));
                componentDescriptor.initComponentDataContainer(events);
            }
        }



        //将默认的组件配置添加到每一个page，如果page中没有该组件，需要兼容处理
        componentList.addAll(defaultComponentMap.values());
        for (com.hframework.web.config.bean.module.Component component : componentList) {
            if(StringUtils.isBlank(component.getDataSet())) {
                component.setDataSet(page.getDataSet());
            }
            Mapper mapper = null;
            if(mappers.get(component.getDataSet() + "_" + component.getId()) != null) {
                mapper = mappers.get(component.getDataSet() + "_" + component.getId());
            }

            if(mappers.get(component.getId()) != null) {
                mapper = mappers.get(component.getId());
            }

            if(mapper == null) {
                logger.warn("no mapper {} exists !", page.getDataSet() + "_" + component.getId());
                continue;
            }
            ComponentDescriptor componentDescriptor = pageDescriptor.getComponentDescriptor(component.getId());
            if("container".equals(component.getId())) {
                System.out.println(1);
            }
            if(componentDescriptor == null) {
                componentDescriptor = pageDescriptor.getComponentDescriptor(component.getId()  + "|" + component.getDataSet() + "|" + component.getDataid());
            }
            if(componentDescriptor != null) {
                componentDescriptor.setEventList(component.getEventList());
                componentDescriptor.setDataId(component.getDataid());
                componentDescriptor.setTitle(component.getTitle());
                componentDescriptor.setShowTitle("false".equals(component.getShowTitle()) ? false : true);
                componentDescriptor.setEventExtend(component.getEventExtend());
                componentDescriptor.setPath(component.getPath());
                componentDescriptor.setMapper(mapper);
                componentDescriptor.setSetValueList(component.getSetValueList());
                componentDescriptor.setDataSetDescriptor(dataSets.get(component.getDataSet()));
                if(dataSets.get(component.getDataSet()) == null) {
                    System.out.println("==>error : data set [" +  component.getDataSet() +"] is not exists !");
                }
                componentDescriptor.initComponentDataContainer(events);
            }
//            Map<String, ElementDescriptor> elements = pageDescriptor.getElements();
//            for (String key : elements.keySet()) {
//                ElementDescriptor elementDescriptor = elements.get(key);
//                if (elementDescriptor instanceof StringDescriptor) {
//                    StringDescriptor descriptor = (StringDescriptor) elementDescriptor;
//                    List<Mapping> mappingList = mapper.getBaseMapper().getMappingList();
//                    for (Mapping mapping : mappingList) {
//                        if(key.equals(mapping.getId())) {
//                            descriptor.setValue(mapping.getValue());
//                        }
//                    }
//                }
//            }

        }

        for (ComponentDescriptor componentDescriptor : pageDescriptor.getComponents().values()) {
            DataSetDescriptor dataSetDescriptor = componentDescriptor.getDataSetDescriptor();
            if(!dataSetComponentApplyCache.containsKey(dataSetDescriptor)) {
                dataSetComponentApplyCache.put(dataSetDescriptor, new ArrayList<ComponentDescriptor>());
            }
            dataSetComponentApplyCache.get(dataSetDescriptor).add(componentDescriptor);
        }

        return pageDescriptor;
    }

    public Mapper getMapper(String dataSet, String componentId) {
        if(mappers.get(dataSet + "_" + componentId) != null) {
            return mappers.get(dataSet + "_" + componentId);
        }
        return mappers.get(componentId);
    }

    public String getElementValue(String elementId) {
        return defaultElementMap.get(elementId) != null ? defaultElementMap.get(elementId).getValue() : null;
    }

    private ElementDescriptor parseContainerDescriptor(Element element) {
        ContainerDescriptor descriptor = new ContainerDescriptor(element);
        return descriptor;
    }

    private ElementDescriptor parseStringDescriptor(Element element) {
        StringDescriptor descriptor = new StringDescriptor(element);

        return descriptor;
    }

    private ComponentDescriptor parseComponentDescriptor(Element element) throws Exception {
        ComponentDescriptor componentDescriptor = new ComponentDescriptor(element);
        Component component = components.get(element.getId());
        if(component == null) {
            throw  new Exception("没有找到对应的组件" + element.getId());
        }
        componentDescriptor.setComponent(component);

        return componentDescriptor;
    }

    private Stack<Pagetemplate> getPageTemplateStack(String pageTemplateId, Stack<Pagetemplate> pageTemplateStack) {
        Pagetemplate pageTemplate = pageTemplates.get(pageTemplateId);
        pageTemplateStack.add(0,pageTemplate);
//        pageTemplateStack.add(pageTemplate);
        if(StringUtils.isNotBlank(pageTemplate.getParentId())) {
            getPageTemplateStack(pageTemplate.getParentId(), pageTemplateStack);
        }

        return pageTemplateStack;
    }


    private void loadComponentConfig() throws IOException {
        //加载项目信息
        try{
            program = XmlUtils.readValueFromFile(contextHelper.programConfigRootDir, contextHelper.programConfigProgramFile, Program.class);
        }catch (Exception e) {
            program = XmlUtils.readValueFromFile(contextHelper.programConfigRootDir,contextHelper.programConfigProgramFile, Program.class);
        }
        //加载模块信息
        List<Module> moduleList = XmlUtils.readValuesFromDirectory(contextHelper.programConfigRootDir, contextHelper.programConfigModuleDir, Module.class, ".xml");
        for (Module module : moduleList) {
            List<Page> pageList = module.getPageList();
            if(pageList != null) {
                for (Page page : pageList) {
                    page.setModule(module);
                }
            }

            if(this.modules.containsKey(module.getCode())) {
                this.modules.get(module.getCode()).getPageList().addAll(module.getPageList());
            }else {
                this.modules.put(module.getCode(),module);
            }

        }

        List<com.hframework.web.config.bean.program.Module> moduleList1 = program.getModules().getModuleList();
        for (com.hframework.web.config.bean.program.Module module : moduleList1) {
            if(!this.modules.containsKey(module.getCode())) {
                Module module1 = new Module();
                module1.setCode(module.getCode());
                module1.setPageList(new ArrayList<Page>());
                this.modules.put(module.getCode(),module1);
            }
        }


        //加载数据映射信息
        List<Mapper> mapperList = XmlUtils.readValuesFromDirectory(contextHelper.programConfigRootDir, contextHelper.programConfigMapperDir, Mapper.class, ".mapper");
        for (Mapper mapper : mapperList) {
            mappers.put(mapper.getDataSet() + "_" + mapper.getComponentId(), mapper);
        }

        //加载页面模板信息
        String pageDescriptorFiles = contextHelper.templateResourcePageDescriptorFile;
        if(pageDescriptorFiles.contains(";")) {
            String[] fileArrays = pageDescriptorFiles.split(";");
            try{
                PageTemplates pageTemplates = XmlUtils.readValueFromFile(contextHelper.programTemplateUnpackDir,
                        fileArrays[0], PageTemplates.class);
                for (Pagetemplate pagetemplate : pageTemplates.getPagetemplateList()) {
                    this.pageTemplates.put(pagetemplate.getId(),pagetemplate);
                }
            }catch (Exception e) {
                PageTemplates pageTemplates = XmlUtils.readValueFromFile(contextHelper.programTemplateUnpackDir,
                        fileArrays[1], PageTemplates.class);
                for (Pagetemplate pagetemplate : pageTemplates.getPagetemplateList()) {
                    this.pageTemplates.put(pagetemplate.getId(),pagetemplate);
                }
            }
        }else {
            PageTemplates pageTemplates = XmlUtils.readValueFromFile(contextHelper.programTemplateUnpackDir,
                    contextHelper.templateResourcePageDescriptorFile, PageTemplates.class);
            for (Pagetemplate pagetemplate : pageTemplates.getPagetemplateList()) {
                this.pageTemplates.put(pagetemplate.getId(),pagetemplate);
            }
        }


        //加载事件信息
        List<EventStore> eventStores = XmlUtils.readValuesFromDirectory(contextHelper.programTemplateUnpackDir,
                contextHelper.templateResourceEventStoreDir, EventStore.class, ".xml");
        for (EventStore eventStore : eventStores) {
            String group = eventStore.getGroup();
            List<Event> eventList = eventStore.getEventList();
            for (Event event : eventList) {
                events.put("#" + group + "." + event.getName(),event);
            }
        }

        //加载组件模板信息
        List<Component> componentList = XmlUtils.readValuesFromDirectory(contextHelper.programTemplateUnpackDir,
                contextHelper.templateResourceComponentDir, Component.class, ".xml");
        for (Component component : componentList) {
            if(StringUtils.isBlank(component.getId())) {
                continue;
            }
            components.put(component.getId(),component);
        }

        //加载默认数据映射信息
        List<Mapper> defaultMappers = XmlUtils.readValuesFromDirectory(contextHelper.programTemplateUnpackDir,
                contextHelper.templateResourceComponentMapperDir, Mapper.class, ".mapper");
        for (Mapper mapper : defaultMappers) {
            mappers.put(mapper.getComponentId(), mapper);
        }
    }


    public static WebContext get(){
        return context;
    }

    public static WebContext get(String companyCode, String programCode, String templateCode){
        WebContext newContext = new WebContext(companyCode, programCode, templateCode);
        return newContext;
    }

    public synchronized static WebContext reload(){
        WebContext newContext = new WebContext();
        context = newContext;
        return context;
    }



    public PageDescriptor getPageInfo(String module, String pageCode) {
//        try {
//            context.init();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        PageDescriptor pageDescriptor = pageSetting.get(module).get(pageCode);
        return pageDescriptor;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public Map<Class, DataSetDescriptor> getDataSetCache() {
        return dataSetCache;
    }

    public DataSetDescriptor getOnlyDataSetDescriptor(String dataSetName) {
        for (String dataSetNameInfo : dataSets.keySet()) {
            if(dataSetNameInfo.endsWith("/" + dataSetName)){
                return dataSets.get(dataSetNameInfo);
            }
        }
        return null;
    }

    public DataSetDescriptor getDataSet(Class clazz) {
        return dataSetCache.get(clazz);
    }

    public void setDataSetCache(Map<Class, DataSetDescriptor> dataSetCache) {
        this.dataSetCache = dataSetCache;
    }

    public Map<String, com.hframework.web.config.bean.module.Component> getDefaultComponentMap() {
        return defaultComponentMap;
    }

    public void setDefaultComponentMap(Map<String, com.hframework.web.config.bean.module.Component> defaultComponentMap) {
        this.defaultComponentMap = defaultComponentMap;
    }

    public static <T> void add(T data) {
        if(data == null) {
            return;
        }
        Class<?> aClass = data.getClass();
        String simpleName = aClass.getName();
        Context.put(simpleName, data);
        Context.put(aClass, data);
    }
    public static Set<Class> getAllClassContext() {
        return Context.getAllClassContext();
    }

    public static void clear() {
        Context.clear();
    }

    public static <T> void put(String key, T data) {
        Context.put(key, data);
    }

    public static <T> void putSession(String key, T data) {
        Context.put("SESSION:" + key, data);
    }

    public static <T> T getSession(String key) {
        return Context.get("SESSION:" + key);
    }

    public static <T> T get(String key) {
        return Context.get(key);
    }

    public static <T> T get(Class key) {
        return Context.get(key);
    }

    public static HashMap getDefault() {
        return Context.getDefault();
    }

    public static Map<String, String> putContext(String key, String value) {
        Map<String, String> map = get(HashMap.class.getName());
        if(map == null) {
            put(HashMap.class.getName(), new HashMap<String, String>());
        }
        map = get(HashMap.class.getName());
        map.put(key, value);
        return map;
    }

    public static Map<String, String> putContext(Map<String, String> objectMap) {
        Map<String, String> map = get(HashMap.class.getName());
        if(map == null) {
            put(HashMap.class.getName(), new HashMap<String, String>());
        }
        map = get(HashMap.class.getName());
        map.putAll(objectMap);
        return map;
    }

    public static <T> boolean fillProperty(String key, T t, String propertyName, String relPropertyName) {
        Object cacheObject = get(key);
        if(cacheObject != null) {
            Object propertyValue;
            if(cacheObject instanceof Map) {
                propertyValue  = ((Map)cacheObject).get(relPropertyName);
            }else {
                propertyValue = ReflectUtils.getFieldValue(cacheObject, relPropertyName);
            }
            if(propertyValue != null) {
                ReflectUtils.setFieldValue(t, propertyName, propertyValue);
                return true;
            }

        }

        return false;
    }

    public void overrideContext(List<File> diffFile, Class config) throws Exception {
        if(config == DataSet.class) {
            for (File file : diffFile) {
                DataSet dataSet = XmlUtils.readValueFromAbsoluteFilePath(file.getAbsolutePath(), DataSet.class);

                DataSetDescriptor dataSetDescriptor = dataSets.get(dataSet.getModule() + "/" + dataSet.getCode());
                if(dataSetDescriptor == null) {
                    dataSets.put(dataSet.getModule() + "/" + dataSet.getCode(), new DataSetDescriptor((dataSet)));
                    dataSetDescriptor =  dataSets.get(dataSet.getModule() + "/" + dataSet.getCode());
                    com.hframework.beans.class0.Class defPoClass = CreatorUtil.getDefPoClass(program.getCompany(),
                            program.getCode(), dataSet.getModule(), dataSet.getCode());
                    if(dataSet.getCode().equals(dataSet.getEventObjectCode())) {
                        try{
                            Class<?> aClass = Class.forName(defPoClass.getClassPath());
                            dataSetCache.put(aClass, dataSetDescriptor);
                        }catch (Exception e) {}
                    }
                }else {
                    BeanUtils.copyProperties(dataSetDescriptor.getDataSet(), dataSet);
                }

                List<Field> fieldList = dataSetDescriptor.getDataSet().getFields().getFieldList();
                for (Field field : fieldList) {
                    if(field.getRel() != null && field.getRel().getEntityCode() != null) {
                        String entityCode = field.getRel().getEntityCode();
                        String dataSetCode = entityCode.substring(0, entityCode.indexOf("/"));
                        String relFieldCode = entityCode.substring(entityCode.indexOf("/") + 1, entityCode.lastIndexOf("/"));
                        DataSetDescriptor relDataSet = this.getOnlyDataSetDescriptor(dataSetCode);
                        com.hframework.beans.class0.Class relPoClass =
                                CreatorUtil.getDefPoClass(program.getCompany(),
                                        program.getCode(), relDataSet.getDataSet().getModule(), dataSetCode);
                        DataSetDescriptor relDataSetDescriptor = dataSetCache.get(Class.forName(relPoClass.getClassPath()));
                        dataSetDescriptor.addRelDataSet(field.getCode(), dataSetCode + "/" + relFieldCode, relDataSetDescriptor);
                    }
                }

                List<ComponentDescriptor> componentDescriptors = dataSetComponentApplyCache.get(dataSetDescriptor);
                for (ComponentDescriptor componentDescriptor : componentDescriptors) {
                    //重新初始化组件内容
                    componentDescriptor.initComponentDataContainer(events);
                }

            }

        }
    }

    public static class Context{
        private static ThreadLocal<Map<String, Item>> itemsTL = new ThreadLocal<Map<String, Item>>();
        private static ThreadLocal<Map<Class, Item>> classTL = new ThreadLocal<Map<Class, Item>>();
        public static <T> void put(String key, T data) {
            if(itemsTL.get() == null) {
                itemsTL.set(new HashMap<String, Item>());
            }
            itemsTL.get().put(key, new Item(data));
        }

        public static <T> void put(Class key, T data) {
            if(classTL.get() == null) {
                classTL.set(new HashMap<Class, Item>());
            }
            classTL.get().put(key, new Item(data));
        }

        public static void clear() {
            itemsTL.remove();
            classTL.remove();
        }

        public static Set<Class> getAllClassContext(){
            if(classTL.get() == null) {
                return null;
            }
            return classTL.get().keySet();
        }


        public static <T> T get(Class key) {
            if(classTL.get() != null && classTL.get().containsKey(key)) {
                return (T) classTL.get().get(key).getT();
            }
            return null;
        }
        public static <T> T get(String key) {
            if(itemsTL.get() != null && itemsTL.get().containsKey(key)) {
                return (T) itemsTL.get().get(key).getT();
            }
            return null;
        }

        public static HashMap getDefault() {
            return get(HashMap.class.getName());
        }
    }



    public static class Item<T>{

        private T t ;

        public Item(T t) {
            this.t = t;
        }

        public T getT() {
            return t;
        }

        public void setT(T t) {
            this.t = t;
        }
    }


    public Map<Module,List<List<Entity>>> getEntityRelats() {
        Map<String, Module> modules = this.modules;
        Map<Module,List<List<Entity>>> result = new HashMap<Module, List<List<Entity>>>();
        for (String moduleCode : modules.keySet()) {
            if(StringUtils.isBlank(moduleCode)) {
                continue;
            }
            Module module = modules.get(moduleCode);
            result.put(module, new ArrayList<List<Entity>>());
            List<List<Entity>> moduleEntityList = result.get(module);
            List<Page> pageList = module.getPageList();
            for (Page page : pageList) {
                if("true".equals(page.getModule().getIsExtend())) {
                    continue;
                }
                Set<Entity> allEntitys = new LinkedHashSet<Entity>();
                DataSetDescriptor dataSetDescriptor = null;
                if(StringUtils.isNotBlank(page.getDataSet())) {
                    dataSetDescriptor = this.dataSets.get(page.getDataSet());
                    allEntitys.addAll(dataSetDescriptor.getDataSet().getEntityList());
                }

                List<com.hframework.web.config.bean.module.Component> componentList = page.getComponentList();
                for (com.hframework.web.config.bean.module.Component component : componentList) {
                    if(this.defaultComponentMap.values().contains(component)) {
                        continue;
                    }
                    String dataSet = component.getDataSet();
                    if(StringUtils.isBlank(dataSet)) {
                        continue;
                    }

                    dataSetDescriptor = this.dataSets.get(dataSet);
                    allEntitys.addAll(dataSetDescriptor.getDataSet().getEntityList());
                }

                Entity rootEntity = null;
                for (final Entity entity : allEntitys) {
                    if(rootEntity == null) rootEntity = entity;
                    List<Entity> relEntityList = getListFromModuleEntityList(moduleEntityList, rootEntity);
                    if(relEntityList == null) {
                        moduleEntityList.add(new LinkedList<Entity>(){{add(entity);}});
                    }else {
                        boolean flag = false;
                        for (Entity entity1 : relEntityList) {
                            if(entity1.getText().equals(entity.getText())) {
                                flag = true;
                                break;
                            }
                        }
                        if(!flag) {
                            relEntityList.add(entity);
                        }
                    }
                }
            }
        }

        return result;
    }

    private List<Entity> getListFromModuleEntityList(List<List<Entity>> moduleEntityList, Entity entity) {

        for (List<Entity> entities : moduleEntityList) {
            if(entities.contains(entity)) {
                return entities;
            }
        }

        return null;
    }

    public WebContextHelper getContextHelper() {
        return contextHelper;
    }

    public void setContextHelper(WebContextHelper contextHelper) {
        this.contextHelper = contextHelper;
    }

    public Map<String, Event> getEvents() {
        return events;
    }

    public void setEvents(Map<String, Event> events) {
        this.events = events;
    }

    public  Object[] getProcess(String dataSet) {
        return processsCache.get(dataSet);
    }

    public Mapper getMapper(String mapper) {
        return mappers.get(mapper);
    }
}
