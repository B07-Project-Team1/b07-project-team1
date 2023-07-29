package com.example.b07_project_team1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.b07_project_team1.model.Product;
import com.example.b07_project_team1.model.Vendor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

public class ProductPageActivity extends AppCompatActivity {

    ImageView storeLogo;
    String productImageUrl;
    Product product;
    String productId;
    String vendorId;
    Vendor vendor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);

        storeLogo = findViewById(R.id.product_page_store_logo_inner);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;

        productImageUrl = bundle.getString("IMAGE_URL");
        product = bundle.getSerializable("PRODUCT_INFO", Product.class);
        productId = bundle.getString("PRODUCT_ID");
        vendorId = product.getVendorId();

        loadVendorInfo();
    }

    void loadVendorInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("vendors");
        ref.child(vendorId).get().addOnCompleteListener(task -> {
            vendor = task.getResult().getValue(Vendor.class);
            Glide.with(ProductPageActivity.this).load(vendor.getLogoUrl()).into(storeLogo);
        });
    }
}