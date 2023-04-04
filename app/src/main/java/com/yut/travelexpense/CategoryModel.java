package com.yut.travelexpense;

public class CategoryModel {

    private String name;
    private int imageURL;

    // TODO add color???

    public CategoryModel(String name, int imageURL) {
        this.name = name;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageURL() {
        return imageURL;
    }

    public void setImageURL(int imageURL) {
        this.imageURL = imageURL;
    }
}
