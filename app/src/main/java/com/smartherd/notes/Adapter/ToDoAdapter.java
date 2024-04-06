package com.smartherd.notes.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartherd.notes.Model.ToDoModel;
import com.smartherd.notes.R;
import com.smartherd.notes.SharedPreferencesHelper;
import com.smartherd.notes.Utils.DataBaseHelper;
import com.smartherd.notes.activities.MainActivity;
import com.smartherd.notes.activities.MainTodo;
import com.smartherd.notes.entities.AddNewTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {

    private List<ToDoModel> mList;
    private MainTodo activity;
    private DataBaseHelper myDB;
    private List<ToDoModel> originalList; // Maintain the original list for filtering
    private Timer timer;
    private boolean isBlackTheme; //Theme flag

    public ToDoAdapter(DataBaseHelper myDB, MainTodo activity){
        this.activity = activity;
        this.myDB = myDB;
        this.originalList = new ArrayList<>();

        //Initialize isBlackTheme here using the activity context
        this.isBlackTheme = SharedPreferencesHelper.loadThemeChoice(activity);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Choose layout file based on the theme
        int layoutResId = isBlackTheme ? R.layout.task_layout: R.layout.task_layout_light;
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ToDoModel item  = mList.get(position);
        holder.mCheckBox.setText(item.getTask());
        holder.mCheckBox.setChecked(toBoolean(item.getStatus()));
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    myDB.updateStatus(item.getId(), 1);
                }else{
                    myDB.updateStatus(item.getId(), 0);
                }
            }
        });
        // Apply the font style to the CheckBox
        holder.applyFontStyleToView(getContext());

        /**
         * The applyFontStyleToView method in the MyViewHolder class determines the current font style using SharedPreferencesHelper.getFontStyle(context) and applies it to the CheckBox.
         * The context needed for SharedPreferencesHelper.getFontStyle(context) is obtained from the getContext() method in the ToDoAdapter, which returns the MainTodo activity context.
         * This approach ensures that the font style selected by the user is applied to the text of each CheckBox in your RecyclerView, maintaining consistency in user-selected preferences across the app.
         */
    }
    public boolean toBoolean(int num){
        return num!=0;
    }

    public Context getContext(){
        return activity;
    }

    public void setTasks(List<ToDoModel> mList){
        this.mList = mList;
        this.originalList = new ArrayList<>(mList); //Update the originalList when setting tasks
        notifyDataSetChanged();
    }
    public void deleteTask(int position){
        ToDoModel item = mList.get(position);
        myDB.deleteTask(item.getId());
        mList.remove(position);
        notifyItemRemoved(position);
    }
    public void editItem(int position){
        ToDoModel item = mList.get(position);

        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());

        AddNewTask task = new AddNewTask();
        task.setArguments(bundle);
        task.show(activity.getSupportFragmentManager(), task.getTag());
    }
    public void filterTasks(String query) {
        mList.clear();

        if (query.isEmpty()) {
            mList.addAll(originalList);
        } else {
            for (ToDoModel task : originalList) {
                if (task.getTask().toLowerCase().contains(query.toLowerCase())) {
                    mList.add(task);
                }
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox mCheckBox;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mCheckBox = itemView.findViewById(R.id.checkbox);
        }
        void applyFontStyleToView(Context context) {
            String fontStyle = SharedPreferencesHelper.getFontStyle(context);
            Typeface typeface;
            switch (fontStyle) {
                case "sans":
                    typeface = Typeface.SANS_SERIF;
                    break;
                case "serif":
                    typeface = Typeface.SERIF;
                    break;
                case "monospace":
                    typeface = Typeface.MONOSPACE;
                    break;
                default:
                    typeface = Typeface.DEFAULT;
                    break;
            }
            mCheckBox.setTypeface(typeface);
        }
    }

}
