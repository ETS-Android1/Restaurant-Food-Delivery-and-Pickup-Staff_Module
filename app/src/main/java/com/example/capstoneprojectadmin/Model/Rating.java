package com.example.capstoneprojectadmin.Model;

public class Rating {
    private String orderID;
    private String rateStars;
    private String rateComment;
    private String custID;
    private String foodsInRating;

    public Rating() {
    }

    public Rating(String orderID, String rateStars, String rateComment, String custID, String foodsInRate) {
        this.orderID = orderID;
        this.rateStars = rateStars;
        this.rateComment = rateComment;
        this.custID = custID;
        this.foodsInRating = foodsInRate;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getRateStars() {
        return rateStars;
    }

    public void setRateStars(String rateStars) {
        this.rateStars = rateStars;
    }

    public String getRateComment() {
        return rateComment;
    }

    public void setRateComment(String rateComment) {
        this.rateComment = rateComment;
    }

    public String getCustID() {
        return custID;
    }

    public void setCustID(String custID) {
        this.custID = custID;
    }

    public String getFoodsInRating() {
        return foodsInRating;
    }

    public void setFoodsInRating(String foodsInRating) {
        this.foodsInRating = foodsInRating;
    }
}
