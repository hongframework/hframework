package com.hframework.smartsql.client.exceptions;

public class DBUpdateException extends DBOperateException {
    public DBUpdateException(Exception parentException) {
        super(parentException);
    }

    public DBUpdateException(String message) {
        super(message);
    }
}
