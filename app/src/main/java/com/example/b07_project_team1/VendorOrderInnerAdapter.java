package com.example.b07_project_team1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.b07_project_team1.data_classes.Product;

import java.util.List;
import java.util.Locale;

public class VendorOrderInnerAdapter extends RecyclerView.Adapter<InnerVendorOrderViewHolder> {
    private Context context;
    private List<Product> products;
    private List<String> productIDs;
    private List<Integer> productAmounts;

    public VendorOrderInnerAdapter(Context context, List<Product> products, List<String> productIDs, List<Integer> productAmounts) {
        this.context = context;
        this.products = products;
        this.productIDs = productIDs;
        this.productAmounts = productAmounts;
    }


    @NonNull
    @Override
    public InnerVendorOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_sublist_item, parent, false);
        return new InnerVendorOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerVendorOrderViewHolder holder, int position) {
        Product product = products.get(position);
        Glide.with(context).load(products.get(position).getImageUrl()).into(holder.productImage);
        holder.productName.setText(product.getProductName());
        holder.unitQuantity.setText(String.format(Locale.US, "Unit Quantity: %d", productAmounts.get(position)));
        holder.price.setText(String.format(Locale.US, "Unit Value: $%.2f", product.getPrice()));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

}

class InnerVendorOrderViewHolder extends RecyclerView.ViewHolder {
    TextView productName, unitQuantity, price;
    ImageView productImage;

    public InnerVendorOrderViewHolder(@NonNull View itemView) {
        super(itemView);

        productImage = itemView.findViewById(R.id.dropdown_product_image);
        productName = itemView.findViewById(R.id.dropdown_vendor_order_product_name);
        unitQuantity = itemView.findViewById(R.id.dropdown_vendor_order_unit_quantity);
        price = itemView.findViewById(R.id.dropdown_vendor_order_product_price);
    }
}
