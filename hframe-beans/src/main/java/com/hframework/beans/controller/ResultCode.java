package com.hframework.beans.controller;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangquanhong on 2016/5/24.
 */
public class ResultCode {

    public static final ResultCode SUCCESS = new ResultCode("0","成功");
    public static final ResultCode ERROR = new ResultCode("-1","系统异常");
    public static final ResultCode UNKNOW = new ResultCode("-1","未知错误");
    public static final ResultCode RECODE_IS_NOT_EXISTS = new ResultCode("1001","没有查询到对应记录！");

    private String errorCode;
    private String errorMsg;

    private static Map<String, ResultCode> errorCodeMap = new HashMap<String, ResultCode>();

    private ResultCode(String errorCode, String errorMsg) {

        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public static boolean isSuccess(String code) {
        return SUCCESS.getErrorCode().equals(code);
    }

    public static ResultCode get(String code) {
        if(errorCodeMap.get(code) != null) {
            return errorCodeMap.get(code);
        }else {
            return new ResultCode(code,"TODO");
        }
    }

    public static ResultCode get(String code, String errorMsg) {
        if(errorCodeMap.get(code) != null) {
            return errorCodeMap.get(code);
        }else {
            return new ResultCode(code,errorMsg);
        }
    }
}
