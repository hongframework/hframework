package com.hframework.generator.web.container;

import com.hframework.common.util.CommonUtils;
import com.hframework.generator.web.container.bean.*;
import com.hframework.generator.web.container.bean.Enum;

import java.math.BigDecimal;
import java.util.*;

/**
 * 模型数据容器
 */
public class HfModelContainer {

	public static final int TYPE_ADD = 1;
	public static final int TYPE_MOD = 2;
	public static final int TYPE_DEL = 3;

	//项目信息
	private Program program;
	//模块信息
	private Map<Long, Module> moduleMap;

	//实体信息<entityName,HfmdEntity>
	private Map<String,Entity> entityMap;
	//实体属性信息<entityCode.entityAttrCode,HfmdEntityAttr>
	private Map<String,EntityAttr> entityAttrMap;

	private Map<EntityAttr, AttrChangeType> entityAttrChangeTypeMap;
	private Map<String, String> relEntityAttr2AttrMapper;

	//实体属计数表
	private Map<String,BigDecimal> entityAttrCountMap;
	//实体关系信息
	private Map<Long,List<EntityRel>> entityRelMap;

	//枚举类信息
	private Map<Long,EnumClass> enumClassMap;
	private Map<String,EnumClass> enumClassCodeMap;
	//枚举信息
	private Map<Long,Enum> enumMap;

	//数据集信息
	private Map<String, DataSet> dataSetMap;
	//数据列信息
	private Map<String, List<DataField>> dataFieldListMap;

	//1:新增结构 2：修改结构 3：删除结构
	private int containerType = 0;

	public Entity getEntity(String entityName) {
		if(entityMap == null) {
			entityMap = new LinkedHashMap<String, Entity>();
		}
		if(!entityMap.containsKey(entityName)) {
			Entity entity = new Entity();
			entity.setHfmdEntityCode(entityName.trim());
			entity.setHfmdEntityId(CommonUtils.uuidL());
			entityMap.put(entityName, entity);
		}
		return entityMap.get(entityName.trim());
	}

	public void removeEntity(String entityName) {
		if(entityMap != null) {
			entityMap.remove(entityName);
		}
	}

	public EntityAttr getEntityAttr(String entityName, String entityAttrName) {
		if(entityAttrMap == null) {
			entityAttrMap = new HashMap<String, EntityAttr>();
		}

		if(entityAttrCountMap == null) {
			entityAttrCountMap = new HashMap<String, BigDecimal>();
		}


		if(!entityAttrCountMap.containsKey(entityName)) {
			entityAttrCountMap.put(entityName,new BigDecimal("0.0"));
		}

		if(!entityAttrMap.containsKey(entityName + "." + entityAttrName)) {
			EntityAttr entity = new EntityAttr();
			entity.setHfmdEntityAttrCode(entityAttrName);
			entity.setHfmdEntityAttrId(CommonUtils.uuidL());
			entityAttrMap.put(entityName + "." + entityAttrName, entity);

			BigDecimal bigDecimal = entityAttrCountMap.get(entityName);
			entityAttrCountMap.put(entityName,bigDecimal.add(new BigDecimal("1.0")));

			entity.setPri(entityAttrCountMap.get(entityName));
		}
		return entityAttrMap.get(entityName + "."+ entityAttrName);
	}

	public void removeEntityAttr(String entityAttrName) {
		if(entityAttrMap != null) {
			entityAttrMap.remove(entityAttrName);
		}
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

	public Map<Long, Module> getModuleMap() {
		if(moduleMap == null) {
			moduleMap = new HashMap<Long, Module>();
		}
		return moduleMap;
	}

	public void setModuleMap(Map<Long, Module> moduleMap) {
		this.moduleMap = moduleMap;
	}

	public Map<String, Entity> getEntityMap() {
		return entityMap;
	}

	public void setEntityMap(Map<String, Entity> entityMap) {
		this.entityMap = entityMap;
	}

	public Map<String, EntityAttr> getEntityAttrMap() {
		return entityAttrMap;
	}

	public void setEntityAttrMap(Map<String, EntityAttr> entityAttrMap) {
		this.entityAttrMap = entityAttrMap;
	}

	public Map<Long, EnumClass> getEnumClassMap() {
		return enumClassMap;
	}

	public Map<String, EnumClass> getEnumClassCodeMap() {
		return enumClassCodeMap;
	}

	public void setEnumClassCodeMap(Map<String, EnumClass> enumClassCodeMap) {
		this.enumClassCodeMap = enumClassCodeMap;
	}

	public void setEnumClassMap(Map<Long, EnumClass> enumClassMap) {
		this.enumClassMap = enumClassMap;
	}

	public Map<Long, Enum> getEnumMap() {
		return enumMap;
	}

	public void setEnumMap(Map<Long, Enum> enumMap) {
		this.enumMap = enumMap;
	}


	public Map<Long, List<EntityRel>> getEntityRelMap() {
		return entityRelMap;
	}

	public void setEntityRelMap(Map<Long, List<EntityRel>> entityRelMap) {
		this.entityRelMap = entityRelMap;
	}

	public Map<String, BigDecimal> getEntityAttrCountMap() {
		return entityAttrCountMap;
	}

	public void setEntityAttrCountMap(Map<String, BigDecimal> entityAttrCountMap) {
		this.entityAttrCountMap = entityAttrCountMap;
	}

	public Map<String, DataSet> getDataSetMap() {
		return dataSetMap;
	}

	public void setDataSetMap(Map<String, DataSet> dataSetMap) {
		this.dataSetMap = dataSetMap;
	}

	public Map<String, List<DataField>> getDataFieldListMap() {
		return dataFieldListMap;
	}

	public void setDataFieldListMap(Map<String, List<DataField>> dataFieldListMap) {
		this.dataFieldListMap = dataFieldListMap;
	}

	public int getContainerType() {
		return containerType;
	}

	public void setContainerType(int containerType) {
		this.containerType = containerType;
	}

	public Map<EntityAttr, AttrChangeType> getEntityAttrChangeTypeMap() {
		if(entityAttrChangeTypeMap == null) {
			entityAttrChangeTypeMap = new HashMap<EntityAttr, AttrChangeType>();
		}
		return entityAttrChangeTypeMap;
	}

	public void setEntityAttrChangeTypeMap(Map<EntityAttr, AttrChangeType> entityAttrChangeTypeMap) {
		this.entityAttrChangeTypeMap = entityAttrChangeTypeMap;
	}

	public Map<String, String> getRelEntityAttr2AttrMapper() {
		if(relEntityAttr2AttrMapper == null) {
			relEntityAttr2AttrMapper = new HashMap<String, String>();
		}
		return relEntityAttr2AttrMapper;
	}

	public void setRelEntityAttr2AttrMapper(Map<String, String> relEntityAttr2AttrMapper) {
		this.relEntityAttr2AttrMapper = relEntityAttr2AttrMapper;
	}

	public enum AttrChangeType{
		FIELD,FK,FULL;

		public boolean containField() {
			return this.equals(FIELD) || this.equals(FULL);
		}
		public boolean containFk() {
			return this.equals(FK) || this.equals(FULL);
		}

	}
}
