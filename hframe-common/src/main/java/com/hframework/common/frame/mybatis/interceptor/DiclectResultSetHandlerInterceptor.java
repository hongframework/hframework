package com.hframework.common.frame.mybatis.interceptor;

import com.hframework.common.util.ReflectUtils;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.RowBounds;

import java.sql.Statement;
import java.util.Properties;


@Intercepts({@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})})
public class DiclectResultSetHandlerInterceptor implements Interceptor {

    public Object intercept(Invocation invocation) throws Throwable {
        DefaultResultSetHandler resultSet = (DefaultResultSetHandler) invocation.getTarget();
        // 不用浪费性能做属性存在判断
        RowBounds rowBounds = (RowBounds) ReflectUtils.getFieldValue(resultSet, "rowBounds");
        if (rowBounds.getLimit() > 0 && rowBounds.getLimit() < RowBounds.NO_ROW_LIMIT) {
            // 强制不允许游标分页
            ReflectUtils.setFieldValue(resultSet, "rowBounds", new RowBounds());
        }
        return invocation.proceed();
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties arg0) {
    }

}
