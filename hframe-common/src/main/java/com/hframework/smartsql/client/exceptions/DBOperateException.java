package com.hframework.smartsql.client.exceptions;

public class DBOperateException extends RuntimeException{
    private Exception parentException;
    private String message;
    public DBOperateException(Exception parentException) {
        super(parentException.getMessage(), parentException);
        this.parentException = parentException;
    }

    public DBOperateException(String message){
        super(message);
        this.message = message;
    }

    public Exception getParentException() {
        return parentException;
    }

    public void setParentException(Exception parentException) {
        this.parentException = parentException;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
