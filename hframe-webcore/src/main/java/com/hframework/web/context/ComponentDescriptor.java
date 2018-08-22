package com.hframework.web.context;

import com.alibaba.fastjson.JSONObject;
import com.hframework.beans.controller.ResultData;
import com.hframework.common.util.JavaUtil;
import com.hframework.common.util.StringUtils;
import com.hframework.web.config.bean.Component;
import com.hframework.web.config.bean.Mapper;
import com.hframework.web.config.bean.component.Event;
import com.hframework.web.config.bean.dataset.Field;
import com.hframework.web.config.bean.mapper.Mapping;
import com.hframework.web.config.bean.module.SetValue;
import com.hframework.web.config.bean.pagetemplates.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangquanhong on 2016/5/26.
 */
public class ComponentDescriptor extends ElementDescriptor{

    private PageDescriptor pageDescriptor;

    private Component component;

    private DataSetDescriptor dataSetDescriptor;

    private Mapper mapper;

    private ComponentDataContainer dataContainer;
    private String dataId;//页面配置的信息
    private String title;//页面配置的信息
    private boolean showTitle =true;//显示标题
    private boolean isDefaultComponent;
    private String path;
    private String eventExtend;
    private List<Event> eventList;//页面配置的信息

    private Map<String, Object> defaultValues;

    private List<SetValue> setValueList;

    public void initComponentDataContainer(Map<String, Event> eventStore) {
        dataContainer = new ComponentDataContainer(component,getElement(), eventList,eventStore, eventExtend);
        List<Mapping> mappingList = mapper.getBaseMapper().getMappingList();
        for (Mapping mapping : mappingList) {
            dataContainer.addMappingAndDataSetDescriptor(mapping, this, dataSetDescriptor, true);
        }

        List<Mapping> mappingList1 = mapper.getEventMapper().getMappingList();
        for (Mapping mapping : mappingList1) {
            dataContainer.addMappingAndDataSetDescriptor(mapping, this, dataSetDescriptor, false);
        }
        dataContainer.setElementOfRowMap();
//        dataSetDescriptor.setDataSetRulers();//该地方执行会有重复数据，需要再datasetrule加载完毕后直接设置连带rule
    }

    public JSONObject getJson(ResultData resultData){
        ComponentDataContainer dataInstance;
        if(resultData.getData() != null) {
            dataInstance = this.dataContainer.getDataInstance(resultData.getData());
            return dataInstance.getJson();
        }else {
//            return dataContainer.getJson();
        }
        return null;
    }

    public JSONObject getJson(){
        return this.dataContainer.getJson();
    }

    public ComponentDescriptor(Element element) {
        super(element);
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;

    }

    public DataSetDescriptor getDataSetDescriptor() {
        return dataSetDescriptor;
    }

    public String getHelperScript(){
        if(dataSetDescriptor != null && dataSetDescriptor.getDataSet() != null
                && dataSetDescriptor.getDataSet().getDescriptor() != null){
            return dataSetDescriptor.getDataSet().getDescriptor().getHelperScript();
        }
        return null;
    }

    public void setDataSetDescriptor(DataSetDescriptor dataSetDescriptor) {
        this.dataSetDescriptor = dataSetDescriptor;
    }

    public Mapper getMapper() {
        return mapper;
    }

    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    public PageDescriptor getPageDescriptor() {
        return pageDescriptor;
    }

    public void setPageDescriptor(PageDescriptor pageDescriptor) {
        this.pageDescriptor = pageDescriptor;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getDataId() {
        return dataId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

    public String getEventExtend() {
        return eventExtend;
    }

    public void setEventExtend(String eventExtend) {
        this.eventExtend = eventExtend;
    }

    public ComponentDataContainer getDataContainer() {
        return dataContainer;
    }

    public void setDataContainer(ComponentDataContainer dataContainer) {
        this.dataContainer = dataContainer;
    }

    public boolean isDefaultComponent() {
        return isDefaultComponent;
    }

    public void setIsDefaultComponent(boolean isDefaultComponent) {
        this.isDefaultComponent = isDefaultComponent;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    public boolean isShowTitle() {
        return showTitle;
    }

    public List<SetValue> getSetValueList() {
        return setValueList;
    }

    public void setSetValueList(List<SetValue> setValueList) {
        this.setValueList = setValueList;
    }

    public JSONObject getWebContextDefaultJson(HashMap context) {
        JSONObject result = new JSONObject(context);
        for (Map.Entry<String, Object> entry : getDefaultValues().entrySet()) {
            if(result.get(entry.getKey()) == null || "".equals(result.get(entry.getKey()))) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        if(setValueList != null && setValueList.size() > 0) {
            for (SetValue setValue : setValueList) {
                //TODO 增加SetValue.method的判断
                result.put(JavaUtil.getJavaVarName(setValue.getField()), setValue.getValue());
            }
        }
        return result;
    }

    public String getDefaultValueByCode(String columnName) {
        if(setValueList != null && setValueList.size() > 0){
            for (SetValue setValue : setValueList) {
                if(columnName != null && columnName.equals(JavaUtil.getJavaVarName(setValue.getField()))) {
                    return setValue.getValue();
                }
            }
        }
        if(getDefaultValues().containsKey(columnName)) {
            return String.valueOf(getDefaultValues().get(columnName));
        }
        return null;
    }

    public Map<String, Object> getDefaultValues() {
        if(defaultValues != null) {
            return defaultValues;
        }

        try {
            defaultValues = new HashMap<String, Object>();
            for (Field field : dataSetDescriptor.getDataSet().getFields().getFieldList()) {
                if(StringUtils.isNotBlank(field.getDefaultValue())) {
                    defaultValues.put(JavaUtil.getJavaVarName(field.getCode()), field.getDefaultValue().trim());
                }
            }
        }catch (Exception e) {
            defaultValues = new HashMap<String, Object>();
        }

        return defaultValues;
    }

}