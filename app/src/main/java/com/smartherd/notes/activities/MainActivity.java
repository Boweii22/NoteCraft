package com.smartherd.notes.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.smartherd.notes.R;
import com.smartherd.notes.SharedPreferencesHelper;
import com.smartherd.notes.adapters.NotesAdapter;
import com.smartherd.notes.entities.Note;
import com.smartherd.notes.listeners.NotesListener;

import java.util.List;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

// Extends AppCompatActivity for compatibility and implements NotesListener for note click interactions.
public class MainActivity extends AppCompatActivity implements NotesListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Request codes for different activities for result handling.
    public static final int REQUEST_CODE_ADD_NOTE = 1; // The request code is used to add a new note
    public static final int REQUEST_CODE_UPDATE_NOTE = 2; // The request code is used to update note

    // UI components and data handling variables.
    private RecyclerView notesRecyclerView;
    LinearLayout layoutSearch;
    ImageView imageAddNoteMain;
    LinearLayout layoutQuickActions;
    private NotesAdapter notesAdapter;

    private int noteClickedPosition = -1;
    DrawerLayout drawerLayout;
    MaterialToolbar materialToolbar;
    NavigationView navigationView;

    TextView textMyNotes;

    // Translation and SharedPreferences components.
    private ProgressDialog progressDialog;
    private Translator translator;
    private SharedPreferences sharedPreferences;
    private SharedPreferences shared_preferences;

    // Variables for text translation.
    private String[] originalTexts = { "My Notes" };
    private String[] originalEditTexts = { "Search notes" };
    private int[] textViewIds = { R.id.textMyNotes };
    private String selectedLanguageCode;
    private int[] editTextViewIds = { R.id.inputSearchNotes };

    private EditText inputSearchNotes;

    FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Theme selection based on SharedPreferences.
        boolean isBlackTheme = SharedPreferencesHelper.loadThemeChoice(this);
        if (isBlackTheme) {
            setTheme(R.style.Theme_App_Black);
        } else {
            setTheme(R.style.Theme_App_White);
        }

        DataHolder.getInstance().setBlackTheme(isBlackTheme);
        setContentView(R.layout.activity_main);

        // Fullscreen flag and UI component initializations.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Initialize firebase authentication
        mAuth = FirebaseAuth.getInstance();

        // Get the current user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // ... Initialization of UI components like buttons, layouts, etc.
        imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
        layoutSearch = findViewById(R.id.layoutSearch);
        layoutQuickActions = findViewById(R.id.layoutQuickActions);
        drawerLayout = findViewById(R.id.drawerLayout);
        materialToolbar = findViewById(R.id.materialToolbar);
        navigationView = findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0); // 0 index for the first header view
        TextView emailText = headerView.findViewById(R.id.email_text);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Translating...");
        progressDialog.setCancelable(false);
        inputSearchNotes = findViewById(R.id.inputSearchNotes);

        sharedPreferences = getSharedPreferences("Translations_SecondActivity", MODE_PRIVATE);

        shared_preferences = getSharedPreferences("tutorial", Context.MODE_PRIVATE);

        // Check if tutorial has been shown before
        if (!shared_preferences.getBoolean("tutorial_shown", false)) {
//            showTutorial();
        }
        showTutorial();

        // Read the selected language code from SharedPreferences and log it
        SharedPreferences sharedPreferencess = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        selectedLanguageCode = sharedPreferencess.getString("SelectedLanguageCode", SettingsActivity.selectedLanguage); // "DefaultCode"
                                                                                                                        // as
                                                                                                                        // a
                                                                                                                        // fallback
        Log.d("MainActivity", "Selected Language Code: " + selectedLanguageCode);

        loadSavedTranslations();

        // Directly start translation as we already know the target language
        initTranslatorAndTranslate(selectedLanguageCode);

        // Setup for navigation drawer toggle.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainActivity.this, drawerLayout, materialToolbar, R.string.drawer_close, R.string.drawer_open);
        drawerLayout.addDrawerListener(toggle);

        if (currentUser != null) {
            String user_email = currentUser.getEmail();
            emailText.setText(user_email);
        }

        String text2 = getTextFromSharedPreferences();
        Log.d("MainActivity", "The text is =" + text2);

        textMyNotes = findViewById(R.id.textMyNotes);

        // Navigation drawer item selection handling.
        // Handling navigation item selection.
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.notes) {
                Toast.makeText(MainActivity.this, "Notes", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (item.getItemId() == R.id.reminder) {
                Toast.makeText(MainActivity.this, "Reminder", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ReminderActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (item.getItemId() == R.id.toDo) {
                Toast.makeText(MainActivity.this, "Todo", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MainTodo.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.label) {
                Toast.makeText(MainActivity.this, "Add   Label", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Labels.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (item.getItemId() == R.id.settings) {
                Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (item.getItemId() == R.id.help) {
                Toast.makeText(MainActivity.this, "Help&Feedback", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, HelpFeedbackActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (item.getItemId() == R.id.SignOut) {
                // Build the alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
            return false;
        });
        updateLayoutBackground(isBlackTheme); // Call this method to set the LinearLayout background based on the theme

        // Add note button setup.
        imageAddNoteMain.setOnClickListener(v -> startActivityForResult(
                new Intent(getApplicationContext(), CreateNoteActivity.class),
                REQUEST_CODE_ADD_NOTE));
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        setupRecyclerView();

        // Query query =
        // Utility.getCollectionReferenceForNotes().orderBy("dateTime",Query.Direction.DESCENDING);
        // notesRecyclerView.setLayoutManager(
        // new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        // );
        // noteList = new ArrayList<>();
        // notesAdapter = new NotesAdapter(noteList, this);
        // notesRecyclerView.setAdapter(notesAdapter);
        /*
         * 
         * Request code is REQUEST_CODE_DISPLAY_NOTES, which means that displaying all
         * notes from the database and therefore as a
         * parameter isNoteDeleted we are passing false
         */
        // Fetch and display notes.
        // getNotes(REQUEST_CODE_DISPLAY_NOTES, false);
        /*
         * The getNotes function is called from onCreate method of an activity. it means
         * the application is just started and we need to
         * display all notes from the database and that's why we are passing
         * REQUEST_CODE_DISPLAY_NOTES to that method
         */

        // Search notes functionality setup.
        EditText inputSearch = findViewById(R.id.inputSearchNotes);

//        inputSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                // called when we press search button
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                return true;
//            }
//        });
//        inputSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
////                filterFirestoreData(query);
//                filterData(query);
//                Log.d("TAG", "The query is " + query);
//                return false;
//            }
//            @Override
//            public boolean onQueryTextChange(String newText) {
////                filterFirestoreData(newText);
////                filterData(newText);
////                Log.d("MainActivity","The search query is " + newText.toString());
////                Log.d("TAG","The notes collection " + notesAdapter.getOriginalNotesList());
////                Log.d("TAG", "The filtered notes collection " + notesAdapter.getFilteredNotesList());
//                return false;
//            }
//        });
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                filterData(s.toString());
                Log.d("MainActivity","The query search text is " + s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // Implementation of text change listener for search functionality.
        // inputSearch.addTextChangedListener(new TextWatcher() {
        // @Override
        // public void beforeTextChanged(CharSequence s, int start, int count, int
        // after) {
        //
        // }
        //
        // @Override
        // public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Log.d("MainActivity", "Search query: " + s.toString());
        //// // Filter the note list based on the search query
        //// notesAdapter.filterNotes(s.toString());
        // }
        //
        // @Override
        // public void afterTextChanged(Editable s) {
        // // Filter notes based on the search query
        // notesAdapter.getFilter().filter(s.toString());
        // }
        // });
    }

    private void showTutorial() {
        new GuideView.Builder(this)
                .setTitle("Navigation")
                .setContentText("Click to open drawer")
                .setGravity(Gravity.auto)
                .setDismissType(DismissType.anywhere)
                .setTargetView(materialToolbar)
                .setTitleTextSize(12)
                .setTitleTextSize(16)
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        nextSuggestion();
                    }
                }).build().show();
    }

    private void nextSuggestion() {
        new GuideView.Builder(this)
                .setTitle("Button")
                .setContentText("Click button to create note")
                .setGravity(Gravity.auto)
                .setDismissType(DismissType.anywhere)
                .setTargetView(imageAddNoteMain)
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

    private void signOut() {
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private String getTextFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE);
        // The second parameter is the default value if the key doesn't exist
        return sharedPreferences.getString("keyForText", "Default Value");
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
                .addOnSuccessListener(unused -> translateTexts())
                .addOnFailureListener(exception -> {
                    progressDialog.dismiss();
                    // Handle model download failure
                });
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
                        // // Update corresponding EditText hint here
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

    // Translate texts for TextViews.
    private void translateTexts() {
        for (int i = 0; i < originalTexts.length; i++) {
            final int index = i;
            translator.translate(originalTexts[index])
                    .addOnSuccessListener(translatedText -> {
                        TextView textView = findViewById(textViewIds[index]);
                        textView.setText(translatedText);
                        // // Update corresponding EditText hint here
                        EditText editText = findEditTextByIndex(index); // Implement this method based on your layout
                        if (editText != null) {
                            editText.setHint(translatedText);
                        }
                        saveTranslation("translation_" + index, translatedText);

                        if (index == originalTexts.length - 1) {
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(exception -> progressDialog.dismiss());
        }
    }

    // Find EditText by index for setting translated hint.
    private EditText findEditTextByIndex(int index) {
        if (index == 0) {
            return inputSearchNotes;
            // Add more cases if you have more EditTexts
        }
        return null; // Index not found
    }

    // Save translations to SharedPreferences.
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

    // Load saved translations from SharedPreferences.
    private void loadSavedTranslations() {
        for (int i = 0; i < textViewIds.length; i++) {
            String translation = sharedPreferences.getString("translation_" + i, null);
            if (translation != null) {
                TextView textView = findViewById(textViewIds[i]);
                textView.setText(translation);
            }
        }
    }

    private void applySelectedFont() {
        applyFontStyleToView(textMyNotes, SharedPreferencesHelper.getFontStyle(this));
        // applyFontStyleToEditText(inputSearchNotes,
        // SharedPreferencesHelper.getFontStyle(this));
    }

    // private void applyFontStyleToEditText(SearchView editText, String fontStyle)
    // {
    // switch (fontStyle) {
    // case "sans":
    // editText.tup(Typeface.SANS_SERIF);
    // break;
    // case "serif":
    // editText.setTypeface(Typeface.SERIF);
    // break;
    // case "monospace":
    // editText.setTypeface(Typeface.MONOSPACE);
    // break;
    // default:
    // editText.setTypeface(Typeface.DEFAULT);
    // break;
    // }
    // }

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

    // This is to change the search background based on the theme
    private void updateLayoutBackground(boolean isBlackTheme) {
        int drawableId = isBlackTheme ? R.drawable.background_search : R.drawable.light_background_search;
        int addBackground = isBlackTheme ? R.drawable.background_add_button : R.drawable.background_add_button_light;
        int imageResId = isBlackTheme ? R.drawable.ic_add : R.drawable.ic_add_white;
        int backgroundColor = isBlackTheme ? ContextCompat.getColor(this, R.color.colorQuickActionsBackground)
                : ContextCompat.getColor(this, R.color.colorQuickActionsBackgroundLight);
        layoutSearch.setBackground(ContextCompat.getDrawable(this, drawableId));
        imageAddNoteMain.setImageResource(imageResId);
        layoutQuickActions.setBackgroundColor(backgroundColor);
        imageAddNoteMain.setBackground(ContextCompat.getDrawable(this, addBackground));

    }

    // Handle note click for view or update.
    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", note);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);
    }

    // Handle result from other activities for note addition or update.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // getNotes(REQUEST_CODE_ADD_NOTE, false);
        /*
         * The getNotes function is called from the onActivityResult() method of
         * activity and we checked
         * the current request code is for add note and the result is RESULT_OK. it
         * means a new note is added
         * from CreateNoteActivity and its result is sent back to this activity that's
         * why we are passing
         * REQUEST_CODE_ADD_NOTE to that method
         */
        // getNotes(REQUEST_CODE_UPDATE_NOTE, data.getBooleanExtra("isNoteDeleted",
        // false));
        /*
         * Here Request code is REQUEST_CODE_UPDATE_NOTE, it means we are updating
         * already available note from the database
         * and it may be possible that the note gets deleted therefore as a parameter
         * isNoteDeleted, we are passing value from
         * CreateNoteActivity, whether the note is deleted or not using intent data with
         * key "isNoteDeleted"
         */
    }

    private void setupRecyclerView() {
        // Setup the query for the Firestore data
        Log.d("MainActivity", "To check if this is executed");
        Query query = Utility.getCollectionReferenceForNotes().orderBy("dateTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class).build();

        notesAdapter = new NotesAdapter(options, this, this);
        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        notesRecyclerView.setAdapter(notesAdapter);
        Log.d("TAG","The original notes " + notesAdapter.getOriginalNotesList());
        Log.d("TAG","The notes are " + options.getSnapshots());
    }

    private void filterFirestoreData(String query) {
        Log.d("TAG", "The filter query " + query);
//        Query filteredQuery = Utility.getCollectionReferenceForNotes()
//                .whereEqualTo("title", query);

//        Log.d("TAG","Filtered query " + filteredQuery);
// Example filtering based on a field
//        FirestoreRecyclerOptions<Note> filteredOptions = new FirestoreRecyclerOptions.Builder<Note>()
//                .setQuery(filteredQuery, Note.class)
//                .build();
//        notesAdapter.updateOptions(filteredOptions);
        Query query1 = Utility.getCollectionReferenceForNotes().orderBy("dateTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query1, Note.class).build();

//        try {
//            notesAdapter.updateOptions(filteredOptions);
//        } catch (Exception e) {
//            Log.e("AdapterUpdateError", "Error updating options: " + e.getMessage());
//            e.printStackTrace();
//        }
        Log.d("TAG", "The options " + options);
    }

    private void filterData(String queryText){
        Query query = Utility.getCollectionReferenceForNotes().orderBy("dateTime", Query.Direction.DESCENDING).whereEqualTo("title", queryText);
        FirestoreRecyclerOptions<Note> newOptions = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .setLifecycleOwner(this)
                .build();
        notesAdapter.updateOptions(newOptions);
    }

    @Override
    protected void onStart() {
        super.onStart();
        notesAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        notesAdapter.stopListening();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        notesAdapter.notifyDataSetChanged();
        applySelectedFont();
        // Replicate the SharedPreferences reading and logging logic here
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String selectedLanguageCode = sharedPreferences.getString("SelectedLanguageCode",
                SettingsActivity.selectedLanguage);
        Log.d("MainActivity", "Selected Language Code: " + selectedLanguageCode);
    }
}
/*
 * The MainActivity effectively integrates various components
 * like a navigation drawer, database operations for notes,
 * dynamic theme switching, and text translation. It demonstrates
 * good practices such as separating concerns (translation,
 * database access, UI updates) and using AsyncTask for background operations.
 */