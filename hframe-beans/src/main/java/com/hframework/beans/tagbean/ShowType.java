package com.hframework.beans.tagbean;

import java.util.List;


public class ShowType {
	private int id;
	private String type;  //编辑方式 select text radio checkbox openwin   
	private List optionList;
	private int colSpan=1;//0：100%   1:1  2:2  3:3  4:4
	
	private String value;//checkbox option 值
	private String text;//checkbox option 显示文本
	private int width;
	private int height;
	//扩展信息
	private String preStr="";
	private String afterStr="";

	private String displayValue="";//tipinput 展示input框需要的值。
	
	private String elementId;
	
	private ShowTypeAttr showTypeAttr;


	private EnumDyn coreEnumDyn;
	
	public ShowType() {
	}
	
	public ShowType(String type) {
		this.type=type;
	}
	public ShowType(String type,int colSpan) {
		this.type=type;
		this.colSpan=colSpan;
	}
	
	public ShowType(String type,int colSpan,int width) {
		this.type=type;
		this.colSpan=colSpan;
		this.width=width;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List getOptionList() {
		return optionList;
	}
	public void setOptionList(List optionList) {
		this.optionList = optionList;
	}

	public int getColSpan() {
		return colSpan;
	}

	public void setColSpan(int colSpan) {
		this.colSpan = colSpan;
	}

	public String getPreStr() {
		return preStr;
	}

	public void setPreStr(String preStr) {
		this.preStr = preStr;
	}

	public String getAfterStr() {
		return afterStr;
	}

	public void setAfterStr(String afterStr) {
		this.afterStr = afterStr;
	}

	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public ShowTypeAttr getShowTypeAttr() {
		return showTypeAttr;
	}

	public void setShowTypeAttr(ShowTypeAttr showTypeAttr) {
		this.showTypeAttr = showTypeAttr;
	}

	public EnumDyn getCoreEnumDyn() {
		return coreEnumDyn;
	}

	public void setCoreEnumDyn(EnumDyn coreEnumDyn) {
		this.coreEnumDyn = coreEnumDyn;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	

}
