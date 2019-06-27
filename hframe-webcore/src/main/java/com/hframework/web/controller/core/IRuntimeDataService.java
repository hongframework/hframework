package com.hframework.web.controller.core;

import com.google.common.base.Joiner;
import com.hframework.base.bean.MapWrapper;
import com.hframework.beans.controller.Pagination;
import com.hframework.beans.controller.ResultCode;
import com.hframework.beans.controller.ResultData;
import com.hframework.common.util.JavaUtil;
import com.hframework.common.util.StringUtils;
import com.hframework.common.util.collect.CollectionUtils;
import com.hframework.common.util.collect.bean.Fetcher;
import com.hframework.smartsql.client.DBClient;
import com.hframework.web.config.bean.dataset.Field;
import com.hframework.web.context.ComponentDescriptor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class IRuntimeDataService {


    public String concatSql(HttpServletRequest request, ComponentDescriptor componentDescriptor) {
        String eventObjectCode = componentDescriptor.getDataSetDescriptor().getDataSet().getEventObjectCode();
        List<Field> fields = componentDescriptor.getDataSetDescriptor().getDataSet().getFields().getFieldList();

        Set<String> conditions = new HashSet<String>();

        for (Field field : fields) {
            String parameterValue = request.getParameter(JavaUtil.getJavaVarName(field.getCode()));
            if(StringUtils.isNotBlank(parameterValue)) {
                conditions.add(field.getCode() + "='" + parameterValue + "'");
            }
        }

        String dbKey = eventObjectCode.substring(0, eventObjectCode.indexOf("_"));
        String tableName = eventObjectCode.substring(eventObjectCode.indexOf("_") + 1);
        String conditionString = Joiner.on(" and ").join(conditions);
        String sql = "select * from " + tableName +
                (StringUtils.isBlank(conditionString) ? "" : (" where " + conditionString));

        DBClient.setCurrentDatabaseKey(dbKey);
        return sql;
    }


    public ResultData invokeDetail(HttpServletRequest request, ComponentDescriptor componentDescriptor) {
        String sql = concatSql(request, componentDescriptor);
        Map<String, Object> map = DBClient.executeQueryMap(sql, DBClient.emptyObjectArray);
        if(map == null) {
            return ResultData.error(ResultCode.RECODE_IS_NOT_EXISTS);
        }else {
            return ResultData.success(MapWrapper.warp(keyChangeToJavaVarKey(map)));
        }
    }
    
    public Map<String, Object> keyChangeToJavaVarKey(Map<String, Object> map ){
        if(map == null || map.isEmpty()) {
            return map;
        }
        Map<String, Object> result = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            result.put(JavaUtil.getJavaVarName(entry.getKey()), entry.getValue());
        }
        return result;
    }

    public ResultData invokeList(HttpServletRequest request, ComponentDescriptor componentDescriptor, Pagination pagination) {
        String baseSql = concatSql(request, componentDescriptor);
        String sql = baseSql +  " limit " + pagination.getStartIndex() + ", " + pagination.getEndIndex();
        Object count = DBClient.executeQueryList(baseSql.replace("*", "count(*)"), DBClient.emptyObjectArray).get(0).get(0);
        pagination.setTotalCount(Integer.valueOf(String.valueOf(count)));
        List<Map<String, Object>> list = DBClient.executeQueryMaps(sql, DBClient.emptyObjectArray);
        List<Map<String, Object>> transList = CollectionUtils.fetch(list, new Fetcher<Map<String, Object>, Map<String, Object>>() {
            public Map<String, Object> fetch(Map<String, Object> stringObjectMap) {
                return keyChangeToJavaVarKey(stringObjectMap);
            }
        });
        return ResultData.success().add("list",transList).add("pagination",pagination);
    }
}
