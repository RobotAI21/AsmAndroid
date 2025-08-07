package com.example.appdevelopment.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.appdevelopment.utils.Notification;
import com.example.appdevelopment.database.BudgetModel;
import com.example.appdevelopment.database.BudgetRepository;
import com.github.mikephil.charting.data.PieEntry;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseRepository {

    private DbHelper dbHelper;
    private final Context context;
    public ExpenseRepository(Context context) {
        this.dbHelper = new DbHelper(context); this.context = context;
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Modified saveExpense function to accept budgetId and userId
    public long saveExpense(String name, int money, String description, int status, int budgetId, int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COL_EXPENSE_NAME, name);
        values.put(DbHelper.COL_EXPENSE_MONEY, money);
        values.put(DbHelper.COL_EXPENSE_DESCRIPTION, description);
        values.put(DbHelper.COL_EXPENSE_STATUS, status);
        values.put(DbHelper.COL_EXPENSE_BUDGET_ID, budgetId); // Add budget_id
        values.put(DbHelper.COL_EXPENSE_USER_ID, userId); // Add user_id
        values.put(DbHelper.COL_CREATED_AT, getCurrentDateTime());
        values.put(DbHelper.COL_UPDATED_AT, getCurrentDateTime());
        long result = db.insert(DbHelper.TABLE_EXPENSE, null, values);
        db.close();
        return result;
    }
    
    // Overload method cũ để tương thích ngược
    public long saveExpense(String name, int money, String description, int status, int budgetId) {
        return saveExpense(name, money, description, status, budgetId, 1); // Default userId = 1
    }

    public List<ExpenseModel> getAllExpenses() {
        List<ExpenseModel> expenses = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DbHelper.TABLE_EXPENSE + " ORDER BY id DESC", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_NAME));
            int money = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_MONEY));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_DESCRIPTION));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_STATUS));
            String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_CREATED_AT));
            int budgetId = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_BUDGET_ID));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_USER_ID));

            expenses.add(new ExpenseModel(id, name, money, description, status, createdAt, budgetId, userId));
        }
        cursor.close();
        db.close();
        return expenses;
    }
    
    // Add method to get expenses by userId
    public List<ExpenseModel> getExpensesByUserId(int userId) {
        List<ExpenseModel> expenses = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DbHelper.TABLE_EXPENSE + " WHERE " + DbHelper.COL_EXPENSE_USER_ID + " = ? ORDER BY id DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_NAME));
            int money = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_MONEY));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_DESCRIPTION));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_STATUS));
            String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_CREATED_AT));
            int budgetId = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_BUDGET_ID));
            int expenseUserId = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_USER_ID));

            expenses.add(new ExpenseModel(id, name, money, description, status, createdAt, budgetId, expenseUserId));
        }
        cursor.close();
        db.close();
        return expenses;
    }

    public int deleteExpense(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DbHelper.TABLE_EXPENSE, DbHelper.COL_EXPENSE_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int updateExpense(int expenseId, String name, int money, String description, int status, int budgetId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COL_EXPENSE_NAME, name);
        values.put(DbHelper.COL_EXPENSE_MONEY, money);
        values.put(DbHelper.COL_EXPENSE_DESCRIPTION, description);
        values.put(DbHelper.COL_EXPENSE_STATUS, status);
        values.put(DbHelper.COL_EXPENSE_BUDGET_ID, budgetId);
        values.put(DbHelper.COL_UPDATED_AT, getCurrentDateTime());
        int result = db.update(DbHelper.TABLE_EXPENSE, values, DbHelper.COL_EXPENSE_ID + " =?", new String[]{String.valueOf(expenseId)});
        db.close();
        return result;
    }

    // Function to get total monthly expenses by budget and user
    public int getTotalMonthlyExpensesByBudgetAndUser(int budgetId, int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int total = 0;
        String query = "SELECT SUM(" + DbHelper.COL_EXPENSE_MONEY + ") as total FROM " +
                DbHelper.TABLE_EXPENSE + " WHERE strftime('%Y-%m', " + DbHelper.COL_CREATED_AT +
                ") = strftime('%Y-%m', 'now', 'localtime') AND " + DbHelper.COL_EXPENSE_BUDGET_ID + " = ? AND " + DbHelper.COL_EXPENSE_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(budgetId), String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
        }
        cursor.close();
        db.close();
        return total;
    }

    // Total monthly expenses for each budget of user
    public ArrayList<PieEntry> getPieEntriesByBudget(int userId) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT " + DbHelper.COL_EXPENSE_BUDGET_ID + ", SUM(" + DbHelper.COL_EXPENSE_MONEY + ") as total FROM " +
                DbHelper.TABLE_EXPENSE + " WHERE strftime('%Y-%m', " + DbHelper.COL_CREATED_AT +
                ") = strftime('%Y-%m', 'now', 'localtime') AND " + DbHelper.COL_EXPENSE_USER_ID + " = ? GROUP BY " + DbHelper.COL_EXPENSE_BUDGET_ID;
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        while (cursor.moveToNext()) {
            int budgetId = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_BUDGET_ID));
            float totalAmount = cursor.getFloat(cursor.getColumnIndexOrThrow("total"));
            // Get budget name from budgetId
            BudgetRepository budgetRepo = new BudgetRepository(context);
            BudgetModel budget = budgetRepo.getBudgetById(budgetId);
            String budgetName = (budget != null) ? budget.getNameBudget() : "Budget " + budgetId;
            entries.add(new PieEntry(totalAmount, budgetName));
        }
        cursor.close();
        db.close();
        return entries;
    }

    // Get expenses of a specific budget in current month
    public ArrayList<PieEntry> getExpensesByBudgetForCurrentMonthByUser(int budgetId, int userId) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT " + DbHelper.COL_EXPENSE_NAME + ", " + DbHelper.COL_EXPENSE_MONEY + " FROM " +
                DbHelper.TABLE_EXPENSE + " WHERE strftime('%Y-%m', " + DbHelper.COL_CREATED_AT +
                ") = strftime('%Y-%m', 'now', 'localtime') AND " + DbHelper.COL_EXPENSE_BUDGET_ID + " = ? AND " + DbHelper.COL_EXPENSE_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(budgetId), String.valueOf(userId)});
        while (cursor.moveToNext()) {
            String expenseName = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_NAME));
            float amount = cursor.getFloat(cursor.getColumnIndexOrThrow(DbHelper.COL_EXPENSE_MONEY));
            entries.add(new PieEntry(amount, expenseName));
        }
        cursor.close();
        db.close();
        return entries;
    }

}