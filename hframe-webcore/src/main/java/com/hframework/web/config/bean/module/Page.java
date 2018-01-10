package com.hframework.web.config.bean.module;

import com.hframework.web.config.bean.Module;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * generated by hframework on 2016.
 */@XStreamAlias("page")
public class Page   {

	@XStreamImplicit
    @XStreamAlias("component")
	private List<Component> componentList;
    @XStreamImplicit
    @XStreamAlias("element")
    private List<Element> elementList;
	@XStreamAlias("event")
	private String event;
	@XStreamAsAttribute
    @XStreamAlias("id")
	private String id;
	@XStreamAsAttribute
    @XStreamAlias("name")
	private String name;
	@XStreamAsAttribute
    @XStreamAlias("page-template")
	private String pageTemplate;
    @XStreamAsAttribute
    @XStreamAlias("vmpath")
    private String vmPath;
    @XStreamAsAttribute
    @XStreamAlias("data-set")
	private String dataSet;
    @XStreamAsAttribute
    @XStreamAlias("rel-page")
    private String relPage;

    //父Module
    @XStreamOmitField
    private Module module;

    public Page() {
    }
   
 	 	 
     public List<Component> getComponentList(){
     	return componentList == null ? componentList = new ArrayList<Component>() : componentList;
     }

     public void setComponentList(List<Component> componentList){
     	this.componentList = componentList;
     }
	 	 	 
     public String getEvent(){
     	return event;
     }

     public void setEvent(String event){
     	this.event = event;
     }
	 	 	 
     public String getId(){
     	return id;
     }

     public void setId(String id){
     	this.id = id;
     }
	 	 	 
     public String getName(){
     	return name;
     }

     public void setName(String name){
     	this.name = name;
     }
	 	 	 
     public String getPageTemplate(){
     	return pageTemplate;
     }

     public void setPageTemplate(String pageTemplate){
     	this.pageTemplate = pageTemplate;
     }
	 	 	 
     public String getDataSet(){
     	return dataSet;
     }

     public void setDataSet(String dataSet){
     	this.dataSet = dataSet;
     }

    public String getRelPage() {
        return relPage;
    }

    public void setRelPage(String relPage) {
        this.relPage = relPage;
    }

    public List<Element> getElementList() {
        return elementList == null ? new ArrayList<Element>() : elementList;
    }

    public void setElementList(List<Element> elementList) {
        this.elementList = elementList;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public String getVmPath() {
        return vmPath;
    }

    public void setVmPath(String vmPath) {
        this.vmPath = vmPath;
    }
}
