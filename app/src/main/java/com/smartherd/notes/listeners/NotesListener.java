package com.smartherd.notes.listeners;

import com.smartherd.notes.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);
}
