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
    protected static final int DB_VERSION = 4;
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

    //dinh nghia bang budget
    protected static final String TABLE_BUDGET = "budget";
    protected static final String COL_BUDGET_ID = "id";
    protected static final String COL_BUDGET_NAME = "name";
    protected static final String COL_BUDGET_MONEY = "money";
    protected static final String COL_BUDGET_DESCRIPTION = "description";
    protected static final String COL_BUDGET_STATUS = "status_budget";

    private final String CREATE_TABLE_BUDGET = " CREATE TABLE " +
            TABLE_BUDGET + " ( " +
            COL_BUDGET_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_BUDGET_NAME + " VARCHAR(100) NOT NULL, " +
            COL_BUDGET_MONEY + " INTEGER NOT NULL, " +
            COL_BUDGET_STATUS + " TINYINT DEFAULT(1), " +
            COL_BUDGET_DESCRIPTION + " TEXT, " +
            COL_CREATED_AT + " DATETIME, " +
            COL_UPDATED_AT + " DATETIME )";

    //dinh nghia bang expense
    protected static final String TABLE_EXPENSE = "expense";
    protected static final String COL_EXPENSE_ID = "id";
    protected static final String COL_EXPENSE_NAME = "name";
    protected static final String COL_EXPENSE_MONEY = "money";
    protected static final String COL_EXPENSE_DESCRIPTION = "description";
    protected static final String COL_EXPENSE_CATEGORY = "category";
    protected static final String COL_EXPENSE_STATUS = "status_expense";

    private final String CREATE_TABLE_EXPENSE = " CREATE TABLE " +
            TABLE_EXPENSE + " ( " +
            COL_EXPENSE_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_EXPENSE_NAME + " VARCHAR(100) NOT NULL, " +
            COL_EXPENSE_MONEY + " INTEGER NOT NULL, " +
            COL_EXPENSE_STATUS + " TINYINT DEFAULT(1), " +
            COL_EXPENSE_DESCRIPTION + " TEXT, " +
            COL_EXPENSE_CATEGORY + " INTEGER NOT NULL, " +
            COL_CREATED_AT + " DATETIME, " +
            COL_UPDATED_AT + " DATETIME )";

    public DbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_BUDGET);
        db.execSQL(CREATE_TABLE_EXPENSE);
        // tao bang them vao day
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE);
            //xoa bo cac bang neu loi and create again
            onCreate(db);
        }

    }
}
