package com.smartherd.notes.activities;

import com.smartherd.notes.adapters.CustomAdapter;
import com.smartherd.notes.entities.TagWithDateTime;

import java.util.List;

public class DataHolder {
    private static DataHolder instance;
    private long data;
    private boolean state;
    private String labelName;

    private String labelId;
    private String title;
    private String subtitle;
    private String tag;
    private String dateTime;
    private String tagTitle;
    CustomAdapter adapter;
    private List<TagWithDateTime> tagsList;
    private FirestoreHelper firestoreHelper;
    private boolean isBlackTheme;
    private int rating;
    private String noteColor;
    private String subtitleIndicatorColor;
    private String documentContent;
    private String labelColor;
    private DataHolder() {
    }

    public static synchronized DataHolder getInstance() {
        if (instance == null) {
            instance = new DataHolder();
        }
        return instance;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public boolean getState() {
        return state;
    }
    public void setState(boolean state){
        this.state = state;
    }

    public String getLabelName() {return labelName;}
    public void setLabelName(String labelId) {this.labelName = labelName;}
    public String getLabelId() {return labelId;}
    public void setLabelId(String labelId) {this.labelId = labelId;}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public CustomAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(CustomAdapter adapter) {
        this.adapter = adapter;
    }

    public List<TagWithDateTime> getTagsList() {
        return tagsList;
    }

    public void setTagsList(List<TagWithDateTime> tagsList) {
        this.tagsList = tagsList;
    }

    public FirestoreHelper getFirestoreHelper() {
        return firestoreHelper;
    }

    public void setFirestoreHelper(FirestoreHelper firestoreHelper) {
        this.firestoreHelper = firestoreHelper;
    }

    public String getTagTitle() {
        return tagTitle;
    }

    public void setTagTitle(String tagTitle) {
        this.tagTitle = tagTitle;
    }

    public boolean isBlackTheme() {
        return isBlackTheme;
    }

    public void setBlackTheme(boolean blackTheme) {
        this.isBlackTheme = blackTheme;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getNoteColor() {
        return noteColor;
    }

    public void setNoteColor(String noteColor) {
        this.noteColor = noteColor;
    }

    public String getSubtitleIndicatorColor() {
        return subtitleIndicatorColor;
    }

    public void setSubtitleIndicatorColor(String subtitleIndicatorColor) {
        this.subtitleIndicatorColor = subtitleIndicatorColor;
    }

    public String getDocumentContent() {
        return documentContent;
    }

    public void setDocumentContent(String documentContent) {
        this.documentContent = documentContent;
    }

    public String getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(String labelColor) {
        this.labelColor = labelColor;
    }
}
