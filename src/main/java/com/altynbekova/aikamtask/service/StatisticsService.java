package com.altynbekova.aikamtask.service;

import com.altynbekova.aikamtask.dao.CustomerDao;
import com.altynbekova.aikamtask.dao.DaoFactory;
import com.altynbekova.aikamtask.exception.DaoException;
import com.altynbekova.aikamtask.exception.ServiceException;
import com.altynbekova.aikamtask.model.Statistics;
import com.altynbekova.aikamtask.model.StatisticsOutput;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис, выполняющий сбор статистики по покупателям
 */
public class StatisticsService {
    private static final long ONE = 1;

    /**
     * Собирает статистику по покупателям за период из двух дат, включительно, без выходных
     *
     * @param startDate начальная дата
     * @param endDate   конечная дата
     * @return статистика по покупателям
     * @throws ServiceException
     */
    public StatisticsOutput getStatistics(LocalDate startDate, LocalDate endDate) throws ServiceException {
        try (DaoFactory daoFactory = DaoFactory.createJdbcFactory()) {
            if (startDate == null || endDate == null)
                throw new ServiceException("startDate and/or endDate values cannot be empty");
            else if (startDate.isAfter(endDate))
                throw new ServiceException("startDate cannot be after endDate");

            StatisticsOutput statisticsOutput = new StatisticsOutput();
            CustomerDao customerDao = daoFactory.getCustomerDao();
            List<Statistics> statisticsList = customerDao.getStatistics(startDate, endDate);

            for (Statistics s : statisticsList) {
                int customerTotalExpenses = s.getPurchases().stream()
                        .mapToInt(Statistics.Purchase::getExpenses).sum();
                s.setCustomerTotalExpenses(customerTotalExpenses);
            }

            int totalExpenses = statisticsList.stream().mapToInt(Statistics::getCustomerTotalExpenses).sum();
            statisticsOutput.setTotalExpenses(totalExpenses);
            if (totalExpenses != 0)
                statisticsOutput.setAvgExpenses((double) totalExpenses / statisticsList.size());
            else
                statisticsOutput.setAvgExpenses(0);

            EnumSet<DayOfWeek> weekends = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
            int totalDays = 0;
            for (LocalDate i = startDate; i.isBefore(endDate) || i.isEqual(endDate); i = i.plusDays(ONE)) {
                if (!weekends.contains(i.getDayOfWeek()))
                    totalDays++;
            }
            statisticsOutput.setTotalDays(totalDays);

            statisticsOutput.setCustomersStatistics(statisticsList.stream()
                    .sorted(Comparator.comparing(Statistics::getCustomerTotalExpenses).reversed())
                    .collect(Collectors.toList()));

            return statisticsOutput;
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage() + e.getCause().getMessage(), e);
        }
    }
}