package com.altynbekova.aikamtask.dao;

import com.altynbekova.aikamtask.exception.JdbcDaoException;
import com.altynbekova.aikamtask.model.Customer;
import com.altynbekova.aikamtask.model.Statistics;

import java.sql.*;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerDao {
    private static final String FIND_BY_LAST_NAME_QUERY = "select * from customers where customers.last_name like ?";
    private static final String FIND_ALL_QUERY = "select * from customers";
    private static final String FIND_BY_PRODUCT_QUERY = "select first_name, last_name " +
            "from customers join purchases on customers.id = customer_id " +
            "join purchases_products on purchases.id = purchases_products.purchase_id " +
            "join products on product_id = products.id " +
            "where products.name LIKE ? " +
            "group by customer_id, first_name, last_name " +
            "having count(product_id)>=?";
    private static final String FIND_EXPENSES_LESS_THAN_QUERY = "(select first_name, last_name " +
            "from customers left join purchases p on customers.id = p.customer_id " +
            "where p.id is NULL) " +
            "UNION " +
            "(select first_name, last_name " +
            "from " +
            "customers join purchases p on customers.id = p.customer_id " +
            "join purchases_products pp on p.id = pp.purchase_id " +
            "join products p2 on pp.product_id = p2.id " +
            "group by customer_id, first_name, last_name " +
            "having sum(price)<=?)";
    private static final String FIND_IN_RANGE_QUERY = "select first_name, last_name " +
            "from customers join purchases p on customers.id = p.customer_id " +
            "join purchases_products pp on p.id = pp.purchase_id " +
            "join products p2 on pp.product_id = p2.id " +
            "group by customer_id, first_name, last_name " +
            "having sum(price)>=? and sum(price)<=?";
    private static final String FIND_BAD_CUSTOMERS_QUERY = "(select first_name, last_name, 0 as count " +
            "from customers left join purchases p on customers.id = p.customer_id " +
            "where p.id is NULL) " +
            "UNION " +
            "(select first_name, last_name, count(product_id) as count " +
            "from " +
            "customers join purchases on customers.id = customer_id " +
            "join purchases_products on purchases.id = purchases_products.purchase_id " +
            "join products on product_id = products.id " +
            "group by customer_id, first_name, last_name) " +
            "order by count, last_name " +
            "limit ?";
    private static final String GET_STATISTICS_QUERY = "select customer_id, first_name, last_name, products.name, sum(price) as expenses " +
            "from customers join purchases on customers.id = customer_id " +
            "join purchases_products on purchases.id = purchases_products.purchase_id " +
            "join products on product_id = products.id " +
            "where date>=? and date<=? and extract(isodow from date)<6 " +
            "group by customer_id, first_name, last_name, product_id, products.name " +
            "order by customer_id, sum(price)";
    private static final int INDEX_1 = 1;
    private static final int INDEX_2 = 2;
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String CUSTOMER_ID = "customer_id";
    private static final String EXPENSES = "expenses";
    private static final String PRODUCT_NAME = "name";
    private Connection connection;

    public CustomerDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Поиск покупателей по заданной фамилии
     *
     * @param lastName фамилия покупателя
     * @return список найденных покупателей
     */
    public List<Customer> findByLastName(String lastName) throws JdbcDaoException {
        try (PreparedStatement ps = connection.prepareStatement(FIND_BY_LAST_NAME_QUERY)) {
            List<Customer> customers = new ArrayList<>();
            ps.setString(INDEX_1, lastName);
            ResultSet rs = ps.executeQuery();
            mapResultSetTo(customers, rs);
            return customers;
        } catch (SQLException e) {
            throw new JdbcDaoException(MessageFormat.format(
                    "Cannot find customers by last name {0}", lastName), e);
        }
    }

    /**
     * Поиск покупателей, купивших заданный товар не менее, чем указанное число раз
     *
     * @param productName - название товара
     * @param minTimes    - минимальное число покупок этого товара
     * @return список найденных покупателей
     */
    public List<Customer> findByProduct(String productName, int minTimes) throws JdbcDaoException {
        String query = minTimes > 0 ? FIND_BY_PRODUCT_QUERY : FIND_ALL_QUERY;
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            List<Customer> customers = new ArrayList<>();
            if (minTimes > 0) {
                ps.setString(INDEX_1, productName);
                ps.setInt(INDEX_2, minTimes);
            }
            ResultSet rs = ps.executeQuery();
            mapResultSetTo(customers, rs);
            return customers;
        } catch (SQLException e) {
            throw new JdbcDaoException(MessageFormat.format(
                    "Cannot find customers who bought product {0} at least {1} times",
                    productName, minTimes), e);
        }
    }

    /**
     * Поиск покупателей, у которых общая стоимость всех покупок за всё время
     * попадает в заданный интервал
     *
     * @param minExpenses - минимальная стоимость
     * @param maxExpenses - максимальная стоимость
     * @return список найденных покупателей
     */
    public List<Customer> findInRange(int minExpenses, int maxExpenses) throws JdbcDaoException {
        String query = minExpenses == 0 ? FIND_EXPENSES_LESS_THAN_QUERY : FIND_IN_RANGE_QUERY;
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            List<Customer> customers = new ArrayList<>();
            if (minExpenses == 0) {
                ps.setInt(INDEX_1, maxExpenses);
            } else {
                ps.setInt(INDEX_1, minExpenses);
                ps.setInt(INDEX_2, maxExpenses);
            }
            ResultSet rs = ps.executeQuery();
            mapResultSetTo(customers, rs);
            return customers;
        } catch (SQLException e) {
            throw new JdbcDaoException(MessageFormat.format(
                    "Cannot find customers who spent between {0} and {1}", minExpenses, maxExpenses), e);
        }
    }

    /**
     * Поиск покупателей, купивших меньше всего товаров.
     * Возвращается не более, чем указанное число покупателей
     *
     * @param maxNumber - максимальное число пассивных покупателей
     * @return список найденных покупателей
     */
    public List<Customer> findBadCustomers(int maxNumber) throws JdbcDaoException {
        try (PreparedStatement ps = connection.prepareStatement(FIND_BAD_CUSTOMERS_QUERY)) {
            List<Customer> customers = new ArrayList<>();
            ps.setInt(INDEX_1, maxNumber);
            ResultSet rs = ps.executeQuery();
            mapResultSetTo(customers, rs);
            return customers;
        } catch (SQLException e) {
            throw new JdbcDaoException(MessageFormat.format(
                    "Cannot find at most {0} customers who bought the least number of products", maxNumber), e);
        }
    }

    /**
     * Собирает статистику по покупателям за определенный период
     *
     * @param startDate начальная дата периода
     * @param endDate   конечная дата периода
     * @return статистика за заданный период из двух дат, включительно, без выходных
     * @throws JdbcDaoException
     */
    public List<Statistics> getStatistics(LocalDate startDate, LocalDate endDate) throws JdbcDaoException {
        try (PreparedStatement ps = connection.prepareStatement(GET_STATISTICS_QUERY)) {
            List<Statistics> statisticsList = new ArrayList<>();
            Map<Integer, String> customersNames = new HashMap<>();
            Map<Integer, List<Statistics.Purchase>> customersPurchases = new HashMap<>();

            ps.setDate(INDEX_1, Date.valueOf(startDate));
            ps.setDate(INDEX_2, Date.valueOf(endDate));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt(CUSTOMER_ID);
                if (!customersNames.containsKey(id)) {
                    customersNames.put(id, rs.getString(LAST_NAME) + " " + rs.getString(FIRST_NAME));
                    customersPurchases.put(id, new ArrayList<>());
                }
                customersPurchases.get(id).add(
                        new Statistics.Purchase(rs.getString(PRODUCT_NAME),
                                rs.getInt(EXPENSES)));
            }

            for (Integer i : customersNames.keySet())
                statisticsList.add(new Statistics(customersNames.get(i), customersPurchases.get(i)));
            return statisticsList;

        } catch (SQLException e) {
            throw new JdbcDaoException(MessageFormat.format(
                    "Cannot get statistics on customers between {0} and {1}", startDate, endDate), e);
        }
    }

    private void mapResultSetTo(List<Customer> customers, ResultSet rs) throws SQLException {
        while (rs.next()) {
            Customer customer = new Customer(rs.getString(FIRST_NAME), rs.getString(LAST_NAME));
            customers.add(customer);
        }
    }
}