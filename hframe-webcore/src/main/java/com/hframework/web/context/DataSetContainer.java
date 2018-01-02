package com.hframework.web.context;

import com.hframework.web.config.bean.dataset.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangquanhong on 2016/9/11.
 */
public class DataSetContainer implements IDataSet{

    public transient  Node  node ;

    public List<DataSetContainer> subDataSetContainers;
    public List<DataSetInstance> datas;
    public List<IDataSet> elementList = new ArrayList<IDataSet>();

    public boolean virtualContainer;

    //数据组
    public List<DataSetGroup> dataGroups ;


    public boolean isMany(){
        return node.getCode().endsWith("[]");
    }

    public static DataSetContainer valueOf(Node node, List<DataSetInstance> datas) {
        return valueOf(node, datas, null);
    }

    public void addDataGroup(DataSetGroup group) {
        if(dataGroups == null) dataGroups = new ArrayList<DataSetGroup>();
        dataGroups.add(group);
    }

    public static DataSetContainer valueOf(Node node, List<DataSetInstance> datas, List<DataSetContainer> subDataSetContainers) {
        DataSetContainer container = new DataSetContainer();
        container.setNode(node);
        container.setDatas(datas);
        container.setSubDataSetContainers(subDataSetContainers);
        return container;
    }

//    public void addContainer(DataSetContainer subContainer) {
//        if(subDataSetContainers == null) subDataSetContainers = new ArrayList<DataSetContainer>();
//        subDataSetContainers.add(subContainer);
//    }

    public void addContainerAll(List<DataSetContainer> subContainers) {
        if(subDataSetContainers == null) subDataSetContainers = new ArrayList<DataSetContainer>();
        subDataSetContainers.addAll(subContainers);
//        elementList.addAll(subContainers);
    }

//    public void addData(DataSetInstance data) {
//        if(datas == null) datas = new ArrayList<DataSetInstance>();
//        datas.add(data);
//    }

    public void addDataAll(List<DataSetInstance> data) {
        if(datas == null) datas = new ArrayList<DataSetInstance>();
        datas.addAll(data);
//        elementList.addAll(data);
    }

    public List<DataSetContainer> getSubDataSetContainers() {
        return subDataSetContainers;
    }

    public void setSubDataSetContainers(List<DataSetContainer> subDataSetContainers) {
//        this.elementList.addAll(subDataSetContainers);
        this.subDataSetContainers = subDataSetContainers;
    }

    public List<DataSetInstance> getDatas() {
        return datas;
    }

    public void setDatas(List<DataSetInstance> datas) {
//        elementList.addAll(datas);
        this.datas = datas;
    }

    public Node getNode() {
        return node;
    }

    public IDataSet cloneBean() {
        DataSetContainer dataSetContainer = new DataSetContainer();
        dataSetContainer.setNode(node);
        List<IDataSet> newElementList = new ArrayList<IDataSet>();
        for (IDataSet iDataSet : this.elementList) {
            newElementList.add(iDataSet.cloneBean());
        }
        dataSetContainer.setElementList(newElementList);
        dataSetContainer.setVirtualContainer(virtualContainer);
        return dataSetContainer;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public List<IDataSet> getElementList() {
        return elementList;
    }

    public void setElementList(List<IDataSet> elementList) {
        this.elementList = elementList;
    }

    public List<DataSetGroup> getDataGroups() {
        return dataGroups;
    }

    public void setDataGroups(List<DataSetGroup> dataGroups) {
        this.dataGroups = dataGroups;
    }

    public boolean isVirtualContainer() {
        return virtualContainer;
    }

    public void setVirtualContainer(boolean virtualContainer) {
        this.virtualContainer = virtualContainer;
    }
}