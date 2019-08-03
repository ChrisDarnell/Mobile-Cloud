package com.example.chrisdarnell.androidsqlite;

import java.util.List;
import com.example.chrisdarnell.androidsqlite.Note;

/**
 * Created by chrisdarnell on 7/29/17.
 */

public interface IDatabase {
        /**
         * Get the current notes (list of {@link Note}s.
         *
         * @return the current notes (list of {@link Note}s
         */
        public List<Note> getNotes();

        /**
         * Add a {@link Note} to the database.
         *
         * @param item the {@link Item} to add
         */
        public void addNote(Note note);

        /**
         * Delete specific note from the database.
         */
        public void deleteNote(Note note);

        /**
         * Delete all notes from the database.
         */
        public void deleteAllNotes();

    }


}
