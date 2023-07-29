package com.example.b07_project_team1.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Vendor implements Serializable {
    private String email;
    private HashMap<String, Boolean> products;
    private HashMap<String, Boolean> orders;

    private String brandName;

    private String LogoUrl;

    public Vendor() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Vendor(String email) {
        this.email = email;
        this.products = new HashMap<String, Boolean>();
        this.orders = new HashMap<String, Boolean>();
    }

    public void setLogoUrl(String LogoUrl) {
        this.LogoUrl = LogoUrl;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getEmail() {
        return email;
    }

    public HashMap<String, Boolean> getProducts() {
        return products;
    }

    public HashMap<String, Boolean> getOrders() {
        return orders;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getLogoUrl() {
        return LogoUrl;
    }
}
