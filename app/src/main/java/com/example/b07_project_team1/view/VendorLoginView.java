package com.example.b07_project_team1.view;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.b07_project_team1.R;
import com.example.b07_project_team1.StoreActivity;
import com.example.b07_project_team1.VendorCreateAccountView;
import com.example.b07_project_team1.VendorSetup;
import com.example.b07_project_team1.model.VendorLoginModel;
import com.example.b07_project_team1.presenter.VendorLoginPresenter;

public class VendorLoginView extends AppCompatActivity {
    TextView errorTextView;
    EditText emailField;
    EditText passwordField;

    Button loginButton;
    Button createAccButton;
    VendorLoginPresenter presenter;

    MotionLayout motionLayout;
    MotionLayout venLoginPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_login);
        errorTextView = (TextView) findViewById(R.id.vendor_login_activity_invalid_credentials_error);

        loginButton = (Button) findViewById(R.id.login_button_vendor_login_activity);
        loginButton.setOnTouchListener(onTouchLogin);

        motionLayout = findViewById(R.id.include_vendor_login);

        venLoginPage = findViewById(R.id.vendor_login_page);

        createAccButton = findViewById(R.id.create_account_button_vendor_login_activity);

        emailField = (EditText) findViewById(R.id.vendor_login_activity_email_input_field);
        emailField.setOnFocusChangeListener(onFocusChangeEditText);

        passwordField = (EditText) findViewById(R.id.vendor_login_activity_password_input_field);
        passwordField.setOnFocusChangeListener(onFocusChangeEditText);

        presenter = new VendorLoginPresenter(this, new VendorLoginModel());
    }

    public void displayMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }


    public void onClickLogin(View view) {
        String emailText = ((EditText) findViewById(R.id.vendor_login_activity_email_input_field)).getText().toString();
        String passwordText = ((EditText) findViewById(R.id.vendor_login_activity_password_input_field)).getText().toString();

        presenter.signIn(emailText, passwordText);

    }

    public void launchPageTransition(boolean flag, String logoUrl, String userId) {
        motionLayout.setVisibility(View.VISIBLE);
        createAccButton.setVisibility(View.INVISIBLE);
        loginButton.setVisibility(View.INVISIBLE);

        motionLayout.transitionToEnd();
        motionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {

            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                venLoginPage.setVisibility(View.INVISIBLE);
                if (flag) {
                    launchVendorSetup();
                } else {
                    launchStoreActivity(logoUrl, userId);
                }
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

            }
        });
    }

    public void launchVendorSetup() {
        Intent vendorSetupIntent = new Intent(getApplicationContext(), VendorSetup.class);
        startActivity(vendorSetupIntent);
        finish();
    }

    public void launchStoreActivity(String logoUrl, String uid) {
        Intent storeActivity = new Intent(getApplicationContext(), StoreActivity.class);
        storeActivity.putExtra("IS_VENDOR", true);
        storeActivity.putExtra("VENDOR_STORE_LOGO", logoUrl);
        storeActivity.putExtra("VENDOR_ID", uid);
        startActivity(storeActivity);
        finish();
    }

    public void onClickCreateAccount(View view) {
        Intent vendorCreateAccount = new Intent(this, VendorCreateAccountView.class);
        startActivity(vendorCreateAccount);
    }

    private View.OnTouchListener onTouchLogin = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                loginButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.medium_gray));
                loginButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.entry_button_background_onpress));
            }
            else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                loginButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.pure_white));
                loginButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.entry_button_background));
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