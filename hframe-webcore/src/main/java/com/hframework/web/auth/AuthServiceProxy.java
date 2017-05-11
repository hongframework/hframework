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
public class AuthServiceProxy {

    private  String autoServiceClassPath = PropertyConfigurerUtils.getProperty("hframe.auth.service.impl");

    public AuthContext auth(HttpServletRequest request) throws Exception {
        AuthService service = (AuthService) ServiceFactory.getService(Class.forName(autoServiceClassPath));
        return  service.initAuthContext(request);
    };

    public List<Long> getFunctionIds(HttpServletRequest request) throws Exception {


        AuthService service = (AuthService) ServiceFactory.getService(Class.forName(autoServiceClassPath));
        return  service.getFunctionIds(request);
    };

    public AuthContext getAuthContext(HttpServletRequest request) throws Exception {
        AuthService service = (AuthService) ServiceFactory.getService(Class.forName(autoServiceClassPath));
        return  service.getAuthContext(request);
    }


}
