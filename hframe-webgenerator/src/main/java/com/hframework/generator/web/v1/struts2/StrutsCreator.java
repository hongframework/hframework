package com.hframework.generator.web.v1.struts2;

/**
 *
 * @author zqh
 *
 */
@Deprecated
public class StrutsCreator {

//	public static final String projectBasePath= CreatorUtil.projectBasePath;
//	public static final String projectTomcatBasePath=CreatorUtil.projectTomcatBasePath;
//
//	public static String createStrutsFile(String username, String dbName, List<Table> tableList){
//
////			File directory=new File(projectBasePath+"/src/");
////			File[] files=FileOperation.getFileList(directory);
//
//		//如果没有拥有者，默认为zqh用户
//		if("".equals(username)){
//			username="zqh";
//		}
//
//		//生成sql将要保存的路径包
//		String xmlFilePath=projectBasePath+"src/strutsconfig/"+dbName.toLowerCase()+"/";
//		String xmlFileName="";
//
//
//		//每张表生成一个sql文件,通过getSqlContent(table)获取文件内容
//		List<String> xmlFileNameList=new ArrayList<String>();
//		for (Table table : tableList) {
//			xmlFileName=xmlFilePath+ table.getTableName().toLowerCase()+".xml";
//			String content=getStrutsXmlContent(table,username,dbName.toLowerCase()).trim();
//			FileUtils.writeFile(xmlFileName, content);
//			xmlFileNameList.add(table.getTableName().toLowerCase()+".xml");
//		}
//
//		xmlFileName=xmlFilePath+"_"+dbName.toLowerCase()+".xml";
//		String content=getStrutsRootXmlContent(xmlFileNameList);
//		FileUtils.writeFile(xmlFileName, content);
//
//		xmlFileName=projectBasePath+"src/struts.xml";
//
//		StringBuffer sb=new StringBuffer();
//		sb.append("<!--"+dbName.toLowerCase()+"-->\n");
//		for(String xmlFileName1:xmlFileNameList){
//			sb.append("    <include file=\"strutsconfig/"+dbName.toLowerCase()+"/"+xmlFileName1+"\"></include>\n");
//		}
//		sb.append("<!--"+dbName.toLowerCase()+"-->\n");
//
//		FileUtils.appendMethod(xmlFileName, sb.toString(), dbName.toLowerCase());
//
//		return null;
//	}
//
//	private static String getStrutsXmlContent(Table table, String username, String dbName) {
//
//		StringBuffer sb=new StringBuffer();
//
//		//获取参数
//		Map<String, String> inputParam=new HashMap<String, String>();
//		inputParam.put("ClassName", JavaUtil.getJavaClassName(table.getTableName()));
//		inputParam.put("VarName", JavaUtil.getJavaVarName(table.getTableName()).toLowerCase());
//		inputParam.put("UserName",username);
//		inputParam.put("DbName",dbName);
//
//		//获取findAll方法
//		ReportMappingSql mappSql= ReportUtils.getInstance().getMappingSql("createStrutsXml");
//		String findByParamCode=ReportUtils.replaceMappingSql(mappSql,inputParam);
//		sb.append(findByParamCode);
//
//
//		return sb.toString();
//	}
//
//	private static String getStrutsRootXmlContent(
//			List<String> xmlFileNameList) {
//
//		StringBuffer sb=new StringBuffer();
//
//		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>").append("\n")
//				.append("<!DOCTYPE struts PUBLIC").append("\n")
//				.append(" \"-//Apache Software Foundation//DTD Struts Configuration 2.0//EN\"").append("\n")
//				.append(" \"http://struts.apache.org/dtds/struts-2.0.dtd\">").append("\n");
//
//		sb.append("<struts>\n");
//
//		for(String xmlFileName:xmlFileNameList){
//			sb.append("    <include file=\""+xmlFileName+"\"></include>\n");
//		}
//		sb.append("</struts>\n");
//		return sb.toString();
//	}
//
//
//	public static String createCombineStrutsFile(String username, String dbName, List<CoreSet> setList){
//
////			File directory=new File(projectBasePath+"/src/");
////			File[] files=FileOperation.getFileList(directory);
//
//		//如果没有拥有者，默认为zqh用户
//		if("".equals(username)){
//			username="zqh";
//		}
//
//		//生成sql将要保存的路径包
//		String xmlFilePath=projectBasePath+"src/strutsconfig/"+dbName.toLowerCase()+"/";
//		String xmlFileName="";
//
//
//		//每张表生成一个sql文件,通过getSqlContent(table)获取文件内容
//		List<String> xmlFileNameList=new ArrayList<String>();
//		for (CoreSet coreSet : setList) {
//			xmlFileName=xmlFilePath+coreSet.getCoreSetName().toLowerCase()+".xml";
//			String content=getCombineStrutsXmlContent(coreSet,username,dbName.toLowerCase()).trim();
//			FileUtil.writeFile(xmlFileName, content);
//			xmlFileNameList.add(coreSet.getCoreSetName().toLowerCase()+".xml");
//		}
//
//		xmlFileName=xmlFilePath+"_"+dbName.toLowerCase()+".xml";
//		String content=getStrutsRootXmlContent(xmlFileNameList);
//		FileUtils.writeFile(xmlFileName, content);
//
//		xmlFileName=projectBasePath+"src/struts.xml";
//
//		StringBuffer sb=new StringBuffer();
//		sb.append("<!--"+dbName.toLowerCase()+"-->\n");
//		for(String xmlFileName1:xmlFileNameList){
//			sb.append("    <include file=\"strutsconfig/"+dbName.toLowerCase()+"/"+xmlFileName1+"\"></include>\n");
//		}
//		sb.append("<!--"+dbName.toLowerCase()+"-->\n");
//
//		FileUtils.appendMethod(xmlFileName,sb.toString(),dbName.toLowerCase());
//
//		return null;
//	}
//
//	private static String getCombineStrutsXmlContent(CoreSet coreSet, String username, String dbName) {
//
//		StringBuffer sb=new StringBuffer();
//
//		//获取参数
//		Map<String, String> inputParam=new HashMap<String, String>();
//		inputParam.put("ClassName", JavaUtil.getJavaClassName(coreSet.getCoreSetName()));
//		inputParam.put("VarName", JavaUtil.getJavaVarName(coreSet.getCoreSetName()).toLowerCase());
//		inputParam.put("UserName",username);
//		inputParam.put("DbName",dbName);
//
//		//获取findAll方法
//		ReportMappingSql mappSql=ReportUtils.getInstance().getMappingSql("createStrutsXml");
//		String findByParamCode=ReportUtils.replaceMappingSql(mappSql,inputParam);
//		sb.append(findByParamCode);
//
//
//		return sb.toString();
//	}
}
