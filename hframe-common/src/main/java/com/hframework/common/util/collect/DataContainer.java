package com.hframework.common.util.collect;

import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 数据容器
 * User: zhangqh6
 * Date: 2015/11/3 13:40:40
 */
public class DataContainer<T> {

    //原始数据列表
    private List<T> originalList;

    //数据容器分组条件列表
    private List<Object> groupingKeyList;

    //分组后目标数据存储结果
    private Map<String,List<T>> targetMap;

    /**
     * 初始化构造函数
     * @param originalList 原始数据列表
     */
    public DataContainer(List<T> originalList) {
        this.originalList = originalList;
        groupingKeyList = new ArrayList<Object>();
        targetMap = new HashMap<String,List<T>>();
    }

    /**
     * 分组数据清除
     */
    public void clear() {
        groupingKeyList.clear();
        targetMap.clear();
    }


    /**
     * 获取某些维度的数据
     * @param t 查询条件对象
     * @return 查询结果
     * @throws Exception
     */
    public List<T> get(T t) throws Exception {
        if( t == null) {
            return originalList;
        }

        //如果没有目标临时数据，进行初始化
        if(CollectionUtils.isEmpty(targetMap)) {
            //初始化keyList分组对象
            initKeyList(t);
            //按照分组对象进行数据分组
            groupingData();
        }

        //返回数据
        List<T> result = targetMap.get(getKey(t));
        if(result == null) {
            result = new ArrayList<T>();
        }

        return result;
    }

    /**
     * 按照分组对象进行数据分组
     * @throws Exception
     */
    private void groupingData() throws Exception {
        for (T t1 : originalList) {
            String key = getKey(t1);
            put(key, t1);
        }
    }

    /**
     * 获取分组KEY字符串
     * @param obj 分组条件对象
     * @return 获取key字符串
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private String getKey(Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        StringBuffer keySb = new StringBuffer();
        if(obj instanceof Map) {
            for (Object keyObj : groupingKeyList) {
                keySb.append(String.valueOf(((Map) obj).get(keyObj))).append("_");
            }
        }else {
            Class<?> cls = obj.getClass();
            for (Object o : groupingKeyList) {
                String name = (String) o;
                String strGet = "get" + name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
                Method methodGet = getDeclaredMethod(cls, strGet);
                Object object = methodGet.invoke(obj);
                keySb.append(String.valueOf(object)).append("_");
            }
        }
        return  keySb.toString();
    }

    private void put(String key, T t1) {
        if(!targetMap.containsKey(key)) {
            targetMap.put(key,new ArrayList<T>());
        }
        targetMap.get(key).add(t1);
    }

//    /**
//     * 根据当前记录的分组KEY
//     * @param record 记录
//     * @return
//     */
//    private String getKey(Map record) {
//        StringBuffer keySb = new StringBuffer();
//        for (Object keyObj : groupingKeyList) {
//            keySb.append(String.valueOf(record.get(keyObj))).append("_");
//        }
//        return  keySb.toString();
//    }

    /**
     * 初始化keyList分组对象
     * @param t 查询对象
     * @throws Exception
     */
    private void initKeyList(T t) throws Exception {
        if(t instanceof Map) {
            for (Object key : ((Map)t).keySet()) {
                if(key instanceof String || key instanceof Long || key instanceof Integer) {
                    groupingKeyList.add(key);
                }else {
                    throw new Exception("[initKeyList]Map对象的key必须为字符串，数字类型，" + key + "不支持！");
                }
            }
        }else if(t instanceof Object) {
            initObjKeyList(t);
        }else {
            throw new Exception("[initKeyList]类型" + t + "不支持！");
        }

    }

    /**
     * 返回由对象的属性为key,值为map的value的Map集合
     * @param obj 查询对象
     * @throws Exception
     */
    private void initObjKeyList(Object obj) throws Exception {
        Class<?> cls = obj.getClass();
        for (Class<?> superClass = cls; superClass != Object.class; superClass = superClass.getSuperclass()) {
            Field[] fields = superClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName();
                if("serialVersionUID".equals(name)) {
                    continue;
                }
                Object value = field.get(obj);
                if(value != null) {
                    if(checkIsNotBlank(field.getType(), value)) {
                        groupingKeyList.add(name);
                        System.out.println(name + "|" + value);
                    }
                }
            }
        }
    }

    /**
     * 根据方法名获取方法
     * @param objClass
     * @param methodName
     * @return
     */
    public Method getDeclaredMethod(Class<?> objClass, String methodName)
    {
        for (Class<?> superClass = objClass; superClass != Object.class; superClass = superClass.getSuperclass()){
            try{
                //superClass.getMethod(methodName, parameterTypes);
                return superClass.getDeclaredMethod(methodName);
            }
            catch (NoSuchMethodException e){
                //Method 不在当前类定义, 继续向上转型
            }
        }

        return null;
    }

    /**
     * 检查是否为空
     * @param classType
     * @param object
     * @return
     */
    private boolean checkIsNotBlank(Class<?> classType, Object object) {
        String nameType = classType.getSimpleName();
        if ("Integer".equals(nameType) || "int".equals(nameType)) {
            if((Integer)object > 0) {
                return true;
            }
        }
        if ("String".equals(nameType) ) {
            if(!"".equals((String)object)) {
                return true;
            }
        }
        if ("Float".equals(nameType) || "float".equals(nameType)) {
            if((Float)object > 0) {
                return true;
            }
        }
        if ("Double".equals(nameType) || "double".equals(nameType)) {
            if((Double)object > 0) {
                return true;
            }
        }

        if ("Boolean".equals(nameType) || "boolean".equals(nameType)) {
            if((Boolean)object) {
                return true;
            }
        }
        if ("Long".equals(nameType) || "long".equals(nameType)) {
            if((Long)object > 0) {
                return true;
            }
        }

        if ("Short".equals(nameType) || "short".equals(nameType)) {
            if((Short)object > 0) {
                return true;
            }
        }

        if ("Character".equals(nameType) || "char".equals(nameType)) {
            if((Character)object > 0) {
                return true;
            }
        }

        return false;
    }


    /**
     * 初始化keyList
     * @param keySet
     * @throws Exception
     */
    private void initKeyList(Set keySet) throws Exception {
        for (Object key : keySet) {
            if(key instanceof String || key instanceof Long || key instanceof Integer) {
                groupingKeyList.add(key);
            }else {
                throw new Exception("Map对象的key必须为字符串，数字类型，" + key + "不支持！");
            }
        }
    }


    public static void main(String[] args) throws Exception {
        List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
        list.add(new HashMap<String,Object>(){{
            put("name","张三");
            put("age",17);
            put("grade",1);
        }});
        list.add(new HashMap<String,Object>(){{
            put("name","李四");
            put("age",16);
            put("grade",2);
        }});
        list.add(new HashMap<String,Object>(){{
            put("name","王二");
            put("age",18);
            put("grade",1);
        }});


        DataContainer<Map<String,Object>> dc =  new DataContainer<Map<String,Object>>(list);

        List<Map<String,Object>> result = dc.get(new HashMap<String, Object>() {{
            put("grade", 1);
        }});
        System.out.println(result.size());
    }
}
