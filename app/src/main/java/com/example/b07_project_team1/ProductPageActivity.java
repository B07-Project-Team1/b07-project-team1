package com.example.b07_project_team1;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.b07_project_team1.data_classes.Order;
import com.example.b07_project_team1.data_classes.Product;
import com.example.b07_project_team1.data_classes.Vendor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

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
    DatabaseReference ref;

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

        productImage = findViewById(R.id.product_page_image);
        Glide.with(ProductPageActivity.this).load(productImageUrl).into(productImage);

        productName = findViewById(R.id.product_page_product_name);
        productName.setText(product.getProductName());

        productInfo = findViewById(R.id.product_page_product_info);
        productInfo.setText(product.getDescription());

        productPrice = findViewById(R.id.product_page_product_price);
        productPrice.setText("$" + String.format("%.2f", (product.getPrice())));

        productQuantity = findViewById(R.id.product_page_quantity);
        productQuantity.setText("1");

        ref = FirebaseDatabase.getInstance().getReference();
        loadVendorInfo();

        backButton = findViewById(R.id.product_page_back_button);
        backButton.setOnClickListener(this);
        countButtonM = findViewById(R.id.product_page_minus_button);
        countButtonM.setOnClickListener(this);
        countButtonP = findViewById(R.id.product_page_plus_button);
        countButtonP.setOnClickListener(this);
        cartButton = findViewById(R.id.product_page_cart_button);
        cartButton.setOnClickListener(this);
        buyButton = findViewById((R.id.product_page_buy_button));
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
            addToCart(count);
        } else if (v.getId() == R.id.product_page_buy_button) {
            addOrder(count);
        }
    }

    void addToCart(int quantity) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref.child("customers").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer currentQuantity = snapshot.child("cart").child(productId).getValue(Integer.class);
                ref.child("customers").child(uid).child("cart").child(productId).setValue((currentQuantity == null) ? count : currentQuantity + count);
                showToast("Added to cart!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("product page", "Couldn't load data");
            }
        });



    }


    void addOrder(int quantity) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String uid = user.getUid();
        ValueEventListener eventListener = new ValueEventListener() {//place order iff user is customer
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {//check user id exists in customer records
                    DatabaseReference newOrderRef = ref.child("orders").push();
                    String orderId = newOrderRef.getKey();
                    assert orderId != null;

                    //create order and write to db
                    HashMap<String, Integer> items = new HashMap<>();
                    items.put(productId, quantity);

                    String formattedDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).format(LocalDateTime.now());

                    Order order = new Order(uid, vendorId, items, false, formattedDate);

                    newOrderRef.setValue(order);

                    //update vendor+customer accordingly
                    ref.child("vendors").child(vendorId).child("orders").child(orderId).setValue(true);
                    ref.child("customers").child(uid).child("pending_orders").child(orderId).setValue(true);

                    showToast("Order success!");
                } else {
                    showToast("Must be customer to purchase!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        ref.child("customers").child(uid).addListenerForSingleValueEvent(eventListener);
    }

    void showToast(CharSequence text) {
        Toast toast = Toast.makeText(ProductPageActivity.this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    void loadVendorInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("vendors");
        ref.child(vendorId).get().addOnCompleteListener(task -> {
            vendor = task.getResult().getValue(Vendor.class);
            Glide.with(ProductPageActivity.this).load(vendor.getLogoUrl()).into(storeLogo);
        });
    }
}