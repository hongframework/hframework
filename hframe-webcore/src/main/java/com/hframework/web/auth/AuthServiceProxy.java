package com.hframework.web.auth;

import com.hframework.common.frame.ServiceFactory;
import com.hframework.common.springext.properties.PropertyConfigurerUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by zhangquanhong on 2016/11/7.
 */
@Service
public class AuthServiceProxy{

    private  String autoServiceClassPath = PropertyConfigurerUtils.getProperty("hframe.auth.service.impl");

    public AuthContext auth(HttpServletRequest request) throws Exception {
        AuthService service = (AuthService) ServiceFactory.getService(Class.forName(autoServiceClassPath));
        return  service.initAuthContext(request);
    };

    /**
     * 获取系统菜单类
     *
     * @return
     * @throws Exception
     */
    public List<Class> getFunctionClasses() throws Exception {
        AuthService service = (AuthService) ServiceFactory.getService(Class.forName(autoServiceClassPath));
        return service.getFunctionClasses();
    }

    public List<Long> getFunctionIds(HttpServletRequest request) throws Exception {


        AuthService service = (AuthService) ServiceFactory.getService(Class.forName(autoServiceClassPath));
        return  service.getFunctionIds(request);
    };

    public AuthContext getAuthContext(HttpServletRequest request) throws Exception {
        AuthService service = (AuthService) ServiceFactory.getService(Class.forName(autoServiceClassPath));
        return  service.getAuthContext(request);
    }


}
