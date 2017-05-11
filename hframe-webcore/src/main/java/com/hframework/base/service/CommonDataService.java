package com.hframework.base.service;

import com.hframework.base.bean.KVBean;
import com.hframework.base.dao.CommonDataMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: zhangqh6
 * Date: 2016/6/6 21:39:39
 */
@Service("commonDataService")
public class CommonDataService {

    @Resource
    private CommonDataMapper commonDataMapper;

    /**
     * 动态查询表数据
     *
     * @param entity
     * @return
     * @throws Exception
     */
    public List<KVBean> selectDynamicTableDataList(Map entity) throws Exception {
        return commonDataMapper.selectDynamicTableDataList(entity);
    }

    /**
     * 动态查询表数据
     *
     * @param entity
     * @return
     * @throws Exception
     */
    public Map<String, Object> selectDynamicTableDataOne(Map entity) throws Exception {
        return commonDataMapper.selectDynamicTableDataOne(entity);
    }

    /**
     * 动态查询表数据
     *
     * @param entity
     * @return
     * @throws Exception
     */
    public List selectDynamicTableDataSome(Map entity) throws Exception {
        return commonDataMapper.selectDynamicTableDataSome(entity);
    }



    /**
     * 数据库结构变更
     *
     * @param sqlInfo
     * @return
     * @throws Exception
     */
    public void executeDBStructChange(Map sqlInfo) throws Exception {
        commonDataMapper.executeDBStructChange(sqlInfo);
    }

    /**
     * 数据库结构变更
     *
     * @param sql
     * @return
     * @throws Exception
     */
    public void executeDBStructChange(final String sql) throws Exception {
        commonDataMapper.executeDBStructChange(new HashMap() {{
            put("sql", sql);
        }});
    }

    /**
     * 数据库结构变更
     *
     * @param sql
     * @return
     * @throws Exception
     */
    public void executeDBStructChange(final List<String> sqls) throws Exception {
        for (final String sql : sqls) {
            commonDataMapper.executeDBStructChange(new HashMap() {{
                put("sql", sql);
            }});
        }

    }

    /**
     * 查询所有表信息
     *
     * @return
     * @throws Exception
     */
    public List<String> showTables() throws Exception {
        return commonDataMapper.showTables(new HashMap() {{
        }});
    }

    /**
     * 查询创建表语句
     *
     * @param tableName
     * @return
     * @throws Exception
     */
    public String showCreateTableSql(final String tableName) throws Exception {
        Map tableName1 = commonDataMapper.showCreateTable(new HashMap() {{
            put("tableName", "`" + tableName + "`");
        }});
        if(tableName1 != null && tableName1.size() > 0) {
            Iterator iterator = tableName1.values().iterator();
            iterator.next();
            return String.valueOf(iterator.next());
        }

        return null;
    }
}