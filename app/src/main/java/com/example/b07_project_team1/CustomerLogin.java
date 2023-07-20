package com.example.b07_project_team1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CustomerLogin extends AppCompatActivity {
    TextView errorTextView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);
        errorTextView = (TextView) findViewById(R.id.customer_login_activity_invalid_credentials_error);
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
        String emailText = ((EditText) findViewById(R.id.customer_login_activity_email_input_field)).getText().toString();
        String passwordText = ((EditText) findViewById(R.id.customer_login_activity_password_input_field)).getText().toString();

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

                            } else {
                                String longErr = "The password is invalid or the user does not have a password.";
                                String longErr2 = "There is no user record corresponding to this identifier. The user may have been deleted.";
                                String message = task.getException().getMessage();
                                String err = message == longErr ? "Incorrect password." : message == longErr2 ? "No account associated with email." : message;
                                errorTextView.setText(err);
                            }
                        }
                    });
        }
    }

    public void onClickCreateAccount(View view) {
        Intent customerCreateAccount = new Intent(this, CustomerCreateAccount.class);
        startActivity(customerCreateAccount);
    }
}