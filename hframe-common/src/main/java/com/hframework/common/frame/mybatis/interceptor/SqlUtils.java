package com.hframework.common.frame.mybatis.interceptor;

/**
 * SQL语句相关工具
 */
public class SqlUtils {

    /**
     * 定位SQL语句select元素位置
     *
     * @param querySql 查询SQL语句
     * @return SQL语句
     */
    public static int getAfterSelectPoint(String querySql) {
        int minLen = "select".length();
        int len = "select distinct".length();
        int selectIndex = querySql.toLowerCase().indexOf("select");
        final int selectDistinctIndex = querySql.toLowerCase().indexOf("select distinct");
        return selectIndex + (selectDistinctIndex == selectIndex ? len : minLen);
    }

    /**
     * 定位SQL语句Form元素位置
     *
     * @param querySql 查询SQL语句
     * @return SQL语句
     */
    public static int getAfterFormPoint(String querySql) {
        int formIndex = querySql.toLowerCase().indexOf("from");
        return formIndex;
    }

    /**
     * 将SQL语句包装成查询记录总数的SQL
     *
     * @param querySql SQL语句
     * @return SQL语句
     */
    public static String getCountString(String querySql) {
        StringBuilder sql = new StringBuilder();
        sql.append("select count(1) AS rowCount from (").append(querySql).append(") a");
        return sql.toString();
    }
}
