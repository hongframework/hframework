package com.hframework.beans.tagbean;

import java.util.ArrayList;
import java.util.List;

/**这个主要正对于数据库中的一列
 * 比如数据库表user 有十个字段，那么就会生成十个Column对象
 * 
 * @author zqh
 *
 */
public class Column {
	
	private String id;
//	private int id;

	
	private String name;
	private String javaVarName;
	private String displayName;
	private ShowType showType=new ShowType();
	private String defaultValue;
	private String value;
	private boolean ediable;
	
	private String tableName;
	
	private int width;
	private int filedWidth;
	
	private List<ShowType> showTypes;//编辑的时候编辑类型
	
	///上面的信息是表column本省带有的信息，而下面则是视图展现的时候需要 展现的形式
//	private String displayType;//默认为：text 我们在选择视图的时候可以配置为 checkbox ,radio,href
	
	private String description;
	
	private int nullable;

	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ShowType getShowType() {
		return showType;
	}

	public void setShowType(ShowType showType) {
		this.showType = showType;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isEdiable() {
		return ediable;
	}

	public void setEdiable(boolean ediable) {
		this.ediable = ediable;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ShowType> getShowTypes() {
		return showTypes;
	}

	public void setShowTypes(List<ShowType> showTypes) {
		this.showTypes = showTypes;
	}

	public void addShowType(ShowType showType){
		
		if(showTypes==null){
			showTypes=new ArrayList<ShowType>();
		}
		showTypes.add(showType);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getFiledWidth() {
		return filedWidth;
	}

	public void setFiledWidth(int filedWidth) {
		this.filedWidth = filedWidth;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJavaVarName() {
		return javaVarName;
	}

	public void setJavaVarName(String javaVarName) {
		this.javaVarName = javaVarName;
	}

	public int getNullable() {
		return nullable;
	}

	public void setNullable(int nullable) {
		this.nullable = nullable;
	}

	

	

	
}
