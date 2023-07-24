package com.example.b07_project_team1.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Vendor implements Serializable {
    private String email;
    private List<String> products;
    private List<String> orders;

    private String brandName;

    private String logoUrl;

    public Vendor() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Vendor(String email) {
        this.email = email;
        this.products = new ArrayList<String>();
        this.orders = new ArrayList<String>();
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getProducts() {
        return products;
    }

    public List<String> getOrders() {
        return orders;
    }
}
