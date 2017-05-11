package com.hframework.common.frame.dialect;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySQLDialect implements Dialect {

    private static Logger logger = LoggerFactory.getLogger(MySQLDialect.class);

    /**
     * 将SQL语句包装成分页查询SQL
     *
     * @param querySql SQL语句
     * @param offset   开始行
     * @param limit    查询条数
     * @return SQL语句
     */
    public String getLimitString(String querySql, int offset, int limit) {
        StringBuffer buffer = new StringBuffer(" limit ");
        if (offset > -1) {
            buffer.append(offset).append(",").append(limit);
        } else {
            buffer.append(limit);
        }
        String sql = new StringBuffer(querySql.length() + 20).append(querySql).append(buffer).toString();
        logger.debug("getLimitString to sql :{}", sql);
        return sql;
    }

}
