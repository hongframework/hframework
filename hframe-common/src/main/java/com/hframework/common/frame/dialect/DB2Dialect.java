package com.hframework.common.frame.dialect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DB2Dialect implements Dialect {

    private static Logger logger = LoggerFactory.getLogger(MySQLDialect.class);

    private static boolean hasDistinct(String sql) {
        return sql.toLowerCase().indexOf("select distinct") >= 0;
    }

    private String getRowNumber(String sql) {
        StringBuffer rownumber = new StringBuffer(50).append("rownumber() over(");

        int orderByIndex = sql.toLowerCase().indexOf("order by");

        if (orderByIndex > 0 && !hasDistinct(sql)) {
            rownumber.append(sql.substring(orderByIndex));
        }

        rownumber.append(") as rownumber_,");

        return rownumber.toString();
    }

    /**
     * 将SQL语句包装成分页查询SQL
     *
     * @param querySql SQL语句
     * @param offset   开始行
     * @param limit    查询条数
     * @return SQL语句
     */
    public String getLimitString(String querySql, int offset, int limit) {
        int startOfSelect = querySql.toLowerCase().indexOf("select");

        StringBuffer pagingSelect = new StringBuffer(querySql.length() + 100)
                .append(querySql.substring(0, startOfSelect))    // add the comment
                .append("select * from ( select ")             // nest the main query in an outer select
                .append(getRowNumber(querySql));                 // add the rownnumber bit into the outer query select list

        if (hasDistinct(querySql)) {
            pagingSelect.append(" row_.* from ( ")            // add another (inner) nested select
                    .append(querySql.substring(startOfSelect)) // add the main query
                    .append(" ) as row_");                     // close off the inner nested select
        } else {
            pagingSelect.append(querySql.substring(startOfSelect + 6)); // add the main query
        }

        pagingSelect.append(" ) as temp_ where rownumber_ ");

        //add the restriction to the outer select
        int endset = offset + limit;
        if (offset > -1) {
            pagingSelect.append("between ").append(offset + 1).append(" and ").append(endset);
        } else {
            pagingSelect.append("<= ").append(endset);
        }

        logger.debug("DB2Dialect getLimitString to sql :{}" , pagingSelect.toString());

        return pagingSelect.toString();
    }
}
