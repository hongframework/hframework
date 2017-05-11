package com.hframework.web.context;

import com.hframework.web.config.bean.pagetemplates.Element;

/**
 * Created by zhangquanhong on 2016/5/26.
 */
public class StringDescriptor extends ElementDescriptor{

    private String value;

    public StringDescriptor(Element element) {
        super(element);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}