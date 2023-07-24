package com.example.b07_project_team1.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Customer implements Serializable {
    private String email;
    private List<String> cart;
    private List<String> pending_orders;

    public Customer() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Customer(String email) {
        this.email = email;
        this.cart = new ArrayList<String>();
        this.pending_orders = new ArrayList<String>();
    }

    public String getEmail() {
        return email;
    }

    public List<String> getCart() {
        return cart;
    }

    public List<String> getPendingOrders() {
        return pending_orders;
    }
}
