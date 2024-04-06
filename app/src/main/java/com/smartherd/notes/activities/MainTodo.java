package com.smartherd.notes.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.smartherd.notes.Adapter.ToDoAdapter;
import com.smartherd.notes.Model.ToDoModel;
import com.smartherd.notes.R;
import com.smartherd.notes.SharedPreferencesHelper;
import com.smartherd.notes.Utils.DataBaseHelper;
import com.smartherd.notes.entities.AddNewTask;
import com.smartherd.notes.entities.RecyclerViewTouchHelper;
import com.smartherd.notes.listeners.OnDialogCloseListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class MainTodo extends AppCompatActivity implements OnDialogCloseListener {


    private RecyclerView mRecyclerView;
    private FloatingActionButton fab;
    private DataBaseHelper myDB;
    private List<ToDoModel> mList;
    private ToDoAdapter adapter;

    MaterialToolbar materialToolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    LinearLayout layoutSearch;
    TextView tasksTextView;
    EditText inputTaskSearch;
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
        setContentView(R.layout.activity_main_todo);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        layoutSearch = findViewById(R.id.layoutTaskSearch);
        tasksTextView = findViewById(R.id.textview);
        inputTaskSearch = findViewById(R.id.inputTaskSearch);

        mRecyclerView = findViewById(R.id.recyclerview);
        materialToolbar = findViewById(R.id.materialToolbar);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerTaskLayout);
        fab = findViewById(R.id.fab);
        myDB = new DataBaseHelper(MainTodo.this);
        mList = new ArrayList<>();
        adapter = new ToDoAdapter(myDB, MainTodo.this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);

        mList = myDB.getAllTasks();
        Collections.reverse(mList);
        adapter.setTasks(mList);

        sharedPreferences = getSharedPreferences("tutorial", Context.MODE_PRIVATE);

        // Check if tutorial has been shown before
        if (!sharedPreferences.getBoolean("tutorial_shown", false)) {
            showTutorial();
        }
//        showTutorial();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainTodo.this, drawerLayout, materialToolbar, R.string.drawer_close, R.string.drawer_open);
        drawerLayout.addDrawerListener(toggle);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId()==R.id.notes){
                    Toast.makeText(MainTodo.this, "Notes", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainTodo.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (item.getItemId()==R.id.reminder) {
                    Toast.makeText(MainTodo.this, "Reminder", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainTodo.this, ReminderActivity.class);
                    startActivity(intent);
                    finish();
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (item.getItemId() == R.id.toDo) {
                    Toast.makeText(MainTodo.this, "Todo", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (item.getItemId()==R.id.label) {
                    Toast.makeText(MainTodo.this, "Add Label", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainTodo.this, Labels.class);
                    startActivity(intent);
                    finish();
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (item.getItemId()==R.id.settings) {
                    Toast.makeText(MainTodo.this, "Settings", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainTodo.this, SettingsActivity.class);
                    startActivity(intent);
                    finish();
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (item.getItemId()==R.id.help) {
                    Toast.makeText(MainTodo.this, "Help&Feedback", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainTodo.this, HelpFeedbackActivity.class);
                    startActivity(intent);
                    finish();
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                return false;
            }
        });
        updateLayoutBackground(isBlackTheme); // Call this method to set the LinearLayout background based on the theme


        EditText inputTaskSearch = findViewById(R.id.inputTaskSearch);
        inputTaskSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mList.size() != 0){
                    adapter.filterTasks(s.toString());
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerViewTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void showTutorial() {
        new GuideView.Builder(this)
                .setTitle("SearchView")
                .setContentText("Search tasks")
                .setGravity(Gravity.auto)
                .setDismissType(DismissType.anywhere)
                .setTargetView(inputTaskSearch)
                .setTitleTextSize(12)
                .setTitleTextSize(16)
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        addTaskSuggestion();
                    }
                }).build().show();
    }

    private void addTaskSuggestion() {
        new GuideView.Builder(this)
                .setTitle("Button")
                .setContentText("Click to create task")
                .setGravity(Gravity.auto)
                .setDismissType(DismissType.anywhere)
                .setTargetView(fab)
                .setTitleTextSize(12)
                .setTitleTextSize(16)
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        taskSuggestion();
                    }
                }).build().show();
    }

    private void taskSuggestion() {
        new GuideView.Builder(this)
                .setTitle("Task")
                .setContentText("Swipe left to delete and right to edit")
                .setGravity(Gravity.auto)
                .setDismissType(DismissType.anywhere)
                .setTargetView(tasksTextView)
                .setTitleTextSize(12)
                .setTitleTextSize(16)
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {

                        sharedPreferences.edit().putBoolean("tutorial_shown", true).apply();
                    }
                }).build().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applySelectedFont();
    }

    private void applySelectedFont() {
        applyFontStyleToView(tasksTextView, SharedPreferencesHelper.getFontStyle(this));
        applyFontStyleToEditText(inputTaskSearch, SharedPreferencesHelper.getFontStyle(this));
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
    public void onDialogClose(DialogInterface dialogInterface) {
        mList = myDB.getAllTasks();
        Collections.reverse(mList);
        adapter.setTasks(mList);
        adapter.notifyDataSetChanged();
    }
}