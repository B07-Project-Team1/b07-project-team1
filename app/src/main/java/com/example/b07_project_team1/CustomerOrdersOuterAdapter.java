package com.example.b07_project_team1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.SortedMap;

public class CustomerOrdersOuterAdapter extends RecyclerView.Adapter<CustomerOrdersViewHolder> {
    Product p;
    double totalValue = 0;
    int quantity = 0;
    SortedMap<Order, String> ordersMap;
    private Context context;
    private List<Map.Entry<Order, String>> orders;

    public CustomerOrdersOuterAdapter(Context context, SortedMap<Order, String> ordersMap) {
        this.context = context;
        this.ordersMap = ordersMap;
    }

    @NonNull
    @Override
    public CustomerOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_customer_orders, parent, false);
        return new CustomerOrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerOrdersViewHolder holder, int position) {
        orders = new ArrayList<>(ordersMap.entrySet());
        List<Product> products = new ArrayList<>();
        List<String> productIDs = new ArrayList<>();
        List<Integer> productAmounts = new ArrayList<>();
        holder.orderIdTextView.setText(orders.get(position).getValue());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("products");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                quantity = 0;
                totalValue = 0;
                for (Map.Entry<String, Integer> entry : orders.get(position).getKey().getItems().entrySet()) {
                    p = snapshot.child(entry.getKey()).getValue(Product.class);
                    if (p != null) {
                        totalValue += p.getPrice() * entry.getValue();
                        products.add(p);
                        productIDs.add(entry.getKey());
                        productAmounts.add(entry.getValue());
                    }
                    quantity += entry.getValue();
                }
                holder.setOrderId(orders.get(position).getValue());
                holder.setOrder(orders.get(position).getKey());
                holder.orderValue.setText(String.format(Locale.US, "Value: $%.2f", totalValue));
                holder.orderQuantity.setText(String.format(Locale.US, "Items: %d", quantity));
                holder.dateText.setText(orders.get(position).getKey().getFormattedTimestamp());
                CustomerOrdersInnerAdapter customerOrdersInnerAdapter = new CustomerOrdersInnerAdapter(context, products, productIDs, productAmounts);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
                holder.recyclerView.setLayoutManager(gridLayoutManager);
                holder.recyclerView.setAdapter(customerOrdersInnerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return ordersMap.size();
    }
}

class CustomerOrdersViewHolder extends RecyclerView.ViewHolder {
    TextView orderIdTextView, orderQuantity, orderValue, completedText, dateText;
    RecyclerView recyclerView;

    Button expandOrderButton;
    String orderId;
    Order order;

    public CustomerOrdersViewHolder(@NonNull View itemView) {
        super(itemView);

        orderIdTextView = itemView.findViewById(R.id.customer_orders_id);
        orderQuantity = itemView.findViewById(R.id.customer_orders_quantity);
        orderValue = itemView.findViewById(R.id.customer_orders_value);
        recyclerView = itemView.findViewById(R.id.customer_orders_dropdown_list);
        completedText = itemView.findViewById(R.id.customer_orders_completed_text);
        expandOrderButton = itemView.findViewById(R.id.customer_orders_expand_order_button);
        dateText = itemView.findViewById(R.id.customer_orders_date);

        expandOrderButton.setOnClickListener(this::toggleExpandOrderButton);
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
            completedText.setText("Completed");
        }
    }
}
