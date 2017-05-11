package com.hframework.common.util;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * User: zhangqh6
 * Date: 2016/2/17 10:30:30
 */
public class PathMatcherUtils {

    public  static boolean matches(String pathPattern, String lookupPath) {
        PathMatcher pathMatcher = new AntPathMatcher();
        return pathMatcher.match(pathPattern,lookupPath);
    }

    public static void main(String[] args) {
        System.out.println(matches("com.*.service.impl.HfmdEnumC*SVIm*pl","com..service.impl.HfmdEnumClassSVImpl"));

        System.out.println(matches("/member/*/*","/member/user/test.jsp"));
    }
}
