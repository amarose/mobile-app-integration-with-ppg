package com.example.mobileappwithlogin.activities;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileappwithlogin.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    // Declare variables
    EditText emailEditText;
    ProgressBar progressBar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Selectors
        emailEditText = findViewById(R.id.resetPassEmailInput);
        Button resetButton = findViewById(R.id.resetPassButton);
        progressBar = findViewById(R.id.progressBar);

        // Instantiate Firebase authorization variable
        auth = FirebaseAuth.getInstance();

        // On click listener on reset password button
        resetButton.setOnClickListener(view -> resetPassword());
    }

    // Reset password logic with validation
    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();

        if(email.isEmpty()){
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Provide a valid email!");
            emailEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(ForgotPassword.this, "Check you email to reset password.", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(ForgotPassword.this, "Something went wrong! Try again.", Toast.LENGTH_LONG).show();
            }
            progressBar.setVisibility(View.GONE);
        });
    }
}