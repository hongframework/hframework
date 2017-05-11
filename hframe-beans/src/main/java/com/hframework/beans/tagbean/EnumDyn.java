package com.hframework.beans.tagbean;



/**
 * EnumDyn entity. @author MyEclipse Persistence Tools
 */

public class EnumDyn implements java.io.Serializable {


    // Fields    

     private String coreEnumId;
     private String  coreEnumGroupId;
     private Integer showType;
     private String sql;
     private String condition;
     private String orderCondition;


    // Constructors

    /** default constructor */
    public EnumDyn() {
    }

	/** minimal constructor */
    public EnumDyn(String coreEnumId, String sql) {
        this.coreEnumId = coreEnumId;
        this.sql = sql;
    }
    
    /** full constructor */
    public EnumDyn(String coreEnumId, Integer showType, String sql, String condition, String orderCondition) {
        this.coreEnumId = coreEnumId;
        this.showType = showType;
        this.sql = sql;
        this.condition = condition;
        this.orderCondition = orderCondition;
    }

   
    // Property accessors

    public String getCoreEnumId() {
        return this.coreEnumId;
    }
    
    public void setCoreEnumId(String coreEnumId) {
        this.coreEnumId = coreEnumId;
    }

    public Integer getShowType() {
        return this.showType;
    }
    
    public void setShowType(Integer showType) {
        this.showType = showType;
    }

    public String getSql() {
        return this.sql;
    }
    
    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getCondition() {
        return this.condition;
    }
    
    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getOrderCondition() {
        return this.orderCondition;
    }
    
    public void setOrderCondition(String orderCondition) {
        this.orderCondition = orderCondition;
    }

	public String getCoreEnumGroupId() {
		return coreEnumGroupId;
	}

	public void setCoreEnumGroupId(String coreEnumGroupId) {
		this.coreEnumGroupId = coreEnumGroupId;
	}
   








}