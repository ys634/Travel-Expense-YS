package com.yut.travelexpense;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.time.LocalDate;

public class Transaction implements Parcelable, Comparable<Transaction> {
    // Sorted by date

    private double originalAmount;
    private String currency;
    private String category;
    private String description;
    private String country;
    private LocalDate date;
    private int id;
    private boolean noStats;
    private long spread;

    public Transaction(double originalAmount, String currency, String category,
                       String description, String country, LocalDate date, int id, boolean noStats,
                       long spread) {
        this.originalAmount = originalAmount;
        this.currency = currency;
        this.category = category;
        this.description = description;
        this.country = country;
        this.date = date;
        this.id = id;
        this.noStats = noStats;
        this.spread = spread;

    }

    public Transaction(){
    };

    public Transaction(Transaction original) {
        this.originalAmount = original.originalAmount;
        this.currency = original.currency;
        this.category = original.category;
        this.description = original.description;
        this.country = original.country;
        this.date = original.date;
        this.id = original.id;
        this.noStats = original.noStats;
        this.spread = original.spread;
    }


    public Transaction(double originalAmount, String category, String description) {
        this.originalAmount = originalAmount;
        this.category = category;
        this.description = description;
    }

    protected Transaction(Parcel in) {
        originalAmount = in.readDouble();
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
        noStats = in.readByte() != 0;
        spread = in.readInt();

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

    public boolean getNoStats() {
        return noStats;
    }

    public void setNoStats(boolean noStats) {
        this.noStats = noStats;
    }

    public long getSpread() {
        return spread;
    }

    public void setSpread(long spread) {
        this.spread = spread;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeDouble(originalAmount);
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
        dest.writeByte((byte) (noStats ? 1 : 0));
        dest.writeLong(spread);

    }

    @Override
    public int compareTo(Transaction o) {
        return Math.toIntExact(o.getDate().toEpochDay() - this.date.toEpochDay()) == 0
                ? o.getId() - this.getId()
                : Math.toIntExact(o.getDate().toEpochDay() - this.date.toEpochDay());
    }

}
