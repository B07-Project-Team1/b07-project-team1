package com.example.b07_project_team1;
// STORES PAGE

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.text.TextWatcher;

import com.bumptech.glide.Glide;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.Toast;

import com.example.b07_project_team1.data_classes.Product;
import com.example.b07_project_team1.data_classes.Vendor;
import com.google.firebase.auth.FirebaseAuth;
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
    ImageButton userMenuButton;
    TextView storeSearchBar;
    RecyclerView recyclerView;
    List<String> productIdList;
    List<Product> productDataList;
    String vendorId;
    Vendor vendor;
    boolean isVendor;

    FirebaseAuth userAuth;

    StoreAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        storeLogo = findViewById(R.id.store_logo_inner);
        storeSearchBar = findViewById(R.id.search_bar_store);
        searchButton = findViewById(R.id.store_ribbon_search);
        ordersButton = findViewById(R.id.store_ribbon_orders);
        cartButton = findViewById(R.id.store_ribbon_cart);
        mallBack = findViewById(R.id.store_back_button);
        productIdList = new ArrayList<>();
        productDataList = new ArrayList<>();
        storeLogo = findViewById(R.id.store_logo_inner);
        recyclerView = findViewById(R.id.product_recycler_view);
        userMenuButton = findViewById(R.id.store_ribbon_user);

        userAuth = FirebaseAuth.getInstance();

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        Glide.with(this).load(bundle.getString("VENDOR_STORE_LOGO")).into(storeLogo);
        vendorId = bundle.getString("VENDOR_ID");
        isVendor = bundle.getBoolean("IS_VENDOR");
        vendor = bundle.getSerializable("VENDOR_INFO", Vendor.class);
        assert vendorId != null;
        createProductsGrid();
        getProductList(vendorId);

        String message = getIntent().getStringExtra("message");
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

        userMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUpMenu(view);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeSearchBar.requestFocus();
                showSoftKeyboard();
            }
        });

        ordersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVendor) {
                    Intent ordersIntent = new Intent(getApplicationContext(), VendorOrders.class);
                    startActivity(ordersIntent);
                } else {
                    Intent ordersIntent = new Intent(getApplicationContext(), CustomerOrders.class);
                    startActivity(ordersIntent);
                }
            }
        });

        storeSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String userInput = charSequence.toString().trim().toLowerCase();
                List<Product> userSearchProduct = new ArrayList<>();
                List<String> userSearchProductID = new ArrayList<>();

                for (int index = 0; index < productDataList.size(); index++) {
                    Product product = productDataList.get(index);
                    String productID = productIdList.get(index);
                    if (product.getProductName().toLowerCase().contains(userInput)) {
                        userSearchProduct.add(product);
                        userSearchProductID.add(productID);
                    }
                }
                adapter.setDataList(userSearchProductID, userSearchProduct);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVendor) {
                    Intent addProductIntent = new Intent(getApplicationContext(), VendorAddProduct.class);
                    startActivity(addProductIntent);
                } else {
                    Intent checkoutIntent = new Intent(getApplicationContext(), CheckoutOrder.class);
                    startActivity(checkoutIntent);
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

    private void showPopUpMenu(View view) {
        PopupMenu userPopupMenu = new PopupMenu(this, view);
        MenuInflater inflater = userPopupMenu.getMenuInflater();
        inflater.inflate(R.menu.ribbon_user_icon_popup, userPopupMenu.getMenu());

        userPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.user_logout_option) {
                    userLogout();
                    return true;
                } else {
                    return false;
                }
            }
        });
        userPopupMenu.show();
    }


    private void userLogout() {
        userAuth.signOut();
        Intent userSelection = new Intent(getApplicationContext(), UserTypeSelection.class);
        startActivity(userSelection);
        finish();
        Toast.makeText(StoreActivity.this, "Logout Successful!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProductList(vendorId);
        adapter.notifyDataSetChanged();
    }

    private void showSoftKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(storeSearchBar, InputMethodManager.SHOW_IMPLICIT);
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