package com.example.b07_project_team1;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.b07_project_team1.data_classes.Vendor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MallActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<String> vendorIdList;
    List<Vendor> dataList;
    ImageButton userMenuButton;
    DatabaseReference dbr;
    FirebaseAuth userAuth;
    ValueEventListener eventListener;

    EditText searchBar;
    ImageButton searchButton;
    ImageButton cartButton;
    ImageButton ordersButton;
    boolean isVendor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall_view);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        isVendor = bundle.getBoolean("IS_VENDOR");

        recyclerView = findViewById(R.id.recyclerView);
        searchBar = findViewById(R.id.search_bar_mall);
        searchButton = findViewById(R.id.ribbon_search);
        cartButton = findViewById(R.id.ribbon_cart);
        userMenuButton = findViewById(R.id.ribbon_user);
        ordersButton = findViewById(R.id.ribbon_orders);

        userAuth = FirebaseAuth.getInstance();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MallActivity.this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        ////////// CHANGE MAYBE REQUIRED
        AlertDialog.Builder builder = new AlertDialog.Builder(MallActivity.this);
        builder.setCancelable(false);
//        builder.setView(R.layout.)
        AlertDialog dialog = builder.create();
        dialog.show();

        vendorIdList = new ArrayList<>();
        dataList = new ArrayList<>();
        MallAdapter adapter = new MallAdapter(MallActivity.this, vendorIdList, dataList);
        recyclerView.setAdapter(adapter);

        dbr = FirebaseDatabase.getInstance().getReference("vendors");
        dialog.show(); ////// CHANGE MAYBE REQUIRED

        // Successful order message
        String message = getIntent().getStringExtra("message");
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

        // Search Function
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String userInput = charSequence.toString().trim().toLowerCase();
                List<String> searchIdList = new ArrayList<>();
                List<Vendor> searchList = new ArrayList<>();
                for (int pos = 0; pos < dataList.size(); pos++) {
                    Vendor vendor = dataList.get(pos);
                    String vendorId = vendorIdList.get(pos);
                    if (vendor.getBrandName().toLowerCase().contains(userInput)) {
                        searchList.add(vendor);
                        searchIdList.add(vendorId);
                    }
                }
                adapter.setDataList(searchIdList, searchList);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

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
                    Intent checkoutIntent = new Intent(getApplicationContext(), CheckoutOrder.class);
                    startActivity(checkoutIntent);
                }
            }
        });

        userMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUpMenu(view);
            }
        });

        ordersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ordersIntent;
                if (isVendor) {
                    ordersIntent = new Intent(getApplicationContext(), VendorOrders.class);
                } else {
                    ordersIntent = new Intent(getApplicationContext(), CustomerOrders.class);
                }
                startActivity(ordersIntent);
            }
        });

        eventListener = dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                vendorIdList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Vendor dataClass = itemSnapshot.getValue(Vendor.class);
                    dataList.add(dataClass);
                    vendorIdList.add(itemSnapshot.getKey());
                }
                assert dataList.size() == vendorIdList.size();
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
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
        Toast.makeText(MallActivity.this, "Logout Successful!", Toast.LENGTH_SHORT).show();
    }

    private void showSoftKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
