package com.example.b07_project_team1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class VendorProductListing extends AppCompatActivity {

    ImageView brandLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_product_listing);

        brandLogo = findViewById(R.id.brandLogo);
    }
}