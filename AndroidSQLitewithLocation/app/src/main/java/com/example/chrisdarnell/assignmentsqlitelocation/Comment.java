package com.example.chrisdarnell.assignmentsqlitelocation;

import android.arch.persistence.room.PrimaryKey;

/**
 * Created by chrisdarnell.
 */

public class Comment {

    @PrimaryKey
    public long id;
    public String comment;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return comment;

    }
}
