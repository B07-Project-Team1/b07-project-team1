package com.example.b07_project_team1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
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
    Button createAccountButton;
    TextView errorTextView;
    EditText emailField;
    EditText passwordField;
    EditText repeatPasswordField;

    private FirebaseAuth mAuth;
    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_create_account);
        errorTextView = (TextView) findViewById(R.id.customer_create_account_activity_invalid_credentials_error);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance("https://b07-projectdb-team1-default-rtdb.firebaseio.com/");

        createAccountButton = (Button) findViewById(R.id.create_account_button_customer_create_account_activity);
        createAccountButton.setOnTouchListener(onTouchCreateAccount);

        emailField = (EditText) findViewById(R.id.customer_create_account_activity_email_input_field);
        emailField.setOnFocusChangeListener(onFocusChangeEditText);

        passwordField = (EditText) findViewById(R.id.customer_create_account_activity_password_input_field);
        passwordField.setOnFocusChangeListener(onFocusChangeEditText);

        repeatPasswordField = (EditText) findViewById(R.id.customer_create_account_activity_repeat_password_input_field);
        repeatPasswordField.setOnFocusChangeListener(onFocusChangeEditText);

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

                            // Transition the user to the mall page
                            Intent mallIntent = new Intent(getApplicationContext(), MallActivity.class);
                            startActivity(mallIntent);
                        } else {
                            String message = task.getException().getMessage();
                            String longErr = "The email address is already in use by another account.";
                            String longErr2 = "The given password is invalid. [ Password should be at least 6 characters ]";
                            String err = message.equals(longErr) ? "Email address already in use." : message.equals(longErr2) ? "Password is too short." :  message;
                            errorTextView.setText(err);
                        }
                    }
                });
        }
    }

    private View.OnTouchListener onTouchCreateAccount = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                createAccountButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.medium_gray));
                createAccountButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.entry_button_background_onpress));
            }
            else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                createAccountButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.pure_white));
                createAccountButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.entry_button_background));
            }
            return false;
        }
    };

    private View.OnFocusChangeListener onFocusChangeEditText = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                view.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.login_info_entry_box_background_active));
            }
            else {
                view.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.login_info_entry_box_background));
            }
        }
    };
}
