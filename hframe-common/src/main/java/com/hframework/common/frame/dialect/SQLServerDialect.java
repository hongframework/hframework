package com.hframework.common.frame.dialect;

import com.hframework.common.frame.mybatis.interceptor.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLServerDialect implements Dialect {

    private static Logger logger = LoggerFactory.getLogger(SQLServerDialect.class);


    /**
     * 将SQL语句包装成分页查询SQL
     *
     * @param querySql SQL语句
     * @param offset   开始行
     * @param limit    查询条数
     * @return SQL语句
     */
    public String getLimitString(String querySql, int offset, int limit) {
        if (offset > 0) {
            throw new UnsupportedOperationException("query result offset is not supported");
        }

        String sql = new StringBuffer(querySql.length() + 8)
                .append(querySql)
                .insert(SqlUtils.getAfterSelectPoint(querySql), " top " + limit)
                .toString();

        logger.info("SQLServerDialect getLimitString to sql :{}" , sql);

        return sql;
    }
}
