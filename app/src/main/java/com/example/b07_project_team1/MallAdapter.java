package com.example.b07_project_team1;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.example.b07_project_team1.model.Vendor;

public class MallAdapter extends RecyclerView.Adapter<StoreHolder> {

    private Context context;
    private List<String> vendorIdList;
    private List<Vendor> dataList;


    public MallAdapter(Context context, List<String> vendorIdList, List<Vendor> dataList) {
        this.context = context;
        this.vendorIdList = vendorIdList;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_mall_stores, parent, false);
        return new StoreHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getLogoUrl()).into(holder.store_logo_mall);

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, StoreActivity.class);
                int position = holder.getAdapterPosition();
                intent.putExtra("IS_VENDOR", false);
                intent.putExtra("VENDOR_STORE_LOGO", dataList.get(position).getLogoUrl());
                intent.putExtra("VENDOR_ID", vendorIdList.get(position));
                intent.putExtra("VENDOR_INFO", dataList.get(position));
                context.startActivity(intent);
            }
        });
    }

    // Changes the datalist provided for the adapter to populate the recycler view
    public void setDataList(List<String> newVendorIdList, List<Vendor> newDataList) {
        assert newVendorIdList.size() == newDataList.size();
        this.vendorIdList = newVendorIdList;
        this.dataList = newDataList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}


class StoreHolder extends RecyclerView.ViewHolder {
    ImageView store_logo_mall;
    CardView recCard;

    public StoreHolder(@NonNull View itemView) {
        super(itemView);
        store_logo_mall = itemView.findViewById(R.id.store_logo_mall);
        recCard = itemView.findViewById(R.id.recCard);
    }
}