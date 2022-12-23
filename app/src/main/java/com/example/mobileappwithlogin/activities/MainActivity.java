package com.example.mobileappwithlogin.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.mobileappwithlogin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pushpushgo.sdk.PushPushGo;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    // Declare variables
    private EditText editTextEmail, editTextPassword;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    // Instantiate PPG
    private final PushPushGo ppg = PushPushGo.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Display notification request prompt if os >= Tiramisu (13)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationsPermission();
        }

        // Register user for subscription to notifications
        ppg.registerSubscriber();

        // Selectors and on click listeners
        TextView register = findViewById(R.id.registerUserButton);
        register.setOnClickListener(this);

        Button signIn = findViewById(R.id.loginButton);
        signIn.setOnClickListener(this);

        editTextEmail = findViewById(R.id.emailInput);
        editTextPassword = findViewById(R.id.passwordInput);

        progressBar = findViewById(R.id.progressBar);

        // Initialize Firebase authorization variable
        mAuth = FirebaseAuth.getInstance();

        TextView forgotPassword = findViewById(R.id.forgotPassButton);
        forgotPassword.setOnClickListener(this);
    }

    // On click listeners logic
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.registerUserButton:
                startActivity(new Intent(this, RegisterUser.class));
                break;

            case R.id.loginButton:
                ppg.registerSubscriber(); // for Android 13 after permission we need to register once more, so user will register after logging in or revisting main activity
                userLogin();
                break;

            case R.id.forgotPassButton:
                startActivity(new Intent(this, ForgotPassword.class));
                break;
        }
    }

    // User login logic and validation
    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(email.isEmpty()){
            editTextEmail.setError("Email is required.");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please enter valid email.");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editTextPassword.setError("Password is required.");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            editTextPassword.setError("Min password length is 6 characters.");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                if(user.isEmailVerified()){
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                }else{
                    user.sendEmailVerification();
                    Toast.makeText(MainActivity.this, "Check your email to verify your account!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }

            }else{
                Toast.makeText(MainActivity.this, "Failed to login! Please check your credentials.", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Method for displaying request notifications prompt
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestNotificationsPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 0);
        }
    }
}