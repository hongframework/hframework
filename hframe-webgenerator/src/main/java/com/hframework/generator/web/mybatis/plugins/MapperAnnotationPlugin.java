package com.hframework.generator.web.mybatis.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.internal.db.ConnectionFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangqh6 on 2015/9/21.
 * Mapper对象需要增加spring对象注解
 */
public class MapperAnnotationPlugin extends PluginAdapter{

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Repository"));
        interfaze.addAnnotation("@Repository");

        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

    public boolean validate(List<String> warnings) {
        return true;
    }
}
