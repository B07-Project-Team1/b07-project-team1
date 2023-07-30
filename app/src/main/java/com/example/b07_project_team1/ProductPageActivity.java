package com.example.b07_project_team1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.b07_project_team1.model.Product;
import com.example.b07_project_team1.model.Vendor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

public class ProductPageActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView storeLogo;
    ImageView productImage;
    TextView productName;
    TextView productInfo;
    TextView productPrice;
    TextView productQuantity;
    String productImageUrl;
    Product product;
    String productId;
    String vendorId;
    Vendor vendor;
    int count = 1;
    Button backButton;
    Button countButtonM;
    Button countButtonP;
    Button cartButton;
    Button buyButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);

        storeLogo = findViewById(R.id.product_page_store_logo_inner);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;

        productImageUrl = bundle.getString("PRODUCT_IMAGE");
        product = bundle.getSerializable("PRODUCT_INFO", Product.class);
        productId = bundle.getString("PRODUCT_ID");
        vendorId = product.getVendorId();

        productImage = (ImageView) findViewById(R.id.product_page_image);
        Glide.with(ProductPageActivity.this).load(productImageUrl).into(productImage);

        productName = (TextView) findViewById(R.id.product_page_product_name);
        productName.setText(product.getProductName());

        productInfo = (TextView) findViewById(R.id.product_page_product_info);
        productInfo.setText(product.getDescription());

        productPrice = (TextView) findViewById(R.id.product_page_product_price);
        productPrice.setText("$" + Double.toString(product.getPrice()));

        productQuantity = (TextView) findViewById(R.id.product_page_quantity);
        productQuantity.setText("1");

        loadVendorInfo();

        backButton = (Button) findViewById(R.id.product_page_back_button);
        backButton.setOnClickListener(this);
        countButtonM = (Button) findViewById(R.id.product_page_minus_button);
        countButtonM.setOnClickListener(this);
        countButtonP = (Button) findViewById(R.id.product_page_plus_button);
        countButtonP.setOnClickListener(this);
        cartButton = (Button) findViewById(R.id.product_page_cart_button);
        cartButton.setOnClickListener(this);
        buyButton = (Button) findViewById((R.id.product_page_buy_button));
        buyButton.setOnClickListener(this);
        
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.product_page_back_button) {
            this.finish();
        } else if (v.getId() == R.id.product_page_minus_button) {
            if (count - 1 >= 1) {
                count--;
                productQuantity.setText(Integer.toString(count));
            }
        } else if (v.getId() == R.id.product_page_plus_button) {
            if (count + 1 <= 99) {
                count++;
                productQuantity.setText(Integer.toString(count));
            }
        } else if (v.getId() == R.id.product_page_cart_button) {
            //TODO
        } else if (v.getId() == R.id.product_page_buy_button) {

        }
    }

    void loadVendorInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("vendors");
        ref.child(vendorId).get().addOnCompleteListener(task -> {
            vendor = task.getResult().getValue(Vendor.class);
            Glide.with(ProductPageActivity.this).load(vendor.getLogoUrl()).into(storeLogo);
        });
    }
}