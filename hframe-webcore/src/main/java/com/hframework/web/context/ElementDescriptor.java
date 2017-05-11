package com.hframework.web.context;

import com.hframework.web.config.bean.pagetemplates.Element;

/**
 * Created by zhangquanhong on 2016/5/26.
 */
public abstract class ElementDescriptor{
    private Element element;
    private String id;

    public ElementDescriptor(Element element) {
        this.element = element;
        id = element.getId();
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}