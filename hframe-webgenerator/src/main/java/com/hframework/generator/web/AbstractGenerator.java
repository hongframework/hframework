package com.hframework.generator.web;

import com.hframework.common.util.file.FileUtils;
import com.hframework.common.util.message.VelocityUtil;
import com.hframework.beans.class0.Table;
import com.hframework.beans.class0.Class;
import com.hframework.generator.web.container.CreatorContainer;
import com.hframework.generator.web.container.CreatorContainerBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangqh6 on 2015/10/17.
 */
public abstract class AbstractGenerator {

    protected CreatorContainer container;//数据容器

    protected String companyName;//公司名称
    protected String projectName;//项目名称
    protected String moduleName;//模块名称
    protected Table table;//表信息

    protected Class poClass;//po对象
    protected Class poExampleClass;//po查询对象
    protected Class daoClass;//dao对象
    protected Class daoImplClass;//dao实现对象
    protected Class mapper;//mapper对象
    protected Class serviceClass;//服务对象
    protected Class serviceImplClass; //服务实现对象
    protected Class action;//action对象
    protected Class controller;//controller对象

    protected Class editClass;//当前编辑对象

    public AbstractGenerator(String companyName, String projectName, String moduleName, Table table) throws Exception {
        this.companyName = companyName;
        this.projectName = projectName;
        this.moduleName = moduleName;
        this.table = table;
        CreatorContainer container = CreatorContainerBuilder.getCreatorContainer(companyName, projectName, moduleName, table.getTableName());
        poClass = container.Po;
        poExampleClass = container.PoExample;
        daoClass = container.Dao;
        daoImplClass = container.DaoImpl;
        mapper = container.Mapper;
        serviceClass = container.Service;
        serviceImplClass = container.ServiceImpl;
        action = container.Action;
        controller = container.Controller;
    }

    public  String create() throws Exception {
        setImportClass();
        setField();
        createMethod();

        Map map=new HashMap();
        map.put("CLASS", editClass);

        String content = VelocityUtil.produceTemplateContent("com/hframework/generator/vm/po.vm", map);
        FileUtils.writeFile(editClass.getFilePath(), content);
        return null;
    }

    public void setEditClass(Class editClass){
        this.editClass = editClass;
    }

    /**
     * 设置引入包信息
     */
    public abstract void setImportClass();

    /**
     * 设置类字段信息
     */
    public abstract void setField();

    /**
     * 创建方法
     */
    public abstract void createMethod();

}
