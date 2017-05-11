package com.hframework.common.frame.cache.ehcache;

/**
 * User: zhangqh6
 * Date: 2015/11/14 14:29:29
 */
public enum CacheKeyEnum {

//    //缓存实体对象先关信息(是否有用，待定)
//    private static final String ENTITY_CACHE = "ENTITY_CACHE";
//
//    //缓存数据集新建对象
//    private static final String DS_CREATE_CACHE = "DS_CREATE_CACHE";
//    //缓存数据集修改对象
//    private static final String DS_MODIFY_CACHE = "DS_MODIFY_CACHE";
//    //缓存数据集展示对象
//    private static final String DS_SHOW_CACHE = "DS_SHOW_CACHE";

    //缓存实体对象先关信息(是否有用，待定)
    ENTITY_CACHE("ENTITY_CACHE"),

    //缓存数据集新建对象
    DS_CREATE_CACHE("DS_CREATE_CACHE"),

    //缓存数据集修改对象
    DS_MODIFY_CACHE("DS_MODIFY_CACHE"),

    //缓存数据集展示对象
    DS_SHOW_CACHE("DS_SHOW_CACHE"),

    //缓存数据集列表展示对象
    DS_LIST_CACHE("DS_LIST_CACHE"),

    //缓存树展示对象
    DS_TREE_CACHE("DS_TREE_CACHE");

    // 成员变量
    private String name;
    private int value;

    // 构造方法
    private CacheKeyEnum(String name,int value) {
        this.name = name;
    }

    // 构造方法
    private CacheKeyEnum(int value, String name) {
        this.name = name;
    }

    // 构造方法
    private CacheKeyEnum(String name) {
        this.name = name;
    }

//    // 普通方法
//    public static String getName(int index) {
//        for (CacheKeyEnum c : CacheKeyEnum.values()) {
//            if (c.getValue() == value) {
//                return c.name;
//            }
//        }
//        return null;
//    }
//
//    // 普通方法
//    public static int getIndex(String name) {
//        for (CacheKeyEnum c : CacheKeyEnum.values()) {
//            if (c.getName().equals(name)) {
//                return c.index;
//            }
//        }
//        return 1;
//    }

    // get set 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
