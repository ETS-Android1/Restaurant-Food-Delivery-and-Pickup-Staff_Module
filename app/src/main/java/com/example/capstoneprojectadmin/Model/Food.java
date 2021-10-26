package com.example.capstoneprojectadmin.Model;

public class Food {
    private String foodCatID;
    private String foodDesc;
    private String foodImageURL;
    private String foodName;
    private String foodPrice;

    public Food() {

    }

    public Food(String foodCatID, String foodDesc, String foodImageURL, String foodName, String foodPrice) {
        this.foodCatID = foodCatID;
        this.foodDesc = foodDesc;
        this.foodImageURL = foodImageURL;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
    }

    public String getFoodCatID() {
        return foodCatID;
    }

    public void setFoodCatID(String foodCatID) {
        this.foodCatID = foodCatID;
    }

    public String getFoodDesc() {
        return foodDesc;
    }

    public void setFoodDesc(String foodDesc) {
        this.foodDesc = foodDesc;
    }

    public String getFoodImageURL() {
        return foodImageURL;
    }

    public void setFoodImageURL(String foodImageURL) {
        this.foodImageURL = foodImageURL;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        this.foodPrice = foodPrice;
    }
}
