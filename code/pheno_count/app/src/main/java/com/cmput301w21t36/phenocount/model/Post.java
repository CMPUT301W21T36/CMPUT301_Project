package com.cmput301w21t36.phenocount;


import android.os.Build;
import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

/**
 * Role: model
 * This Post abstract class can be specialized as class Question, and class Reply;
 * it contains basic information for a post in the discussion forum.
 */
public abstract class Post implements Serializable {
    protected String ID = "";
    protected String text;
    protected LocalDateTime date;

    public Post(String text){
        this.text = text;
    }
    public Post(){}

    /**
     * It returns the current date when the post is created.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getCurrentDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        date = LocalDateTime.now();
    }

    /**
     * It is used for retrieving the text of a question or a reply
     */
    public String getText() {
        return text;
    }

    /**
     * It is used for retrieving the ID of a question or a reply generated by
     * the database
     */
    public String getID() {
        return ID;
    }

    /**
     * It is used for setting the ID for a question or a reply as the ID
     * generated by the database
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    public void setText(String text) {
        this.text = text;
    }

    //date feature can be added later
    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}