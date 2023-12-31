package com.example.b07_project_team1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.*;

public class VendorSetup extends AppCompatActivity {

    EditText brandNameField;
    TextView errorTextView;
    Button createAccountButton;
    ImageView uploadImage;
    String imageUrl;
    Uri uri;
    private FirebaseDatabase db;
    private View.OnTouchListener onTouchLogin = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                createAccountButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.medium_gray));
                createAccountButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.entry_button_background_onpress));
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                createAccountButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.pure_white));
                createAccountButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.entry_button_background));
                if (uri == null || brandNameField.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "You have empty fields.", Toast.LENGTH_SHORT).show();
//                    errorTextView.setText(R.string.empty_field_error_string);
                    return false;
                }

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Android Images").child(uri.getLastPathSegment());
                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri urlImage = uriTask.getResult();
                        imageUrl = urlImage.toString();

                        String brandName = brandNameField.getText().toString();

                        DatabaseReference ref = db.getReference();
                        String uid = FirebaseAuth.getInstance().getUid();

                        ref.child("vendors").child(uid).child("brandName").setValue(brandName);
                        ref.child("vendors").child(uid).child("logoUrl").setValue(imageUrl);

                        Intent storeIntent = new Intent(VendorSetup.this, StoreActivity.class);
                        storeIntent.putExtra("IS_VENDOR", true);
                        storeIntent.putExtra("VENDOR_STORE_LOGO", (String) imageUrl);
                        storeIntent.putExtra("VENDOR_ID", (String) FirebaseAuth.getInstance().getUid());
                        storeIntent.putExtra("message", "Account created!");
                        startActivity(storeIntent);
                        finish();
                    }
                });
            }
            return false;
        }
    };
    private View.OnFocusChangeListener onFocusChangeEditText = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                view.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.login_info_entry_box_background_active));
            } else {
                view.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.login_info_entry_box_background));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_setup);

        brandNameField = (EditText) findViewById(R.id.vendor_setup_activity_brand_name_input_field);
        brandNameField.setOnFocusChangeListener(onFocusChangeEditText);

        createAccountButton = (Button) findViewById(R.id.button_vendor_setup_activity);
        createAccountButton.setOnTouchListener(onTouchLogin);

        uploadImage = findViewById(R.id.brand_logo_upload_container);

        db = FirebaseDatabase.getInstance("https://b07-projectdb-team1-default-rtdb.firebaseio.com/");

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uri = data.getData();
//                            Log.i("setup", uri.getLastPathSegment()); // Debug options
                            uploadImage.setImageURI(uri);
                        } else if (result.getResultCode() != Activity.RESULT_CANCELED) {
//                            errorTextView.setText(R.string.file_upload_error_string);
                            Toast.makeText(VendorSetup.this, "Error in uploading file", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

    }

}