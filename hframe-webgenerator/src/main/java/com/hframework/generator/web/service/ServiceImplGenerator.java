package com.hframework.generator.web.service;

import com.hframework.common.util.JavaUtil;
import com.hframework.common.util.StringUtils;
import com.hframework.common.util.message.VelocityUtil;
import com.hframework.beans.class0.Field;
import com.hframework.beans.class0.Table;
import com.hframework.generator.util.CreatorUtil;
import com.hframework.generator.web.AbstractGenerator;

import java.util.HashMap;
import java.util.Map;

/**
 *  @author zhangqh6
 */
public class ServiceImplGenerator extends AbstractGenerator {

	public ServiceImplGenerator(String companyName, String projectName, String moduleName, Table table) throws Exception {
		super(companyName, projectName, moduleName, table);
		setEditClass(serviceImplClass);
	}
	
	@Override
	public void setImportClass() {

		editClass.addImportClass("java.util.*");
		editClass.addImportClass("org.springframework.stereotype.Service");
		editClass.addImportClass("javax.annotation.Resource");
		editClass.addImportClass("com.google.common.collect.Lists");
		editClass.addImportClass("com.google.common.collect.Lists");
		editClass.addImportClass("com.hframework.common.util.ExampleUtils");

		editClass.addImportClass(poClass.getClassPath());
		editClass.addImportClass(poExampleClass.getClassPath());
		editClass.addImportClass(mapper.getClassPath());
		editClass.addImportClass(serviceClass.getClassPath());

		editClass.setAnnotation("@Service(\"" + StringUtils.lowerCaseFirstChar(serviceClass.getClassName()) + "\")");
		editClass.addInterface(serviceClass.getClassName());
	}

	@Override
	public void setField() {
		//注入对应的DAO
		editClass.addField(new Field(mapper.getClassName()).addFieldAnno("@Resource"));
	}

	@Override
	public void createMethod() {
		Map contentMap=new HashMap();
		contentMap.put("ClassName", JavaUtil.getJavaClassName(table.getTableName()));
		contentMap.put("VarName", JavaUtil.getJavaVarName(table.getTableName()));
		contentMap.put("EntityName", table.getTableDesc());
		contentMap.put("KeyProperty", JavaUtil.getJavaClassName(table.getDbId()));
		if(StringUtils.isNotBlank(table.getParentId())) {
			contentMap.put("ParentIdPropertyClassName", JavaUtil.getJavaClassName(table.getParentId()));
		}

		String methodStr = VelocityUtil.produceTemplateContent(
				"com/hframework/generator/vm/service_impl_method_content.vm", contentMap);

		editClass.setExtMethodStr(methodStr);
	}
}
