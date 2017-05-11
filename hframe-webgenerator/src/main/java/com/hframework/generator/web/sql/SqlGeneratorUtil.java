package com.hframework.generator.web.sql;

import com.hframework.base.service.CommonDataService;
import com.hframework.common.frame.ServiceFactory;
import com.hframework.common.util.file.FileUtils;
import com.hframework.common.util.StringUtils;
import com.hframework.generator.util.CreatorUtil;

import java.util.List;
import java.util.Set;

/**
 * Created by zhangquanhong on 2016/8/2.
 */
public class SqlGeneratorUtil {

    /**
     * 通过拥有者，数据库名称，所有表，生成的sql
     * @param username
     * @return
     * @throws Exception
     */
    public static String createSqlFile(String username, String programCode) throws Exception{
        return createSqlFile(username, programCode, getSqlContent());
    }

    /**
     * 通过拥有者，数据库名称，所有表，生成的sql
     * @param username
     * @return
     * @throws Exception
     */
    public static String createSqlFile(String username, String programCode, String content) throws Exception{


        //如果没有拥有者，默认为zqh用户
        if("".equals(username)){
            username="zqh";
        }
        String sqlFileName = CreatorUtil.getSQLFilePath(
                username, programCode, programCode);
        FileUtils.writeFile(sqlFileName, content);
        return sqlFileName;
    }

    public static String getSqlFilePath(String username, String programCode){
        try {
            return CreatorUtil.getSQLFilePath(
                    username, programCode, programCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getSqlContent() throws Exception {
        CommonDataService service = ServiceFactory.getService(CommonDataService.class);
        StringBuffer content=new StringBuffer();
        List<String> tableNames = service.showTables();
        for (String tableName : tableNames) {
            String sql = service.showCreateTableSql(tableName);
            if(StringUtils.isNotBlank(sql)) {
                content.append(sql).append(";").append("\n\n\n");
            }
        }

        return content.toString();
    }

    public static String getSqlContent(Set<String> tableNames) throws Exception {
        CommonDataService service = ServiceFactory.getService(CommonDataService.class);
        StringBuffer content=new StringBuffer();
        for (String tableName : tableNames) {
            String sql = service.showCreateTableSql(tableName);
            if(StringUtils.isNotBlank(sql)) {
                content.append(sql).append(";").append("\n\n\n");
            }
        }

        return content.toString();
    }
}
