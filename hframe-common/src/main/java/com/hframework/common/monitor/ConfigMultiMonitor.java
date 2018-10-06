package com.hframework.common.monitor;

import com.hframework.common.util.collect.bean.Fetcher;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by zhangquanhong on 2016/9/26.
 */
public abstract class ConfigMultiMonitor<T> extends AbstractMonitor<T> implements Monitor<T>{
    private static final Logger logger = LoggerFactory.getLogger(ConfigMultiMonitor.class);

    protected List<ConfigMapMonitor> subMonitors;

    private Map<Class, Set<Object[]>> networkRedirectMap = null; //Object[] = {Class, Fetcher}

    private Map<Class, Map<String, Node>> nodeNetwork = new HashMap<Class, Map<String, Node>>();

    public ConfigMultiMonitor(long refreshSeconds) {
        super(refreshSeconds);
    }

    public ConfigMultiMonitor(Class invokeClass, long refreshSeconds) {
        super(invokeClass, refreshSeconds);
    }


    public void addSubMonitor(ConfigMapMonitor configMapMonitor) {
        if(subMonitors == null) {
            subMonitors = new ArrayList<ConfigMapMonitor>();
        }
        subMonitors.add(configMapMonitor);
    }

    public void reload() {
        if(subMonitors != null) {
            buildNetworkRedirectMap();
            List<ConfigMapMonitor> changedMonitor = new ArrayList<ConfigMapMonitor>();
            for (ConfigMapMonitor subMonitor : subMonitors) {
                if(subMonitor.reload(false)){
                    changedMonitor.add(subMonitor);
                }
            }
            if(changedMonitor.size() > 0) {
                changed(changedMonitor);
            }
        }
    }

    protected void buildNetworkRedirectMap(){
        if(networkRedirectMap == null) {
            synchronized (this) {
                if(networkRedirectMap == null) {
                    networkRedirectMap = new HashMap<Class, Set<Object[]>>();
                    for (ConfigMapMonitor subMonitor : subMonitors) {
                        Type toClassType = ((ParameterizedType)subMonitor.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
                        Map<Class, Fetcher<T, String>> fetchers = subMonitor.getFetchers();
                        if(fetchers != null) {
                            for (Class fromClass : fetchers.keySet()) {
                                if(!networkRedirectMap.containsKey(fromClass)) {
                                    networkRedirectMap.put(fromClass, new HashSet<Object[]>());
                                }
                                networkRedirectMap.get(fromClass).add(new Object[]{(Class) toClassType, fetchers.get(fromClass)});
                            }
                        }
                    }
                }
            }
        }
    }


    public T getObject() {
        return null;
    }

    public void changed(List<ConfigMapMonitor> monitors){
        List<Node> nodeLists = new ArrayList<Node>();

        for (ConfigMapMonitor mapMonitor : monitors) {
            Map<String, Object> addObjectMap = mapMonitor.getAddObjectMap();
            if(addObjectMap != null && addObjectMap.size() > 0) {
                nodeLists.addAll(addObjectMap(mapMonitor, addObjectMap));
            }
            Map<String, Object> modObjectMap = mapMonitor.getModObjectMap();
            if(modObjectMap != null && modObjectMap.size() > 0) {
                nodeLists.addAll(modObjectMap(mapMonitor, modObjectMap));
            }
            Map<String, Object> delObjectMap = mapMonitor.getDelObjectMap();
            if(delObjectMap != null && delObjectMap.size() > 0) {
                nodeLists.addAll(delObjectMap(mapMonitor, delObjectMap));
            }
        }
        onEvent(nodeLists);
    }

    public Map<Class, Map<String, Node>> getNodeNetwork() {
        return nodeNetwork;
    }

    public abstract void onEvent(List<Node> nodeLists);


    public List<Node> addObjectMap(ConfigMapMonitor monitor, Map<String, Object> map){
        List<Node> nodeLists = new ArrayList<Node>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Class objectClass = value.getClass();
            Node node = getAndCreateNodeIfAbsent(objectClass, key, value);
            node.setOperateType(Node.OperateType.add);
            nodeLists.add(node);
            buildNodeRelation(node, value, monitor.getFetchers());
        }
        return nodeLists;
    }

    public List<Node> modObjectMap(ConfigMapMonitor monitor, Map<String, Object> map){
        List<Node> nodeLists = new ArrayList<Node>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Class objectClass = value.getClass();
            Node node = getAndCreateNodeIfAbsent(objectClass, key, value);
            node.setOperateType(Node.OperateType.modify);
            if(node.getInputs() != null && node.getInputs().size() > 0) {
                for (Object inputNode : node.getInputs()) {
                    ((Node)inputNode).removeOutput(node);
                }
                node.getInputs().clear();
            }
            nodeLists.add(node);
            buildNodeRelation(node, value, monitor.getFetchers());
        }
        return nodeLists;
    }

    public void buildNodeRelation(Node node, Object value, Map<Class, Fetcher<Object, String>> fetchers){
        if(fetchers != null) {
            for (Class mainClass : fetchers.keySet()) {//from
                String mainKeyVal = fetchers.get(mainClass).fetch(value);
                if("null".equals(mainKeyVal) || StringUtils.isBlank(mainKeyVal)) {
                    continue;
                }
                if(mainKeyVal.contains(",")) {
                    for (String mainKey : StringUtils.split(mainKeyVal, ",")) {
                        if(StringUtils.isNotBlank(mainKey)) {
                            Node mainNode = getAndCreateNodeIfAbsent(mainClass, mainKey, null);
                            node.addInput(mainNode);
                            mainNode.addOutput(node);
                        }
                    }
                }else {
                    Node mainNode = getAndCreateNodeIfAbsent(mainClass, mainKeyVal, null);
                    node.addInput(mainNode);
                    mainNode.addOutput(node);
                }
            }

            if(networkRedirectMap != null) {
                if(networkRedirectMap.containsKey(node.getObjectClass())) {
                    for (Object[] targets : networkRedirectMap.get(node.getObjectClass())) {//to
                        Class toClass = (Class) targets[0];
                        Fetcher fetcher = (Fetcher) targets[1];
                        if(nodeNetwork.containsKey(toClass)) {
                            Map<String, Node> targetMap = nodeNetwork.get(toClass);
                            for (Node targetNode : targetMap.values()) {
                                if(node.getKeyValue().equals(String.valueOf(fetcher.fetch(targetNode.getObject())))){
                                    targetNode.addInput(node);
                                    node.addOutput(targetNode);
                                }
                            }
                        }

                    }
                }
            }

        }
    }

    public List<Node> delObjectMap(ConfigMapMonitor monitor, Map<String, Object> map){
        List<Node> nodeLists = new ArrayList<Node>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Class objectClass = value.getClass();
            Node node = removeNode(objectClass, key);
            node.setOperateType(Node.OperateType.delete);
            if(node != null) {
                nodeLists.add(node);
            }
        }
        return nodeLists;
    }

    public Node removeNode(Class clazz , String keyVal) {
        if(!nodeNetwork.containsKey(clazz)) {
            return null;
        }
        Node node = nodeNetwork.get(clazz).get(keyVal);
        if(node == null) {
            return null;
        }

        if(node.getInputs() != null && node.getInputs().size() > 0) {
            for (Object inputNode : node.getInputs()) {
                ((Node)inputNode).removeOutput(node);
            }
        }

        if(node.getOutputs() != null && node.getOutputs().size() > 0) {
            for (Object outputNode : node.getOutputs()) {
                ((Node)outputNode).removeInput(node);
            }
        }
        nodeNetwork.get(clazz).remove(keyVal);
        return node;
    }

    public Node getAndCreateNodeIfAbsent(Class clazz , String keyVal, Object object) {
        if(!nodeNetwork.containsKey(clazz)) {
            nodeNetwork.put(clazz, new HashMap<String, Node>());
        }
        Node node = nodeNetwork.get(clazz).get(keyVal);
        if(node == null) {
            node = new Node(keyVal, object);
        }else if(object != null){
            node.setObject(object);
        }
        nodeNetwork.get(clazz).put(keyVal, node);
        return node;
    }





    /**
     * 获取该节点的祖先
     * @param clazz
     * @param keyValue
     */
    public Map<Class, Map<String, Node>> getAncestors(Class clazz, String keyValue){
        Node node = nodeNetwork.get(clazz).get(keyValue);
        return getAncestors(node);
    }

    /**
     * 获取该节点的子网络（祖先节点的集合）
     * @param node
     */
    public Map<Class, Map<String, Node>> getAncestors(Node node){
        Map<Class, Map<String, Node>> nodeNetwork = new HashMap<Class, Map<String, Node>>();
        addNodeAncestorToNetwork(nodeNetwork, node);
        return nodeNetwork;
    }

    /**
     * 添加该节点以及祖先节点至网络
     * @param nodeNetwork
     * @param node
     */
    public void addNodeAncestorToNetwork(Map<Class, Map<String, Node>> nodeNetwork, Node node){
        boolean addResultFlag = addNodeToNetwork(nodeNetwork, node);
        if(addResultFlag) {
            List<Node> inputs = node.getInputs();
            if(inputs != null && inputs.size() > 0) {
                for (Node input : inputs) {
                    addNodeDescendantToNetwork(nodeNetwork, input);
                }
            }
        }
    }

    /**
     * 获取该节点的后代
     * @param clazz
     * @param keyValue
     */
    public Map<Class, Map<String, Node>> getDescendants(Class clazz, String keyValue){
        Node node = nodeNetwork.get(clazz).get(keyValue);
        return getDescendants(node);
    }

    /**
     * 获取该节点的子网络（后代节点的集合）
     * @param node
     */
    public Map<Class, Map<String, Node>> getDescendants(Node node){
        Map<Class, Map<String, Node>> nodeNetwork = new HashMap<Class, Map<String, Node>>();
        addNodeDescendantToNetwork(nodeNetwork, node);
        return nodeNetwork;
    }

    /**
     * 添加该节点以及后代节点至网络
     * @param nodeNetwork
     * @param node
     */
    public void addNodeDescendantToNetwork(Map<Class, Map<String, Node>> nodeNetwork, Node node){
        boolean addResultFlag = addNodeToNetwork(nodeNetwork, node);
        if(addResultFlag) {
            List<Node> outputs = node.getOutputs();
            if(outputs != null && outputs.size() > 0) {
                for (Node output : outputs) {
                    addNodeDescendantToNetwork(nodeNetwork, output);
                }
            }
        }
    }

    /**
     * 添加当前节点至网络
     * @param nodeNetwork
     * @param node
     * @return
     */
    public boolean addNodeToNetwork(Map<Class, Map<String, Node>> nodeNetwork, Node node) {
        Class objectClass = node.getObjectClass();
        if(!nodeNetwork.containsKey(objectClass)) {
            nodeNetwork.put(objectClass, new HashMap<String, Node>());
        }
        if(nodeNetwork.get(objectClass).containsKey(node.getKeyValue())) {
            return false;
        }else {
            nodeNetwork.get(objectClass).put(node.getKeyValue(), node);
            return true;
        }
    }
}
