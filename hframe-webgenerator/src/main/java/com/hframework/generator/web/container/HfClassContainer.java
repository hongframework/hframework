package com.hframework.generator.web.container;

import java.util.HashMap;
import java.util.Map;

/**
 * 模型数据容器
 */
public class HfClassContainer {


	private String programCode;
	private String programName;
	private Map<String, HfClassDescriptor> classs = new HashMap<String, HfClassDescriptor>();

	public HfClassDescriptor getClassInfo(String className) {

		if(!classs.containsKey(className)) {
			classs.put(className, new HfClassDescriptor());
			HfClassDescriptor hfClassDescriptor = classs.get(className);
			hfClassDescriptor.setClassName(className);
		}

		return classs.get(className);
	}


	public Map<String, HfClassDescriptor> getClasss() {
		return classs;
	}

	public void setClasss(Map<String, HfClassDescriptor> classs) {
		this.classs = classs;
	}

	public String getProgramCode() {
		return programCode;
	}

	public void setProgramCode(String programCode) {
		this.programCode = programCode;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}



	public class HfClassDescriptor{
		private String moduleCode;
		private String entityCode;

		private String className;
		private String classDesc;

		private Map<String, HfClassFieldDescriptor> fields = new HashMap<String, HfClassFieldDescriptor>();

		public void addField( String entityAttrCode, String fieldName, Class fieldType,String fieldDesc) {
			HfClassFieldDescriptor fieldDescriptor = new HfClassFieldDescriptor();
			fieldDescriptor.setEntityAttrCode(entityAttrCode);
			fieldDescriptor.setFieldName(fieldName);
			fieldDescriptor.setFieldType(fieldType);
			fieldDescriptor.setFieldDesc(fieldDesc);
			fields.put(fieldName, fieldDescriptor);
		}

		public String getModuleCode() {
			return moduleCode;
		}

		public void setModuleCode(String moduleCode) {
			this.moduleCode = moduleCode;
		}

		public String getEntityCode() {
			return entityCode;
		}

		public void setEntityCode(String entityCode) {
			this.entityCode = entityCode;
		}

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public String getClassDesc() {
			return classDesc;
		}

		public void setClassDesc(String classDesc) {
			this.classDesc = classDesc;
		}

		public Map<String, HfClassFieldDescriptor> getFields() {
			return fields;
		}

		public void setFields(Map<String, HfClassFieldDescriptor> fields) {
			this.fields = fields;
		}
	}

	public class HfClassFieldDescriptor{

		private String entityAttrCode;
		private String fieldName;
		private Class fieldType;
		private String fieldDesc;

		public String getEntityAttrCode() {
			return entityAttrCode;
		}

		public void setEntityAttrCode(String entityAttrCode) {
			this.entityAttrCode = entityAttrCode;
		}

		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public Class getFieldType() {
			return fieldType;
		}

		public void setFieldType(Class fieldType) {
			this.fieldType = fieldType;
		}

		public String getFieldDesc() {
			return fieldDesc;
		}

		public void setFieldDesc(String fieldDesc) {
			this.fieldDesc = fieldDesc;
		}
	}

	public static HfClassContainer getInstance(String programCode, String programName) {
		HfClassContainer hfClassContainer = new HfClassContainer();
		hfClassContainer.setProgramCode(programCode);
		hfClassContainer.setProgramName(programName);
		return hfClassContainer;
	}

}
