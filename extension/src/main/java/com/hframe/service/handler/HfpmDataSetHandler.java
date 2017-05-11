package com.hframe.service.handler;

import com.hframe.domain.model.HfpmDataSet;
import com.hframe.domain.model.HfpmProgram;
import com.hframe.service.interfaces.IHfpmDataSetSV;
import com.hframe.service.interfaces.IHfpmProgramSV;
import com.hframework.web.extension.AbstractBusinessHandler;
import com.hframework.base.service.DataSetLoaderHelper;
import com.hframework.base.service.DataSetLoaderService;
import com.hframework.web.extension.annotation.AfterCreateHandler;
import com.hframework.web.extension.annotation.AfterUpdateHandler;
import com.hframework.web.extension.annotation.BeforeDeleteHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zhangquanhong on 2016/10/16.
 */
@Service
public class HfpmDataSetHandler extends AbstractBusinessHandler<HfpmDataSet> {

    @Resource
    private IHfpmProgramSV hfpmProgramSV;
    @Resource
    private IHfpmDataSetSV hfpmDataSetSV;

    @AfterUpdateHandler
    @AfterCreateHandler
    public boolean add(HfpmDataSet hfpmDataSet) throws Exception {

        HfpmProgram hfpmProgram = hfpmProgramSV.getHfpmProgramByPK(hfpmDataSet.getHfpmProgramId());

        String companyCode = "hframe";
        String programCode = hfpmProgram.getHfpmProgramCode();

        DataSetLoaderService dataSetLoaderService = DataSetLoaderHelper.getDataSetLoaderService(
                companyCode, programCode, null);
        dataSetLoaderService.overrideDataSet(hfpmDataSet);
        dataSetLoaderService.updateDataSetXml(hfpmDataSet.getHfpmDataSetId());
        return true;
    }

    @BeforeDeleteHandler
    public boolean delete(HfpmDataSet hfpmDataSet) throws Exception {

        if(hfpmDataSet.getHfpmProgramId() == null) {
            hfpmDataSet = hfpmDataSetSV.getHfpmDataSetByPK(hfpmDataSet.getHfpmDataSetId());
        }
        HfpmProgram hfpmProgram = hfpmProgramSV.getHfpmProgramByPK(hfpmDataSet.getHfpmProgramId());

        String companyCode = "hframe";
        String programCode = hfpmProgram.getHfpmProgramCode();

        DataSetLoaderService dataSetLoaderService = DataSetLoaderHelper.getDataSetLoaderService(
                companyCode, programCode, null);
        dataSetLoaderService.removeDataSet(hfpmDataSet);
//        dataSetLoaderService.updateDataSetXml(hfpmDataSet.getHfpmDataSetId());

        return true;
    }
}
