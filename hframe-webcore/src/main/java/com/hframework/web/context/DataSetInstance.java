package com.hframework.web.context;

import com.alibaba.fastjson.JSONObject;
import com.hframework.web.config.bean.dataset.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangquanhong on 2016/9/11.
 */
public class DataSetInstance implements IDataSet{

    private Node node ;
    private List<Map<String, String>> list;
    private Map<String, String> one;

    private JSONObject componentData;

    public static DataSetInstance valueOf(Object data) {
        DataSetInstance instance = new DataSetInstance();
        if (data instanceof List) {
            instance.setList((List<Map<String, String>>) data);
        }else if (data instanceof Map) {
            instance.setOne((Map<String, String>) data);
        }else if (data instanceof Node) {
            instance.setNode((Node) data);
        }
        return instance;
    }

    public boolean isOne() {
        return !node.getCode().endsWith("[]");
    }

    public List<Map<String, String>> getList() {

        return list;
    }

    public void add(Map<String, String> one) {
        if(list == null) list = new ArrayList<Map<String, String>>();
        list.add(one);
    }

    public void setList(List<Map<String, String>> list) {
        this.list = list;
    }

    public Map<String, String> getOne() {
        return one;
    }

    public void setOne(Map<String, String> one) {
        this.one = one;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public JSONObject getComponentData() {
        return componentData;
    }

    public void setComponentData(JSONObject componentData) {
        this.componentData = componentData;
    }
}