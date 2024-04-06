package com.smartherd.notes.activities;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smartherd.notes.entities.NoteTag;
import com.smartherd.notes.entities.TagWithDateTime;

import java.util.ArrayList;
import java.util.List;

public class FirestoreHelper {
    private static final String TAG = "FirestoreHelper";
    private static final String COLLECTION_NAME = "NoteLabels";
    private FirebaseFirestore db;

    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void addNote(NoteTag note) {
        db.collection(COLLECTION_NAME)
                .add(note)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Note added with ID: " + task.getResult().getId());
                        } else {
                            Log.w(TAG, "Error adding note", task.getException());
                        }
                    }
                });
    }


    public void getNotesByTag(String tag, final FirestoreCallback<List<NoteTag>> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notesRef = db.collection("NoteLabels");

        notesRef.whereEqualTo("tag", tag)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<NoteTag> notes = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            NoteTag note = documentSnapshot.toObject(NoteTag.class);
                            notes.add(note);
                        }
                        callback.onCallback(notes);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting notes by tag", e);
                        callback.onCallback(null);
                    }
                });
    }

    public void getUniqueTags(final FirestoreCallback<List<TagWithDateTime>> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notesRef = db.collection("NoteLabels");

        notesRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<TagWithDateTime> tags = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String tag = documentSnapshot.getString("tag");
                            String dateTime = documentSnapshot.getString("date");
                            String color = documentSnapshot.getString("color");
                            TagWithDateTime tagWithDateTime = new TagWithDateTime(tag, dateTime, color);
                            if (!tags.contains(tagWithDateTime)) {
                                tags.add(tagWithDateTime);
                            }
                        }
                        callback.onCallback(tags);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting tags", e);
                        callback.onCallback(null);
                    }
                });
    }

    public void deleteTag(final String tag, final FirestoreCallback<Boolean> callback) {
        db.collection(COLLECTION_NAME)
                .whereEqualTo("tag", tag)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection(COLLECTION_NAME).document(document.getId())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                callback.onCallback(true);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                callback.onCallback(false);
                                            }
                                        });
                            }
                        } else {
                            callback.onCallback(false);
                        }
                    }
                });
    }


    public interface FirestoreCallback<T> {
        void onCallback(T data);
    }
}
