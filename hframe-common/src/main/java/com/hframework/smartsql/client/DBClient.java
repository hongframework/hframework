package com.hframework.smartsql.client;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.hframework.common.util.message.JsonUtils;
import com.hframework.smartsql.client.exceptions.DBInitializeException;
import com.hframework.smartsql.client.exceptions.DBQueryException;
import com.hframework.smartsql.client.exceptions.DBUpdateException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Created by zhangquanhong on 2017/8/10.
 */
public class DBClient {

    private static final Logger logger = LoggerFactory.getLogger(DBClient.class);

    private static final Boolean REGISTER_DATABASE_IGNORE_KEY_REPEAT = true;
    private static ThreadLocal<String> currentDatabaseKey = new ThreadLocal<String>();

    private static Map<String, DBInfo> dbInfoCache = new HashMap();
    private static Map<String, DataSource> cache = new HashMap();

    public static void registerDatabase(String key, String url, String username, String password) {
        logger.info("register database :{},{},{}", key, url, username);
        if(dbInfoCache.containsKey(key)) {
            if(!REGISTER_DATABASE_IGNORE_KEY_REPEAT) {
                throw new DBInitializeException("register database key [" + key + "] exists !");
            }
        }else {
            dbInfoCache.put(key, new DBInfo(url, username, password));
        }
        logger.info("register database success :{},{},{}", key, url, username);
    }

    public static void setCurrentDatabaseKey(String key) {
        currentDatabaseKey.set(key);
    }

    public static String getCurrentDatabaseKey() {
        String key = currentDatabaseKey.get();
        if(StringUtils.isBlank(key)) {
            throw new DBInitializeException("get current database key [" + key + "] failed , not exists !");
        }
        logger.debug("get current database success :{}", key);
        return key;
    }

    public static DataSource getDataSource(String key) {
        if(!dbInfoCache.containsKey(key)) {
            throw new DBInitializeException("get datasource failed, [" + key + "] 's not register !");
        }
        DBInfo dbInfo = dbInfoCache.get(key);
        return getDataSource(dbInfo.getUrl(), dbInfo.getUsername(), dbInfo.getPassword());
    }

    private static DataSource getDataSource(String url, String username, String password) {
        String cacheKey = Joiner.on("|").join(new String[]{url, username, password});
        if(!cache.containsKey(cacheKey)) {
            synchronized (DBClient.class) {
                if(!cache.containsKey(cacheKey)) {
                    try {
                        cache.put(cacheKey, getDataSourceInternal(url, username, password));
                    } catch (SQLException e) {
                        e.printStackTrace();
                        throw new DBInitializeException("datasource create error => " + e.getMessage());
                    }
                }
            }
        }
        return cache.get(cacheKey);
    }

    public static DataSource getDataSourceInternal(String url, String username, String password) throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver"); //设置驱动
        dataSource.setUsername(username); //用户名
        dataSource.setPassword(password);//密码
        dataSource.setUrl(url);//URL
        dataSource.setInitialSize(5);//连接池初始化大小
        dataSource.setMinIdle(1);//连接池最小空闲数
        dataSource.setMaxActive(10); // 启用监控统计功能
        dataSource.setFilters("stat");// for mysql
        dataSource.setPoolPreparedStatements(false);

        return dataSource;
    }

    /**
     * 建立数据库连接
     * @return 数据库连接
     * @throws Exception
     */
    private static Connection getConnection(String key) throws SQLException {
        Connection connection = null;
        try {
            // 获取连接
            connection = getDataSource(key).getConnection();
        } catch (SQLException e) {
           throw e;

        }
        return connection;
    }

    /**
     * 关闭所有资源
     */
    private static void closeAll(ResultSet resultSet, PreparedStatement preparedStatement, CallableStatement callableStatement, Connection connection) {
        // 关闭结果集对象
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        // 关闭PreparedStatement对象
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        // 关闭CallableStatement 对象
        if (callableStatement != null) {
            try {
                callableStatement.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        // 关闭Connection 对象
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
    * insert update delete SQL语句的执行的统一方法
    * @param sql SQL语句
    * @param params 参数数组，若没有参数则为null
    * @return 受影响的行数
    * @throws Exception
    */
    public static int executeUpdate(String sql, Object[] params)  {
        return executeUpdate(getCurrentDatabaseKey(), sql, params);
    }

    /**
     * insert update delete SQL语句的执行的统一方法
     * @param sql SQL语句
     * @param params 参数数组，若没有参数则为null
     * @return 受影响的行数
     * @throws Exception
     */
    public static int executeUpdate(String dbKey, String sql, Object[] params)  {
        // 受影响的行数
        int affectedLine = 0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            // 获得连接
            connection = getConnection(dbKey);
            // 调用SQL
            preparedStatement = connection.prepareStatement(sql);

            // 参数赋值
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1, params[i]);
                }
            }

            // 执行
            affectedLine = preparedStatement.executeUpdate();

        } catch (Exception e) {
            logger.warn("db update error => {}", ExceptionUtils.getMessage(e));
            throw new DBUpdateException(e);
        } finally {
            // 释放资源
            closeAll(null, preparedStatement, null, connection);
        }
        return affectedLine;
    }

    /**
     * 获取结果集，并将结果放在List中
     * @param sql SQL语句
     * @return List 结果集
     * @throws Exception
     */
    public static Map<String, Object> executeQueryMap( String sql, Object[] params)  {
        return executeQueryMap(getCurrentDatabaseKey(), sql, params);
    }

    /**
     * 获取结果集，并将结果放在List中
     * @param sql SQL语句
     * @return List 结果集
     * @throws Exception
     */
    public static Map<String, Object> executeQueryMap(String dbKey, String sql, Object[] params)  {
        List<Map<String, Object>> maps = executeQueryMaps(dbKey, sql, params);

        if(maps != null && maps.size() > 1) {
            throw new DBQueryException("execute query result not only one !");
        }
        if(maps == null || maps.size() == 0) {
            return null;
        }
        return maps.get(0);
    }

    /**
     * 获取结果集，并将结果放在List中
     * @param sql SQL语句
     * @return List 结果集
     * @throws Exception
     */
    public static List<Map<String, Object>> executeQueryMaps(String sql, Object[] params)  {
        return executeQueryMaps(getCurrentDatabaseKey(), sql, params);
    }

    /**
     * 获取结果集，并将结果放在List中
     * @param sql SQL语句
     * @return List 结果集
     * @throws Exception
     */
    public static List<Map<String, Object>> executeQueryMaps(String dbKey, String sql, Object[] params)  {
        return executeQueryMaps(dbKey, sql, params, -1);
    }

    /**
     * 获取结果集，并将结果放在List中
     * @param sql SQL语句
     * @return List 结果集
     * @throws Exception
     */
    public static List<Map<String, Object>> executeQueryMaps(String dbKey, String sql, Object[] params, int timeOutSecond)  {
        logger.debug("db query => {}|{}|{}",dbKey, sql, Arrays.toString(params));

        ResultSet resultSet = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        // 执行SQL获得结果集
        // 创建ResultSetMetaData对象
        ResultSetMetaData rsmd = null;
        // 结果集列数
        int columnCount = 0;

        // 创建List
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            // 获得连接
            connection = getConnection(dbKey);
            // 调用SQL
            preparedStatement = connection.prepareStatement(sql);

            // 参数赋值
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1, params[i]);
                }
            }

            if(timeOutSecond > 0) {
                preparedStatement.setQueryTimeout(timeOutSecond);
            }

            // 执行
            resultSet = preparedStatement.executeQuery();

            rsmd = resultSet.getMetaData();
            // 获得结果集列数
            columnCount = rsmd.getColumnCount();
            // 将ResultSet的结果保存到List中
            while (resultSet.next()) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 1; i <= columnCount; i++) {
                    map.put(rsmd.getColumnLabel(i), resultSet.getObject(i));
                }
                list.add(map);
            }
        } catch (Exception e) {
            logger.warn("db query error => {}", ExceptionUtils.getMessage(e));
            throw new DBQueryException(e);
        } finally {
            // 释放资源
            closeAll(resultSet, preparedStatement, null, connection);
        }
        if(logger.isDebugEnabled()) {
            try {
                logger.debug("db query result => {}", JsonUtils.writeValueAsString(list));
            } catch (IOException e) {
                logger.debug("db query result => {}", list);
            }
        }
        return list;
    }

    /**
     * 获取结果集，并将结果放在List中
     * @param sql SQL语句
     * @return List 结果集
     * @throws Exception
     */
    public static List<List<Object>> executeQueryList(String sql, Object[] params)  {
        return executeQueryList(sql, params, -1);
    }

    /**
     * 获取结果集，并将结果放在List中
     * @param sql SQL语句
     * @return List 结果集
     * @throws Exception
     */
    public static List<List<Object>> executeQueryList(String sql, Object[] params, int timeOutSecond)  {
        return executeQueryList(getCurrentDatabaseKey(), sql, params, false, timeOutSecond);
    }

    /**
     * 获取结果集，并将结果放在List中
     * @param sql SQL语句
     * @return List 结果集
     * @throws Exception
     */
    public static List<List<Object>> executeQueryList(String sql, Object[] params, boolean addHead)  {
        return executeQueryList(getCurrentDatabaseKey(), sql, params, addHead);
    }

    /**
     * 获取结果集，并将结果放在List中
     * @param sql SQL语句
     * @return List 结果集
     * @throws Exception
     */
    public static List<List<Object>> executeQueryList(String dbKey, String sql, Object[] params, boolean addHead)  {
        return executeQueryList(dbKey, sql, params, addHead, -1);
    }

    /**
     * 获取结果集，并将结果放在List中
     * @param sql SQL语句
     * @return List 结果集
     * @throws Exception
     */
    public static List<List<Object>> executeQueryList(String dbKey, String sql, Object[] params, boolean addHead, int timeOutSecond)  {
        logger.debug("db query => {}|{}|{}|{}",dbKey, sql, Arrays.toString(params), addHead);
        ResultSet resultSet = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        // 执行SQL获得结果集
        // 创建ResultSetMetaData对象
        ResultSetMetaData rsmd = null;
        // 结果集列数
        int columnCount = 0;

        // 创建List
        List<List<Object>> data = new ArrayList<List<Object>>();
        try {
            // 获得连接
            connection = getConnection(dbKey);
            // 调用SQL
            preparedStatement = connection.prepareStatement(sql);

            // 参数赋值
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1, params[i]);
                }
            }

            if(timeOutSecond > 0) {
                preparedStatement.setQueryTimeout(timeOutSecond);
            }

            // 执行
            resultSet = preparedStatement.executeQuery();

            rsmd = resultSet.getMetaData();
            // 获得结果集列数
            columnCount = rsmd.getColumnCount();
            if(addHead) {
                List<Object> columns = new ArrayList<Object>();
                for (int i = 1; i <= columnCount; i++) {
                    columns.add(rsmd.getColumnLabel(i));
                }
                data.add(columns);
            }


            // 将ResultSet的结果保存到List中
            while (resultSet.next()) {
                List<Object> row = new ArrayList<Object>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(resultSet.getObject(i));
                }
                data.add(row);
            }
        } catch (Exception e) {
            logger.warn("db query error => {}", ExceptionUtils.getMessage(e));
            throw new DBQueryException(e);
        } finally {
            // 释放资源
            closeAll(resultSet, preparedStatement, null, connection);
        }
        if(logger.isDebugEnabled()){
            try {
                logger.debug("db query result => {}", JsonUtils.writeValueAsString(data));
            } catch (IOException e) {
                logger.debug("db query result => {}", data);
            }
        }

        return data;
    }

//    /**
//     * SQL 查询将查询结果直接放入ResultSet中
//     * @param sql SQL语句
//     * @param params 参数数组，若没有参数则为null
//     * @return 结果集
//     * @throws Exception
//     */
//    private static ResultSet executeQueryRS(String sql, Object[] params){
//        ResultSet resultSet = null;
//        Connection connection = null;
//        PreparedStatement preparedStatement = null;
//        try {
//            // 获得连接
//            connection = getConnection("as");
//            // 调用SQL
//            preparedStatement = connection.prepareStatement(sql);
//
//            // 参数赋值
//            if (params != null) {
//                for (int i = 0; i < params.length; i++) {
//                    preparedStatement.setObject(i + 1, params[i]);
//                }
//            }
//
//            // 执行
//            resultSet = preparedStatement.executeQuery();
//
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }finally {
//            // 释放资源
//            closeAll(resultSet, preparedStatement, null, connection);
//        }
//
//        return resultSet;
//    }

    public static class DBInfo{
        private String url;
        private String username;
        private String password;

        public DBInfo(String url, String username, String password) {
            this.url = url;
            this.username = username;
            this.password = password;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DBInfo dbInfo = (DBInfo) o;
            return Objects.equal(url, dbInfo.url) &&
                    Objects.equal(username, dbInfo.username) &&
                    Objects.equal(password, dbInfo.password);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(url, username, password);
        }
    }
}
