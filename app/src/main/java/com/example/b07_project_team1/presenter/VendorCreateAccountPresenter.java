package com.example.b07_project_team1.presenter;

import com.example.b07_project_team1.model.VendorCreateAccountModel;
import com.example.b07_project_team1.view.VendorCreateAccountView;

public class VendorCreateAccountPresenter {
    VendorCreateAccountView view;
    VendorCreateAccountModel model;

    public VendorCreateAccountPresenter(VendorCreateAccountView view, VendorCreateAccountModel model) {
        this.view = view;
        this.model = model;
    }

    public void startNewActivity() {
        view.launchVendorSetup();
    }

    public void createAccount(String email, String password, String repeatPassword) {
        if (email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            view.displayMessage("You have empty fields.");
        }

        else if (!password.equals(repeatPassword)) {
            view.displayMessage("Passwords do not match.");
        }
        else {
            model.createAccount(this, email, password);
        }
    }

    public void setText(String message) {
        String longErr = "The email address is already in use by another account.";
        String longErr2 = "The given password is invalid. [ Password should be at least 6 characters ]";
        String err = message.equals(longErr) ? "Email address already in use." : message.equals(longErr2) ? "Password is too short." :  message;
        view.displayMessage(err);
    }

}
