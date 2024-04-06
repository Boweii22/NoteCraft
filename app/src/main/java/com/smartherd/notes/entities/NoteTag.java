package com.smartherd.notes.entities;

import com.google.firebase.firestore.PropertyName;

public class NoteTag {
    private String title;
    private String content;
    private String tag;
    private String color;
    private String date;

    // Required no-argument constructor for Firestore
    public NoteTag() {
    }

    public NoteTag(String title, String content, String tag, String color, String date) {
        this.title = title;
        this.content = content;
        this.tag = tag;
        this.color = color;
        this.date = date;
    }

    @PropertyName("title")
    public String getTitle() {
        return title;
    }

    @PropertyName("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @PropertyName("content")
    public String getContent() {
        return content;
    }

    @PropertyName("content")
    public void setContent(String content) {
        this.content = content;
    }

    @PropertyName("tag")
    public String getTag() {
        return tag;
    }

    @PropertyName("tag")
    public void setTag(String tag) {
        this.tag = tag;
    }

    @PropertyName("color")
    public String getColor() {return color;}

    @PropertyName("color")
    public void setColor(String color) {this.color = color;}

    @PropertyName("date")
    public String getDate() {return date;}

    @PropertyName("date")
    public void setDate(String date) {this.date = date;}
}
