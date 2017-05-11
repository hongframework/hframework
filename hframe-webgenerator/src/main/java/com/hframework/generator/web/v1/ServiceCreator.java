package com.hframework.generator.web.v1;


/**
 *
 * @author zqh
 *
 */
@Deprecated
public class ServiceCreator {

//	public static final String projectBasePath= CreatorUtil.projectBasePath;
//	public static final String projectTomcatBasePath=CreatorUtil.projectTomcatBasePath;
//
//	public static String createServiceFile(
//			String companyName, String projectName, List<Table> tableList) throws Exception {
//
//
//		for (Table table : tableList) {
//
//			CreatorContainer container = CreatorUtil.getCreatorContainer(
//					companyName, projectName, table.getTableName());
//			Class serviceClass = container.Service;
//
//			String content=getServiceContent(container, table);
//			FileUtils.writeFile(serviceClass.getFilePath(), content);
//		}
//
//		return null;
//	}
//
//
//	private static String getServiceContent(CreatorContainer container , Table table) throws Exception {
//
//		Class daoImplClass = container.DaoImpl;
//		Class daoClass = container.Dao;
//		Class poClass = container.Po;
//		Class serviceClass = container.Service;
//
//		List<CoreTableColumnRelationVo> relationList =  table.getCoreTableColumnRelationVo();
//
//		serviceClass.addImportClass("java.util.*");
//		serviceClass.addImportClass("javax.annotation.Resource");
//		serviceClass.addImportClass("org.springframework.stereotype.Component");
//		serviceClass.addImportClass("java.io.Serializable");
//		serviceClass.addImportClass(poClass.getClassPath());
//		serviceClass.addImportClass(daoClass.getClassPath());
//
//		serviceClass.setAnnotation("@Component");
//		//引入包
//
//		for (CoreTableColumnRelationVo coreTableColumnRelationVo : relationList) {
//			CreatorContainer relatContainer = CreatorUtil.getCreatorContainer(
//					container.companyName, container.projectName, coreTableColumnRelationVo.getTableNameTo());
//			serviceClass.addImportClass(relatContainer.Service.getClassPath());
//		}
//
//
////			//引入迭代运算所需要的Service
////			sb.append(CreatorUtil.getFkServImport(table,"Serv",""));
//
//
////			sb.append("@Component/n");
////			sb.append("public class "+JavaUtil.getJavaClassName(table.getTableName())+"Serv {/n/n");
//		//注入对应的DAO
//		serviceClass.addField(new Field(daoClass.getClassName()).addGetMethodAnno("@Resource"));
////			//注入对应的DAO
////			sb.append(CreatorUtil.injectBean(table.getTableName(),"DAO"));
//
//		//注入迭代运算所需要的Service
//
//		for (CoreTableColumnRelationVo coreTableColumnRelationVo : relationList) {
//			CreatorContainer relatContainer = CreatorUtil.getCreatorContainer(
//					container.companyName, container.projectName, coreTableColumnRelationVo.getTableNameTo());
//			serviceClass.addField(new Field(relatContainer.Service.getClassName()).addGetMethodAnno("@Resource"));
////				sb.append(CreatorUtil.injectBean(coreTableColumnRelationVo.getTableNameTo(),"Serv"));
//		}
//
//		Map contentMap=new HashMap();
//		contentMap.put("ClassName", JavaUtil.getJavaClassName(table.getTableName()));
//		contentMap.put("VarName", JavaUtil.getJavaVarName(table.getTableName()));
//		List<Map<String, String>> fklist = getFKMap(table, table.getCoreTableColumnRelationVo());
//		contentMap.put("FKList", fklist);
//		String Hql=getHqlCascade(table,poClass.getClassPath());
//		contentMap.put("HQL", Hql);
//		String CascadeContent=getCascadeContent(table,poClass.getClassPath());
//		contentMap.put("CascadeContent", CascadeContent);
////			contentMap.put("FkClassName", JavaUtil.getJavaClassName(coreTableColumnRelationVo.getColumnName()));
////			contentMap.put("FkVarName", JavaUtil.getJavaVarName(coreTableColumnRelationVo.getColumnName()));
//		String methodStr = VelocityUtil.produceTemplateContent("com/hframe/creator/vm/service_content.vm", contentMap);
//
//		serviceClass.setExtMethodStr(methodStr);
//		//生成对应的基本方法 增删改
////			sb.append(getBaseMethod(table.getTableName(),"DAO"));
//
//		//生成查询方法
////			sb.append(getFindMethod(table,"DAO"));
//
//
////			//含外键的查询方法
////			sb.append(getFindMethodFK(table,"DAO",poPath));
//
////			//级联预算
////			sb.append(getFindMethodCasCade1(table,"DAO",poPath));
//
//
//		Map  map=new HashMap();
//		map.put("CLASS", serviceClass);
//
//		String resultStr = VelocityUtil.produceTemplateContent("com/hframe/creator/vm/po.vm", map);
//		return resultStr;
//
////			sb.append("}/n/n");
//
////			return sb.toString();
//	}
//
////	private static String getCombineServiceContent(CoreSet coreSet,
////												   String serviceFileName, List<CoreTableColumnRelationVo> relationList) {
////
////		StringBuffer sb=new StringBuffer();
////
////		//声明包路径
////		sb.append("package "+serviceFileName.substring(serviceFileName.indexOf("/src/")+5,serviceFileName.lastIndexOf("/")).replace("/", ".")+";/n/n");
////
////		//引入包
////		sb.append("import java.util.*;/n/n");
////		sb.append("import javax.annotation.Resource;/n/n");
////		sb.append("import org.springframework.stereotype.Component;/n/n");
////		sb.append("import java.io.Serializable;/n/n");
////
////		//引入该表对应的dao，vo对象
////		String poPath=serviceFileName.substring(serviceFileName.indexOf("/src/")+5,serviceFileName.lastIndexOf("/service/")).replace("/", ".")+".po."+JavaUtil.getJavaClassName(coreSet.getCoreSetName());
////		sb.append("import "+poPath+";/n/n");
////
////		List<Table> coreTableList=coreSet.getCoreTableList();
////		for (Table table : coreTableList) {
////			String tablePoPath=CreatorUtil.getPoPath("",table.getCoreDb().getName(),table);
////			sb.append("import "+tablePoPath+";/n/n");
////
////		}
////		for (Table table : coreTableList) {
////			String tabledaoPath=CreatorUtil.getDaoPath("",table.getCoreDb().getName(),table);
////			sb.append("import "+tabledaoPath+"DAO;/n/n");
////		}
////
////
//////
//////
//////			//引入迭代运算所需要的Service
//////			sb.append(getFkServImport(table,"Serv",poPath));
////
////
////		sb.append("@Component/n");
////		sb.append("public class "+JavaUtil.getJavaClassName(coreSet.getCoreSetName())+"Serv {/n/n");
////
////		//注入对应的DAO
////		for (Table table : coreTableList) {
////			sb.append(CreatorUtil.injectBean(table.getTableName(),"DAO"));
////		}
////
////
//////			//注入迭代运算所需要的Service
////		//
//////			for (CoreTableColumnRelationVo coreTableColumnRelationVo : relationList) {
//////
//////				sb.append(injectBean(coreTableColumnRelationVo.getTableNameTo(),"Serv"));
//////			}
////
////		//生成对应的基本方法 增删改
////		sb.append(getBaseMethod(coreSet,"DAO"));
////
////
////		//生成查询方法
////		sb.append(getFindMethod(coreSet,"DAO"));
////
////		//含外键的查询方法
////		sb.append(getFindMethodFK(coreSet,"DAO",poPath));
////
////
////
////
////		sb.append("}/n/n");
////
////		return sb.toString();
////	}
//
//
////	public static String createCombineServiceFile(String username, String dbName, List<CoreSet> setList) {
////
////		if("".equals(username)){
////			username="zqh";
////		}
////		String servFilePath=projectBasePath+"src/"+"com/"+username.toLowerCase()+"/"+dbName.toLowerCase()+"/";
////
////		for (CoreSet coreSet : setList) {
////
////			String serviceName=JavaUtil.getJavaClassName(coreSet.getCoreSetName());
////			//获得table对应serv文件的名称
////			String serviceFileName=servFilePath+serviceName.toLowerCase()+"/service/"+serviceName+"Serv.java";
////			//找出table对象的外键关系
////			String content=getCombineServiceContent(coreSet,serviceFileName,null);
////			FileUtils.writeFile(serviceFileName, content);
////		}
////
////		return null;
////	}
////
////	private static String getFindMethod(CoreSet coreSet, String Type) {
////
////		StringBuffer sb=new StringBuffer();
////
////		StringBuffer sql=new StringBuffer();
////		sql.append("select new "+coreSet.getCoreSetName()+"(");
////
////		List<Table> tableList = coreSet.getCoreTableList();
////		for (Table table : tableList) {
////			List<Column> tableColumnList = table.getColumnList();
////			for (Column coreTableColumn : tableColumnList) {
////				if(coreSetContainsStr(coreSet,coreTableColumn)){
////					sql.append(JavaUtil.getJavaVarName(table.getTableName())+"."+JavaUtil.getJavaVarName(coreTableColumn.getColumnName())+",");
////				}
////			}
////		}
////		sql.substring(0,sql.length()-1);
////		sql.append(") from ");
////		for (Table table : tableList) {
////			List<Column> tableColumnList = table.getColumnList();
////			sql.append(JavaUtil.getJavaClassName(table.getTableName())+" "+JavaUtil.getJavaVarName(table.getTableName())+",");
////		}
////		sql.substring(0,sql.length()-1);
////
////		String sqlAheadPart=sql.toString();
////
////		//获取参数
////		Map<String, String> inputParam=new HashMap<String, String>();
////		inputParam.put("ClassName", JavaUtil.getJavaClassName(tableList.get(0).getTableName()));
////		inputParam.put("VarName", JavaUtil.getJavaVarName(tableList.get(0).getTableName()));
////		inputParam.put("VOClassName", JavaUtil.getJavaClassName(coreSet.getCoreSetName()));
////		inputParam.put("VOVarName", JavaUtil.getJavaVarName(coreSet.getCoreSetName()));
////		inputParam.put("sqlAheadPart", sqlAheadPart);
////		inputParam.put("PkColumnName", JavaUtil.getJavaVarName(tableList.get(0).getColumnList().get(0).getColumnName()));
////
////		//获取findAll方法
////		ReportMappingSql mappSql=ReportUtils.getInstance().getMappingSql("findAll_Combine");
////		String findByParamCode=ReportUtils.replaceMappingSql(mappSql,inputParam);
////		sb.append(findByParamCode);
////
////		//获取findByPK方法
////		mappSql=ReportUtils.getInstance().getMappingSql("findByPK_Combine");
////		findByParamCode=ReportUtils.replaceMappingSql(mappSql,inputParam);
////		sb.append(findByParamCode);
////
////		//获取findByParam方法
////		mappSql=ReportUtils.getInstance().getMappingSql("findByParam_Combine");
////		findByParamCode=ReportUtils.replaceMappingSql(mappSql,inputParam);
////		sb.append(findByParamCode);
////
////
////		sb.append("/**************上面为find基本方法************************//n/n");
////
////		return sb.toString();
////	}
//
//	private static List<Map<String, String>> getFKMap(
//			Table table, List<CoreTableColumnRelationVo> relationList) {
//
//		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
//
//		//获取该table的关系vo对象
//		for (CoreTableColumnRelationVo coreTableColumnRelationVo : relationList) {
//			Map<String, String> inputParam=new HashMap<String, String>();
//			inputParam.put("FkClassName", JavaUtil.getJavaClassName(coreTableColumnRelationVo.getColumnName()));
//			inputParam.put("FkVarName", JavaUtil.getJavaVarName(coreTableColumnRelationVo.getColumnName()));
//			inputParam.put("Sign","int".equals(coreTableColumnRelationVo.getColumnType()) ? "":"'");
//			inputParam.put("FKType",String.valueOf(coreTableColumnRelationVo.getType()));
//			list.add(inputParam);
//		}
//
//
//		return list;
//	}
//
////	private static String getFindMethodFK(CoreSet coreSet, String Type, String poPath) {
////
////		StringBuffer sb=new StringBuffer();
////		sb.append("    /**************下面为find外键查詢************************//n/n");
////
////		StringBuffer sql=new StringBuffer();
////		sql.append("select new " + coreSet.getCoreSetName() + "(");
////
////		List<Table> tableList = coreSet.getCoreTableList();
////		for (Table table : tableList) {
////			List<Column> tableColumnList = table.getColumnList();
////			for (Column coreTableColumn : tableColumnList) {
////				if(coreSetContainsStr(coreSet,coreTableColumn)){
////					sql.append(JavaUtil.getJavaVarName(table.getTableName())+"."+JavaUtil.getJavaVarName(coreTableColumn.getColumnName())+",");
////				}
////			}
////		}
////		sql.substring(0,sql.length() - 1);
////		sql.append(") from ");
////		for (Table table : tableList) {
////			List<Column> tableColumnList = table.getColumnList();
////			sql.append(JavaUtil.getJavaClassName(table.getTableName())+" "+JavaUtil.getJavaVarName(table.getTableName())+",");
////		}
////		sql.substring(0,sql.length()-1);
////
////		String sqlAheadPart=sql.toString();
////
////		//获取参数
////		Map<String, String> inputParam=new HashMap<String, String>();
////		inputParam.put("VOClassName", JavaUtil.getJavaClassName(coreSet.getCoreSetName()));
////		inputParam.put("VOVarName", JavaUtil.getJavaVarName(coreSet.getCoreSetName()));
////		inputParam.put("sqlAheadPart", sqlAheadPart);
////		inputParam.put("PkColumnName", JavaUtil.getJavaVarName(tableList.get(0).getColumnList().get(0).getColumnName()));
////
////		Table table=coreSet.getCoreTableList().get(0);
////
////
////		//获取该table的关系vo对象
////		List<CoreTableColumnRelationVo> relationList=table.getCoreTableColumnRelationVo();
////
////		for (CoreTableColumnRelationVo coreTableColumnRelationVo : relationList) {
////
////			inputParam.put("ClassName", JavaUtil.getJavaClassName(table.getTableName()));
////			inputParam.put("VarName", JavaUtil.getJavaVarName(table.getTableName()));
////			inputParam.put("FkClassName", JavaUtil.getJavaClassName(coreTableColumnRelationVo.getColumnName()));
////			inputParam.put("FkVarName", JavaUtil.getJavaVarName(coreTableColumnRelationVo.getColumnName()));
////
////			if("int".equals(coreTableColumnRelationVo.getColumnType())){
////				inputParam.put("Sign", "");
////			}else{
////				inputParam.put("Sign", "'");
////			}
////
////			if(2!=coreTableColumnRelationVo.getType()){
////				//find级联运算
////				ReportMappingSql mappSql=ReportUtils.getInstance().getMappingSql("findListByFK_Combine");
////				String findByParamCode=ReportUtils.replaceMappingSql(mappSql,inputParam);
////
////				if(sb.indexOf(findByParamCode)<=0){
////					sb.append(findByParamCode);
////				}
////
////			}else{
////				//find级联运算
////				ReportMappingSql mappSql=ReportUtils.getInstance().getMappingSql("findOneByPK_Combine");
////				String findByParamCode=ReportUtils.replaceMappingSql(mappSql,inputParam);
////
////				if(sb.indexOf(findByParamCode)<=0){
////					sb.append(findByParamCode);
////				}
////			}
////		}
////		sb.append("    /**************上面为find外键查詢************************//n/n");
////
////
////		return sb.toString();
////	}
//
//
//
//	private static String getFKParamQueryList(
//			List<CoreTableColumnRelationVo> relationList) {
//
//		String s="";
//
//
//		for (int i = 0; i < relationList.size(); i++) {
//
//			if(2==relationList.get(i).getType()) continue;
//			s+=JavaUtil.getJavaVarName(relationList.get(i).getTableNameTo());
//			s+=",";
//		}
//		return s.length()>0?s.substring(0,s.length()-1):s;
//	}
//
//	private static String getHqlCascade(Table table, String poPath) {
//
//		List<CoreTableColumnRelationVo> relationList= table.getCoreTableColumnRelationVo();
//		String str="select new "+poPath+"("+JavaUtil.getJavaVarName(table.getTableName())+(getFKParamQueryList(relationList)==""?"":(","+getFKParamQueryList(relationList)))+")";
//
//		String headStr=" from "+JavaUtil.getJavaClassName(table.getTableName())+" "+JavaUtil.getJavaVarName(table.getTableName())+(CreatorUtil.getFKParamList(relationList)==""?"":(", "+CreatorUtil.getFKParamList(relationList)));
//
//		String endStr=" where 1=1 ";
//
//		for (CoreTableColumnRelationVo coreTableColumnRelationVo : relationList) {
//			if(2!=coreTableColumnRelationVo.getType()){
//				endStr+="and "+JavaUtil.getJavaVarName(table.getTableName())+"."+JavaUtil.getJavaVarName(coreTableColumnRelationVo.getColumnName())+"="+JavaUtil.getJavaVarName(coreTableColumnRelationVo.getTableNameTo())+"."+JavaUtil.getJavaVarName(coreTableColumnRelationVo.getColumnNameTo());
//			}
//		}
//
//		return str+headStr+endStr+" ";
//	}
//
//	private static String getCascadeContent(Table table, String poPath) {
//
//		String str="";
//
//		//获取该table的关系vo对象
//		List<CoreTableColumnRelationVo> relationList= table.getCoreTableColumnRelationVo();
//
//		for (CoreTableColumnRelationVo coreTableColumnRelationVo : relationList) {
//			if(2==coreTableColumnRelationVo.getType()){
//				str+=JavaUtil.getJavaVarName(table.getTableName())+".set"+JavaUtil.getJavaClassName(coreTableColumnRelationVo.getTableNameTo())+"List("+
//						JavaUtil.getJavaVarName(coreTableColumnRelationVo.getTableNameTo())+"Serv.get"+JavaUtil.getJavaClassName(coreTableColumnRelationVo.getTableNameTo())+
//						"By"+JavaUtil.getJavaClassName(coreTableColumnRelationVo.getColumnNameTo())+"("+JavaUtil.getJavaVarName(table.getTableName())+".get"+
//						JavaUtil.getJavaClassName(coreTableColumnRelationVo.getColumnName())+"(),cascadeLevel-1));\n\n";
//			}
//		}
//
//
//
//		return str;
//	}
//
//
//
////	private static String getBaseMethod(CoreSet coreSet, String Type) {
////
////		StringBuffer sb=new StringBuffer();
////
////		sb.append("/**************下面为日常常用方法************************//n/n");
////
////		sb.append("  public void create("+JavaUtil.getJavaClassName(coreSet.getCoreSetName())+" "+JavaUtil.getJavaVarName(coreSet.getCoreSetName())+"){/n/n");
////		sb.append("    try{/n");
////
////		List<Table> tableList=coreSet.getCoreTableList();
////		for (Table table : tableList) {
////			sb.append("      "+JavaUtil.getJavaClassName(table.getTableName())+" "+JavaUtil.getJavaVarName(table.getTableName())+" = new "+JavaUtil.getJavaClassName(table.getTableName())+"();/n");
////		}
////		for (Table table : tableList) {
////			List<Column> columnList=table.getColumnList();
////			for (Column coreTableColumn : columnList) {
////				if(coreSetContainsStr(coreSet,coreTableColumn)){
////					sb.append("      "+JavaUtil.getJavaVarName(table.getTableName())+".set"+JavaUtil.getJavaClassName(coreTableColumn.getColumnName())+"("+JavaUtil.getJavaVarName(coreSet.getCoreSetName())+".get"+JavaUtil.getJavaClassName(coreTableColumn.getColumnName())+"());"+"/n");
////				}
////			}
////		}
////
////		for (Table table : tableList) {
////			sb.append("      "+JavaUtil.getJavaVarName(table.getTableName())+Type+" .saveOrUpdate("+JavaUtil.getJavaVarName(table.getTableName())+");/n");
////		}
////
////
////		sb.append("    } catch (Exception e) {/n");
////		sb.append("      e.printStackTrace();/n");
////		sb.append("    }/n/n");
////		sb.append("  }/n/n");
////
////
////		sb.append("  public void batchCreate("+JavaUtil.getJavaClassName(coreSet.getCoreSetName())+"[] "+JavaUtil.getJavaVarName(coreSet.getCoreSetName())+"s){/n/n");
////		sb.append("    try{/n");
////		for (Table table : tableList) {
////			sb.append("      "+JavaUtil.getJavaClassName(table.getTableName())+"[] "+JavaUtil.getJavaVarName(table.getTableName())+"s = new "+JavaUtil.getJavaClassName(table.getTableName())+"["+JavaUtil.getJavaVarName(coreSet.getCoreSetName())+"s.length];/n");
////		}
////		sb.append("      for(int i=0;i<"+JavaUtil.getJavaVarName(coreSet.getCoreSetName())+"s.length;i++){/n");
////
//////		for (Table table : tableList) {
//////			sb.append("      "+JavaUtil.getJavaClassName(table.getTableName())+" "+JavaUtil.getJavaVarName(table.getTableName())+" = new "+JavaUtil.getJavaClassName(table.getTableName())+"();/n");
//////		}
////		for (Table table : tableList) {
////			List<Column> columnList=table.getColumnList();
////			for (Column coreTableColumn : columnList) {
////				if(coreSetContainsStr(coreSet,coreTableColumn)){
////					sb.append("          "+JavaUtil.getJavaVarName(table.getTableName())+"s[i].set"+JavaUtil.getJavaClassName(coreTableColumn.getColumnName())+"("+JavaUtil.getJavaVarName(coreSet.getCoreSetName())+"s[i].get"+JavaUtil.getJavaClassName(coreTableColumn.getColumnName())+"());"+"/n");
////				}
////			}
////		}
////		sb.append("      }/n");
////		for (Table table : tableList) {
////			sb.append("      "+JavaUtil.getJavaVarName(table.getTableName())+Type+" .batchSave("+JavaUtil.getJavaVarName(table.getTableName())+"s);/n");
////		}
////		sb.append("    } catch (Exception e) {/n");
////		sb.append("      e.printStackTrace();/n");
////		sb.append("    }/n/n");
////		sb.append("  }/n/n");
////
////
////		//sb.append("  public void batchCreate(List<"+JavaUtil.getJavaClassName(tableName)+"> "+JavaUtil.getJavaVarName(tableName)+"s){/n/n");
////		sb.append("  public void batchCreate(List "+JavaUtil.getJavaVarName(coreSet.getCoreSetName())+"s){/n/n");
////		sb.append("     batchCreate(("+JavaUtil.getJavaClassName(coreSet.getCoreSetName())+"[])"+JavaUtil.getJavaVarName(coreSet.getCoreSetName())+"s.toArray(new "+JavaUtil.getJavaClassName(coreSet.getCoreSetName())+"[0])"+");/n/n");
////		sb.append("  }/n/n");
////
////
////
////		sb.append("  public void update("+JavaUtil.getJavaClassName(coreSet.getCoreSetName())+" "+JavaUtil.getJavaVarName(coreSet.getCoreSetName())+"){/n/n");
////		sb.append("    try{/n");
////		for (Table table : tableList) {
////			sb.append("      "+JavaUtil.getJavaClassName(table.getTableName())+" "+JavaUtil.getJavaVarName(table.getTableName())+" = new "+JavaUtil.getJavaClassName(table.getTableName())+"();/n");
////		}
////		for (Table table : tableList) {
////			List<Column> columnList=table.getColumnList();
////			for (Column coreTableColumn : columnList) {
////				if(coreSetContainsStr(coreSet,coreTableColumn)){
////					sb.append("      "+JavaUtil.getJavaVarName(table.getTableName())+".set"+JavaUtil.getJavaClassName(coreTableColumn.getColumnName())+"("+JavaUtil.getJavaVarName(coreSet.getCoreSetName())+".get"+JavaUtil.getJavaClassName(coreTableColumn.getColumnName())+"());"+"/n");
////				}
////			}
////		}
////
////		for (Table table : tableList) {
////			sb.append("      "+JavaUtil.getJavaVarName(table.getTableName())+Type+" .update("+JavaUtil.getJavaVarName(table.getTableName())+");/n");
////		}
////		sb.append("    } catch (Exception e) {/n");
////		sb.append("      e.printStackTrace();/n");
////		sb.append("    }/n/n");
////		sb.append("  }/n/n");
////
////		sb.append("  public void delete("+JavaUtil.getJavaClassName(coreSet.getCoreSetName())+" "+JavaUtil.getJavaVarName(coreSet.getCoreSetName())+"){/n/n");
////		sb.append("    try{/n");
////		for (Table table : tableList) {
////			sb.append("      "+JavaUtil.getJavaClassName(table.getTableName())+" "+JavaUtil.getJavaVarName(table.getTableName())+" = new "+JavaUtil.getJavaClassName(table.getTableName())+"();/n");
////		}
////		for (Table table : tableList) {
////			List<Column> columnList=table.getColumnList();
////			for (Column coreTableColumn : columnList) {
////				if(coreSetContainsStr(coreSet,coreTableColumn)){
////					sb.append("      "+JavaUtil.getJavaVarName(table.getTableName())+".set"+JavaUtil.getJavaClassName(coreTableColumn.getColumnName())+"("+JavaUtil.getJavaVarName(coreSet.getCoreSetName())+".get"+JavaUtil.getJavaClassName(coreTableColumn.getColumnName())+"());"+"/n");
////				}
////			}
////		}
////
////		for (Table table : tableList) {
////			sb.append("      "+JavaUtil.getJavaVarName(table.getTableName())+Type+" .delete("+JavaUtil.getJavaVarName(table.getTableName())+");/n");
////		}
////		sb.append("    } catch (Exception e) {/n");
////		sb.append("      e.printStackTrace();/n");
////		sb.append("    }/n/n");
////		sb.append("  }/n/n");
////
////		sb.append("  public void delete(Serializable id){/n/n");
////		sb.append("    try{/n");
////		for (Table table : tableList) {
////			sb.append("      "+JavaUtil.getJavaVarName(table.getTableName())+Type+" .delete(id);/n");
////		}
////		sb.append("    } catch (Exception e) {/n");
////		sb.append("      e.printStackTrace();/n");
////		sb.append("    }/n/n");
////		sb.append("  }/n/n/n/n");
////
////		sb.append("/**************上面为日常常用方法************************//n/n");
////
////
////		return sb.toString();
////	}
//
////	private static boolean coreSetContainsStr(CoreSet coreSet,
////											  Column coreTableColumn) {
////
////		List<Column> columnList = coreSet.getCoreColumnList();
////		for (Column coreTableColumn2 : columnList) {
////			if(coreTableColumn2.getId().equals(coreTableColumn.getId())){
////				return true;
////			}
////		}
////
////		return false;
////	}

}
