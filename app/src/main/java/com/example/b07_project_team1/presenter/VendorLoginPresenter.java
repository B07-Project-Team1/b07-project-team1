package com.example.b07_project_team1.presenter;

import com.example.b07_project_team1.view.VendorLoginView;
import com.example.b07_project_team1.model.VendorLoginModel;

public class VendorLoginPresenter {
    VendorLoginView view;
    VendorLoginModel model;

    public VendorLoginPresenter(VendorLoginView view, VendorLoginModel model) {
        this.view = view;
        this.model = model;
    }

    public void signIn(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            view.displayMessage("You have empty fields.");
        }
        else {
            model.signIn(this, email, password);
        }
    }

    public void onSignIn() {
        String uid = model.getUID();
        if (uid != null) {
            model.generateUserData(this, uid);
        }
    }

    public void setupNewActivity(String brandName, String logoUrl) {
        if (brandName == null || logoUrl == null) {
            view.launchPageTransition(true, null, null);
        } else {
            view.launchPageTransition(false, logoUrl, model.getUID());
        }
    }

    public void setMessage(String message) {
        String longErr = "The password is invalid or the user does not have a password.";
        String longErr2 = "There is no user record corresponding to this identifier. The user may have been deleted.";
        String longErr3 = "A network error (such as timeout, interrupted connection or unreachable host) has occurred.";
        String msg = message.equals(longErr) ? "Incorrect password." : message.equals(longErr2) ? "No account associated with email." : message.equals(longErr3) ? "Network error." : message;
        view.displayMessage(msg);
    }
}
