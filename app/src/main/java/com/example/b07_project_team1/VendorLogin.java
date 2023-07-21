package com.example.b07_project_team1;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class VendorLogin extends AppCompatActivity {
    TextView errorTextView;
    EditText emailField;
    EditText passwordField;

    Button loginButton;
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_login);
        errorTextView = (TextView) findViewById(R.id.vendor_login_activity_invalid_credentials_error);

        loginButton = (Button) findViewById(R.id.login_button_vendor_login_activity);
        loginButton.setOnTouchListener(onTouchLogin);

        emailField = (EditText) findViewById(R.id.vendor_login_activity_email_input_field);
        emailField.setOnFocusChangeListener(onFocusChangeEditText);

        passwordField = (EditText) findViewById(R.id.vendor_login_activity_password_input_field);
        passwordField.setOnFocusChangeListener(onFocusChangeEditText);

        db = FirebaseDatabase.getInstance("https://b07-projectdb-team1-default-rtdb.firebaseio.com/");

        mAuth = FirebaseAuth.getInstance();
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


    public void onClickLogin(View view) {
        String emailText = ((EditText) findViewById(R.id.vendor_login_activity_email_input_field)).getText().toString();
        String passwordText = ((EditText) findViewById(R.id.vendor_login_activity_password_input_field)).getText().toString();

        if (emailText.isEmpty() || passwordText.isEmpty()) {
            errorTextView.setText("You have empty fields.");
        }
        else {
            mAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                db.getReference().child("vendors").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (task.isSuccessful()){
                                            if (task.getResult().getValue() == null) {
                                                FirebaseAuth.getInstance().signOut();
                                                errorTextView.setText("Email associated with a customer.");
                                            }
                                            DataSnapshot brandName = task.getResult().child("brandName");
                                            DataSnapshot logoUrl = task.getResult().child("logoUrl");
                                            if (brandName.getValue() == null || logoUrl.getValue() == null) {
                                                Intent vendorSetupIntent = new Intent(getApplicationContext(), VendorSetup.class);
                                                startActivity(vendorSetupIntent);
                                            }
                                        }
                                    }
                                });

                            } else {
                                String longErr = "The password is invalid or the user does not have a password.";
                                String longErr2 = "There is no user record corresponding to this identifier. The user may have been deleted.";
                                String longErr3 = "A network error (such as timeout, interrupted connection or unreachable host) has occurred.";
                                String message = task.getException().getMessage();
                                String err = message.equals(longErr) ? "Incorrect password." : message.equals(longErr2) ? "No account associated with email." : message.equals(longErr3) ? "Network error." : message;
                                errorTextView.setText(err);
                            }
                        }
                    });
        }
    }

    public void onClickCreateAccount(View view) {
        Intent vendorCreateAccount = new Intent(this, VendorCreateAccount.class);
        startActivity(vendorCreateAccount);
    }

    private View.OnTouchListener onTouchLogin = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                loginButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.medium_gray));
                loginButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.entry_button_background_onpress));
            }
            else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                loginButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.pure_white));
                loginButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.entry_button_background));
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