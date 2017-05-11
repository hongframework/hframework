package com.hframework.common.util.math;

import com.google.common.collect.Lists;

import java.util.*;

/**
 * Created by zhangquanhong on 2016/12/11.
 */
public class ShortestPathUtils {

    public static <T> List<List<T>> search(T startPoint, T endPoint , Map<T, List<T>> accessibilityPathMap) {
        List<List<T>> result = new ArrayList<List<T>>();
        List<T> excludeList = new ArrayList<T>();
        search(startPoint, endPoint, accessibilityPathMap, result, excludeList, new ArrayList<T>());
        Collections.sort(result, new Comparator<List<T>>() {
            public int compare(List<T> o1, List<T> o2) {
                return o1.size() - o2.size();
            }
        });
        return result;
    }

    private static <T> void search(T startPoint, T endPoint , Map<T, List<T>> accessibilityPathMap, List<List<T>> result, List<T> excludeList, List<T> curSearchPath) {

        curSearchPath = Lists.newArrayList(curSearchPath);
        curSearchPath.add(startPoint);

        if(!accessibilityPathMap.containsKey(startPoint)) {
            return ;
        }
        List<T> candidates = Lists.newArrayList(accessibilityPathMap.get(startPoint));

        if(candidates.contains(endPoint)) {
            List<T> ts = Lists.newArrayList(curSearchPath);
            ts.add(endPoint);
            result.add(ts);
            candidates.remove(endPoint);
        }

        excludeList.add(startPoint);

        candidates.removeAll(excludeList);

        for (T nextStartPoint : candidates) {

            search(nextStartPoint, endPoint ,accessibilityPathMap, result, Lists.newArrayList(excludeList), curSearchPath);
        }

    }

    public static void main(String[] args) {
        List<List<Integer>> search = search(5, 4, new HashMap<Integer, List<Integer>>() {{
            put(1, Lists.newArrayList(2, 3, 4));
            put(2, Lists.newArrayList( 3, 4));
            put(3, Lists.newArrayList(1, 2, 5));
            put(4, Lists.newArrayList(1, 3, 5));
            put(5, Lists.newArrayList(1, 2));
        }});
        for (List<Integer> integers : search) {
            System.out.println(integers);
        }

    }
}
