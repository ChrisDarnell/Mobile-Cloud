package com.example.chrisdarnell.androidsqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;



import com.example.chrisdarnell.androidsqlite.Note;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by chrisdarnell on 7/29/17.
 */

public class NotesDataSource implements IDatabase {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.COLUMN_ID,MySQLiteHelper.COLUMN_TEXT};

    public NotesDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    @Override
    public List<Note> getNotes() {
        List<Note> notes = new ArrayList<Note>();

        // TODO: Retrieve all records from the database

        // Convert cursor records to list of Note's


        // make sure to close the cursor

        return notes;
    }

    @Override
    public void addNote(Note note) {
        // TODO: Create ContentValues for key/value pairs to add

        // TODO: Execute insert

        // TODO: Set id for note

    }

    @Override
    public void deleteNote(Note note) {
        // TODO: Delete specified note from the database

    }

    @Override
    public void deleteAllNotes() {
        // TODO: Delete all records from the database
    }


    // ORM method
    private Note cursorToNote(Cursor cursor) {
        Note note = new Note();
        note.setId(cursor.getLong(0));
        note.setText(cursor.getString(1));
        return note;
    }

}
