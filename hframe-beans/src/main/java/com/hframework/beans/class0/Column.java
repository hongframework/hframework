package com.hframework.beans.class0;

//import com.hframe.vo.CoreTableColumnRelationVo;

/**
 * Column entity.
 *
 * @author MyEclipse Persistence Tools
 */

public class Column implements java.io.Serializable {

	// Fields

	private String id;
	private String columnName;
	private String columnType;
	private String showTypeId;//注意，这里可以是多个showTypeID通过“，”来分割的，比如出生年月 年-月-日
	private Integer columnSize;
	private Integer ispk;
	private Integer nullable;
	private Integer showable;
	private String showName;
	private String tableId;
	private String description;
	private boolean isFk;
	private String fkTableName;


	private Float priv;

	//********************vo  part**************************

//	private CoreShowType coreShowType;
//	private List<CoreShowType> coreShowTypeList;
//
//
//	private List<CoreTableColumnRelationVo> relations;

	// Constructors

	/** default constructor */
	public Column() {
	}

	/** minimal constructor */
	public Column(String id, String columnName, String columnType,
				  String tableId) {
		this.id = id;
		this.columnName = columnName;
		this.columnType = columnType;
		this.tableId = tableId;
	}

	/** full constructor */
	public Column(String id, String columnName, String columnType,
				  Integer columnSize, Integer ispk, Integer nullable,
				  Integer showable, String showName, String tableId,
				  String description) {
		this.id = id;
		this.columnName = columnName;
		this.columnType = columnType;
		this.columnSize = columnSize;
		this.ispk = ispk;
		this.nullable = nullable;
		this.showable = showable;
		this.showName = showName;
		this.tableId = tableId;
		this.description = description;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getColumnName() {
		return this.columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnType() {
		return this.columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

	public Integer getColumnSize() {
		return this.columnSize;
	}

	public void setColumnSize(Integer columnSize) {
		this.columnSize = columnSize;
	}

	public Integer getIspk() {
		return this.ispk;
	}

	public void setIspk(Integer ispk) {
		this.ispk = ispk;
	}

	public Integer getNullable() {
		return this.nullable;
	}

	public void setNullable(Integer nullable) {
		this.nullable = nullable;
	}

	public Integer getShowable() {
		return this.showable;
	}

	public void setShowable(Integer showable) {
		this.showable = showable;
	}

	public String getShowName() {
		return this.showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public String getTableId() {
		return this.tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

//	public List<CoreTableColumnRelationVo> getRelations() {
//		return relations;
//	}
//
//	public void setRelations(List<CoreTableColumnRelationVo> relations) {
//		this.relations = relations;
//	}
//
//	public String getShowTypeId() {
//		return showTypeId;
//	}
//
//	public CoreShowType getCoreShowType() {
//		return coreShowType;
//	}
//
//	public void setCoreShowType(CoreShowType coreShowType) {
//		this.coreShowType = coreShowType;
//	}
//
//	public void setShowTypeId(String showTypeId) {
//		this.showTypeId = showTypeId;
//	}
//
//	public List<CoreShowType> getCoreShowTypeList() {
//		return coreShowTypeList;
//	}
//
//	public void setCoreShowTypeList(List<CoreShowType> coreShowTypeList) {
//		this.coreShowTypeList = coreShowTypeList;
//	}

	public Float getPriv() {
		return priv;
	}

	public void setPriv(Float priv) {
		this.priv = priv;
	}

	public String getShowTypeId() {
		return showTypeId;
	}

	public void setShowTypeId(String showTypeId) {
		this.showTypeId = showTypeId;
	}

	public boolean isFk() {
		return isFk;
	}

	public void setIsFk(boolean isFk) {
		this.isFk = isFk;
	}

	public String getFkTableName() {
		return fkTableName;
	}

	public void setFkTableName(String fkTableName) {
		this.fkTableName = fkTableName;
	}
}