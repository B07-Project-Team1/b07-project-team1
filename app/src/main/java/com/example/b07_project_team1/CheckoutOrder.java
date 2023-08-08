package com.example.b07_project_team1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.b07_project_team1.data_classes.Order;
import com.example.b07_project_team1.data_classes.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CheckoutOrder extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recyclerView;
    List<Product> products;
    List<String> productIDs;
    List<Integer> productAmounts;
    DatabaseReference customerRef;
    DatabaseReference productRef;
    Button checkoutButton;
    Button checkoutBackButton;
    TextView subtotalText;
    TextView taxText;
    TextView totalText;
    ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_order);

        checkoutButton = findViewById(R.id.checkout_button);
        checkoutButton.setOnClickListener(this);

        checkoutBackButton = findViewById(R.id.checkout_back_button);
        checkoutBackButton.setOnClickListener(this);

        subtotalText = findViewById(R.id.subtotal_text);
        taxText = findViewById(R.id.tax_text);
        totalText = findViewById(R.id.checkout_total_price);


        recyclerView = findViewById(R.id.recycler_cart);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(CheckoutOrder.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        products = new ArrayList<Product>();
        productIDs = new ArrayList<String>();
        productAmounts = new ArrayList<Integer>();

        CartAdapter adapter = new CartAdapter(CheckoutOrder.this, products, productIDs, productAmounts);
        recyclerView.setAdapter(adapter);

        FirebaseDatabase fb = FirebaseDatabase.getInstance();

        customerRef = fb.getReference(String.format("customers/%s/cart", FirebaseAuth.getInstance().getUid()));

        eventListener = customerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                products.clear();
                productIDs.clear();
                productAmounts.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    // key
                    String key = snap.getKey();
                    productIDs.add(key);

                    // amount
                    Long amtLong = snap.getValue(Long.class);
                    assert amtLong != null;
                    int amt = amtLong.intValue();
                    productAmounts.add(amt);

                    // products associated with key
                    productRef = fb.getReference(String.format("products/%s", key));
                    productRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            products.add(snapshot.getValue(Product.class));
                            adapter.notifyDataSetChanged();
                            setPriceFields();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.checkout_back_button) { this.finish(); }
        else if (view.getId() == R.id.checkout_button) {
            checkout();
        }
    }

    private void setPriceFields() {
        double subtotal = 0;
        for (int i = 0; i < products.size(); i++) {
            subtotal += products.get(i).getPrice() * productAmounts.get(i);
        }
        SpannableString subtotalSpannable = new SpannableString(String.format(Locale.CANADA, "Subtotal (%d items): $%.2f", products.size(), subtotal));
        subtotalSpannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.dark_gray)),0, String.format(Locale.CANADA, "Subtotal (%d items):", products.size()).length(), 0);
        SpannableString taxSpannable = new SpannableString(String.format(Locale.CANADA, "GST/HST: $%.2f", subtotal * 0.13));
        taxSpannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.dark_gray)),0, "GST/HST:".length(), 0);

        subtotalText.setText(subtotalSpannable);
        taxText.setText(taxSpannable);
        totalText.setText(String.format(Locale.CANADA,"%.2f", subtotal * 1.13));

    }

    private void checkout() {
        Order order;
        if (products.size() == 0) {
            Toast.makeText(CheckoutOrder.this, "No products to order", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Order> ordersByBrand = new HashMap<String, Order>();

        for (int i = 0; i < products.size(); i++) {
            String vendorId = products.get(i).getVendorId();
            if (ordersByBrand.containsKey(vendorId)) {
                order = ordersByBrand.get(vendorId);
                order.addItem(productIDs.get(i), productAmounts.get(i));
                ordersByBrand.put(vendorId, order);
            } else {
                HashMap<String, Integer> items = new HashMap<String, Integer>();
                items.put(productIDs.get(i), productAmounts.get(i));
                String formattedDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).format(LocalDateTime.now());
                order = new Order(FirebaseAuth.getInstance().getUid(), vendorId, items, false, formattedDate);
                ordersByBrand.put(vendorId, order);
            }
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        for (Order ord : ordersByBrand.values()) {
            DatabaseReference newOrderRef = ref.child("orders").push();
            String orderId = newOrderRef.getKey();
            assert orderId != null;
            newOrderRef.setValue(ord);
            ref.child("vendors").child(ord.getVendorId()).child("orders").child(orderId).setValue(true);
            ref.child("customers").child(ord.getCustomerId()).child("pending_orders").child(orderId).setValue(true);
        }

        customerRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Intent mallIntent = new Intent(getApplicationContext(), MallActivity.class);
                mallIntent.putExtra("message", "Order Complete!");
                startActivity(mallIntent);
            }
        });
    }
}