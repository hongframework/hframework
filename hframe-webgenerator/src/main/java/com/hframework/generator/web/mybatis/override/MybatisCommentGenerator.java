package com.hframework.generator.web.mybatis.override;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.InnerEnum;
import org.mybatis.generator.internal.DefaultCommentGenerator;

/**
 * Created by zhangqh6 on 2015/10/19.
 */
public class MybatisCommentGenerator extends DefaultCommentGenerator{

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {


//        innerClass.addJavaDocLine("/**"); //$NON-NLS-1$
//        innerClass.addJavaDocLine(" * " + introspectedTable.getTableConfiguration().getProperty("chineseName"));
//        innerClass.addJavaDocLine(" * User hframe admin");
//        innerClass.addJavaDocLine(" */"); //$NON-NLS-1$
        super.addClassComment(innerClass, introspectedTable, markAsDoNotDelete);
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {

//        innerClass.addJavaDocLine("/**"); //$NON-NLS-1$
//        innerClass.addJavaDocLine(" * " + introspectedTable.getFullyQualifiedTable());
//        innerClass.addJavaDocLine(" * User： zhangqh6"); //$NON-NLS-1$
//        innerClass.addJavaDocLine(" */"); //$NON-NLS-1$
        super.addClassComment(innerClass, introspectedTable);
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
        super.addFieldComment(field, introspectedTable);
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        super.addFieldComment(field, introspectedTable, introspectedColumn);
    }

    @Override
    public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
        super.addEnumComment(innerEnum, introspectedTable);
    }


}
