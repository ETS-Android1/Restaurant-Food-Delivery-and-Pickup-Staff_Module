package com.example.capstoneprojectadmin.Model;

public class Admin {
    private String adminUsername, adminName, adminPassword, adminTelNo, superAdmin;

    public Admin() {
    }

    public Admin(String name, String password, String telNo, String superAdmin) {
        this.adminName = name;
        this.adminPassword = password;
        this.adminTelNo = telNo;
        this.superAdmin = superAdmin;
    }

    public String getName() {
        return adminName;
    }

    public void setName(String name) {this.adminName = name;}

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String password) {
        this.adminPassword = password;
    }

    public String getUsername() {
        return adminUsername;
    }

    public void setUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminTelNo() {
        return adminTelNo;
    }

    public void setAdminTelNo(String adminTelNo) {
        this.adminTelNo = adminTelNo;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(String superAdmin) {
        this.superAdmin = superAdmin;
    }
}
