package com.yut.travelexpense;

import java.time.LocalDate;
import java.util.List;

public class Section implements Comparable<Section> {

    private LocalDate header;
    private List<Transaction> transactions;

    public Section(LocalDate header, List<Transaction> transactions) {
        this.header = header;
        this.transactions = transactions;
    }

    public int getTransactionCount() {
        return transactions.size();
    }

    public LocalDate getHeader() {
        return header;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }


    @Override
    public int compareTo(Section o) {
        return Math.toIntExact(o.getHeader().toEpochDay() - header.toEpochDay());
    }
}
