package com.example.b07_project_team1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class VendorCreateAccountView extends AppCompatActivity {
    Button createAccountButton;
    TextView errorTextView;
    EditText emailField;
    EditText passwordField;
    EditText repeatPasswordField;
    VendorCreateAccountPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_create_account);
        errorTextView = (TextView) findViewById(R.id.vendor_create_account_activity_invalid_credentials_error);

        createAccountButton = (Button) findViewById(R.id.create_account_button_vendor_create_account_activity);
        createAccountButton.setOnTouchListener(onTouchCreateAccount);

        emailField = (EditText) findViewById(R.id.vendor_create_account_activity_email_input_field);
        emailField.setOnFocusChangeListener(onFocusChangeEditText);

        passwordField = (EditText) findViewById(R.id.vendor_create_account_activity_password_input_field);
        passwordField.setOnFocusChangeListener(onFocusChangeEditText);

        repeatPasswordField = (EditText) findViewById(R.id.vendor_create_account_activity_repeat_password_input_field);
        repeatPasswordField.setOnFocusChangeListener(onFocusChangeEditText);

        presenter = new VendorCreateAccountPresenter(this, new VendorCreateAccountModel());
    }

    public void displayMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void onClickRegister(View view) {
        String emailText = ((EditText) findViewById(R.id.vendor_create_account_activity_email_input_field)).getText().toString();
        String passwordText = ((EditText) findViewById(R.id.vendor_create_account_activity_password_input_field)).getText().toString();
        String repeatPasswordText = ((EditText) findViewById(R.id.vendor_create_account_activity_repeat_password_input_field)).getText().toString();

        presenter.createAccount(emailText, passwordText, repeatPasswordText);
    }

    public void launchVendorSetup() {
        Intent vendorSetupIntent = new Intent(getApplicationContext(), VendorSetup.class);
        startActivity(vendorSetupIntent);
    }

    private View.OnTouchListener onTouchCreateAccount = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                createAccountButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.medium_gray));
                createAccountButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.entry_button_background_onpress));
            }
            else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                createAccountButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.pure_white));
                createAccountButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.entry_button_background));
            }
            return false;
        }
    };

    private View.OnFocusChangeListener onFocusChangeEditText = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                view.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.login_info_entry_box_background_active));
            }
            else {
                view.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.login_info_entry_box_background));
            }
        }
    };
}
