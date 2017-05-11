package com.hframework.web.context.enums;

/**
 * Created by zhangquanhong on 2016/5/26.
 */
public enum ElementType {
    component("component","组件"),
    string("string","字符串"),
    container("container","容器");

    private String code;
    private String name;
    ElementType(String code, String name) {
        this.code = code;
        this.name = name;
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

//        public boolean equal(String name){
//            Optional<ElementType> ifPresent = Enums.getIfPresent(ElementType.class, name);
//            if(ifPresent != null && ifPresent.get() == this) {
//                return true;
//            }
//            return false;
//        }


}