package com.altynbekova.aikamtask.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

/**
 * Статистика по покупателю
 */
@JsonPropertyOrder({"name", "purchases"})
public class Statistics {
    /**
     * Фамилия и имя покупателя
     */
    private String name;
    /**
     * Список уникальных товаров, купленных покупателем
     */
    private List<Purchase> purchases;
    /**
     * Общая стоимость покупок
     */
    @JsonProperty("totalExpenses")
    private int customerTotalExpenses;

    public Statistics() {
    }

    public Statistics(String name, List<Purchase> purchases) {
        this.name = name;
        this.purchases = purchases;
    }

    public String getName() {
        return name;
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }

    public int getCustomerTotalExpenses() {
        return customerTotalExpenses;
    }

    public void setCustomerTotalExpenses(int customerTotalExpenses) {
        this.customerTotalExpenses = customerTotalExpenses;
    }

    /**
     * Покупка определенного товара
     */
    @JsonPropertyOrder({"name", "expenses"})
    public static class Purchase {
        /**
         * Название товара
         */
        @JsonProperty("name")
        private String productName;
        /**
         * Суммарная стоимость всех покупок этого товара
         */
        private int expenses;

        public Purchase() {
        }

        public Purchase(String productName, int expenses) {
            this.productName = productName;
            this.expenses = expenses;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public int getExpenses() {
            return expenses;
        }

        public void setExpenses(int expenses) {
            this.expenses = expenses;
        }
    }
}