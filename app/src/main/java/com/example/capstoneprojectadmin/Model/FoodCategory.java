package com.example.capstoneprojectadmin.Model;

public class FoodCategory {
    private String foodCatName;
    private String foodCatImageURL;

    public FoodCategory() {

    }

    public FoodCategory(String foodCatName, String foodCatImageURL) {
        this.foodCatName = foodCatName;
        this.foodCatImageURL = foodCatImageURL;
    }

    public String getFoodCatName() {
        return foodCatName;
    }

    public String getFoodCatImageURL() {
        return foodCatImageURL;
    }

    public void setFoodCatName(String foodCatName) {
        this.foodCatName = foodCatName;
    }

    public void setFoodImageURL(String foodCatImageURL) {
        this.foodCatImageURL = foodCatImageURL;
    }
}
