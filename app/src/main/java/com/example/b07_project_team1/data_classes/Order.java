package com.example.b07_project_team1.data_classes;

import java.io.Serializable;
import java.util.HashMap;

public class Order implements Serializable {
    private String customerId;
    private String vendorId;
    private HashMap<String, Integer> items;
    private boolean isCompleted;
    private String formattedTimestamp;

    public Order(String customerId, String vendorId, HashMap<String, Integer> items, boolean isCompleted, String formattedTimestamp) {
        this.customerId = customerId;
        this.vendorId = vendorId;
        this.items = items;
        this.isCompleted = isCompleted;
        this.formattedTimestamp = formattedTimestamp;
    }

    public Order() {

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

    public void addItem(String key, int value) {
        items.put(key, items.containsKey(key) && items.get(key) != null ? items.get(key) + value : value);
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getFormattedTimestamp() {
        return formattedTimestamp;
    }
}
