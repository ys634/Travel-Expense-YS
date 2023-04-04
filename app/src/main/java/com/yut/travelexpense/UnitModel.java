package com.yut.travelexpense;

public class UnitModel {

    private String shortName;
    private String fullName;

    public UnitModel(String shortName, String fullName) {
        this.shortName = shortName;
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getFullName() {
        return fullName;
    }
}
