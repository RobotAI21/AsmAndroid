package com.example.appdevelopment.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.contentcapture.DataRemovalRequest;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    //Where define create Database and table
    //logCat Tag
    private static final String LOG = DbHelper.class.getName(); // use db class
    //create name for db
    protected static final String DB_NAME = "campus_expenses";
    protected static final int DB_VERSION = 1;
    //tao ten bang db
    protected static final String TABLE_USERS = "users";
    //ten cac cot trong bang
    protected static final String COL_ID = "id";
    protected static final String COL_USERNAME = "username";
    protected static final String COL_PASSWORD = "password";
    protected static final String COL_EMAIL = "email";
    protected static final String COL_PHONE = "phone";
    protected static final String COL_ROLE = "role";
    protected static final String COL_CREATED_AT = "created_at";
    protected static final String COL_UPDATED_AT = "updated_at";
    //tao bang du lieu
    private static final String CREATE_TABLE_USERS = " CREATE TABLE " +
            TABLE_USERS + " ( " +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
            COL_USERNAME + " VARCHAR(50) NOT NULL, " +
            COL_PASSWORD + " VARCHAR(255) NOT NULL, " +
            COL_EMAIL + " VARCHAR(150) NOT NULL, " +
            COL_PHONE + " VARCHAR(50), " +
            COL_ROLE + " INTEGER NOT NULL, " +
            COL_CREATED_AT + " DATETIME, " +
            COL_UPDATED_AT + " DATETIME )";

    public DbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        // tao bang them vao day
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        //xoa bo cac bang neu loi and create again
        onCreate(db);
    }
}
