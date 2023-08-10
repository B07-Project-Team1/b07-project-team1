package com.example.b07_project_team1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.example.b07_project_team1.data_classes.Product;
import com.example.b07_project_team1.data_classes.Vendor;

public class StoreAdapter extends RecyclerView.Adapter<StoreViewHolder> {

    private Context context;
    private List<String> productIdList;
    private List<Product> dataList;
    Vendor vendor;
    Boolean isVendor;


    public StoreAdapter(Context context, List<String> productIdList, List<Product> dataList, Boolean isVendor) {
        this.context = context;
        this.productIdList = productIdList;
        this.dataList = dataList;
        this.isVendor = isVendor;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_store_product, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getImageUrl()).into(holder.productIcon);

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent productPageIntent = new Intent(context, ProductPageActivity.class);
                int position = holder.getAdapterPosition();
                productPageIntent.putExtra("PRODUCT_IMAGE", dataList.get(position).getImageUrl());
                productPageIntent.putExtra("PRODUCT_INFO", dataList.get(position));
                productPageIntent.putExtra("VENDOR_INFO", vendor);
                productPageIntent.putExtra("IS_VENDOR", isVendor);
                productPageIntent.putExtra("PRODUCT_ID", productIdList.get(position));
                context.startActivity(productPageIntent);
            }
        });

        Product product = dataList.get(position);
        holder.recCard.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                ProductPreview productPreview = new ProductPreview();
                Bundle args = new Bundle();
                args.putSerializable("product", product);
                productPreview.setArguments(args);
                productPreview.show(fragmentManager, "product_preview");
                return true;
            }
        });
    }

    public void setDataList(@NonNull List<String> newProductIdList, @NonNull List<Product> newDataList) {
        assert newProductIdList.size() == newDataList.size();
        this.productIdList = newProductIdList;
        this.dataList = newDataList;
        notifyDataSetChanged();
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}


class StoreViewHolder extends RecyclerView.ViewHolder{
    ImageView productIcon;
    CardView recCard;

    public StoreViewHolder(@NonNull View itemView) {
        super(itemView);
        productIcon = itemView.findViewById(R.id.product_icon);
        recCard = itemView.findViewById(R.id.recCard);
    }
}