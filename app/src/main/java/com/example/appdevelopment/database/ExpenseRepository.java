package com.example.appdevelopment.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.github.mikephil.charting.data.PieEntry;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseRepository {

    private DbHelper dbHelper;

    public ExpenseRepository(Context context) {
        this.dbHelper = new DbHelper(context);
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Sửa hàm saveExpense để nhận thêm budgetId
    public long saveExpense(String name, int money, String description, int category, int budgetId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COL_EXPENSE_NAME, name);
        values.put(DbHelper.COL_EXPENSE_MONEY, money);
        values.put(DbHelper.COL_EXPENSE_DESCRIPTION, description);
        values.put(DbHelper.COL_EXPENSE_CATEGORY, category);
        values.put(DbHelper.COL_EXPENSE_BUDGET_ID, budgetId); // Thêm budget_id
        values.put(DbHelper.COL_CREATED_AT, getCurrentDateTime());
        values.put(DbHelper.COL_UPDATED_AT, getCurrentDateTime());
        long result = db.insert(DbHelper.TABLE_EXPENSE, null, values);
        db.close();
        return result;
    }

    // Sửa hàm updateExpense để nhận thêm budgetId
    public int updateExpense(int expenseId, String name, int money, String description, int category, int budgetId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COL_EXPENSE_NAME, name);
        values.put(DbHelper.COL_EXPENSE_MONEY, money);
        values.put(DbHelper.COL_EXPENSE_DESCRIPTION, description);
        values.put(DbHelper.COL_EXPENSE_CATEGORY, category);
        values.put(DbHelper.COL_EXPENSE_BUDGET_ID, budgetId); // Thêm budget_id
        values.put(DbHelper.COL_UPDATED_AT, getCurrentDateTime());
        return db.update(DbHelper.TABLE_EXPENSE, values, DbHelper.COL_EXPENSE_ID + " =?", new String[]{String.valueOf(expenseId)});
    }

    // Sửa hàm getAllExpenses để lấy thêm budgetId
    public List<ExpenseModel> getAllExpenses() {
        List<ExpenseModel> expenses = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DbHelper.TABLE_EXPENSE + " ORDER BY id DESC", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_NAME));
            int money = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_MONEY));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_DESCRIPTION));
            int category = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_CATEGORY));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_STATUS));
            String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_CREATED_AT));
            int budgetId = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_BUDGET_ID));

            expenses.add(new ExpenseModel(id, name, money, description, category, status, createdAt, budgetId));
        }
        cursor.close();
        db.close();
        return expenses;
    }

    public int deleteExpense(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DbHelper.TABLE_EXPENSE, DbHelper.COL_EXPENSE_ID + "=?", new String[]{String.valueOf(id)});
    }

    // Hàm mới: Lấy tổng chi tiêu của tháng theo một ngân sách cụ thể
    public int getTotalMonthlyExpensesByBudget(int budgetId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int total = 0;
        String query = "SELECT SUM(" + DbHelper.COL_EXPENSE_MONEY + ") as total FROM " +
                DbHelper.TABLE_EXPENSE + " WHERE strftime('%Y-%m', " + DbHelper.COL_CREATED_AT +
                ") = strftime('%Y-%m', 'now', 'localtime') AND " + DbHelper.COL_EXPENSE_BUDGET_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(budgetId)});
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
        }
        cursor.close();
        db.close();
        return total;
    }

    // Hàm mới: Lấy dữ liệu biểu đồ của tháng theo một ngân sách cụ thể
    public ArrayList<PieEntry> getSpendingByCategoryForCurrentMonthByBudget(int budgetId) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        String[] categoryNames = {"Ăn uống", "Di chuyển", "Mua sắm", "Giải trí", "Khác"};
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT " + DbHelper.COL_EXPENSE_CATEGORY + ", SUM(" + DbHelper.COL_EXPENSE_MONEY + ") as total FROM " +
                DbHelper.TABLE_EXPENSE + " WHERE strftime('%Y-%m', " + DbHelper.COL_CREATED_AT +
                ") = strftime('%Y-%m', 'now', 'localtime') AND " + DbHelper.COL_EXPENSE_BUDGET_ID + " = ? GROUP BY " + DbHelper.COL_EXPENSE_CATEGORY;
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(budgetId)});
        while (cursor.moveToNext()) {
            int categoryIndex = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_CATEGORY));
            float totalAmount = cursor.getFloat(cursor.getColumnIndexOrThrow("total"));
            String categoryName = "Unknown";
            if (categoryIndex >= 0 && categoryIndex < categoryNames.length) {
                categoryName = categoryNames[categoryIndex];
            }
            entries.add(new PieEntry(totalAmount, categoryName));
        }
        cursor.close();
        db.close();
        return entries;
    }
}