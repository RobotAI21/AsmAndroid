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

/**
 * Repository class để quản lý các thao tác với bảng budget trong cơ sở dữ liệu
 * Cung cấp các phương thức CRUD cho ngân sách
 */
public class BudgetRepository {

    private DbHelper dbHelper;

    /**
     * Constructor của BudgetRepository
     * @param context Context của ứng dụng
     */
    public BudgetRepository(Context context) {
        this.dbHelper = new DbHelper(context);
    }

    /**
     * Phương thức lấy thời gian hiện tại theo định dạng chuẩn
     * @return Chuỗi thời gian hiện tại
     */
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * Phương thức lưu ngân sách mới vào cơ sở dữ liệu
     * @param name Tên ngân sách
     * @param money Số tiền ngân sách
     * @param description Mô tả ngân sách
     * @param userId ID của người dùng sở hữu ngân sách
     * @return ID của ngân sách mới được tạo, -1 nếu thất bại
     */
    public long saveBudget(String name, int money, String description, int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COL_BUDGET_NAME, name);
        values.put(DbHelper.COL_BUDGET_MONEY, money);
        values.put(DbHelper.COL_BUDGET_DESCRIPTION, description);
        values.put(DbHelper.COL_BUDGET_USER_ID, userId);
        values.put(DbHelper.COL_CREATED_AT, getCurrentDateTime());
        values.put(DbHelper.COL_UPDATED_AT, getCurrentDateTime());
        long result = db.insert(DbHelper.TABLE_BUDGET, null, values);
        db.close();
        return result;
    }
    
    /**
     * Phương thức lưu ngân sách mới (overload để tương thích ngược)
     * @param name Tên ngân sách
     * @param money Số tiền ngân sách
     * @param description Mô tả ngân sách
     * @return ID của ngân sách mới được tạo, -1 nếu thất bại
     */
    public long saveBudget(String name, int money, String description) {
        return saveBudget(name, money, description, 1); // Default userId = 1
    }

    /**
     * Phương thức lấy tất cả ngân sách từ cơ sở dữ liệu
     * @return Danh sách tất cả ngân sách
     */
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
            String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_CREATED_AT));
            String updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COL_UPDATED_AT));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COL_BUDGET_USER_ID));

            budgets.add(new BudgetModel(id, name, money, description, status, createdAt, updatedAt, userId));
        }
        cursor.close();
        db.close();
        return budgets;
    }
    
    /**
     * Phương thức lấy danh sách ngân sách theo ID người dùng
     * @param userId ID của người dùng
     * @return Danh sách ngân sách của người dùng
     */
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

    /**
     * Phương thức kiểm tra xem ngân sách có chứa expense nào không
     * @param budgetId ID của ngân sách cần kiểm tra
     * @return true nếu có expense, false nếu không có
     */
    public boolean hasExpenses(int budgetId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + DbHelper.TABLE_EXPENSE + " WHERE " + DbHelper.COL_EXPENSE_BUDGET_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(budgetId)});
        
        boolean hasExpenses = false;
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            hasExpenses = count > 0;
        }
        
        cursor.close();
        db.close();
        return hasExpenses;
    }

    /**
     * Phương thức đếm số lượng expense của một ngân sách
     * @param budgetId ID của ngân sách
     * @return Số lượng expense
     */
    public int getExpenseCount(int budgetId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + DbHelper.TABLE_EXPENSE + " WHERE " + DbHelper.COL_EXPENSE_BUDGET_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(budgetId)});
        
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        
        cursor.close();
        db.close();
        return count;
    }

    /**
     * Phương thức xóa ngân sách theo ID
     * Đồng thời xóa tất cả expense liên quan đến ngân sách này
     * @param id ID của ngân sách cần xóa
     * @return Số dòng bị ảnh hưởng
     */
    public int deleteBudget(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        // Bắt đầu transaction để đảm bảo tính toàn vẹn dữ liệu
        db.beginTransaction();
        try {
            // Xóa tất cả expense liên quan đến budget này trước
            int expenseRows = db.delete(DbHelper.TABLE_EXPENSE, 
                DbHelper.COL_EXPENSE_BUDGET_ID + " =?", 
                new String[]{String.valueOf(id)});
            
            // Sau đó xóa budget
            int budgetRows = db.delete(DbHelper.TABLE_BUDGET, 
                DbHelper.COL_BUDGET_ID + " =?", 
                new String[]{String.valueOf(id)});
            
            // Commit transaction nếu thành công
            db.setTransactionSuccessful();
            
            // Trả về tổng số dòng bị ảnh hưởng
            return budgetRows;
        } finally {
            // Kết thúc transaction
            db.endTransaction();
        }
    }

    /**
     * Phương thức cập nhật thông tin ngân sách
     * @param budgetId ID của ngân sách cần cập nhật
     * @param name Tên mới
     * @param money Số tiền mới
     * @param description Mô tả mới
     * @return Số dòng bị ảnh hưởng
     */
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
     * Phương thức tính tổng số tiền của tất cả ngân sách
     * @return Tổng số tiền ngân sách
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
    
    /**
     * Phương thức lấy số tiền ngân sách theo ID
     * @param budgetId ID của ngân sách
     * @return Số tiền ngân sách
     */
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

    /**
     * Phương thức lấy thông tin ngân sách theo ID
     * @param budgetId ID của ngân sách
     * @return BudgetModel chứa thông tin ngân sách, null nếu không tìm thấy
     */
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