package com.smartherd.notes.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.smartherd.notes.Model.ModelLanguage;
import com.smartherd.notes.R;
import com.smartherd.notes.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private TextView textFont;

    private ImageView settingsBack;
    private Switch themeSwitch;

    private TextView settingsText;
    private TextView accountTextView;
    private TextView userName;
    private TextView userEmail;
    private TextView textLogout;
    private TextView appearanceTextView;
    private TextView textLanguage;
    private TextView convertToTitle;
    private TextView securityTextView;
    private TextView change_password;
    private Switch setBiometric;
    private Translator translator;
    private String selectedTargetLanguageCode = TranslateLanguage.ENGLISH; // Default to AFRIKAANS
    static String selectedLanguage = "";

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;


    private String[] originalTexts = {"Settings", "Accounts", "Logout","Appearance", "Font", "Language","Convert To", "Theme","Security","Change Password","Set Biometric","Others","Privacy Policy"};
    private int[] textViewIds = {R.id.textSettings, R.id.textAccount, R.id.textLogout, R.id.textAppearance,R.id.textFont, R.id.textLanguage, R.id.convertToTitle,R.id.changeTheme,R.id.textSecurity,R.id.change_password,R.id.setBiometric,R.id.textOthers,R.id.privacy_policy};

    private SharedPreferences sharedPreferences;

    private Switch biometricSwitch;
    private Switch emailUserNotes;
    private TextView privacyPolicy;
    private TextView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isBlackTheme = SharedPreferencesHelper.loadThemeChoice(this);
        if (isBlackTheme) {
            setTheme(R.style.Theme_App_Black);
        } else {
            setTheme(R.style.Theme_App_White);
        }
        setContentView(R.layout.activity_settings);

        biometricSwitch = findViewById(R.id.setBiometric);
        emailUserNotes = findViewById(R.id.emailNotes);
        privacyPolicy = findViewById(R.id.privacy_policy);
        logout = findViewById(R.id.textLogout);

        //Initialize the firebase authentication
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        //Get the current user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //Load the switch state and set it
        emailUserNotes.setChecked(loadEmailSwitchState());

        emailUserNotes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveEmailSwitchState(isChecked);
            if(isChecked){
                Toast.makeText(this, "Email notes enabled", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Email notes disabled", Toast.LENGTH_SHORT).show();
            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, PrivacyPolicyActivity.class));
            }
        });


        // Load the switch state and set it
        biometricSwitch.setChecked(loadSwitchState());

        biometricSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSwitchState(isChecked);
            if (isChecked) {
                Toast.makeText(SettingsActivity.this, "Biometric authentication has been set", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SettingsActivity.this, "Biometric authentication has been disabled", Toast.LENGTH_SHORT).show();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setMessage("Are you sure you want to logout?")
                        .setTitle("Logout")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked Yes button, proceed with logout
                                mAuth.signOut();
                                signOut();
                                // You can add any additional actions after logout if needed
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked No button, dismiss the dialog and do nothing
                                dialog.dismiss();
                            }
                        });
                // Show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Translating...");
        progressDialog.setCancelable(false); // Optional: makes it non-cancellable by user action

        sharedPreferences = getSharedPreferences("Translations", MODE_PRIVATE);
        
        loadSavedTranslations();


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        settingsText = findViewById(R.id.textSettings);
        accountTextView = findViewById(R.id.textAccount);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        textLogout = findViewById(R.id.textLogout);
        appearanceTextView = findViewById(R.id.textAppearance);
        textLanguage = findViewById(R.id.textLanguage);
        convertToTitle = findViewById(R.id.convertToTitle);
        securityTextView = findViewById(R.id.textSecurity);
        change_password = findViewById(R.id.change_password);
        setBiometric = findViewById(R.id.setBiometric);

        settingsBack = findViewById(R.id.settingsBack);

        textFont = findViewById(R.id.textFont);

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });


        if(currentUser != null){
            String userId = currentUser.getUid();
            mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Get the user data
                    if (snapshot.exists()) {
                        String username = snapshot.child("Full name").getValue(String.class);
                        // Now you have the username, you can use it as needed
                        userName.setText(username);

                        Log.d("Username", username);
                    } else {
                        Log.d("User", "User data not found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            String user_email = currentUser.getEmail();
            userEmail.setText(user_email);
        }

        textFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFontSelectionDialog();
            }
        });

        settingsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // Setup the switch
        themeSwitch = findViewById(R.id.changeTheme);
        themeSwitch.setChecked(isBlackTheme); // Set the current state of the switch

        // Handle switch changes
        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save theme choice and apply it
                SharedPreferencesHelper.saveThemeChoice(SettingsActivity.this, isChecked);
                // Recreate the activity to apply the theme
                recreate();
            }
        });

        Spinner languageSpinner = findViewById(R.id.language_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String languageName = parent.getItemAtPosition(position).toString(); // Getting the name from the spinner directly
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        selectedTargetLanguageCode = TranslateLanguage.AFRIKAANS;
                        break;
                    case 2:
                        selectedTargetLanguageCode = TranslateLanguage.ALBANIAN;
                        break;
                    case 3:
                        selectedTargetLanguageCode = TranslateLanguage.ARABIC;
                        break;
                    case 4:
                        selectedTargetLanguageCode = TranslateLanguage.BELARUSIAN;
                        break;
                    case 5:
                        selectedTargetLanguageCode = TranslateLanguage.BENGALI;
                        break;
                    case 6:
                        selectedTargetLanguageCode = TranslateLanguage.BULGARIAN;
                        break;
                    case 7:
                        selectedTargetLanguageCode = TranslateLanguage.CATALAN;
                        break;
                    case 8:
                        selectedTargetLanguageCode = TranslateLanguage.CHINESE;
                        break;
                    case 9:
                        selectedTargetLanguageCode = TranslateLanguage.CROATIAN;
                        break;
                    case 10:
                        selectedTargetLanguageCode = TranslateLanguage.CZECH;
                        break;
                    case 11:
                        selectedTargetLanguageCode = TranslateLanguage.DANISH;
                        break;
                    case 12:
                        selectedTargetLanguageCode = TranslateLanguage.DUTCH;
                        break;
                    case 13:
                        selectedTargetLanguageCode = TranslateLanguage.ENGLISH;
                        break;
                    case 14:
                        selectedTargetLanguageCode = TranslateLanguage.ESPERANTO;
                        break;
                    case 15:
                        selectedTargetLanguageCode = TranslateLanguage.ESTONIAN;
                        break;
                    case 16:
                        selectedTargetLanguageCode = TranslateLanguage.FINNISH;
                        break;
                    case 17:
                        selectedTargetLanguageCode = TranslateLanguage.FRENCH;
                        break;
                    case 18:
                        selectedTargetLanguageCode = TranslateLanguage.GALICIAN;
                        break;
                    case 19:
                        selectedTargetLanguageCode = TranslateLanguage.GEORGIAN;
                        break;
                    case 20:
                        selectedTargetLanguageCode = TranslateLanguage.GERMAN;
                        break;
                    case 21:
                        selectedTargetLanguageCode = TranslateLanguage.GREEK;
                        break;
                    case 22:
                        selectedTargetLanguageCode = TranslateLanguage.GUJARATI;
                        break;
                    case 23:
                        selectedTargetLanguageCode = TranslateLanguage.HAITIAN_CREOLE;
                        break;
                    case 24:
                        selectedTargetLanguageCode = TranslateLanguage.HEBREW;
                        break;
                    case 25:
                        selectedTargetLanguageCode = TranslateLanguage.HINDI;
                        break;
                    case 26:
                        selectedTargetLanguageCode = TranslateLanguage.HUNGARIAN;
                        break;
                    case 27:
                        selectedTargetLanguageCode = TranslateLanguage.ICELANDIC;
                        break;
                    case 28:
                        selectedTargetLanguageCode = TranslateLanguage.INDONESIAN;
                        break;
                    case 29:
                        selectedTargetLanguageCode = TranslateLanguage.IRISH;
                        break;
                    case 30:
                        selectedTargetLanguageCode = TranslateLanguage.ITALIAN;
                        break;
                    case 31:
                        selectedTargetLanguageCode = TranslateLanguage.JAPANESE;
                        break;
                    case 32:
                        selectedTargetLanguageCode = TranslateLanguage.KANNADA;
                        break;
                    case 33:
                        selectedTargetLanguageCode = TranslateLanguage.KOREAN;
                        break;
                    case 34:
                        selectedTargetLanguageCode = TranslateLanguage.LATVIAN;
                        break;
                    case 35:
                        selectedTargetLanguageCode = TranslateLanguage.LITHUANIAN;
                        break;
                    case 36:
                        selectedTargetLanguageCode = TranslateLanguage.MACEDONIAN;
                        break;
                    case 37:
                        selectedTargetLanguageCode = TranslateLanguage.MALAY;
                        break;
                    case 38:
                        selectedTargetLanguageCode = TranslateLanguage.MALTESE;
                        break;
                    case 39:
                        selectedTargetLanguageCode = TranslateLanguage.MARATHI;
                        break;
                    case 40:
                        selectedTargetLanguageCode = TranslateLanguage.NORWEGIAN;
                        break;
                    case 41:
                        selectedTargetLanguageCode = TranslateLanguage.PERSIAN;
                        break;
                    case 42:
                        selectedTargetLanguageCode = TranslateLanguage.POLISH;
                        break;
                    case 43:
                        selectedTargetLanguageCode = TranslateLanguage.PORTUGUESE;
                        break;
                    case 44:
                        selectedTargetLanguageCode = TranslateLanguage.ROMANIAN;
                        break;
                    case 45:
                        selectedTargetLanguageCode = TranslateLanguage.RUSSIAN;
                        break;
                    case 46:
                        selectedTargetLanguageCode = TranslateLanguage.SLOVAK;
                        break;
                    case 47:
                        selectedTargetLanguageCode = TranslateLanguage.SLOVENIAN;
                        break;
                    case 48:
                        selectedTargetLanguageCode = TranslateLanguage.SPANISH;
                        break;
                    case 49:
                        selectedTargetLanguageCode = TranslateLanguage.SWAHILI;
                        break;
                    case 50:
                        selectedTargetLanguageCode = TranslateLanguage.SWEDISH;
                        break;
                    case 51:
                        selectedTargetLanguageCode = TranslateLanguage.TAMIL;
                        break;
                    case 52:
                        selectedTargetLanguageCode = TranslateLanguage.TELUGU;
                        break;
                    case 53:
                        selectedTargetLanguageCode = TranslateLanguage.THAI;
                        break;
                    case 54:
                        selectedTargetLanguageCode = TranslateLanguage.TURKISH;
                        break;
                    case 55:
                        selectedTargetLanguageCode = TranslateLanguage.UKRAINIAN;
                        break;
                    case 56:
                        selectedTargetLanguageCode = TranslateLanguage.URDU;
                        break;
                    case 57:
                        selectedTargetLanguageCode = TranslateLanguage.VIETNAMESE;
                        break;
                    case 58:
                        selectedTargetLanguageCode = TranslateLanguage.WELSH;
                        break;
                    // Add more cases for additional languages
                }
                // Log the selected language code and/or name
                Log.d("LanguageSelection", "Selected language code: " + selectedTargetLanguageCode);
                Log.d("LanguageSelection", "Selected language name: " + languageName);



                selectedLanguage = selectedTargetLanguageCode;

                // Save the selected language code to SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("SelectedLanguageCode", selectedTargetLanguageCode);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ImageView translateDone = findViewById(R.id.translateDone);
        translateDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTranslatorAndTranslate();
            }
        });


    }

    private void signOut() {
        Intent intent = new Intent(SettingsActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveSwitchState(boolean state) {
        SharedPreferences sharedPreferences = getSharedPreferences("BiometricPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("biometric_switch", state);
        editor.apply();
    }
    private void saveEmailSwitchState(boolean state){
        SharedPreferences sharedPreferences1 = getSharedPreferences("EmailNotesPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
        editor1.putBoolean("emailNotes_switch", state);
        editor1.apply();
    }

    private boolean loadSwitchState() {
        SharedPreferences sharedPreferences = getSharedPreferences("BiometricPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("biometric_switch", false);
    }
    private boolean loadEmailSwitchState(){
        SharedPreferences sharedPreferences = getSharedPreferences("EmailNotesPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("emailNotes_switch", false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        applySelectedFont();
    }

    private void showFontSelectionDialog() {
        final String[] fontStyles = {"Default", "Sans", "Serif", "Monospace"};
        String currentFontStyle = SharedPreferencesHelper.getFontStyle(this);
        int checkedItem = Arrays.asList(fontStyles).indexOf(Character.toUpperCase(currentFontStyle.charAt(0)) + currentFontStyle.substring(1));
        if (checkedItem == -1) checkedItem = 0; // Default if not found

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Font Style");
        builder.setSingleChoiceItems(fontStyles, checkedItem, (dialog, which) -> {
            String selectedFontStyle = fontStyles[which].toLowerCase();
            SharedPreferencesHelper.setFontStyle(SettingsActivity.this, selectedFontStyle);
            dialog.dismiss(); // Dismiss dialog after selection
            applySelectedFont();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void applySelectedFont() {
        applyFontStyleToView(settingsText, SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToView(accountTextView, SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToView(userName, SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToView(userEmail, SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToView(textLogout, SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToView(appearanceTextView, SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToView(textFont, SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToView(textLanguage, SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToView(convertToTitle, SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToView(securityTextView, SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToView(change_password, SharedPreferencesHelper.getFontStyle(this));
    }


    private void applyFontStyleToView(TextView textView, String fontStyle) {
        switch (fontStyle) {
            case "sans":
                textView.setTypeface(Typeface.SANS_SERIF);
                break;
            case "serif":
                textView.setTypeface(Typeface.SERIF);
                break;
            case "monospace":
                textView.setTypeface(Typeface.MONOSPACE);
                break;
            default:
                textView.setTypeface(Typeface.DEFAULT);
                break;
        }
    }

    private void loadSavedTranslations() {
        for (int i = 0; i < textViewIds.length; i++) {
            String translation = sharedPreferences.getString("translation_" + i, null);
            if (translation != null) {
                TextView textView = findViewById(textViewIds[i]);
                textView.setText(translation);
            }
        }
    }

    private void initTranslatorAndTranslate() {
        progressDialog.show();
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(selectedTargetLanguageCode)
                        .build();
        translator = com.google.mlkit.nl.translate.Translation.getClient(options);

        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        unused -> translateTexts())
                .addOnFailureListener(
                        exception -> {
                            progressDialog.dismiss();/* Handle model download failure */
                        });
    }

    private void translateTexts() {
        for (int i = 0; i < originalTexts.length; i++) {
            final int index = i;
            translator.translate(originalTexts[index])
                    .addOnSuccessListener(
                            translatedText -> {
                                TextView textView = findViewById(textViewIds[index]);
                                textView.setText(translatedText);
                                saveTranslation("translation_" + index, translatedText);

                                // Check if it's the last text to translate and dismiss the dialog
                                if (index == originalTexts.length - 1) {
                                    progressDialog.dismiss();
                                }
                            })
                    .addOnFailureListener(
                            exception -> {
                                // Dismiss dialog on failure
                                progressDialog.dismiss();
                                // Handle translation failure
                            });
        }
    }

    private void saveTranslation(String key, String translation) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, translation);
        editor.apply();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (translator != null) {
            translator.close();
        }
    }
}
