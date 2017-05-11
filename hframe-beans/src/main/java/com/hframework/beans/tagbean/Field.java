package com.hframework.beans.tagbean;


/**
 * 这个主要针对于页面显示的时候的一个域
 * 比如页面日志列表  有 复选框   标题   以及发表时间 3个域
 * 但对于数据库而言  可能是好几个列  比如复选框中我们传入了好几个东西（id、name）
 * 也可能什么都没有，比如<a href ="">详情</a>
 * 但一般来说他们有对应关系
 * 
 * 当然他也是在数据库中以一条记录存在，不过它的处理层面要高些
 * 
 * @author zqh
 *
 */
public class Field {

	private String id;//由于他是数据库中的一条记录，所以也有id
	private String type; //checkbox radio href text virtual_pid,virtual_id,virtual_value(主要是tree用到，当然list也可以用),
	
	private String title;

	private String showExp;//显示表达式  --${field:name}+"("+${sql:511}+")/("+${sql:511}+")"   -->注意建议都用column，除服复合类型用field
	private String hiddenExp;//隐藏表达式  --${href:1751}+"${column:274}
	
	//如果是href的話，我們還要判斷href是直接跳轉，還是_blank窗口打開，還是彈出窗口，都需要設置
	
	/*
	 * url-------------> ${href:1751}+"${column:274}"
	 * href:1751-->  /user/userDetail.jsp
	 * column:274-->   userId
	 * 
	 * title------------>
	 * 
	 * ${field:name}+"("+${sql:511}+")/("+${sql:511}+")"
	 * 
	 * 最美的爱情（10/43)  标题（评论数/点击数）；
	 * 
	 * $sql:511 -->select count(*) from article where ${field:id}
	 * 
	 */



	public Field() {
		super();
	}




	public Field(String id, String type, String showExp, String hiddenExp) {
		super();
		this.id = id;
		this.type = type;
		this.showExp = showExp;
		this.hiddenExp = hiddenExp;
	}

	public Field(String id,String title, String type, String showExp, String hiddenExp) {
		super();
		this.id = id;
		this.title=title;
		this.type = type;
		this.showExp = showExp;
		this.hiddenExp = hiddenExp;
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}




	public String getShowExp() {
		return showExp;
	}




	public void setShowExp(String showExp) {
		this.showExp = showExp;
	}




	public String getHiddenExp() {
		return hiddenExp;
	}




	public void setHiddenExp(String hiddenExp) {
		this.hiddenExp = hiddenExp;
	}




	public String getTitle() {
		return title;
	}




	public void setTitle(String title) {
		this.title = title;
	}



	public Field clone(){
		Field field=new Field();
		field.setId(this.id);
		field.setTitle(this.title);
		field.setType(this.type);
		field.setShowExp(this.showExp);
		field.setHiddenExp(this.hiddenExp);
		
		return field;
	}
	
	
}
