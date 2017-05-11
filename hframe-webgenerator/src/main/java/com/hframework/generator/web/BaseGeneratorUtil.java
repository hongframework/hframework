package com.hframework.generator.web;

import com.hframework.beans.class0.Table;
import com.hframework.common.util.file.FileUtils;
import com.hframework.common.util.message.VelocityUtil;
import com.hframework.common.springext.datasource.DataSourceContextHolder;
import com.hframework.generator.util.CreatorUtil;
import com.hframework.generator.web.controller.ControllerV2Generator;
import com.hframework.generator.web.mybatis.MyBatisGeneratorUtil;
import com.hframework.generator.web.service.ServiceImplGenerator;
import com.hframework.generator.web.service.ServiceInterfaceGenerator;
import com.hframework.web.context.WebContext;
import org.mybatis.generator.config.TableConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangqh6 on 2015/10/18.
 */
public class BaseGeneratorUtil {

    public static  String generateMybatisConfig(List<Map<String, String>> tables, String rootPath, String companyCode,
                                                String programCode, String moduleCode, DataSourceContextHolder.DataSourceDescriptor dataSourceInfo) throws Exception {

        String javaPoPackage = CreatorUtil.getDefPoClass(companyCode, programCode, moduleCode, "X").getClassPackage();
        String javaDaoPackage = CreatorUtil.getDefDaoClass(companyCode, programCode, moduleCode, "X").getClassPackage();

        Map map = new HashMap();
        map.put("Tables", tables);
        map.put("RootPath", rootPath);
        map.put("Jdbc", new Jdbc(dataSourceInfo.url.replaceAll("&","&amp;"), dataSourceInfo.user, dataSourceInfo.password));
        map.put("JavaModelPackage", javaPoPackage);
        map.put("JavaClientPackage", javaDaoPackage);



        String content = VelocityUtil.produceTemplateContent("com/hframework/generator/vm/mybatis-generator-config.vm", map);
        System.out.println(content);
        FileUtils.writeFile(CreatorUtil.getGeneratorConfigFilePath(companyCode, programCode, null), content);
        return CreatorUtil.getGeneratorConfigFilePath(companyCode, programCode, null);
    }

    public static class Jdbc{
        private String url;
        private String user;
        private String password;
        private String driverClass = "com.mysql.jdbc.Driver";

        public Jdbc(String url, String user, String password) {
            this.url = url;
            this.user = user;
            this.password = password;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDriverClass() {
            return driverClass;
        }

        public void setDriverClass(String driverClass) {
            this.driverClass = driverClass;
        }
    }

    /**
     * 服务生成
     * @param companyName
     * @param projectName
     * @param moduleName
     * @param table
     */
    public static void serviceGenerate(String companyName, String projectName, String moduleName, Table table) throws Exception {
        serviceGenerate(companyName, projectName, moduleName, Arrays.asList(new Table[]{table}));
    }


    /**
     * 服务生成
     * @param companyName
     * @param projectName
     * @param moduleName
     * @param tables
     */
    public static void serviceGenerate(String companyName, String projectName, String moduleName, List<Table> tables) throws Exception {

        if(tables != null && tables.size() > 0) {
            for (Table table : tables) {
                ServiceInterfaceGenerator serviceInterfaceGenerator = new ServiceInterfaceGenerator(companyName, projectName, moduleName, table);
                serviceInterfaceGenerator.create();
                ServiceImplGenerator serviceImplGenerator = new ServiceImplGenerator(companyName, projectName, moduleName, table);
                serviceImplGenerator.create();
            }
        }
    }

    /**
     * Controller生成
     * @param companyName
     * @param projectName
     * @param moduleName
     * @param table
     */
    public static void controllerGenerate(String companyName, String projectName, String moduleName, Table table) throws Exception {
        controllerGenerate(companyName, projectName, moduleName,  Arrays.asList(new Table[]{table}));
    }

    /**
     * Controller生成
     * @param companyName
     * @param projectName
     * @param moduleName
     * @param tables
     */
    public static void controllerGenerate(String companyName, String projectName, String moduleName,  List<Table> tables) throws Exception {
        if(tables != null && tables.size() > 0) {
            for (Table table : tables) {
                ControllerV2Generator controllerGenerator = new ControllerV2Generator(companyName, projectName, moduleName, table);
                controllerGenerator.create();
            }
        }
    }

    public static void generator(String mybatisConfigFile, String companyCode, String programCode, String moduleCode) throws Exception {
        List<TableConfiguration> tableConfigurations = MyBatisGeneratorUtil.getTableCfg(new File(mybatisConfigFile));
        for (TableConfiguration tableConfiguration : tableConfigurations) {
            Table table = new Table();
            table.setTableName(tableConfiguration.getTableName());
            table.setTableDesc(tableConfiguration.getProperty("chineseName"));
            table.setParentId(tableConfiguration.getProperty("parentId"));
            table.setDbId(tableConfiguration.getGeneratedKey().getColumn());
            serviceGenerate(companyCode, programCode, moduleCode, table);
            controllerGenerate(companyCode,programCode, moduleCode, table);
        }
    }

    public static void generator() throws Exception {
        List<TableConfiguration> tableConfigurations = MyBatisGeneratorUtil.getTableCfg();
        for (TableConfiguration tableConfiguration : tableConfigurations) {
            Table table = new Table();
            table.setTableName(tableConfiguration.getTableName());
            table.setTableDesc(tableConfiguration.getProperty("chineseName"));
            table.setParentId(tableConfiguration.getProperty("parentId"));
            serviceGenerate("", "hframe", "hframe", table);
            controllerGenerate("", "hframe", "hframe", table);
        }
    }


    public static void main(String[] args) throws Exception {
        Table table = new Table();
        table.setTableName("sec_user");
        table.setTableDesc("用户");

        BaseGeneratorUtil.serviceGenerate("zqh","studuent","sec",table);
    }


}
