package com.smartherd.notes.activities;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Utility {
    static CollectionReference getCollectionReferenceForNotes(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            return FirebaseFirestore.getInstance().collection("notes").document(currentUser.getUid()).collection("my_notes");
        }
        return null;
    }

    static CollectionReference getCollectionReferenceForReminders(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            return FirebaseFirestore.getInstance().collection("reminder").document(currentUser.getUid()).collection("my_reminder_notes");
        }
        return null;
    }
    static CollectionReference getCollectionReferenceForTasks(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        return FirebaseFirestore.getInstance().collection("tasks").document(currentUser.getUid()).collection("my_tasks");
    }
    static CollectionReference getCollectionReferenceForLabels(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            return FirebaseFirestore.getInstance().collection("labels").document(currentUser.getUid()).collection("my_labels");
        }
        return null;
    }
    static CollectionReference getCollectionReferenceForFeedbacks(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            return FirebaseFirestore.getInstance().collection("feedbacks").document(currentUser.getUid()).collection("my_feedbacks");
        }
        return null;
    }
}
