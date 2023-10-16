package com.altynbekova.aikamtask.exception;

public class JdbcDaoException extends DaoException {
    public JdbcDaoException(String message) {
        super(message);
    }

    public JdbcDaoException(String message, Throwable cause) {
        super(message, cause);
    }
}