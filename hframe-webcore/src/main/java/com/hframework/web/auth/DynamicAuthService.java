package com.hframework.web.auth;

import com.google.common.collect.Lists;
import com.hframework.common.util.collect.CollectionUtils;
import com.hframework.common.util.collect.bean.Fetcher;
import com.hframework.common.frame.ServiceFactory;
import com.hframework.common.util.*;
import com.hframework.web.CreatorUtil;
import com.hframework.web.SessionKey;
import com.hframework.web.context.DataSetDescriptor;
import com.hframework.web.context.WebContext;
import com.hframework.web.config.bean.Program;
import com.hframework.web.config.bean.dataset.Field;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 *
 * 用户：hfsec_user(管理添加)
 * 用户授权：hfsec_user_authorize(管理添加)
 * 角色：hfsec_role(管理添加)
 * 角色授权：hfsec_role_authorize(管理添加)
 * 功能：hfsec_menu(管理添加)
 * 数据单元：hfsec_organize(管理添
 * 用户  用户授权
 *
 * 角色 角色授权
 *
 * 组织
 *
 * 用户:student, teacher（管理添加）
 * 用户授权：hfsec_user_authorize（默认分配）
 * 角色：role(管理添加)
 * 角色授权：role_authorize(管理添加)
 * 功能：menu
 * 数据单元：college specialty grade class
 *
 * 用户:user（用户注册）
 * 用户授权：hfsec_user_authorize（默认分配）
 * 角色：role(管理添加)
 * 角色授权：role_authorize(管理添加)
 * 功能：menu
 * 数据单元：group（默认分配，辅助管理）
 *
 *
 * Created by zhangquanhong on 2016/11/2.
 */
@Service
public class DynamicAuthService implements AuthService {

    private  String authUserImpl = null;//PropertyConfigurerUtils.getProperty("hframe.auth.user.impl");
    private  String authDataImpl = null;//PropertyConfigurerUtils.getProperty("hframe.auth.data.impl");
    private  String authRoleImpl = null;//PropertyConfigurerUtils.getProperty("hframe.auth.data.impl");
    private  String authFuncImpl = null;//PropertyConfigurerUtils.getProperty("hframe.auth.func.impl");
    private  String authUserDataImpl = null;//PropertyConfigurerUtils.getProperty("hframe.auth.user.data.impl");
    private  String authUserFuncImpl = null;//PropertyConfigurerUtils.getProperty("hframe.auth.user.func.impl");

    //管理员默认权限实例
    private  String adminDefaultAuthImpl = null;//PropertyConfigurerUtils.getProperty("hframe.admin.default.auth.impl");
    //管理员默认权限字段（域）
    private  String adminDefaultAuthFiledName = null;//PropertyConfigurerUtils.getProperty("hframe.admin.default.auth.filed.name");
    //管理员默认权限字段值
    private  String adminDefaultAuthFiledValue = null;//PropertyConfigurerUtils.getProperty("hframe.admin.default.auth.filed.value");

    public AuthContext initAuthContext(HttpServletRequest request) throws Exception {
        AuthContext context = new AuthContext();

        Program program = WebContext.get().getProgram();
        authUserImpl = program.getAuthInstance().getUser();
        authDataImpl = program.getAuthInstance().getData();
        authRoleImpl = program.getAuthInstance().getRole();
        authFuncImpl = program.getAuthInstance().getFunction();
        authUserDataImpl = program.getAuthInstance().getUserDataAuth();
        authUserFuncImpl = program.getAuthInstance().getUserFuncAuth();
        adminDefaultAuthImpl = program.getAuthInstance().getSuperAuthFilter().getDataSet();
        adminDefaultAuthFiledName = program.getAuthInstance().getSuperAuthFilter().getDataField();
        adminDefaultAuthFiledValue = program.getAuthInstance().getSuperAuthFilter().getDataFieldValue();

        //添加用户实体信息
        addUser(context, request);

        //添加数据单元包含关系
        addDataUnitRels(context);

        //添加功能
        addFunctions(context);

        //添加用户的所有权限（数据与功能对应）
        addAuthRelation(context);

        //添加角色
        addRoles(context);

        //添加用户所有的角色 （作废，合并进入addAuthRelation（））
        addRoleRelation(context);


        request.getSession().setAttribute(SessionKey.AUTH, context);

        return context;
    }

    private void addRoleRelation(AuthContext context) {

    }

    private void addRoles(AuthContext context) throws Exception {
        String[] authRoleImpls = RegexUtils.split(authRoleImpl, "[ ]*[;,]+[ ]*");
        for (String authRoleImpl : authRoleImpls) {
            String moduleCode = authRoleImpl.substring(0, authRoleImpl.indexOf("."));
            String dataSetCode = authRoleImpl.substring(authRoleImpl.indexOf(".") + 1);
            Class<?> defPoClass = Class.forName(CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
                    WebContext.get().getProgram().getCode(), moduleCode, dataSetCode).getClassPath());
            DataSetDescriptor dataSet = WebContext.get().getDataSet(defPoClass);
            List list = getAll(moduleCode, dataSetCode);
            context.getAuthRoleManager().roleClass = defPoClass;
            for (Object roleObject : list) {
                Long roleId = Long.parseLong(org.apache.commons.beanutils.BeanUtils.getProperty(
                        roleObject, JavaUtil.getJavaVarName(dataSet.getKeyField().getCode())));
                String roleName = String.valueOf(org.apache.commons.beanutils.BeanUtils.getProperty(
                        roleObject, JavaUtil.getJavaVarName(dataSet.getNameField().getCode())));
                context.getAuthRoleManager().getRoleIdNameMap().put(String.valueOf(roleId), roleName);
            }
            context.getAuthRoleManager().setAllRoles(list);
        }
    }

    private Long getAdminAuthKeyValue() throws Exception {

        Long adminAuthKeyValue = -1L;
        String moduleCode = adminDefaultAuthImpl.substring(0, adminDefaultAuthImpl.indexOf("."));
        String dataSetCode = adminDefaultAuthImpl.substring(adminDefaultAuthImpl.indexOf(".") + 1);

        List all = getAll(moduleCode, dataSetCode);
        for (Object o : all) {
            if(adminDefaultAuthFiledValue.equals(
                    ReflectUtils.getFieldValue(o, JavaUtil.getJavaVarName(adminDefaultAuthFiledName)))){
                adminAuthKeyValue = (Long) getObjectKeyPropertyValue(o);
            }
        }
        return adminAuthKeyValue;
    }

    private Class<?> getAdminAuthClass() throws Exception {

        String moduleCode = adminDefaultAuthImpl.substring(0, adminDefaultAuthImpl.indexOf("."));
        String dataSetCode = adminDefaultAuthImpl.substring(adminDefaultAuthImpl.indexOf(".") + 1);
        Class<?> adminAuthClass = Class.forName(CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
                WebContext.get().getProgram().getCode(), moduleCode, dataSetCode).getClassPath());
        return adminAuthClass;
    }

    private void addAuthRelation(AuthContext context) throws Exception {
        Class<?> adminAuthClass = getAdminAuthClass();
        Long adminAuthKeyValue = getAdminAuthKeyValue();

        String[] authUserDataArray = filterLongUserClass(context, RegexUtils.split(authUserDataImpl, "[ ]*[,]+[ ]*"));
        String[] authUserFuncArray = filterLongUserClass(context, RegexUtils.split(authUserFuncImpl, "[ ]*[,]+[ ]*"));
        for (String authUserData1 : authUserDataArray) {
            for (String authUserFunc1 : authUserFuncArray) {
                String[] authUserDataImpls =  RegexUtils.split(authUserData1, "[ ]*[/]+[ ]*");
                String[] authUserFuncImpls =  RegexUtils.split(authUserFunc1, "[ ]*[/]+[ ]*");

                authUserDataImpls = Arrays.copyOfRange(authUserDataImpls, 1, authUserDataImpls.length - 1);
                authUserFuncImpls = Arrays.copyOfRange(authUserFuncImpls, 1, authUserFuncImpls.length - 1);
                if(!checkDataFuncInclude(authUserDataImpls, authUserFuncImpls)) {
                    return;
                }

                String authUserDataImpl = authUserFuncImpls[authUserDataImpls.length];
                if(StringUtils.isBlank(authRoleImpl))  authRoleImpl = authUserDataImpl;
                String[] dataSets = {authUserDataImpl};// RegexUtils.split(authUserDataImpl, "[ ]*[,]+[ ]*");
                String moduleCode = dataSets[0].substring(0, dataSets[0].indexOf("."));
                String dataSetCode = dataSets[0].substring(dataSets[0].indexOf(".") + 1);
                Class<?> funcRootPoClass = Class.forName(CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
                        WebContext.get().getProgram().getCode(), moduleCode, dataSetCode).getClassPath());

                DataSetDescriptor userDataSet = WebContext.get().getDataSet(context.getUser().userClass);
                Long userId = Long.parseLong(org.apache.commons.beanutils.BeanUtils.getProperty(
                        context.getUser().userObject, JavaUtil.getJavaVarName(userDataSet.getKeyField().getCode())));

                List authUserDatas = getEndPointListFromRoot(context.getUser().userClass, Lists.newArrayList(userId), authUserDataImpls);
                for (Object authUserData : authUserDatas) {
                    DataSetDescriptor dataSet = WebContext.get().getDataSet(authUserData.getClass());
                    List<String> relFieldCodes = dataSet.getRelFieldCodes(context.getAuthManager().getAuthDataClass());
                    List filedValues = new ArrayList();
                    for (String relFieldCode : relFieldCodes) {
                        Object fieldValue = ReflectUtils.getFieldValue(authUserData, JavaUtil.getJavaVarName(relFieldCode));
                        filedValues.add(fieldValue);
                    }

                    String adminAuthClassFieldName = dataSet.getRelFieldCode(adminAuthClass);

                    //添加角色关系
                    for (Object filedValue : filedValues) {
                        context.getAuthRoleManager().add((Long) filedValue, String.valueOf(ReflectUtils.getFieldValue(authUserData, JavaUtil.getJavaVarName(adminAuthClassFieldName))));
                    }

                    if(adminAuthKeyValue.equals(ReflectUtils.getFieldValue(authUserData, JavaUtil.getJavaVarName(adminAuthClassFieldName)))) {
                        List allFunctions = context.getAuthFunctionManager().getAllFunctions();
                        for (Object authUserFunc : allFunctions) {
                            Long funcId = (Long) getObjectKeyPropertyValue(authUserFunc);
                            for (Object filedValue : filedValues) {
                                context.getAuthManager().add((Long) filedValue, funcId);
                            }
                        }

                        return ;
                    }else {
                        List authUserFuncs;
                        String relFieldCode = dataSet.getRelFieldCode(funcRootPoClass);
                        if(StringUtils.isNotBlank(relFieldCode)) {
                            Long fieldValue = (Long) ReflectUtils.getFieldValue(authUserData, JavaUtil.getJavaVarName(relFieldCode));
                            authUserFuncs = getEndPointListFromRoot(null,
                                    Lists.newArrayList(fieldValue),
                                    Arrays.copyOfRange(authUserFuncImpls, authUserDataImpls.length, authUserFuncImpls.length));
                        }else {
                            authUserFuncs = getEndPointListFromRoot(authUserData.getClass(),
                                    Lists.newArrayList((Long) getObjectKeyPropertyValue(authUserData)),
                                    Arrays.copyOfRange(authUserFuncImpls, authUserDataImpls.length, authUserFuncImpls.length));
                        }

                        for (Object authUserFunc : authUserFuncs) {
                            DataSetDescriptor dataSet1 = WebContext.get().getDataSet(authUserFunc.getClass());
                            String urlFieldCode = dataSet1.getUrlFieldCode("/frame/getEventsByFuncId.json");
                            String eventList = null;
                            if(StringUtils.isNotBlank(urlFieldCode)) {
                                String relPropertyName = JavaUtil.getJavaVarName(urlFieldCode);
                                eventList = (String) ReflectUtils.getFieldValue(authUserFunc, relPropertyName);
                            }
                            List<Class> authFunctionClass = context.getAuthManager().getAuthFunctionClass();
                            List<String> relFieldCodes1 = dataSet1.getRelFieldCodes(authFunctionClass);
                            for (String relFieldCode1 : relFieldCodes1) {
                                String relPropertyName = JavaUtil.getJavaVarName(relFieldCode1);
                                Long funcId = (Long)  ReflectUtils.getFieldValue(authUserFunc, relPropertyName);
                                for (Object filedValue : filedValues) {
                                    context.getAuthManager().add((Long) filedValue, funcId, eventList);
                                }
                            }
                        }
                    }


                }
            }
        }


    }

    private String[] filterLongUserClass(AuthContext context, String[] split) throws Exception {
        List<String> result = new ArrayList();
        for (String authUserDatas : split) {
            String[] authUserDataImpls = RegexUtils.split(authUserDatas, "[ ]*[/]+[ ]*");

            String moduleCode = authUserDataImpls[0].substring(0, authUserDataImpls[0].indexOf("."));
            String dataSetCode = authUserDataImpls[0].substring(authUserDataImpls[0].indexOf(".") + 1);
            Class<?> rootClass = Class.forName(CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
                    WebContext.get().getProgram().getCode(), moduleCode, dataSetCode).getClassPath());
            if(rootClass == context.getUser().userClass) {
                result.add(authUserDatas);
            }
        };
        return result.toArray(new String[0]);
    }

    private Object getObjectKeyPropertyValue(Object authUserData) {
        String keyPropertyName = JavaUtil.getJavaVarName(
                WebContext.get().getDataSet(authUserData.getClass()).getKeyField().getCode());
        return  ReflectUtils.getFieldValue(authUserData, keyPropertyName);
    }

    private static List getEndPointListFromRoot(Class<?> parentPoClass, List<Long> parentPoIds, String[] dataSets) throws Exception {
        List result = new ArrayList();
        for (int i = 0; i < dataSets.length; i++) {
            boolean isEndPoint = dataSets.length-1 == i;
            String[] dataSetss = RegexUtils.split(dataSets[i], "[ ]*[,]+[ ]*");
            List<Long> childPoIds = new ArrayList<Long>();
            Class<?> lastChildPoClass = null;
            for (String dataSet : dataSetss) {
                String moduleCode = dataSet.substring(0, dataSet.indexOf("."));
                String dataSetCode = dataSet.substring(dataSet.indexOf(".") + 1);
                Class<?> childPoClass = Class.forName(CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
                        WebContext.get().getProgram().getCode(), moduleCode, dataSetCode).getClassPath());
                DataSetDescriptor childDataSet = WebContext.get().getDataSet(childPoClass);
                Class<?> childPoExampleClass = Class.forName(CreatorUtil.getDefPoExampleClass(WebContext.get().getProgram().getCompany(),
                        WebContext.get().getProgram().getCode(), moduleCode, dataSetCode).getClassPath());
                Class<?> childServiceImplClass = Class.forName(CreatorUtil.getDefServiceImplClass(WebContext.get().getProgram().getCompany(),
                        WebContext.get().getProgram().getCode(), moduleCode, dataSetCode).getClassPath());
                String relFieldCode ;
                if(parentPoClass == null) {
                    relFieldCode = childDataSet.getKeyField().getCode();
                }else {
                    relFieldCode = childDataSet.getRelFieldCode(parentPoClass);
                }

                if(StringUtils.isNotBlank(relFieldCode)) {

                    Object service = ServiceFactory.getService(childServiceImplClass);
                    final Object example = childPoExampleClass.newInstance();

                    Object newCriteria = ReflectUtils.invokeMethod(example, "createCriteria", new Class[]{}, new Object[]{});
                    ReflectUtils.invokeMethod(newCriteria, "and" + JavaUtil.getJavaClassName(relFieldCode)+ "In",
                            new Class[]{List.class}, new Object[]{parentPoIds});

                    if(childDataSet.getFields().containsKey("status")) {
                        Field field = childDataSet.getFields().get("status");
                        if(field.getEnumClass() != null && StringUtils.isNotBlank(field.getEnumClass().getCode())) {
                            ReflectUtils.invokeMethod(newCriteria, "andStatusEqualTo",
                                    new Class[]{Byte.class}, new Object[]{(byte)1});
                        }
                    }
                    List list = (List) ReflectUtils.invokeMethod(service,
                            "get" + JavaUtil.getJavaClassName(dataSetCode)
                                    + "ListByExample", new Class[]{childPoExampleClass},new Object[]{example});

                    if(isEndPoint) {
                        result.addAll(list);
                    }else {
                        final String keyPropertyName = JavaUtil.getJavaVarName(childDataSet.getKeyField().getCode());
                        childPoIds.addAll(CollectionUtils.fetch(list, new Fetcher() {
                            public Object fetch(Object o) {
                                return ReflectUtils.getFieldValue(o, keyPropertyName);
                            }
                        }));
                    }
                    lastChildPoClass = childPoClass;
                }
            }
            parentPoClass = lastChildPoClass;
            parentPoIds = childPoIds;
        }

        return result;

    }


    private boolean checkDataFuncInclude(String[] authUserDataImpls, String[] authUserFuncImpls) {
        if(authUserDataImpls.length > authUserDataImpls.length) return false;
        for (int i = 0; i < authUserDataImpls.length; i++) {
            if(!authUserDataImpls[i].replaceAll("[ ]+", "").equals(authUserFuncImpls[i].replaceAll("[ ]+", ""))) {
                return false;
            }
        }
        return true;
    }

    private void addFunctions(AuthContext context) throws Exception {
        String[] authFuncImpls = RegexUtils.split(authFuncImpl, "[ ]*[;,]+[ ]*");
        for (String authFuncImpl : authFuncImpls) {
            String moduleCode = authFuncImpl.substring(0, authFuncImpl.indexOf("."));
            String dataSetCode = authFuncImpl.substring(authFuncImpl.indexOf(".") + 1);
            Class<?> defPoClass = Class.forName(CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
                    WebContext.get().getProgram().getCode(), moduleCode, dataSetCode).getClassPath());
            DataSetDescriptor dataSet = WebContext.get().getDataSet(defPoClass);
            List list = getAll(moduleCode, dataSetCode);
            for (Object funcObject : list) {
                Long funcId = Long.parseLong(org.apache.commons.beanutils.BeanUtils.getProperty(
                        funcObject, JavaUtil.getJavaVarName(dataSet.getKeyField().getCode())));
                String url = String.valueOf(org.apache.commons.beanutils.BeanUtils.getProperty(
                        funcObject, "url"));

                if(!context.getAuthFunctionManager().containsKey(url)) {
                    context.getAuthFunctionManager().put(url, funcId);
                }

            }
            context.getAuthManager().addAuthFunctionClass(defPoClass);
            context.getAuthFunctionManager().setAllFunctions(list);
        }
    }

    private void addDataUnitRels(AuthContext context) throws Exception {
        //设置数据单元关联信息
        String[] dataUnitImpls = RegexUtils.split(authDataImpl, "[ ]*[;,]+[ ]*");
        for (String dataUnitImpl : dataUnitImpls) {
            String moduleCode = dataUnitImpl.substring(0, dataUnitImpl.indexOf("."));
            String dataSetCode = dataUnitImpl.substring(dataUnitImpl.indexOf(".") + 1);
            Class<?> defPoClass = Class.forName(CreatorUtil.getDefPoClass(WebContext.get().getProgram().getCompany(),
                    WebContext.get().getProgram().getCode(), moduleCode, dataSetCode).getClassPath());
            Class<?> defPoExampleClass = Class.forName(CreatorUtil.getDefPoExampleClass(WebContext.get().getProgram().getCompany(),
                    WebContext.get().getProgram().getCode(), moduleCode, dataSetCode).getClassPath());
            Class<?> serviceImplClass = Class.forName(CreatorUtil.getDefServiceImplClass(WebContext.get().getProgram().getCompany(),
                    WebContext.get().getProgram().getCode(), moduleCode, dataSetCode).getClassPath());

            Object service = ServiceFactory.getService(serviceImplClass);
            DataSetDescriptor dataSet = WebContext.get().getDataSet(defPoClass);
            if(dataSet.isSelfDepend()) {
                Map<Long, List> treeMap = (Map<Long, List>) ReflectUtils.invokeMethod(service,
                        "get" + JavaUtil.getJavaClassName(dataSetCode)
                                + "TreeByParentId", new Class[]{defPoClass, defPoExampleClass},
                        new Object[]{defPoClass.newInstance(), null});
                setAuthDataUnitRelFromDataUnitTree(treeMap.get(-1l), context, treeMap, dataSet);
            }else {
                List list = getAll(moduleCode, dataSetCode);
            }
            context.getAuthManager().addAuthDataClass(defPoClass);
        }


    }

    private List getAll(String moduleCode, String dataSetCode) throws Exception {
        Class<?> serviceImplClass = Class.forName(CreatorUtil.getDefServiceImplClass(WebContext.get().getProgram().getCompany(),
                WebContext.get().getProgram().getCode(), moduleCode, dataSetCode).getClassPath());

        Object service = ServiceFactory.getService(serviceImplClass);

        return (List) ReflectUtils.invokeMethod(service,
                "get" + JavaUtil.getJavaClassName(dataSetCode) + "All",
                new Class[]{}, new Object[]{});
    }

    private Object addUser(AuthContext context, HttpServletRequest request) {
        Object user =  request.getSession().getAttribute(SessionKey.USER);
        context.setUser(user);
        return user;
    }

    public List<Long> getFunctionIds(HttpServletRequest request) throws Exception {
        AuthContext context = (AuthContext) request.getSession().getAttribute(SessionKey.AUTH);
        Set<Long> functionIds = context.getAuthManager().keySet();
        return Lists.newArrayList(functionIds);
    }

    public AuthContext getAuthContext(HttpServletRequest request) throws Exception {
        AuthContext context = (AuthContext) request.getSession().getAttribute(SessionKey.AUTH);
        return context;
    }

    private void setAuthDataUnitRelFromDataUnitTree(List dataUnits, AuthContext context, Map<Long, List> treeMap, DataSetDescriptor dataSet) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        if(dataUnits == null || dataUnits.size() == 0) return;

        for (Object dataUnit : dataUnits) {

            Long dataUnitId = Long.parseLong(org.apache.commons.beanutils.BeanUtils.getProperty(
                    dataUnit, JavaUtil.getJavaVarName(dataSet.getKeyField().getCode())));
            Long parentDataUnitId = Long.parseLong(org.apache.commons.beanutils.BeanUtils.getProperty(
                    dataUnit, JavaUtil.getJavaVarName(dataSet.getSelfDependPropertyName())));

            //如果存在上级元素，将上级元素的值放入其中
            if(context.getAuthDataUnitRelManager().containsKey(parentDataUnitId)) {
                context.getAuthDataUnitRelManager().addAll(dataUnitId,
                        context.getAuthDataUnitRelManager().get(parentDataUnitId));
            }
            context.getAuthDataUnitRelManager().add(dataUnitId, parentDataUnitId);
            setAuthDataUnitRelFromDataUnitTree(treeMap.get(dataUnitId), context, treeMap, dataSet);
        }
    }
}
