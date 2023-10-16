package com.altynbekova.aikamtask.dao;

import com.altynbekova.aikamtask.exception.JdbcDaoException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcDaoFactory extends DaoFactory {
    private static final String URL = "db.connection.url";
    private static final String LOGIN = "db.connection.user.login";
    private static final String PASSWORD = "db.connection.user.password";
    private static final String DB_PROPERTIES = "db.properties";
    private Connection connection;

    public JdbcDaoFactory() throws JdbcDaoException {
        try (InputStream in = JdbcDaoFactory.class.getClassLoader().getResourceAsStream(DB_PROPERTIES)) {
            Properties properties = new Properties();
            properties.load(in);
            String url = properties.getProperty(URL);
            String login = properties.getProperty(LOGIN);
            String password = properties.getProperty(PASSWORD);
            this.connection = DriverManager.getConnection(url, login, password);
        } catch (SQLException e) {
            //LOG.error("Cannot get connection. DB access error occurred or the url is invalid.", e.getMessage(), e);
            throw new JdbcDaoException("Cannot get connection. DB access error occurred or the url is invalid.", e);
        } catch (IOException e) {
            throw new JdbcDaoException("Cannot load file db.properties from resources", e);
        }
    }

    public CustomerDao getCustomerDao() {
        return new CustomerDao(connection);
    }

    @Override
    public void close() throws JdbcDaoException {
        try {
            if (!connection.isClosed())
                connection.close();
        } catch (SQLException e) {
            throw new JdbcDaoException("Cannot close connection", e);
        }
    }
}