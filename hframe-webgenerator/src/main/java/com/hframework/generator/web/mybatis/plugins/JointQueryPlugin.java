package com.hframework.generator.web.mybatis.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Element;
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
 * 增加对联表查询的支持，联表查询默认读取视图sql语句，作为查询的where条件
 */
public class JointQueryPlugin extends PluginAdapter{

    private Map<String ,String[]> viewInfos = new HashMap<String, String[]>();

    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {

        //如果该数据对象没有insert语句，表明为联表查询对象
        /*
     [0]-->:
     select
     [1]-->:
    <if test="distinct">
      distinct
    </if>
    [2]-->:
    <include refid="Base_Column_List" />
    [3]-->:
    from v_core_table_db
    [4]-->:
    <if test="_parameter != null">
      <include refid="Example_Where_Clause" />
    </if>
    [5]-->:
    <if test="orderByClause != null">
      order by ${orderByClause}
    </if>
    [6]-->:
    <if test="limitStart != null and limitStart != 0">
      limit #{limitStart} , #{limitEnd}
    </if>
         */

        if(!introspectedTable.getTableConfiguration().isInsertStatementEnabled()) {
            if(!viewInfos.containsKey(introspectedTable.getFullyQualifiedTableNameAtRuntime())) {
                init(introspectedTable);
            }
            TextElement fromElement =new TextElement("from " + viewInfos.get(introspectedTable.getFullyQualifiedTableNameAtRuntime())[0]); //$NON-NLS-1$
            element.getElements().set(3,fromElement);
        }

        return super.sqlMapSelectByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapBaseColumnListElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
//        if(!introspectedTable.getTableConfiguration().isInsertStatementEnabled()) {
//            if(!viewInfos.containsKey(introspectedTable.getFullyQualifiedTableNameAtRuntime())) {
//                init(introspectedTable);
//            }
//
//            TextElement fromElement =new TextElement("a,b,c,d,e,f,g,h [todo1]"); //$NON-NLS-1$
//            element.getElements().set(0,fromElement);
//        }
        return super.sqlMapBaseColumnListElementGenerated(element, introspectedTable);
    }

    private void init(IntrospectedTable introspectedTable) {
        JDBCConnectionConfiguration jdbcConnectionConfiguration =
                introspectedTable.getContext().getJdbcConnectionConfiguration();
        Connection connection = null;
        try {
            connection = ConnectionFactory.getInstance().getConnection(
                    jdbcConnectionConfiguration);
            Statement statement = null;
            ResultSet rs = null;
            statement = connection.createStatement();
            rs = statement.executeQuery("SHOW CREATE VIEW " + introspectedTable.getFullyQualifiedTableNameAtRuntime());
            while(rs.next()) {
                String viewSql = rs.getString(2);
                viewSql = viewSql.replaceAll("`", "");
                String columnInfos = viewSql.substring(viewSql.indexOf(" AS select ")+11, viewSql.indexOf(" from "));
                String tableInfos = viewSql.substring(viewSql.indexOf(" from ") + 6, viewSql.indexOf(" where "));
                tableInfos = tableInfos.substring(tableInfos.indexOf("(")+1,tableInfos.lastIndexOf(")"));
                String conditionInfos = viewSql.substring(viewSql.indexOf(" where ") + 7);
                conditionInfos = conditionInfos.substring(conditionInfos.indexOf("(")+1,conditionInfos.lastIndexOf(")"));

                StringBuffer selSubSql = new StringBuffer()
                        .append(" select ").append(columnInfos)
                        .append(" from ").append(tableInfos)
                        .append(" where ").append(conditionInfos);

                viewInfos.put(introspectedTable.getFullyQualifiedTableNameAtRuntime(),
                        new String[]{" (" + selSubSql.toString() + " ) " + introspectedTable.getFullyQualifiedTableNameAtRuntime()});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e1) {
                }
            }
        }
    }

    @Override
    public boolean sqlMapCountByExampleElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {

        if(!introspectedTable.getTableConfiguration().isInsertStatementEnabled()) {
            if(!viewInfos.containsKey(introspectedTable.getFullyQualifiedTableNameAtRuntime())) {
                init(introspectedTable);
            }
            TextElement fromElement = new TextElement("select count(*) from " +
                    viewInfos.get(introspectedTable.getFullyQualifiedTableNameAtRuntime())[0]); //$NON-NLS-1$
            element.getElements().set(0,fromElement);
        }

        return super.sqlMapCountByExampleElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

    public boolean validate(List<String> warnings) {
        return true;
    }
}
