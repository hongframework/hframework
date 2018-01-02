package com.hframework.web.context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hframework.common.util.collect.CollectionUtils;
import com.hframework.common.util.collect.bean.Mapper;
import com.hframework.common.util.JavaUtil;
import com.hframework.common.util.ReflectUtils;
import com.hframework.common.util.StringUtils;
import com.hframework.common.util.UrlHelper;
import com.hframework.common.util.message.Dom4jUtils;
import com.hframework.common.util.message.XmlUtils;
import com.hframework.web.CreatorUtil;
import com.hframework.web.config.bean.DataSet;
import com.hframework.web.config.bean.DataSetHelper;
import com.hframework.web.config.bean.DataSetRuler;
import com.hframework.web.config.bean.dataset.*;
import com.hframework.web.config.bean.datasethelper.Mapping;
import com.hframework.web.config.bean.datasetruler.Rule;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.NodeList;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by zhangquanhong on 2016/5/26.
 */
public class DataSetDescriptor {

    private DataSet dataSet;

    private Field keyField;

    private Field nameField;


    private String helperDataXml;
    private JSONObject helperTags;
    private boolean helperRuntime = false;

    private static final String[] orderdEditType = {"input","select","checkbox","hidden"};

    //<hfpm_program_id,hfpm_program/hfpm_program_id>
    private Map<String, String> relFieldKeyMap = new TreeMap<String, String>(new Comparator() {
        public int compare(Object o1, Object o2) {
            String field1EditType = null, field2EditType = null;
            for (Field field : dataSet.getFields().getFieldList()) {
                if(field.getCode().equals(o1)) {
                    field1EditType = field.getEditType();
                }

                if(field.getCode().equals(o2)){
                    field2EditType = field.getEditType();
                }
            }
            int distinct = Arrays.binarySearch(orderdEditType, field2EditType) - Arrays.binarySearch(orderdEditType, field1EditType);

            return distinct != 0 ? distinct : (o1.hashCode() - o2.hashCode());
        }
    });

    //<hfpm_program/hfpm_program_id,ProgramDescriptor.class>
    private Map<String, DataSetDescriptor> relDataSetMap = new HashMap<String, DataSetDescriptor>();


    private List<DataSetHelper> dataSetHelpers = new ArrayList<DataSetHelper>();

    private List<DataSetRuler> dataSetRulers = new ArrayList<DataSetRuler>();

    private JSONObject dataSetRulerJsonObject = new JSONObject();

    private IDataSet dateSetStruct;

    private Map<String, Field> fields = null;
    private Set<String> virtualContainerSubNodePath;

    public void addRelDataSet(String fieldName, String key, DataSetDescriptor descriptor) {
        relDataSetMap.put(key,descriptor);
        relFieldKeyMap.put(fieldName,key);
    }

    public DataSetDescriptor(DataSet dataSet) {
        this.dataSet = dataSet;
        if(dataSet.getFields() != null && dataSet.getFields().getFieldList() != null) {
            for (Field field : dataSet.getFields().getFieldList()) {
                if("true".equals(field.getIsKey())) {
                    keyField = field;
                }

                if("true".equals(field.getIsName())) {
                    nameField = field;
                }
            }
        }
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public Map<String, DataSetDescriptor> getRelDataSetMap() {
        return relDataSetMap;
    }

    public Map<String, String> getRelFieldKeyMap() {
        return relFieldKeyMap;
    }

    public Map<String, Object> getRelFieldValueMap(Object object) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (String fieldCode : relFieldKeyMap.keySet()) {
            String propertyName = JavaUtil.getJavaVarName(fieldCode);
            Object propertyValue = ReflectUtils.getFieldValue(object, propertyName);
            result.put(relFieldKeyMap.get(fieldCode), propertyValue);
        }


        return result;
    }

    public String getUrlFieldCode(String url) {
        for (String fieldName : this.fields.keySet()) {
            Field field = this.fields.get(fieldName);
            if(field.getRel() != null && StringUtils.isNotBlank(field.getRel().getUrl()) && field.getRel().getUrl().equals(url)) {
                return fieldName;
            }
        }
        return null;
    }

    public String getRelFieldCode(Class relPoClass) throws Exception {
        for (String fieldCode : relFieldKeyMap.keySet()) {
            DataSetDescriptor relDataSetDescriptor = relDataSetMap.get(relFieldKeyMap.get(fieldCode));
            com.hframework.beans.class0.Class poClass =
                    CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
                            WebContext.get().getProgram().getCode(), relDataSetDescriptor.getDataSet().getModule(),
                            relDataSetDescriptor.getDataSet().getEventObjectCode());
            if(Class.forName(poClass.getClassPath()) == relPoClass) {
                return fieldCode;
            }
        }

        return null;
    }

    public List<String> getRelFieldCodes(List<Class> relPoClass) throws Exception {
        List<String> result = new ArrayList<String>();
        for (String fieldCode : relFieldKeyMap.keySet()) {
            DataSetDescriptor relDataSetDescriptor = relDataSetMap.get(relFieldKeyMap.get(fieldCode));
            com.hframework.beans.class0.Class poClass =
                    CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
                            WebContext.get().getProgram().getCode(), relDataSetDescriptor.getDataSet().getModule(),
                            relDataSetDescriptor.getDataSet().getEventObjectCode());
            if(relPoClass.contains(Class.forName(poClass.getClassPath()))) {
                result.add(fieldCode);
            }
        }

        return result;
    }

    public boolean isSelfDepend() {
        for (String fieldName : relFieldKeyMap.keySet()) {
            String relDataSetInfo = relFieldKeyMap.get(fieldName);
            if(relDataSetMap.get(relDataSetInfo) != null && relDataSetMap.get(relDataSetInfo).equals(this)) {
                if(!"true".equals(getFields().get(fieldName).getRel().getAddByGlobal())) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getSelfDependPropertyName() {
        for (String fieldName : relFieldKeyMap.keySet()) {
            if(relDataSetMap.get(relFieldKeyMap.get(fieldName)).equals(this)) {
                return fieldName;
            }
        }
        return null;
    }

    public void addDataSetHelper(DataSetHelper dataSetHelper) {
        dataSetHelpers.add(dataSetHelper);
    }
    public void addDataSetRuler(DataSetRuler dataSetRuler) {
        dataSetRulers.add(dataSetRuler);
    }

    public List<DataSetHelper> getDataSetHelpers() {
        return dataSetHelpers;
    }

    public static Map<String, String> getConditionMap(String helpDatascore) {
        Map<String, String> urlParameters = UrlHelper.getUrlParameters("?" + helpDatascore, true);
        return urlParameters;

    }

    public JSONObject getDynamicHelper() {
        JSONObject result = new JSONObject();
        List<DataSetHelper> dataSetHelpers = this.dataSetHelpers;
        if(dataSetHelpers != null) {
            for (DataSetHelper dataSetHelper : dataSetHelpers) {
                String helpDatascore = dataSetHelper.getHelpDatascore();
                if(StringUtils.isNotBlank(helpDatascore)) {
                    Map<String, String> condition = getConditionMap(helpDatascore);
                    for (String propertyName : condition.keySet()) {
                        String propertyValue = condition.get(propertyName);
                        if(!propertyValue.startsWith("{") && !propertyValue.endsWith("}") ) {
                            continue ;
                        }
                        propertyValue = propertyValue.substring(1, propertyValue.length() - 1);
                        String referDataSetCode = propertyValue.substring(0, propertyValue.indexOf("/"));
                        String referDataFiledCode = propertyValue.substring(propertyValue.indexOf("/") + 1);
                        String referDataFiledPropertyName = JavaUtil.getJavaVarName(referDataFiledCode);

                        JSONObject object = new JSONObject();
                        object.put("sourceCode",referDataFiledPropertyName);
                        object.put("targetCode",propertyValue);
                        object.put("ruleType",3);
                        List<Mapping> mappingList = dataSetHelper.getMappings().getMappingList();
                        for (Mapping mapping : mappingList) {
                            if("true".equals(mapping.getIsCompareKey())) {
                                object.put("compareKey", JavaUtil.getJavaVarName(mapping.getEffectDatasetField()));
                            }
                            if("true".equals(mapping.getIsCompareName())) {
                                object.put("compareName", JavaUtil.getJavaVarName(mapping.getEffectDatasetField()));
                            }
                        }
                        result.put(referDataFiledPropertyName,object);
                    }
                }else {
                    JSONObject object = new JSONObject();
                    List<Mapping> mappingList = dataSetHelper.getMappings().getMappingList();
                    for (Mapping mapping : mappingList) {
                        if("true".equals(mapping.getIsCompareKey())) {
                            object.put("compareKey", JavaUtil.getJavaVarName(mapping.getEffectDatasetField()));
                        }
                        if("true".equals(mapping.getIsCompareName())) {
                            object.put("compareName", JavaUtil.getJavaVarName(mapping.getEffectDatasetField()));
                        }
                    }
                    result.put("NE",object);
                }
            }
        }
        return result;
    }



    public void setDataSetHelpers(List<DataSetHelper> dataSetHelpers) {
        this.dataSetHelpers = dataSetHelpers;
    }

    public List<DataSetRuler> getDataSetRulers() {
        return dataSetRulers;
    }

    public void setDataSetRulers() {
        for (DataSetRuler dataSetRuler : dataSetRulers) {
            List<Rule> ruleList = dataSetRuler.getRuleList();
            if(ruleList != null) {
                dataSetRulerJsonObject = new JSONObject();
                for (Rule rule : ruleList) {
                    String sourceCode = JavaUtil.getJavaVarName(rule.getSourceCode());
                    String sourceValue = rule.getSourceValue();
                    String targetCode = JavaUtil.getJavaVarName(rule.getTargetCode());
                    String targetValue = rule.getTargetValue();
                    String editable = rule.getEditable();
                    String key = sourceCode;
                    String ruleType = rule.getRuleType();
                    JSONObject object = new JSONObject();
                    object.put("sourceCode",sourceCode);
                    object.put("sourceValue",sourceValue);
                    object.put("targetCode",targetCode);
                    object.put("targetValue",targetValue);
                    object.put("editable",editable);
                    object.put("ruleType",ruleType);

                    if("1".equals(ruleType)) {//1值映射 2 值关联 //3范围映射
                        key = key + "=" + sourceValue;
                    }
                    if(!dataSetRulerJsonObject.containsKey(key)) {
                        dataSetRulerJsonObject.put(key, new JSONArray());
                    }
                    JSONArray jsonArray = dataSetRulerJsonObject.getJSONArray(key);
                    jsonArray.add(object);
                }
            }
        }

        //范围映射需要增加默认dataset默认的rel关联
        if(dataSet.getFields() != null && dataSet.getFields().getFieldList() != null) {
            List<Field> fields = dataSet.getFields().getFieldList();
            for (Field field : fields) {
                if(field.getRel() != null && StringUtils.isNotBlank(field.getRel().getRelField())) {
                    for (String relField : field.getRel().getRelField().split(",")) {
                        JSONObject object = new JSONObject();
                        object.put("sourceCode", JavaUtil.getJavaVarName(relField));
                        object.put("targetCode", JavaUtil.getJavaVarName(field.getCode()));
//                    object.put("editable",true);
                        object.put("ruleType",3);
                        if(StringUtils.isNotBlank(field.getRel().getRelScope())) {
                            object.put("scope", field.getRel().getRelScope());
                        }
                        String key = JavaUtil.getJavaVarName(relField);
                        if(!dataSetRulerJsonObject.containsKey(key)) {
                            dataSetRulerJsonObject.put(key, new JSONArray());
                        }
                        JSONArray jsonArray = dataSetRulerJsonObject.getJSONArray(key);
                        jsonArray.add(object);
                    }
                }
            }
        }
    }

    public JSONObject getDataSetRulerJsonObject() {
        return dataSetRulerJsonObject;
    }

    public String[] getRelPropertyNames() {
        for (Map.Entry<String, String> entry : relFieldKeyMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if(value.startsWith(dataSet.getEventObjectCode() + "/")){
                return new String[]{
                        JavaUtil.getJavaVarName(value.replace(dataSet.getEventObjectCode() + "/", "")),
                        JavaUtil.getJavaVarName(key)};
            }
        }
        return null;
    }

    public IDataSet getDateSetStruct() {
        return dateSetStruct;
    }



    public void setDateSetStruct(IDataSet dateSetStruct) {
        this.dateSetStruct = dateSetStruct;
    }

    public Field getKeyField() {
        return keyField;
    }

    public void setKeyField(Field keyField) {
        this.keyField = keyField;
    }

    public Field getNameField() {
        return nameField;
    }

    public void setNameField(Field nameField) {
        this.nameField = nameField;
    }

    public Map<String, Field> getFields() {
        if(fields == null) {
            synchronized (this) {
                if(fields == null) {
                    fields = CollectionUtils.convert(dataSet.getFields().getFieldList(), new Mapper<String, Field>() {
                        public <K> K getKey(Field field) {
                            return (K) field.getCode();
                        }
                    });
                }
            }
        }
        return fields;
    }

    public void setFields(Map<String, Field> fields) {
        this.fields = fields;
    }

    public String getHelperDataXml() {
        return helperDataXml;
    }

    public void setHelperDataXml(String helperDataXml) {
        this.helperDataXml = helperDataXml;
    }

    public JSONObject getHelperTags() {
        return helperTags;
    }

    public void setHelperTags(JSONObject helperTags) {
        this.helperTags = helperTags;
    }

    public void setVirtualContainerSubNodePath(Set<String> virtualContainerSubNodePath) {
        this.virtualContainerSubNodePath = virtualContainerSubNodePath;
    }

    public Set<String> getVirtualContainerSubNodePath() {
        return virtualContainerSubNodePath;
    }

    public boolean isHelperRuntime() {
        return helperRuntime;
    }

    public void setHelperRuntime(boolean helperRuntime) {
        this.helperRuntime = helperRuntime;
    }

    public void resetHelperInfo() throws DocumentException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if(dataSet == null || dataSet.getDescriptor() == null || dataSet.getDescriptor().getHelperDatas() == null){
            return;
        }
        HelperDatas helperDatas = dataSet.getDescriptor().getHelperDatas();
        if(helperDatas != null && helperDatas.getHelperDatas() != null) {
            JSONObject helpTags = new JSONObject(true);
            DOMElement root = null;
            for (HelperData helpData : helperDatas.getHelperDatas()) {
                String embedClass = helpData.getEmbedClass();
                String embedMethod = helpData.getEmbedMethod();
                if(StringUtils.isNotBlank(embedClass) && StringUtils.isNotBlank(embedMethod)){
                    String replaceString = String.valueOf(java.lang.Class.forName(embedClass).getMethod(embedMethod, new java.lang.Class[0]).invoke(null, null));
                    helpData.setHelpLabels(XmlUtils.readValue("<helper-data>" + replaceString + "</helper-data>", HelperData.class).getHelpLabels());
                }
                String targetId = helpData.getTargetId();
                String[] nodes = StringUtils.split(targetId, ".");
                if(root == null) {
                    Map blankMap = new HashMap();
                    root = Dom4jUtils.createElement(nodes[0], blankMap);
                }
                addElementToDomElement(root, Arrays.copyOfRange(nodes, 0, nodes.length - 1), helpData.getHelpLabels());
                String xml = root.asXML();
                this.helperDataXml = xml;

                JSONObject helpTag = new JSONObject(true);
                helpTags.put(dataSet.getCode() + "#" + targetId, helpTag);
                int count = 0;
                for (HelperLabel helperLabel : helpData.getHelpLabels()) {
                    JSONObject helpLabel = new JSONObject(true);
                    helpTag.put(helperLabel.getName(), helpLabel);
                    for (int i = 0; i < helperLabel.getHelpItems().size(); i++) {
                        helpLabel.put(helperLabel.getHelpItems().get(i).getName(), count ++);
                    }
                }
                this.helperTags = helpTags;
            }
        }
    }

    private void addElementToDomElement(DOMElement root, String[] nodes, List<HelperLabel> helpLabels) throws DocumentException {

        DOMElement parentElement = getCurrentElement(root, nodes);
        for (HelperLabel helpLabel : helpLabels) {
            List<HelperItem> helpItems = helpLabel.getHelpItems();
            for (HelperItem helpItem : helpItems) {
                parentElement.add(DocumentHelper.parseText(helpItem.getText()).getRootElement());
            }
        }
        ;
    }

    private DOMElement getCurrentElement(DOMElement root, String[] nodes) {
        org.w3c.dom.Node result = root;
        for (String node : nodes) {
            if(result.getNodeName().equals(node)) {
            }else {
                boolean createNew = true;
                NodeList childNodes = result.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    try{
                        org.w3c.dom.Node item = childNodes.item(i);
                        if(item.getNodeName().equals(node)){
                            result = item;
                            createNew = false;
                            break;
                        }
                    }catch (Exception e) {

                    }
                }
                if(createNew) {
                    DOMElement leaf = new DOMElement(node);
                    result.appendChild(leaf);
                    result = leaf;
                }
            }
        }
        return (DOMElement) result;
    }
}
