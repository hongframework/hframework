package com.hframework.beans.class0;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * class的数据对象
 * @author zhangqh6
 *
 */
public class ClassDescData {

	//引用类列表
	List<String> importClassList = new ArrayList<String>();

	//成员变量列表-单个类型成员变量
	List<List<String>> sigleVarList = new ArrayList<List<String>>();

	//成员变量列表-集合类型成员变量
	List<List<String>> multiVarList = new ArrayList<List<String>>();

	//构造方法类表
	List<List<List<String>>> constrauctorListList = new ArrayList<List<List<String>>>();


	/**
	 * 添加引入类
	 * @param importClass
	 */
	public void addImportClass(String importClass){
		importClassList.add(importClass);
	}

	/**
	 * 添加成员变量-单个
	 * @param varInfo
	 */
	public void addSigleVar(String[] varInfo){
		sigleVarList.add(Arrays.asList(varInfo));
	}

	/**
	 * 添加成员变量-单个
	 * @param varInfo
	 */
	public void addSigleVar(List<String> varInfo){
		sigleVarList.add(varInfo);
	}

	/**
	 * 添加成员变量-列表
	 * @param varInfo
	 */
	public void addMultiVar(String[] varInfo){
		multiVarList.add(Arrays.asList(varInfo));
	}

	/**
	 * 添加成员变量-列表
	 * @param varInfo
	 */
	public void addMultiVar(List<String> varInfo){
		multiVarList.add(varInfo);
	}

	/**
	 * 获得一个构造器
	 * @return
	 */
	public Constrauctor getConstrauctor(){
		Constrauctor constrauctor = new Constrauctor();
		constrauctorListList.add(constrauctor.getConstrauctorList());
		return constrauctor;
	}

	class  Constrauctor{
		List<List<String>> constrauctorList = new ArrayList<List<String>>();

		public List<List<String>> getConstrauctorList() {
			return constrauctorList;
		}

		public void addInVar(String[] varInfo){
			constrauctorList.add(Arrays.asList(varInfo));
		}

		public void addInVar(List varInfo){
			constrauctorList.add(varInfo);
		}

		public void addCodeLine(String[] code){
			constrauctorList.add(Arrays.asList(code));
		}

		public void addCodeLine(List code){
			constrauctorList.add(code);
		}

	}
}
