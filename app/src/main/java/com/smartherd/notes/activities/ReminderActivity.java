package com.smartherd.notes.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.smartherd.notes.R;
import com.smartherd.notes.SharedPreferencesHelper;
import com.smartherd.notes.activities.CreateNoteActivity;
import com.smartherd.notes.adapters.NotesAdapter;
import com.smartherd.notes.database.NotesDatabase;
import com.smartherd.notes.entities.Note;
import com.smartherd.notes.listeners.NotesListener;

import java.util.ArrayList;
import java.util.List;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class ReminderActivity extends AppCompatActivity implements NotesListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    DrawerLayout drawerLayout;
    MaterialToolbar materialToolbar;
    NavigationView navigationView;
    LinearLayout layoutSearch;
    TextView remindersTextView;
    EditText inputSearch;
    TextView info;
    private RecyclerView notesReminderRecyclerView;

    private int noteClickedPosition = -1;
    private NotesAdapter notesAdapter;
    public static final int REQUEST_CODE_UPDATE_NOTE = 2; //The request code is used to update note
    long futureTimeInMillis;
    long currentTimeInMillis;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isBlackTheme = SharedPreferencesHelper.loadThemeChoice(this);
        if (isBlackTheme) {
            setTheme(R.style.Theme_App_Black);
        } else {
            setTheme(R.style.Theme_App_White);
        }
        setContentView(R.layout.activity_reminder);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        drawerLayout = findViewById(R.id.drawerLayout);
        materialToolbar = findViewById(R.id.materialToolbar);
        navigationView = findViewById(R.id.navigationView);
        layoutSearch = findViewById(R.id.layoutSearch);
        remindersTextView = findViewById(R.id.textReminders);
        inputSearch = findViewById(R.id.inputSearch);
        info = findViewById(R.id.info);


        notesReminderRecyclerView = findViewById(R.id.notesReminderRecyclerView);
        setupRecyclerView();


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                ReminderActivity.this, drawerLayout, materialToolbar, R.string.drawer_close, R.string.drawer_open);
        drawerLayout.addDrawerListener(toggle);

        sharedPreferences = getSharedPreferences("tutorial", Context.MODE_PRIVATE);

        // Check if tutorial has been shown before
        if (!sharedPreferences.getBoolean("tutorial_shown", false)) {
            showTutorial();
        }
//        showTutorial();


        futureTimeInMillis = DataHolder.getInstance().getData();
        currentTimeInMillis = CreateNoteActivity.currentTimeInMillis;

        Log.d("ReminderActivity", "The future time is " + futureTimeInMillis);
        Log.d("ReminderActivity", "The current time is "+ CreateNoteActivity.currentTimeInMillis);

        Boolean state = DataHolder.getInstance().getState();
        Log.d("ReminderActivity", "The boolean state is " + state);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.notes) {
                    Toast.makeText(ReminderActivity.this, "Notes", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ReminderActivity.this, MainActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (item.getItemId() == R.id.reminder) {
                    Toast.makeText(ReminderActivity.this, "Reminder", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (item.getItemId() == R.id.toDo) {
                    Toast.makeText(ReminderActivity.this, "Todo", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ReminderActivity.this, MainTodo.class);
                    startActivity(intent);
                    finish();
                } else if (item.getItemId() == R.id.label) {
                    Toast.makeText(ReminderActivity.this, "Add Label", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (item.getItemId() == R.id.settings) {
                    Toast.makeText(ReminderActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ReminderActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    finish();
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (item.getItemId() == R.id.help) {
                    Toast.makeText(ReminderActivity.this, "Help&Feedback", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                return false;
            }
        });
        updateLayoutBackground(isBlackTheme); // Call this method to set the LinearLayout background based on the theme


    }

    private void showTutorial() {
        new GuideView.Builder(this)
                .setTitle("Reminder")
                .setContentText("notes with reminders will appear here")
                .setGravity(Gravity.auto)
                .setDismissType(DismissType.anywhere)
                .setTargetView(info)
                .setTitleTextSize(12)
                .setTitleTextSize(16)
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {

                        sharedPreferences.edit().putBoolean("tutorial_shown", true).apply();
                    }
                }).build().show();
    }
    private void setupRecyclerView() {
        // Setup the query for the Firestore data
        Log.d("MainActivity","To check if this is executed");
        Query query = Utility.getCollectionReferenceForReminders().orderBy("dateTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class).build();

        notesAdapter = new NotesAdapter(options, this, this);
        notesReminderRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        notesReminderRecyclerView.setAdapter(notesAdapter);

//        // Check if the RecyclerView has any items
        if (notesAdapter.getItemCount() > 0) {
            info.setVisibility(View.GONE);
        }
        info.setVisibility(View.VISIBLE);

        Log.d("ReminderActivity","The item count for the reminder activity is " + notesAdapter.getItemCount());
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

    @Override
    protected void onResume() {
        super.onResume();
        notesAdapter.notifyDataSetChanged();
        applySelectedFont();
    }

    private void applySelectedFont() {
        applyFontStyleToView(remindersTextView, SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToView(info, SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToEditText(inputSearch, SharedPreferencesHelper.getFontStyle(this));
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
    private void updateLayoutBackground(boolean isBlackTheme) {
        int drawableId = isBlackTheme ? R.drawable.background_search : R.drawable.light_background_search;
        layoutSearch.setBackground(ContextCompat.getDrawable(this, drawableId));

    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", note);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);
    }
}