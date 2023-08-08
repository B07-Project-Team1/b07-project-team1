package com.example.b07_project_team1.model;

import androidx.annotation.NonNull;

import com.example.b07_project_team1.presenter.CustomerLoginPresenter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerLoginModel {
    private FirebaseAuth mAuth;
    FirebaseDatabase db;

    public CustomerLoginModel() {
        db = FirebaseDatabase.getInstance("https://b07-projectdb-team1-default-rtdb.firebaseio.com/");
        mAuth = FirebaseAuth.getInstance();
    }

    public String getUID() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user == null ? null : user.getUid();
    }

    public void signIn(CustomerLoginPresenter presenter, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            db.getReference().child("customers").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()){
                                        if (task.getResult().getValue() == null) {
                                            FirebaseAuth.getInstance().signOut();
                                            presenter.setMessage("Email associated with a vendor.");
                                            return;
                                        }
                                        else {
                                            presenter.setupNewActivity();
                                        }
                                    }

                                }
                            });

                        } else {
                            presenter.setMessage(task.getException().getMessage());
                        }
                    }
                });
    }
}
