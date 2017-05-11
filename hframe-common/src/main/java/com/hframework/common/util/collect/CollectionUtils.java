package com.hframework.common.util.collect;


import com.hframework.common.util.collect.bean.*;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * User: zhangqh6
 * Date: 2015/11/17 17:03:03
 */
public class CollectionUtils {

    /**
     * 数据分组
     * @param list
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> Map<K,List<V>> group(List<V> list, Grouper<? super K, ? super V> grouper) {
        Map<K,List<V>> retMap = new LinkedHashMap<K, List<V>>();
        if(list != null && list.size() > 0) {
            for (V value : list) {
                K key = grouper.groupKey(value);
                if(!retMap.containsKey(key)) {
                    retMap.put(key, new ArrayList<V>());
                }
                retMap.get(key).add(value);
            }
        }
        return retMap;
    }
    /**
     * List转Map
     * @param list
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> Map<K,V> convert(List<V> list, Mapper<? super K, ? super V> mapper) {
        Map<K,V> retMap = new LinkedHashMap<K, V>();
        if(list != null && list.size() > 0) {
            for (V value : list) {
                K key = mapper.getKey(value);
                retMap.put(key, value);
            }
        }
        return retMap;
    }

    /**
     * List转Map
     * @param list
     * @param merger
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> int search(List<V> list,V data, Merger<? super K, ? super V> merger) {
        K dataKey = merger.getKey(data);
        if(list != null && list.size() > 0) {
            for (V value : list) {
                K key = merger.getKey(value);
                if(key.equals(dataKey)) {
                    return list.indexOf(value);
                }
            }
        }
        return -1;
    }

    /**
     * List转Map
     * @param map
     * @param <K>
     * @param <V>
     * @param merger
     * @return
     */
    public static <K,V> K search(Map<K, List<V>> map, V data, Merger<? super K, ? super V> merger) {
        K dataKey = merger.getKey(data);
        for (Map.Entry<K, List<V>> mapEntry : map.entrySet()) {
            int index = search(mapEntry.getValue(), data, merger);
            if(index > -1) {
                return mapEntry.getKey();
            }
        }
        return null;
    }

    /**
     * 列表更新
     * @param srcList 被更新的列表
     * @param destList 更新列表
     * @param mapper 合并器
     * @param <K> 对象
     * @param <V> Value
     * @return
     */
    public static <K,V> List<V> update(List<V> srcList, List<V> destList, Mapper<? super K, ? super V> mapper) {
        //更新列表为空，直接返回源对象即可
        if(destList == null || destList.isEmpty()) {
            return srcList;
        }

        //源对象为空，进行初始化
        if(srcList == null) {
            srcList = new ArrayList<V>();
        }

        //循环更新列表，判断对应的值是否存在被更新列表中，
        // 如果存在进行替换，如果不存在直接添加一个新的对象
        flag : for (V dest : destList) {
            K dataKey = mapper.getKey(dest);
            for (V value : srcList) {
                K key = mapper.getKey(value);
                if(key.equals(dataKey)) {
                    srcList.set(srcList.indexOf(value),dest);
                    break flag;
                }
            }
            srcList.add(dest);

        }
        return srcList;
    }

    /**
     * Map更新
     * @param srcMap
     * @param destMap
     * @param mapper
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> Map<K, List<V>> update(Map<K, List<V>> srcMap, Map<K, List<V>> destMap, Mapper<? super K, ? super V> mapper) {
        //更新列表为空，直接返回源对象即可
        if(destMap == null || destMap.isEmpty()) {
            return srcMap;
        }

        //源对象为空，进行初始化
        if(srcMap == null) {
            srcMap = new HashMap();
        }

        //循环Map对象进行值列表更新
        for (K key : destMap.keySet()) {
            List<V> destList = destMap.get(key);
            List<V> srcList = srcMap.get(key);
            srcList = update(srcList, destList, mapper);
            srcMap.put(key,srcList);

        }

        return srcMap;
    }

    /**
     * 列表删除
     * @param srcList 总列表
     * @param destList 删除列表
     * @param merger 合并器
     * @param <K> 对象
     * @param <V> Value
     * @return
     */
    public static <K,V> List<V> remove(List<V> srcList, List<V> destList, Merger<? super K, ? super V> merger) {
        //更新列表为空，直接返回源对象即可
        if(destList == null || destList.isEmpty()) {
            return srcList;
        }

        //源对象为空，进行初始化
        if(srcList == null) {
            return srcList;
        }

        //循环待删除列表，判断对应的值是否存在总列表中，
        // 如果存在直接删除，否则不做任何处理
        for (V dest : destList) {
            int index = CollectionUtils.search(srcList, dest, merger);
            if(index > -1) {
                srcList.remove(index);
            }
        }
        return srcList;
    }

    public static <E> List<E> copy(List<E> originList) {
        if(originList == null) {
            return null;
        }

        List<E> targetList = new ArrayList<E>();

        for (E e : originList) {
            try {
                targetList.add((E) BeanUtils.cloneBean(e));
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            } catch (InvocationTargetException e1) {
                e1.printStackTrace();
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            }
        }

        return targetList;
    }

    /**
     * Map删除
     * @param srcMap
     * @param destMap
     * @param merger
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V> Map<K, List<V>> remove(Map<K, List<V>> srcMap, Map<K, List<V>> destMap, Merger<? super K, ? super V> merger) {
        //更新列表为空，直接返回源对象即可
        if(destMap == null || destMap.isEmpty()) {
            return srcMap;
        }

        //源对象为空,无需删除，直接返回
        if(srcMap == null) {
            return srcMap;
        }

        //循环Map对象进行列表值删除操作
        for (K key : destMap.keySet()) {
            List<V> destList = destMap.get(key);
            List<V> srcList = srcMap.get(key);
            srcList = remove(srcList, destList, merger);
            srcMap.put(key,srcList);
        }
        return srcMap;
    }


    public static <F, T> List<T> fetch(List<F> originList, Fetcher<F, T> fetcher) {
        if(originList == null) {
            return null;
        }
        List<T> targetList = new ArrayList<T>();
        for (F f : originList) {
            targetList.add(fetcher.fetch(f));
        }
        return targetList;
    }

    public static <F,T> List<T> from(List<F> originList, Mapping<F, T> mapping) {
        if(originList == null) {
            return null;
        }
        List<T> targetList = new ArrayList<T>();
        for (F f : originList) {
            targetList.add(mapping.from(f));
        }
        return targetList;
    }


    public static void main(String[] args) {
        List<UsrBean> userList = new ArrayList<UsrBean>();
        userList.add(new UsrBean(1,"张三",29));
        userList.add(new UsrBean(2,"李四",28));
        userList.add(new UsrBean(3,"王二",27));
        userList.add(new UsrBean(4,"李四",29));

        Map<Integer, List<UsrBean>> merge = CollectionUtils.group(userList, new Grouper<Integer, UsrBean>() {
            public Integer groupKey(UsrBean usrBean) {
                return usrBean.getAge();
            }
        });

        Map<Integer, UsrBean> convert = CollectionUtils.convert(userList, new Mapper<Integer, UsrBean>() {
            public Integer getKey(UsrBean usrBean) {
                return usrBean.getAge();
            }

            public Integer groupKey(UsrBean usrBean) {
                return usrBean.getAge();
            }
        });

        System.out.println(1111);
    }


    /**
     *
     * @param map
     * @param key
     * @param aValue
     */
    public static <K,V>  void  addMapValue(Map<K, List<V>> map, K key, V aValue) {
            if(!map.containsKey(key)) {
                map.put(key,new ArrayList<V>());
            }
            map.get(key).add(aValue);
    }

    /**
     *
     * @param map
     * @param key
     * @return
     */
    public static <K,V>   List<V> getMapValue(Map<K, List<V>> map, K key) {
        if(!map.containsKey(key)) {
            map.put(key,new ArrayList<V>());
        }
        return map.get(key);
    }

}
