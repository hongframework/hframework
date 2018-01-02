package com.hframework.web.context;

import com.hframework.web.config.bean.dataset.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangquanhong on 2016/9/11.
 */
public class DataSetGroup implements IDataSet{

    private transient Node node;

    private String name;

    public List<IDataSet> elementList;

    public Map<String, IDataSet> elementMap;

    public List<IDataSet> getElementList() {
        return elementList;
    }

//    public void addElement(IDataSet dataSet) {
//        if(elementList == null) elementList = new ArrayList<IDataSet>();
//        elementList.add(dataSet);
//    }

    public void setElementList(List<IDataSet> elementList) {
        this.elementList = elementList;
        if(elementList != null) {
            elementMap = new HashMap<String, IDataSet>();
            for (IDataSet iDataSet : elementList) {
                String path = iDataSet.getNode().getPath();
                elementMap.put(path, iDataSet);
            }
        }
    }

    public Node getNode() {
        return node;
    }

    public IDataSet cloneBean() {
        DataSetGroup dataSetGroup = new DataSetGroup();
        dataSetGroup.setNode(node);
        dataSetGroup.setName(name);
        List<IDataSet> newElementList = new ArrayList<IDataSet>();
        for (IDataSet iDataSet : elementList) {
            newElementList.add(iDataSet.cloneBean());
        }
        dataSetGroup.setElementList(newElementList);
        return dataSetGroup;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Map<String, IDataSet> getElementMap() {
        return elementMap;
    }

    public void setElementMap(Map<String, IDataSet> elementMap) {
        this.elementMap = elementMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}