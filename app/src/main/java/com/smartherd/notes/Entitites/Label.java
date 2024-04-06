package com.smartherd.notes.Entitites;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "labels")
public class Label implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "label_name")
    public String labelName;

    @ColumnInfo(name = "date_time")
    private String dateTime;

    @ColumnInfo(name = "color")
    private String color;

    public String getLabelName() {return labelName;}
    public void setLabelName(String labelName) {this.labelName = labelName;}

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Constructor, Getters, and Setters omitted for brevity

    /**
     * //@Entity annotation specifies that this class should be treated as a table within the database. The tableName attribute specifies the name of the table.
     * //@PrimaryKey annotation indicates which field should be treated as the primary key for the table. autoGenerate = true means the ID will be generated automatically.
     * //@NonNull annotation indicates that the labelName field must not be null.
     */
}
