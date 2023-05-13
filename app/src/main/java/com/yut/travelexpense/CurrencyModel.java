package com.yut.travelexpense;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CurrencyModel implements Parcelable {

    private String shortName;
    private String fullName;
    private String symbol;

    public CurrencyModel(String shortName, String fullName, String symbol) {
        this.shortName = shortName;
        this.fullName = fullName;
        this.symbol = symbol;
    }

    public CurrencyModel() {

    }

    protected CurrencyModel(Parcel in) {
        shortName = in.readString();
        fullName = in.readString();
        symbol = in.readString();
    }

    public static final Creator<CurrencyModel> CREATOR = new Creator<CurrencyModel>() {
        @Override
        public CurrencyModel createFromParcel(Parcel in) {
            return new CurrencyModel(in);
        }

        @Override
        public CurrencyModel[] newArray(int size) {
            return new CurrencyModel[size];
        }
    };

    public String getShortName() {
        return shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(shortName);
        dest.writeString(fullName);
        dest.writeString(symbol);
    }
}
