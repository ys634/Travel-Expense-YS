package com.yut.travelexpense;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.time.LocalDate;

public class Transaction implements Parcelable, Comparable<Transaction> {
    // Sorted by date

    private double originalAmount;
    private double convertedAmount;
    private String currency;
    private String category;
    private String description;
    private String country;
    private LocalDate date;
    private int id;

    public Transaction(double originalAmount, double convertedAmount, String currency, String category,
                       String description, String country, LocalDate date, int id) {
        this.originalAmount = originalAmount;
        this.convertedAmount = convertedAmount;
        this.currency = currency;
        this.category = category;
        this.description = description;
        this.country = country;
        this.date = date;
        this.id = id;

    }

    public Transaction(){
    };


    public Transaction(double originalAmount, double convertedAmount, String category, String description) {
        this.originalAmount = originalAmount;
        this.convertedAmount = convertedAmount;
        this.category = category;
        this.description = description;
    }

    protected Transaction(Parcel in) {
        originalAmount = in.readDouble();
        convertedAmount = in.readDouble();
        currency = in.readString();
        category = in.readString();
        description = in.readString();
        country = in.readString();
        if (in.readLong() == -1) {
            date = null;
        } else {
            date = LocalDate.ofEpochDay(in.readLong());
        }
        id = in.readInt();
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    public double getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(double originalAmount) {
        this.originalAmount = originalAmount;
    }

    public double getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(double convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeDouble(originalAmount);
        dest.writeDouble(convertedAmount);
        dest.writeString(currency);
        dest.writeString(category);
        dest.writeString(description);
        dest.writeString(country);
//        if (date == null) {
//            dest.writeLong(-1);
//        } else {
            dest.writeLong(date.toEpochDay());
//        }
        dest.writeInt(id);
    }

    @Override
    public int compareTo(Transaction o) {
        return Math.toIntExact(o.getDate().toEpochDay() - this.date.toEpochDay()) == 0
                ? o.getId() - this.getId()
                : Math.toIntExact(o.getDate().toEpochDay() - this.date.toEpochDay());
    }
}
