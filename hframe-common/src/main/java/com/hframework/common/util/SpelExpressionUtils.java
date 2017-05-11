package com.hframework.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangquanhong on 2016/10/21.
 */
public class SpelExpressionUtils {

    private static final Logger logger = LoggerFactory.getLogger(SpelExpressionUtils.class);

    public static boolean check(String express, Map<String, Object> data) throws Exception {
        return Boolean.parseBoolean(execute(express, data));
    }

    public static String execute(String express, Map<String, Object> data) throws Exception {
        if(StringUtils.isNotBlank(express)) {
            //去掉字符常量，同时必须以字母开头的才会是变量
            String[] vars = RegexUtils.find(express.replaceAll("'[^']*'", ""), "[a-zA-Z]+[a-zA-Z0-9._]*");
            Arrays.sort(vars, new Comparator<String>() {
                public int compare(String o1, String o2) {
                    return o1.contains(o2) ? -1 : 1;
                }
            });
            return execute(data, express, vars);
        }

        return null;
    }
    private static String execute(Map<String, Object> data, String filterExp, String[] vars) throws Exception {
        String resultExp = filterExp;
        try{
            if(StringUtils.isBlank(filterExp)) {
                return null;
            }

            if(vars != null && vars.length > 0) {
                for (String var : vars) {
                    resultExp = resultExp.replaceAll(var,getValValueFromData(var, data));
                }
            }

            return execute(resultExp);
        }catch (Exception e) {
            logger.error("SPEL execute failed, [filterExp = {}, resultExp = {}]", filterExp, resultExp);
            throw new Exception(resultExp);
        }
    }

    public static String execute(String express){
        ExpressionParser parser = new SpelExpressionParser();
        return parser.parseExpression("#{" + express + "}",
                new TemplateParserContext()).getValue(String.class);
    }

    private static String getValValueFromData(String var, Map<String, Object> data) {
        if(data.get(var) != null) {
            if (data.get(var) instanceof String) {
                return "'" + String.valueOf(data.get(var)) + "'" ;
            }
        }
        return String.valueOf(data.get(var));
    }

    public static void main(String[] args) throws Exception {
        String express = "add_time != orig.add_time &&  !(1>2) && '2015-12-12 12:12:21' > '2015-12-12 12:12:12' ";


//        express = "(1>2) && '2015-12-12 12:12:21' > '2015-12-12 12:12:12' || '2015-12-12 12:12:22' != '2015-12-12 12:12:12'";

        System.out.println(check(express, new HashMap<String, Object>() {{
            put("add_time", "2015-12-12 12:12:12");
            put("orig.add_time", "2015-12-12 12:12:11");
        }}));

        System.out.println(execute(express, new HashMap<String, Object>() {{
            put("add_time", "2015-12-12 12:12:11");
            put("orig.add_time", "2015-12-12 12:12:11");
        }}));

        System.out.println(execute("count * 3", new HashMap<String, Object>() {{
            put("count", 10);
        }}));
    }
}
