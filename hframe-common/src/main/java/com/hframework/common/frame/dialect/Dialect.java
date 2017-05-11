package com.hframework.common.frame.dialect;

public interface Dialect {

    /**
     * 将SQL语句包装成分页查询SQL
     * @param querySql SQL语句
     * @param offset 开始行
     * @param limit 查询条数
     * @return SQL语句
     */

    String getLimitString(String querySql, int offset, int limit);

}
