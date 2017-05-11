package com.hframe.activiti.ext.filter;

import org.activiti.explorer.filter.ExplorerFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by zhangquanhong on 2016/11/30.
 */
public class ActivitiExplorerFilter extends ExplorerFilter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI().substring(req.getContextPath().length());
        int indexSlash = path.indexOf("/", 1);
        String firstPart = null;
        if (indexSlash > 0) {
            firstPart = path.substring(0, indexSlash);
        } else {
            firstPart = path;
        }

//        if (super.ignoreList.contains(firstPart)) {
//
//            // Only authenticated users can access /service
//            if("/service".equals(firstPart) && req.getRemoteUser() == null &&
//                    (req.getSession(false) == null || req.getSession().getAttribute(Constants.AUTHENTICATED_USER_ID) == null)){
//                ((HttpServletResponse)response).sendError(HttpServletResponse.SC_FORBIDDEN);
//                return;
//            }
//
//            chain.doFilter(request, response); // Goes to default servlet.
//        } else {
        chain.doFilter(request, response);
//            request.getRequestDispatcher(path).forward(request, response);
//        }
    }
}
