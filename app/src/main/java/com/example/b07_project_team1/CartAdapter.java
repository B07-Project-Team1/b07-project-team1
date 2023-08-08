package com.example.b07_project_team1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.b07_project_team1.data_classes.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Context context;
    private List<Product> products;
    private List<String> productIDs;
    private List<Integer> productAmounts;

    public CartAdapter(Context context, List<Product> products, List<String> productIDs, List<Integer> productAmounts) {
        this.context = context;
        this.products = products;
        this.productIDs = productIDs;
        this.productAmounts = productAmounts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_cart_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(products.get(position).getImageUrl()).into(holder.productImage);
        holder.productName.setText(products.get(position).getProductName());
        holder.productBrand.setText(products.get(position).getBrandName());
        holder.productPrice.setText(String.format(Locale.US, "$%.2f", products.get(position).getPrice()));
        holder.productCount.setText(String.valueOf(productAmounts.get(position)));

        holder.plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                int count = productAmounts.get(pos);
                if (count < 99) {
                    int newCount = count + 1;
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customers");
                    ref.child(FirebaseAuth.getInstance().getUid()).child("cart").child(productIDs.get(pos)).setValue(newCount, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error != null) {
                                // TODO: MAKE TOAST
                            } else {
                                productAmounts.set(pos, newCount);
                                holder.productCount.setText(String.valueOf(newCount));
                            }
                        }
                    });


                }
            }
        });
        holder.minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                int count = productAmounts.get(pos);
                if (count > 1) {
                    int newCount = count - 1;
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customers");
                    ref.child(FirebaseAuth.getInstance().getUid()).child("cart").child(productIDs.get(pos)).setValue(newCount, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error != null) {
                                // TODO: MAKE TOAST
                            } else {
                                productAmounts.set(pos, newCount);
                                holder.productCount.setText(String.valueOf(newCount));
                            }
                        }
                    });
                }
                else {
                    FirebaseDatabase.getInstance().getReference(String.format(Locale.CANADA, "customers/%s/cart/%s", FirebaseAuth.getInstance().getUid(), productIDs.get(pos))).removeValue();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView productImage;
    TextView productName, productBrand, productPrice, productCount;
    Button plusButton, minusButton;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        productImage = itemView.findViewById(R.id.product_image);
        productName = itemView.findViewById(R.id.cart_product_name);
        productBrand = itemView.findViewById(R.id.cart_product_brand);
        productPrice = itemView.findViewById(R.id.cart_product_price);
        productCount = itemView.findViewById(R.id.product_count);
        plusButton = itemView.findViewById(R.id.checkout_plus_button);
        minusButton = itemView.findViewById(R.id.checkout_minus_button);

    }
}
