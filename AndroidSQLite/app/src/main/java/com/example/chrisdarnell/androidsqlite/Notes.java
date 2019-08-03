package com.example.chrisdarnell.androidsqlite;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * Created by chrisdarnell on 7/29/17.
 */

public class Notes extends Activity {
    private NotesDataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get data source
        datasource = new NotesDataSource(this);
        datasource.open();

        setDefaultView();
    }

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }


    public void setDefaultView() {
        setContentView(R.layout.activity_main);

        Button addButton = (Button) findViewById(R.id.addButton);
        Button viewButton = (Button) findViewById(R.id.viewButton);
        final EditText text = (EditText) findViewById(R.id.noteText);

        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    // TODO: Add new Note

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        viewButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                displayNoteView();
            }
        });

    }

    public void displayNoteView() {

        // Display notes
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT);

        Button backButton = new Button(this);
        backButton.setText("Back");
        backButton.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                setDefaultView();
            }
        });

        layout.addView(backButton);

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO: Delete all existing notes

                // Refresh view
                displayNoteView();
            }
        });
        layout.addView(deleteButton);

        // Add ListView with notes
        final List<Note> notes = datasource.getNotes();
        // Add ListView with notes
        ListAdapter la = new ArrayAdapter<Note>(this, R.layout.list_item, notes);
        ListView lv = new ListView(this);
        lv.setAdapter(la);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                                           long id) {
                // TODO: Delete note at selected item

                // Refresh view
                displayNoteView();
                return true;
            }

        });
        layout.addView(lv);

        // Make inventory view visible
        setContentView(layout,llp);
    }

}

