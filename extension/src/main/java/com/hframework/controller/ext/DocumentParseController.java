package com.hframework.controller.ext;

import com.google.common.collect.Lists;
import com.hframework.beans.controller.ResultData;
import com.hframework.common.frame.ServiceFactory;
import com.hframework.common.util.StringUtils;
import com.hframework.common.util.collect.CollectionUtils;
import com.hframework.common.util.collect.bean.Grouper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by zhangquanhong on 2017/6/14.
 */
@Controller
@RequestMapping(value = "/extend")
public class DocumentParseController {
    private static final LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    private static final Logger logger = LoggerFactory.getLogger(DocumentParseController.class);
    /**
     * 获取方法所有参数名
     * @param method
     * @return
     */
    public static String[] getParameterNames(Method method) {
        return parameterNameDiscoverer.getParameterNames(method);
    }

    @RequestMapping(value = "/apidoc.json")
    @ResponseBody
    public ResultData apidoc(HttpServletRequest request) {
        final List<InterfaceInfo> list = new ArrayList<InterfaceInfo>();
        Map<RequestMappingInfo, HandlerMethod> map = ServiceFactory.getService(RequestMappingHandlerMapping.class).getHandlerMethods();

        Set<TreeItem> apiSet = new HashSet<TreeItem>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
            String url = m.getKey().getPatternsCondition().getPatterns().iterator().next();
            parseListInfo(apiSet, url, "name");
            HandlerMethod method = m.getValue();
            String[] parameterNames = getParameterNames(method.getMethod());
            InterfaceInfo interfaceInfo = new InterfaceInfo();
            interfaceInfo.setName("测试1");
            interfaceInfo.setUrl(url);
            interfaceInfo.setDescription("测试");
            interfaceInfo.setVersion("1.0.1");
            for (int i=0;i<parameterNames.length;i++){
                ParameterInfo parameterInfo = new ParameterInfo();
                parameterInfo.setCode(parameterNames[i]);
                parameterInfo.setName(parameterNames[i]);
                parameterInfo.setType(method.getMethod().getParameterTypes()[i].getName());
                parameterInfo.setRequired(true);
                parameterInfo.setDefaultValue("123");
                parameterInfo.setDescription("234");
                interfaceInfo.addBusinessParameter(parameterInfo);
            }
            list.add(interfaceInfo);
        }

        Collections.sort(list, new Comparator<InterfaceInfo>() {
            public int compare(InterfaceInfo o1, InterfaceInfo o2) {
                return o1.getUrl().compareTo(o2.getUrl());
            }
        });

        ArrayList<TreeItem> treeItems = Lists.newArrayList(apiSet);
        Collections.sort(treeItems, new Comparator<TreeItem>() {
            public int compare(TreeItem o1, TreeItem o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });

        final Map<String, List<TreeItem>> group = CollectionUtils.group(treeItems, new Grouper<String, TreeItem>() {
            public <K> K groupKey(TreeItem treeItem) {
                return (K) treeItem.getPid();
            }
        });

        return ResultData.success(new HashMap<String, Object>() {{
            put("apihome", list);
            put("apitree", group);
        }});
    }

    private void parseListInfo(Set<TreeItem> apiSet, String url, String name) {
        if(apiSet.contains(url) && StringUtils.isBlank(url)) return;
        String parentPath = url.substring(0, url.lastIndexOf("/"));
        if(StringUtils.isNotBlank(parentPath)) {
            apiSet.add(new TreeItem(url, parentPath, url.substring(url.lastIndexOf("/")+1)));
            parseListInfo(apiSet, parentPath, name);
        }else {
            apiSet.add(new TreeItem(url, "-1", url.substring(url.lastIndexOf("/")+1)));
        }

    }

    public static class TreeItem{
        private String id;
        private String pid;
        private String name;
        private String url;
        private String icon;

        public TreeItem(String id, String pid, String name) {
            this.id = id;
            this.pid = pid;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof String) {
                return this.id.equals(obj);

            }
            return this.id.equals(((TreeItem)obj).getId());
        }
    }

    public static class InterfaceInfo {
        private String url;
        private String name;
        private String description;
        private String version;

        private List<ParameterInfo> publicInfos;
        private List<ParameterInfo> parameterInfos;
        private List<ParameterInfo> staticInfos;


        public void addPublicParameter(ParameterInfo parameterInfo) {
            if(publicInfos ==null)
                publicInfos = new ArrayList<ParameterInfo>();
            publicInfos.add(parameterInfo);
        }

        public void addBusinessParameter(ParameterInfo parameterInfo) {
            if(parameterInfos ==null)
                parameterInfos = new ArrayList<ParameterInfo>();
            parameterInfos.add(parameterInfo);
        }

        public void addStaticParameter(ParameterInfo parameterInfo) {
            if(staticInfos ==null)
                staticInfos = new ArrayList<ParameterInfo>();
            staticInfos.add(parameterInfo);
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public List<ParameterInfo> getPublicInfos() {
            return publicInfos;
        }

        public void setPublicInfos(List<ParameterInfo> publicInfos) {
            this.publicInfos = publicInfos;
        }

        public List<ParameterInfo> getParameterInfos() {
            return parameterInfos;
        }

        public void setParameterInfos(List<ParameterInfo> parameterInfos) {
            this.parameterInfos = parameterInfos;
        }

        public List<ParameterInfo> getStaticInfos() {
            return staticInfos;
        }

        public void setStaticInfos(List<ParameterInfo> staticInfos) {
            this.staticInfos = staticInfos;
        }
    }
    public static class ParameterInfo {
        private String name;
        private String code;
        private String type;
        private boolean required;
        private String defaultValue;
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }
}
