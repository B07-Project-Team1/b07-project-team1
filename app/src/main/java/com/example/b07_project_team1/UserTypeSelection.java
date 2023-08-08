package com.example.b07_project_team1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.b07_project_team1.view.CustomerLoginView;
import com.example.b07_project_team1.view.VendorLoginView;

public class UserTypeSelection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type_selection);
    }

    public void onClickCustomer(View view) {
        Intent customerLoginIntent = new Intent(this, CustomerLoginView.class);
        startActivity(customerLoginIntent);
    }

    public void onClickVendor(View view) {
        Intent vendorLoginIntent = new Intent(this, VendorLoginView.class);
        startActivity(vendorLoginIntent);
    }
}