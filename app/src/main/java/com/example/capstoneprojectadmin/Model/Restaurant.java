package com.example.capstoneprojectadmin.Model;

public class Restaurant {
    private String restName;
    private String restSlogan;
    private String restOpening;
    private String restClosing;
    private String restLastOrderTime;

    public Restaurant() {

    }

    public Restaurant(String restName, String restSlogan, String restOpening, String restClosing, String restLastOrderTime) {
        this.restName = restName;
        this.restSlogan = restSlogan;
        this.restOpening = restOpening;
        this.restClosing = restClosing;
        this.restLastOrderTime = restLastOrderTime;
    }

    public String getRestName() {
        return restName;
    }

    public void setRestName(String restName) {
        this.restName = restName;
    }

    public String getRestSlogan() {
        return restSlogan;
    }

    public void setRestSlogan(String restSlogan) {
        this.restSlogan = restSlogan;
    }

    public String getRestOpening() {
        return restOpening;
    }

    public void setRestOpening(String restOpening) {
        this.restOpening = restOpening;
    }

    public String getRestClosing() {
        return restClosing;
    }

    public void setRestClosing(String restClosing) {
        this.restClosing = restClosing;
    }

    public String getRestLastOrderTime() {
        return restLastOrderTime;
    }

    public void setRestLastOrderTime(String restLastOrderTime) {
        this.restLastOrderTime = restLastOrderTime;
    }
}
