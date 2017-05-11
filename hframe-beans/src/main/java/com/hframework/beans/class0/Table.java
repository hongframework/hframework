package com.hframework.beans.class0;

import java.util.List;


/**
 * Table entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class Table implements java.io.Serializable {

	// Fields

	private String tableId;
	private String tableName;
	private String tableDesc;
	private String dbId;

	private List<Column> columnList;
//	private List<CoreTableColumnRelationVo> CoreTableColumnRelationVo;


	private String parentId;

	// Constructors

	/** default constructor */
	public Table() {
	}

	/** minimal constructor */
	public Table(String tableId, String tableName) {
		this.tableId = tableId;
		this.tableName = tableName;
	}

	/** full constructor */
	public Table(String tableId, String tableName, String tableDesc,
				 String dbId) {
		this.tableId = tableId;
		this.tableName = tableName;
		this.tableDesc = tableDesc;
		this.dbId = dbId;
	}

	// Property accessors

	public String getTableId() {
		return this.tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableDesc() {
		return this.tableDesc;
	}

	public void setTableDesc(String tableDesc) {
		this.tableDesc = tableDesc;
	}


	public String getDbId() {
		return this.dbId;
	}

	public void setDbId(String dbId) {
		this.dbId = dbId;
	}

	public List<Column> getColumnList() {
		return columnList;
	}

	public void setColumnList(List<Column> columnList) {
		this.columnList = columnList;
	}

//	public List<CoreTableColumnRelationVo> getCoreTableColumnRelationVo() {
//		return CoreTableColumnRelationVo;
//	}
//
//	public void setCoreTableColumnRelationVo(
//			List<CoreTableColumnRelationVo> coreTableColumnRelationVo) {
//		CoreTableColumnRelationVo = coreTableColumnRelationVo;
//	}


	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
}