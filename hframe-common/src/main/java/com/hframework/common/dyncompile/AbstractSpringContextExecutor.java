package com.hframework.common.dyncompile;

/**
 * Created by zhangquanhong on 2017/3/14.
 */
public abstract class AbstractSpringContextExecutor {
    protected GenericDAO dao = new GenericDAO();

    public static class GenericDAO{
        public String query(String tableName, String keyColumn, String returnColumn) {
            return "123456";
        }
    }
}
