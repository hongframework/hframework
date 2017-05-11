package com.hframework.beans.class0;

public class Field {

	private String type;
	private String name;
	private String ucName;

	private boolean setGetMethod = true;

	private String fieldAnno;
	private String getMethodAnno;
	private String fieldComment;

	public Field(String type) {
		super();
		this.type = type;
		this.name = type.substring(0,1).toLowerCase() + type.substring(1);
		this.ucName = type;
	}

	public Field(String type, String name) {
		super();
		this.type = type;
		this.name = name;
		this.ucName = name.substring(0,1).toUpperCase() + name.substring(1);
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getUcName() {
		return ucName;
	}

	public void setUcName(String ucName) {
		this.ucName = ucName;
	}

	public Field addFieldAnno(String fieldAnno) {
		if(this.fieldAnno == null) {
			this.fieldAnno = fieldAnno;
		}else {
			this.fieldAnno += "/n    " + fieldAnno;
		}

		return this;
	}

	public Field addGetMethodAnno(String getMethodAnno) {
		this.getMethodAnno = getMethodAnno;
		return this;
	}

	public String getFieldAnno() {
		return fieldAnno;
	}

//	public void setFieldAnno(String fieldAnno) {
//		this.fieldAnno = fieldAnno;
//	}

	public String getGetMethodAnno() {
		return getMethodAnno;
	}

//	public void setGetMethodAnno(String getMethodAnno) {
//		this.getMethodAnno = getMethodAnno;
//	}


	public boolean isSetGetMethod() {
		return setGetMethod;
	}

	public void setSetGetMethod(boolean setGetMethod) {
		this.setGetMethod = setGetMethod;
	}

	public String getFieldComment() {
		return fieldComment;
	}

	public void setFieldComment(String fieldComment) {
		this.fieldComment = fieldComment;
	}
}
