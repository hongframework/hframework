package com.hframework.base.dao;

import com.hframework.base.bean.KVBean;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CommonDataMapper {

    /**
     * 动态查询表数据
     * @param entity
     * @return
     * @throws Exception
     */
    public List<KVBean> selectDynamicTableDataList(Map entity) throws Exception;


    /**
     * 动态查询表数据
     * @param entity
     * @return
     * @throws Exception
     */
    public Map<String, Object> selectDynamicTableDataOne(Map entity) throws Exception;

    /**
     * 动态查询表数据
     * @param entity
     * @return
     * @throws Exception
     */
    public List selectDynamicTableDataSome(Map entity) throws Exception;

    /**
     * 数据库结构变更
     * @param sqlInfo
     * @return
     * @throws Exception
     */
    public void executeDBStructChange(Map sqlInfo) throws Exception;

    /**
     * 查询所有表信息
     * @param sqlInfo
     * @return
     * @throws Exception
     */
    public List showTables(Map sqlInfo) throws Exception;

    /**
     * 查询创建表语句
     * @param sqlInfo
     * @return
     * @throws Exception
     */
    public Map showCreateTable(Map sqlInfo) throws Exception;

}