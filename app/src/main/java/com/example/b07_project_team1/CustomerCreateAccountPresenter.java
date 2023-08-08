package com.example.b07_project_team1;

public class CustomerCreateAccountPresenter {
    CustomerCreateAccountModel model;
    CustomerCreateAccountView view;

    public CustomerCreateAccountPresenter( CustomerCreateAccountView view, CustomerCreateAccountModel model) {
        this.model = model;
        this.view = view;
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

    public void startNewActivity() {
        view.launchMallActivity();
    }

    public void setText(String message) {
        String longErr = "The email address is already in use by another account.";
        String longErr2 = "The given password is invalid. [ Password should be at least 6 characters ]";
        String err = message.equals(longErr) ? "Email address already in use." : message.equals(longErr2) ? "Password is too short." :  message;
        view.displayMessage(err);
    }
}
