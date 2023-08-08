package com.example.b07_project_team1;

import androidx.annotation.NonNull;

import com.example.b07_project_team1.data_classes.Customer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerCreateAccountModel {
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;

    public CustomerCreateAccountModel() {
        db = FirebaseDatabase.getInstance("https://b07-projectdb-team1-default-rtdb.firebaseio.com/");
        mAuth = FirebaseAuth.getInstance();
    }

    public void createAccount(CustomerCreateAccountPresenter presenter, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Customer newCustomer = new Customer(email);

                            DatabaseReference ref = db.getReference();
                            ref.child("customers").child(user.getUid()).setValue(newCustomer);

                            presenter.startNewActivity();
                        } else {
                            presenter.setText(task.getException().getMessage());
                        }
                    }
                });
    }
}
