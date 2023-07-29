package com.example.b07_project_team1;
// STORES PAGE

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.TextWatcher;

import com.bumptech.glide.Glide;

public class StoreActivity extends AppCompatActivity {

    ImageView storeLogo;
    ImageButton searchButton;
    ImageButton mallBack;
    TextView searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_detail);

        storeLogo = findViewById(R.id.store_logo_inner);
        searchBar = findViewById(R.id.search_bar_store);
        searchButton = findViewById(R.id.store_ribbon_search);
        mallBack = findViewById(R.id.store_back_button);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Glide.with(this).load(bundle.getString("Image")).into(storeLogo);
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBar.requestFocus();
                showSoftKeyboard();
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

    private void showSoftKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)  getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}