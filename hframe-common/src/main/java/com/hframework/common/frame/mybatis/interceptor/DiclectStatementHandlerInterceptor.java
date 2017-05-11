package com.hframework.common.frame.mybatis.interceptor;


import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class DiclectStatementHandlerInterceptor extends SQLDialectUtils implements Interceptor {
    // 数据库方言
    private static String dialect;
    // 日志对象
    protected Logger logger = Logger.getLogger(this.getClass());

    public Object intercept(Invocation invocation) throws Throwable {
//        // 翻页参数
//        Page page = PagingContextHolder.getPage();
//        if (page != null) {
//            RoutingStatementHandler statement = (RoutingStatementHandler) invocation.getTarget();
//            PreparedStatementHandler delegate = (PreparedStatementHandler) ReflectUtils.getFieldValue(statement, "delegate");
//            MappedStatement mappedStatement = (MappedStatement) ReflectUtils.getFieldValue(delegate, "mappedStatement");
//
//            Connection conn = (Connection) invocation.getArgs()[0];
//            dialect = conn.getMetaData().getDatabaseProductName().toLowerCase();
//            BoundSql boundSql = statement.getBoundSql();
//            String sql = boundSql.getSql();
//            logger.debug("[执行查询]" + sql);
//
//            String sqlCount = SqlUtils.getCountString(sql);
//            logger.debug("[SQL查询记录总数]" + sqlCount);
//            PreparedStatement preparedStatement = conn.prepareStatement(sqlCount);
//            // 分页SQL<select>中parameterType属性对应的实体参数，即Mapper接口中执行分页方法的参数,该参数不得为空
//            Object parameterObject = boundSql.getParameterObject();
//            BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), sqlCount, boundSql.getParameterMappings(), parameterObject);
//            // 设置查询参数
//            setParameters(preparedStatement, mappedStatement, countBS, parameterObject);
//            // 执行记录总数查询语句
//            ResultSet res = preparedStatement.executeQuery();
//            // 获得记录总数
//            int totalResult = 0;
//            if (res.next()) {
//                totalResult = res.getInt(1);
//            }
//            res.close();
//            preparedStatement.close();
//            // 将记录总数设置到翻页参数中
//            page.setTotalResult(totalResult);
//            logger.debug("[执行总数查询结果]" + totalResult);
//
//            logger.debug("[SQL查询记录]" + sql);
//            sql = SQLDialectUtils.getLimitString(dialect, sql, page.getCurrentResult(), page.getPageSize());
//
//            ReflectUtils.setFieldValue(boundSql, "sql", sql);
//        }
        return invocation.proceed();
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
    }

    /**
     * 对SQL参数(?)设值,参考org.apache.ibatis.scripting.defaults.DefaultParameterHandler
     *
     * @param preparedStatement
     * @param mappedStatement
     * @param boundSql
     * @param parameterObject
     * @throws SQLException
     */
    private void setParameters(PreparedStatement preparedStatement, MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject) throws SQLException {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        Configuration configuration = mappedStatement.getConfiguration();
        if (parameterMappings != null) {
            MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (mappedStatement.getConfiguration().getTypeHandlerRegistry().hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        value = metaObject == null ? null : metaObject.getValue(propertyName);
                    }
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    JdbcType jdbcType = parameterMapping.getJdbcType();
                    if (value == null && jdbcType == null) jdbcType = configuration.getJdbcTypeForNull();
                    typeHandler.setParameter(preparedStatement, i + 1, value, jdbcType);
                }
            }
        }
    }

}
