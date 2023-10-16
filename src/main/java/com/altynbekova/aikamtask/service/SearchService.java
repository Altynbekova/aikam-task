package com.altynbekova.aikamtask.service;

import com.altynbekova.aikamtask.dao.CustomerDao;
import com.altynbekova.aikamtask.dao.DaoFactory;
import com.altynbekova.aikamtask.exception.DaoException;
import com.altynbekova.aikamtask.exception.ServiceException;
import com.altynbekova.aikamtask.model.Criterion;
import com.altynbekova.aikamtask.model.SearchOutput;
import com.altynbekova.aikamtask.model.SearchResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис, выполняющий поиск покупателей по заданным критериям.
 * <p>Критерии:
 * <ul>
 *     <li>Фамилия — поиск покупателей с этой фамилией
 *     <li>Название товара и число раз — поиск покупателей, купивших этот товар не менее, чем указанное число раз
 *     <li>Минимальная и максимальная стоимость всех покупок — поиск покупателей, у которых
 *     общая стоимость всех покупок за всё время попадает в интервал
 *     <li>Число пассивных покупателей — поиск покупателей, купивших меньше всего товаров.
 *     Возвращается не более, чем указанное число покупателей.
 * </ul></p>
 */
public class SearchService {
    /**
     * Осуществляет поиск покупателей по критериям
     *
     * @param criteria список критериев для поиска покупателей
     * @return списки покупателей для каждого критерия из запроса.
     * Порядок списков такой же как в запросе, порядок покупателей в списке — произвольный
     * @throws ServiceException
     */
    public SearchOutput findCustomers(List<Criterion> criteria) throws ServiceException {
        try (DaoFactory daoFactory = DaoFactory.createJdbcFactory()) {
            CustomerDao customerDao = daoFactory.getCustomerDao();
            List<SearchResult> searchResults = new ArrayList<>();

            for (Criterion c : criteria) {
                SearchResult result = new SearchResult();
                result.setCriterion(c);

                if (c.getProductName() != null && c.getMinTimes() != null && c.getMinTimes() < 0)
                    throw new ServiceException("Criterion minTimes cannot be negative");
                else if (c.getMinExpenses() != null && c.getMaxExpenses() != null &&
                        c.getMinExpenses() > c.getMaxExpenses())
                    throw new ServiceException("Criterion minExpenses cannot be greater than maxExpenses");
                else if (c.getMinExpenses() != null && c.getMaxExpenses() != null &&
                        (c.getMinExpenses() < 0 || c.getMaxExpenses() < 0))
                    throw new ServiceException("Criteria minExpenses and maxExpenses cannot be negative");
                else if (c.getBadCustomers() != null && c.getBadCustomers() < 0)
                    throw new ServiceException("Criterion badCustomers cannot be negative");

                if (c.getLastName() != null)
                    result.setCustomers(customerDao.findByLastName(c.getLastName()));
                else if (c.getProductName() != null && c.getMinTimes() != null && c.getMinTimes() >= 0)
                    result.setCustomers(customerDao.findByProduct(c.getProductName(), c.getMinTimes()));
                else if (c.getMinExpenses() != null && c.getMaxExpenses() != null &&
                        c.getMinExpenses() < c.getMaxExpenses() && c.getMinExpenses() > 0)
                    result.setCustomers(customerDao.findInRange(c.getMinExpenses(), c.getMaxExpenses()));
                else if (c.getBadCustomers() != null && c.getBadCustomers() >= 0)
                    result.setCustomers(customerDao.findBadCustomers(c.getBadCustomers()));
                else throw new ServiceException("Some of the search criteria are invalid");

                searchResults.add(result);
            }

            return new SearchOutput(searchResults);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}