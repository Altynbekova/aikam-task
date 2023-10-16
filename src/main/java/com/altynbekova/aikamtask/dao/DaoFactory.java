package com.altynbekova.aikamtask.dao;

import com.altynbekova.aikamtask.exception.DaoException;
import com.altynbekova.aikamtask.exception.JdbcDaoException;

public abstract class DaoFactory implements AutoCloseable {
    public static DaoFactory createJdbcFactory() throws DaoException {
        try {
            return new JdbcDaoFactory();
        } catch (JdbcDaoException e) {
            throw new DaoException("Jdbc connection error", e);
        }
    }

    public abstract CustomerDao getCustomerDao();

    @Override
    public void close() throws DaoException {

    }
}