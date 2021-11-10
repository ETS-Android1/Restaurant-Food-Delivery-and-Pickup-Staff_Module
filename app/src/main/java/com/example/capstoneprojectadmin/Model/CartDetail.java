package com.example.capstoneprojectadmin.Model;

public class CartDetail {
    private String foodID;
    private String foodName;
    private String quantity;
    private String foodPrice;


    public CartDetail() {
    }

    public CartDetail(String foodID, String foodName, String quantity, String foodPrice) {
        this.foodID = foodID;
        this.foodName = foodName;
        this.quantity = quantity;
        this.foodPrice = foodPrice;

    }

    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        this.foodPrice = foodPrice;
    }

}
