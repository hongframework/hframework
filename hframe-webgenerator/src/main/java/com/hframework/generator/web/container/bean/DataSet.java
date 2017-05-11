package com.hframework.generator.web.container.bean;

import com.hframework.common.util.BeanUtils;

import java.util.Date;

public class DataSet {
    private Long hfpmDataSetId;

    private String hfpmDataSetName;

    private String hfpmDataSetCode;

    private Long mainHfmdEntityId;

    private Long hfpmProgramId;

    private Long opId;

    private Date createTime;

    private Long modifyOpId;

    private Date modifyTime;

    private Integer delFlag;

    public DataSet(Long hfpmDataSetId, String hfpmDataSetName, String hfpmDataSetCode, Long mainHfmdEntityId, Long hfpmProgramId, Long opId, Date createTime, Long modifyOpId, Date modifyTime, Integer delFlag) {
        this.hfpmDataSetId = hfpmDataSetId;
        this.hfpmDataSetName = hfpmDataSetName;
        this.hfpmDataSetCode = hfpmDataSetCode;
        this.mainHfmdEntityId = mainHfmdEntityId;
        this.hfpmProgramId = hfpmProgramId;
        this.opId = opId;
        this.createTime = createTime;
        this.modifyOpId = modifyOpId;
        this.modifyTime = modifyTime;
        this.delFlag = delFlag;
    }

    public Long getHfpmDataSetId() {
        return hfpmDataSetId;
    }

    public String getHfpmDataSetName() {
        return hfpmDataSetName;
    }

    public String getHfpmDataSetCode() {
        return hfpmDataSetCode;
    }

    public Long getMainHfmdEntityId() {
        return mainHfmdEntityId;
    }

    public Long getHfpmProgramId() {
        return hfpmProgramId;
    }

    public Long getOpId() {
        return opId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Long getModifyOpId() {
        return modifyOpId;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setHfpmDataSetId(Long hfpmDataSetId) {
        this.hfpmDataSetId=hfpmDataSetId;
    }

    public void setHfpmDataSetName(String hfpmDataSetName) {
        this.hfpmDataSetName=hfpmDataSetName;
    }

    public void setHfpmDataSetCode(String hfpmDataSetCode) {
        this.hfpmDataSetCode=hfpmDataSetCode;
    }

    public void setMainHfmdEntityId(Long mainHfmdEntityId) {
        this.mainHfmdEntityId=mainHfmdEntityId;
    }

    public void setHfpmProgramId(Long hfpmProgramId) {
        this.hfpmProgramId=hfpmProgramId;
    }

    public void setOpId(Long opId) {
        this.opId=opId;
    }

    public void setCreateTime(Date createTime) {
        this.createTime=createTime;
    }

    public void setModifyOpId(Long modifyOpId) {
        this.modifyOpId=modifyOpId;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime=modifyTime;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag=delFlag;
    }

    public DataSet() {
        super();
    }

    public Object getAs(Class tClass) {
        return BeanUtils.convertObject(tClass, this);
    }
}