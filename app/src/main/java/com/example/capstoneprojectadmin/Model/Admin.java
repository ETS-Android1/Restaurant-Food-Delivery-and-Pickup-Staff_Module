package com.example.capstoneprojectadmin.Model;

public class Admin {
    private String adminUsername, adminName, adminPassword, adminTelNo;

    public Admin() {
    }

    public Admin(String name, String password, String telNo) {
        this.adminName = name;
        this.adminPassword = password;
        this.adminTelNo = telNo;
    }

    public String getName() {
        return adminName;
    }

    public void setName(String name) {this.adminName = name;}

    public String getPassword() {
        return adminPassword;
    }

    public void setPassword(String password) {
        this.adminPassword = password;
    }

    public String getUsername() {
        return adminUsername;
    }

    public void setUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }
}
