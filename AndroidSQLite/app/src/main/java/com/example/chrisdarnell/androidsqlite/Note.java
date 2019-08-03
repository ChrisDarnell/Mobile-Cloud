package com.example.chrisdarnell.androidsqlite;

/**
 * Created by chrisdarnell on 7/29/17.
 */


public class Note {
    private long id;
    private String text;

    public Note() {

    }

    public Note(String text) {
        this.text = text;
        id = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return text;
    }

}