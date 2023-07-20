package com.example.b07_project_team1.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Customer implements Serializable {
    public String email;
    public List<String> cart;
    public List<String> pending_orders;

    public Customer() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Customer(String email) {
        this.email = email;
        this.cart = new ArrayList<String>();
        cart.add("daniels mom");
        this.pending_orders = new ArrayList<String>();
        pending_orders.add("daniels dad");
    }
}
