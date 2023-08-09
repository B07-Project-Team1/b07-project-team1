package com.example.b07_project_team1;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.b07_project_team1.data_classes.Order;
import com.example.b07_project_team1.data_classes.OrderDateComparator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.SortedMap;
import java.util.TreeMap;

public class CustomerOrders extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recyclerView;
    SortedMap<Order, String> ordersMap;
    DatabaseReference refP;
    DatabaseReference refC;
    DatabaseReference ref2;
    ValueEventListener eventListenerP;
    ValueEventListener eventListenerC;
    Order order;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_orders);
        backButton = findViewById(R.id.customer_orders_back_button);
        backButton.setOnClickListener(this);


        recyclerView = findViewById(R.id.recycler_customer_orders);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(CustomerOrders.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        ordersMap = new TreeMap<>(new OrderDateComparator());

        CustomerOrdersOuterAdapter adapter = new CustomerOrdersOuterAdapter(CustomerOrders.this, ordersMap);
        recyclerView.setAdapter(adapter);

        refP = FirebaseDatabase.getInstance().getReference(String.format("customers/%s/pending_orders", FirebaseAuth.getInstance().getUid()));
        refC = FirebaseDatabase.getInstance().getReference(String.format("customers/%s/completed_orders", FirebaseAuth.getInstance().getUid()));

        //refresh orders (and their status, ie pending or completed) when either pending or completed orders change
        eventListenerP = refP.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String orderID = itemSnapshot.getKey();
                    ref2 = FirebaseDatabase.getInstance().getReference("orders");
                    ref2.child(orderID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            order = task.getResult().getValue(Order.class);
                            ordersMap.put(order, orderID);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        eventListenerC = refC.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String orderID = itemSnapshot.getKey();
                    ref2 = FirebaseDatabase.getInstance().getReference("orders");
                    ref2.child(orderID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            order = task.getResult().getValue(Order.class);
                            ordersMap.put(order, orderID);
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
        if (view.getId() == R.id.customer_orders_back_button) {
            this.finish();
        }
    }
}
