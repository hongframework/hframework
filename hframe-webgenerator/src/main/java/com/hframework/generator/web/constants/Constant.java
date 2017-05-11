package com.hframework.generator.web.constants;

import java.util.HashMap;
import java.util.Map;

public class Constant {


	public static Map<String,Boolean> columnTypeMap=new HashMap<String, Boolean>();

	static{
		columnTypeMap.put("int", false);
		columnTypeMap.put("char", true);
		columnTypeMap.put("varchar",true );
		columnTypeMap.put("date", false);
		columnTypeMap.put("datetime", false);
		columnTypeMap.put("timestamp", false);
		columnTypeMap.put("text", false);
	}

	public static Map<String, String> column2ObjectType=new HashMap<String, String>();

	static{
		column2ObjectType.put("int", "int");
		column2ObjectType.put("char", "String");
		column2ObjectType.put("varchar", "String");
		column2ObjectType.put("date", "Date");
		column2ObjectType.put("datetime", "Date");
		column2ObjectType.put("timestamp", "Date");
		column2ObjectType.put("text", "String");
	}

	public static Map<String, String> column2ObjectFullPath=new HashMap<String, String>();

	static{
		column2ObjectFullPath.put("int", "java.lang.Integer");
		column2ObjectFullPath.put("char", "java.lang.String");
		column2ObjectFullPath.put("varchar", "java.lang.String");
		column2ObjectFullPath.put("date", "java.util.Date");
		column2ObjectFullPath.put("datetime", "java.util.Date");
		column2ObjectFullPath.put("timestamp", "java.util.Date");
		column2ObjectFullPath.put("text", "ava.lang.String");
	}


}
