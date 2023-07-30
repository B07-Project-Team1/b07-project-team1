package com.example.b07_project_team1.model;

import java.io.Serializable;
import java.util.HashMap;

public class Order implements Serializable {
    private String customerId;
    private String vendorId;
    private HashMap<String, Integer> items;
    private boolean isCompleted;

    public Order(String customerId, String vendorId, HashMap<String, Integer> items, boolean isCompleted) {
        this.customerId = customerId;
        this.vendorId = vendorId;
        this.items = items;
        this.isCompleted = isCompleted;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public HashMap<String, Integer> getItems() {
        return items;
    }

    public void setItems(HashMap<String, Integer> items) {
        this.items = items;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
