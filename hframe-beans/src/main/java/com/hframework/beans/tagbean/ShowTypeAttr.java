package com.hframework.beans.tagbean;

import java.util.*;

public class ShowTypeAttr   implements java.io.Serializable{

  private String id;
  private String view;
  private String type;
  private String condition;
  private String title;
  private Date createDate;
  private String src;

  public ShowTypeAttr(){
  }

  public ShowTypeAttr(String id, String view, String type, String condition, String title, Date createDate){
       this.id=id;
       this.view=view;
       this.type=type;
       this.condition=condition;
       this.title=title;
       this.createDate=createDate;
  }

  public String getId(){
    return id;
  }

  public String getView(){
    return view;
  }

  public String getType(){
    return type;
  }

  public String getCondition(){
    return condition;
  }

  public String getTitle(){
    return title;
  }

  public Date getCreateDate(){
    return createDate;
  }

  public void setId(String id){
    this.id=id;
  }

  public void setView(String view){
    this.view=view;
  }

  public void setType(String type){
    this.type=type;
  }

  public void setCondition(String condition){
    this.condition=condition;
  }

  public void setTitle(String title){
    this.title=title;
  }

  public void setCreateDate(Date createDate){
    this.createDate=createDate;
  }

public String getSrc() {
	return src;
}

public void setSrc(String src) {
	this.src = src;
}
  
  

}
