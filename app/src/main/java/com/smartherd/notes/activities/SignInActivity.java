package com.smartherd.notes.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smartherd.notes.R;

public class SignInActivity extends AppCompatActivity {

    private Button button_sign_in;
    FirebaseAuth mAuth;
    private EditText emailText, passwordText;
    private CheckBox show_password;
    private TextView registerNow;
    private TextView forgotPassword;
    // Declare the ProgressDialog as a field in your activity or fragment
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://notecraft-dd55e-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Initializing UI components
        emailText = findViewById(R.id.editTextEmailPhone); // Replace 'editTextEmail' with your actual ID in layout
        passwordText = findViewById(R.id.editTextPassword);
        show_password = findViewById(R.id.showPassword);
        button_sign_in = findViewById(R.id.buttonSignIn);
        registerNow = findViewById(R.id.textViewRegister);
        forgotPassword = findViewById(R.id.textViewForgotPassword);

        mAuth = FirebaseAuth.getInstance();


        // Toggle password visibility
        show_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, ForgotPasswordActivity.class));
            }
        });

        // Sign-in logic
        button_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailTxt = emailText.getText().toString().trim();
                final String passwordTxt = passwordText.getText().toString().trim();

                if (emailTxt.isEmpty() || passwordTxt.isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Please enter your credentials", Toast.LENGTH_SHORT).show();
                } else {

                    showProgressDialog();

                    mAuth.signInWithEmailAndPassword(emailTxt, passwordTxt)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        dismissProgressDialog();
                                        if(mAuth.getCurrentUser().isEmailVerified()){
                                            Toast.makeText(SignInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }else {
                                            dismissProgressDialog();
                                            Toast.makeText(SignInActivity.this, "Please verify your email address", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        dismissProgressDialog();
                                        // Here we're adding the exception message to the Toast to give more insight into what went wrong
                                        String errorMessage = task.getException() == null ? "Authentication failed." : task.getException().getMessage();
                                        Toast.makeText(SignInActivity.this, "Authentication failed: " + errorMessage, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });

        // Navigate to Registration Activity
        registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace RegistrationActivity.class with your actual registration activity class
                startActivity(new Intent(SignInActivity.this, RegisterActivity.class));
            }
        });
    }

    // Method to dismiss progress dialog
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    // Method to show progress dialog
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Signing in...");
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    private void saveTextToSharedPreferences(String getEmailText) {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("keyForText", getEmailText);
        editor.apply(); // Or editor.commit(); if you want to write data synchronously

    }

    private void signInUser(String email, String password) {
        String encodedEmail = email.replace(".", ",");
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(encodedEmail)) {
                    final String getPassword = snapshot.child(encodedEmail).child("Password").getValue(String.class);
                    if (getPassword != null && getPassword.equals(password)) {
                        Toast.makeText(SignInActivity.this, "Successfully Logged in", Toast.LENGTH_SHORT).show();

                        // Navigate to MainActivity
                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignInActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignInActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SignInActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTextPermanently(String text) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("savedText", text);
        editor.apply();
    }

}
