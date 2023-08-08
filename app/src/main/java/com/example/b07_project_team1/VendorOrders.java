package com.example.b07_project_team1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.b07_project_team1.model.Order;

import com.example.b07_project_team1.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class VendorOrders extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recyclerView;
    List<Order> orders;
    List<String> orderIDs;
    DatabaseReference ref;
    DatabaseReference ref2;
    ValueEventListener eventListener;
    Order order;
    Button markCompleteButton;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_orders);
        backButton = findViewById(R.id.order_back_button);
        backButton.setOnClickListener(this);

        recyclerView = findViewById(R.id.recycler_vendor_orders);

        GridLayoutManager  gridLayoutManager = new GridLayoutManager(VendorOrders.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        orders = new ArrayList<>();
        orderIDs = new ArrayList<>();

        VendorOrderOuterAdapter adapter = new VendorOrderOuterAdapter(VendorOrders.this, orders, orderIDs);
        recyclerView.setAdapter(adapter);

        ref = FirebaseDatabase.getInstance().getReference(String.format("vendors/%s/orders", FirebaseAuth.getInstance().getUid()));

        eventListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orders.clear();
                orderIDs.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String orderID = itemSnapshot.getKey();
                    ref2 = FirebaseDatabase.getInstance().getReference("orders");
                    ref2.child(orderID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            order = task.getResult().getValue(Order.class);
                            orders.add(order);
                            orderIDs.add(orderID);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.order_back_button) { this.finish(); }
    }
}