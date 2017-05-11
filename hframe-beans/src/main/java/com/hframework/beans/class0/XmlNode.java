package com.hframework.beans.class0;

import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Created by zhangquanhong on 2016/4/14.
 */
public class XmlNode {
    //XML节点名称
    private String nodeName;
    //XML节点识别码
    private String nodeCode;
    //是否是单节点属性
    private boolean isSingleton = true;
    //节点属性信息
    private Map<String, String> attrMap;
    //子XML节点信息
    private List<XmlNode> childrenXmlNode;
    //父XML节点信息
    private XmlNode parentXmlNode;
    //节点内的文本内容
    private String nodeText;

    private boolean generated = false;

    public XmlNode() {

    }

    public XmlNode(XmlNode parentXmlNode) {
        this.parentXmlNode = parentXmlNode;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeCode() {
        if(nodeCode == null) {
            nodeCode = "/" + nodeName;
        }
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    public void setIsSingleton(boolean isSingleton) {
        this.isSingleton = isSingleton;
    }

    public Map<String, String> getAttrMap() {
        return attrMap == null ? new HashMap<String, String>() : attrMap;
    }

    public void setAttrMap(Map<String, String> attrMap) {
        this.attrMap = attrMap;
    }

    public List<XmlNode> getChildrenXmlNode() {
        if(childrenXmlNode == null) {
            childrenXmlNode = new ArrayList<XmlNode>();
        }
        return childrenXmlNode;
    }

    public void setChildrenXmlNode(List<XmlNode> childrenXmlNode) {
        this.childrenXmlNode = childrenXmlNode;
    }

    public XmlNode getParentXmlNode() {
        return parentXmlNode;
    }

    public void setParentXmlNode(XmlNode parentXmlNode) {
        this.parentXmlNode = parentXmlNode;
    }

    public String getNodeText() {
        return nodeText;
    }

    public void setNodeText(String nodeText) {
        this.nodeText = nodeText;
    }

    public void addOrMergeChildNode(XmlNode childNode) {
        if(childrenXmlNode == null) {
            childrenXmlNode = new ArrayList<XmlNode>();
        }
        for (XmlNode xmlNode : childrenXmlNode) {
            if(xmlNode.getNodeName().equals(childNode.getNodeName())) {
                xmlNode.merge(childNode);
                return ;
            }
        }
        childrenXmlNode.add(childNode);
    }

    public void addNode(XmlNode childNode) {
        if(childrenXmlNode == null) {
            childrenXmlNode = new ArrayList<XmlNode>();
        }
        childrenXmlNode.add(childNode);
    }

    public void merge(XmlNode targetNode) {
        mergeInternal(targetNode, false);
    }

    public void mergeOutSide(XmlNode targetNode) {
        mergeInternal(targetNode, true);
    }


    private void mergeInternal(XmlNode targetNode, boolean isOutsideMerge) {

//        if(this.getNodeCode() == null || !this.getNodeCode().equals(targetNode.getNodeCode())) {
//            return ;
//        }

        if(StringUtils.isNotBlank(this.getNodeText())) {
            this.setNodeText(targetNode.getNodeText());
        }

        //属性合并
        Map<String, String> targetNodeAttrMap = targetNode.getAttrMap();
        for (Map.Entry<String, String> entry : targetNodeAttrMap.entrySet()) {
            this.addNodeAttr(entry.getKey(), entry.getValue());
        }

        //递归调用子节点合并
        List<XmlNode> tmpList = new ArrayList<XmlNode>();
        flag:for (XmlNode targetChildNode : targetNode.getChildrenXmlNode()) {
            for (XmlNode thisChildNode : this.getChildrenXmlNode()) {
                if(thisChildNode.getNodeName().equals(targetChildNode.getNodeName())) {
                    thisChildNode.mergeInternal(targetChildNode, isOutsideMerge);
                    continue flag;
                }
            }
            tmpList.add(targetChildNode);
        }

        if(tmpList.size() != 0) {
            getChildrenXmlNode().addAll(tmpList);
        }

        if(isOutsideMerge) {
            this.setIsSingleton(this.isSingleton() && targetNode.isSingleton());
        }else {
            this.setIsSingleton(false);
        }

        targetNode.setParentXmlNode(this.getParentXmlNode());
    }

    public void addNodeAttr(String key, String value) {
        if(attrMap == null) {
            attrMap = new LinkedHashMap<String, String>();
        }

        if(attrMap.get(key) == null) {
            attrMap.put(key, value);
        }
    }

    public void settingNodeCode() {
        if(parentXmlNode == null) {
            setNodeCode("/" + this.getNodeName());
        }else if("/".equals(parentXmlNode.getNodeCode())){
            setNodeCode(parentXmlNode.getNodeCode() + this.getNodeName());
        }else {
            setNodeCode(parentXmlNode.getNodeCode() + "/" + this.getNodeName());
        }
        if (childrenXmlNode != null) {
            for (XmlNode xmlNode : childrenXmlNode) {
                xmlNode.settingNodeCode();
            }
        }
    }

    public Map<String, List<XmlNode>> fetchSameNameNode(Map<String, List<XmlNode>> sameNameNodeMap) {

        if(!sameNameNodeMap.containsKey(this.getNodeName())) {
            sameNameNodeMap.put(this.getNodeName(), new ArrayList<XmlNode>());
            sameNameNodeMap.get(this.getNodeName()).add(this);
        }else {
            sameNameNodeMap.get(this.getNodeName()).add(this);
        }
        if (childrenXmlNode != null) {
            for (XmlNode xmlNode : childrenXmlNode) {
                xmlNode.fetchSameNameNode(sameNameNodeMap);
            }
        }
        return sameNameNodeMap;
    }

    public static class XmlNodeHelper{
        public static Map<String, List<XmlNode>> filterSingletonNode(Map<String, List<XmlNode>> sameNameNodeMap) {
            if(sameNameNodeMap != null) {
                Iterator<Map.Entry<String, List<XmlNode>>> iterator = sameNameNodeMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, List<XmlNode>> entry = iterator.next();
                    if(entry.getValue().size() == 1) {
                        iterator.remove();
                    }else {
                        int tmpCount = 0;
                        for (XmlNode xmlNode : entry.getValue()) {
                            if(xmlNode.getAttrMap().size() != 0 || xmlNode.getChildrenXmlNode().size() != 0) {
                                tmpCount ++;
                            }
                        }
                        if(tmpCount <= 1) {
                            iterator.remove();
                        }
                    }
                }
            }

            return sameNameNodeMap;
        }
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }
}