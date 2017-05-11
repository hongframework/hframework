package com.hframework.generator.web.service;

import com.hframework.common.util.JavaUtil;
import com.hframework.common.util.StringUtils;
import com.hframework.common.util.message.VelocityUtil;
import com.hframework.beans.class0.Table;
import com.hframework.generator.util.CreatorUtil;
import com.hframework.generator.web.AbstractGenerator;

import java.util.HashMap;
import java.util.Map;


/**
 *  @author zhangqh6
 */
public class ServiceInterfaceGenerator extends AbstractGenerator {

	public ServiceInterfaceGenerator(String companyName, String projectName, String moduleName, Table table) throws Exception {
		super(companyName, projectName, moduleName, table);
		setEditClass(serviceClass);
	}
	
	@Override
	public void setImportClass() {
		editClass.addImportClass("java.util.*");
		editClass.addImportClass(poClass.getClassPath());
		editClass.addImportClass(poExampleClass.getClassPath());

		editClass.setType("interface");
	}

	@Override
	public void setField() {
	}

	@Override
	public void createMethod() {
		Map contentMap=new HashMap();
		contentMap.put("ClassName", JavaUtil.getJavaClassName(table.getTableName()));
		contentMap.put("VarName", JavaUtil.getJavaVarName(table.getTableName()));
		contentMap.put("EntityName", table.getTableDesc());
		if(StringUtils.isNotBlank(table.getParentId())) {
			contentMap.put("ParentIdPropertyClassName", JavaUtil.getJavaClassName(table.getParentId()));
		}
		String methodStr = VelocityUtil.produceTemplateContent(
				"com/hframework/generator/vm/service_interface_method_content.vm", contentMap);

		editClass.setExtMethodStr(methodStr);
	}
}
