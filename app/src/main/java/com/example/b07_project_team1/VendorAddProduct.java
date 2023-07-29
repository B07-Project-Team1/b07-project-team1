package com.example.b07_project_team1;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.b07_project_team1.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class VendorAddProduct extends AppCompatActivity {

    DatabaseReference ref;
    Button addButton;
    EditText productNameField;
    EditText priceField;
    EditText descriptionField;
    ImageView uploadImage;
    Uri uri;
    TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_add_product);

        ref = FirebaseDatabase.getInstance().getReference();

        addButton = (Button) findViewById(R.id.button_vendor_add_product_activity);
        addButton.setOnClickListener(onClickAddButton);

        productNameField = (EditText) findViewById(R.id.vendor_add_product_product_name_input);
        priceField = (EditText) findViewById(R.id.vendor_add_product_price_input);
        descriptionField = (EditText) findViewById(R.id.vendor_add_product_description_input);

        uploadImage = (ImageView) findViewById(R.id.product_image_upload_container);
        uploadImage.setOnClickListener(onClickUploadImage);

        errorTextView = (TextView) findViewById(R.id.vendor_add_product_activity_invalid_error);
    }

    private final View.OnClickListener onClickUploadImage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent photoPicker = new Intent(Intent.ACTION_PICK);
            photoPicker.setType("image/*");
            activityResultLauncher.launch(photoPicker);
        }
    };

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        uri = data.getData();
                        uploadImage.setImageURI(uri);
                    } else {
                        errorTextView.setText(R.string.file_upload_error_string);
                    }
                }
            }
    );

    private final View.OnClickListener onClickAddButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String product = productNameField.getText().toString();
            double price = Double.parseDouble(priceField.getText().toString());
            String description = descriptionField.getText().toString();

            boolean fieldsCompleted = inputFieldsCompleted(product, price, description, uri);
            if (!fieldsCompleted) return;

            prepareWriteProductToDatabase(product, price, description, uri);
        }
    };

    private boolean inputFieldsCompleted(String productName, double price, String description, Uri imageUri) {
        if (productName.isEmpty() || description.isEmpty() || imageUri == null) {
            errorTextView.setText(R.string.empty_field_error_string);
            return false;
        }
        if (price <= 0) {
            errorTextView.setText(R.string.price_error_string);
            return false;
        }
        return true;
    }

    private void prepareWriteProductToDatabase(String product, double price, String description, Uri imageUri) {
        // We must upload the image first to get the URL
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("Android Images").child(
                        Objects.requireNonNull(imageUri.getLastPathSegment()));

        storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isComplete());
            String imageUrl = uriTask.getResult().toString();
            writeProductToDatabase(product, price, description, imageUrl);
        });
    }

    private void writeProductToDatabase(String productName, double price, String description, String imageUrl) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String uid = user.getUid();

        ref.child("vendors").child(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String brandName = String.valueOf(task.getResult().child("brandName").getValue());
                Product newProductInfo = new Product(productName, price, description,
                        imageUrl, brandName, uid);

                DatabaseReference newProductReference = ref.child("products").push();
                String productId = newProductReference.getKey();

                newProductReference.setValue(newProductInfo);
                addProductToVendor(productId, uid);
            }
        });
    }

    private void addProductToVendor(String newProductId, String vendorUid) {
        ref.child("vendors").child(vendorUid).child("products")
                .child(newProductId).setValue(true);
    }
}