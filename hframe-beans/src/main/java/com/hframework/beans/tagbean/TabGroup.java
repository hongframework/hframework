package com.hframework.beans.tagbean;

import java.util.List;

public class TabGroup {

	private String groupId;
	private String groupName;
	
	
	private List<TabItem> tabItems;
	
	
	public TabGroup() {
		super();
	}
	public TabGroup(String groupId, String groupName) {
		super();
		this.groupId = groupId;
		this.groupName = groupName;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public List<TabItem> getTabItems() {
		return tabItems;
	}
	public void setTabItems(List<TabItem> tabItems) {
		this.tabItems = tabItems;
	}
	
	
	
	
}
