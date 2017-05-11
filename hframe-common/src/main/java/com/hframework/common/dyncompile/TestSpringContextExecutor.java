package com.hframework.common.dyncompile;

/**
 * Created by zhangquanhong on 2017/3/14.
 */
public class TestSpringContextExecutor extends AbstractSpringContextExecutor implements SpringContextExecutor {
    public Integer execute() {
        return Integer.valueOf(dao.query("1","1","1"));
    }
}
