package com.example.b07_project_team1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.b07_project_team1.model.Order;
import com.example.b07_project_team1.model.Product;
import com.example.b07_project_team1.model.Vendor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VendorOrderOuterAdapter extends RecyclerView.Adapter<VendorOrderViewHolder> {
    private Context context;
    private List<Order> orders;
    private List<String> orderIDs;
    Product p;
    double totalValue = 0;
    int quantity = 0;

    public VendorOrderOuterAdapter(Context context, List<Order> orders, List<String> orderIDs) {
        this.context = context;
        this.orders = orders;
        this.orderIDs = orderIDs;
    }

    @NonNull
    @Override
    public VendorOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_vendor_order, parent, false);
        return new VendorOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorOrderViewHolder holder, int position) {
        List<Product> products = new ArrayList<Product>();
        List<String> productIDs = new ArrayList<String>();
        List<Integer> productAmounts = new ArrayList<Integer>();
        holder.orderID.setText(orderIDs.get(position));
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
    TextView orderID, orderQuantity, orderValue;
    RecyclerView recyclerView;
    Button expandOrderButton;

    public VendorOrderViewHolder(@NonNull View itemView) {
        super(itemView);

        orderID = itemView.findViewById(R.id.vendor_order_id);
        orderQuantity = itemView.findViewById(R.id.vendor_order_quantity);
        orderValue = itemView.findViewById(R.id.vendor_order_value);
        recyclerView = itemView.findViewById(R.id.vendor_order_dropdown_list);
        expandOrderButton = itemView.findViewById(R.id.expand_order_button);
        expandOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    recyclerView.setVisibility(View.GONE);
                    expandOrderButton.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.chevron_up));
                }
                else {
                    recyclerView.setVisibility(View.VISIBLE);
                    expandOrderButton.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.chevron_down));
                }
            }
        });
    }
}