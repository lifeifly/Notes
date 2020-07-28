package com.example.lo17notes.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class NoteDB extends SQLiteOpenHelper {
    public static final String TABLE_NAME_NOTES = "notes";
    public static final String TABLE_NAME_MEDIA = "media";
    public static final String COLUMN_NAME_NOTE_ID = "_id";
    public static final String COLUMN_NAME_NOTE_NAME = "name";
    public static final String COLUMN_NAME_NOTE_CONTENT = "content";
    public static final String COLUMN_NAME_NOTE_DATE = "date";
    public static final String COLUMN_NAME_MEDIA_PATH = "path";
    public static final String COLUMN_NAME_MEDIA_OWNERID = "note_id";


    public NoteDB(@Nullable Context context) {
        super(context, "notes", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建Note表
        db.execSQL("CREATE TABLE " + TABLE_NAME_NOTES + "(" +
                COLUMN_NAME_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME_NOTE_NAME + " TEXT NOT NULL DEFAULT \"\"," +
                COLUMN_NAME_NOTE_CONTENT + " TEXT NOT NULL DEFAULT \"\"," +
                COLUMN_NAME_NOTE_DATE + " TEXT NOT NULL DEFAULT \"\"" +
                ")");
        //创建Media表
        db.execSQL("CREATE TABLE "+TABLE_NAME_MEDIA+"(" +
                COLUMN_NAME_NOTE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                COLUMN_NAME_MEDIA_PATH+" TEXT NOT NULL DEFAULT \"\", " +
                COLUMN_NAME_MEDIA_OWNERID+" TEXT NOT NULL DEFAULT \"\"" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
