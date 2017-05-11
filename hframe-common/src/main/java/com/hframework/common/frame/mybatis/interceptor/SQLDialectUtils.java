package com.hframework.common.frame.mybatis.interceptor;


import com.hframework.common.frame.dialect.DB2Dialect;
import com.hframework.common.frame.dialect.MySQLDialect;
import com.hframework.common.frame.dialect.OracleDialect;
import org.springframework.jdbc.BadSqlGrammarException;

import java.sql.SQLException;

/**
 * 数据库对应SQL类型解析器
 */
public class SQLDialectUtils {

    public static final String MYSQL = "mysql";
    public static final String ORACLE = "oracle";
    public static final String DB2 = "db2";

    /**
     * 根据类型获得sql分页语句
     */
    public static String getLimitString(String type, String sql, int offset, int limit) {
        String pageSql = "";
        if (sql.equals("") || sql == null) {
            return pageSql;
        }
        if (MYSQL.equals(type)) {
            MySQLDialect dialect = new MySQLDialect();
            sql = dialect.getLimitString(sql, offset, limit);
            //generatePageMySql(sql, offset, limit);
            return sql;
        }
        if (ORACLE.equals(type)) {
            OracleDialect dialect = new OracleDialect();
            //sql = dialect.getLimitString(sql, offset, limit);
            return generatePageOracleSql(sql, offset, limit);
        }
        if (DB2.equals(type)) {
            DB2Dialect dialect = new DB2Dialect();
            //sql = dialect.getLimitString(sql, offset, limit);
            return generatePageDB2Sql(sql, offset, limit);
        }
        return pageSql;
    }

    /**
     * 组装MySql语句
     */
    private static String generatePageMySql(String sql, int offset, int limit) {
        String pageSql = "";
        StringBuffer sb = new StringBuffer();
        if (offset != 0) {
            sb.append("limit ").append(offset).append(", ").append(limit);
            pageSql = sql.replace("limit ?, ?", sb.toString());
        } else {
            sb.append("limit ").append(limit);
            pageSql = sql.replace("limit ?", sb.toString());
        }
        return pageSql;
    }

    /**
     * 组转Oracle语句
     */
    private static String generatePageOracleSql(String sql, int start, int end) {
        String pageSql = "";
        StringBuffer sb = new StringBuffer();
        if (start != 0) {
            sb.append("rownum_ <= ").append(end);
            pageSql = sql.replace("rownum_ <= ?", sb.toString());
            sb = new StringBuffer();
            sb.append("rownum_ > ").append(start);
            pageSql = pageSql.replace("rownum_ > ?", sb.toString());
        } else {
            sb.append("rownum <= ").append(end);
            pageSql = sql.replace("rownum <= ?", sb.toString());
        }
        return pageSql;
    }

    /**
     * 组转DB2语句(未验证)
     */
    private static String generatePageDB2Sql(String sql, int offset, int limit) {
        String pageSql = "";
        StringBuffer sb = new StringBuffer();
        if (offset != 0) {
            sb.append("rownumber_ between ").append(offset).append("+1 ").append("and ").append(limit);
            pageSql = sql.replace("rownumber_ between ?+1 and ?", sb.toString());
        } else {
            sb.append("rownumber_ <= ").append(limit);
            pageSql = sql.replace("rownumber_ <= ?", sb.toString());
        }
        return pageSql;
    }

    public static void main(String[] args) {
//		Page<TUser> page = new Page<TUser>(5);
//		page.setPageNo(6);
        String sql = "select id, user_name as userName, email, status, remark  from t_user";
//		int num1 = page.getStartRow();//10
//		int num2 = page.getEndRow();//15
//		String tableSql = getLimitString(ORACLE, sql, num1, num2);
//		System.out.println(tableSql);
//		System.out.println("getStartRow()===" + page.getStartRow());
//		System.out.println("getEndRow()===" + page.getEndRow());
//		System.out.println("getOffset()===" + page.getOffset());
//		System.out.println("getPageSize()===" + page.getPageSize());

        int index = sql.toUpperCase().indexOf(" FROM ");
        if (index != -1) {
            sql = sql.substring(index);
            sql = "SELECT COUNT(*) AS ROWCOU" + sql;
        } else {
            throw new BadSqlGrammarException("count rows", sql, new SQLException("unsupported sql"));
        }
        index = sql.toUpperCase().lastIndexOf(" ORDER BY ");
        if (index != -1)
            sql = sql.substring(0, index);

        System.out.println(sql);
    }
}
