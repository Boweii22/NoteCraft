package com.smartherd.notes.activities;


import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.core.widget.NestedScrollView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;


import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.smartherd.notes.R;
import com.smartherd.notes.SharedPreferencesHelper;
import com.smartherd.notes.entities.Note;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;
import yuku.ambilwarna.AmbilWarnaDialog;

public class CreateNoteActivity extends AppCompatActivity {

    private final String stringURLEndPoint = "https://api.openai.com/v1/chat/completions";

    private final String API_KEY = "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";

    private String stringOutput = "";
    private String stringOutput1 = "";

    String title, content, noteId, subtitle, url, color, image_path, search, reminder, document_path, audio_path, document_text;



    private NestedScrollView scrollView;
    LinearLayout layoutMiscellaneous;

    LinearLayout reminderBackground;


    private EditText inputNoteTitle, inputNoteSubtitle, inputNoteText;
    private TextView wordCountTextView;
    private TextView textDateTime;
    private TextView thinking_textview;
    private EditText documentTextView;
    private View viewSubtitleIndicator;
    private ImageView imageNote;
    private TextView textWebURL;
    private LinearLayout layoutWebURL;
    private String selectedNoteColor;
    private String reminderTime;
    private String selectedImagePath;
    private String audioPath;
    private String documentPath;
    private ImageView boldText;
    private ImageView italicText;
    private ImageView underlineText;
    private ImageView centerAlign;
    private ImageView leftAlign;
    private ImageView rightAlign;
    private ImageView header1;
    private ImageView header2;
    private ImageView textColorPicker;
    private ImageView numberingBullets;
    private ImageView cancel_audio;
    private ImageView ibRecord;
    private LottieAnimationView audioRecording;
    TextRecognizer textRecognizer;
    private LinearLayout audio_background;
    private boolean isEditMode = false;
    private boolean isState = false;
    File file;
    private ProgressDialog progressDialog;
    private Translator translator;
    private SharedPreferences sharedPreferences;

    FirebaseFirestore firebaseFirestore;

    ImageView menuFeatures;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 4;
    private static final int REQUEST_CODE_SELECT_IMAGE_FOR_OCR = 6;

    // Updated code for color picker
    private static final int REQUEST_CODE_COLOR_PICKER = 3;

    private static final String PREF_TITLE_COLOR = "TitleColor";
    private static final String PREF_SUBTITLE_COLOR = "SubtitleColor";
    private static final String PREF_TEXT_COLOR = "TextColor";

    private int titleColor, subtitleColor, textColor;

    private AlertDialog dialogAddURL;
    private AlertDialog dialogDeleteNote;
    ImageView addReminder;

    private Note alreadyAvailableNote;
    private static final String PREFS_NAME = "NotePrefs";
    private static final String PREF_TEXT_ALIGNMENT = "TextAlignment";
    private static final String PREF_FORMATTED_TEXT = "FormattedText";

//
//    private static final int REQUEST_PERMISSION_CODE = 1;
    private static final int REQ_CODE_SPEECH_INPUT = 100;


    private static final int PICK_DOCUMENT_REQUEST = 17;
//    private static final int PICK_DOCUMENT_REQUEST1 = 15;

    static final String CHANNEL_ID = "CHANNEL_ID_NOTIFICATION";
    static final int NOTIFICATION_ID = 5;

    private boolean isTintChanged = false;
    private static final int REQUEST_AUDIO_PERMISSION_CODE = 101;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    boolean isRecording = false;
    boolean isPlaying = false;
    TextView tvTime;
    ImageView ibPlay;
    TextView timeText;
    int seconds = 0;
    String path = null;
    LottieAnimationView lavPlaying;
    LottieAnimationView lavFormat;
    ImageView audioFormat;
    int dummySeconds = 0;
    int playableSeconds = 0;
    Handler handler;
    private final Handler handler1 = new Handler();
    private Runnable thinkingAnimationRunnable;
    private String[] originalEditTexts = {"Note Title","Note Subtitle","Type note here..."};
    private int[] editTextViewIds = {R.id.inputNoteTitle,R.id.inputNoteSubtitle,R.id.inputNote};

    ExecutorService executorService = Executors.newSingleThreadExecutor();
    private String selectedLanguageCode;
    ImageView translateNotes;
    FirebaseAuth mAuth;
    String user_email;
    long futureTimeInMillis;
    static long currentTimeInMillis = System.currentTimeMillis();
    private SharedPreferences shared_preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isBlackTheme = SharedPreferencesHelper.loadThemeChoice(this);
        if (isBlackTheme) {
            setTheme(R.style.Theme_App_Black);
        } else {
            setTheme(R.style.Theme_App_White);
        }
        setContentView(R.layout.activity_create_note);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        createNotificationChannel();

        translateNotes = findViewById(R.id.translateNote);

        //initialize firebase authentication
        mAuth = FirebaseAuth.getInstance();

        //Get the current user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        reminderBackground = findViewById(R.id.reminder_background);

        timeText = reminderBackground.findViewById(R.id.time_text);

        shared_preferences = getSharedPreferences("tutorial", Context.MODE_PRIVATE);





        scrollView = findViewById(R.id.scrollView);
        layoutMiscellaneous = findViewById(R.id.layoutMiscellaneous);
        updateScrollViewBackground(isBlackTheme); // Call this method to set the LinearLayout background based on the theme

        translateNotes = findViewById(R.id.translateNote);
        translateNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if there is text to translate
                String titleText = inputNoteTitle.getText().toString();
                String subtitleText = inputNoteSubtitle.getText().toString();
                String noteText = inputNoteText.getText().toString();

                // Assuming you have a method translateText that handles the translation and setting of the translated text
                if (!titleText.isEmpty()) {
                    translateText(titleText, translatedText -> inputNoteTitle.setText(translatedText));
                }
                if (!subtitleText.isEmpty()) {
                    translateText(subtitleText, translatedText -> inputNoteSubtitle.setText(translatedText));
                }
                if (!noteText.isEmpty()) {
                    translateText(noteText, translatedText -> inputNoteText.setText(translatedText));
                }
            }
        });

                progressDialog = new ProgressDialog(CreateNoteActivity.this);
                progressDialog.setMessage("Translating...");
                progressDialog.setCancelable(false);

                sharedPreferences = getSharedPreferences("Translations_ThirdActivity", MODE_PRIVATE);

                // Read the selected language code from SharedPreferences and log it
                SharedPreferences sharedPreferencess = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
                selectedLanguageCode = sharedPreferencess.getString("SelectedLanguageCode", SettingsActivity.selectedLanguage); // "DefaultCode" as a fallback
                Log.d("CreateNoteActivity", "Selected Language Code: " + selectedLanguageCode);


                // Directly start translation as we already know the target language
                initTranslatorAndTranslate(selectedLanguageCode);



        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(v -> {
            if(inputNoteText.getText().toString().isEmpty() && inputNoteTitle.getText().toString().isEmpty() && inputNoteSubtitle.getText().toString().isEmpty()){

                onBackPressed();
            }else{
                saveNote();
            }
        });

        menuFeatures = findViewById(R.id.menuFeatures);
        addReminder = findViewById(R.id.addReminder);
        addReminder.setOnClickListener(v -> showTimePickerDialog());
        //                FeaturesBottomSheetFragment featuresBottomSheetFragment = new FeaturesBottomSheetFragment();
        //                featuresBottomSheetFragment.show(getSupportFragmentManager(), featuresBottomSheetFragment.getTag());
        menuFeatures.setOnClickListener(this::showBottomMenu);


        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle = findViewById(R.id.inputNoteSubtitle);
        inputNoteText = findViewById(R.id.inputNote);
        textDateTime = findViewById(R.id.textDateTime);
        documentTextView = findViewById(R.id.documentTextView);
        viewSubtitleIndicator = findViewById(R.id.viewSubtitleIndicator);
        imageNote = findViewById(R.id.imageNote);
        textWebURL = findViewById(R.id.textWebURL);
        layoutWebURL = findViewById(R.id.layoutWebURL);
        centerAlign = findViewById(R.id.centerAlign);
        leftAlign = findViewById(R.id.leftAlign);
        rightAlign = findViewById(R.id.rightAlign);

        wordCountTextView = findViewById(R.id.wordCountTextView);
        thinking_textview = findViewById(R.id.thinking_textview);

        //receive data
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        subtitle = getIntent().getStringExtra("subTitle");
        url = getIntent().getStringExtra("url");
        color = getIntent().getStringExtra("color");
        image_path = getIntent().getStringExtra("image_path");
        noteId = getIntent().getStringExtra("noteId");
        search = getIntent().getStringExtra("title".toLowerCase());
        reminder = getIntent().getStringExtra("reminder");
        document_path = getIntent().getStringExtra("document_path");
        audio_path = getIntent().getStringExtra("audio_path");
        document_text = getIntent().getStringExtra("document_text");



        Log.d("CreateNoteActivity","The subtitle text is: " + subtitle);
        inputNoteTitle.setText(title);
        inputNoteText.setText(content);
        inputNoteSubtitle.setText(subtitle);
        textWebURL.setText(url);
        selectedNoteColor = color;
        selectedImagePath = image_path;
        audioPath = audio_path;
        documentTextView.setText(document_text);

        reminderTime = reminder;
        documentPath = document_path;

        timeText.setText(reminderTime);


        firebaseFirestore = FirebaseFirestore.getInstance();


        Log.d("CreateNoteActivity","The reminderTime is " + reminderTime);

        if(noteId != null && !noteId.isEmpty()){
            isEditMode = true;
//            reminderBackground.setVisibility(View.VISIBLE);
        }


        // Check if tutorial has been shown before
        if (!shared_preferences.getBoolean("tutorial_shown", false)) {
             new GuideView.Builder(this)
                .setTitle("Translate")
                .setContentText("Click to update translation")
                .setGravity(smartdevelop.ir.eram.showcaseviewlib.config.Gravity.auto)
                .setDismissType(DismissType.anywhere)
                .setTargetView(menuFeatures)
                .setTitleTextSize(12)
                .setTitleTextSize(16)
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        nextSuggestion();
                    }
                }).build().show();
        }


        //Initialize word count
        updateWordCount();

        //A TextWatcher to listen for changes in the inputNoteText view
        inputNoteText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Update word count after text changes
                updateWordCount();
            }
        });



        textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );
        ImageView imageSave = findViewById(R.id.imageSave);
        // Access SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        // Get the switch state or default to false if the key doesn't exist
        boolean isEmailSwitchChecked = sharedPreferences.getBoolean("EmailSwitchState", false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(currentUser != null){
            user_email = currentUser.getEmail();
        }


        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CreateNoteActivity","Checking switch state" + loadSwitchState());
                //use the switch state
                if(loadSwitchState()){
                    // Email details
                    Map<String, Object> email = new HashMap<>();
                    Map<String, String> message = new HashMap<>();
                    message.put("subject", inputNoteTitle.getText().toString());
                    message.put("text", inputNoteText.getText().toString());

                    email.put("to", user_email); // To email address
                    email.put("message", message);

                    // Add a new document with a generated ID to the `mail` collection
                    db
                            .collection("mail").add(email)
                            .addOnSuccessListener(documentReference -> Log.d("EmailTrigger", "DocumentSnapshot added with ID: " + documentReference.getId()))
                            .addOnFailureListener(e -> Log.w("EmailTrigger", "Error adding document", e));

                }
                saveNote();
            }
        });
        selectedNoteColor = "#333333";
        selectedImagePath = "";
        audio_path = "";

        titleColor = retrieveColorPreference(PREF_TITLE_COLOR, Color.BLACK);
        subtitleColor = retrieveColorPreference(PREF_SUBTITLE_COLOR, Color.BLACK);
        textColor = retrieveColorPreference(PREF_TEXT_COLOR, Color.BLACK);

        inputNoteTitle.setTextColor(titleColor);
        inputNoteSubtitle.setTextColor(subtitleColor);
        inputNoteText.setTextColor(textColor);

        numberingBullets = findViewById(R.id.numberBullets);
        numberingBullets.setOnClickListener(v -> {
            showNumberedBullets();
            toggleTint(numberingBullets);
        });

        textColorPicker = findViewById(R.id.textColor);
        textColorPicker.setOnClickListener(v -> {
            toggleTint(textColorPicker);
            showColorPickerDialog(new OnColorSelectedListener() {
                @Override
                public void onColorSelected(int color) {
                    inputNoteTitle.setTextColor(color);
                    inputNoteSubtitle.setTextColor(color);
                    inputNoteText.setTextColor(color);
                    titleColor = color;
                    subtitleColor = color;
                    textColor = color;
                    saveColorPreference(PREF_TITLE_COLOR, color);
                    saveColorPreference(PREF_SUBTITLE_COLOR, color);
                    saveColorPreference(PREF_TEXT_COLOR, color);
                }
            });
        });

        boldText = findViewById(R.id.bold);
        underlineText = findViewById(R.id.underline);
        italicText = findViewById(R.id.italic);
        header1 = findViewById(R.id.header1);
        header2 = findViewById(R.id.header2);

        header1.setOnClickListener(v -> {
            applyHeader(1);
            toggleTint(header1);
        });
        header2.setOnClickListener(v -> {
            applyHeader(2);
            toggleTint(header2);
        });

        boldText.setOnClickListener(v -> {
            applyStyle(Typeface.BOLD);
            toggleTint(boldText);
        });
        ImageViewCompat.setImageTintList(boldText, getResources().getColorStateList(R.color.colorIcons));
        underlineText.setOnClickListener(v -> {
            applyUnderline();
            toggleTint(underlineText);
        });
        italicText.setOnClickListener(v -> {
            applyItalic();
            toggleTint(italicText);
        });

        centerAlign.setOnClickListener(v -> {
            setGravityAndSave(Gravity.CENTER_HORIZONTAL);
            toggleTint(centerAlign);
        });
        leftAlign.setOnClickListener(v -> {
            setGravityAndSave(Gravity.START);
            toggleTint(leftAlign);
        });
        rightAlign.setOnClickListener(v -> {
            setGravityAndSave(Gravity.END);
            toggleTint(rightAlign);
        });

        // Check if the activity is opened for viewing or updating an existing note
        if(getIntent().getBooleanExtra("isViewOrUpdate", false)){
            alreadyAvailableNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }

        findViewById(R.id.imageRemoveWebURL).setOnClickListener(v -> {
            textWebURL.setText(null);
            layoutWebURL.setVisibility(View.GONE);
        });

        findViewById(R.id.imageRemoveReminder).setOnClickListener(v -> {
            timeText.setText(null);
            reminderBackground.setVisibility(View.GONE);
            reminderTime = "";
            firebaseFirestore.collection("notes")
                    .document("my_notes")
                    .update("reminder", "")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG", "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("TAG", "Error writing document", e);
                        }
                    });
        });

        findViewById(R.id.imageRemoveImage).setOnClickListener(v -> {
            imageNote.setImageBitmap(null);
            imageNote.setVisibility(View.GONE);
            findViewById(R.id.imageRemoveImage).setVisibility(View.GONE);
            selectedImagePath = "";
        });

        initMiscellaneous();
        setSubtitleIndicatorColor();

        //Retrieve and apply the saved text alignment preference
        applyTextAlignmentPreference();
    }


    private void nextSuggestion() {
        new GuideView.Builder(this)
                .setTitle("Add Reminder")
                .setContentText("Click to set up reminder notification")
                .setGravity(smartdevelop.ir.eram.showcaseviewlib.config.Gravity.auto)
                .setDismissType(DismissType.anywhere)
                .setTargetView(addReminder)
                .setTitleTextSize(12)
                .setTitleTextSize(16)
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        layoutMiscellaneousSuggestion();
                    }
                }).build().show();
    }

    private void layoutMiscellaneousSuggestion() {
        new GuideView.Builder(this)
                .setTitle("Layout Miscellaneous")
                .setContentText("Miscellaneous")
                .setGravity(smartdevelop.ir.eram.showcaseviewlib.config.Gravity.auto)
                .setDismissType(DismissType.anywhere)
                .setTargetView(layoutMiscellaneous)
                .setTitleTextSize(12)
                .setTitleTextSize(16)
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        // Mark tutorial as shown
                        sharedPreferences.edit().putBoolean("tutorial_shown", true).apply();
                    }
                }).build().show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel Name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private boolean loadSwitchState() {
        SharedPreferences sharedPreferences = getSharedPreferences("EmailNotesPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("emailNotes_switch", false);
    }

    // Method to perform translation and update the UI
    private void translateText(String originalText, OnTranslationCompleteListener listener) {
        // Assuming selectedLanguageCode contains the target language code
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH) // Adjust source language as needed
                .setTargetLanguage(selectedLanguageCode)
                .build();
        translator = Translation.getClient(options);

        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi() // Adjust download conditions as needed
                .build();

        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(unused -> translator.translate(originalText)
                        .addOnSuccessListener(translatedText -> {
                            // Pass the translated text to the callback
                            listener.onTranslationComplete(translatedText);
                        })
                        .addOnFailureListener(exception -> {
                            // Handle failure
                        }))
                .addOnFailureListener(exception -> {
                    // Handle failure
                });
    }
    // Callback interface for when translation is complete
    interface OnTranslationCompleteListener {
        void onTranslationComplete(String translatedText);
    }

    // Initialize translator and start translation.
    private void initTranslatorAndTranslate(String targetLanguageCode) {
        progressDialog.show();
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(targetLanguageCode)
                .build();
        translator = com.google.mlkit.nl.translate.Translation.getClient(options);

        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(unused -> translateEditTexts())
                .addOnFailureListener(exception -> progressDialog.dismiss());
    }


    // Translate hints for EditTexts.
    private OnSuccessListener<? super Void> translateEditTexts() {

        for (int i = 0; i < originalEditTexts.length; i++) {
            final int index = i;
            translator.translate(originalEditTexts[index])
                    .addOnSuccessListener(translatedText -> {
//                        // Update corresponding EditText hint here
                        EditText editText = findEditTextByIndex(index); // Implement this method based on your layout
                        if (editText != null) {
                            editText.setHint(translatedText);
                        }
                        saveTranslation("translation_" + index, translatedText);

                        if (index == originalEditTexts.length - 1) {
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(exception -> progressDialog.dismiss());
        }
        return null;
    }

    // Save translations to SharedPreferences.
    private void saveTranslation(String key, String translation) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, translation);
        editor.apply();
    }

    // Find EditText by index for setting translated hint.
    private EditText findEditTextByIndex(int index) {
        if (index == 0) {
            return inputNoteTitle;
            // Add more cases if you have more EditTexts
        } else if (index == 1) {
            return inputNoteSubtitle;
        } else if (index == 2) {
            return inputNoteText;
        }
        return null; // Index not found
    }


    @Override
    protected void onResume() {
        super.onResume();
        applySelectedFont();
    }
    private void applySelectedFont() {
        applyFontStyleToEditText(inputNoteTitle, SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToEditText(inputNoteSubtitle, SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToEditText(inputNoteText, SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToView(textDateTime,SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToView(textWebURL,SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToView(thinking_textview,SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToView(wordCountTextView, SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToView(layoutMiscellaneous.findViewById(R.id.textMiscellaneous),SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToView(layoutMiscellaneous.findViewById(R.id.pick_color),SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToView(layoutMiscellaneous.findViewById(R.id.add_image),SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToView(layoutMiscellaneous.findViewById(R.id.add_url),SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToView(layoutMiscellaneous.findViewById(R.id.add_audio),SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToView(layoutMiscellaneous.findViewById(R.id.share),SharedPreferencesHelper.getFontStyle(this));
    }

    private void applyFontStyleToEditText(EditText editText, String fontStyle) {
        switch (fontStyle) {
            case "sans":
                editText.setTypeface(Typeface.SANS_SERIF);
                break;
            case "serif":
                editText.setTypeface(Typeface.SERIF);
                break;
            case "monospace":
                editText.setTypeface(Typeface.MONOSPACE);
                break;
            default:
                editText.setTypeface(Typeface.DEFAULT);
                break;
        }
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


    private void updateScrollViewBackground(boolean isBlackTheme) {
        int drawableId = isBlackTheme ? R.drawable.doc_background : R.drawable.doc_background_light;
        int miscellaneousBackground = isBlackTheme ? R.drawable.background_miscellaneous : R.drawable.background_miscellaneous_light;
        layoutMiscellaneous.setBackground(ContextCompat.getDrawable(this, miscellaneousBackground));
        scrollView.setBackground(ContextCompat.getDrawable(this, drawableId));
    }

    private void showNumberedBullets() {
        StringBuilder numberedBulletPoints = new StringBuilder();

        for (int i = 1; i <= 7; i++) {
            numberedBulletPoints.append("\t" + "\t").append(i).append(". ").append("write... ").append("\n");
        }

        inputNoteText.setText(numberedBulletPoints.toString());
    }


    //Toggle tint icons
    private void toggleTint(ImageView imageView) {
        int defaultColor =ContextCompat.getColor(this, R.color.colorIcons);
        int mainColor = ContextCompat.getColor(this, R.color.main_color);

        if(!isTintChanged){
            ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(mainColor));
        }else{
            ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(defaultColor));
        }

        //Toggle the flag
        isTintChanged = !isTintChanged;

    }

    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                CreateNoteActivity.this,
                (view, hourOfDay, minute1) -> {
                    String title = inputNoteTitle.getText().toString();
                    String subTitle = inputNoteSubtitle.getText().toString();
                    // Use selected hour and minute
                    scheduleNotification(title,subTitle, hourOfDay, minute1);
                },
                hour,
                minute,
                false);

        timePickerDialog.setOnCancelListener(dialog -> Toast.makeText(CreateNoteActivity.this, "Time selection canceled", Toast.LENGTH_SHORT).show());

        timePickerDialog.show();
    }

    private void scheduleNotification(String title,String subtitle, int hour, int minute) {
        futureTimeInMillis = calculateFutureTimeInMillis(hour, minute);
        DataHolder.getInstance().setData(futureTimeInMillis);

        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra("notification_title", title);
        notificationIntent.putExtra("notification_text", subtitle);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, futureTimeInMillis, pendingIntent);

            Log.d("Notification", "Scheduled at: " + futureTimeInMillis);

            // Convert 24-hour time format to 12-hour time format with AM/PM
            String amPm = (hour < 12) ? "AM" : "PM";

            reminderBackground.setVisibility(View.VISIBLE);
            reminderTime = hour + ":" + String.format("%02d", minute) + " " + amPm;
            timeText.setText(reminderTime);


            Toast.makeText(this, "Notification scheduled at " + reminderTime, Toast.LENGTH_SHORT).show();
        }

        Log.d("CreatenoteActivity","The reminder time is " + reminderTime.toString());

    }

    private long calculateFutureTimeInMillis(int hour, int minute) {
        Calendar futureTime = Calendar.getInstance();
        futureTime.set(Calendar.HOUR_OF_DAY, hour);
        futureTime.set(Calendar.MINUTE, minute);
        futureTime.set(Calendar.SECOND, 0);

        // Use current time in millis for comparison
        long currentTimeInMillis = System.currentTimeMillis();
        long futureTimeInMillis = futureTime.getTimeInMillis();

        if (futureTimeInMillis <= currentTimeInMillis) {
            futureTime.add(Calendar.DAY_OF_MONTH, 1);
            futureTimeInMillis = futureTime.getTimeInMillis();
        }

        return futureTimeInMillis;
    }


    private void showBottomMenu(View v) {
        // Create a BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        // Inflate the layout for the bottom sheet
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_menu, null);

        // Set up the views and click listeners inside the bottom sheet if needed
        TextView speech_to_text = bottomSheetView.findViewById(R.id.speech_to_text);
        speech_to_text.setOnClickListener(v1 -> {
            // Call method to prompt speech input when the Textview speech_to_text is clicked
            promptSpeechInput();
            speech_to_text.setTextColor(getResources().getColor(R.color.main_color));
        });
        TextView import_file = bottomSheetView.findViewById(R.id.import_file);
        import_file.setOnClickListener(v12 -> {
            // Create an intent to open a file picker
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/plain"); // Filter for plain text files

            startActivityForResult(intent, PICK_DOCUMENT_REQUEST);
            import_file.setTextColor(getResources().getColor(R.color.main_color));
        });
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        TextView image_to_text = bottomSheetView.findViewById(R.id.image_to_text);
        TextView generate_noteText = bottomSheetView.findViewById(R.id.text_generate);

        TextView summarize_text = bottomSheetView.findViewById(R.id.text_summarize);
        TextView label_text = bottomSheetView.findViewById(R.id.label_text);

        label_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataHolder.getInstance().setTitle(inputNoteTitle.getText().toString());
                DataHolder.getInstance().setSubtitle(inputNoteSubtitle.getText().toString());
                DataHolder.getInstance().setDateTime(textDateTime.getText().toString());
                startActivity(new Intent(CreateNoteActivity.this, LabelActivity.class));
            }
        });

        summarize_text.setOnClickListener(v13 -> {
                summarizeNote();
            summarize_text.setTextColor(getResources().getColor(R.color.main_color));
        });

        generate_noteText.setOnClickListener(v14 -> {
            if(inputNoteTitle.getText().toString().isEmpty()){
                Toast.makeText(CreateNoteActivity.this, "Note Title is Empty", Toast.LENGTH_SHORT).show();
            }else {
                generateNote();
                generate_noteText.setTextColor(getResources().getColor(R.color.main_color));
            }
        });

        image_to_text.setOnClickListener(v15 -> {
            ImagePicker.with(CreateNoteActivity.this)
                    .crop()	    			//Crop image(Optional), Check Customization for more option
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start(REQUEST_CODE_SELECT_IMAGE_FOR_OCR);
            image_to_text.setTextColor(getResources().getColor(R.color.main_color));
        });

        TextView export = bottomSheetView.findViewById(R.id.export_file);
        export.setOnClickListener(v16 -> {
            saveAsDocument();
            export.setTextColor(getResources().getColor(R.color.main_color));
        });

        // Set the view for the bottom sheet
        bottomSheetDialog.setContentView(bottomSheetView);

        // Show the bottom sheet
        bottomSheetDialog.show();

    }

    private void selectImage1() {
        // Handle image selection code here
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE_FOR_OCR);
        }

    }

    private void summarizeNote() {

        String text_to_summarize = inputNoteText.getText().toString();

            JSONObject jsonObject = new JSONObject();
            // ...

            try {
                jsonObject.put("model", "gpt-3.5-turbo");

                JSONArray jsonArrayMessage = new JSONArray();
                JSONObject jsonObjectMessage = new JSONObject();

                jsonObjectMessage.put("role", "user");
                jsonObjectMessage.put("content", "Summarize " + text_to_summarize);  // Move this line inside jsonObjectMessage

                jsonArrayMessage.put(jsonObjectMessage);

                jsonObject.put("messages", jsonArrayMessage);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

// ...


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    stringURLEndPoint,jsonObject, response -> {

                String stringText = null;
                try {
                    stringText = response.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                Log.d("CreateNoteActivity","The summarized text is " + stringText.toString());

                stringOutput1 = stringOutput1 + stringText;
                inputNoteText.setText("");
                inputNoteText.setText(stringText.toString());

            }, error -> {

            }){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> mapHeader = new HashMap<>();
                    mapHeader.put("Authorization", "Bearer " + API_KEY);
                    mapHeader.put("Content-Type", "application/json");


                    return mapHeader;
                }
                @Override
                protected VolleyError parseNetworkError(VolleyError volleyError){
                    return super.parseNetworkError(volleyError);
                }
            };

            int intTimeoutPeriod = 60000; //60 seconds timeout duration defined
            RetryPolicy retryPolicy = new DefaultRetryPolicy(intTimeoutPeriod, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(retryPolicy);


            Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);
        }

//Summarize note is not working.. either have to use another api or....??
    private void generateNote() {

        startThinkingAnimation();

            JSONObject jsonObject = new JSONObject();
            // ...

            try {
                jsonObject.put("model", "gpt-3.5-turbo");

                JSONArray jsonArrayMessage = new JSONArray();
                JSONObject jsonObjectMessage = new JSONObject();

                jsonObjectMessage.put("role", "user");
                jsonObjectMessage.put("content", "Write a short note about " + inputNoteTitle.getText().toString()); // Move this line inside jsonObjectMessage

                jsonArrayMessage.put(jsonObjectMessage);

                jsonObject.put("messages", jsonArrayMessage);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

// ...


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    stringURLEndPoint,jsonObject, response -> {

                String stringText = null;
                try {
                    stringText = response.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                stringOutput = stringOutput + stringText;
                inputNoteText.setText(stringOutput);
                stopThinkingAnimation();

            }, error -> {
                error.printStackTrace();
                stopThinkingAnimation();

            }){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> mapHeader = new HashMap<>();
                    mapHeader.put("Authorization", "Bearer " + API_KEY);
                    mapHeader.put("Content-Type", "application/json");


                    return mapHeader;
                }
                @Override
                protected VolleyError parseNetworkError(VolleyError volleyError){
                    return super.parseNetworkError(volleyError);
                }
            };

            int intTimeoutPeriod = 60000; //60 seconds timeout duration defined
            RetryPolicy retryPolicy = new DefaultRetryPolicy(intTimeoutPeriod, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(retryPolicy);


            Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);
    }
    private void startThinkingAnimation() {
//        thinking_textview.setVisibility(View.VISIBLE);
        int[] dotCount = {1};
        thinkingAnimationRunnable = new Runnable() {
            @Override
            public void run() {
                inputNoteText.setText("Generating" + new String(new char[dotCount[0]]).replace("\0", "."));
                dotCount[0] = (dotCount[0] % 3) + 1;
                handler1.postDelayed(this, 500);
            }
        };
        handler1.post(thinkingAnimationRunnable);
    }

    private void stopThinkingAnimation() {
        handler1.removeCallbacks(thinkingAnimationRunnable);
//        thinking_textview.setVisibility(View.GONE); // Hide the TextView or reset its text
    }

    // Method to prompt the user for speech input using RecognizerIntent
    private void promptSpeechInput() {
        // Create an Intent for speech recognition
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Specify the language model and language for recognition
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        // Provide a prompt for the user to speak
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something...");

        try {
            // Start the speech recognition activity with the specified intent
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            // Handle the case where speech recognition is not supported on the device
            Toast.makeText(this, "Speech recognition not supported on your device", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveAsDocument() {
        String content = inputNoteText.getText().toString();

        if (content.isEmpty()) {
            Toast.makeText(this, "Cannot save an empty document", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/plain"); //Default file type

            intent.putExtra(Intent.EXTRA_TITLE, "Document.txt"); //Default Document Name

            startActivityForResult(intent, 21);
        }
    }
    private void updateWordCount() {
        String text =inputNoteText.getText().toString().trim();
        int wordCount =text.isEmpty() ? 0 : text.split("\\s+").length;

        //Update the TextView with the Current word count
        wordCountTextView.setText(getString(R.string.word_count, wordCount));
    }


    private void showToast(final String s) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CreateNoteActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void applyHeader(int headerLevel) {
        int start = inputNoteText.getSelectionStart();
        int end = inputNoteText.getSelectionEnd();

        if (start != -1 && end != -1) {
            SpannableStringBuilder selectedText = new SpannableStringBuilder(
                    inputNoteText.getText().subSequence(start, end));

            switch (headerLevel) {
                case 1:
                    selectedText.setSpan(new StyleSpan(Typeface.BOLD), 0, selectedText.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    selectedText.setSpan(new AbsoluteSizeSpan(24, true), 0, selectedText.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case 2:
                    selectedText.setSpan(new StyleSpan(Typeface.BOLD), 0, selectedText.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    selectedText.setSpan(new AbsoluteSizeSpan(18, true), 0, selectedText.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                default:
                    // Remove bold and set default text size
                    selectedText.removeSpan(new StyleSpan(Typeface.BOLD));
                    selectedText.setSpan(new AbsoluteSizeSpan(14, true), 0, selectedText.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
            }

            inputNoteText.getText().replace(start, end, selectedText);
            saveFormattedText(inputNoteText.getText().toString());
        }

    }

    private void showColorPickerDialog(final OnColorSelectedListener onColorSelectedListener) {
        AmbilWarnaDialog colorPickerDialog = new AmbilWarnaDialog(
                CreateNoteActivity.this,
                textColor,  // Use the current text color as the initial color
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        onColorSelectedListener.onColorSelected(color);
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // Do nothing here
                    }
                }
        );
        colorPickerDialog.show();
    }
    private void saveColorPreference(String key, int color) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, color);
        editor.apply();
    }

    private int retrieveColorPreference(String key, int defaultColor) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getInt(key, defaultColor);
    }

    // ... (your existing code)

    // Define this interface in your class
    interface OnColorSelectedListener {
        void onColorSelected(int color);
    }

private void applyStyle(int style) {
    int start = inputNoteText.getSelectionStart();
    int end = inputNoteText.getSelectionEnd();

    if (start != -1 && end != -1) {
        SpannableStringBuilder selectedText = new SpannableStringBuilder(
                inputNoteText.getText().subSequence(start, end));

        selectedText.setSpan(new StyleSpan(style), 0, selectedText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        inputNoteText.getText().replace(start, end, selectedText);

        // Save the formatted text when bold is applied
        saveFormattedText(inputNoteText.getText().toString());
    }
}

    private void toggleBold() {
        int currentStyle = getCurrentTextStyle();
        int newStyle = (currentStyle == Typeface.BOLD) ? Typeface.NORMAL : Typeface.BOLD;

        applyStyle(newStyle);
    }

    private int getCurrentTextStyle() {
        int start = inputNoteText.getSelectionStart();
        int end = inputNoteText.getSelectionEnd();

        if (start != -1 && end != -1) {
            StyleSpan[] styleSpans = inputNoteText.getText().getSpans(start, end, StyleSpan.class);

            for (StyleSpan styleSpan : styleSpans) {
                if (styleSpan.getStyle() == Typeface.BOLD) {
                    return Typeface.BOLD;
                }
                // Add more conditions for other styles if needed
            }
        }

        return Typeface.NORMAL;
    }

    private void applyUnderline() {
        int start = inputNoteText.getSelectionStart();
        int end = inputNoteText.getSelectionEnd();
        if (start != -1 && end != -1) {
            boolean isUnderlined = isUnderlined(start, end);

            if (isUnderlined) {
                // Remove underline style
                UnderlineSpan[] underlineSpans = inputNoteText.getText().getSpans(start, end, UnderlineSpan.class);
                for (UnderlineSpan underlineSpan : underlineSpans) {
                    inputNoteText.getEditableText().removeSpan(underlineSpan);
                }
            } else {
                // Apply underline style
                inputNoteText.getEditableText().setSpan(new UnderlineSpan(),
                        inputNoteText.getSelectionStart(), inputNoteText.getSelectionEnd(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }
    private boolean isUnderlined(int start, int end) {
        UnderlineSpan[] underlineSpans = inputNoteText.getText().getSpans(start, end, UnderlineSpan.class);
        return underlineSpans.length > 0;
    }
    private void applyItalic() {
        int start = inputNoteText.getSelectionStart();
        int end = inputNoteText.getSelectionEnd();

        if (start != -1 && end != -1) {
            boolean isItalic = isItalic(start, end);

            if (isItalic) {
                // Remove italic style
                StyleSpan[] styleSpans = inputNoteText.getText().getSpans(start, end, StyleSpan.class);
                for(StyleSpan styleSpan : styleSpans){
                    inputNoteText.getEditableText().removeSpan(styleSpan);
                }
            } else {
                // Apply italic style
                inputNoteText.getEditableText().setSpan(new StyleSpan(Typeface.ITALIC),
                        start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private boolean isItalic(int start, int end) {
        StyleSpan[] styleSpans = inputNoteText.getText().getSpans(start, end, StyleSpan.class);

        for (StyleSpan styleSpan : styleSpans) {
            if (styleSpan.getStyle() == Typeface.ITALIC) {
                return true;
            }
        }

        return false;
    }


    private void setGravityAndSave(int gravity) {
        inputNoteText.setGravity(gravity);
        saveTextAlignmentPreference(gravity);
    }

    private void saveTextAlignmentPreference(int textAlignment) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREF_TEXT_ALIGNMENT, textAlignment);
        editor.apply();
    }

    private void applyTextAlignmentPreference() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int textAlignment = preferences.getInt(PREF_TEXT_ALIGNMENT, Gravity.LEFT);
        inputNoteText.setGravity(textAlignment);
    }

    private void applyBoldToSelection() {
        int start = inputNoteText.getSelectionStart();
        int end = inputNoteText.getSelectionEnd();

        if(start != end){
            SpannableStringBuilder selectedText = new SpannableStringBuilder(inputNoteText.getText().subSequence(start, end));

            selectedText.setSpan(new StyleSpan(Typeface.BOLD), 0, selectedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            inputNoteText.getText().replace(start, end, selectedText);
        }

    }

    private void setViewOrUpdateNote(){
            inputNoteTitle.setText(alreadyAvailableNote.getTitle());
            inputNoteSubtitle.setText(alreadyAvailableNote.getSubtitle());
            inputNoteText.setText(alreadyAvailableNote.getNoteText());
            textDateTime.setText(alreadyAvailableNote.getDateTime());
            if(alreadyAvailableNote.getImagePath() != null && !alreadyAvailableNote.getImagePath().trim().isEmpty()){
                imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath()));
                imageNote.setVisibility(View.VISIBLE);
                findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
                selectedImagePath = alreadyAvailableNote.getImagePath();
            }

            if(alreadyAvailableNote.getWebLink() != null && !alreadyAvailableNote.getWebLink().trim().isEmpty()){
                textWebURL.setText(alreadyAvailableNote.getWebLink());
                layoutWebURL.setVisibility(View.VISIBLE);
            }
            if(alreadyAvailableNote.getDocument_path() != null && !alreadyAvailableNote.getDocument_path().trim().isEmpty()){
                documentTextView.setText(DataHolder.getInstance().getDocumentContent());
            }
            if(alreadyAvailableNote.getAudioPath() != null && !alreadyAvailableNote.getAudioPath().trim().isEmpty()){
                audioPath = alreadyAvailableNote.getAudioPath();
            }
        ibPlay.setVisibility(View.VISIBLE);
        audio_background.setVisibility(View.VISIBLE);
        audioRecording.setVisibility(View.GONE);
//                                                lavPlaying.setVisibility(View.VISIBLE);
        lavFormat.setVisibility(View.VISIBLE);

            //Retrieve and apply the saved formatted text
            String formattedText = retrieveFormattedText();
//            inputNoteText.setText(formattedText); this is what is causing same text everywhere...work on it later
    }
    private void saveFormattedText(String formattedText) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_FORMATTED_TEXT, formattedText);
        editor.apply();
    }

    private String retrieveFormattedText() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getString(PREF_FORMATTED_TEXT, "");
    }

    private void applyFormattedTextPreference() {
        String formattedText = retrieveFormattedText();
        inputNoteText.setText(formattedText);
    }

    private String getRecordingFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File music = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        file = new File(music, "testfile" + ".mp3");
        audioPath = file.getPath();
        return file.getPath();
    }

    private void saveNote(){
        // Save the text alignment preference when the note is saved
        saveTextAlignmentPreference(inputNoteText.getGravity());

        // Save the formatted text preference when the note is saved
        saveFormattedText(inputNoteText.getText().toString());

        if(inputNoteTitle.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Note title can't be empty", Toast.LENGTH_SHORT).show();
            return;
        } else if (inputNoteSubtitle.getText().toString().trim().isEmpty() && inputNoteText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Note can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String audioFilePath = getRecordingFilePath();

        final Note note = new Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setSubtitle(inputNoteSubtitle.getText().toString());
        note.setNoteText(inputNoteText.getText().toString());
        note.setDateTime(textDateTime.getText().toString());
        note.setColor(selectedNoteColor);
        note.setImagePath(selectedImagePath);
        note.setReminder(reminderTime);
        note.setDocument_path(documentPath);
        note.setAudioPath(audioFilePath);
        note.setDocument_text(documentTextView.getText().toString());

        saveNoteToFirebase(note);

        if(layoutWebURL.getVisibility() == View.VISIBLE){
            note.setWebLink(textWebURL.getText().toString());
        }

        if(!documentTextView.getText().toString().isEmpty()){
            note.setDocument_path(document_path);
        }

        /**
         * Setting id of new note from an already available note. Since we have set onConflictStrategy to Replace
         * in NoteDao. That means if id of new note is already available in the database then it will no be replaced
         * with the new note and the note will get updated
         */
        if (alreadyAvailableNote != null){
            note.setId(alreadyAvailableNote.getId());
        }
    }


    private void saveNoteToFirebase(Note note) {
        DocumentReference documentReference;
        DocumentReference documentReferenceForReminderNotes;
        if(isEditMode){
            //update the note
            documentReference = Utility.getCollectionReferenceForNotes().document(noteId);

            documentReferenceForReminderNotes = Utility.getCollectionReferenceForReminders().document(noteId);
        }else {
            //create new note
            documentReference = Utility.getCollectionReferenceForNotes().document();

            documentReferenceForReminderNotes = Utility.getCollectionReferenceForReminders().document();
        }



        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //notes is added
                    Toast.makeText(CreateNoteActivity.this, "Notes Added Succesfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CreateNoteActivity.this, MainActivity.class));
                }else{
                    Toast.makeText(CreateNoteActivity.this, "Failed while adding the notes", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Check if a specific field in the note is not null
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Document exists, now you can check if the field is not null
                        if (document.contains("reminder")) {
                            Object fieldValue = document.get("reminder");
                            if (fieldValue != null) {
                                // Field is not null
                                // Do something here
                                // For example:
                                documentReferenceForReminderNotes.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            //notes is added
                                            Log.d("CreateNoteActivity","The reminder notes added successfully");
                                        }else{
                                            Log.d("CreateNoteActivity", "Failed in adding reminder notes");
                                        }
                                    }
                                });
                                Log.d("The Field", "Field 'fieldName' is not null");
                            } else {
                                // Field is null
                                // Do something else
                                // For example:
                                Log.d("The Fieldn", "Field 'fieldName' is null");
                            }
                        } else {
                            // Field does not exist in the document
                            Log.d("Existence of Field", "Field 'fieldName' does not exist in the document");
                        }
                    } else {
                        // Document does not exist
                        Log.d("Document exists?", "Document does not exist");
                    }
                } else {
                    // Failed to retrieve document
                    Log.d("Failed to Retrieve", "Failed to retrieve document: ", task.getException());
                }
            }
        });
    }


    private void initMiscellaneous() {
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);
        layoutMiscellaneous.findViewById(R.id.textMiscellaneous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else{
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    ImageViewCompat.setImageTintList(header1, getResources().getColorStateList(R.color.colorIcons));
                    ImageViewCompat.setImageTintList(header2, getResources().getColorStateList(R.color.colorIcons));
                    ImageViewCompat.setImageTintList(boldText, getResources().getColorStateList(R.color.colorIcons));
                    ImageViewCompat.setImageTintList(textColorPicker, getResources().getColorStateList(R.color.colorIcons));
                    ImageViewCompat.setImageTintList(numberingBullets, getResources().getColorStateList(R.color.colorIcons));
                    ImageViewCompat.setImageTintList(italicText, getResources().getColorStateList(R.color.colorIcons));
                    ImageViewCompat.setImageTintList(underlineText, getResources().getColorStateList(R.color.colorIcons));
                    ImageViewCompat.setImageTintList(leftAlign, getResources().getColorStateList(R.color.colorIcons));
                    ImageViewCompat.setImageTintList(centerAlign, getResources().getColorStateList(R.color.colorIcons));
                    ImageViewCompat.setImageTintList(rightAlign, getResources().getColorStateList(R.color.colorIcons));

                }
            }
        });
        final ImageView imageColor1 = layoutMiscellaneous.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = layoutMiscellaneous.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = layoutMiscellaneous.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = layoutMiscellaneous.findViewById(R.id.imageColor4);
        final ImageView imageColor5 = layoutMiscellaneous.findViewById(R.id.imageColor5);
        final ImageView imageColor6 = layoutMiscellaneous.findViewById(R.id.imageColor6);
        final ImageView imageColor7 = layoutMiscellaneous.findViewById(R.id.imageColor7);
        final ImageView imageColor8 = layoutMiscellaneous.findViewById(R.id.imageColor8);
        final ImageView imageColor9 = layoutMiscellaneous.findViewById(R.id.imageColor9);
        final ImageView imageColor10 = layoutMiscellaneous.findViewById(R.id.imageColor10);
//        final ImageView imageColorPicker = layoutMiscellaneous.findViewById(R.id.imageColorPicker);


        layoutMiscellaneous.findViewById(R.id.viewColor1).setOnClickListener(v -> {
            selectedNoteColor = "#333333";
            imageColor1.setImageResource(R.drawable.ic_done);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            imageColor6.setImageResource(0);
            imageColor7.setImageResource(0);
            imageColor8.setImageResource(0);
            imageColor9.setImageResource(0);
            imageColor10.setImageResource(0);
            setSubtitleIndicatorColor();
        });
        layoutMiscellaneous.findViewById(R.id.viewColor2).setOnClickListener(v -> {
            selectedNoteColor = "#008080";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(R.drawable.ic_done);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            imageColor6.setImageResource(0);
            imageColor7.setImageResource(0);
            imageColor8.setImageResource(0);
            imageColor9.setImageResource(0);
            imageColor10.setImageResource(0);
            setSubtitleIndicatorColor();
        });
        layoutMiscellaneous.findViewById(R.id.viewColor3).setOnClickListener(v -> {
            selectedNoteColor = "#FF4842";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(R.drawable.ic_done);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            imageColor6.setImageResource(0);
            imageColor7.setImageResource(0);
            imageColor8.setImageResource(0);
            imageColor9.setImageResource(0);
            imageColor10.setImageResource(0);
            setSubtitleIndicatorColor();
        });
        layoutMiscellaneous.findViewById(R.id.viewColor4).setOnClickListener(v -> {
            selectedNoteColor = "#3A52Fc";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(R.drawable.ic_done);
            imageColor5.setImageResource(0);
            imageColor6.setImageResource(0);
            imageColor7.setImageResource(0);
            imageColor8.setImageResource(0);
            imageColor9.setImageResource(0);
            imageColor10.setImageResource(0);
            setSubtitleIndicatorColor();
        });
        layoutMiscellaneous.findViewById(R.id.viewColor5).setOnClickListener(v -> {
            selectedNoteColor = "#000000";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(R.drawable.ic_done);
            imageColor6.setImageResource(0);
            imageColor7.setImageResource(0);
            imageColor8.setImageResource(0);
            imageColor9.setImageResource(0);
            imageColor10.setImageResource(0);
            setSubtitleIndicatorColor();
        });
        layoutMiscellaneous.findViewById(R.id.viewColor6).setOnClickListener(v -> {
            selectedNoteColor = "#0E7337";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            imageColor6.setImageResource(R.drawable.ic_done);
            imageColor7.setImageResource(0);
            imageColor8.setImageResource(0);
            imageColor9.setImageResource(0);
            imageColor10.setImageResource(0);
            setSubtitleIndicatorColor();

        });
        layoutMiscellaneous.findViewById(R.id.viewColor7).setOnClickListener(v -> {
            selectedNoteColor = "#FFA500";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            imageColor6.setImageResource(0);
            imageColor7.setImageResource(R.drawable.ic_done);
            imageColor8.setImageResource(0);
            imageColor9.setImageResource(0);
            imageColor10.setImageResource(0);
            setSubtitleIndicatorColor();

        });
        layoutMiscellaneous.findViewById(R.id.viewColor8).setOnClickListener(v -> {
            selectedNoteColor = "#A020F0";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            imageColor6.setImageResource(0);
            imageColor7.setImageResource(0);
            imageColor8.setImageResource(R.drawable.ic_done);
            imageColor9.setImageResource(0);
            imageColor10.setImageResource(0);
            setSubtitleIndicatorColor();

        });
        layoutMiscellaneous.findViewById(R.id.viewColor9).setOnClickListener(v -> {
            selectedNoteColor = "#FFD700";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            imageColor6.setImageResource(0);
            imageColor7.setImageResource(0);
            imageColor8.setImageResource(0);
            imageColor9.setImageResource(R.drawable.ic_done);
            imageColor10.setImageResource(0);
            setSubtitleIndicatorColor();

        });
        layoutMiscellaneous.findViewById(R.id.viewColor10).setOnClickListener(v -> {
            selectedNoteColor = "#6f432a";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            imageColor6.setImageResource(0);
            imageColor7.setImageResource(0);
            imageColor8.setImageResource(0);
            imageColor9.setImageResource(0);
            imageColor10.setImageResource(R.drawable.ic_done);
            setSubtitleIndicatorColor();

        });
        layoutMiscellaneous.findViewById(R.id.viewColorPicker).setOnClickListener(v -> {
            AmbilWarnaDialog colorPickerDialog = new AmbilWarnaDialog(CreateNoteActivity.this, Color.parseColor(selectedNoteColor), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onCancel(AmbilWarnaDialog dialog) {
                    Log.d("ColorPicker", "onCancel");
                }

                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    Log.d("ColorPicker", "onOk: " + color);
                    // Update the selectedNoteColor and set the indicator color
                    selectedNoteColor = String.format("#%06X", (0xFFFFFF & color));
                    setSubtitleIndicatorColor();

                    // Show the "Done" button
                    findViewById(R.id.imageColorPicker).setBackgroundResource(R.drawable.baseline_done_24);
                }
            });
            Log.d("ColorPicker","showColorPickerDialog: show dialog");
            colorPickerDialog.show();
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            imageColor6.setImageResource(0);
            imageColor7.setImageResource(0);
            imageColor8.setImageResource(0);
            imageColor9.setImageResource(0);
            imageColor10.setImageResource(0);
        });

        if (alreadyAvailableNote != null && alreadyAvailableNote.getColor() != null && !alreadyAvailableNote.getColor().trim().isEmpty()){
            switch (alreadyAvailableNote.getColor()){
                case "#008080":
                    layoutMiscellaneous.findViewById(R.id.viewColor2).performClick();
                    break;
                case "#FF4842":
                    layoutMiscellaneous.findViewById(R.id.viewColor3).performClick();
                    break;
                case "#3A52Fc":
                    layoutMiscellaneous.findViewById(R.id.viewColor4).performClick();
                    break;
                case "#000000":
                    layoutMiscellaneous.findViewById(R.id.viewColor5).performClick();
                    break;
                case "#0E7337":
                    layoutMiscellaneous.findViewById(R.id.viewColor6).performClick();
                    break;
                case "#FFA500":
                    layoutMiscellaneous.findViewById(R.id.viewColor7).performClick();
                    break;
                case "#A020F0":
                    layoutMiscellaneous.findViewById(R.id.viewColor8).performClick();
                    break;
                case "#FFD700":
                    layoutMiscellaneous.findViewById(R.id.viewColor9).performClick();
                    break;
                case "#6f432a":
                    layoutMiscellaneous.findViewById(R.id.viewColor10).performClick();
                    break;
            }
        }

        layoutMiscellaneous.findViewById(R.id.layoutAddImage).setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if (ContextCompat.checkSelfPermission(
                    CreateNoteActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        CreateNoteActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE_PERMISSION
                );
            }else{
                selectImage();
            }
        });
        layoutMiscellaneous.findViewById(R.id.layoutAddUrl).setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            showAddURLDialog();
        });
        layoutMiscellaneous.findViewById(R.id.layoutShareNote).setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            shareNoteDialog();
        });
        layoutMiscellaneous.findViewById(R.id.layoutAddAudio).setOnClickListener(v -> {
            //Close the miscellaneous layout once it is opened
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            // Create a BottomSheetDialog
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(CreateNoteActivity.this);

            // Inflate the layout for the bottom sheet
            View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_add_audio, null);

            ibRecord = bottomSheetView.findViewById(R.id.ib_record);
            cancel_audio = bottomSheetView.findViewById(R.id.cancel_audio);
            audioRecording = bottomSheetView.findViewById(R.id.recording_in_progress);

            tvTime = bottomSheetView.findViewById(R.id.tv_time);
            ibPlay = findViewById(R.id.ib_play);

            lavPlaying = findViewById(R.id.lav_playing);
            lavFormat = findViewById(R.id.lav_format);
            audioFormat = findViewById(R.id.lav_play);
            mediaPlayer = new MediaPlayer();

            audio_background = findViewById(R.id.audio_background);
            // Set the view for the bottom sheet
            bottomSheetDialog.setContentView(bottomSheetView);

            // Show the bottom sheet
            bottomSheetDialog.show();

            ibRecord.setOnClickListener(v1 -> {
                if(checkRecordingPermission()){
                    if(!isRecording){
                        isRecording = true;
                        executorService.execute(() -> {
                            mediaRecorder = new MediaRecorder();
                            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                            mediaRecorder.setOutputFile(getRecordingFilePath());
                            path = getRecordingFilePath();
                            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                            try {
                                mediaRecorder.prepare();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            mediaRecorder.start();
                            runOnUiThread(() -> {
                                audioRecording.setVisibility(View.VISIBLE);
                                lavFormat.setVisibility(View.VISIBLE);
                                playableSeconds = 0;
                                seconds = 0;
                                dummySeconds = 0;
                                ibRecord.setImageDrawable(ContextCompat.getDrawable(CreateNoteActivity.this, R.drawable.recording_active));
                                runTimer();
                            });
                        });
                    }else {
                        executorService.execute(() -> {
                            mediaRecorder.stop();
                            mediaRecorder.release();
                            mediaRecorder = null;
                            playableSeconds = seconds;
                            dummySeconds = seconds;
                            seconds = 0;
                            isRecording = false;


                            runOnUiThread(() -> {
                                ibPlay.setVisibility(View.VISIBLE);
                                audio_background.setVisibility(View.VISIBLE);
                                audioRecording.setVisibility(View.GONE);
//                                                lavPlaying.setVisibility(View.VISIBLE);
                                lavFormat.setVisibility(View.VISIBLE);
                                handler.removeCallbacksAndMessages(null);
                                ibRecord.setImageDrawable(ContextCompat.getDrawable(CreateNoteActivity.this, R.drawable.baseline_mic_24));
                            });
                        });
                    }
                }else{
                    requestRecordingPermission();
                }
            });
            ibPlay.setOnClickListener(v12 -> {
                if(!isPlaying){
                    if(path != null){
                        try {
                            mediaPlayer.setDataSource(getRecordingFilePath());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }else{
                        Toast.makeText(CreateNoteActivity.this, "No Recording Present", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    mediaPlayer.start();
                    isPlaying = true;
                    ibPlay.setImageDrawable(ContextCompat.getDrawable(CreateNoteActivity.this, R.drawable.recording_pause));
                    audioRecording.setVisibility(View.GONE);
                    lavPlaying.setVisibility(View.VISIBLE);
                    runTimer();
                }else {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    mediaPlayer = new MediaPlayer();
                    isPlaying = false;
                    seconds = 0;
                    handler.removeCallbacksAndMessages(null);
                    audioRecording.setVisibility(View.VISIBLE);
                    lavPlaying.setVisibility(View.GONE);
                    lavFormat.setVisibility(View.VISIBLE);
                    ibPlay.setImageDrawable(ContextCompat.getDrawable(CreateNoteActivity.this,R.drawable.recording_play));

                }
            });

        });

        if(isEditMode){
            layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setVisibility(View.VISIBLE);
            layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setOnClickListener(v -> {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showDeleteNoteDialog();
            });
        }
    }

    private boolean checkRecordingPermission() {

        if(ContextCompat.checkSelfPermission(CreateNoteActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED){
            requestRecordingPermission();
            return false;
        }
        return true;

    }

    private void requestRecordingPermission() {
        ActivityCompat.requestPermissions(CreateNoteActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    private void runTimer() {
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = (seconds % 3600) / 60;
                int secs = seconds%60;
                String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, secs);
                tvTime.setText(time);

                if(isRecording || (isPlaying && playableSeconds != -1)){
                    seconds++;
                    playableSeconds--;

                    if(playableSeconds==-1 && isPlaying){
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                        mediaPlayer = new MediaPlayer();
                        playableSeconds = dummySeconds;
                        seconds = 0;
                        handler.removeCallbacksAndMessages(null);
                        audioRecording.setVisibility(View.VISIBLE);
                        lavPlaying.setVisibility(View.GONE);
                        lavFormat.setVisibility(View.VISIBLE);
                        ibPlay.setImageDrawable(ContextCompat.getDrawable(CreateNoteActivity.this, R.drawable.recording_play));
                        return;
                    }
                }
                handler.postDelayed(this, 1000);
            }
        });
    }






    private void shareNoteDialog() {
        String textToShare = inputNoteText.getText().toString();

        if(textToShare.isEmpty()){
            Toast.makeText(this, "Note is Empty", Toast.LENGTH_SHORT).show();
        }else{
            // Create an Intent with the ACTION_SEND action
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);

            // Set the MIME type to text/plain
            sendIntent.setType("text/plain");

            // Add the text to the Intent
            sendIntent.putExtra(Intent.EXTRA_TEXT, textToShare);

            // Start the activity with the Intent
            startActivity(Intent.createChooser(sendIntent, "Share text via"));
        }
    }

    private void showDeleteNoteDialog(){
        //Checks if the dialog instance is null
        if(dialogDeleteNote == null){
            //Creates a new AlertDialog.Builder
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);

            //Inflate the custom layout for the dialog
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,
                    (ViewGroup) findViewById(R.id.layoutDeleteNoteContainer)
            );

            //Sets the inflated view as the content of the dialog
            builder.setView(view);

            //Creates the AlertDialog
            dialogDeleteNote = builder.create();

            //Sets the background of the dialog to be transparent
            dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            view.findViewById(R.id.textDeleteNote).setOnClickListener(v -> deleteNoteFromFirebase());
            view.findViewById(R.id.textCancel).setOnClickListener(v -> dialogDeleteNote.dismiss());
        }
        dialogDeleteNote.show();
    }

    private void deleteNoteFromFirebase() {
        DocumentReference documentReference;
        DocumentReference documentReferenceForReminderNotes;
            //delete the note
            documentReference = Utility.getCollectionReferenceForNotes().document(noteId);

        documentReferenceForReminderNotes = Utility.getCollectionReferenceForReminders().document(noteId);
        documentReferenceForReminderNotes.delete().addOnCompleteListener(task -> {
            if(task.isSuccessful()){

            }else{

            }
        });

        documentReference.delete().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                //notes is deleted
                Toast.makeText(CreateNoteActivity.this, "Notes deleted", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreateNoteActivity.this, MainActivity.class));
            }else{
                Toast.makeText(CreateNoteActivity.this, "Failed while deleting the notes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSubtitleIndicatorColor(){
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
        DataHolder.getInstance().setSubtitleIndicatorColor(selectedNoteColor);
    }

    private void selectImage(){
        // Handle image selection code here
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }else{
                Toast.makeText(this, "Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    //Handle the result of image selection
    if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
        if (data != null) {
            handleImageSelectionResult(data);
        }
        //Handle the result of document creation
    } else if ((requestCode == 2 || requestCode == 21) && resultCode == RESULT_OK) {
        if (data != null && data.getData() != null) {
            Uri uri = data.getData();
            handleDocumentCreationResult(uri);
        }
        //Handle the result of speech recognition
    } else if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK) {
        if (data != null) {
            handleSpeechRecognitionResult(data);
        }
    }
    //Handle the result of image recognition
    if(requestCode==REQUEST_CODE_SELECT_IMAGE_FOR_OCR && resultCode == RESULT_OK){ //MY NAME IS BOWEI
        if(data != null){
            Toast.makeText(this, "image has been selected", Toast.LENGTH_SHORT).show();

            recognizeText(data);
        }else{
            Toast.makeText(this, "image not selected", Toast.LENGTH_SHORT).show();
        }
    }
    if (requestCode == PICK_DOCUMENT_REQUEST && resultCode == Activity.RESULT_OK) {
        if (data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                // Read the content of the selected document
                String documentContent = readDocument(uri);
                documentPath = uri.getPath();
                documentTextView.setText(documentContent);
                DataHolder.getInstance().setDocumentContent(documentContent);

                Log.d("CreateNoteActivity","The document text is " + documentTextView.getText().toString());


                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setShape(GradientDrawable.RECTANGLE);
                gradientDrawable.setColor(Color.parseColor("#FFFFFF"));
                gradientDrawable.setStroke(2, ContextCompat.getColor(CreateNoteActivity.this, R.color.white));

                documentTextView.setBackground(gradientDrawable);
                documentTextView.setTextColor(Color.parseColor("#000000"));
                Toast.makeText(this, "Document Selected", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

    private String readDocument(Uri uri){
        StringBuilder content = new StringBuilder();
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    //Method to perform text recognition on the selected image
    private void recognizeText(@NonNull Intent data) {
        Uri selectedImageUri1 = data.getData();
        if(selectedImageUri1!=null){
            try{
                InputImage inputImage = InputImage.fromFilePath(CreateNoteActivity.this, selectedImageUri1);

                textRecognizer.process(inputImage)
                        .addOnSuccessListener(text -> {
                            //Extract recognized text and set it to the inputNoteText EditText view
                            String recognizeText = text.getText();
                            if(inputNoteText.getText().toString().isEmpty()){
                                inputNoteText.setText(recognizeText);
                            }else{
                                inputNoteText.setText(inputNoteText.getText().toString() + " " + recognizeText);
                            }
                        }).addOnFailureListener(e -> Toast.makeText(CreateNoteActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
            }catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void handleImageSelectionResult(@NonNull Intent data) {
        // Handle the result of image selection
        Uri selectedImageUri = data.getData();
        if (selectedImageUri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageNote.setImageBitmap(bitmap);
                imageNote.setVisibility(View.VISIBLE);
                findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);

                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();

                selectedImagePath = getPathFromUri(selectedImageUri);
            } catch (Exception exception) {
                Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleDocumentCreationResult(@NonNull Uri uri) {
        // Handle the result of document creation
        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            if (outputStream != null) {
                String content = inputNoteText.getText().toString();
                outputStream.write(content.getBytes());
                outputStream.close();
                Toast.makeText(this, "Document saved successfully", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving document", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSpeechRecognitionResult(@NonNull Intent data) {
        // Handle the result of speech recognition
        ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        if (result != null && result.size() > 0) {
            String text = result.get(0);
            inputNoteText.setText(text);
        }
    }

    private String getPathFromUri(Uri contentUri){
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri, null, null, null,null);
        if(cursor == null){
            filePath = contentUri.getPath();
        }else{
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }
    private void showAddURLDialog(){
        if(dialogAddURL == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_add_url,
                    (ViewGroup) findViewById(R.id.layoutAddUrlContainer)
            );
            builder.setView(view);

            dialogAddURL = builder.create();
            if(dialogAddURL.getWindow() != null){
                dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            final EditText inputURL = view.findViewById(R.id.inputURL);
            inputURL.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(v -> {
                if(inputURL.getText().toString().trim().isEmpty()){
                    Toast.makeText(CreateNoteActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                }else if(!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()){
                    Toast.makeText(CreateNoteActivity.this,"Enter valid URL", Toast.LENGTH_SHORT).show();
                }else{
                    textWebURL.setText(inputURL.getText().toString());
                    layoutWebURL.setVisibility(View.VISIBLE);
                    dialogAddURL.dismiss();
                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(v -> dialogAddURL.dismiss());
        }
        dialogAddURL.show();
    }
}
