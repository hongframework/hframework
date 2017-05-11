package com.hframework.web.interceptor;

import com.hframework.common.frame.ServiceFactory;
import com.hframework.web.context.WebContext;
import com.hframework.web.config.bean.component.Event;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.explorer.Constants;
import org.activiti.explorer.ExplorerApp;
import org.activiti.explorer.identity.LoggedInUser;
import org.activiti.explorer.ui.login.LoginHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zqh on 2016/8/18.
 */
public class WorkflowInterceptor implements HandlerInterceptor {


    /**
     * Intercept the execution of a handler. Called after HandlerMapping determined
     * an appropriate handler object, but before HandlerAdapter invokes the handler.
     * <p>DispatcherServlet processes a handler in an execution chain, consisting
     * of any number of interceptors, with the handler itself at the end.
     * With this method, each interceptor can decide to abort the execution chain,
     * typically sending a HTTP error or writing a custom response.
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  chosen handler to execute, for type and/or instance evaluation
     * @return {@code true} if the execution chain should proceed with the
     * next interceptor or the handler itself. Else, DispatcherServlet assumes
     * that this interceptor has already dealt with the response itself.
     * @throws Exception in case of errors
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String workflowButton = request.getParameter("_WFB");
        String dataSet = request.getParameter("_DS");
        String dataId = request.getParameter("_DI");

        if(request.getSession().getAttribute(Constants.AUTHENTICATED_USER_ID) == null) {
            LoginHandler loginHandler = (LoginHandler) ServiceFactory.getService("activitiLoginHandler");
            LoggedInUser user = loginHandler.authenticate(request, response);
            if(user != null) {
                ExplorerApp.get().setUser(user);
                loginHandler.onRequestStart(request, response);
            }
        }else {
            Authentication.setAuthenticatedUserId(String.valueOf(request.getSession().getAttribute(Constants.AUTHENTICATED_USER_ID)));
        }


        if(StringUtils.isNotBlank(workflowButton) && StringUtils.isNotBlank(dataSet) && StringUtils.isNotBlank(dataId) && WebContext.get().getProcess(dataSet) != null) {
            Object[] objects = WebContext.get().getProcess(dataSet);
            Event workflowStartEvent = (Event) objects[5];
            final String case1 = workflowStartEvent.getPreHandleList().get(0).getCase1();
            String when = workflowStartEvent.getPreHandleList().get(0).getWhen();
            String then = workflowStartEvent.getPreHandleList().get(0).getThen();
            final String curWorkflowValue = request.getParameter(case1);
            String oldWorkflowValue = request.getParameter("_" + case1);
            if(oldWorkflowValue.equals(when) && then.equals(curWorkflowValue)) {
                ProcessInstance processInstance = ProcessEngines.getDefaultProcessEngine().getRuntimeService().startProcessInstanceByKey(dataSet, dataId);
                List<Task> list = ProcessEngines.getDefaultProcessEngine().getTaskService().createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).active().list();
                for (Task task : list) {
                    if(!("value-" + curWorkflowValue).equals(task.getTaskDefinitionKey())) {
                        //TODO 完善dataset对应字段的值更新
                    }
                }
            }else {
                List<Task> list = ProcessEngines.getDefaultProcessEngine().getTaskService().createTaskQuery().processDefinitionKey(dataSet).processInstanceBusinessKey(dataId).active().list();
                for (Task task : list) {
                    if(("value-" + oldWorkflowValue).equals(task.getTaskDefinitionKey())) {
                        if(StringUtils.isBlank(task.getAssignee())) {
                            ProcessEngines.getDefaultProcessEngine().getTaskService().setAssignee(task.getId(), Authentication.getAuthenticatedUserId());
                        }
                        ProcessEngines.getDefaultProcessEngine().getTaskService().complete(task.getId(),new HashMap<String, Object>(){{
                            put(case1, curWorkflowValue);
                            put(case1.toUpperCase(), curWorkflowValue);
                        }});
                    }
                }
            }
        }
        return true;
    }

    /**
     * Intercept the execution of a handler. Called after HandlerAdapter actually
     * invoked the handler, but before the DispatcherServlet renders the view.
     * Can expose additional model objects to the view via the given ModelAndView.
     * <p>DispatcherServlet processes a handler in an execution chain, consisting
     * of any number of interceptors, with the handler itself at the end.
     * With this method, each interceptor can post-process an execution,
     * getting applied in inverse order of the execution chain.
     *
     * @param request      current HTTP request
     * @param response     current HTTP response
     * @param handler      handler (or {@link HandlerMethod}) that started async
     *                     execution, for type and/or instance examination
     * @param modelAndView the {@code ModelAndView} that the handler returned
     *                     (can also be {@code null})
     * @throws Exception in case of errors
     */
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * Callback after completion of request processing, that is, after rendering
     * the view. Will be called on any outcome of handler execution, thus allows
     * for proper resource cleanup.
     * <p>Note: Will only be called if this interceptor's {@code preHandle}
     * method has successfully completed and returned {@code true}!
     * <p>As with the {@code postHandle} method, the method will be invoked on each
     * interceptor in the chain in reverse order, so the first interceptor will be
     * the last to be invoked.
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  handler (or {@link HandlerMethod}) that started async
     *                 execution, for type and/or instance examination
     * @param ex       exception thrown on handler execution, if any
     * @throws Exception in case of errors
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Authentication.setAuthenticatedUserId(null);
    }
}
