package com.hframework.beans.exceptions;

import com.hframework.beans.controller.ResultCode;
import com.hframework.beans.controller.ResultData;

/**
 * Created by zhangquanhong on 2016/10/19.
 */
public class BusinessException extends RuntimeException {
    private ResultCode resultCode;

    public BusinessException(String errorCode, String errorMsg) {
        resultCode = ResultCode.get(errorCode, errorMsg);
    }

    public BusinessException(String errorMsg) {
        resultCode = ResultCode.get("999", errorMsg);
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public ResultData result(){
        return ResultData.error(resultCode);
    }
}
