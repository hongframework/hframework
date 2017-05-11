package com.hframework.web.context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hframework.common.util.collect.CollectionUtils;
import com.hframework.common.util.collect.bean.Mapper;
import com.hframework.common.util.JavaUtil;
import com.hframework.common.util.ReflectUtils;
import com.hframework.common.util.StringUtils;
import com.hframework.common.util.UrlHelper;
import com.hframework.web.CreatorUtil;
import com.hframework.web.config.bean.DataSet;
import com.hframework.web.config.bean.DataSetHelper;
import com.hframework.web.config.bean.DataSetRuler;
import com.hframework.web.config.bean.dataset.Field;
import com.hframework.web.config.bean.datasethelper.Mapping;
import com.hframework.web.config.bean.datasetruler.Rule;

import java.util.*;

/**
 * Created by zhangquanhong on 2016/5/26.
 */
public class DataSetDescriptor {

    private DataSet dataSet;

    private Field keyField;

    private Field nameField;

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
}
