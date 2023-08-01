package com.example.b07_project_team1;
// STORES PAGE

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.TextWatcher;

import com.bumptech.glide.Glide;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;

import com.example.b07_project_team1.model.Product;
import com.example.b07_project_team1.model.Vendor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends AppCompatActivity {

    ImageView storeLogo;
    ImageButton searchButton;
    ImageButton ordersButton;
    ImageButton cartButton;
    ImageButton mallBack;
    TextView searchBar;
    RecyclerView recyclerView;
    List<String> productIdList;
    List<Product> productDataList;
    String vendorId;
    Vendor vendor;
    boolean isVendor;

    StoreAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        storeLogo = findViewById(R.id.store_logo_inner);
        searchBar = findViewById(R.id.search_bar_store);
        searchButton = findViewById(R.id.store_ribbon_search);
        ordersButton = findViewById(R.id.store_ribbon_orders);
        cartButton = findViewById(R.id.store_ribbon_cart);
        mallBack = findViewById(R.id.store_back_button);
        productIdList = new ArrayList<>();
        productDataList = new ArrayList<>();
        storeLogo = findViewById(R.id.store_logo_inner);
        recyclerView = findViewById(R.id.product_recycler_view);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        Glide.with(this).load(bundle.getString("VENDOR_STORE_LOGO")).into(storeLogo);
        vendorId = bundle.getString("VENDOR_ID");
        isVendor = bundle.getBoolean("IS_VENDOR");
        vendor = bundle.getSerializable("VENDOR_INFO", Vendor.class);
        assert vendorId != null;
        createProductsGrid();
        getProductList(vendorId);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBar.requestFocus();
                showSoftKeyboard();
            }
        });

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVendor) {
                    Intent addProductIntent = new Intent(getApplicationContext(), VendorAddProduct.class);
                    startActivity(addProductIntent);
                } else {
                    // TODO: customer task
                }
            }
        });

        mallBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mallViewIntent = new Intent(getApplicationContext(), MallActivity.class);
                startActivity(mallViewIntent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getProductList(vendorId);
        adapter.notifyDataSetChanged();
    }

    private void showSoftKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)  getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);
        }
    }
    void getProductList(String vendorId) {
        DatabaseReference vendorProductIdList = FirebaseDatabase.getInstance()
                .getReference("vendors").child(vendorId).child("products");
        vendorProductIdList.get().addOnCompleteListener(getIdTask -> {
            if (!getIdTask.isSuccessful()) {
                Log.e("FIREBASE", "Error getting data", getIdTask.getException());
                return;
            }

            DataSnapshot productIdsSnapshot = getIdTask.getResult();
            ArrayList<String> productIds = new ArrayList<>();
            for (DataSnapshot item : productIdsSnapshot.getChildren()) {
                productIds.add(item.getKey());
            }

            DatabaseReference productsRef = FirebaseDatabase.getInstance()
                    .getReference("products");
            productsRef.get().addOnCompleteListener(getProductsTask -> {
                if (!getProductsTask.isSuccessful()) {
                    Log.e("FIREBASE", "Error getting data", getProductsTask.getException());
                    return;
                }

                DataSnapshot productsSnapshot = getProductsTask.getResult();
                productIdList.clear();
                productDataList.clear();
                for (String productId : productIds) {
                    Product product = productsSnapshot.child(productId).getValue(Product.class);
                    productIdList.add(productId);
                    productDataList.add(product);
                }

                adapter.setDataList(productIdList, productDataList);
                adapter.notifyDataSetChanged();
            });
        });
    }

    void createProductsGrid() {
        adapter = new StoreAdapter(StoreActivity.this, productIdList, productDataList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                this, 2, GridLayoutManager.VERTICAL, false);
        adapter.setVendor(vendor);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
    }
}