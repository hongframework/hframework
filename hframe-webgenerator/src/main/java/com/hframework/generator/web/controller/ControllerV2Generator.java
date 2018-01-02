package com.hframework.generator.web.controller;

import com.hframework.beans.class0.Field;
import com.hframework.beans.class0.Table;
import com.hframework.common.util.JavaUtil;
import com.hframework.common.util.StringUtils;
import com.hframework.common.util.message.VelocityUtil;
import com.hframework.generator.util.CreatorUtil;
import com.hframework.generator.web.AbstractGenerator;

import java.util.HashMap;
import java.util.Map;

public class ControllerV2Generator extends AbstractGenerator {

	public ControllerV2Generator(String companyName, String projectName, String moduleName, Table table) throws Exception {
		super(companyName, projectName, moduleName, table);
		setEditClass(controller);
	}

	/**
	 * 设置引入包信息
	 */
	@Override
	public void setImportClass() {

		editClass.addImportClass("com.hframework.beans.controller.Pagination");
		editClass.addImportClass("com.hframework.beans.controller.ResultCode");
		editClass.addImportClass("com.hframework.beans.controller.ResultData");
		editClass.addImportClass("com.hframework.common.util.ExampleUtils");
		editClass.addImportClass("com.hframework.beans.exceptions.BusinessException");


		editClass.addImportClass("org.slf4j.Logger");
		editClass.addImportClass("org.slf4j.LoggerFactory");

		editClass.addImportClass("org.springframework.stereotype.Controller");
		editClass.addImportClass("org.springframework.web.bind.annotation.InitBinder");
		editClass.addImportClass("org.springframework.web.bind.annotation.ModelAttribute");
		editClass.addImportClass("org.springframework.web.bind.annotation.RequestMapping");
		editClass.addImportClass("org.springframework.web.bind.annotation.ResponseBody");
		editClass.addImportClass("org.springframework.web.bind.annotation.RequestBody");
		editClass.addImportClass("org.springframework.web.servlet.ModelAndView");
		editClass.addImportClass("javax.annotation.Resource");
		editClass.addImportClass("java.util.*");

		editClass.addImportClass("javax.servlet.http.HttpServletRequest");
		editClass.addImportClass("org.springframework.web.bind.ServletRequestDataBinder");
		editClass.addImportClass("java.text.DateFormat");
		editClass.addImportClass("java.text.SimpleDateFormat");
		editClass.addImportClass("org.springframework.beans.propertyeditors.CustomDateEditor");
		editClass.addImportClass("com.hframework.web.ControllerHelper");



		editClass.addImportClass(poClass.getClassPath());
		editClass.addImportClass(poExampleClass.getClassPath());
		editClass.addImportClass(serviceClass.getClassPath());

		editClass.setUseLogger(true);

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
		if(StringUtils.isNotBlank(table.getParentId())) {
			contentMap.put("ParentIdPropertyClassName", JavaUtil.getJavaClassName(table.getParentId()));
		}
		contentMap.put("KeyProperty", JavaUtil.getJavaClassName(table.getDbId()));
		contentMap.put("KeyPropertyVar", JavaUtil.getJavaVarName(table.getDbId()));
		contentMap.put("EntityName", table.getTableDesc());
		contentMap.put("SeviceClassName", JavaUtil.getJavaClassName(serviceClass.getClassName()));
		contentMap.put("SeviceVarName", StringUtils.lowerCaseFirstChar(serviceClass.getClassName()));
		contentMap.put("PoExampleClassName", poExampleClass.getClassName());

		contentMap.put("ProjectName", projectName);
		contentMap.put("ModuleName", moduleName);

		String methodStr = VelocityUtil.produceTemplateContent(
				"com/hframework/generator/vm/controller_method_content_V2.0.vm", contentMap);

		editClass.setExtMethodStr(methodStr);
	}
}
