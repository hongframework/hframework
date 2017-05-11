package com.hframework.web.context;

import com.hframework.common.springext.properties.PropertyConfigurerUtils;
import com.hframework.common.util.StringUtils;
import com.hframework.common.util.message.XmlUtils;
import com.hframework.web.config.bean.Program;

import java.io.File;
import java.io.IOException;

/**
 * Created by zhangquanhong on 2016/8/26.
 */
public class WebContextHelper {

    private String companyCode;//公司编码
    private String programCode;//项目编码
    private String moduleCode;//模块编码
    public String templateCode;//模板编码


    public String programConfigRootDir = null;//项目配置根目录
    public String programTemplateUnpackDir = null;//项目模板配置编译解包存放位置
    public String programConfigDataSetDir = null;//数据集配置目录
    public String programConfigDataSetHelperDir = null;//数据集助手配置目录
    public String programConfigDataSetRulerDir = null;//数据集规则配置目录

    public String programConfigModuleDir = null;//模块与页面配置目录
    public String programConfigMapperDir = null;//组件数据集特殊关系配置目录
    public String programConfigProgramFile = null;//项目配置文件

    public String templateResourceEventStoreDir = null;//事件模板目录
    public String templateResourceComponentDir = null;//组件模板目录
    public String templateResourceComponentMapperDir = null;//组件模板映射目录
    public String templateResourcePageDescriptorFile = null;//页面模板文件

    public WebContextHelper(String companyCode, String programCode, String moduleCode, String templateCode) {
        this.companyCode = companyCode;
        this.programCode = programCode;
        this.moduleCode = moduleCode;
        this.templateCode = templateCode;
        format();
    }

    public WebContextHelper() throws IOException {
        Program programInfo = getProgramInfo();
        this.companyCode = programInfo.getCompany();
        this.programCode = programInfo.getCode();
        this.templateCode = programInfo.getTemplate().getCode();
        format();
    }

    public static Program getProgramInfo() throws IOException {
        String programConfigProgramFile = PropertyConfigurerUtils.getProperty(ProgramResourceConst.PROGRAM_CONFIG_PROGRAM_File);
        return  XmlUtils.readValueFromFile(programConfigProgramFile, Program.class);
    }

    private void format() {
        String projectsRoot = PropertyConfigurerUtils.containProperty(ProgramResourceConst.TARGET_PROJECT_BASE_PATH)
                && StringUtils.isNotBlank(PropertyConfigurerUtils.getProperty(ProgramResourceConst.TARGET_PROJECT_BASE_PATH))?
                PropertyConfigurerUtils.getProperty(ProgramResourceConst.TARGET_PROJECT_BASE_PATH) :
                WebContextHelper.class.getClassLoader().getResource("").getPath() + "/projects";

        programConfigRootDir =  projectsRoot  + File.separatorChar +
                PropertyConfigurerUtils.getProperty(ProgramResourceConst.TARGET_PROJECT_NAME,
                companyCode ,programCode,moduleCode,templateCode)
                + File.separatorChar
                + PropertyConfigurerUtils.getProperty(ProgramResourceConst.PROGRAM_CONFIG_ROOT_DIR,
                companyCode ,programCode,moduleCode,templateCode);
        programTemplateUnpackDir =  projectsRoot  + File.separatorChar +
                PropertyConfigurerUtils.getProperty(ProgramResourceConst.TARGET_PROJECT_NAME,
                        companyCode ,programCode,moduleCode,templateCode)
                + File.separatorChar
                + PropertyConfigurerUtils.getProperty(ProgramResourceConst.PROGRAM_TEMPLATE_UNPACK_DIR,
                companyCode ,programCode,moduleCode,templateCode);
        programConfigDataSetDir = PropertyConfigurerUtils.getProperty(ProgramResourceConst.PROGRAM_CONFIG_DATA_SET_DIR,companyCode,programCode,moduleCode,templateCode);
        programConfigDataSetHelperDir = PropertyConfigurerUtils.getProperty(ProgramResourceConst.PROGRAM_CONFIG_DATA_SET_HELPER_DIR,companyCode,programCode,moduleCode,templateCode);
        programConfigDataSetRulerDir = PropertyConfigurerUtils.getProperty(ProgramResourceConst.PROGRAM_CONFIG_DATA_SET_RULER_DIR,companyCode,programCode,moduleCode,templateCode);

        programConfigModuleDir = PropertyConfigurerUtils.getProperty(ProgramResourceConst.PROGRAM_CONFIG_MODULE_DIR,companyCode,programCode,moduleCode,templateCode);
        programConfigMapperDir = PropertyConfigurerUtils.getProperty(ProgramResourceConst.PROGRAM_CONFIG_MAPPER_DIR,companyCode,programCode,moduleCode,templateCode);
        programConfigProgramFile = PropertyConfigurerUtils.getProperty(ProgramResourceConst.PROGRAM_CONFIG_PROGRAM_File,companyCode,programCode,moduleCode,templateCode);

        templateResourceEventStoreDir = PropertyConfigurerUtils.getProperty(ProgramResourceConst.TEMPLATE_RESOURCE_EVENT_STORE_DIR,companyCode,programCode,moduleCode,templateCode);
        templateResourceComponentDir = PropertyConfigurerUtils.getProperty(ProgramResourceConst.TEMPLATE_RESOURCE_COMPONENT_DIR,companyCode,programCode,moduleCode,templateCode);
        templateResourceComponentMapperDir = PropertyConfigurerUtils.getProperty(ProgramResourceConst.TEMPLATE_RESOURCE_COMPONENT_MAPPER_DIR,companyCode,programCode,moduleCode,templateCode);
        templateResourcePageDescriptorFile = PropertyConfigurerUtils.getProperty(ProgramResourceConst.TEMPLATE_RESOURCE_PAGE_DESCRIPTOR_FILE,companyCode,programCode,moduleCode,templateCode);
    }


    private class ProgramResourceConst{

        public static final String TARGET_PROJECT_BASE_PATH = "target_project_base_path";
        public static final String TARGET_PROJECT_NAME = "target_project_name";


        public static final String PROGRAM_CONFIG_ROOT_DIR = "program_config_root_dir";
        public static final String PROGRAM_TEMPLATE_UNPACK_DIR = "program_template_unpack_dir";
        public static final String PROGRAM_CONFIG_DATA_SET_DIR = "program_config_data_set_dir";
        public static final String PROGRAM_CONFIG_DATA_SET_HELPER_DIR = "program_config_data_set_helper_dir";
        public static final String PROGRAM_CONFIG_DATA_SET_RULER_DIR = "program_config_data_set_ruler_dir";

        public static final String PROGRAM_CONFIG_MODULE_DIR = "program_config_module_dir";
        public static final String PROGRAM_CONFIG_MAPPER_DIR = "program_config_mapper_dir";
        public static final String PROGRAM_CONFIG_PROGRAM_File = "program_config_program_file";

        public static final String TEMPLATE_RESOURCE_EVENT_STORE_DIR = "template_resource_event_store_dir";
        public static final String TEMPLATE_RESOURCE_COMPONENT_DIR = "template_resource_component_dir";
        public static final String TEMPLATE_RESOURCE_COMPONENT_MAPPER_DIR = "template_resource_component_mapper_dir";
        public static final String TEMPLATE_RESOURCE_PAGE_DESCRIPTOR_FILE = "template_resource_page_descriptor_file";

    }

}
