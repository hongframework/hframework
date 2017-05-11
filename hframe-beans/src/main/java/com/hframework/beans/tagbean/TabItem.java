package com.hframework.beans.tagbean;

public class TabItem {

	private String itemId;
	private String title;
	private String jsp;
	
	private int width;
	private int height;
	
	private String isIframe;
	private String style;
	
	private String tabGroupId;

	
	
	
	public TabItem(String itemId, String title, String jsp, int width, int height,
			String isIframe, String style, String tabGroupId) {
		super();
		this.itemId = itemId;
		this.title = title;
		this.jsp = jsp;
		this.width = width;
		this.height = height;
		this.isIframe = isIframe;
		this.style = style;
		this.tabGroupId = tabGroupId;
	}

	

	public String getItemId() {
		return itemId;
	}



	public void setItemId(String itemId) {
		this.itemId = itemId;
	}



	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getJsp() {
		return jsp;
	}

	public void setJsp(String jsp) {
		this.jsp = jsp;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getIsIframe() {
		return isIframe;
	}

	public void setIsIframe(String isIframe) {
		this.isIframe = isIframe;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getTabGroupId() {
		return tabGroupId;
	}

	public void setTabGroupId(String tabGroupId) {
		this.tabGroupId = tabGroupId;
	}
	
	
	
	
	
	
}
