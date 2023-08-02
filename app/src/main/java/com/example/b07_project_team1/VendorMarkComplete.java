package com.example.b07_project_team1;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VendorMarkComplete {
    public void onClickMarkComplete() {

    }

    public void markOrderAsComplete(String orderId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("orders").child(orderId).setValue(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showSuccessToast();
            } else {
                Log.e("FIREBASE", "Couldn't mark order as complete");
            }
        });
    }

    public void showSuccessToast() {
        // TODO: Change the context to the correct class
        Context context = null;
        Toast toast = Toast.makeText(context, "Order completed!", Toast.LENGTH_SHORT);
        toast.show();
    }

}
