package com.hframework.web.context;

import com.hframework.base.service.CommonDataService;
import com.hframework.common.util.collect.CollectionUtils;
import com.hframework.common.util.collect.bean.Grouper;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangquanhong on 2018/2/3.
 */
public class WorkflowUtils {
    private final static String DELETE_SQL_FORMAT = "DELETE FROM act_procdef_extauth WHERE  proc_name_ = ''{0}'' and proc_key_ = ''{1}'';";
    private final static String INSERT_SQL_FORMAT = "INSERT INTO act_procdef_extauth(category_, proc_name_, proc_key_, task_key_, auth_type_, auth_target_, auth_value_, create_time_) VALUES {0};";
    private final static String SELECT_SQL_FORMAT = "SELECT * FROM act_procdef_extauth t WHERE t.CATEGORY_ =  ''{0}'' AND PROC_NAME_ =  ''{1}'' AND PROC_KEY_ =  ''{2}'';";
    private final static String SELECT_ALL_SQL_FORMAT = "SELECT * FROM act_procdef_extauth t;";


    public static Map<String, List<Object>> getWorkflowExtAuth(CommonDataService commonDataService, final String category, final String procName, final String procKey) throws Exception {
        List configList = commonDataService.selectDynamicTableDataSome(new HashMap<String, String>() {{
            put("sql", MessageFormat.format(SELECT_SQL_FORMAT, category, procName, procKey));
        }});
        Map<String, List<Object>> configMap = CollectionUtils.group(configList, new Grouper() {
            public String groupKey(Object configObject) {
                Map<String, String> config = (Map<String, String>) configObject;
                return config.get("TASK_KEY_");
            }
        });
        return configMap;
    }

    public static Map<String, List<Object>> getWorkflowAllExtAuth(CommonDataService commonDataService) throws Exception {
        List configList = commonDataService.selectDynamicTableDataSome(new HashMap<String, String>() {{
            put("sql", SELECT_ALL_SQL_FORMAT);
        }});
        Map<String, List<Object>> configMap = CollectionUtils.group(configList, new Grouper() {
            public String groupKey(Object configObject) {
                Map<String, String> config = (Map<String, String>) configObject;
                return config.get("PROC_KEY_");
//                return StringUtils.join(new String[]{config.get("CATEGORY_"), config.get("PROC_NAME_"), config.get("PROC_KEY_")},"_");
            }
        });
        return configMap;
    }
}
