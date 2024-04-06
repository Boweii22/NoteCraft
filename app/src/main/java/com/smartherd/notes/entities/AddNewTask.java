package com.smartherd.notes.entities;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smartherd.notes.Model.ToDoModel;
import com.smartherd.notes.R;
import com.smartherd.notes.Utils.DataBaseHelper;
import com.smartherd.notes.activities.DataHolder;
import com.smartherd.notes.activities.Utility;
import com.smartherd.notes.listeners.OnDialogCloseListener;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "AddNewTask";

    // Widgets
    private EditText mEditText;
    private TextView mAddTask;
    private TextView mCancelTask;

    private FirebaseFirestore db;

    private DataBaseHelper myDb;
    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_newtask, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEditText = view.findViewById(R.id.inputTask);
        mAddTask = view.findViewById(R.id.textAdd);
        mCancelTask = view.findViewById(R.id.textCancel);

        db = FirebaseFirestore.getInstance();
        myDb = new DataBaseHelper(getActivity());
        boolean isUpdate = false;

        Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            String task = bundle.getString("task");
            mEditText.setText(task);

            if (task.length() > 0) {
                mAddTask.setEnabled(false);
            }
        }

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    mAddTask.setEnabled(false);
                    mAddTask.setTextColor(Color.GRAY);
                } else {
                    mAddTask.setEnabled(true);
                    mAddTask.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        final boolean finalIsUpdate = isUpdate;
        mAddTask.setOnClickListener(v -> {
            String text = mEditText.getText().toString();

            if (finalIsUpdate) {
                // If you need to update in SQLite, you can add the logic here
                myDb.updateTask(bundle.getInt("id"),text);
            } else {
                ToDoModel item = new ToDoModel();
                item.setTask(text);
                item.setStatus(0);
                myDb.insertTask(item);
                // Save task to Firebase Firestore
                saveTaskToFirebase(text);
            }
            dismiss();
        });

        mCancelTask.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof OnDialogCloseListener) {
            ((OnDialogCloseListener) activity).onDialogClose(dialog);
        }
    }

    private void saveTaskToFirebase(String task) {
        // Add a new document with a generated ID
        DocumentReference documentReference = db.collection("tasks").document();
        ToDoModel toDoModel = new ToDoModel();
        toDoModel.setTask(task);
        toDoModel.setStatus(0); // Assuming initial status is 0
//        toDoModel.setId(documentReference.getId()); // Set ID using the generated ID
        documentReference.set(toDoModel)
                .addOnSuccessListener(aVoid -> {
                    // Log success or perform any other actions needed
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

}
