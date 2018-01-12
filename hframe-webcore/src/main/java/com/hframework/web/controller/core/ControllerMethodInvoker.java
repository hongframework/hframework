package com.hframework.web.controller.core;

import com.hframework.beans.class0.Class;
import com.hframework.beans.controller.Pagination;
import com.hframework.beans.controller.ResultData;
import com.hframework.common.frame.ServiceFactory;
import com.hframework.common.util.BeanUtils;
import com.hframework.common.util.JavaUtil;
import com.hframework.common.util.ReflectUtils;
import com.hframework.common.util.UrlHelper;
import com.hframework.common.util.collect.CollectionUtils;
import com.hframework.common.util.collect.bean.Fetcher;
import com.hframework.common.util.collect.bean.Mapper;
import com.hframework.web.CreatorUtil;
import com.hframework.web.context.ComponentDescriptor;
import com.hframework.web.context.DataSetDescriptor;
import com.hframework.web.context.WebContext;
import com.hframework.web.controller.DefaultController;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangquanhong on 2018/1/11.
 */
@Service
public class ControllerMethodInvoker {

    private static final Logger logger = LoggerFactory.getLogger(ControllerMethodInvoker.class);

    @Resource
    private ConfigurableWebBindingInitializer initializer;


    private ModelAttributeSetter modelAttributeSetter ;

    public  ResultData invokeDetail(Class defPoClass, Object controller, HttpServletRequest request) throws ClassNotFoundException, InvocationTargetException {
        String action = "detail";
        Object po = getPoInstance(request, controller, action, new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath())});
        Map<String, String> pageFlowParams = WebContext.get(HashMap.class.getName());
        ReflectUtils.setFieldValue(po, pageFlowParams);
        ResultData resultData = DefaultController.invokeMethod(controller, action,
                new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath())},
                new Object[]{po});
        //将页面传过来的参数覆盖原先的值，主要用于具体的form表单提交时需要附带一些参数值
        if(resultData.getData() != null) {
            ReflectUtils.setFieldValue(resultData.getData(), pageFlowParams);
        }
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

    public  ResultData invokeList(Pagination pagination, Object poExample, Class defPoClass, Class defPoExampleClass, Object controller, HttpServletRequest request) throws ClassNotFoundException, InvocationTargetException {
        String action = "list";
        Object po = getPoInstance(request, controller, action, new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath()),
                java.lang.Class.forName(defPoExampleClass.getClassPath()), Pagination.class});
        Map<String, String> pageFlowParams = WebContext.get(HashMap.class.getName());
        ReflectUtils.setFieldValue(po, pageFlowParams);

        Map<String, String> params = BeanUtils.convertMap(po, false);
        String componentQueryString = UrlHelper.getUrlQueryString(params);
        System.out.println("=======> " + componentQueryString);
        ResultData resultData = DefaultController.invokeMethod(controller, action,
                new java.lang.Class[]{java.lang.Class.forName(defPoClass.getClassPath()),
                        java.lang.Class.forName(defPoExampleClass.getClassPath()), Pagination.class},
                new Object[]{po, poExample, pagination});
        return resultData;
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
                Map<String, String> pageFlowParams = WebContext.get(HashMap.class.getName());
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
