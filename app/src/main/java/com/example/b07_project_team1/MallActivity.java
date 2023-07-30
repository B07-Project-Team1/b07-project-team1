package com.example.b07_project_team1;


import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.b07_project_team1.model.Vendor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class MallActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<String> vendorIdList;
    List<Vendor> dataList;
    DatabaseReference dbr;
    ValueEventListener eventListener;

    EditText searchBar;
    ImageButton searchButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall_view);

        recyclerView = findViewById(R.id.recyclerView);
        searchBar = findViewById(R.id.search_bar_mall);
        searchButton = findViewById(R.id.ribbon_search);

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

        // Search Function
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

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
            public void afterTextChanged(Editable editable) {}
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBar.requestFocus();
                showSoftKeyboard();
            }
        });

        eventListener = dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                vendorIdList.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
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

    private void showSoftKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}