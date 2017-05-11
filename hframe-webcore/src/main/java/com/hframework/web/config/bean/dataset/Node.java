package com.hframework.web.config.bean.dataset;

import com.hframework.common.util.StringUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("node")
public class Node {

	@XStreamImplicit
    @XStreamAlias("node")
	private List<Node> nodeList;
	@XStreamAsAttribute
	@XStreamAlias("code")
	private String code;
	@XStreamAsAttribute
	@XStreamAlias("name")
	private String name;

	private String path;
    public Node() {
    	}
   
 
 	
	public List<Node> getNodeList(){
		return nodeList == null ? new ArrayList<Node>() : nodeList;
	}

	public void setNodeList(List<Node> nodeList){
    	this.nodeList = nodeList;
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void calcPath(){
		calcPath(null);
	}

	public void calcPath(String parentPath){
		if(StringUtils.isBlank(parentPath)) {
			path = getNodeCode();
		}else {
			path = parentPath + "." + getNodeCode();
		}
		if(nodeList != null && nodeList.size() > 0) {
			for (Node node : nodeList) {
				node.calcPath(path);
			}
		}
	}

	public String getNodeCode(){
		if(code.endsWith("[]")) {
			return code.substring(0, code.length()-2);
		}
		return code;
	}

	public String getPath() {
		return path;
	}

}
