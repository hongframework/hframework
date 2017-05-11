package com.hframework.generator.web.mybatis;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by zhangqh6 on 2015/9/4.
 */
public class MyBatisGeneratorUtil {

    public static void main(String[] args) throws IOException, XMLParserException, InvalidConfigurationException, InterruptedException, SQLException {
//        generate(new File("D://my_workspace/hframe-trunk/hframe-core/src/main/resources/hframe/generator/mybatis-generator-config-hframe.xml"));
        generate(new File("D:\\my_workspace\\hframe-trunk\\hframe-generator-web\\src\\main\\resources\\mybatis-generator-config-lcs.xml"));

    }

    public static  void generate() throws IOException, XMLParserException, InvalidConfigurationException, SQLException, InterruptedException {
       generate("mybatis-generator-config-hframe.xml");
    }

    public static  List<TableConfiguration> getTableCfg() throws IOException, XMLParserException, InvalidConfigurationException, SQLException, InterruptedException {
        return getTableCfg("mybatis-generator-config-lcs.xml");
    }

    public static  List<TableConfiguration> getTableCfg(String cfgFileName) throws IOException, XMLParserException, InvalidConfigurationException, SQLException, InterruptedException {
        boolean overwrite = true;
        String rootClassPath = Thread.currentThread().getContextClassLoader ().getResource("").getPath();
        System.out.println(rootClassPath);
        File configFile = new File(rootClassPath + cfgFileName);
        return getTableCfg(configFile);
    }

    public static  List<TableConfiguration> getTableCfg(File mybatisConfigFile) throws IOException, XMLParserException, InvalidConfigurationException, SQLException, InterruptedException {
        List<String> warnings = new ArrayList<String>();
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(mybatisConfigFile);
        List<TableConfiguration> tableConfigurations = config.getContexts().get(0).getTableConfigurations();
//        for (TableConfiguration tableConfiguration : tableConfigurations) {
//            System.out.println(tableConfiguration.getTableName() + "：" + tableConfiguration.getProperty("chineseName"));
//        }
        return tableConfigurations;
    }

    public static  void generate(File configFile) throws IOException, XMLParserException, InvalidConfigurationException, SQLException, InterruptedException {
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
        System.out.println(Arrays.toString(warnings.toArray(new String[0])));
    }

    public static  void generate(String cfgFileName) throws IOException, XMLParserException, InvalidConfigurationException, SQLException, InterruptedException {
        String rootClassPath = Thread.currentThread().getContextClassLoader ().getResource("").getPath();
        System.out.println(rootClassPath);
        File configFile = new File(rootClassPath + cfgFileName);
        generate(configFile);
    }

}
