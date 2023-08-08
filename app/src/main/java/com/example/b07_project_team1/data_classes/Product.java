package com.example.b07_project_team1.data_classes;

import java.io.Serializable;

public class Product implements Serializable {
    private String productName;
    private double price;
    private String description;
    private String imageUrl;
    private String brandName;
    private String vendorId;

    public Product() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Product(String productName, double price, String description, String imageUrl,
                   String brandName, String vendorId) {
        this.productName = productName;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.brandName = brandName;
        this.vendorId = vendorId;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getVendorId() {
        return vendorId;
    }

}
