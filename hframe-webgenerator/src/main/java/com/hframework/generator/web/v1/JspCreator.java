package com.hframework.generator.web.v1;

import java.util.List;

import com.hframework.common.util.file.FileUtils;
import com.hframework.beans.class0.Table;
import com.hframework.generator.util.CreatorUtil;


/**
 *
 * @author zqh
 *
 */
public class JspCreator {

//	public static final String projectBasePath= CreatorUtil.projectBasePath;
//	public static final String projectTomcatBasePath=CreatorUtil.projectTomcatBasePath;

//	public static void createPageFile(String username, String pageName,
//									  String content) {
//
//		if("".equals(username)){
//			username="zqh";
//		}
//
//		//生成sql将要保存的路径包
//		String filePath=projectBasePath+"WebRoot/"+pageName;
//		FileUtils.writeFile(filePath, content);
//
//		filePath=projectTomcatBasePath+pageName;
//		FileUtils.writeFile(filePath, content);
//	}

	public static String createJspFile(String string, String dbName,
									   List<Table> tableList) {

		return null;
	}

//	public static String createCombineJspFile(String string, String dbName,
//											  List<CoreSet> tableList, CoreTableColumnServ coreTableColumnServ) {
//
//		return null;
//	}
}
