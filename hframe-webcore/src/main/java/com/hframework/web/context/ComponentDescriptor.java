package com.hframework.web.context;

import com.alibaba.fastjson.JSONObject;
import com.hframework.beans.controller.ResultData;
import com.hframework.web.config.bean.Component;
import com.hframework.web.config.bean.Mapper;
import com.hframework.web.config.bean.component.Event;
import com.hframework.web.config.bean.mapper.Mapping;
import com.hframework.web.config.bean.pagetemplates.Element;

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
    private boolean isDefaultComponent;
    private String path;
    private String eventExtend;
    private List<Event> eventList;//页面配置的信息

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
}