package com.hframework.web.context;

import com.hframework.web.config.bean.module.Page;
import com.hframework.web.config.bean.pagetemplates.Pagetemplate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zhangquanhong on 2016/5/26.
 */
public class PageDescriptor{
    private String module;
    private String code;
    private String name;
    private Page page;

    private Pagetemplate pageTemplate;

    private String subDataSetNames;

    //组件信息
    private Map<String, ComponentDescriptor> components = new LinkedHashMap<String, ComponentDescriptor>();

    //元素信息
    private Map<String, ElementDescriptor> elements = new LinkedHashMap<String, ElementDescriptor>();

    //容器信息
    private Map<String, ContainerDescriptor> containers = new HashMap<String, ContainerDescriptor>();

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, ComponentDescriptor> getComponents() {
        return components;
    }

    public void setComponents(Map<String, ComponentDescriptor> components) {
        this.components = components;
    }

    public Pagetemplate getPageTemplate() {
        return pageTemplate;
    }

    public void setPageTemplate(Pagetemplate pageTemplate) {
        this.pageTemplate = pageTemplate;
    }

    public Map<String, ElementDescriptor> getElements() {
        return elements;
    }

    public void setElements(Map<String, ElementDescriptor> elements) {
        this.elements = elements;
    }

    public void addComponentDescriptor(String componentId, ComponentDescriptor descriptor) {
        descriptor.setPageDescriptor(this);
        if(components.containsKey(componentId)) {
            components.put(componentId,descriptor);
        }else {
            components.put(componentId,descriptor);
        }
    }

    public ComponentDescriptor getComponentDescriptor(String componentId) {
        return components.get(componentId);
    }

    public ComponentDescriptor getComponentDescriptorBy(String componentId) {
        ComponentDescriptor componentDescriptor = components.get(componentId);
        if(componentDescriptor != null) {
            return componentDescriptor;
        }
        for (ComponentDescriptor descriptor : components.values()) {
            if(descriptor.getId().equals(componentId)) {
                return descriptor;
            }
        }
        return null;
    }

    public void addElementDescriptor(String elementId, ElementDescriptor descriptor) {
        elements.put(elementId,descriptor);
    }

    public Map<String, ContainerDescriptor> getContainers() {
        return containers;
    }

    public void setContainers(Map<String, ContainerDescriptor> containers) {
        this.containers = containers;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public String getSubDataSetNames() {
        return subDataSetNames;
    }

    public void setSubDataSetNames(String subDataSetNames) {
        this.subDataSetNames = subDataSetNames;
    }
}