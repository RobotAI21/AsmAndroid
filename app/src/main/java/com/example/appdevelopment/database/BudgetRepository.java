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

    public long saveBudget(String name, int money, String description) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COL_BUDGET_NAME, name);
        values.put(DbHelper.COL_BUDGET_MONEY, money);
        values.put(DbHelper.COL_BUDGET_DESCRIPTION, description);
        values.put(DbHelper.COL_CREATED_AT, getCurrentDateTime());
        values.put(DbHelper.COL_UPDATED_AT, getCurrentDateTime());
        long result = db.insert(DbHelper.TABLE_BUDGET, null, values);
        db.close();
        return result;
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

            // SỬA 2: Gọi constructor mới với đủ 7 tham số theo đúng thứ tự.
            // Dòng này sẽ hết lỗi và khớp hoàn toàn với BudgetModel mới.
            budgets.add(new BudgetModel(id, name, money, description, status, createdAt, updatedAt));
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
     * Hàm này tính tổng tiền của tất cả các ngân sách.
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
}