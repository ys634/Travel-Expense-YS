package com.yut.travelexpense;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.util.ArrayList;

public class TripModel implements Parcelable, Comparable<TripModel> {

    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double budget;
    private int id;
    private CurrencyModel homeCurrency;
    private ArrayList<Transaction> transactions;
    private boolean isCurrentTrip;
    private boolean isOngoing;

    public TripModel(String name, LocalDate startDate, LocalDate endDate, Double budget, int id,
                     CurrencyModel homeCurrency, ArrayList<Transaction> transactions, boolean isCurrentTrip, boolean isOngoing) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.id = id;
        this.homeCurrency = homeCurrency;
        this.transactions = transactions;
        this.isCurrentTrip = isCurrentTrip;
        this.isOngoing = isOngoing;
    }

    public TripModel() {
    }



    protected TripModel(Parcel in) {
        name = in.readString();
        if (in.readLong() == -1) {
            startDate = null;
        } else {
            startDate = LocalDate.ofEpochDay(in.readLong());
        }
        if (in.readLong() == -1) {
            endDate = null;
        } else {
            endDate = LocalDate.ofEpochDay(in.readLong());
        }
        if (in.readByte() == 0) {
            budget = null;
        } else {
            budget = in.readDouble();
        }
        id = in.readInt();
        homeCurrency = in.readParcelable(CurrencyModel.class.getClassLoader());
        transactions = in.createTypedArrayList(Transaction.CREATOR);
        isCurrentTrip = in.readByte() != 0;
        isOngoing = in.readByte() != 0;
    }

    public static final Creator<TripModel> CREATOR = new Creator<TripModel>() {
        @Override
        public TripModel createFromParcel(Parcel in) {
            return new TripModel(in);
        }

        @Override
        public TripModel[] newArray(int size) {
            return new TripModel[size];
        }
    };


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CurrencyModel getHomeCurrency() {
        return homeCurrency;
    }

    public void setHomeCurrency(CurrencyModel homeCurrency) {
        this.homeCurrency = homeCurrency;
    }



    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public boolean getIsCurrentTrip() {
        return isCurrentTrip;
    }

    public void setIsCurrentTrip(boolean currentTrip) {
        isCurrentTrip = currentTrip;
    }


    public boolean getIsOngoing() {
        return isOngoing;
    }

    public void setIsOngoing(boolean ongoing) {
        isOngoing = ongoing;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
//        if (startDate == null) {
//            dest.writeLong(-1);
//        } else {
            dest.writeLong(startDate.toEpochDay());
//        }
//        if (endDate == null) {
//            dest.writeLong(-1);
//        } else {
            dest.writeLong(endDate.toEpochDay());
//        }
        if (budget == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(budget);
        }
        dest.writeInt(id);
        dest.writeParcelable(homeCurrency, flags);
        dest.writeTypedList(transactions);
        dest.writeByte((byte) (isCurrentTrip ? 1 : 0));
        dest.writeByte((byte) (isOngoing ? 1 : 0));
    }


    @Override
    public int compareTo(TripModel o) {
        return Math.toIntExact(o.getStartDate().toEpochDay() - this.startDate.toEpochDay());
    }
}
