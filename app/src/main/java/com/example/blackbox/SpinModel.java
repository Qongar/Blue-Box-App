package com.example.blackbox;

public class SpinModel {

    private String category;
    private int img;

    // constructor
    public SpinModel(String category, int img) {
        this.category = category;
        this.img = img;
    }

    // getter and setter
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
