package com.example.b07_project_team1.model;

import androidx.annotation.NonNull;

import com.example.b07_project_team1.presenter.VendorLoginPresenter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VendorLoginModel {
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;


    public VendorLoginModel() {
        db = FirebaseDatabase.getInstance("https://b07-projectdb-team1-default-rtdb.firebaseio.com/");
        mAuth = FirebaseAuth.getInstance();
    }

    public String getUID() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user == null ? null : user.getUid();
    }

    public void generateUserData(VendorLoginPresenter presenter, String uid) {
        db.getReference(String.format("vendors/%s", uid)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String brandName = snapshot.child("brandName").getValue(String.class);
                String logoUrl = snapshot.child("logoUrl").getValue(String.class);

                presenter.setupNewActivity(brandName, logoUrl);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void signIn(VendorLoginPresenter presenter, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                                            presenter.setMessage("Email associated with a customer.");
                                            return;
                                        }
                                        presenter.onSignIn();
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
