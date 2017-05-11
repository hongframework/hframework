
package com.hframe.service.handler;

import com.hframe.domain.model.*;
import com.hframe.service.interfaces.*;
import com.hframework.web.extension.AbstractBusinessHandler;
import com.hframework.web.extension.annotation.AfterCreateHandler;
import com.hframework.web.extension.annotation.AfterUpdateHandler;
import com.hframework.common.util.collect.CollectionUtils;
import com.hframework.common.util.collect.bean.Fetcher;
import com.hframework.common.util.file.FileUtils;
import com.hframework.common.util.StringUtils;
import com.hframework.common.util.message.VelocityUtil;
import com.hframework.common.util.message.XmlUtils;
import com.hframework.generator.util.CreatorUtil;
import com.hframework.generator.web.BaseGeneratorUtil;
import com.hframework.web.config.bean.Program;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangquanhong on 2016/10/14.
 */
@Service
public class HfpmProgramCfgHandler extends AbstractBusinessHandler<HfpmProgramCfg> {

    @Resource
    private IHfcfgDbConnectSV iHfcfgDbConnectSV;
    @Resource
    private IHfsecUserSV hfsecUserSV;
    @Resource
    private IHfpmProgramSV hfpmProgramSV;
    @Resource
    private IHfpmModuleSV hfpmModuleSV;

    @Resource
    private IHfmdEntitySV hfmdEntitySV;

    @Resource
    private IHfpmDataSetSV hfpmDataSetSV;
    @Resource
    private IHfmdEntityAttrSV hfmdEntityAttrSV;

    @AfterCreateHandler
    @AfterUpdateHandler(attr = "hfcfgDbConnectId")
    public boolean dataSetSetting(HfpmProgramCfg hfpmProgramCfg) throws Exception {
        if(hfpmProgramCfg.getHfcfgDbConnectId() != null && hfpmProgramCfg.getHfcfgDbConnectId() > 0L){
            String companyCode = "hframe";
            HfpmProgram hfpmProgram = hfpmProgramSV.getHfpmProgramByPK(hfpmProgramCfg.getHfpmProgramId());
            String programCode = hfpmProgram.getHfpmProgramCode();
//            String programName = hfpmProgram.getHfpmProgramCode();
            String projectBasePath = CreatorUtil.getTargetProjectBasePath(companyCode,
                    programCode, null);
            HfcfgDbConnect dataSourceInfo = iHfcfgDbConnectSV.getHfcfgDbConnectByPK(hfpmProgramCfg.getHfcfgDbConnectId());
            if(dataSourceInfo == null) return true;

            //重定义项目的dataSource.properties文件
            Map map = new HashMap();
            map.put("Jdbc", new BaseGeneratorUtil.Jdbc(dataSourceInfo.getUrl().replaceAll("&", "&amp;"), dataSourceInfo.getUser(), dataSourceInfo.getPassword()));
            String content = VelocityUtil.produceTemplateContent("com/hframework/generator/vm/jdbcProperties.vm", map);
            System.out.println(content);
            FileUtils.writeFile(projectBasePath + "/hframe-core/src/main/resources/properties/dataSource.properties", content);

        }
        return true;
    }

    @AfterCreateHandler
    @AfterUpdateHandler
    public boolean generateAuthInstance(HfpmProgramCfg hfpmProgramCfg) throws Exception {

        if(hfpmProgramCfg == null) return true;
        String companyCode = "hframe";
        HfpmProgram hfpmProgram = hfpmProgramSV.getHfpmProgramByPK(hfpmProgramCfg.getHfpmProgramId());
        String programCode = hfpmProgram.getHfpmProgramCode();
        String projectBasePath = CreatorUtil.getTargetProjectBasePath(companyCode,
                programCode, null);
        Program program = XmlUtils.readValueFromAbsoluteFilePath(projectBasePath + "/hframe-web/src/main/resources/program/program.xml", Program.class);
        if(StringUtils.isNotBlank(hfpmProgramCfg.getShowName())) program.setName(hfpmProgramCfg.getShowName());
        //用户实体
        if(hfpmProgramCfg.getUserEntityName() != null) program.getAuthInstance().setUser(getCfgName(hfpmProgramCfg.getUserEntityName()));
        //数据实体
        if(hfpmProgramCfg.getDataEntityName() != null) program.getAuthInstance().setData(getCfgName(hfpmProgramCfg.getDataEntityName()));
        //功能实体
        if(hfpmProgramCfg.getFuncEntityName() != null) program.getAuthInstance().setFunction(getCfgName(hfpmProgramCfg.getFuncEntityName()));
        //用户数据授权路径
        if(hfpmProgramCfg.getUserAuthPath() != null) program.getAuthInstance().setUserDataAuth(hfpmProgramCfg.getUserAuthPath());
        //用户功能授权路径
        if(hfpmProgramCfg.getFuncAuthPath() != null) program.getAuthInstance().setUserFuncAuth(hfpmProgramCfg.getFuncAuthPath());
        //超级管理员规则【实体】
        if(hfpmProgramCfg.getSuperAuthFilterEntity() != null) program.getAuthInstance().getSuperAuthFilter().setDataSet(getCfgName(hfpmProgramCfg.getSuperAuthFilterEntity() + ""));
        //超级管理员规则【字段】
        HfmdEntityAttr hfmdEntityAttrByPK = hfmdEntityAttrSV.getHfmdEntityAttrByPK(hfpmProgramCfg.getSuperAuthFilterField());
        if(hfpmProgramCfg.getSuperAuthFilterField() != null) program.getAuthInstance().getSuperAuthFilter().setDataField(hfmdEntityAttrByPK.getHfmdEntityAttrCode());
        //超级管理员规则【字段值】
        if(hfpmProgramCfg.getSuperAuthFilterFieldValue() != null) program.getAuthInstance().getSuperAuthFilter().setDataFieldValue(hfpmProgramCfg.getSuperAuthFilterFieldValue());

        if(hfpmProgramCfg.getUserLoginDataSet() != null) program.getLogin().setDataSet(getDataSetName(hfpmProgramCfg.getUserLoginDataSet()));

        String xml = XmlUtils.writeValueAsString(program);
        FileUtils.writeFile(projectBasePath + "/hframe-web/src/main/resources/program/program.xml", xml);

        return true;
    }

    private String getDataSetName(String dataSetId) throws Exception {

        HfpmDataSet hfpmDataSet = hfpmDataSetSV.getHfpmDataSetByPK(Long.parseLong(dataSetId));
        HfmdEntity hfmdEntity = hfmdEntitySV.getHfmdEntityByPK(hfpmDataSet.getMainHfmdEntityId());
        HfpmModule hfpmModule = hfpmModuleSV.getHfpmModuleByPK(hfmdEntity.getHfpmModuleId());
        return hfpmModule.getHfpmModuleCode() + "/" + hfpmDataSet.getHfpmDataSetCode();
    }


    private String getCfgName(String userEntityName) throws Exception {
        HfmdEntity_Example example = new HfmdEntity_Example();
        List<Long> entityIds = CollectionUtils.fetch(Arrays.asList(userEntityName.split(",")),
                new Fetcher<String, Long>() {
                    public Long fetch(String s) {
                        return Long.valueOf(s);
                    }
                });
        example.createCriteria().andHfmdEntityIdIn(entityIds);
        List<HfmdEntity> hfmdEntityList = hfmdEntitySV.getHfmdEntityListByExample(example);

        for (HfmdEntity hfmdEntity : hfmdEntityList) {
            HfpmModule hfpmModule = hfpmModuleSV.getHfpmModuleByPK(hfmdEntity.getHfpmModuleId());
            ;
            userEntityName = userEntityName.replaceAll(String.valueOf(hfmdEntity.getHfmdEntityId()), hfpmModule.getHfpmModuleCode() + "." + hfmdEntity.getHfmdEntityCode());
        }

        return userEntityName;
    }
}
