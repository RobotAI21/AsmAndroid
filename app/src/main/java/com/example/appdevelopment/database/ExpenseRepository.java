package com.example.appdevelopment.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.Nullable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExpenseRepository extends DbHelper{

    public ExpenseRepository(@Nullable Context context) {
        super(context);
    }
    public long addNewExpense(String name, int money, String description, int category){
        //lay ngay thang hien tai
        @SuppressLint({"NewApi", "LocalSuppress"}) DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
        @SuppressLint({"NewApi", "LocalSuppress"}) ZonedDateTime zoneDt = ZonedDateTime.now();
        @SuppressLint({"NewApi", "LocalSuppress"}) String currentDate = dtf.format(zoneDt);
        ContentValues values = new ContentValues();
        values.put(DbHelper.COL_EXPENSE_NAME, name);
        values.put(DbHelper.COL_EXPENSE_MONEY, money);
        values.put(DbHelper.COL_EXPENSE_DESCRIPTION, description);
        values.put(DbHelper.COL_EXPENSE_CATEGORY, category);
        values.put(DbHelper.COL_EXPENSE_STATUS, 1);
        values.put(DbHelper.COL_CREATED_AT, currentDate);
        SQLiteDatabase db = this.getWritableDatabase();
        long insert = db.insert(DbHelper.TABLE_EXPENSE, null, values);
        db.close();
        return insert;
    }
    public List<ExpenseModel> getAllExpenses() {
        List<ExpenseModel> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DbHelper.TABLE_EXPENSE, null, null, null, null, null, DbHelper.COL_CREATED_AT + " DESC");
        if (cursor.moveToFirst()) {
            do {
                ExpenseModel expense = new ExpenseModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_MONEY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_CREATED_AT))
                );
                expenses.add(expense);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return expenses;
    }
    public int updateExpense(int id, String name, int money, String description, int category) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.COL_EXPENSE_NAME, name);
        values.put(DbHelper.COL_EXPENSE_MONEY, money);
        values.put(DbHelper.COL_EXPENSE_DESCRIPTION, description);
        values.put(DbHelper.COL_EXPENSE_CATEGORY, category);
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.update(DbHelper.TABLE_EXPENSE, values, DbHelper.COL_EXPENSE_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public int deleteExpense(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(DbHelper.TABLE_EXPENSE, DbHelper.COL_EXPENSE_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }
}
