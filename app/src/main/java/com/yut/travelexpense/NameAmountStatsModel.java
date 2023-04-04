package com.yut.travelexpense;

public class NameAmountStatsModel {

    private String groupName;
    private double groupTotal;

    public NameAmountStatsModel(String groupName, double groupTotal) {
        this.groupName = groupName;
        this.groupTotal = groupTotal;
    }


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public double getGroupTotal() {
        return groupTotal;
    }

    public void setGroupTotal(double groupTotal) {
        this.groupTotal = groupTotal;
    }

}
