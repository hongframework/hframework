package com.hframework.beans.tagbean;

public class Option {

	private String value;
	private String text;
	private String desc;
	
	public Option() {
	}
	
	public Option(String value,String text){
		this.value=value;
		this.text=text;
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
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
}
