package com.hframework.base.service;

import com.google.common.base.Joiner;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangquanhong on 2016/10/16.
 */
public class DataSetLoaderHelper {

    private static Map<String, DataSetLoaderService> dataSetLoaderServiceMap = new HashMap<String, DataSetLoaderService>();

    public static DataSetLoaderService getDataSetLoaderService(
            String companyCode, String programCode, String moduleCode) {
        if(moduleCode == null) moduleCode = "";
        String key = Joiner.on("|").join(new String[]{companyCode, programCode, moduleCode});

        if(!dataSetLoaderServiceMap.containsKey(key)) {
            synchronized (DataSetLoaderHelper.class) {
                if(!dataSetLoaderServiceMap.containsKey(key)) {
                    DataSetLoaderService dataSetLoaderService = new DataSetLoaderService();
                    dataSetLoaderService.init(companyCode, programCode, moduleCode);
                    dataSetLoaderServiceMap.put(key, dataSetLoaderService);
                }
            }
        }
        return dataSetLoaderServiceMap.get(key);
    }

}
