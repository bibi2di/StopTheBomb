package com.example.stopthebomb.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Comment {
    public int id;
    public String text;
    public String author;
    public String createdDate;
    public boolean isEdited;
    public String editedDate;

    // Default constructor
    public Comment() {
    }

    // Constructor with parameters
    public Comment(String text, String author) {
        this.text = text;
        this.author = author;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        this.createdDate = dateFormat.format(new Date());
        this.isEdited = false;
        this.editedDate = null;
    }

    // Constructor with all fields
    public Comment(int id, String text, String author, String createdDate, boolean isEdited, String editedDate) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.createdDate = createdDate;
        this.isEdited = isEdited;
        this.editedDate = editedDate;
    }
}