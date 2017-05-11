package com.hframework.beans.tagbean;

public class MenuVO {
	
	private String id;
	private String pid;
	private String name;
	private String url;
	
	public MenuVO() {
	}
	
	public MenuVO(String id ,String pid, String name, String url) {
		this.id=id;
		this.pid=pid;
		this.name=name;
		this.url=url;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
	

}
