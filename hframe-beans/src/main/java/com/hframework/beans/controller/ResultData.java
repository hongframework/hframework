package com.hframework.beans.controller;

import java.awt.geom.Area;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangquanhong on 2016/5/24.
 */
public class ResultData<T> {

    private String resultCode;
    private String resultMessage;
    private T data;

    public  static <K> ResultData success(){
        ResultData resultData = new ResultData(ResultCode.SUCCESS);
        return resultData;
    }
    public  static <K> ResultData success(K data){
        ResultData resultData = new ResultData(ResultCode.SUCCESS);
        resultData.setData(data);
        return resultData;
    }

    public  static <K> ResultData success(K data, String resultMessage){
        ResultData resultData = new ResultData(ResultCode.SUCCESS);
        resultData.setData(data);
        resultData.setResultMessage(resultMessage);
        return resultData;
    }

    public static ResultData error(String code){
        return new ResultData(ResultCode.get(code));
    }

    public static ResultData error(ResultCode resultCode){
        return new ResultData(resultCode);
    }

    private ResultData(){
        super();
    }
    private ResultData(ResultCode resultCode){
        this.resultCode = resultCode.getErrorCode();
        this.resultMessage = resultCode.getErrorMsg();
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ResultData add(String key, Object value) {
        if(data == null) {
            data = (T) new LinkedHashMap<String, Object>();
        }
        ((Map)data).put(key, value);
        return this;
    }

    public boolean isSuccess() {
        return ResultCode.SUCCESS.getErrorCode().equals(this.getResultCode());
    }

}
