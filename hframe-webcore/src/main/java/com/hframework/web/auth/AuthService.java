package com.hframework.web.auth;

import com.hframework.web.auth.AuthContext;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by zhangquanhong on 2016/11/7.
 */
public interface AuthService {

    /**
     * 获取系统菜单类
     * @return
     * @throws Exception
     */
    public List<Class> getFunctionClasses() throws Exception;

    public AuthContext initAuthContext(HttpServletRequest request) throws Exception;

    public List<Long> getFunctionIds(HttpServletRequest request) throws Exception;

    public AuthContext getAuthContext(HttpServletRequest request) throws Exception;
}
