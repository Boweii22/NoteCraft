package com.smartherd.notes.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.smartherd.notes.R;
import com.smartherd.notes.SharedPreferencesHelper;
import com.smartherd.notes.adapters.CustomNoteAdapter;
import com.smartherd.notes.entities.NoteTag;

import java.util.ArrayList;
import java.util.List;

public class TagsActivity extends AppCompatActivity {
    private FirestoreHelper firestoreHelper;
    private TextView tagTitleTextView;
    private TextView textBack;

    List<NoteTag> noteTags = new ArrayList<>();

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
        setContentView(R.layout.activity_tags);

        // Fullscreen flag and UI component initializations.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        tagTitleTextView = findViewById(R.id.tagTitleTextView);
        textBack = findViewById(R.id.text_back);

        String tagTitle = DataHolder.getInstance().getTagTitle() + " Notes";
        tagTitleTextView.setText(tagTitle);


        textBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        RecyclerView notesRecyclerView = findViewById(R.id.notesRecyclerView);
        String tag = getIntent().getStringExtra("tag");

//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));


        firestoreHelper = new FirestoreHelper();

        firestoreHelper.getNotesByTag(tag, new FirestoreHelper.FirestoreCallback<List<NoteTag>>() {
            @Override
            public void onCallback(List<NoteTag> notes) {
                if (notes != null) {
                    CustomNoteAdapter adapter = new CustomNoteAdapter(TagsActivity.this, notes);
                    notesRecyclerView.setAdapter(adapter);

                }
            }
        });
    }

}
