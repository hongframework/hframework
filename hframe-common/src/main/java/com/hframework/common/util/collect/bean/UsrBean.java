package com.hframework.common.util.collect.bean;

/**
 * User: zhangqh6
 * Date: 2015/12/29 10:53:53
 */
public class UsrBean {

    private int id ;

    private String name;

    private int age;

    public UsrBean(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
