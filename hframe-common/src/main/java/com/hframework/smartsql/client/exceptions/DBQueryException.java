package com.hframework.smartsql.client.exceptions;

public class DBQueryException extends DBOperateException {
    public DBQueryException(Exception parentException) {
        super(parentException);
    }

    public DBQueryException(String message) {
        super(message);
    }
}
