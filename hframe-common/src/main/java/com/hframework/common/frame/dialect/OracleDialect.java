package com.hframework.common.frame.dialect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OracleDialect implements Dialect {

    private static Logger logger = LoggerFactory.getLogger(OracleDialect.class);

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
        boolean isForUpdate = false;
        if (querySql.toLowerCase().endsWith(" for update")) {
            querySql = querySql.substring(0, querySql.length() - 11);
            isForUpdate = true;
        }

        StringBuffer pagingSelect = new StringBuffer(querySql.length() + 100);
        if (offset > 0) {
            pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
        } else {
            pagingSelect.append("select * from ( ");
        }
        pagingSelect.append(querySql);
        int endset = offset + limit;
        if (offset > 0) {
            pagingSelect.append(" ) row_ ) where rownum_ <= ")
                    .append(endset).append(" and rownum_ > ")
                    .append(offset);
        } else {
            pagingSelect.append(" ) where rownum <= ").append(endset);
        }

        if (isForUpdate) {
            pagingSelect.append(" for update");
        }
        logger.debug("OracleDialect getLimitString to sql :{}" , pagingSelect.toString());
        return pagingSelect.toString();
    }
}
