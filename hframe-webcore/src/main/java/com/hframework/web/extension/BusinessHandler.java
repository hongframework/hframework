package com.hframework.web.extension;

/**
 * Created by zhangquanhong on 2016/9/22.
 */
public interface BusinessHandler<T> {

    public boolean afterCreate(T t);

    public boolean afterUpdate(T t, T ot);

    public boolean afterDelete(T t);
}
