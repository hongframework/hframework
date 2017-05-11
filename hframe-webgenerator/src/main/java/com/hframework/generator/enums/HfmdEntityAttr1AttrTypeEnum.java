package com.hframework.generator.enums;


import java.math.BigDecimal;
import java.util.Date;

public enum HfmdEntityAttr1AttrTypeEnum {
    INT("int", 1, Integer.class, 11),
    BIGINT("bigint", 2, Long.class, 20),
    VARCHAR("varchar", 3, String.class),
    NUMERIC("numeric", 4, BigDecimal.class),
    DATETIME("datetime", 5, Date.class),
    DATE("date", 6, Date.class),
    TINYINT("tinyint", 7, Byte.class),
    SMALLINT("smallint", 8, Integer.class,6),
    DOUBLE("double", 9, Double.class),
    DECIMAL("decimal", 10, BigDecimal.class),
    FLOAT("float", 11, Float.class),
    text("text", 12, String.class);


    // 成员变量
    private String name;
    private int index;
    private Class javaTypeClass;
    private Integer defaultSize;

    // 构造方法
    private HfmdEntityAttr1AttrTypeEnum(String name, int index, Class javaTypeClass) {
        this.name = name;
        this.index = index;
        this.javaTypeClass = javaTypeClass;
    }

    // 构造方法
    private HfmdEntityAttr1AttrTypeEnum(String name, int index, Class javaTypeClass, int defaultSize) {
        this.name = name;
        this.index = index;
        this.javaTypeClass = javaTypeClass;
        this.defaultSize = defaultSize;
    }

    // 普通方法
    public static String getName(int index) {
        for (HfmdEntityAttr1AttrTypeEnum c : HfmdEntityAttr1AttrTypeEnum.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }

    // 普通方法
    public static Integer getDefaultSize(int index) {
        for (HfmdEntityAttr1AttrTypeEnum c : HfmdEntityAttr1AttrTypeEnum.values()) {
            if (c.getIndex() == index) {
                return c.defaultSize;
            }
        }
        return null;
    }

    // 普通方法
    public static Class getJavaTypeClass(int index) {
        for (HfmdEntityAttr1AttrTypeEnum c : HfmdEntityAttr1AttrTypeEnum.values()) {
            if (c.getIndex() == index) {
                return c.javaTypeClass;
            }
        }
        return null;
    }

    // 普通方法
    public static int getIndex(String name) {
        for (HfmdEntityAttr1AttrTypeEnum c : HfmdEntityAttr1AttrTypeEnum.values()) {
            if (name != null && c.getName().equals(name.toLowerCase())) {
                return c.index;
            }
        }
        System.out.println("32432");
        return 1;
    }

    // get set 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    public Class getJavaTypeClass() {
        return javaTypeClass;
    }

    public void setJavaTypeClass(Class javaTypeClass) {
        this.javaTypeClass = javaTypeClass;
    }

    public Integer getDefaultSize() {
        return defaultSize;
    }

    public void setDefaultSize(Integer defaultSize) {
        this.defaultSize = defaultSize;
    }
}
