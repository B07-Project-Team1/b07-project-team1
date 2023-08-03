package com.example.b07_project_team1;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class VendorMarkComplete {
    public void onClickMarkComplete() {

    }

    public void markOrderAsComplete(String orderId, String vendorId, String customerId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        /*
        Database writes:
         orders/orderId/completed, true
         vendors/vendorId/orders/orderId, null
         customers/customerId/pending_orders/orderId, null
         customers/customerId/completed_orders/orderId, true
        */
        Map<String, Object> updates = new HashMap<>();
        updates.put(String.format("orders/%s/completed", orderId), true);
        updates.put(String.format("vendors/%s/orders/%s", vendorId, orderId), null);
        updates.put(String.format("customers/%s/pending_orders/%s", customerId, orderId), null);
        updates.put(String.format("customers/%s/completed_orders/%s", customerId, orderId), true);
        ref.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showToast("Order completed!");
            } else {
                Log.e("FIREBASE", "Couldn't mark order as complete");
                showToast("Error: Couldn't complete order");
            }
        });
    }

    public void showToast(CharSequence text) {
        // TODO: Change the context to the correct class
        Context context = null;
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }

}
