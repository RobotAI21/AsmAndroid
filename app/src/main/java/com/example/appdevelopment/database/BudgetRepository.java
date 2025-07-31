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

public class BudgetRepository extends DbHelper{

    public BudgetRepository(@Nullable Context context) {
        super(context);
    }

    //tao moi budget luu vao db
    public long addNewBudget(String name, int money, String description){
        //lay ngay thang hien tai
        @SuppressLint({"NewApi", "LocalSuppress"}) DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
        @SuppressLint({"NewApi", "LocalSuppress"}) ZonedDateTime zoneDt = ZonedDateTime.now();
        @SuppressLint({"NewApi", "LocalSuppress"}) String currentDate = dtf.format(zoneDt);
        ContentValues values = new ContentValues();
        values.put(DbHelper.COL_BUDGET_NAME, name);
        values.put(DbHelper.COL_BUDGET_MONEY, money);
        values.put(DbHelper.COL_BUDGET_DESCRIPTION, description);
        values.put(DbHelper.COL_BUDGET_STATUS, 1);
        values.put(DbHelper.COL_CREATED_AT, currentDate);
        SQLiteDatabase db = this.getWritableDatabase(); //ghi du lieu
        long insert = db.insert(DbHelper.TABLE_BUDGET, null, values);
        db.close();
        return insert;
    }
    public List<BudgetModel> getAllBudgets() {
        List<BudgetModel> budgets = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DbHelper.TABLE_BUDGET, null, null, null, null, null, DbHelper.COL_CREATED_AT + " DESC");
        if (cursor.moveToFirst()) {
            do {
                BudgetModel budget = new BudgetModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_MONEY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_CREATED_AT))
                );
                budgets.add(budget);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return budgets;
    }
    public int updateBudget(int id, String name, int money, String description) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.COL_BUDGET_NAME, name);
        values.put(DbHelper.COL_BUDGET_MONEY, money);
        values.put(DbHelper.COL_BUDGET_DESCRIPTION, description);
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.update(DbHelper.TABLE_BUDGET, values, DbHelper.COL_BUDGET_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }
    public int deleteBudget(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(DbHelper.TABLE_BUDGET, DbHelper.COL_BUDGET_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

}
