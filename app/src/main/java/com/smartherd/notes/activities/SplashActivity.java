package com.smartherd.notes.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smartherd.notes.R;

import java.util.concurrent.Executor;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 seconds
    private FirebaseAuth firebaseAuth;
    private boolean biometricEnabled = false;
    private BiometricManager biometricManager;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    private static final int BIOMETRIC_STRONG = BiometricManager.Authenticators.BIOMETRIC_STRONG;
    private static final int DEVICE_CREDENTIAL = BiometricManager.Authenticators.DEVICE_CREDENTIAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        firebaseAuth = FirebaseAuth.getInstance();

        // Load the switch state from SharedPreferences
        biometricEnabled = loadSwitchState();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the main activity after the splash delay
                if (biometricEnabled) {
                    // If biometric authentication is enabled, show the authentication prompt
                    showBiometricPrompt();
                } else {
                    // If biometric authentication is not enabled, proceed to start main activity directly
                    startMainActivity();
                }
            }
        }, SPLASH_DELAY);
    }

    private boolean loadSwitchState() {
        SharedPreferences sharedPreferences = getSharedPreferences("BiometricPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("biometric_switch", false);
    }

    private void showBiometricPrompt() {
        // Initialize BiometricManager for checking
        biometricManager = BiometricManager.from(this);

        // Initialize BiometricPrompt to setup success & error callbacks of biometric prompt
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                showSnackBar("Authentication error: Code: " + errorCode + " (" + errString + ")");
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                showSnackBar("Failed to authenticate. Please try again.");
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                int type = result.getAuthenticationType();
                showSnackBar("\uD83C\uDF89 Authentication successful! Type: " + type + " \uD83C\uDF89");
                startMainActivity();
                finish();
            }
        });

        // Initialize PromptInfo to set title, subtitle, and authenticators of the biometric prompt
        try {
            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("NoteCraft Biometric authentication")
                    .setSubtitle("Please authenticate yourself first.")
                    .setAllowedAuthenticators(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)
                    .build();
        } catch (Exception e) {
            showSnackBar(e.getMessage() != null ? e.getMessage() : "Unable to initialize PromptInfo");
        }

//        // Setup on click listener for button
//        Button biometricButton = findViewById(R.id.button_biometric);
//        biometricButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tryAuthenticateBiometric();
//            }
//        });
        // Trigger biometric authentication immediately when the app is opened
        tryAuthenticateBiometric();
    }


    /**
     * Attempt to show biometric prompt dialog to user.
     */
    private void tryAuthenticateBiometric() {
        checkDeviceCapability();
    }

    /**
     * Check the device capability for biometric.
     *
     * If the device is capable, [onSuccess] will be called. Otherwise, a [Snackbar] is shown.
     */
    private void checkDeviceCapability() {
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                biometricPrompt.authenticate(promptInfo);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                showSnackBar("No biometric features available on this device");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                showSnackBar("Biometric features are currently unavailable");
                break;
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                showSnackBar("Biometric options are incompatible with the current Android version");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                    enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                    startActivityForResult(enrollIntent, 0);
                } else {
                    showSnackBar("Could not request biometric enrollment in API level < 30");
                }
                break;
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                showSnackBar("Biometric features are unavailable because security vulnerabilities have been discovered in one or more hardware sensors");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL));
        }
    }

    private void showSnackBar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
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
