package com.altynbekova.aikamtask.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"type", "totalDays", "customers", "totalExpenses", "avgExpenses"})
public class StatisticsOutput {
    private static final String TYPE = "stat";
    private int totalDays;
    @JsonProperty("customers")
    private List<Statistics> customersStatistics;
    private int totalExpenses;
    private double avgExpenses;

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    public List<Statistics> getCustomersStatistics() {
        return customersStatistics;
    }

    public void setCustomersStatistics(List<Statistics> customersStatistics) {
        this.customersStatistics = customersStatistics;
    }

    public int getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(int totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public double getAvgExpenses() {
        return avgExpenses;
    }

    public void setAvgExpenses(double avgExpenses) {
        this.avgExpenses = avgExpenses;
    }

    public String getType() {
        return TYPE;
    }
}