package com.hframework.generator.web.v1;

@Deprecated
public class ActionCreator {
//
//	public static final String projectBasePath= CreatorUtil.projectBasePath;
//	public static final String projectTomcatBasePath=CreatorUtil.projectTomcatBasePath;
//
//	private static List<HfpmPageEventAttr> loginSessionEntitys = null;
//
//	public static String createActionFile(String companyName, String projectName,
//										  List<Table> tableList,List<HfpmPageEventAttr> loginSessionEntitys) throws Exception {
//
//		ActionCreator.loginSessionEntitys = loginSessionEntitys;
//
//		for (Table table : tableList) {
//
//			CreatorContainer container = CreatorUtil.getCreatorContainer(
//					companyName, projectName, table.getTableName());
//			Class actionClass = container.Action;
//
//			String content=getActionContent(container, table,
//					table.getCoreTableColumnRelationVo());
//			FileUtils.writeFile(actionClass.getFilePath(), content);
//		}
//		return null;
//	}
//
//	private static String getActionContent(CreatorContainer container, Table table,
//										   List<CoreTableColumnRelationVo> relationList) throws Exception {
//
//		Class actionClass = container.Action;
//		Class serviceClass = container.Service;
//		Class poClass = container.Po;
//
//		actionClass.setAnnotation("@Component");
//		actionClass.setSuperClass("AbstractActionSupport");
//
//		actionClass.addImportClass("org.springframework.stereotype.Component");
//		actionClass.addImportClass("javax.annotation.Resource");
//		actionClass.addImportClass("ava.util.*");
//		actionClass.addImportClass("com.hframework.common.util.COMMON");
//		actionClass.addImportClass("com.hframework.common.ssh.action.AbstractActionSupport");
//		actionClass.addImportClass(serviceClass.getClassPath());
//		actionClass.addImportClass(poClass.getClassPath());
//
//		for (CoreTableColumnRelationVo coreTableColumnRelationVo : relationList) {
//			CreatorContainer relContainer = CreatorUtil.getCreatorContainer(
//					container.companyName, container.projectName, coreTableColumnRelationVo.getTableNameTo());
//			actionClass.addImportClass(relContainer.Po.getClassPath());
//			actionClass.addImportClass(relContainer.Service.getClassPath());
//		}
//
//		actionClass.addField(new Field(
//				getJavaClassName(table.getTableName())+"Serv").addGetMethodAnno("@Resource"));
//		//注入service层
//		for (CoreTableColumnRelationVo coreTableColumnRelationVo : relationList) {//注入关联的Service
//			actionClass.addField(new Field(getJavaClassName(
//					coreTableColumnRelationVo.getTableNameTo())+"Serv").addGetMethodAnno("@Resource"));
//		}
//
//
//		//注册vo对象
//		actionClass.addField(new Field(getJavaClassName(table.getTableName())));
//		actionClass.addField(new Field(
//				"List<" + getJavaClassName(table.getTableName()) + ">",
//				getJavaVarName(table.getTableName())+"s"));
//
//		for (CoreTableColumnRelationVo coreTableColumnRelationVo : relationList) {//注入关联的Vo
//
//			if(coreTableColumnRelationVo.getType()==2){
//				actionClass.addField(new Field(
//						"List<" + getJavaClassName(coreTableColumnRelationVo.getTableNameTo()) + ">",
//						getJavaVarName(coreTableColumnRelationVo.getTableNameTo())+"s"));
//			}else{
//				actionClass.addField(new Field(getJavaClassName(coreTableColumnRelationVo.getTableNameTo())));
//			}
//		}
//
//		actionClass.setExtMethodStr(getMethodStr(table, relationList));
//
//		Map  map=new HashMap();
//		map.put("CLASS", actionClass);
//
//		String resultStr = VelocityUtil.produceTemplateContent("com/hframe/creator/vm/po.vm", map);
//
//		return resultStr;
//	}
//
//	private static String getJavaClassName(String value) {
//		return JavaUtil.getJavaClassName(value);
//	}
//
//	private static String getJavaVarName(String value) {
//		return JavaUtil.getJavaVarName(value);
//	}
//
//	private static String getMethodStr( Table table,
//									   List<CoreTableColumnRelationVo> relationList) {
//		StringBuffer sb = new StringBuffer();
//		//生成list方法
//		sb.append("    public String list(){/n/n");
//		sb.append("        setFourScope();/n/n");
//		List<CoreControl> controlList =
//				coreControlServ.findCoreControlByControlNameAndControlQuote("list", table.getTableName());
//		List<CoreControl> sessionList = coreControlServ.findCoreControlByControlName("session");
//		String sessiontable = sessionList.size()>0?sessionList.get(0).getControlValue():"";
//
//		if(controlList.size()>0){
//			String[] values=controlList.get(0).getControlValue().split(",");
//			sb.append("        Map<String, String> params=new HashMap<String, String>();");
//			for (String value : values) {
//				if(!"".equals(value)){
//					sb.append("        String "+getJavaVarName(value)+" = request.getParameter(\""+getJavaVarName(value)+"\");/n");
//					sb.append("        params.put(\""+getJavaVarName(value)+"\","+getJavaVarName(value)+");/n");
//				}
//			}
//			sb.append("        List<"+getJavaClassName(table.getTableName())+"> list="+getJavaVarName(table.getTableName())+"Serv.find"+getJavaClassName(table.getTableName())+"ByParams(params);/n/n");
//		}else if(!"".equals(sessiontable)){
//			sb.append("        "+getJavaClassName(sessiontable)+" session"+getJavaVarName(sessiontable)+"=("+getJavaClassName(sessiontable)+")session.getAttribute(session"+getJavaVarName(sessiontable)+");");
//			sb.append("        List<"+getJavaClassName(table.getTableName())+"> list="+getJavaVarName(table.getTableName())+"Serv.find"+getJavaClassName(table.getTableName())+"ByXXX(xxx);/n/n");
//		}else{
//			sb.append("        List<"+getJavaClassName(table.getTableName())+"> list="+getJavaVarName(table.getTableName())+"Serv.find"+getJavaClassName(table.getTableName())+"All();/n/n");
//		}
//		sb.append("        request.setAttribute(\"list\", list);/n/n");
//		sb.append("        return SUCCESS;/n/n");
//		sb.append("    }/n/n");
//
//		//生成create方法
//		sb.append("    public String create(){/n/n");
//		sb.append("        setFourScope();/n/n");
//		if(!"".equals(sessiontable)){
//			sb.append("        "+getJavaClassName(sessiontable)+" session"+getJavaVarName(sessiontable)+"=("+getJavaClassName(sessiontable)+")session.getAttribute(session"+getJavaVarName(sessiontable)+");");
//
//			for(CoreTableColumnRelationVo relation:relationList){
//				if(sessiontable.equals(relation.getTableNameTo())){
//					sb.append("        "+getJavaVarName(table.getTableName())+".set"+getJavaClassName(relation.getColumnName())+"("+"session"+getJavaVarName(sessiontable)+");/n/n");
//				}
//			}
//		}
//
//		List<CoreTableColumn> columnList= table.getColumnList();
//		String pkId=autofullObj(columnList, table.getTableName(),sb,null);
//		sb.append("        "+getJavaVarName(table.getTableName())+"Serv.create("+getJavaVarName(table.getTableName())+");/n/n");
//
//		//如果有子关系一起保存
//		sb.append("        ////如果有子关系一起保存/n");
//		for (CoreTableColumnRelationVo coreTableColumnRelationVo : relationList) {//注入关联的Vo
//
//			columnList=coreTableColumnServ.findCoreTableColumnByTableId(coreTableColumnRelationVo.getTableIdTo());
//
//			if(coreTableColumnRelationVo.getType()==2){
//				sb.append("        if("+getJavaVarName(coreTableColumnRelationVo.getTableNameTo())+"s!=null){/n");
//				sb.append("        	 putRightList("+getJavaVarName(coreTableColumnRelationVo.getTableNameTo())+"s,request);/n/n");
//
//				sb.append("          for("+getJavaClassName(coreTableColumnRelationVo.getTableNameTo())+" "+getJavaVarName(coreTableColumnRelationVo.getTableNameTo())+" : "+getJavaVarName(coreTableColumnRelationVo.getTableNameTo())+"s){/n");
//
//				///TODO
//				autofullObj(columnList,coreTableColumnRelationVo.getTableNameTo(),sb,null);
//				sb.append("              "+getJavaVarName(coreTableColumnRelationVo.getTableNameTo())+".set"+getJavaClassName(coreTableColumnRelationVo.getColumnNameTo())+"("+pkId+");/n");
//
//				sb.append("          }/n/n");
//				sb.append("          "+getJavaVarName(coreTableColumnRelationVo.getTableNameTo())+"Serv.batchCreate("+getJavaVarName(coreTableColumnRelationVo.getTableNameTo())+"s);/n");
//				sb.append("        }/n");
//			}else{
////				autofullObj(columnList,coreTableColumnRelationVo.getTableNameTo(),sb);
////				sb.append("            "+getJavaVarName(coreTableColumnRelationVo.getTableNameTo())+".set"+getJavaClassName(coreTableColumnRelationVo.getColumnNameTo())+"("+pkId+");/n");
////				sb.append("            "+getJavaVarName(coreTableColumnRelationVo.getTableNameTo())+"Serv.create("+getJavaVarName(coreTableColumnRelationVo.getTableNameTo())+");/n");
//			}
//		}
//
//		sb.append("        return SUCCESS;/n/n");
//		sb.append("    }/n/n");
//
//		//生成create方法
//		sb.append("    public String batchCreate(){/n/n");
//		sb.append("        setFourScope();/n/n");
//		sb.append("        putRightList("+getJavaVarName(table.getTableName())+"s,request);/n/n");
//
//
//		sb.append("        String globalParam=request.getParameter(\"GlobalParam\") ;/n/n");
//		sb.append("        String key= null,value = null;/n/n");
//		sb.append("        if(globalParam!=null&&!\"\".equals(globalParam)){/n/n");
//		sb.append("        		key=globalParam.substring(0,globalParam.indexOf(\"=\"));/n/n");
//		sb.append("        		value=globalParam.substring(globalParam.indexOf(\"=\")+1);/n/n");
//		sb.append("        		if(value.startsWith(\"'\")&&value.endsWith(\"'\")){/n/n");
//		sb.append("        			value=value.substring(1, value.length()-1);/n/n");
//		sb.append("        		}/n/n");
//		sb.append("        }/n/n");
//
//
//		sb.append("		    for ("+getJavaClassName(table.getTableName())+" a"+getJavaClassName(table.getTableName())+" : "+getJavaVarName(table.getTableName())+"s) {/n");
//
//		if(!"".equals(sessiontable)){
//			sb.append("        		"+getJavaClassName(sessiontable)+" session"+getJavaVarName(sessiontable)+"=("+getJavaClassName(sessiontable)+")session.getAttribute(session"+getJavaVarName(sessiontable)+");");
//			for(CoreTableColumnRelationVo relation:relationList){
//				if(sessiontable.equals(relation.getTableNameTo())){
//					sb.append("        		a"+getJavaClassName(table.getTableName())+".set"+getJavaClassName(relation.getColumnName())+"("+"session"+getJavaVarName(sessiontable)+");/n/n");
//				}
//			}
//		}
//		columnList= table.getColumnList();
//		pkId=autofullObj(columnList,null,sb,"a"+getJavaClassName(table.getTableName()));
//		sb.append("		    }/n/n");
//
//		sb.append("        putListFkValue("+getJavaVarName(table.getTableName())+"s,key,value);/n/n");
//
//		sb.append("        "+getJavaVarName(table.getTableName())+"Serv.batchCreate("+getJavaVarName(table.getTableName())+"s.toArray(new "+getJavaClassName(table.getTableName())+"[0]));/n/n");
//
//
//		sb.append("        return SUCCESS;/n/n");
//		sb.append("    }/n/n");
//
//
//
//		//生成delete方法
//		sb.append("    public String delete(){/n/n");
//		sb.append("        setFourScope();/n/n");
//
//		sb.append("        "+getJavaVarName(table.getTableName())+"Serv.delete("+getJavaVarName(table.getTableName())+");/n/n");
//		sb.append("        return SUCCESS;/n/n");
//		sb.append("    }/n/n");
//
//
//
//		//生成update方法
//		sb.append("    public String update(){/n/n");
//		sb.append("        setFourScope();/n/n");
//
//		sb.append("        "+getJavaVarName(table.getTableName())+"Serv.update("+getJavaVarName(table.getTableName())+");/n/n");
//		sb.append("        return SUCCESS;/n/n");
//		sb.append("    }/n/n");
//
//
//		//生成查询方法
//		sb.append("    public String search(){/n/n");
//		sb.append("        setFourScope();/n/n");
//		sb.append("        if("+getJavaVarName(table.getTableName())+"!=null){/n");
//		sb.append("            Map param=new HashMap();/n");
//		columnList= table.getColumnList();
//		for (CoreTableColumn coreTableColumn : columnList) {
//			//如果是日期类型，自动生成
//			if("int".equals(coreTableColumn.getColumnType())){
//				sb.append("            if("+getJavaVarName(table.getTableName())+".get"+getJavaClassName(coreTableColumn.getColumnName())+"()!=0){/n");
//				sb.append("           		 param.put(\""+getJavaVarName(coreTableColumn.getColumnName())+"\", "+getJavaVarName(table.getTableName())+".get"+getJavaClassName(coreTableColumn.getColumnName())+"());/n");
//				sb.append("            }/n");
//			}else{
//				sb.append("            if("+getJavaVarName(table.getTableName())+".get"+getJavaClassName(coreTableColumn.getColumnName())+"()!=null){/n");
//				sb.append("           		 param.put(\""+getJavaVarName(coreTableColumn.getColumnName())+"\", "+getJavaVarName(table.getTableName())+".get"+getJavaClassName(coreTableColumn.getColumnName())+"());/n");
//				sb.append("            }/n");
//			}
//		}
//		sb.append("             List<"+getJavaClassName(table.getTableName())+"> list=  "+getJavaVarName(table.getTableName())+"Serv.find"+getJavaClassName(table.getTableName())+"ByParam(param);/n");
//		sb.append("             request.setAttribute(\"list\", list);/n");
//		sb.append("       		return SUCCESS;/n");
//		sb.append("       }/n/n");
//		sb.append("       List<"+getJavaClassName(table.getTableName())+"> list=  "+getJavaVarName(table.getTableName())+"Serv.find"+getJavaClassName(table.getTableName())+"All();/n");
//		sb.append("       request.setAttribute(\"list\", list);/n");
//		sb.append("       return SUCCESS;/n");
//		sb.append("    }/n/n");
//
//		return sb.toString();
//
//	}
//
//	private static Object getFkImport(Table table, String string,
//									  String poPath) {
//
//		String str="//引入迭代关联关联的po/n";
//
//		//获取该table的关系vo对象
//		List<CoreTableColumnRelationVo> relationList= table.getCoreTableColumnRelationVo();
//		String poName;
//		poPath=poPath.substring(0,poPath.lastIndexOf(".po."));
//		poPath=poPath.substring(0,poPath.lastIndexOf(".")+1);
//		for (CoreTableColumnRelationVo coreTableColumnRelationVo : relationList) {
//			poName=poPath+getJavaClassName(coreTableColumnRelationVo.getTableNameTo()).toLowerCase()+".po."+getJavaClassName(coreTableColumnRelationVo.getTableNameTo());
//
//			//if(2!=coreTableColumnRelationVo.getType()){
//			str+="import "+poName+";/n";//TODO
//			//}
//		}
//
//		return str+"/n";
//	}
//
//	private static String autofullObj(List<CoreTableColumn> columnList,
//									  String tableName, StringBuffer sb,String objectName) {
//
//		if(objectName==null){
//			objectName=getJavaVarName(tableName);
//		}
//
//		String pkId = null;
//		for (CoreTableColumn coreTableColumn : columnList) {
//			//如果是日期类型，自动生成
//			if("date".equals(coreTableColumn.getColumnType())||"datetime".equals(coreTableColumn.getColumnType())||"timestamp".equals(coreTableColumn.getColumnType())){
//				sb.append("        "+objectName+".set"+getJavaClassName(coreTableColumn.getColumnName())+"(COMMON.getNowTime());/n/n");
//			}
//			//如果是主键，自动生成
//			if(1==coreTableColumn.getIspk()){
//
//				pkId=getJavaVarName(coreTableColumn.getColumnName());
//				sb.append("        String "+pkId+"=COMMON.uuid();/n");
//
//				sb.append("        if("+objectName+".get"+getJavaClassName(coreTableColumn.getColumnName())+"()==null||\"\".equals("+objectName+".get"+getJavaClassName(coreTableColumn.getColumnName())+"())){\n");
//				sb.append("              "+objectName+".set"+getJavaClassName(coreTableColumn.getColumnName())+"("+pkId+");/n/n");
//				sb.append("        }");
//			}
//		}		return pkId;
//	}

//	public static String createCombineActionFile(String username, CoreDb db,
//												 List<CoreSet> setList, CoreTableColumnServ coreTableColumnServ, CoreControlServ coreControlServ) {
//
//		if("".equals(username)){
//			username="zqh";
//		}
//		String actionFilePath=projectBasePath+"src/"+"com/"+username.toLowerCase()+"/"+db.getDbName().toLowerCase()+"/";
//		for (CoreSet coreSet : setList) {
//
//			String actionName=getJavaClassName(coreSet.getCoreSetName());
//
//			String actioinFileName=actionFilePath+actionName.toLowerCase()+"/action/"+actionName+"Action.java";
//			//List<CoreTableColumnRelationVo> relationList=coreTableColumnServ.findCoreTableColumnRelationByTableId(table.getTableId());
//			String content=getCombineActionContent(coreSet,actioinFileName,null,coreControlServ,coreTableColumnServ);
//
//			System.out.println("actioinFileName----"+actioinFileName);
//
//			FileUtils.writeFile(actioinFileName, content);
//		}
//		return null;
//	}

//	private static String getCombineActionContent(CoreSet coreSet,
//												  String actioinFileName, List<CoreTableColumnRelationVo> relationList, CoreControlServ coreControlServ, CoreTableColumnServ coreTableColumnServ) {
//
//		StringBuffer sb=new StringBuffer();
//
//		sb.append("package "+actioinFileName.substring(actioinFileName.indexOf("/src/")+5,actioinFileName.lastIndexOf("/")).replace("/", ".")+";/n/n");
//		sb.append("import org.springframework.stereotype.Component;/n/n");
//		sb.append("import javax.annotation.Resource;/n/n");
//		sb.append("import java.util.*;/n/n");
//		sb.append("import com.hframework.common.util.COMMON;/n/n");
//
//
//		sb.append("import com.hframework.common.ssh.action.AbstractActionSupport;/n/n");
//
//		sb.append("import "+actioinFileName.substring(actioinFileName.indexOf("/src/")+5,actioinFileName.lastIndexOf("/action/")).replace("/", ".")+".po."+getJavaClassName(coreSet.getCoreSetName())+";/n/n");
//		sb.append("import "+actioinFileName.substring(actioinFileName.indexOf("/src/")+5,actioinFileName.lastIndexOf("/action/")).replace("/", ".")+".service."+getJavaClassName(coreSet.getCoreSetName())+"Serv;/n/n");
//
//		//sb.append(getFkServImport(table,null,actioinFileName.substring(actioinFileName.indexOf("/src/")+5,actioinFileName.lastIndexOf("/action/")).replace("/", ".")+".po."+getJavaClassName(table.getTableName())));
//
//		//sb.append(getFkImport(table,null,actioinFileName.substring(actioinFileName.indexOf("/src/")+5,actioinFileName.lastIndexOf("/action/")).replace("/", ".")+".po."+getJavaClassName(table.getTableName())));
//
//		sb.append("@Component/n/n");
//		sb.append("public class "+getJavaClassName(coreSet.getCoreSetName())+"Action  extends AbstractActionSupport{/n/n");
//
//		//注入service层
//		sb.append(CreatorUtil.injectBean(coreSet.getCoreSetName(), "Serv"));
//
//
//		//注册vo对象
//		sb.append(getSetGetMethod(getJavaClassName(coreSet.getCoreSetName()),getJavaVarName(coreSet.getCoreSetName()),0));
//		//sb.append(getSetGetMethod(getJavaClassName(table.getTableName())+"[]",getJavaVarName(table.getTableName())+"s",0));
//		sb.append(getSetGetMethod("List<"+getJavaClassName(coreSet.getCoreSetName())+">",getJavaVarName(coreSet.getCoreSetName())+"s",0));
//
//
//
//		//生成list方法
//		sb.append("    public String list(){/n/n");
//		sb.append("        setFourScope();/n/n");
//		List<CoreControl> controlList=coreControlServ.findCoreControlByControlNameAndControlQuote("list",coreSet.getCoreSetName());
//		List<CoreControl> sessionList=coreControlServ.findCoreControlByControlName("session");
//		String sessiontable="";
//		if(controlList.size()>0){
//			sessiontable=sessionList.get(0).getControlValue();
//		}
//
//		if(controlList.size()>0){
//
//			String[] values=controlList.get(0).getControlValue().split(",");
//
//			sb.append("        Map<String, String> params=new HashMap<String, String>();");
//			for (String s : values) {
//				if(!"".equals(s)){
//					sb.append("        String "+getJavaVarName(s)+"=request.getParameter(\""+getJavaVarName(s)+"\");/n");
//					sb.append("        params.put(\""+getJavaVarName(s)+"\","+getJavaVarName(s)+");/n");
//				}
//			}
//			sb.append("        List<"+getJavaClassName(coreSet.getCoreSetName())+"> list="+getJavaVarName(coreSet.getCoreSetName())+"Serv.find"+getJavaClassName(coreSet.getCoreSetName())+"ByParams(params);/n/n");
//		}else if(!"".equals(sessiontable)){
//			sb.append("        "+getJavaClassName(sessiontable)+" session"+getJavaVarName(sessiontable)+"=("+getJavaClassName(sessiontable)+")session.getAttribute(session"+getJavaVarName(sessiontable)+");");
//			sb.append("        List<"+getJavaClassName(coreSet.getCoreSetName())+"> list="+getJavaVarName(coreSet.getCoreSetName())+"Serv.find"+getJavaClassName(coreSet.getCoreSetName())+"ByXXX(xxx);/n/n");
//		}else{
//			sb.append("        List<"+getJavaClassName(coreSet.getCoreSetName())+"> list="+getJavaVarName(coreSet.getCoreSetName())+"Serv.find"+getJavaClassName(coreSet.getCoreSetName())+"All();/n/n");
//		}
//		sb.append("        request.setAttribute(\"list\", list);/n/n");
//		sb.append("        return SUCCESS;/n/n");
//		sb.append("    }/n/n");
//
//		//生成create方法
//		sb.append("    public String create(){/n/n");
//		sb.append("        setFourScope();/n/n");
//		if(!"".equals(sessiontable)){
//			sb.append("        "+getJavaClassName(sessiontable)+" session"+getJavaVarName(sessiontable)+"=("+getJavaClassName(sessiontable)+")session.getAttribute(session"+getJavaVarName(sessiontable)+");");
//
//		}
//
//		List<Column> columnList=coreSet.getCoreColumnList();
//		String pkId=autofullObj(columnList,coreSet.getCoreSetName(),sb,null);
//		sb.append("        "+getJavaVarName(coreSet.getCoreSetName())+"Serv.create("+getJavaVarName(coreSet.getCoreSetName())+");/n/n");
//
//
//		sb.append("        return SUCCESS;/n/n");
//		sb.append("    }/n/n");
//
//		//生成create方法
//		sb.append("    public String batchCreate(){/n/n");
//		sb.append("        setFourScope();/n/n");
//		sb.append("        putRightList("+getJavaVarName(coreSet.getCoreSetName())+"s,request);/n/n");
//
//
//
//		sb.append("		    for ("+getJavaClassName(coreSet.getCoreSetName())+" a"+getJavaClassName(coreSet.getCoreSetName())+" : "+getJavaVarName(coreSet.getCoreSetName())+"s) {/n");
//
//		if(!"".equals(sessiontable)){
//			sb.append("        		"+getJavaClassName(sessiontable)+" session"+getJavaVarName(sessiontable)+"=("+getJavaClassName(sessiontable)+")session.getAttribute(session"+getJavaVarName(sessiontable)+");");
//			for(CoreTableColumnRelationVo relation:relationList){
//				if(sessiontable.equals(relation.getTableNameTo())){
//					sb.append("        		a"+getJavaClassName(coreSet.getCoreSetName())+".set"+getJavaClassName(relation.getColumnName())+"("+"session"+getJavaVarName(sessiontable)+");/n/n");
//				}
//			}
//		}
//		columnList=coreSet.getCoreColumnList();
//		pkId=autofullObj(columnList,null,sb,"a"+getJavaClassName(coreSet.getCoreSetName()));
//		sb.append("		    }/n/n");
//
//		sb.append("        "+getJavaVarName(coreSet.getCoreSetName())+"Serv.batchCreate("+getJavaVarName(coreSet.getCoreSetName())+"s.toArray(new "+getJavaClassName(coreSet.getCoreSetName())+"[0]));/n/n");
//
//
//		sb.append("        return SUCCESS;/n/n");
//		sb.append("    }/n/n");
//
//
//
//		//生成delete方法
//		sb.append("    public String delete(){/n/n");
//		sb.append("        setFourScope();/n/n");
//
//		sb.append("        "+getJavaVarName(coreSet.getCoreSetName())+"Serv.delete("+getJavaVarName(coreSet.getCoreSetName())+");/n/n");
//		sb.append("        return SUCCESS;/n/n");
//		sb.append("    }/n/n");
//
//
//
//		//生成update方法
//		sb.append("    public String update(){/n/n");
//		sb.append("        setFourScope();/n/n");
//
//		sb.append("        "+getJavaVarName(coreSet.getCoreSetName())+"Serv.update("+getJavaVarName(coreSet.getCoreSetName())+");/n/n");
//		sb.append("        return SUCCESS;/n/n");
//		sb.append("    }/n/n");
//
//
//		//生成查询方法
//		sb.append("    public String search(){/n/n");
//		sb.append("        setFourScope();/n/n");
//		sb.append("        if("+getJavaVarName(coreSet.getCoreSetName())+"!=null){/n");
//		sb.append("            Map param=new HashMap();/n");
//		columnList=coreSet.getCoreColumnList();
//		for (Column coreTableColumn : columnList) {
//			//如果是日期类型，自动生成
//			if("int".equals(coreTableColumn.getColumnType())){
//				sb.append("            if("+getJavaVarName(coreSet.getCoreSetName())+".get"+getJavaClassName(coreTableColumn.getColumnName())+"()!=0){/n");
//				sb.append("           		 param.put(\""+getJavaVarName(coreTableColumn.getColumnName())+"\", "+getJavaVarName(coreSet.getCoreSetName())+".get"+getJavaClassName(coreTableColumn.getColumnName())+"());/n");
//				sb.append("            }/n");
//			}else{
//				sb.append("            if("+getJavaVarName(coreSet.getCoreSetName())+".get"+getJavaClassName(coreTableColumn.getColumnName())+"()!=null){/n");
//				sb.append("           		 param.put(\""+getJavaVarName(coreTableColumn.getColumnName())+"\", "+getJavaVarName(coreSet.getCoreSetName())+".get"+getJavaClassName(coreTableColumn.getColumnName())+"());/n");
//				sb.append("            }/n");
//			}
//		}
//		sb.append("             List<"+getJavaClassName(coreSet.getCoreSetName())+"> list=  "+getJavaVarName(coreSet.getCoreSetName())+"Serv.find"+getJavaClassName(coreSet.getCoreSetName())+"ByParam(param);/n");
//		sb.append("             request.setAttribute(\"list\", list);/n");
//		sb.append("       		return SUCCESS;/n");
//		sb.append("       }/n/n");
//		sb.append("       List<"+getJavaClassName(coreSet.getCoreSetName())+"> list=  "+getJavaVarName(coreSet.getCoreSetName())+"Serv.find"+getJavaClassName(coreSet.getCoreSetName())+"All();/n");
//		sb.append("       request.setAttribute(\"list\", list);/n");
//		sb.append("       return SUCCESS;/n");
//		sb.append("    }/n/n");
//		sb.append("}/n/n");
//		return sb.toString();
//	}

//	private static String getSetGetMethod(String varType, String varName, int isList) {
//
//		StringBuffer sb=new StringBuffer();
//		//申明变量
//		if(isList==0){
//			sb.append("    private "+varType+" "+varName+";/n/n");
//		}else{
//			sb.append("    private List<"+varType+"> "+varName+"s;/n/n");
//		}
//
//		//获取get方法
//		if(isList==0){
//			sb.append("    public "+varType+" get"+varName.substring(0,1).toUpperCase()+varName.substring(1)+"(){/n/n");
//			sb.append("        return "+varName+";/n/n");
//			sb.append("    }/n/n");
//		}else{
//			sb.append("    public List<"+varType+"> get"+varType+"s(){/n/n");
//			sb.append("        return "+varName+"s;/n/n");
//			sb.append("    }/n/n");
//		}
//
//
//		//获得set方法
//		if(isList==0){
//			sb.append("    public void set"+varName.substring(0,1).toUpperCase()+varName.substring(1)+"("+varType+" "+varName+"){/n/n");
//			sb.append("        this."+varName+"="+varName+";/n/n");
//			sb.append("    }/n/n");
//		}else{
//
//			sb.append("    public void set"+varType+"s(List<"+varType+"> "+varName+"s){/n/n");
//			sb.append("        this."+varName+"s="+varName+"s;/n/n");
//			sb.append("    }/n/n");
//		}
//
//		return sb.toString();
//
//	}
}
