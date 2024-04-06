//package com.smartherd.notes.database;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//import com.smartherd.notes.entities.Note;
//import com.smartherd.notes.entities.NoteTag;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//public class DatabaseHelper extends SQLiteOpenHelper {
//    private static final String DATABASE_NAME = "notesDatabase";
//    private static final int DATABASE_VERSION = 1;
//    private static final String TABLE_NOTES = "notes";
//    private static final String KEY_ID = "id";
//    private static final String KEY_TITLE = "title";
//    private static final String KEY_CONTENT = "content";
//    private static final String KEY_TAG = "tag";
//
//    public DatabaseHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES + "("
//                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT,"
//                + KEY_CONTENT + " TEXT," + KEY_TAG + " TEXT" + ")";
//        db.execSQL(CREATE_NOTES_TABLE);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
//        onCreate(db);
//    }
//
//    public void addNote(NoteTag note) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(KEY_TITLE, note.getTitle());
//        values.put(KEY_CONTENT, note.getContent());
//        values.put(KEY_TAG, note.getTag());
//        db.insert(TABLE_NOTES, null, values);
//        db.close();
//    }
//
//    public List<NoteTag> getNotesByTag(String tag) {
//        List<NoteTag> noteList = new ArrayList<>();
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = db.query(TABLE_NOTES, new String[] { KEY_ID, KEY_TITLE,
//                        KEY_CONTENT, KEY_TAG }, KEY_TAG + "=?",
//                new String[] { String.valueOf(tag) }, null, null, null, null);
//
//        if (cursor.moveToFirst()) {
//            do {
//                NoteTag note = new NoteTag(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
//                noteList.add(note);
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        db.close();
//        return noteList;
//    }
//
//    public List<String> getUniqueTags() {
//        List<String> tags = new ArrayList<>();
//        Set<String> uniqueTags = new HashSet<>();
//        String selectQuery = "SELECT " + KEY_TAG + " FROM " + TABLE_NOTES;
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        if (cursor.moveToFirst()) {
//            do {
//                uniqueTags.add(cursor.getString(0));
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        db.close();
//        tags.addAll(uniqueTags);
//        return tags;
//    }
//}
//
