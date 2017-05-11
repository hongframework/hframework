package com.hframework.generator.web.v1.hibernate;

/**
 *
 * @author zqh
 *
 */
@Deprecated
public class HbmCreator {

//	public static final String projectBasePath= CreatorUtil.projectBasePath;
//	public static final String projectTomcatBasePath=CreatorUtil.projectTomcatBasePath;
//
//	public static void createHbmFile(String username, String dbName,
//									 List<Table> tableList) {
//
//		if("".equals(username)){
//			username="zqh";
//		}
//
//		String xmlFilePath=projectBasePath+"src/"+"com/"+username.toLowerCase()+"/"+dbName.toLowerCase()+"/";
//
//		for (Table table : tableList) {
//
//			String xmlName=JavaUtil.getJavaClassName(table.getTableName());
//
//			String xmlFileName=xmlFilePath+"/hbm/"+xmlName+".hbm.xml";
//
//			DOMDocument doc=getXmlContent(table,xmlFileName);
//			Dom4jUtils.writeToFile(doc, xmlFileName);
//		}
//	}
//
//	private static DOMDocument getXmlContent(Table table, String xmlFileName) {
//
//		DOMDocument doc=new DOMDocument();
//
//		DOMDocumentType docType=new DOMDocumentType("hibernate-mapping", "-//Hibernate/Hibernate Mapping DTD 3.0//EN", "http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd");
//		doc.setDocType(docType);
//
//		DOMElement rootElement=new DOMElement("hibernate-mapping");
//		doc.add(rootElement);
//
//		DOMElement classElement=new DOMElement("class");
//		String poPath=xmlFileName.substring(xmlFileName.indexOf("/src/")+5,xmlFileName.lastIndexOf("/hbm/")).replace("/", ".")+JavaUtil.getJavaClassName(table.getTableName()).toLowerCase()+".po."+JavaUtil.getJavaClassName(table.getTableName());
//		classElement.setAttribute("name", poPath);
//		classElement.setAttribute("table", table.getTableName());
//		rootElement.add(classElement);
//
//		List<CoreTableColumn> columnList= table.getColumnList();
//
//		int pkCount=0;
//		for (CoreTableColumn column : columnList) {
//
//			if(column.getIspk()==1&&pkCount==0){
//				DOMElement idElement=new DOMElement("id");
//				idElement.setAttribute("name", JavaUtil.getJavaVarName(column.getColumnName()));
//				idElement.setAttribute("type", Constant.column2ObjectFullPath.get(column.getColumnType()));
//				classElement.add(idElement);
//
//				DOMElement columnElement=new DOMElement("column");
//
//				columnElement.setAttribute("name", column.getColumnName());
//				if(column.getColumnSize()>0){
//					columnElement.setAttribute("length", column.getColumnSize()+"");
//				}
//				idElement.add(columnElement);
//
//				DOMElement generatorElement=new DOMElement("generator");
//				generatorElement.setAttribute("class", "assigned");
//				idElement.add(generatorElement);
//				pkCount++;
//				continue;
//			}
//
//			column.setIspk(0);
//		}
//
//		for (CoreTableColumn column : columnList) {
//
//			if(column.getIspk()!=1){
//				DOMElement propertyElement=new DOMElement("property");
//				propertyElement.setAttribute("name", JavaUtil.getJavaVarName(column.getColumnName()));
//				propertyElement.setAttribute("type", Constant.column2ObjectFullPath.get(column.getColumnType()));
//				classElement.add(propertyElement);
//
//				DOMElement columnElement=new DOMElement("column");
//
//				columnElement.setAttribute("name", column.getColumnName());
//				if(column.getColumnSize()!=null&&column.getColumnSize()>0){
//					columnElement.setAttribute("length", column.getColumnSize()+"");
//				}
//				propertyElement.add(columnElement);
//			}
//		}
//
//
//		return doc;
//	}

}
