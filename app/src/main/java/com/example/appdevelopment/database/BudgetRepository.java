package com.example.appdevelopment.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BudgetRepository {

    private DbHelper dbHelper;

    public BudgetRepository(Context context) {
        this.dbHelper = new DbHelper(context);
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    public long saveBudget(String name, int money, String description, int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COL_BUDGET_NAME, name);
        values.put(DbHelper.COL_BUDGET_MONEY, money);
        values.put(DbHelper.COL_BUDGET_DESCRIPTION, description);
        values.put(DbHelper.COL_BUDGET_USER_ID, userId); // Add userId
        values.put(DbHelper.COL_CREATED_AT, getCurrentDateTime());
        values.put(DbHelper.COL_UPDATED_AT, getCurrentDateTime());
        long result = db.insert(DbHelper.TABLE_BUDGET, null, values);
        db.close();
        return result;
    }
    
    // Overload method cũ để tương thích ngược
    public long saveBudget(String name, int money, String description) {
        return saveBudget(name, money, description, 1); // Default userId = 1
    }

    // Trong file: database/BudgetRepository.java

    public List<BudgetModel> getAllBudgets() {
        List<BudgetModel> budgets = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DbHelper.TABLE_BUDGET + " ORDER BY id DESC", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_NAME));
            int money = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_MONEY));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_DESCRIPTION));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_STATUS));

            // SỬA 1: Đọc thêm 2 cột còn lại từ database
            String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_CREATED_AT));
            String updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_UPDATED_AT));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_USER_ID));

            // FIX 2: Call new constructor with all 8 parameters in correct order.
            budgets.add(new BudgetModel(id, name, money, description, status, createdAt, updatedAt, userId));
        }
        cursor.close();
        db.close();
        return budgets;
    }
    
    // Add method to get budgets by userId
    public List<BudgetModel> getBudgetsByUserId(int userId) {
        List<BudgetModel> budgets = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DbHelper.TABLE_BUDGET + " WHERE " + DbHelper.COL_BUDGET_USER_ID + " = ? ORDER BY id DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_NAME));
            int money = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_MONEY));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_DESCRIPTION));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_STATUS));
            String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_CREATED_AT));
            String updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_UPDATED_AT));
            int budgetUserId = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_USER_ID));

            budgets.add(new BudgetModel(id, name, money, description, status, createdAt, updatedAt, budgetUserId));
        }
        cursor.close();
        db.close();
        return budgets;
    }


    public int deleteBudget(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(DbHelper.TABLE_BUDGET, DbHelper.COL_BUDGET_ID + " =?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public int updateBudget(int budgetId, String name, int money, String description) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COL_BUDGET_NAME, name);
        values.put(DbHelper.COL_BUDGET_MONEY, money);
        values.put(DbHelper.COL_BUDGET_DESCRIPTION, description);
        values.put(DbHelper.COL_UPDATED_AT, getCurrentDateTime());
        return db.update(DbHelper.TABLE_BUDGET, values, DbHelper.COL_BUDGET_ID + " =?", new String[]{String.valueOf(budgetId)});
    }

    /**
     * This function calculates the total amount of all budgets.
     */
    public int getTotalBudgetAmount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int total = 0;
        String query = "SELECT SUM(" + DbHelper.COL_BUDGET_MONEY + ") as total FROM " + DbHelper.TABLE_BUDGET;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
        }
        cursor.close();
        db.close();
        return total;
    }
    // This function gets the budget amount by ID
    public int getBudgetAmountById(int budgetId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int amount = 0;
        Cursor cursor = db.rawQuery("SELECT " + DbHelper.COL_BUDGET_MONEY + " FROM " + DbHelper.TABLE_BUDGET + " WHERE " + DbHelper.COL_BUDGET_ID + " = ?", new String[]{String.valueOf(budgetId)});
        if (cursor.moveToFirst()) {
            amount = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_MONEY));
        }
        cursor.close();
        db.close();
        return amount;
    }

    // This function gets budget by ID
    public BudgetModel getBudgetById(int budgetId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        BudgetModel budget = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + DbHelper.TABLE_BUDGET + " WHERE " + DbHelper.COL_BUDGET_ID + " = ?", new String[]{String.valueOf(budgetId)});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_NAME));
            int money = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_MONEY));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_DESCRIPTION));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_STATUS));
            String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_CREATED_AT));
            String updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_UPDATED_AT));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_USER_ID));
            budget = new BudgetModel(id, name, money, description, status, createdAt, updatedAt, userId);
        }
        cursor.close();
        db.close();
        return budget;
    }
}