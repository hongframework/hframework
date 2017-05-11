package com.hframework.generator.web.mybatis.plugins;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;

/**
 * Created by zhangqh6 on 2015/9/22.
 * 无参数默认构造器添加
 */
public class NoParamConstructorPlugin extends PluginAdapter{

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        CommentGenerator commentGenerator = context.getCommentGenerator();
        for (Field field : topLevelClass.getFields()) {
            String name = field.getName();
            char c = name.charAt(0);
            String camel = Character.toUpperCase(c) + name.substring(1);
            Method method = new Method();
            method.setVisibility(JavaVisibility.PUBLIC);
            method.setName("set" + camel);
            method.addParameter(new Parameter(field.getType(), name));
            method.addBodyLine("this." + name + "=" + name + ";");
            commentGenerator.addGeneralMethodComment(method, introspectedTable);
            topLevelClass.addMethod(method);
        }

        Method method = new Method();
        method.setConstructor(true);
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName(topLevelClass.getType().getShortName());
        method.addBodyLine("super();");
        topLevelClass.addMethod(method);
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    public boolean validate(List<String> warnings) {
        return true;
    }
}
