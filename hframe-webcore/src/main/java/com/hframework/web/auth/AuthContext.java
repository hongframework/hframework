package com.hframework.web.auth;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by zhangquanhong on 2016/11/2.
 */
public class AuthContext {

    private AuthUser user;

    private AuthManager authManager = new AuthManager();

    private AuthDataUnitRelManager authDataUnitRelManager = new AuthDataUnitRelManager();

    private AuthFunctionManager authFunctionManager = new AuthFunctionManager();

    private AuthRoleManager authRoleManager = new AuthRoleManager();

    public class AuthManager extends HashMap<Long, List<AuthDataUnit>> {

        private List<Class> authDataClass;
        private List<Class> authFunctionClass;

        //<funcId, <dataUnit, eventList>>
        private Map<Long, Map<AuthDataUnit, String>>  eventAuth  = new HashMap<Long, Map<AuthDataUnit, String>>();


        public void add(Long dataUnitId, Long functionId) {
            this.add(dataUnitId, functionId, null);
        }

        public void add(Long dataUnitId, Long functionId, String eventList) {
            if(!this.containsKey(functionId)) {
                this.put(functionId, new ArrayList<AuthDataUnit>());
            }
            if(this.eventAuth == null) {
                this.eventAuth = new HashMap<Long, Map<AuthDataUnit, String>>();
            }
            if(!this.eventAuth.containsKey(functionId)) {
                this.eventAuth.put(functionId, new HashMap<AuthDataUnit, String>());
            }

            Iterator<AuthDataUnit> iterator = this.get(functionId).iterator();
            boolean isChild = false;
            while (iterator.hasNext()) {
                AuthDataUnit next = iterator.next();
                if(authDataUnitRelManager.isChild(next.dataUnitId, dataUnitId)){
                    iterator.remove();
                }

                if(authDataUnitRelManager.isParent(next.dataUnitId, dataUnitId)){
                    return;//添加的节点对应的父节点已经在授权数据列表中，直接返回
                }
            }
            this.get(functionId).add(AuthDataUnit.valueOf(dataUnitId));

            Iterator<Map.Entry<AuthDataUnit, String>> dataEventRel = this.eventAuth.get(functionId).entrySet().iterator();
            while (dataEventRel.hasNext()) {
                Map.Entry<AuthDataUnit, String> next = dataEventRel.next();
                if(authDataUnitRelManager.isChild(next.getKey().dataUnitId, dataUnitId)){
                    if(StringUtils.isNotBlank(eventList) && eventList.trim().equals(next.getValue())) {
                        dataEventRel.remove();
                    }
                }

                if(authDataUnitRelManager.isParent(next.getKey().dataUnitId, dataUnitId)){
                    if(StringUtils.isNotBlank(eventList) && eventList.trim().equals(next.getValue())) {
                        return;//添加的节点对应的父节点已经在授权数据列表中，直接返回
                    }
                }
            }
            if(StringUtils.isNotBlank(eventList)) {
                eventAuth.get(functionId).put(AuthDataUnit.valueOf(dataUnitId), eventList);
            }

        }

        public List<Long> getDataUnitIds(Long functionId) {
            Set<Long> dataUnitIds = new HashSet<Long>();
            if(this.containsKey(functionId)) {
                List<AuthDataUnit> authDataUnits = this.get(functionId);
                for (AuthDataUnit authDataUnit : authDataUnits) {
                    dataUnitIds.add(authDataUnit.dataUnitId);
                    dataUnitIds.addAll(authDataUnitRelManager.getChildren(authDataUnit.dataUnitId));

                }
                return Lists.newArrayList(dataUnitIds);
            }
            return Lists.newArrayList();
        }

        public List<Class> getAuthDataClass() {
            return authDataClass;
        }

        public void setAuthDataClass(List<Class> authDataClass) {
            this.authDataClass = authDataClass;
        }

        public void addAuthDataClass(Class authDataClass) {
            if(this.authDataClass == null) {
                synchronized (this) {
                    if(this.authDataClass == null) {
                        this.authDataClass = new ArrayList<Class>();
                    }
                }
            }
            this.authDataClass.add(authDataClass);
        }

        public List<Class> getAuthFunctionClass() {
            return authFunctionClass;
        }

        public void setAuthFunctionClass(List<Class> authFunctionClass) {
            this.authFunctionClass = authFunctionClass;
        }

        public void addAuthFunctionClass(Class authFunctionClass) {
            if(this.authFunctionClass == null) {
                synchronized (this) {
                    if(this.authFunctionClass == null) {
                        this.authFunctionClass = new ArrayList<Class>();
                    }
                }
            }
            this.authFunctionClass.add(authFunctionClass);
        }

        public Map<Long, Map<AuthDataUnit, String>> getEventAuth() {
            return eventAuth;
        }

        public void setEventAuth(Map<Long, Map<AuthDataUnit, String>> eventAuth) {
            this.eventAuth = eventAuth;
        }
    }

    public class AuthDataUnitRelManager extends HashMap<Long, Set<Long>> {

        private Map<Long, Set<Long>> subMap = new HashMap<Long, Set<Long>>();

        public void add(Long subAuthDataUnitId, Long parentAuthDataUnitId) {
            if(!this.containsKey(subAuthDataUnitId)) {
                this.put(subAuthDataUnitId, new HashSet<Long>());
            }
            this.get(subAuthDataUnitId).add(parentAuthDataUnitId);
        }

        public void addAll(Long subAuthDataUnitId, Set<Long> parentAuthDataUnitIds) {
            if(!this.containsKey(subAuthDataUnitId)) {
                this.put(subAuthDataUnitId, new HashSet<Long>());
            }
            this.get(subAuthDataUnitId).addAll(parentAuthDataUnitIds);
        }

        public boolean isChild(Long currentDataUnitId, Long targetDataUnitId) {
            return this.get(currentDataUnitId).contains(targetDataUnitId);
        }

        public boolean isParent(Long currentDataUnitId, Long targetDataUnitId) {
            return this.get(targetDataUnitId).contains(currentDataUnitId);
        }

        public Set<Long> getChildren(Long parentAuthDataUnitId) {
            Set<Long> children = new HashSet<Long>();

            for (Map.Entry<Long, Set<Long>> longSetEntry : this.entrySet()) {
                if(longSetEntry.getValue().contains(parentAuthDataUnitId)) {
                    children.add(longSetEntry.getKey());
                }
            }
            return children;
        }

    }

    public class AuthRoleManager extends HashMap<String, List<AuthDataUnit>> {
        private List allRoles ;
        private Map<String, String> roleIdNameMap;

        public void add(Long dataUnitId, String roleId) {
            if(!this.containsKey(roleId)) {
                this.put(roleId, new ArrayList<AuthDataUnit>());
            }

            Iterator<AuthDataUnit> iterator = this.get(roleId).iterator();
            boolean isChild = false;
            while (iterator.hasNext()) {
                AuthDataUnit next = iterator.next();
                if(authDataUnitRelManager.isChild(next.dataUnitId, dataUnitId)){
                    iterator.remove();
                }

                if(authDataUnitRelManager.isParent(next.dataUnitId, dataUnitId)){
                    return;//添加的节点对应的父节点已经在授权数据列表中，直接返回
                }
            }

            this.get(roleId).add(AuthDataUnit.valueOf(dataUnitId));
        }

        public List<Long> getDataUnitIds(String roleId) {
            Set<Long> dataUnitIds = new HashSet<Long>();
            if(this.containsKey(roleId)) {
                List<AuthDataUnit> authDataUnits = this.get(roleId);
                for (AuthDataUnit authDataUnit : authDataUnits) {
                    dataUnitIds.add(authDataUnit.dataUnitId);
                    dataUnitIds.addAll(authDataUnitRelManager.getChildren(authDataUnit.dataUnitId));

                }
                return Lists.newArrayList(dataUnitIds);
            }
            return Lists.newArrayList();
        }

        public List getAllRoles() {
            return allRoles;
        }

        public void setAllRoles(List allRoles) {
            this.allRoles = allRoles;
        }

        public Map<String, String> getRoleIdNameMap() {
            if(roleIdNameMap == null) roleIdNameMap = new HashMap<String, String>();
            return roleIdNameMap;
        }

        public void setRoleIdNameMap(Map<String, String> roleIdNameMap) {
            this.roleIdNameMap = roleIdNameMap;
        }
    }

    public class AuthFunctionManager extends HashMap<String, Long> {
        private List allFunctions ;

        public List getAllFunctions() {
            return allFunctions;
        }

        public void setAllFunctions(List allFunctions) {
            this.allFunctions = allFunctions;
        }
    }

    public static class AuthUser{
        public static Class userClass;
        public Object userObject;
        public Long userId;

        public static AuthUser valueOf(Object object) {
            AuthUser authUser = new AuthUser();
            authUser.userClass = object.getClass();
            authUser.userObject = object;
            return  authUser;
        }
    }

    public static class AuthDataUnit{
        public Class dataUnitClass;
        public Object dataUnitObject;
        public Long dataUnitId;

        public static AuthDataUnit valueOf(Long authDataId) {
            AuthDataUnit authDataUnit = new AuthDataUnit();
            authDataUnit.dataUnitId = authDataId;
            return  authDataUnit;
        }

        public static AuthDataUnit valueOf(Object object) {
            AuthDataUnit authDataUnit = new AuthDataUnit();
            authDataUnit.dataUnitClass = object.getClass();
            authDataUnit.dataUnitObject = object;
            return  authDataUnit;
        }
    }

    public static class AuthDataUnitRel{
        public Class dataUnitClass;
        public Object dataUnitObject;
        public Long dataUnitId;

        public static AuthDataUnit valueOf(Object object) {
            AuthDataUnit authDataUnit = new AuthDataUnit();
            authDataUnit.dataUnitClass = object.getClass();
            authDataUnit.dataUnitObject = object;
            return  authDataUnit;
        }
    }

    public AuthUser getUser() {
        return user;
    }

    public void setUser(Object user) {
        this.user = AuthUser.valueOf(user);
    }

    public AuthManager getAuthManager() {
        return authManager;
    }

    public void setAuthManager(AuthManager authManager) {
        this.authManager = authManager;
    }

    public AuthDataUnitRelManager getAuthDataUnitRelManager() {
        return authDataUnitRelManager;
    }

    public void setAuthDataUnitRelManager(AuthDataUnitRelManager authDataUnitRelManager) {
        this.authDataUnitRelManager = authDataUnitRelManager;
    }

    public AuthFunctionManager getAuthFunctionManager() {
        return authFunctionManager;
    }

    public void setAuthFunctionManager(AuthFunctionManager authFunctionManager) {
        this.authFunctionManager = authFunctionManager;
    }

    public AuthRoleManager getAuthRoleManager() {
        return authRoleManager;
    }

    public void setAuthRoleManager(AuthRoleManager authRoleManager) {
        this.authRoleManager = authRoleManager;
    }
}
