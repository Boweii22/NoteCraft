package com.smartherd.notes.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smartherd.notes.R;

import java.util.concurrent.Executor;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 seconds
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        firebaseAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the main activity after the splash delay
                startMainActivity();
            }
        }, SPLASH_DELAY);
        if (loadSwitchState()) {
            Executor executor = ContextCompat.getMainExecutor(this);
            BiometricPrompt biometricPrompt = new BiometricPrompt(SplashActivity.this,
                    executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    // Show an error message or take appropriate action
                    Toast.makeText(getApplicationContext(), "Biometric authentication error: " + errString, Toast.LENGTH_SHORT).show();
                    // Close the app or navigate user accordingly
                    SplashActivity.this.finish();
                }

                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    // Start the main activity after successful authentication
                    startMainActivity();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    // Show an error message or take appropriate action
                    Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            });

            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric Authentication")
                    .setSubtitle("Log in using your biometric credential")
                    .setNegativeButtonText("Cancel")
                    .build();

            biometricPrompt.authenticate(promptInfo);
        }
    }

    private boolean loadSwitchState() {
        SharedPreferences sharedPreferences = getSharedPreferences("BiometricPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("biometric_switch", false);
    }

    private void startMainActivity() {
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in, navigate to main activity
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        } else {
            // User is not signed in, navigate to login activity
            startActivity(new Intent(SplashActivity.this, AccountActivity.class));
            finish();
        }
    }
}
