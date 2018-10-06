package com.hframework.smartsql.client.exceptions;

public class DBInitializeException extends DBOperateException {

    public DBInitializeException(Exception parentException) {
        super(parentException);
    }

    public DBInitializeException(String message) {
        super(message);
    }
}
