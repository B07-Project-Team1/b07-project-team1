package com.example.b07_project_team1;


import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

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

import java.util.ArrayList;
import java.util.List;

public class MallActivity extends AppCompatActivity {

    RecyclerView recyclerView;
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

        dataList = new ArrayList<>();

        MallAdapter adapter = new MallAdapter(MallActivity.this, dataList);
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
                List<Vendor> searchList = new ArrayList<>();
                for (Vendor vendor: dataList) {
                    if (vendor.getBrandName().toLowerCase().contains(userInput)) {
                        searchList.add(vendor);
                    }
                }
                adapter.setDataList(searchList);
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
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    Vendor dataClass = itemSnapshot.getValue(Vendor.class);
                    dataList.add(dataClass);
                }
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
