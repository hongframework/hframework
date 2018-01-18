package com.hframework.common.util;

import com.google.common.base.Enums;
import com.google.common.base.Optional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangqh6 on 2015/10/18.
 */
public class ExampleUtils {

    private static final Map<String, String> signKeyWordMap = new HashMap<String, String>();
    public static final Map<RelationOperator, String> inputSignAndSignMap = new HashMap<RelationOperator, String>();

    /*
    EQU - 等于:equal
    NEQ - 不等于:not equal
    LSS - 小于:less than
    LEQ - 小于或等于:equal or less than
    GTR - 大于:greater than
    GEQ - 大于或等于:equal or greater than
    LKE - 模糊匹配:like
    RLK -右侧模糊匹配：right like
    LLK - 左侧模糊匹配：left like
     */

    public static String getSign(RelationOperator operator) {
        return inputSignAndSignMap.get(operator);
    }

    public enum RelationOperator{
        EQU, NEQ, LSS, LEQ, GTR, GEQ, LKE/*, RLK, LLK*/;
    }

    static {
        inputSignAndSignMap.put(RelationOperator.EQU,"==");
        inputSignAndSignMap.put(RelationOperator.NEQ,"!=");
        inputSignAndSignMap.put(RelationOperator.GTR,">");
        inputSignAndSignMap.put(RelationOperator.LSS,"<");
        inputSignAndSignMap.put(RelationOperator.GEQ,">=");
        inputSignAndSignMap.put(RelationOperator.LEQ,"<=");
        inputSignAndSignMap.put(RelationOperator.LKE,"~=");
//        inputSignAndSignMap.put("N","N");
//        inputSignAndSignMap.put("NN","NN");
//        inputSignAndSignMap.put(RelationOperator.GTR,"IN");
//        inputSignAndSignMap.put(RelationOperator.GTR,"NIN");
//        inputSignAndSignMap.put(RelationOperator.GTR,"BT");
//        inputSignAndSignMap.put(RelationOperator.GTR,"NBT");
//        inputSignAndSignMap.put(RelationOperator.GTR,"NLK");
    }

    static {
        signKeyWordMap.put("=","EqualTo");
        signKeyWordMap.put("!=","NotEqualTo");
        signKeyWordMap.put(">","GreaterThan");
        signKeyWordMap.put("<","LessThan");
        signKeyWordMap.put(">=","GreaterThanOrEqualTo");
        signKeyWordMap.put("<=","LessThanOrEqualTo");
        signKeyWordMap.put("~=","Like");
        signKeyWordMap.put("N","IsNull");
        signKeyWordMap.put("NN","IsNotNull");
        signKeyWordMap.put("IN","In");
        signKeyWordMap.put("NIN","NotIn");
        signKeyWordMap.put("BT","Between");
        signKeyWordMap.put("NBT","NotBetween");
        signKeyWordMap.put("NLK","NotLike");
    }


    /**
     * 将一个业务对象转换为Example查询对象
     * @param srcObj
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public  static <T> T parseExample(Object srcObj,Class<T> exampleClass) throws InvocationTargetException,
            IllegalAccessException, InstantiationException, NoSuchMethodException {
        return (T) parseExample(srcObj,exampleClass.newInstance());
    }

    public static <T> T clone(Object example) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Class<?> exampleClass = example.getClass();

        Object newExample = exampleClass.newInstance();

        ReflectUtils.setFieldValue(newExample,"distinct",ReflectUtils.getFieldValue(example, "distinct"));
        ReflectUtils.setFieldValue(newExample, "limitEnd", ReflectUtils.getFieldValue(example, "limitEnd"));
        ReflectUtils.setFieldValue(newExample, "limitStart", ReflectUtils.getFieldValue(example, "limitStart"));
        ReflectUtils.setFieldValue(newExample, "orderByClause", ReflectUtils.getFieldValue(example, "orderByClause"));

        List oredCriteria = (List) ReflectUtils.getFieldValue(example, "oredCriteria");

        if(oredCriteria != null) {
            for (Object criteria : oredCriteria) {
                Object newCriteria = ReflectUtils.invokeMethod(newExample, "createCriteria", new Class[]{}, new Object[]{});
                List newCriterions = new ArrayList();
                List allCriteria = (List) ReflectUtils.invokeMethod(criteria, "getAllCriteria", new Class[]{}, new Object[]{});
                for (Object criterion : allCriteria) {
                    newCriterions.add(criterion);
                }
                ReflectUtils.setFieldValue(newCriteria, "criteria", newCriterions);

            }
        }
        return (T) newExample;
    }

    public static Object getBaseCriteriaOrCreate(Object exampleObj) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        List oredCriteria = (List) ReflectUtils.getFieldValue(exampleObj, "oredCriteria");
        Object criteriaObj = null;
        if(oredCriteria != null && oredCriteria.size() > 0) {
            criteriaObj = oredCriteria.get(0);
        }else {
            Method criteriaMethod = exampleObj.getClass().getMethod("createCriteria");
            criteriaObj = criteriaMethod.invoke(exampleObj);
        }
        return criteriaObj;
    }

    public static void invokeConditionOnCriteria(Object criteriaObj, Method method, String... parameters) throws InvocationTargetException, IllegalAccessException {
        if(method == null ||
                (parameters.length > 0 && StringUtils.isBlank(parameters[0]))) return;

        List<Object> parameterList = new ArrayList<Object>();
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            String stringValue = null;
            if(parameters.length > i && StringUtils.isNotBlank(parameters[i])) {
                stringValue = parameters[i].trim();
                // 对exampleObj对象直接赋值
                if(method.getParameterTypes()[i] == Long.class) {
                    parameterList.add(Long.valueOf(stringValue));
                }else if(method.getParameterTypes()[i] == Integer.class) {
                    parameterList.add(Integer.valueOf(stringValue));
                }else if(method.getParameterTypes()[i] == Double.class) {
                    parameterList.add(Double.valueOf(stringValue));
                }else if(method.getParameterTypes()[i] == Byte.class) {
                    parameterList.add(Byte.valueOf(stringValue));
                }else if( method.getParameterTypes()[i] == Date.class) {
                    try {
                        parameterList.add(DateUtils.parseYYYYMMDDHHMMSS(URLDecoder.decode(stringValue)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }else {
                    parameterList.add(stringValue);
                }
            }else {
                parameterList.add(null);
            }
        }

        method.invoke(criteriaObj, parameterList.toArray(new Object[0]));
    }

    /**
     * 将一个业务对象转换为Example查询对象
     * @param srcObj
     * @param exampleObj
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object parseExample(Object srcObj,Object exampleObj) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        Object criteriaObj = getBaseCriteriaOrCreate(exampleObj);

        // 方法名称及方法
        Map<String, Method> criteriaMethods = BeanUtils.getMethods(criteriaObj.getClass());

        Map<String, String> convertMap = BeanUtils.convertMap(srcObj, false);
        if(convertMap != null && !convertMap.isEmpty()) {
            for (String filed : convertMap.keySet()) {
                StringBuffer sb = new StringBuffer("and");
                sb.append(StringUtils.upperCaseFirstChar(filed));
                sb.append("EqualTo");

                // 获取对应方法
                Method method = criteriaMethods.get(sb.toString());
                String value = convertMap.get(filed);
                invokeConditionOnCriteria(criteriaObj, method, value);
            }
        }

        return exampleObj;
    }

    /**
     * 将参数字符串转换为MAP对象
     * @param params
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Map<String, List<String[]>> parseParams2Map(String params) throws InvocationTargetException, IllegalAccessException{
        Map<String, List<String[]>> convertMap = new HashMap<String, List<String[]>>();

        String[] splits = StringUtils.split(params, "&");
        if(splits !=null && splits.length > 0) {
            for (String split : splits) {
                Pattern pattern = Pattern.compile("(>=|<=|>|<|!=|==|~=)");
                Matcher matches = pattern.matcher(split);

                if(matches.find()){
                    String sign = matches.group();
                    if(!convertMap.containsKey(sign)) {
                        convertMap.put(sign,new ArrayList<String[]>());
                    }
                    convertMap.get(sign).add(new String[]{
                            split.substring(0, split.indexOf(sign)).trim(),
                            split.substring(split.indexOf(sign) + sign.length()).trim()});
                }
            }
        }

        return convertMap;
    }

    /**
     * 将参数MAP转换为Example查询对象
     * @param map
     * @param exampleObj
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object parseExample(Map<String, List<String[]>> map,Object exampleObj) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        Object criteriaObj = getBaseCriteriaOrCreate(exampleObj);

        // 方法名称及方法
        Map<String, Method> criteriaMethods = BeanUtils.getMethods(criteriaObj.getClass());
        if (map != null && !map.isEmpty()) {
            for (String sign : map.keySet()) {
                List<String[]> conditions = map.get(sign);
                for (String[] condition : conditions) {
                    String filed = condition[0];
                    String value = condition[1];
                    String methodName = calcExampleMethodName(sign, filed, value);
                    // 获取对应方法
                    Method method = criteriaMethods.get(methodName);
                    invokeConditionOnCriteria(criteriaObj, method, value);
                }
            }
        }
//BUG:nameValue[0] 是java变量名 并非数据库字段名
//        if(map.get("~=") != null) {
//            String orderStr = "";
//            for (String[] nameValue : map.get("~=")) {
//                orderStr += (nameValue[0] + ",");
//            }
//            ReflectUtils.invokeMethod(exampleObj, "setOrderByClause", new java.lang.Class[]{String.class}, new Object[]{orderStr.substring(0, orderStr.length() - 1)});
//        }

        return exampleObj;
    }

    /**
     * 将查询参数转换为Example查询对象
     * @param params
     * @param exampleObj
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object parseExample(String params,Object exampleObj) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        Map<String, List<String[]>> convertMap = parseParams2Map(params);

        return parseExample(convertMap,exampleObj);
    }

    private static String calcExampleMethodName(String sign, String filed, String value) {
        StringBuffer sb = new StringBuffer("and");
        sb.append(StringUtils.upperCaseFirstChar(filed));
        sb.append(signKeyWordMap.get(sign));
        return sb.toString();
    }
    /*

    public Criteria andHfpmProgramIdIsNull() {
        addCriterion("hfpm_program_id is null");
        return (Criteria) this;
    }

    public Criteria andHfpmProgramIdIsNotNull() {
        addCriterion("hfpm_program_id is not null");
        return (Criteria) this;
    }

    public Criteria andHfpmProgramIdEqualTo(Long value) {
        addCriterion("hfpm_program_id =", value, "hfpmProgramId");
        return (Criteria) this;
    }

    public Criteria andHfpmProgramIdNotEqualTo(Long value) {
        addCriterion("hfpm_program_id <>", value, "hfpmProgramId");
        return (Criteria) this;
    }

    public Criteria andHfpmProgramIdGreaterThan(Long value) {
        addCriterion("hfpm_program_id >", value, "hfpmProgramId");
        return (Criteria) this;
    }

    public Criteria andHfpmProgramIdGreaterThanOrEqualTo(Long value) {
        addCriterion("hfpm_program_id >=", value, "hfpmProgramId");
        return (Criteria) this;
    }

    public Criteria andHfpmProgramIdLessThan(Long value) {
        addCriterion("hfpm_program_id <", value, "hfpmProgramId");
        return (Criteria) this;
    }

    public Criteria andHfpmProgramIdLessThanOrEqualTo(Long value) {
        addCriterion("hfpm_program_id <=", value, "hfpmProgramId");
        return (Criteria) this;
    }

    public Criteria andHfpmProgramIdIn(List<Long> values) {
        addCriterion("hfpm_program_id in", values, "hfpmProgramId");
        return (Criteria) this;
    }

    public Criteria andHfpmProgramIdNotIn(List<Long> values) {
        addCriterion("hfpm_program_id not in", values, "hfpmProgramId");
        return (Criteria) this;
    }

    public Criteria andHfpmProgramIdBetween(Long value1, Long value2) {
        addCriterion("hfpm_program_id between", value1, value2, "hfpmProgramId");
        return (Criteria) this;
    }

    public Criteria andHfpmProgramIdNotBetween(Long value1, Long value2) {
        addCriterion("hfpm_program_id not between", value1, value2, "hfpmProgramId");
        return (Criteria) this;
    }
    */
}
