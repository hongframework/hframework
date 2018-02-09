package com.hframework.web.controller.core;

import com.google.common.collect.Lists;
import com.hframework.beans.class0.Class;
import com.hframework.beans.controller.Pagination;
import com.hframework.beans.controller.ResultData;
import com.hframework.common.frame.ServiceFactory;
import com.hframework.common.util.*;
import com.hframework.common.util.collect.CollectionUtils;
import com.hframework.common.util.collect.bean.Fetcher;
import com.hframework.common.util.collect.bean.Mapper;
import com.hframework.web.CreatorUtil;
import com.hframework.web.context.ComponentDescriptor;
import com.hframework.web.context.DataSetDescriptor;
import com.hframework.web.context.WebContext;
import com.hframework.web.controller.DefaultController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.*;

/**
 * Created by zhangquanhong on 2018/1/11.
 */
@Service
public class ControllerMethodInvoker {

    private static final Logger logger = LoggerFactory.getLogger(ControllerMethodInvoker.class);

    @Resource
    private ConfigurableWebBindingInitializer initializer;


    private ModelAttributeSetter modelAttributeSetter ;


    public  ResultData invokeDetail(Class defPoClass, Object controller, HttpServletRequest request, ComponentDescriptor componentDescriptor) throws Exception {
        String action = "detail";
        Object po  = getPo(defPoClass, controller, action, request, componentDescriptor, new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath())});

        ResultData resultData = DefaultController.invokeMethod(controller, action,
                new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath())},
                new Object[]{po});
        //将页面传过来的参数覆盖原先的值，主要用于具体的form表单提交时需要附带一些参数值 TODO
//        if(resultData.getData() != null) {
//            ReflectUtils.setFieldValue(resultData.getData(), WebContext.getDefault());
//        }
        return resultData;
    }

    public  ResultData invokeTree(Object poExample, Class defPoClass, Class defPoExampleClass, Object controller, HttpServletRequest request) throws ClassNotFoundException, InvocationTargetException {
        String action = "tree";
        Object po = getPoInstance(request, controller, action, new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath()),
                java.lang.Class.forName(defPoExampleClass.getClassPath())});
        ResultData resultData = DefaultController.invokeMethod(controller, action,
                new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath()),
                        java.lang.Class.forName(defPoExampleClass.getClassPath())},
                new Object[]{po, poExample});
        return resultData;
    }

    public  ResultData invokeList(boolean isRefresh, Pagination pagination, Object poExample, Class defPoClass, Class defPoExampleClass, Object controller, HttpServletRequest request, ComponentDescriptor componentDescriptor) throws Exception {
        String action = "list";
        Object po  = getPo(defPoClass, controller, action, request, componentDescriptor, new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath()),
                java.lang.Class.forName(defPoExampleClass.getClassPath()), Pagination.class});

        Map<String, String> params = BeanUtils.convertMap(po, false);
        if(isRefresh && new HashSet(Lists.newArrayList(params.values())).size() == 1 && params.values().contains(null)){
            String refererUrl = request.getHeader("referer");
            String[] refererUrlInfo = Arrays.copyOfRange(refererUrl.split("[/]+"), 2, refererUrl.split("[/]+").length);
            String module = refererUrlInfo[0];
            String pageCode = refererUrlInfo[1].substring(0, refererUrlInfo[1].indexOf(".html"));
            logger.debug("request referer : {},{},{}", refererUrl, module, pageCode);
            if(refererUrlInfo[1].indexOf("?")  > 0 &&  refererUrlInfo[1].indexOf("=")  > 0) {
                String parameters = refererUrlInfo[1].substring(refererUrlInfo[1].indexOf("?") + 1);
                String key = parameters.split("=")[0];
                String value = parameters.split("=")[1];
                org.apache.commons.beanutils.BeanUtils.setProperty(po, key, value);
                //TODO 临时处理
            }
        }

        String componentQueryString = UrlHelper.getUrlQueryString(params);
        System.out.println("=======> " + componentQueryString);
        ResultData resultData = DefaultController.invokeMethod(controller, action,
                new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath()),
                        java.lang.Class.forName(defPoExampleClass.getClassPath()), Pagination.class},
                new Object[]{po, poExample, pagination});
        return resultData;
    }

    public static void invokeSimpleDelete(String company, String program, String module, String entity, HashMap<String, Object> matchProperties) throws Exception {
        com.hframework.beans.class0.Class defPoClass = CreatorUtil.getDefPoClass(company, program, module, entity);
        Class defControllerClass = CreatorUtil.getDefControllerClass(company, program, module, entity);
        Object controller = ServiceFactory.getService(defControllerClass.getClassName().substring(0, 1).toLowerCase() + defControllerClass.getClassName().substring(1));
        Object po = java.lang.Class.forName(defPoClass.getClassPath()).newInstance();

        boolean hasCondition = false;
        if(matchProperties != null && matchProperties.size() > 0) {
            for (String propertyName : matchProperties.keySet()) {
                Object propertyValue = matchProperties.get(propertyName);
                if(propertyValue != null) {
                    final java.lang.Class propertyType = BeanUtils.getFilds(java.lang.Class.forName(defPoClass.getClassPath())).get(JavaUtil.getJavaVarName(propertyName));
                    if(propertyValue.getClass() != propertyType) {
                        if(propertyType== Long.class) {
                            propertyValue = Long.valueOf(String.valueOf(propertyValue));
                        }else if(propertyType == Integer.class) {
                            propertyValue =  Integer.valueOf(String.valueOf(propertyValue));
                        }else if(propertyType == Double.class) {
                            propertyValue =  Double.valueOf(String.valueOf(propertyValue));
                        }else if(propertyType == Byte.class) {
                            propertyValue =  Byte.valueOf(String.valueOf(propertyValue));
                        }else {
                            propertyValue =  String.valueOf(propertyValue);
                        }
                    }
                    ReflectUtils.setFieldValue(po, JavaUtil.getJavaVarName(propertyName), propertyValue);
                    hasCondition = true;

                }
            }
        }
        if(!hasCondition) {
            return;
        }
        DefaultController.invokeMethod(controller, "delete",
                new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath())},
                new Object[]{po});

    }

    public static List invokeSimpleListMethod(String company, String program, String module, String entity, HashMap<String, List<Object>> matchProperties) throws Exception {
        com.hframework.beans.class0.Class defPoClass = CreatorUtil.getDefPoClass(company, program, module, entity);
        Class defPoExampleClass = CreatorUtil.getDefPoExampleClass(company, program, module, entity);
        Class defControllerClass = CreatorUtil.getDefControllerClass(company, program, module, entity);
        Object poExample = java.lang.Class.forName(defPoExampleClass.getClassPath()).newInstance();
        Object criteria = ReflectUtils.invokeMethod(poExample, "createCriteria", new java.lang.Class[]{}, new Object[]{});

        boolean hasCondition = false;
        if(matchProperties != null && matchProperties.size() > 0) {
            for (String propertyName : matchProperties.keySet()) {
                List<Object> propertyValue = matchProperties.get(propertyName);
                if(propertyValue != null && propertyValue.size() > 0) {
                    final java.lang.Class propertyType = BeanUtils.getFilds(java.lang.Class.forName(defPoClass.getClassPath())).get(JavaUtil.getJavaVarName(propertyName));
                    if(propertyValue.get(0).getClass() != propertyType) {
                        propertyValue = CollectionUtils.fetch(propertyValue, new Fetcher<Object, Object>() {
                            public Object fetch(Object o) {
                                if(propertyType== Long.class) {
                                    return Long.valueOf(String.valueOf(o));
                                }else if(propertyType == Integer.class) {
                                    return Integer.valueOf(String.valueOf(o));
                                }else if(propertyType == Double.class) {
                                    return Double.valueOf(String.valueOf(o));
                                }else if(propertyType == Byte.class) {
                                    return Byte.valueOf(String.valueOf(o));
//                                }else if( propertyType == Date.class) {
//                                    return Date.valueOf(String.valueOf(o));
                                }else {
                                    return String.valueOf(o);
                                }
                            }
                        });
                    }

                    ReflectUtils.invokeMethod(criteria,
                            "and" + JavaUtil.getJavaClassName(propertyName) + "In",
                            new java.lang.Class[]{List.class}, new Object[]{propertyValue});
                    hasCondition = true;

                }
            }
        }
        if(!hasCondition) {
            return null;
        }

        Object controller = ServiceFactory.getService(defControllerClass.getClassName().substring(0, 1).toLowerCase() + defControllerClass.getClassName().substring(1));
        Object po = java.lang.Class.forName(defPoClass.getClassPath()).newInstance();
        ResultData resultData = DefaultController.invokeMethod(controller, "list",
                new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath()),
                        java.lang.Class.forName(defPoExampleClass.getClassPath()), Pagination.class},
                new Object[]{po, poExample, new Pagination()});
        Object data = resultData.getData();
        return (List)((Map)data).get("list");
    }

    public ResultData invokeTreeByRelDataSet(Object poExample, Pagination pagination, Class defPoClass, Class defPoExampleClass, Object controller, String action, ComponentDescriptor componentDescriptor, HttpServletRequest request) throws Exception {
        for (final String relFieldName : componentDescriptor.getDataSetDescriptor().getRelFieldKeyMap().keySet()) {
            final DataSetDescriptor dataSetDescriptor = componentDescriptor.getDataSetDescriptor().getRelDataSetMap().get(componentDescriptor.getDataSetDescriptor().getRelFieldKeyMap().get(relFieldName));
            if(dataSetDescriptor.isSelfDepend()) {
                String selfDependEventObjectCode = dataSetDescriptor.getDataSet().getEventObjectCode();
                String selfDependModule = dataSetDescriptor.getDataSet().getModule();
                Class selfDependDefPoClass = CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
                        WebContext.get().getProgram().getCode(), selfDependModule, selfDependEventObjectCode);
                Class selfDependDefPoExampleClass = CreatorUtil.getDefPoExampleClass(WebContext.get().getProgram().getCompany(),
                        WebContext.get().getProgram().getCode(), selfDependModule, selfDependEventObjectCode);
                Class selfDependDefControllerClass = CreatorUtil.getDefControllerClass(WebContext.get().getProgram().getCompany(),
                        WebContext.get().getProgram().getCode(), selfDependModule, selfDependEventObjectCode);
                Object selfDependController = ServiceFactory.getService(
                        selfDependDefControllerClass.getClassName().substring(0, 1).toLowerCase()
                                + selfDependDefControllerClass.getClassName().substring(1));
                ResultData baseResultData = DefaultController.invokeMethod(selfDependController, action,
                        new java.lang.Class[]{java.lang.Class.forName(selfDependDefPoClass.getClassPath()),
                                java.lang.Class.forName(selfDependDefPoExampleClass.getClassPath())},
                        new Object[]{getPoInstance(request, selfDependController, action,
                                new java.lang.Class[]{java.lang.Class.forName(selfDependDefPoClass.getClassPath()),
                                        java.lang.Class.forName(selfDependDefPoExampleClass.getClassPath())})
                                , java.lang.Class.forName(selfDependDefPoExampleClass.getClassPath()).newInstance()});

                Object po = getPoInstance(request, controller, "list", new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath()),
                        java.lang.Class.forName(defPoExampleClass.getClassPath()), Pagination.class});
                Map<String, String> pageFlowParams = WebContext.getDefault();
                ReflectUtils.setFieldValue(po, pageFlowParams);

                Map<String, String> params = BeanUtils.convertMap(po, false);
                String componentQueryString = UrlHelper.getUrlQueryString(params);
                System.out.println("=======> " + componentQueryString);
                ResultData reallyResultData = DefaultController.invokeMethod(controller, "list",
                        new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath()),
                                java.lang.Class.forName(defPoExampleClass.getClassPath()), Pagination.class},
                        new Object[]{po, poExample, pagination});
                Map baseTreeData = (Map) baseResultData.getData();
                final Map reallyData = CollectionUtils.convert((List) ((Map) reallyResultData.getData()).get("list"), new Mapper() {
                    public String getKey(Object o) {
                        return String.valueOf(ReflectUtils.getFieldValue(o, JavaUtil.getJavaVarName(relFieldName)));
                    }
                });

                Map resultTreeData = new LinkedHashMap();
                for (Object parentObjectId : baseTreeData.keySet()) {
                    String parentId = String.valueOf(parentObjectId);
                    resultTreeData.put(parentId, CollectionUtils.fetch((List<Object>) baseTreeData.get(parentObjectId), new Fetcher<Object, Map<String, String>>() {
                        public Map<String, String> fetch(Object o) {
                            Map<String, String> map = BeanUtils.convertMapAndFormat(o, true);
                            String keyPropertyValue = String.valueOf(ReflectUtils.getFieldValue(o, JavaUtil.getJavaVarName(dataSetDescriptor.getKeyField().getCode())));
                            String namePropertyValue = String.valueOf(ReflectUtils.getFieldValue(o, JavaUtil.getJavaVarName(dataSetDescriptor.getNameField().getCode())));
                            map.put("NAME_FIELD", namePropertyValue);
                            map.put("KEY_FIELD", keyPropertyValue);
                            map.remove(JavaUtil.getJavaVarName(dataSetDescriptor.getNameField().getCode()));
                            if(reallyData.containsKey(String.valueOf(keyPropertyValue))) {
                                map.putAll(BeanUtils.convertMapAndFormat(reallyData.get(keyPropertyValue), true));
                            }
                            return map;
                        }
                    }));
                }
                return ResultData.success(resultTreeData);
            }
        }
        return null;
    }

    public Object getPo(Class defPoClass, Object controller, String action, HttpServletRequest request, ComponentDescriptor componentDescriptor, java.lang.Class[] parameterTypes) throws Exception {
        boolean hasRelEntity = false;
        if(WebContext.getAllClassContext() != null) {
            for (java.lang.Class cacheClass : WebContext.getAllClassContext()) {
                String relFieldCode = componentDescriptor.getDataSetDescriptor().getRelFieldCode(cacheClass);
                if(StringUtils.isNoneBlank(relFieldCode)) {
                    hasRelEntity  = true;
                    break;
                }
            }
        }
        Object po ;
        if(hasRelEntity){
            po = java.lang.Class.forName(defPoClass.getClassPath()).newInstance();
            for (java.lang.Class cacheClass : WebContext.getAllClassContext()) {
                String relFieldCode = componentDescriptor.getDataSetDescriptor().getRelFieldCode(cacheClass);
                Object relObject = WebContext.get(cacheClass);
                if(StringUtils.isNoneBlank(relFieldCode) && relObject != null) {
                    String relObjectInfo = componentDescriptor.getDataSetDescriptor().getRelFieldKeyMap().get(relFieldCode);
                    Object relValue = ReflectUtils.getFieldValue(relObject, JavaUtil.getJavaVarName(relObjectInfo.substring(relObjectInfo.lastIndexOf("/") + 1)));
                    ReflectUtils.setFieldValue(po, JavaUtil.getJavaVarName(relFieldCode), relValue);
                }
            }
        }else {
            po = getPoInstance(request, controller, action, parameterTypes);
            Map<String, String> pageFlowParams = WebContext.getDefault();
            ReflectUtils.setFieldValue(po, pageFlowParams);
        }
        return po;
    }

    private Object getPoInstance(HttpServletRequest request, Object controller, String action, java.lang.Class[] defPoClass) throws ClassNotFoundException {
        //                    Object po= java.lang.Class.forName(defPoClass.getClassPath()).newInstance();

        java.lang.reflect.Method declaredMethod = ReflectUtils.getDeclaredMethod(controller, action, defPoClass);
        if(declaredMethod == null) {
            logger.warn("{}", controller,action,defPoClass);
        }
        if(modelAttributeSetter == null) modelAttributeSetter = new ModelAttributeSetter();
        Object po = modelAttributeSetter.resolveArgument(request, new MethodParameter(declaredMethod, 0));
        return po;
    }

    public  class ModelAttributeSetter{
        private ServletModelAttributeMethodProcessor processor;

        private ModelAndViewContainer mavContainer;

        private WebDataBinderFactory binderFactory;

        public ModelAttributeSetter() {
            init();
        }

        public void init() {
            this.processor = new ServletModelAttributeMethodProcessor(false);

            //调用spring-mvc中的配置的WebBindingInitializer
            this.binderFactory = new ServletRequestDataBinderFactory(null, initializer);
            this.mavContainer = new ModelAndViewContainer();
        }

        public Object resolveArgument(HttpServletRequest request, MethodParameter methodParameter) {
            NativeWebRequest webRequest = new ServletWebRequest(request);

            try {
                return this.processor.resolveArgument(
                        methodParameter,  new ModelAndViewContainer(), webRequest, this.binderFactory);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
