package com.hframework.beans.class0;

import java.util.ArrayList;
import java.util.List;

public class Constructor {

	private List<Field> parameterList =new ArrayList<Field>();

	private List<String> codelnList = new ArrayList<String>();

	private String parameterStr = null;

	private boolean flag = false;

	public Constructor() {
		super();
	}

	public Constructor(List<Field> parameterList) {
		super();
		this.parameterList = parameterList;
		for (Field field : parameterList) {
			codelnList.add("this." + field.getName() + " = " + field.getName() + ";");
		}
		getParameterStr();
	}

	public void addParameters(List<Field> fields) {
		parameterList.addAll(fields);
		parameterList.clear();
		for (Field field : parameterList) {
			codelnList.add("this." + field.getName() + " = " + field.getName() + ";");
		}
		getParameterStr();
	}

	public void addCodeLn(String codeLn) {
		if(!flag) {
			codelnList.clear();
			flag = true;
		}
		codelnList.add(codeLn);
	}

	public void addParameter(Field field) {
		parameterList.add(field);
		getParameterStr();
	}

	public List<Field> getParameterList() {
		return parameterList;
	}

	public void setParameterList(List<Field> parameterList) {
		this.parameterList = parameterList;
	}

	public String getParameterStr() {
		parameterStr = null;
		if(parameterList != null) {
			for (Field field : parameterList) {
				if(parameterStr == null) {
					parameterStr = field.getType() + " " + field.getName();
				}else {
					parameterStr += (", " + field.getType() + " " + field.getName());
				}

			}
		}
		return parameterStr;
	}

	public List<String> getCodelnList() {
		return codelnList;
	}

	public void setCodelnList(List<String> codelnList) {
		this.codelnList = codelnList;
	}


}
