package com.example.patankar.notesqlite.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.patankar.notesqlite.database.model.Notes;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "notes_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(Notes.CREATE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Notes.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }

    public long insertNote(String note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(Notes.COLUMN_NOTE, note);

        long id = db.insert(Notes.TABLE_NAME, null, contentValues);
        db.close();

        return id;
    }

    public Notes getNote(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Notes.TABLE_NAME,
                new String[]{Notes.COLUMN_ID, Notes.COLUMN_NOTE, Notes.COLUMN_TIMESTAMP},
                Notes.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Notes note = new Notes(cursor.getInt(cursor.getColumnIndex(Notes.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Notes.COLUMN_NOTE)),
                cursor.getString(cursor.getColumnIndex(Notes.COLUMN_TIMESTAMP)));


        cursor.close();

        return note;

    }

    public List<Notes> getAllNotes() {
        List<Notes> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Notes.TABLE_NAME + " ORDER BY " +
                Notes.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Notes note = new Notes();
                note.setId(cursor.getInt(cursor.getColumnIndex(Notes.COLUMN_ID)));
                note.setNote(cursor.getString(cursor.getColumnIndex(Notes.COLUMN_NOTE)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(Notes.COLUMN_TIMESTAMP)));
                notes.add(note);
            } while (cursor.moveToNext());
        }
        else {

        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public  int getNotesCount() {
        String  countquery = " SELECT * FROM " + Notes.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countquery, null);

        int count = cursor.getCount();
        cursor.close();

        return  count;
    }

    public int updateNOte(Notes note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Notes.COLUMN_NOTE, note.getNote());

        return  db.update(Notes.TABLE_NAME, values,Notes.COLUMN_ID + " = ? ",
                new String[]{String.valueOf(note.getNote())});
    }

    public void deleteNote(Notes note){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Notes.TABLE_NAME,Notes.COLUMN_ID + " = ? " ,
                new String[]{String.valueOf(note.getId())});
        db.close();


    }
}