package com.hframework.controller.ext;

import com.hframe.domain.model.*;
import com.hframe.service.interfaces.*;
import com.hframework.base.service.CommonDataService;
import com.hframework.base.service.DataSetLoaderService;
import com.hframework.base.service.ModelLoaderService;
import com.hframework.beans.controller.ResultCode;
import com.hframework.beans.controller.ResultData;
import com.hframework.common.util.StringUtils;
import com.hframework.web.controller.DefaultController;
import com.hframework.common.springext.datasource.DataSourceContextHolder;
import com.hframework.web.context.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by zhangquanhong on 2016/12/11.
 */
@Controller
@RequestMapping(value = "/extend")
public class MenuGeneratorController extends ExtBaseController {
    private static final Logger logger = LoggerFactory.getLogger(MenuGeneratorController.class);
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
     * menuChart初始化处理
     * @return
     */
    @RequestMapping(value = "/menu_chart.json")
    @ResponseBody
    public ResultData getMenuChart(HttpServletRequest request){
        logger.debug("request : {}");
        try{
            Map<String, String>  pageContextParams = DefaultController.getPageContextParams(request);
            WebContext.putContext(DefaultController.getPageContextRealyParams(pageContextParams));
            Map<String, String> pageFlowParams = WebContext.get(HashMap.class.getName());

            String companyCode = "hframe";
            String programCode = "hframe";
            String programeName = "框架";
            String moduleCode = "hframe";
            String moduleName = "框架";
            if(pageFlowParams != null) {
                if(pageFlowParams.containsKey("hfpmProgramId") && StringUtils.isNotBlank(pageFlowParams.get("hfpmProgramId"))) {
                    HfpmProgram program = hfpmProgramSV.getHfpmProgramByPK(Long.parseLong(pageFlowParams.get("hfpmProgramId")));
                    programCode = program.getHfpmProgramCode();
                    programeName = program.getHfpmProgramName();
                }
                if(pageFlowParams.containsKey("hfpmModuleId") && StringUtils.isNotBlank(pageFlowParams.get("hfpmModuleId"))) {
                    HfpmModule module = hfpmModuleSV.getHfpmModuleByPK(Long.parseLong(pageFlowParams.get("hfpmModuleId")));
                    moduleCode = module.getHfpmModuleCode();
                    moduleName = module.getHfpmModuleName();
                }
            }

            startDynamicDataSource(pageFlowParams);
            HfsecMenu_Example hfsecMenuExample = new HfsecMenu_Example();
            HfsecMenu hfsecMenu = new HfsecMenu();
            hfsecMenu.setParentHfsecMenuId(-1L);
            final Map<Long, List<HfsecMenu>> result = hfsecMenuSV.getHfsecMenuTreeByParentId(hfsecMenu, hfsecMenuExample);

            HfsecMenu  virtualNode = new HfsecMenu();
            virtualNode.setParentHfsecMenuId(-1L);
            virtualNode.setHfsecMenuId(-2L);
            virtualNode.setHfsecMenuName("未设置菜单");
            if(!result.containsKey(-1L)) result.put(-1L, new ArrayList<HfsecMenu>());
            result.get(-1L).add(virtualNode);

            hfsecMenuExample = new HfsecMenu_Example();
            hfsecMenu.setParentHfsecMenuId(-2L);
            Map<Long, List<HfsecMenu>> virtualMenus = hfsecMenuSV.getHfsecMenuTreeByParentId(hfsecMenu, hfsecMenuExample);
            if(virtualMenus.containsKey(-2L)) {

                result.putAll(virtualMenus);
            }

            return ResultData.success(new HashMap<String,Object>(){{
                put("AllMenuTree",result);
            }});
        }catch (Exception e) {
            logger.error("error : ", e);
            return ResultData.error(ResultCode.ERROR);
        }finally {
            DataSourceContextHolder.clear();
        }
    }


    /**
     * 数据保存
     * @return
     */
    @RequestMapping(value = "/save_menu.json")
    @ResponseBody
    public ResultData saveData(HttpServletRequest request,
                               HttpServletResponse response){

        try {
            Map<String, String>  pageContextParams = DefaultController.getPageContextParams(request);
            WebContext.putContext(DefaultController.getPageContextRealyParams(pageContextParams));
            Map<String, String> pageFlowParams = WebContext.get(HashMap.class.getName());
            startDynamicDataSource(pageFlowParams);
            ResultData resultData = new DefaultController().saveData(request, response);
            return resultData;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            DataSourceContextHolder.clear();
        }

        return ResultData.error(ResultCode.UNKNOW);
    }




}
