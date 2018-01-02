package com.hframework.web.context;

import com.hframework.web.config.bean.dataset.Node;

import java.io.Serializable;

/**
 * Created by zhangquanhong on 2016/9/11.
 */
public interface IDataSet extends Serializable{

    public Node getNode();

    public IDataSet cloneBean();
}
