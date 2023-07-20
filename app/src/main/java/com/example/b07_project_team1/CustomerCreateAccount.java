package com.example.b07_project_team1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.b07_project_team1.model.Customer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerCreateAccount extends AppCompatActivity {
    TextView errorTextView;
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_create_account);
        errorTextView = (TextView) findViewById(R.id.customer_create_account_activity_invalid_credentials_error);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance("https://b07-projectdb-team1-default-rtdb.firebaseio.com/");
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            // TODO
        }
    }

    public void onClickRegister(View view) {
        String emailText = ((EditText) findViewById(R.id.customer_create_account_activity_email_input_field)).getText().toString();
        String passwordText = ((EditText) findViewById(R.id.customer_create_account_activity_password_input_field)).getText().toString();
        String repeatPasswordText = ((EditText) findViewById(R.id.customer_create_account_activity_repeat_password_input_field)).getText().toString();

        if (emailText.isEmpty() || passwordText.isEmpty()) {
            errorTextView.setText(R.string.create_account_empty_fields_text);
        }

        else if (!passwordText.equals(repeatPasswordText)) {
            errorTextView.setText(R.string.create_account_password_repeat_no_match_text);
        }
        else {
        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Customer newCustomer = new Customer(emailText);

                            DatabaseReference ref = db.getReference();
                            ref.child("customers").child(user.getUid()).setValue(newCustomer);
                        } else {
                            String longErr = "The email address is already in use by another account.";
                            String err = task.getException().getMessage() == longErr ? "Email address already in use." : task.getException().getMessage();
                            errorTextView.setText(err);
                        }
                    }
                });
        }


    }
}

/*
        ~DATABASE STRUCTURE~
=====================================
LEGEND:
- “” means you actually write that
- no “” means it’s a unique input
=====================================


“customers”: {
         email: {
                  “password”: password
                  “cart”: [product id: quantity, ...]
                  “pending_orders”: [order id, ...]
        }
}

“vendors”: {
    email: {
        “password”: password
            “logo_url”: logo_url
            “brand_name”: brand_name
            “products”: [product_id, ...]
            “orders”: [order id, ...]
        }
}

“products” {
    product_id: {
        “brand_name”: brand_name
        “price”: price
        “image_url”: image_url
        “vendor_email”: vendor_email
    }
}

“orders” {
    order_id: {
        “customer_email”: customer_email
        “vendor_email”: vendor_email
        “items”: [product_id: quantity, ...]
    }
}
 */