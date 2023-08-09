package com.example.b07_project_team1.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.b07_project_team1.CustomerCreateAccountView;
import com.example.b07_project_team1.MallActivity;
import com.example.b07_project_team1.R;
import com.example.b07_project_team1.model.CustomerLoginModel;
import com.example.b07_project_team1.presenter.CustomerLoginPresenter;

public class CustomerLoginView extends AppCompatActivity {
    TextView errorTextView;
    EditText emailField;
    EditText passwordField;

    Button loginButton;
    CustomerLoginPresenter presenter;
    MotionLayout motionLayout;
    ConstraintLayout cusLoginPage;
    Button createAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);
        errorTextView = (TextView) findViewById(R.id.customer_login_activity_invalid_credentials_error);

        cusLoginPage = findViewById(R.id.customer_login_page);

        motionLayout = findViewById(R.id.include_customer_login);

        createAccount = findViewById(R.id.create_account_button_customer_login_activity);

        loginButton = (Button) findViewById(R.id.login_button_customer_login_activity);
        loginButton.setOnTouchListener(onTouchLogin);

        emailField = (EditText) findViewById(R.id.customer_login_activity_email_input_field);
        emailField.setOnFocusChangeListener(onFocusChangeEditText);

        passwordField = (EditText) findViewById(R.id.customer_login_activity_password_input_field);
        passwordField.setOnFocusChangeListener(onFocusChangeEditText);

        presenter = new CustomerLoginPresenter(this, new CustomerLoginModel());
    }

    public void displayMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }


    public void onClickLogin(View view) {
        String emailText = ((EditText) findViewById(R.id.customer_login_activity_email_input_field)).getText().toString();
        String passwordText = ((EditText) findViewById(R.id.customer_login_activity_password_input_field)).getText().toString();

        presenter.signIn(emailText, passwordText);
    }

    public void launchAnimationView() {
        // Transition the user to the mall page using animation
        motionLayout.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.INVISIBLE);
        createAccount.setVisibility(View.INVISIBLE);
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
                Log.e("TRANSITION", "COMPLETED ANIMATION");
                cusLoginPage.setVisibility(View.INVISIBLE);
                launchMallView();
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

            }
        });
    }

    public void launchMallView() {
        Intent mallViewIntent = new Intent(getApplicationContext(), MallActivity.class);
        startActivity(mallViewIntent);
        finish();
    }

    public void onClickCreateAccount(View view) {
        Intent customerCreateAccount = new Intent(this, CustomerCreateAccountView.class);
        startActivity(customerCreateAccount);
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