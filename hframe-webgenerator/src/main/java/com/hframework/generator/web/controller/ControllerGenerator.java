package com.hframework.generator.web.controller;

import com.hframework.common.util.JavaUtil;
import com.hframework.common.util.StringUtils;
import com.hframework.common.util.message.VelocityUtil;
import com.hframework.beans.class0.Field;
import com.hframework.beans.class0.Table;
import com.hframework.generator.util.CreatorUtil;
import com.hframework.generator.web.AbstractGenerator;

import java.util.HashMap;
import java.util.Map;

public class ControllerGenerator extends AbstractGenerator {

	public ControllerGenerator(String companyName, String projectName, String moduleName, Table table) throws Exception {
		super(companyName, projectName, moduleName, table);
		setEditClass(controller);
	}

	/**
	 * 设置引入包信息
	 */
	@Override
	public void setImportClass() {
		editClass.addImportClass("com.hframework.common.util.ExampleUtils");
		editClass.addImportClass("com.hframe.controller.bean.ResultMessage");
		editClass.addImportClass("org.springframework.stereotype.Controller");
		editClass.addImportClass("org.springframework.web.bind.annotation.ModelAttribute");
		editClass.addImportClass("org.springframework.web.bind.annotation.RequestMapping");
		editClass.addImportClass("org.springframework.web.bind.annotation.ResponseBody");
		editClass.addImportClass("org.springframework.web.servlet.ModelAndView");
		editClass.addImportClass("javax.annotation.Resource");
		editClass.addImportClass("java.util.*");

		editClass.addImportClass(poClass.getClassPath());
		editClass.addImportClass(poExampleClass.getClassPath());
		editClass.addImportClass(serviceClass.getClassPath());

		editClass.addAnnotation("@Controller");
		editClass.addAnnotation("@RequestMapping(value = \"" +
				(StringUtils.isBlank(moduleName)?"":"/"+ moduleName) + "/" + JavaUtil.getJavaVarName(table.getTableName()) + "\")");
	}

	/**
	 * 设置类字段信息
	 */
	@Override
	public void setField() {
		editClass.addField(new Field(serviceClass.getClassName()).addFieldAnno("@Resource"));
	}

	/**
	 * 创建方法
	 */
	@Override
	public void createMethod() {
		Map contentMap=new HashMap();
		contentMap.put("ClassName", JavaUtil.getJavaClassName(table.getTableName()));
		contentMap.put("VarName", JavaUtil.getJavaVarName(table.getTableName()));
		contentMap.put("EntityName", table.getTableDesc());
		contentMap.put("SeviceClassName", JavaUtil.getJavaClassName(serviceClass.getClassName()));
		contentMap.put("SeviceVarName", StringUtils.lowerCaseFirstChar(serviceClass.getClassName()));
		contentMap.put("PoExampleClassName", poExampleClass.getClassName());

		contentMap.put("ProjectName", projectName);
		contentMap.put("ModuleName", moduleName);

		String methodStr = VelocityUtil.produceTemplateContent(
				"com/hframework/generator/vm/controller_method_content.vm", contentMap);

		editClass.setExtMethodStr(methodStr);
	}
}
