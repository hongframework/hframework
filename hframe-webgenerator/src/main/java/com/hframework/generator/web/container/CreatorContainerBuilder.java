package com.hframework.generator.web.container;

import com.hframework.generator.util.CreatorUtil;

/**
 * Created by zhangquanhong on 2016/4/20.
 */
public class CreatorContainerBuilder {
    public static CreatorContainer getCreatorContainer(String companyName, String projectName,
                                                       String moduleName, String tableName)  throws Exception {
        CreatorContainer container= new CreatorContainer();
        container.companyName = companyName;
        container.projectName = projectName;
        container.Po = CreatorUtil.getDefPoClass(companyName, projectName, moduleName, tableName);
        container.Dao = CreatorUtil.getDefDaoClass(companyName, projectName, moduleName, tableName);
        container.DaoImpl = CreatorUtil.getDefDaoImplClass(companyName, projectName, moduleName, tableName);
        container.Mapper = CreatorUtil.getDefMapperClass(companyName, projectName, moduleName, tableName);
        container.Service = CreatorUtil.getDefServiceClass(companyName, projectName, moduleName, tableName);
        container.ServiceImpl = CreatorUtil.getDefServiceImplClass(companyName, projectName, moduleName, tableName);
        container.Action = CreatorUtil.getDefActionClass(companyName, projectName, moduleName, tableName);
        container.Controller = CreatorUtil.getDefControllerClass(companyName, projectName, moduleName, tableName);

        container.PoExample = CreatorUtil.getDefPoExampleClass(companyName, projectName, moduleName, tableName);

        return container;

    }
}
