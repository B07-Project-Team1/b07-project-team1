package com.example.b07_project_team1;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.b07_project_team1.model.Product;

public class ProductPreview extends DialogFragment {

    ImageView productImage;
    TextView productTitle;
    TextView productPrice;
    ImageButton previewClose;


    public ProductPreview() {
        // Pass
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_preview, container, false);
        Bundle args = getArguments();

        if (args != null) {
            Product product = (Product) args.getSerializable("product");
            if (product != null) {
                productImage = view.findViewById(R.id.preview_product_image_holder);
                productTitle = view.findViewById(R.id.preview_product_title_holder);
                productPrice = view.findViewById(R.id.preview_product_price_holder);

                Glide.with(this).load(product.getImageUrl()).into(productImage);
                productTitle.setText(product.getProductName());
                productPrice.setText("$" + String.format("%.2f", (product.getPrice())));
            }
        }


        previewClose = view.findViewById(R.id.preview_close);
        previewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }


}