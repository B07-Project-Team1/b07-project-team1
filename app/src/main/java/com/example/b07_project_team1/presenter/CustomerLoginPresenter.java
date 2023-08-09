package com.example.b07_project_team1.presenter;

import com.example.b07_project_team1.view.CustomerLoginView;
import com.example.b07_project_team1.model.CustomerLoginModel;

public class CustomerLoginPresenter {
    CustomerLoginView view;
    CustomerLoginModel model;

    public CustomerLoginPresenter(CustomerLoginView view, CustomerLoginModel model) {
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

    public void setupNewActivity() {
        view.launchAnimationView();
    }

    public void setMessage(String message) {
        String longErr = "The password is invalid or the user does not have a password.";
        String longErr2 = "There is no user record corresponding to this identifier. The user may have been deleted.";
        String err = message.equals(longErr) ? "Incorrect password." : message.equals(longErr2) ? "No account associated with email." : message;
        view.displayMessage(err);
    }


}
