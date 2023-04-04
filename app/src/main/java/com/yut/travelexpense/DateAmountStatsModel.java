package com.yut.travelexpense;

import java.time.LocalDate;

public class DateAmountStatsModel {
    private LocalDate date;
    private double amount;

    public DateAmountStatsModel(LocalDate date, double amount) {
        this.date = date;
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
