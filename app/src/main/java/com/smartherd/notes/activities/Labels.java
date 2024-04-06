package com.smartherd.notes.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.smartherd.notes.Entitites.Label;
import com.smartherd.notes.R;
import com.smartherd.notes.SharedPreferencesHelper;
import com.smartherd.notes.adapters.CustomAdapter;
import com.smartherd.notes.adapters.LabelAdapter;
import com.smartherd.notes.entities.NoteTag;
import com.smartherd.notes.entities.TagWithDateTime;
import com.smartherd.notes.listeners.LabelListener;

import java.util.ArrayList;
import java.util.List;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class Labels extends AppCompatActivity implements CustomAdapter.ItemClickListener, CustomAdapter.LongClickListener {

    ImageView backToNote;
    private CustomAdapter adapter;
    private RecyclerView tagsRecyclerView;
    private AlertDialog deleteDialog;
    private EditText searchLabels;

    TextView textMyLabels;

//    LinearLayout layoutSearch;

    // Translation and SharedPreferences components.
    private ProgressDialog progressDialog;
    private Translator translator;
    private SharedPreferences sharedPreferences;

    // Variables for text translation.
    private String[] originalTexts = { "My Labels"};
    private String[] originalEditTexts = { "Search Labels" };
    private int[] textViewIds = { R.id.textMyLabels};
    private String selectedLanguageCode;
    private int[] editTextViewIds = {R.id.searchLabels};
    private SharedPreferences shared_preferences;

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
        setContentView(R.layout.activity_labels);


        // Fullscreen flag and UI component initializations.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        tagsRecyclerView = findViewById(R.id.recyclerView);

        int verticalSpacing = getResources().getDimensionPixelSize(R.dimen.vertical_spacing);
        tagsRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(verticalSpacing));

        adapter = new CustomAdapter(this, DataHolder.getInstance().getTagsList());
        adapter.setClickListener(this);
        adapter.setLongClickListener(this);
        tagsRecyclerView.setAdapter(adapter);
        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchLabels = findViewById(R.id.searchLabels);

        textMyLabels = findViewById(R.id.textMyLabels);

//        layoutSearch = findViewById(R.id.layoutSearch);

        shared_preferences = getSharedPreferences("tutorial", Context.MODE_PRIVATE);

        // Check if tutorial has been shown before
        if (!shared_preferences.getBoolean("tutorial_shown", false)) {
            showTutorial();
        }
//        showTutorial();

        updateLayoutBakground(isBlackTheme);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Translating...");
        progressDialog.setCancelable(false);

        sharedPreferences = getSharedPreferences("Translations_SecondActivity", MODE_PRIVATE);

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

        DataHolder.getInstance().setAdapter(adapter);

        backToNote = findViewById(R.id.back_to_create_note);
        backToNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchLabels.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString();
                searchLabel(searchText);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        setupRecyclerView();

        // Initialize AlertDialog for tag deletion confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this tag?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        CustomAdapter adapter = DataHolder.getInstance().getAdapter();
                        // Delete tag here
                        LabelActivity labelActivity = new LabelActivity();
                        deleteTag(adapter.getTagToDelete());
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        deleteDialog = builder.create();

    }

    private void showTutorial() {
        new GuideView.Builder(this)
                .setTitle("Labels")
                .setContentText("Labels will appear when created")
                .setGravity(Gravity.auto)
                .setDismissType(DismissType.anywhere)
                .setTargetView(textMyLabels)
                .setTitleTextSize(12)
                .setTitleTextSize(16)
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {

                        sharedPreferences.edit().putBoolean("tutorial_shown", true).apply();
                    }
                }).build().show();
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
                        if (textView != null) {
                            textView.setText(translatedText);
                        }
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
            return searchLabels;
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
                if (textView != null) {
                    textView.setText(translation);
                } else {
                    Log.e(TAG, "TextView with ID " + textViewIds[i] + " not found");
                }
            }
        }
    }

    private void applySelectedFont() {
        applyFontStyleToView(textMyLabels, SharedPreferencesHelper.getFontStyle(this));
        // applyFontStyleToEditText(inputSearchNotes,
        // SharedPreferencesHelper.getFontStyle(this));
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

    private void updateLayoutBakground(boolean isBlackTheme){
        int drawableId = isBlackTheme ? R.drawable.background_search : R.drawable.light_background_search;
//        layoutSearch.setBackground(ContextCompat.getDrawable(this, drawableId));drawableId
        searchLabels.setBackground(ContextCompat.getDrawable(this, drawableId));
    }


    private void searchLabel(String searchText) {
        List<TagWithDateTime> filteredNotes = new ArrayList<>();
        for (TagWithDateTime note : DataHolder.getInstance().getTagsList()) {
            if (note.getTag().toLowerCase().contains(searchText.toLowerCase())) {
                filteredNotes.add(note);
            }
        }
        adapter.setData(filteredNotes);
    }

    private void deleteTag(final String tag) {
        List<TagWithDateTime> tagsList = DataHolder.getInstance().getTagsList();

        //I would do this if i wanted to delay it before deleting from the list

//    new Handler().postDelayed(new Runnable() {
//        @Override
//        public void run() {
////            // Remove tag from RecyclerView
////            for (int i = 0; i < tagsList.size(); i++) {
////                TagWithDateTime tagWithDateTime = tagsList.get(i);
////                if (tagWithDateTime.getTag().equals(tag)) {
////                    tagsList.remove(i);
////                    adapter.notifyItemRemoved(i);
////                    break;
////                }
////            }
//        }
//    }, 2000);

        //Remove Tag from RecyclerView
        // Remove tag from RecyclerView

        for (int i = 0; i < tagsList.size(); i++) {
            TagWithDateTime tagWithDateTime = tagsList.get(i);
            if (tagWithDateTime.getTag().equals(tag)) {
                tagsList.remove(i);
                adapter.notifyItemRemoved(i);
                break;
            }
        }


        // Delete tag from Firestore
        DataHolder.getInstance().getFirestoreHelper().deleteTag(tag, new FirestoreHelper.FirestoreCallback<Boolean>() {
            @Override
            public void onCallback(Boolean success) {
                if (success) {
                    Toast.makeText(Labels.this, "Tag deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Labels.this, "Failed to delete tag", Toast.LENGTH_SHORT).show();
                    // If deletion from Firestore fails, add the tag back to RecyclerView
                    tagsList.add(new TagWithDateTime(tag, 0)); // Add back the tag to the list
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


//    private void setupRecyclerView() {
//        // Setup the query for the Firestore data
//        Log.d("LabelActivity","To check if this is executed");
//        Query query = Utility.getCollectionReferenceForLabels().orderBy("dateTime", Query.Direction.DESCENDING);
//        FirestoreRecyclerOptions<Label> options = new FirestoreRecyclerOptions.Builder<Label>()
//                .setQuery(query, Label.class).build();
//
//        labelAdapter = new LabelAdapter(options,this, this);
//        labelsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        labelsRecyclerView.setAdapter(labelAdapter);
//
//        Toast.makeText(this, "The labels class", Toast.LENGTH_SHORT).show();
//
//    }

//    @Override
//    public void onLabelClicked(int position) {
//        DocumentReference documentReference;
//        documentReference = Utility.getCollectionReferenceForLabels().document(DataHolder.getInstance().getLabelId());
//        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    DocumentSnapshot document = task.getResult();
//                    if(document.exists()) {
//                        if(document.contains("labelName")){
//                            Object fieldValue = document.get("labelName");
//                            if(fieldValue != null) {
//                                Toast.makeText(Labels.this, fieldValue.toString(), Toast.LENGTH_SHORT).show();
//                            }else {
//                                Log.d("The Fieldn", "Field 'fieldName' is null");
//                            }
//                        }else {
//                            // Field does not exist in the document
//                            Log.d("Existence of Field", "Field 'fieldName' does not exist in the document");
//                        }
//                    }else {
//                        // Document does not exist
//                        Log.d("Document exists?", "Document does not exist");
//                    }
//                }else {
//                    // Failed to retrieve document
//                    Log.d("Failed to Retrieve", "Failed to retrieve document: ", task.getException());
//                }
//            }
//        });
//    }


    @Override
    public void onItemClick(View view, int position) {
        String tag = DataHolder.getInstance().getTagsList().get(position).getTag();
        DataHolder.getInstance().setTagTitle(tag.toString());
        Intent intent = new Intent(Labels.this, TagsActivity.class);
        intent.putExtra("tag", tag);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(String tag) {
        // Show dialog box for tag deletion
        adapter.setTagToDelete(tag);
        deleteDialog.show();
    }
}