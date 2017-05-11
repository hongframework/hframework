package com.hframework.generator.web.mybatis.plugins;

import java.util.List;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.PrimitiveTypeWrapper;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * Created by zhangqh6 on 2015/9/20.
 */
public class PaginationPlugin extends PluginAdapter {
    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
                                              IntrospectedTable introspectedTable) {
        // add field, getter, setter for limit clause
        addLimit(topLevelClass, introspectedTable, "limitStart");
        addLimit(topLevelClass, introspectedTable, "limitEnd");
        return super.modelExampleClassGenerated(topLevelClass,
                introspectedTable);
    }
    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
//		XmlElement isParameterPresenteElemen = (XmlElement) element
//				.getElements().get(element.getElements().size() - 1);
        XmlElement isNotNullElement = new XmlElement("if"); //$NON-NLS-1$
        isNotNullElement.addAttribute(new Attribute("test", "limitEnd != null and limitEnd != 0")); //$NON-NLS-1$ //$NON-NLS-2$
//		isNotNullElement.addAttribute(new Attribute("compareValue", "0")); //$NON-NLS-1$ //$NON-NLS-2$
        isNotNullElement.addElement(new TextElement(
                "limit #{limitStart} , #{limitEnd}"));
//		isParameterPresenteElemen.addElement(isNotNullElement);
        element.addElement(isNotNullElement);
        return super.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(element,
                introspectedTable);
    }
    private void addLimit(TopLevelClass topLevelClass,
                          IntrospectedTable introspectedTable, String name) {
        CommentGenerator commentGenerator = context.getCommentGenerator();
        Field field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
//		field.setType(FullyQualifiedJavaType.getIntInstance());
        field.setType(PrimitiveTypeWrapper.getIntegerInstance());
        field.setName(name);
//		field.setInitializationString("-1");
        commentGenerator.addFieldComment(field, introspectedTable);
        topLevelClass.addField(field);
        char c = name.charAt(0);
        String camel = Character.toUpperCase(c) + name.substring(1);
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("set" + camel);
        method.addParameter(new Parameter(PrimitiveTypeWrapper.getIntegerInstance(), name));
        method.addBodyLine("this." + name + "=" + name + ";");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(PrimitiveTypeWrapper.getIntegerInstance());
        method.setName("get" + camel);
        method.addBodyLine("return " + name + ";");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
    }
    /**
     * This plugin is always valid - no properties are required
     */
    public boolean validate(List<String> warnings) {
        return true;
    }

}
