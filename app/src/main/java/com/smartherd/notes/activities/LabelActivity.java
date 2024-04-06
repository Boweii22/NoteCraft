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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.smartherd.notes.Entitites.Label;
import com.smartherd.notes.R;
import com.smartherd.notes.SharedPreferencesHelper;
import com.smartherd.notes.adapters.CustomAdapter;
import com.smartherd.notes.adapters.CustomLabelAdapter;
import com.smartherd.notes.adapters.LabelAdapter;
import com.smartherd.notes.entities.NoteTag;
import com.smartherd.notes.entities.TagWithDateTime;
import com.smartherd.notes.listeners.LabelListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LabelActivity extends AppCompatActivity implements LabelListener {

    TextView textDateTime;
    ImageView labelBackView;
    ImageView save_label;
    EditText inputLabel;
    View viewColor1;
    View viewColor2;
    View viewColor3;
    View viewColor4;
    View viewColor5;
    View viewColor6;
    View viewColor7;
    View viewColor8;
    View viewColor9;
    View viewColor10;
    private RecyclerView labelRecyclerView;
    private LabelAdapter labelAdapter;
    String selectedNoteColor;

    private FirestoreHelper firestoreHelper;
    private List<TagWithDateTime> tagsList = new ArrayList<>();
    private AlertDialog deleteDialog;

    TextView textLabels;
    EditText inputLabels;






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
        setContentView(R.layout.activity_label);

        // Fullscreen flag and UI component initializations.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DataHolder.getInstance().setTagsList(tagsList);

        firestoreHelper = new FirestoreHelper();

        selectedNoteColor = "#333333";

        textLabels = findViewById(R.id.textLabels);
        inputLabels = findViewById(R.id.inputLabel);
        textDateTime = findViewById(R.id.textDateTime);
        textDateTime.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date()));

        labelBackView = findViewById(R.id.labelBackView);
        save_label = findViewById(R.id.save_label);
        inputLabel = findViewById(R.id.inputLabel);
//        labelRecyclerView = findViewById(R.id.labelRecyclerView);
//        setupRecyclerView();

        updateLayoutBakground(isBlackTheme);


        chooseColor();

        firestoreHelper.getUniqueTags(new FirestoreHelper.FirestoreCallback<List<TagWithDateTime>>() {
            @Override
            public void onCallback(List<TagWithDateTime> tags) {
                if (tags != null) {
                    tagsList.clear();
                    tagsList.addAll(tags);
                    CustomAdapter adapter = DataHolder.getInstance().getAdapter();
                }
            }
        });

        save_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_label();
            }
        });
        labelBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    private void applySelectedFont() {
        applyFontStyleToView(textLabels, SharedPreferencesHelper.getFontStyle(this));
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
        inputLabels.setBackground(ContextCompat.getDrawable(this, drawableId));
    }

    private void refreshTagsList() {
        firestoreHelper.getUniqueTags(new FirestoreHelper.FirestoreCallback<List<TagWithDateTime>>() {
            @Override
            public void onCallback(List<TagWithDateTime> tags) {
                if (tags != null) {
                    tagsList.clear();
                    tagsList.addAll(tags);
                    DataHolder.getInstance().getAdapter().notifyDataSetChanged();
                }
            }
        });
    }


    private void chooseColor() {
        final ImageView imageColor1 = findViewById(R.id.imageColor1);
        final ImageView imageColor2 = findViewById(R.id.imageColor2);
        final ImageView imageColor3 = findViewById(R.id.imageColor3);
        final ImageView imageColor4 = findViewById(R.id.imageColor4);
        final ImageView imageColor5 = findViewById(R.id.imageColor5);
        final ImageView imageColor6 = findViewById(R.id.imageColor6);
        final ImageView imageColor7 = findViewById(R.id.imageColor7);
        final ImageView imageColor8 = findViewById(R.id.imageColor8);
        final ImageView imageColor9 = findViewById(R.id.imageColor9);
        final ImageView imageColor10 = findViewById(R.id.imageColor10);

        viewColor1 = findViewById(R.id.viewColor1);
        viewColor2 = findViewById(R.id.viewColor2);
        viewColor3 = findViewById(R.id.viewColor3);
        viewColor4 = findViewById(R.id.viewColor4);
        viewColor5 = findViewById(R.id.viewColor5);
        viewColor6 = findViewById(R.id.viewColor6);
        viewColor7 = findViewById(R.id.viewColor7);
        viewColor8 = findViewById(R.id.viewColor8);
        viewColor9 = findViewById(R.id.viewColor9);
        viewColor10 = findViewById(R.id.viewColor10);
        viewColor1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
        viewColor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
        viewColor3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
        viewColor4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
        viewColor5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
        viewColor6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
        viewColor7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
        viewColor8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
        viewColor9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
        viewColor10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
        NoteTag noteTag = new NoteTag();
        switch (selectedNoteColor) {
            case "#333333":
                noteTag.setColor("#333333");
                break;
            case "#008080":
                noteTag.setColor("#008080");
                break;
            case "#FF4842":
                noteTag.setColor("#FF4842");
                break;
            case "#3A52Fc":
                noteTag.setColor("#3A52Fc");
                break;
            case "#000000":
                noteTag.setColor("#000000");
                break;
            case "#0E7337":
                noteTag.setColor("#0E7337");
                break;
            case "#FFA500":
                noteTag.setColor("#FFA500");
                break;
            case "#A020F0":
                noteTag.setColor("#A020F0");
                break;
            case "#FFD700":
                noteTag.setColor("#FFD700");
                break;
            case "#6f432a":
                noteTag.setColor("#6f432a");
                break;
        }

    }



    private void save_label() {
        NoteTag note = null;
        String title = DataHolder.getInstance().getTitle().toString();
        String subtitle = DataHolder.getInstance().getSubtitle().toString();
        String tag = inputLabel.getText().toString();
        String color = selectedNoteColor;
        String dateTime = textDateTime.getText().toString();
        DataHolder.getInstance().setDateTime(dateTime);

        if (!title.isEmpty() && !subtitle.isEmpty() && !tag.isEmpty()) {
            note = new NoteTag(title, subtitle, tag, color, dateTime);
            note.setDate(dateTime);
            firestoreHelper.addNote(note);
            refreshTagsList();
            startActivity(new Intent(LabelActivity.this, Labels.class));
        } else {
            Toast.makeText(LabelActivity.this, "Provide inputs", Toast.LENGTH_SHORT).show();
        }

        DataHolder.getInstance().setFirestoreHelper(firestoreHelper);

//                saveLabel();
        Log.d("LabelActivity", "The note title is " + DataHolder.getInstance().getTitle().toString());
        Log.d("LabelActivity", "The note subtitle is " + DataHolder.getInstance().getSubtitle().toString());
        Log.d("LabelActivity", "The note dateTime is " + dateTime);
        Log.d("LabelActivity", "The note color is " + selectedNoteColor);
//        Log.d("LabelActivity", "The ")
//                DataHolder.getInstance().setUniqueTags(tags);
    }

//    private void setupRecyclerView() {
//        // Setup the query for the Firestore data
//        Log.d("LabelActivity","To check if this is executed");
//        Query query = Utility.getCollectionReferenceForLabels().orderBy("dateTime", Query.Direction.DESCENDING);
//        FirestoreRecyclerOptions<Label> options = new FirestoreRecyclerOptions.Builder<Label>()
//                .setQuery(query, Label.class).build();
//
//        labelAdapter = new LabelAdapter(options,this, this);
//        labelRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        labelRecyclerView.setAdapter(labelAdapter);
//
//    }
//    private void saveLabel() {
//
//        if(inputLabel.getText().toString().isEmpty()){
//            Toast.makeText(this, "Label can't be Empty", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        final Label label = new Label();
//        label.setLabelName(inputLabel.getText().toString());
//        label.setDateTime(DataHolder.getInstance().getDateTime());
//        label.setColor(selectedNoteColor);
//
//        saveLabelToFirebase(label);
//    }

//    private void saveLabelToFirebase(Label label) {
//        DocumentReference documentReference;
//        documentReference = Utility.getCollectionReferenceForLabels().document();
//
//        documentReference.set(label).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    //Label is added
//                    Toast.makeText(LabelActivity.this, "Label added successfully", Toast.LENGTH_SHORT).show();
//                    inputLabel.setText("");
//                }else {
//                    Toast.makeText(LabelActivity.this, "Failed while creating label", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }




    @Override
    public void onLabelClicked(int position) {

    }
}