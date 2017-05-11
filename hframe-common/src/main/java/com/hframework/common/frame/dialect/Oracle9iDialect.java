package com.hframework.common.frame.dialect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Oracle9iDialect implements Dialect {

    private static Logger logger = LoggerFactory.getLogger(Oracle9iDialect.class);

    /**
     * 将SQL语句包装成分页查询SQL
     *
     * @param querySql SQL语句
     * @param offset   开始行
     * @param limit    查询条数
     * @return SQL语句
     */
    public String getLimitString(String querySql, int offset, int limit) {
        querySql = querySql.trim();
        String forUpdateClause = null;
        boolean isForUpdate = false;
        final int forUpdateIndex = querySql.toLowerCase().lastIndexOf("for update");
        if (forUpdateIndex > -1) {
            forUpdateClause = querySql.substring(forUpdateIndex);
            querySql = querySql.substring(0, forUpdateIndex - 1);
            isForUpdate = true;
        }

        StringBuffer pagingSelect = new StringBuffer(querySql.length() + 100);
        if (offset > -1) {
            pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
        } else {
            pagingSelect.append("select * from ( ");
        }

        pagingSelect.append(querySql);

        int endset = offset + limit;
        if (offset > -1) {
            pagingSelect.append(" ) row_ where rownum <= ")
                    .append(endset).append(") where rownum_ > ")
                    .append(offset);
        } else {
            pagingSelect.append(" ) where rownum <= ").append(endset);
        }

        if (isForUpdate) {
            pagingSelect.append(" ");
            pagingSelect.append(forUpdateClause);
        }

        String sql = pagingSelect.toString();
        logger.debug("Oracle9iDialect getLimitString to sql :{}", sql);
        return sql;
    }
}
