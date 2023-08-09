package com.example.b07_project_team1;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.example.b07_project_team1.data_classes.Order;
import com.example.b07_project_team1.data_classes.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VendorOrderOuterAdapter extends RecyclerView.Adapter<VendorOrderViewHolder> {
    Product p;
    double totalValue = 0;
    int quantity = 0;
    private Context context;
    private List<Order> orders;
    private List<String> orderIDs;

    public VendorOrderOuterAdapter(Context context, List<Order> orders, List<String> orderIDs) {
        this.context = context;
        this.orders = orders;
        this.orderIDs = orderIDs;
        Log.d("cust", "const vnd: " + Integer.toString(this.getItemCount()));
    }

    @NonNull
    @Override
    public VendorOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("cust", "oncreate vnd: " + Integer.toString(this.getItemCount()));
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_vendor_order, parent, false);
        return new VendorOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorOrderViewHolder holder, int position) {
        List<Product> products = new ArrayList<>();
        List<String> productIDs = new ArrayList<>();
        List<Integer> productAmounts = new ArrayList<>();
        holder.orderIdTextView.setText(orderIDs.get(position));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("products");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                quantity = 0;
                totalValue = 0;
                for (Map.Entry<String, Integer> entry : orders.get(position).getItems().entrySet()) {
                    p = snapshot.child(entry.getKey()).getValue(Product.class);
                    if (p != null) {
                        totalValue += p.getPrice() * entry.getValue();
                        products.add(p);
                        productIDs.add(entry.getKey());
                        productAmounts.add(entry.getValue());
                    }
                    quantity += entry.getValue();
                }
                holder.setOrderId(orderIDs.get(position));
                holder.setOrder(orders.get(position));
                holder.orderValue.setText(String.format(Locale.US, "Value: $%.2f", totalValue));
                holder.orderQuantity.setText(String.format(Locale.US, "Items: %d", quantity));
                VendorOrderInnerAdapter vendorOrderInnerAdapter = new VendorOrderInnerAdapter(context, products, productIDs, productAmounts);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
                holder.recyclerView.setLayoutManager(gridLayoutManager);
                holder.recyclerView.setAdapter(vendorOrderInnerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}

class VendorOrderViewHolder extends RecyclerView.ViewHolder {
    TextView orderIdTextView, orderQuantity, orderValue;
    RecyclerView recyclerView;
    MaterialButton markCompleteButton;

    Button expandOrderButton;
    String orderId;
    Order order;

    public VendorOrderViewHolder(@NonNull View itemView) {
        super(itemView);

        orderIdTextView = itemView.findViewById(R.id.vendor_order_id);
        orderQuantity = itemView.findViewById(R.id.vendor_order_quantity);
        orderValue = itemView.findViewById(R.id.vendor_order_value);
        recyclerView = itemView.findViewById(R.id.vendor_order_dropdown_list);
        markCompleteButton = itemView.findViewById(R.id.complete_order_button);
        expandOrderButton = itemView.findViewById(R.id.expand_order_button);

        markCompleteButton.setOnClickListener(this::toggleMarkCompleteButton);
        expandOrderButton.setOnClickListener(this::toggleExpandOrderButton);
    }

    private void toggleMarkCompleteButton(View view) {
        if (order.isCompleted()) {
            showToast("Order already completed!");
            return;
        }
        markOrderAsComplete(orderId, order.getCustomerId());
    }

    private void markOrderAsComplete(String orderId, String customerId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> updates = new HashMap<>();

        updates.put(String.format("orders/%s/completed", orderId), true);
        updates.put(String.format("customers/%s/pending_orders/%s", customerId, orderId), null);
        updates.put(String.format("customers/%s/completed_orders/%s", customerId, orderId), true);

        ref.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showToast("Order completed!");
                order.setCompleted(true);
                setMarkCompleteButtonAsComplete();
            } else {
                Log.e("FIREBASE", "Couldn't mark order as complete");
            }
        });
    }

    private void setMarkCompleteButtonAsComplete() {
        markCompleteButton.setStrokeWidth(0);
        markCompleteButton.setText(R.string.mark_complete_button_fulfilled);
        markCompleteButton.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(itemView.getContext(), R.color.lime_green)));
        markCompleteButton.setTextColor(
                ContextCompat.getColor(itemView.getContext(), R.color.pure_white));
    }

    private void showToast(CharSequence text) {
        Toast.makeText(itemView.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void toggleExpandOrderButton(View view) {
        if (recyclerView.getVisibility() == View.VISIBLE) {
            recyclerView.setVisibility(View.GONE);
            expandOrderButton.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.chevron_down));
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            expandOrderButton.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.chevron_up));
        }
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setOrder(Order order) {
        this.order = order;
        if (order.isCompleted()) {
            setMarkCompleteButtonAsComplete();
        }
    }
}