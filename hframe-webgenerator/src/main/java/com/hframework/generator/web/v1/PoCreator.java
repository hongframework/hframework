package com.hframework.generator.web.v1;


/**
 * @author zhangqh6
 *
 */
@Deprecated
public class PoCreator {

//	public static final String projectBasePath= CreatorUtil.projectBasePath;
//	public static final String projectTomcatBasePath=CreatorUtil.projectTomcatBasePath;
//
//	private static String companyName1; //公司名称
//	private static String projectName1; //项目名称
//	private static String moduleName1; //模块名称
//
//	public static String createPoFile(String companyName, String projectName,String moduleName, List<Table> tableList) throws Exception{
//
//		companyName1 = companyName;
//		projectName1 = projectName;
//		moduleName1 = moduleName;
//
//		for (Table table : tableList) {
//
//			Class poClass = CreatorUtil.getCreatorContainer(
//					companyName, projectName, table.getTableName()).Po;
//
//			List<Column> columnList= table.getColumnList();
//
//			List<String> fkTableName = new ArrayList<String>();
//			//包含外键的时候，vo对象需要做的使用引用类的关系
//			for (Column column : table.getColumnList()) {
//				if(column.isFk()) {
//					fkTableName.add(column.getFkTableName());
//				}
//			}
//
//			String content=getPoContent(poClass, table);
//			FileUtils.writeFile(poClass.getFilePath(), content);
//		}
//		return null;
//	}
//
//	private static String getPoContent(Class poClass, Table table) throws Exception {
//
//
//		//包含外键的时候，vo对象需要做的使用引用类的关系
//		for (Column column : table.getColumnList()) {
//			if(column.isFk()) {
//				String relatClassPath = CreatorUtil.getPoClassPath(
//						companyName1, projectName1, column.getFkTableName());
//				poClass.addImportClass(relatClassPath);
//			}
//		}
//
//
//		List<Column> columnList= table.getColumnList();
//		List<Field> fieldList =new ArrayList<Field>();
//		for (Column Column : columnList) {
//			Field field = new Field(
//					Constant.column2ObjectType.get(Column.getColumnType()),
//					JavaUtil.getJavaVarName(Column.getColumnName()));
//			poClass.addField(field);
//			fieldList.add(field);
//		}
//
//
//		//包含外键的时候，vo对象需要做的使用引用类的关系
//		boolean containtFk=false;
//		for (ColumnRelationVo ColumnRelationVo : relationList) {
//			if(2==ColumnRelationVo.getType()){
//				poClass.addField(new Field(
//						"List<" + JavaUtil.getJavaClassName(ColumnRelationVo.getTableNameTo()) + ">",
//						JavaUtil.getJavaVarName(ColumnRelationVo.getTableNameTo()) + "List"
//				));
//			}else{
//				poClass.addField(new Field(
//						JavaUtil.getJavaClassName(ColumnRelationVo.getTableNameTo())));
//				containtFk=true;
//			}
//		}
//
//		poClass.addConstructor();
//
//		//含全参数的构造方法
//		if(columnList.size()>0){
//			poClass.addConstructor(new Constructor(fieldList));
//		}
//
//
//
//		//含外键的构造方法-----start
//		if(containtFk==true){
//			Constructor constructor = poClass.addConstructor();
//			constructor.addParameters(fieldList);
//			constructor.addParameters(CreatorUtil.getFKParameterList(relationList));
//
//			for (int i = 0; i < columnList.size(); i++) {
//				constructor.addCodeLn(
//						"this." + JavaUtil.getJavaVarName(columnList.get(i).getColumnName()) +
//								" = " + JavaUtil.getJavaVarName(table.getTableName())+".get"
//								+JavaUtil.getJavaClassName(columnList.get(i).getColumnName())+"();");
//			}
//			for (ColumnRelationVo ColumnRelationVo : relationList) {
//				if(2!=ColumnRelationVo.getType()){
//					constructor.addCodeLn(
//							"this." + JavaUtil.getJavaVarName(ColumnRelationVo.getTableNameTo()) +
//									" = " + JavaUtil.getJavaVarName(ColumnRelationVo.getTableNameTo())+";");
//				}
//			}
//		}
//
//		Map  map=new HashMap();
//		map.put("CLASS", poClass);
//
//		String resultStr = VelocityUtil.produceTemplateContent("com/hframe/creator/vm/po.vm", map);
//		return resultStr;
//	}
//	public static String createCombinePoFile(String companyName, String projectName, List<Table> setList) throws Exception{
//
//		companyName1 = companyName;
//		projectName1 = projectName;
//
//		for (Table coreSet : setList) {
//
//			Class poClass = new Class();
//			poClass.setSrcFilePath(CreatorUtil.getSrcFilePath(companyName, projectName));
//			poClass.setClassPath(CreatorUtil.getPoClassPath(
//					companyName, projectName, coreSet.getTableName()));
//
//			String content=getCombinePoContent(poClass,coreSet);
//			FileUtils.writeFile(poClass.getFilePath(), content);
//		}
//		return null;
//	}
//
//	private static String getCombinePoContent(Class poClass, Table coreSet) {
//
//
//		List<Field> fieldList =new ArrayList<Field>();
//		List<Column> columnList=coreSet.getColumnList();
//		for (Column Column : columnList) {
//			Field field = new Field(
//					Constant.column2ObjectType.get(Column.getColumnType()),
//					JavaUtil.getJavaVarName(Column.getColumnName()));
//			poClass.addField(field);
//			fieldList.add(field);
//		}
//
//		//含全参数的构造方法
//		if(columnList.size()>0){
//			poClass.addConstructor(new Constructor(fieldList));
//		}
//
//		Map  map=new HashMap();
//		map.put("CLASS", poClass);
//
//		String resultStr = VelocityUtil.produceTemplateContent("com/hframe/creator/vm/po.vm", map);
//
//		return resultStr;
//	}
//
//	private static String getParamList(List<Column> columnList) {
//
//		String s="";
//		for (int i = 0; i < columnList.size(); i++) {
//			s+=Constant.column2ObjectType.get(columnList.get(i).getColumnType())+" "+JavaUtil.getJavaVarName(columnList.get(i).getColumnName());
//
//			if(i!=columnList.size()-1){
//				s+=", ";
//			}
//		}
//		return s;
//	}
}
