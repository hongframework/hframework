package com.hframework.generator.web.mybatis.plugins;

import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.List;

/**
 * Created by zhangqh6 on 2015/9/21.
 * Mapper对象需要增加spring对象注解
 */
public class MapperXmlOverwritePlugin extends PluginAdapter{

    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        try {
            File directory = new DefaultShellCallback(true).getDirectory(sqlMap
                    .getTargetProject(), sqlMap.getTargetPackage());
            File targetFile = new File(directory, sqlMap.getFileName());
            if (targetFile.exists()) {
                targetFile.delete();
            }
        } catch (ShellException e) {
            e.printStackTrace();
        }
        return super.sqlMapGenerated(sqlMap, introspectedTable);
    }

    public boolean validate(List<String> warnings) {
        return true;
    }
}
