package com.smartherd.notes.entities;

public class TagWithDateTime {
    private String tag;
    private String dateTime;
    private String color;

    public TagWithDateTime(String tag, String dateTime, String color) {
        this.tag = tag;
        this.dateTime = dateTime;
        this.color = color;
    }

    public TagWithDateTime(String tag, int i) {
    }

    public String getTag() {
        return tag;
    }

    public String getDateTime() {
        return dateTime;
    }
    public String getColor() {
        return color;
    }
}

