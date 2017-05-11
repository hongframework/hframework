package com.hframework.common.frame.validation;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: zhangqh6
 * Date: 2015/12/17 16:06:06
 */
public class ValidationUtil {

    class TypeOf {
        public static final String INT = "int"; //整数
        public static final String FLOAT = "float"; //浮点数
        public static final String STRING = "string"; //字符串
        public static final String DATE = "date"; //日期
        public static final String DATETIME = "datetime";//时间
    }

    class RelationOperator {
        public static final String EQ = "eq"; //等于
        public static final String LT = "lt"; //小于
        public static final String GT = "gt"; //大于
        public static final String NE = "ne"; //不等于
        public static final String LE = "le"; //小于等于
        public static final String GE = "ge"; //大于等于
        public static final String IN = "in"; //在之内
        public static final String NI = "ni"; //不在之内
        public static final String BT = "bt"; //在之间
        public static final String NB = "nb"; //不在之间
        public static final String LENGTH = "length"; //长度
        public static final String TYPEOF = "typeof"; //类型

    }

    class LogicalOperator {
        public static final String AND = "&&"; //且
        public static final String OR = "||"; //或
        public static final String NON = "!"; //非
    }

    class ReplacementCharacter {
        public static final String NULL = "NULL"; //且
        public static final String NOW = "NOW"; //且
    }

    /*
     * 算法一：值替换 => 优点：简单；缺点：存在没必要的运算
     * (!(ne[NULL] && in[5,4,3,2,1] && true)||eq[NULL])||（typeof[datetime]&&rl[NOW])分解如下:
     * a: ne[NULL] = ture；in[5,4,3,2,1] = false；eq[NULL] =true；typeof[datetime] = true； rl[NOW] = false；
     * b: (!true && false && true) || true || (true || false );
     * c: true
     * 算法二：rule表达式树形递归分解（从底向上递归 =>优点：减少不必要运算
     * (!(ne[NULL] && in[5,4,3,2,1] && true)||eq[NULL])||（typeof[datetime]&&rl[NOW])分解如下:
     * a: (ne[NULL]&&in[5,4,3,2,1])  （typeof[datetime]&&rl[NOW]) ==> (x||eq[NULL])||y
     * b: (x||eq[NULL]) ==> z||y
     * c: z||y ==> true/false
     * 算法二：rule表达式树形递归分解（从上向下递归）=>优点：减少不必要运算，节省拆解
     * (!(ne[NULL] && in[5,4,3,2,1] && true)||eq[NULL])||（typeof[datetime]&&rl[NOW])分解如下:
     * a:执行第一个
     * b: (!true && false && true) || true || (true || false );
     * c: true
     *
     * @param data
     * @param rule
     * @return
     */
    public static ValidInfo validate(String data, String rule) {

        if (StringUtils.isBlank(rule)) {
            ValidInfo validInfo = new ValidInfo();
            validInfo.setResult(true);
            return validInfo;
        }

        rule = rule.replaceAll("[ ]+", "");

        ValidInfo validInfo = new ValidInfo();
        validInfo.setData(data);
        boolean result = excuteCascade(data, null, rule, validInfo);
        validInfo.setResult(result);
        validInfo.setResultMessage(ValidationUtil.transferResultMessage(data, validInfo.getRule()));
        return validInfo;

    }

    private static String getFirstExp(String rule) {
        Pattern pattern = Pattern.compile("[^\\(\\)!(\\|\\|)(\\&\\&)]+");
        Matcher matches = pattern.matcher(rule);
        if(matches.find()) {
            return matches.group();
        }
        return null;
    }

    private static boolean excuteCascade(String data, String dataType, String rule, ValidInfo validInfo) {

        String headExp = getFirstExp(rule);
        if(dataType == null) {
            dataType = parseDataType(headExp);
        }
        //获得表达式执行结果
        boolean headResult = excuteExp(data, dataType, headExp, validInfo);
        //将表达式替换为执行结果，只可能为true或者false
        rule = rule.replace(headExp,String.valueOf(headResult));

        //前后关联将该表达式前后多余的括号与否计算掉，并替换为最终结果
        System.out.print("==> rule : " + rule);
        rule = trimHeadResult(rule, headResult);
        System.out.println(" ==> : " + rule);
        headResult = Boolean.parseBoolean(getFirstExp(rule));

        //判断该表达式后面的逻辑运算符，根据逻辑运算符做相应替换
        Pattern groupPattern = Pattern.compile("(true|false)[\\|\\&]*");
        Matcher matches = groupPattern.matcher(rule);
        if(matches.find()) {//一定能匹配
            String headExpExt = matches.group();
//            System.out.println(headExpExt);
            //后续没有逻辑运算符，说明已是最终结果，直接返回结果
            if(headExpExt.equals(String.valueOf(headResult))) {
                return headResult;
            }

            if(headExpExt.endsWith("&&") && !headResult) {
                int beginIndex = rule.indexOf(headExpExt);
                int endIndex = getNextExpEndIndex(rule,headExpExt);
                rule = rule.substring(0,beginIndex) + false + rule.substring(endIndex);
                return excuteCascade(data, dataType, rule, validInfo);

            }else if(headExpExt.endsWith("||") && headResult) {
                int beginIndex = rule.indexOf(headExpExt);
                int endIndex = getNextExpEndIndex(rule,headExpExt);
                rule = rule.substring(0,beginIndex) + true + rule.substring(endIndex);
                return excuteCascade(data, dataType, rule, validInfo);
            }else {
                rule = rule.replace(headExpExt,"");
                return excuteCascade(data, dataType, rule, validInfo);
            }
        }
        System.out.println("该处原则上不会执行到！");
        return false;
    }

    private static String parseDataType(String headExp) {
        if(headExp.startsWith(RelationOperator.TYPEOF)) {
            return headExp.substring(headExp.indexOf("[") + 1, headExp.indexOf("]"));
        }
        return null;
    }

    private static String trimHeadResult(String rule, boolean headResult) {
        Pattern groupPattern = Pattern.compile("(true|false)\\)*");
        Matcher matches = groupPattern.matcher(rule);
        if(matches.find()) {
            String temp = matches.group();
            rule = rule.replace(temp,String.valueOf(headResult));
            int count = temp.replace("true", "").replace("false", "").length();
            groupPattern = Pattern.compile("!?(\\(!?){" + count + "}(true|false)");
            matches = groupPattern.matcher(rule);
            if(matches.find()) {
                temp = matches.group();
                int length = temp.replace("true", "").replace("false", "").replaceAll("\\(", "").length();
                if(length % 2 == 1) {
                    headResult = !headResult;
                }
                rule = rule.replace(temp, String.valueOf(headResult));
            }
        }
        return rule;
    }

    private static int getNextExpEndIndex(String rule, String headExpExt) {

        int count = 0 ;

        int beginIndex = rule.indexOf(headExpExt);
        int endIndex = rule.length();
        for(int i = beginIndex; i < rule.length(); i ++) {
            if('(' == rule.charAt(i)) {
                count ++;
            }
            if(')' == rule.charAt(i)) {
                count --;
            }
            if(count < 0) {
                endIndex = i;
                break;
            }
        }
        return endIndex;
    }

    @SuppressWarnings("unused")
    private static String trans2RegexText(String curExp) {
        return curExp.replaceAll("\\[","\\\\\\[").replaceAll("\\]", "\\\\\\]");
    }

    private static boolean excuteExp(String data, String dataType, String curExp,ValidInfo validInfo) {
        if("true".equals(curExp)) {
            return true;
        }
        if("false".equals(curExp)) {
            return false;
        }

        String ruleType = curExp.substring(0,curExp.indexOf("["));
        String paramStr = curExp.substring(curExp.indexOf("[") + 1, curExp.indexOf("]"));
        String[] params = paramStr.split(",");

        boolean result =  excuteExp(data, dataType, ruleType, params);

        validInfo.setRule(curExp);
        validInfo.setRuleResult(result);
        System.out.println("==> data : " + data + " ; curExp : " + curExp + " ; result : " + result);
        return result;
    }

    private static boolean excuteExp(String data, String dataType, String ruleType, String[] params) {

        if(TypeOf.DATETIME.equals(dataType) || TypeOf.DATE.equals(dataType)) {
            if(ReplacementCharacter.NOW.equals(params[0])) {
                params[0] = DateUtil.getCurrentDateString(DateUtil.DATETIME_PATTERN);
            }
        }

        if (ruleType.equals(RelationOperator.EQ)) {
            if (ReplacementCharacter.NULL.equals(params[0])) {
                return data == null;
            } else {
                return params[0].equals(data);
            }
        } else if (ruleType.equals(RelationOperator.LT)) {
            return compareLessThan(data, dataType, params);
        } else if (ruleType.equals(RelationOperator.GT)) {
            return compareGreaterThan(data, dataType, params);
        } else if (ruleType.equals(RelationOperator.NE)) {
            if (ReplacementCharacter.NULL.equals(params[0])) {
                return !(data == null);
            } else {
                return !params[0].equals(data);
            }
        } else if (ruleType.equals(RelationOperator.LE)) {
            return !compareGreaterThan(data, dataType, params);
        } else if (ruleType.equals(RelationOperator.GE)) {
            return !compareLessThan(data, dataType, params);
        } else if (ruleType.equals(RelationOperator.IN)) {
            return Arrays.binarySearch(params, data) > -1;
        } else if (ruleType.equals(RelationOperator.NI)) {
            return Arrays.binarySearch(params, dataType) < -1;
        } else if (ruleType.equals(RelationOperator.BT)) {
            return compareBetween(data, dataType, params);
        } else if (ruleType.equals(RelationOperator.NB)) {
            return !compareBetween(data, dataType, params);
        } else if (ruleType.equals(RelationOperator.LENGTH)) {
            int param1 = Integer.parseInt(params[0]);
            int param2 = -1;
            if (params.length > 1) {
                param2 = Integer.parseInt(params[1]);
            }
            if (TypeOf.FLOAT.equals(data)) {
                int precision = new BigDecimal(data).precision();
                int intLength = new BigDecimal(data).toBigInteger().bitLength();
                if (param1 > intLength) {
                    if (param2 != -1 && param2 < precision) {
                        return false;
                    }
                    return true;
                } else {
                    return false;
                }
            }
            return data.length() < Integer.valueOf(param1);
        } else if (ruleType.equals(RelationOperator.TYPEOF)) {
            if (TypeOf.INT.equals(dataType)) {
                try {
                    Long.parseLong(data);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            } else if (TypeOf.FLOAT.equals(dataType)) {
                try {
                    new BigDecimal(data);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            } else if (TypeOf.DATE.equals(dataType)) {
                return DateUtil.stringToDate(data, DateUtil.ISO_EXPANDED_DATE_FORMAT) != null;
            } else if (TypeOf.DATETIME.equals(dataType)) {
                return DateUtil.stringToDate(data, DateUtil.DATETIME_PATTERN) != null;
            } else if (TypeOf.STRING.equals(dataType)) {
                return true;
            } else {
                System.out.println("【ERROR】该类型不支持：" + ruleType);
                return false;
            }
        } else {
            System.out.println("不支持类型：" + ruleType);
        }


        return false;
    }

    private static boolean compareBetween(String data, String dataType, String[] params) {
        if(TypeOf.INT.equals(data)) {
            return Long.parseLong(data) >= Long.parseLong(params[0])
                    && Long.parseLong(data) <= Long.parseLong(params[1]);
        }else if(TypeOf.FLOAT.equals(data)) {
            return new BigDecimal(data).compareTo(new BigDecimal(params[0])) >= 0
                    &&  new BigDecimal(data).compareTo(new BigDecimal(params[1])) <= 0;
        }else if(TypeOf.DATE.equals(data)) {
            return DateUtil.stringToDate(data, DateUtil.ISO_EXPANDED_DATE_FORMAT)
                    .compareTo(DateUtil.stringToDate(params[0], DateUtil.ISO_EXPANDED_DATE_FORMAT)) >= 0
                    && DateUtil.stringToDate(data, DateUtil.ISO_EXPANDED_DATE_FORMAT)
                    .compareTo(DateUtil.stringToDate(params[1], DateUtil.ISO_EXPANDED_DATE_FORMAT)) <= 0;
        }else if(TypeOf.DATETIME.equals(data)) {
            return DateUtil.stringToDate(data, DateUtil.DATETIME_PATTERN)
                    .compareTo(DateUtil.stringToDate(params[0], DateUtil.DATETIME_PATTERN)) >= 0
                    && DateUtil.stringToDate(data, DateUtil.DATETIME_PATTERN)
                    .compareTo(DateUtil.stringToDate(params[1], DateUtil.DATETIME_PATTERN)) <= 0;
        }else if(TypeOf.STRING.equals(data)) {
            System.out.println("【ERROR】该类型不支持！");
            return false;
        }else {
            System.out.println("【ERROR】该类型不支持！");
            return false;
        }
    }

    private static boolean compareGreaterThan(String data, String dataType, String[] params) {
        if(TypeOf.INT.equals(dataType)) {
            return Long.parseLong(data) > Long.parseLong(params[0]);
        }else if(TypeOf.FLOAT.equals(dataType)) {
            return new BigDecimal(data).compareTo(new BigDecimal(params[0])) > 0;
        }else if(TypeOf.DATE.equals(dataType)) {
            return DateUtil.stringToDate(data, DateUtil.ISO_EXPANDED_DATE_FORMAT)
                    .after(DateUtil.stringToDate(params[0], DateUtil.ISO_EXPANDED_DATE_FORMAT));
        }else if(TypeOf.DATETIME.equals(dataType)) {
            return DateUtil.stringToDate(data, DateUtil.DATETIME_PATTERN)
                    .after(DateUtil.stringToDate(params[0], DateUtil.DATETIME_PATTERN));
        }else if(TypeOf.STRING.equals(dataType)) {
            System.out.println("【ERROR】该类型不支持！");
            return false;
        }else {
            System.out.println("【ERROR】该类型不支持！");
            return false;
        }
    }

    private static boolean compareLessThan(String data, String dataType, String[] params) {
        if(TypeOf.INT.equals(dataType)) {
            return Long.parseLong(data) < Long.parseLong(params[0]);
        }else if(TypeOf.FLOAT.equals(dataType)) {
            return new BigDecimal(data).compareTo(new BigDecimal(params[0])) < 0;
        }else if(TypeOf.DATE.equals(dataType)) {
            return DateUtil.stringToDate(data, DateUtil.ISO_EXPANDED_DATE_FORMAT)
                    .before(DateUtil.stringToDate(params[0], DateUtil.ISO_EXPANDED_DATE_FORMAT));
        }else if(TypeOf.DATETIME.equals(dataType)) {
            return DateUtil.stringToDate(data, DateUtil.DATETIME_PATTERN)
                    .before(DateUtil.stringToDate(params[0], DateUtil.DATETIME_PATTERN));
        }else if(TypeOf.STRING.equals(dataType)) {
            System.out.println("【ERROR】该类型不支持！");
            return false;
        }else {
            System.out.println("【ERROR】该类型不支持！");
            return false;
        }
    }

    public static String transferResultMessage(String data, String rule) {
        String ruleType = rule.substring(0, rule.indexOf("["));
        String paramStr = rule.substring(rule.indexOf("[") + 1, rule.indexOf("]"));
        String[] params = paramStr.split(",");

        if (ruleType.equals(RelationOperator.EQ)) {
            if (ReplacementCharacter.NULL.equals(params[0])) {
                return "只能为空";
            } else {
                return "只能为" + params[0];
            }
        } else if (ruleType.equals(RelationOperator.LT)) {
            return "小于" + params[0];
        } else if (ruleType.equals(RelationOperator.GT)) {
            return "大于" + params[0];
        } else if (ruleType.equals(RelationOperator.NE)) {
            return "不等于" + params[0];
        } else if (ruleType.equals(RelationOperator.LE)) {
            return "小于等于" + params[0];
        } else if (ruleType.equals(RelationOperator.GE)) {
            return "大于等于" + params[0];
        } else if (ruleType.equals(RelationOperator.IN)) {
            return "在" + Arrays.toString(params) + "之内";
        } else if (ruleType.equals(RelationOperator.NI)) {
            return "不在" + Arrays.toString(params) + "之内";
        } else if (ruleType.equals(RelationOperator.BT)) {
            return "在" + Arrays.toString(params) + "之间";
        } else if (ruleType.equals(RelationOperator.NB)) {
            return "不在" + Arrays.toString(params) + "之间";
        } else if (ruleType.equals(RelationOperator.LENGTH)) {
            int param1 = Integer.parseInt(params[0]);
            int param2 = -1;
            if (params.length > 1) {
                param2 = Integer.parseInt(params[1]);
            }
            if (TypeOf.FLOAT.equals(data)) {
                String tmp = "小数前最多" + param1 + "位";
                if (param2 > -1) {
                    tmp += "，小数点后最多" + param2 + "位";
                }

                return tmp;
            }
            return "长度不能大于" + params[0] + "位";
        } else if (ruleType.equals(RelationOperator.TYPEOF)) {
            String dataType = params[0];
            if (TypeOf.INT.equals(dataType)) {
                return "必须为数字类型";

            } else if (TypeOf.FLOAT.equals(dataType)) {
                return "必须为浮点类型";
            } else if (TypeOf.DATE.equals(dataType)) {
                return "必须为日期类型";
            } else if (TypeOf.DATETIME.equals(dataType)) {
                return "必须为时间类型";
            } else if (TypeOf.STRING.equals(dataType)) {
                return "必须为字符类型";
            }
        } else {
        }
        return null;
    }

    public static void main(String[] args) {
        String exp = "(!(ne[NULL] && in[5,4,3,2,1] && true)||eq[NULL]) && (typeof[datetime]&&gt[NOW])&&true";
//        exp = "ne[NULL]";
//        exp = "(typeof[date]) && gt[NOW]";
//        exp = "(true&&typeof[date])||eq[]";


        ValidInfo result = ValidationUtil.validate("", exp);

        System.out.println(result.isResult() + ":"+ result.getResultMessage());

    }

}
