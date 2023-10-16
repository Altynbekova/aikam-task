package com.altynbekova.aikamtask.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

/**
 * Критерий поиска покупателей
 */
@JsonPropertyOrder({"lastName", "productName", "minTimes", "minExpenses", "maxExpenses", "badCustomers"})
@JsonInclude(NON_EMPTY)
public class Criterion {

    private String lastName;
    private String productName;
    private Integer minTimes;
    private Integer minExpenses;
    private Integer maxExpenses;
    private Integer badCustomers;

    public String getLastName() {
        return lastName;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getMinTimes() {
        return minTimes;
    }

    public Integer getMinExpenses() {
        return minExpenses;
    }

    public Integer getMaxExpenses() {
        return maxExpenses;
    }

    public Integer getBadCustomers() {
        return badCustomers;
    }
}