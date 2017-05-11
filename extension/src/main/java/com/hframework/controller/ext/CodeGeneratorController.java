package com.hframework.controller.ext;

import com.hframe.domain.model.*;
import com.hframe.service.interfaces.*;
import com.hframework.base.service.CommonDataService;
import com.hframework.base.service.DataSetLoaderService;
import com.hframework.base.service.ModelLoaderService;
import com.hframework.beans.controller.ResultCode;
import com.hframework.beans.controller.ResultData;
import com.hframework.common.util.cmd.ShellExecutor;
import com.hframework.common.util.StringUtils;
import com.hframework.generator.web.container.bean.Entity;
import com.hframework.generator.web.container.bean.EntityAttr;
import com.hframework.web.controller.DefaultController;
import com.hframework.common.springext.datasource.DataSourceContextHolder;
import com.hframework.generator.util.CreatorUtil;
import com.hframework.generator.web.BaseGeneratorUtil;
import com.hframework.generator.web.container.HfClassContainer;
import com.hframework.generator.web.container.HfClassContainerUtil;
import com.hframework.generator.web.container.HfModelContainer;
import com.hframework.generator.web.mybatis.MyBatisGeneratorUtil;
import com.hframework.generator.web.sql.HfModelService;
import com.hframework.generator.web.sql.SqlGeneratorUtil;
import com.hframework.generator.web.sql.reverse.SQLParseUtil;
import com.hframework.web.context.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

/**
 * Created by zhangquanhong on 2016/12/11.
 */
@Controller
@RequestMapping(value = "/extend")
public class CodeGeneratorController extends ExtBaseController {
    private static final Logger logger = LoggerFactory.getLogger(CodeGeneratorController.class);
    @Resource
    private DataSetLoaderService dataSetLoaderService;

    @Resource
    private ModelLoaderService modelLoaderService;
    @Resource
    private CommonDataService commonDataService;

    @Resource
    private IHfpmProgramSV hfpmProgramSV;

    @Resource
    private IHfsecMenuSV hfsecMenuSV;

    @Resource
    private IHfsecUserSV hfsecUserSV;


    @Resource
    private IHfpmModuleSV hfpmModuleSV;

    @Resource
    private IHfcfgDbConnectSV iHfcfgDbConnectSV;
    @Resource
    private IHfpmProgramCfgSV iHfpmProgramCfgSV;

    @Resource
    private IHfusEntityStoreSV hfusEntityStoreSV;

    @Resource
    private IHfsecMenuSV iHfsecMenuSV;

    @Resource
    private IHfmdEntitySV iHfmdEntitySV;

    @Resource
    private IHfmdEntityAttrSV hfmdEntityAttrSV;
    /**
     * 代码差异比对
     * @return
     */
    @RequestMapping(value = "/code_diff.json")
    @ResponseBody
    public ResultData getCodeDiff(HttpServletRequest request){
        logger.debug("request : {}");
        try{
            Map<String, String> pageContextParams = DefaultController.getPageContextParams(request);
            WebContext.putContext(DefaultController.getPageContextRealyParams(pageContextParams));
            Map<String, String> pageFlowParams = WebContext.get(HashMap.class.getName());

            String companyCode = "hframe";
            String programCode = "hframe";
            Long programId = -1L;
            String programName = "框架";
            String moduleCode = "hframe";
            String moduleName = "框架";
            HfpmProgram hfpmProgram = null;
            if(pageFlowParams != null) {
                if(pageFlowParams.containsKey("hfpmProgramId") && StringUtils.isNotBlank(pageFlowParams.get("hfpmProgramId"))) {
                    hfpmProgram = hfpmProgramSV.getHfpmProgramByPK(Long.parseLong(pageFlowParams.get("hfpmProgramId")));
                    programCode = hfpmProgram.getHfpmProgramCode();
                    programId = hfpmProgram.getHfpmProgramId();
                    programName = hfpmProgram.getHfpmProgramName();
                }
                if(pageFlowParams.containsKey("hfpmModuleId") && StringUtils.isNotBlank(pageFlowParams.get("hfpmModuleId"))) {
                    HfpmModule module = hfpmModuleSV.getHfpmModuleByPK(Long.parseLong(pageFlowParams.get("hfpmModuleId")));
                    moduleCode = module.getHfpmModuleCode();
                    moduleName = module.getHfpmModuleName();
                }
            }

            HfcfgDbConnect dataSourceInfo = startDynamicDataSource(pageFlowParams);
            String dbSqlPath = SqlGeneratorUtil.createSqlFile(companyCode, programCode);
            DataSourceContextHolder.clear();
            HfModelContainer targetFileModelContainer = SQLParseUtil.parseModelContainerFromSQLFile(
                    dbSqlPath, programCode, programName, moduleCode, moduleName);
            HfClassContainer targetClassContainer = HfClassContainerUtil.getClassInfoContainer(targetFileModelContainer);


            String projectBasePath = CreatorUtil.getTargetProjectBasePath(companyCode,
                    programCode, moduleCode);


//            if(!new File(projectBasePath).exists()) {
//                WebContextHelper helper = new WebContextHelper(companyCode,programCode,moduleCode,"");
//                String programTemplatePath = CreatorUtil.getTargetProjectBasePath("hframe", "template", moduleCode);
//                FileUtils.copyFolder(programTemplatePath, projectBasePath);
//
//                FileUtils.copyFolder(helper.programConfigRootDir + "/" + helper.programConfigMapperDir.replaceAll(programCode,"template"),
//                        helper.programConfigRootDir + "/" + helper.programConfigMapperDir);
//                FileUtils.delFolder(helper.programConfigRootDir + "/" + helper.programConfigMapperDir.replaceAll(programCode,"template"));
//                if(dataSourceInfo != null) {
//                    Map map = new HashMap();
//                    map.put("Jdbc", new BaseGeneratorUtil.Jdbc(dataSourceInfo.getUrl().replaceAll("&", "&amp;"), dataSourceInfo.getUser(), dataSourceInfo.getPassword()));
//                    String content = VelocityUtil.produceTemplateContent("com/hframework/generator/vm/jdbcProperties.vm", map);
//                    System.out.println(content);
//                    FileUtils.writeFile(projectBasePath + "/hframe-core/src/main/resources/properties/dataSource.properties", content);
//                }

//                Map map = new HashMap();
//                map.put("companyCode", companyCode);
//                map.put("programCode", programCode);
//                String content = VelocityUtil.produceTemplateContent("com/hframework/generator/vm/compileBat.vm", map);
//                System.out.println(content);
//                FileUtils.writeFile(projectBasePath + "/build/compile.bat", content);
//
//                content = VelocityUtil.produceTemplateContent("com/hframework/generator/vm/compileSh.vm", map);
//                System.out.println(content);
//                FileUtils.writeFile(projectBasePath + "/build/compile.sh", content);


//                Program program = new Program();
//                program.setCompany(companyCode);
//                program.setCode(programCode);
//                program.setName(programName);
//                program.setDescription(hfpmProgram != null ? hfpmProgram.getHfpmProgramDesc() : programName);
//
//
//                Modules modules = new Modules();
//                program.setModules(modules);
//
//                List<com.hframework.web.config.bean.program.Module> moduleList = new ArrayList<com.hframework.web.config.bean.program.Module>();
//                modules.setModuleList(moduleList);
//
//                HfpmModule_Example example = new HfpmModule_Example();
//                example.createCriteria().andHfpmProgramIdEqualTo(programId);
//                List<HfpmModule> hfpmModuleListByExample = hfpmModuleSV.getHfpmModuleListByExample(example);
//                for (HfpmModule module : hfpmModuleListByExample) {
//                    com.hframework.web.config.bean.program.Module module1 = new com.hframework.web.config.bean.program.Module();
//                    module1.setCode(module.getHfpmModuleCode());
//                    module1.setName(module.getHfpmModuleName());
//                    moduleList.add(module1);
//                }
//
//                Template template = new Template();
//                template.setCode("default");
//                template.setPath("hframework.template.default");
//                program.setTemplate(template);
//
//                program.setWelcome("uc/login.html");
//
//                SuperManager superManager = new SuperManager();
//                superManager.setCode("admin");
//                superManager.setPassword("admin");
//                superManager.setName("草鸡管理员");
//                program.setSuperManager(superManager);
//                String xml = XmlUtils.writeValueAsString(program);
//                FileUtils.writeFile(projectBasePath + "/hframe-web/src/main/resources/program/program.xml", xml);

//                startDynamicDataSource(pageFlowParams);
//                HfsecUser hfsecUser = new HfsecUser();
//                hfsecUser.setHfsecUserName(superManager.getName());
//                hfsecUser.setAccount(superManager.getCode());
//                hfsecUser.setPassword(superManager.getPassword());
//                hfsecUser.setStatus(1);
//                hfsecUser.setAvatar("http://pic.hanhande.com/files/141127/1283574_094432_8946.jpg");
//                hfsecUserSV.create(hfsecUser);
//                DataSourceContextHolder.clear();

//                Map map = new HashMap();
//                map.put("programName", programName);
//                map.put("menuDataSet", "hframe/hfsec_menu");
//                map.put("userDataSet", "hframe/hfsec_user");
//                content = VelocityUtil.produceTemplateContent("com/hframework/generator/vm/defaultPage.vm", map);
//                System.out.println(content);
//
//                WebContextHelper contextHelper = new WebContextHelper(companyCode,programCode,moduleCode,"default");
//                String pageFilePath = contextHelper.programConfigRootDir + "/" + contextHelper.programConfigModuleDir + "/default.xml";
//                FileUtils.writeFile(pageFilePath, content);
//            }


            String projectCompileTargetPath = projectBasePath + "\\hframe-core\\target\\classes\\";
//            String filePath = "/D:/my_workspace/hframe-trunk" + "\\hframe-core\\target\\classes\\";
            String classPackage = "com.hframe.domain.model.";
            HfClassContainer originClassContainer = HfClassContainerUtil.fromClassPath(
                    projectCompileTargetPath, classPackage, programCode, programName);

            final List<Map<String, String>>[] result = HfClassContainerUtil.compare(originClassContainer, targetClassContainer);

            return ResultData.success(new HashMap<String,Object>(){{
                put("AddClassInfo", new HashMap<String, Object>() {{
                    put("list", result[0]);
                }});
                put("ModClassInfo", new HashMap<String, Object>() {{
                    put("list", result[1]);
                }});
                put("DelClassInfo", new HashMap<String, Object>() {{
                    put("list", result[2]);
                }});
            }});
        }catch (Exception e) {
            logger.error("error : ", e);
            return ResultData.error(ResultCode.ERROR);
        }finally {
            DataSourceContextHolder.clear();
        }
    }

    /**
     * 重新加载（全量）
     * @return
     */
    @RequestMapping(value = "/code_generate.json")
    @ResponseBody
    public ResultData codeGenerate(@RequestParam(value="checkIds[]",required=false) Set<String> entityCodes, HttpServletRequest request){
        logger.debug("request : {}", entityCodes);

        try{

            Map<String, String>  pageContextParams = DefaultController.getPageContextParams(request);
            WebContext.putContext(DefaultController.getPageContextRealyParams(pageContextParams));
            Map<String, String> pageFlowParams = WebContext.get(HashMap.class.getName());

            String companyCode = "hframe";
            String programCode = "hframe";
            Long programId = -1L;
            String programName = "框架";
            String moduleCode = "hframe";
            String moduleName = "框架";
            if(pageFlowParams != null) {
                if(pageFlowParams.containsKey("hfpmProgramId") && StringUtils.isNotBlank(pageFlowParams.get("hfpmProgramId"))) {
                    HfpmProgram program = hfpmProgramSV.getHfpmProgramByPK(Long.parseLong(pageFlowParams.get("hfpmProgramId")));
                    programId  = program.getHfpmProgramId();
                    programCode = program.getHfpmProgramCode();
                    programName = program.getHfpmProgramName();
                }
                if(pageFlowParams.containsKey("hfpmModuleId") && StringUtils.isNotBlank(pageFlowParams.get("hfpmModuleId"))) {
                    HfpmModule module = hfpmModuleSV.getHfpmModuleByPK(Long.parseLong(pageFlowParams.get("hfpmModuleId")));
                    moduleCode = module.getHfpmModuleCode();
                    moduleName = module.getHfpmModuleName();
                }
            }

            startDynamicDataSource(pageFlowParams);
            DataSourceContextHolder.DataSourceDescriptor dataSourceInfo = DataSourceContextHolder.getDBInfoAnyMore();
            String sql = SqlGeneratorUtil.getSqlContent(entityCodes);
            DataSourceContextHolder.clear();

            final HfModelContainer hfModelContainer = SQLParseUtil.parseModelContainerFromSQL(sql, null, null, null, null);
            HfModelContainer configModelContainer = HfModelService.get().getModelContainerFromDB(
                    programCode, programName, moduleCode, moduleName);
            List<Map<String, String>> tables = new ArrayList<Map<String, String>>();
            for (final String tableName : entityCodes) {
                final Entity entity = configModelContainer.getEntity(tableName);
                EntityAttr keyAttrInfo = configModelContainer.getEntityAttr(tableName, tableName + "_id");
                Long hfmdEntityAttrId = keyAttrInfo.getHfmdEntityAttrId();

                HfmdEntityAttr_Example example = new HfmdEntityAttr_Example();
                example.createCriteria().andRelHfmdEntityAttrIdEqualTo(hfmdEntityAttrId).andHfmdEntityIdEqualTo(entity.getHfmdEntityId());
                List<HfmdEntityAttr> hfmdEntityAttrListByExample = hfmdEntityAttrSV.getHfmdEntityAttrListByExample(example);

                HashMap<String, String> hashMap = new HashMap<String, String>() {{
                    put("tableName", tableName);
                    put("tableDesc", entity.getHfmdEntityName());
                    put("generatedKey", tableName + "_id");

                }};
                if(hfmdEntityAttrListByExample != null && hfmdEntityAttrListByExample.size() > 0) {
                    hashMap.put("parentId", hfmdEntityAttrListByExample.get(0).getHfmdEntityAttrCode());
                }
                tables.add(hashMap);
            }

            String projectBasePath = CreatorUtil.getTargetProjectBasePath(companyCode,
                    programCode, moduleCode);

            String projectRootPath = projectBasePath + "\\basic";
            String mybatisConfigFilePath = BaseGeneratorUtil.generateMybatisConfig(tables, projectRootPath,
                    WebContext.get().getProgram().getCompany(), programCode, moduleCode, dataSourceInfo);
            MyBatisGeneratorUtil.generate(new File(mybatisConfigFilePath));
            BaseGeneratorUtil.generator(mybatisConfigFilePath, companyCode, programCode, moduleCode);

            ShellExecutor.exeCmd(projectBasePath + "/build/compile.bat");

            return ResultData.success();
        }catch (Exception e) {
            logger.error("error : ", e);
            return ResultData.error(ResultCode.ERROR);
        }
    }
}
