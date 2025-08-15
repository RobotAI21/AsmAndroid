package com.example.appdevelopment.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

/**
 * Helper class để quản lý cơ sở dữ liệu SQLite
 * Tạo và quản lý các bảng: users, budget, expense
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String LOG = DbHelper.class.getName();
    protected static final String DB_NAME = "campus_expenses";
    protected static final int DB_VERSION = 7;

    // Định nghĩa các cột cho bảng users
    protected static final String TABLE_USERS = "users";
    protected static final String COL_ID = "id";
    protected static final String COL_USERNAME = "username";
    protected static final String COL_PASSWORD = "password";
    protected static final String COL_EMAIL = "email";
    protected static final String COL_PHONE = "phone";
    protected static final String COL_ROLE = "role";
    protected static final String COL_CREATED_AT = "created_at";
    protected static final String COL_UPDATED_AT = "updated_at";

    // Câu lệnh tạo bảng users
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

    // Định nghĩa các cột cho bảng budget
    protected static final String TABLE_BUDGET = "budget";
    protected static final String COL_BUDGET_ID = "id";
    protected static final String COL_BUDGET_NAME = "name";
    protected static final String COL_BUDGET_MONEY = "money";
    protected static final String COL_BUDGET_DESCRIPTION = "description";
    protected static final String COL_BUDGET_STATUS = "status_budget";
    protected static final String COL_BUDGET_USER_ID = "user_id";

    // Câu lệnh tạo bảng budget
    private final String CREATE_TABLE_BUDGET = " CREATE TABLE " +
            TABLE_BUDGET + " ( " +
            COL_BUDGET_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_BUDGET_NAME + " VARCHAR(100) NOT NULL, " +
            COL_BUDGET_MONEY + " INTEGER NOT NULL, " +
            COL_BUDGET_STATUS + " TINYINT DEFAULT(1), " +
            COL_BUDGET_DESCRIPTION + " TEXT, " +
            COL_BUDGET_USER_ID + " INTEGER NOT NULL, " +
            COL_CREATED_AT + " DATETIME, " +
            COL_UPDATED_AT + " DATETIME, " +
            "FOREIGN KEY(" + COL_BUDGET_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID + "))";

    // Định nghĩa các cột cho bảng expense
    protected static final String TABLE_EXPENSE = "expense";
    protected static final String COL_EXPENSE_ID = "id";
    protected static final String COL_EXPENSE_NAME = "name";
    protected static final String COL_EXPENSE_MONEY = "money";
    protected static final String COL_EXPENSE_DESCRIPTION = "description";
    protected static final String COL_EXPENSE_STATUS = "status_expense";
    protected static final String COL_EXPENSE_BUDGET_ID = "budget_id";
    protected static final String COL_EXPENSE_USER_ID = "user_id";

    // Câu lệnh tạo bảng expense
    private final String CREATE_TABLE_EXPENSE = " CREATE TABLE " +
            TABLE_EXPENSE + " ( " +
            COL_EXPENSE_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_EXPENSE_NAME + " VARCHAR(100) NOT NULL, " +
            COL_EXPENSE_MONEY + " INTEGER NOT NULL, " +
            COL_EXPENSE_STATUS + " TINYINT DEFAULT(1), " +
            COL_EXPENSE_DESCRIPTION + " TEXT, " +
            COL_EXPENSE_BUDGET_ID + " INTEGER, " +
            COL_EXPENSE_USER_ID + " INTEGER NOT NULL, " +
            COL_CREATED_AT + " DATETIME, " +
            COL_UPDATED_AT + " DATETIME, " +
            "FOREIGN KEY(" + COL_EXPENSE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID + "), " +
            "FOREIGN KEY(" + COL_EXPENSE_BUDGET_ID + ") REFERENCES " + TABLE_BUDGET + "(" + COL_BUDGET_ID + "))";

    private final Context context;
    
    /**
     * Constructor của DbHelper
     * @param context Context của ứng dụng
     */
    public DbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION); 
        this.context = context;
    }
    
    /**
     * Lấy context của ứng dụng
     * @return Context
     */
    public Context getContext() {
        return context;
    }
    
    /**
     * Phương thức được gọi khi tạo cơ sở dữ liệu lần đầu
     * Tạo các bảng cần thiết
     * @param db SQLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_BUDGET);
        db.execSQL(CREATE_TABLE_EXPENSE);
    }

    /**
     * Phương thức được gọi khi nâng cấp cơ sở dữ liệu
     * Xóa và tạo lại tất cả các bảng
     * @param db SQLiteDatabase
     * @param oldVersion Phiên bản cũ
     * @param newVersion Phiên bản mới
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE);
            onCreate(db);
        }
    }
    
    /**
     * Kiểm tra email có tồn tại trong cơ sở dữ liệu không
     * @param email Email cần kiểm tra
     * @return true nếu email tồn tại, false nếu không
     */
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + " = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    /**
     * Cập nhật mật khẩu theo email
     * @param email Email của người dùng
     * @param newPassword Mật khẩu mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updatePassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PASSWORD, newPassword);
        values.put(COL_UPDATED_AT, System.currentTimeMillis());
        int rows = db.update(TABLE_USERS, values, COL_EMAIL + "=?", new String[]{email});
        return rows > 0;
    }

    /**
     * Lấy thông tin người dùng theo ID
     * @param userId ID của người dùng
     * @return Cursor chứa thông tin người dùng
     */
    public Cursor getUserInfoById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    /**
     * Kiểm tra mật khẩu cũ có đúng không
     * @param userId ID của người dùng
     * @param oldPassword Mật khẩu cũ
     * @return true nếu mật khẩu đúng, false nếu sai
     */
    public boolean checkPassword(int userId, String oldPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + COL_ID + " = ? AND " + COL_PASSWORD + " = ?",
                new String[]{String.valueOf(userId), oldPassword}
        );
        boolean match = cursor.getCount() > 0;
        cursor.close();
        return match;
    }

    /**
     * Cập nhật mật khẩu theo ID người dùng
     * @param userId ID của người dùng
     * @param newPassword Mật khẩu mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updatePasswordById(int userId, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PASSWORD, newPassword);
        values.put(COL_UPDATED_AT, System.currentTimeMillis());

        int rows = db.update(TABLE_USERS, values, COL_ID + "=?", new String[]{String.valueOf(userId)});
        return rows > 0;
    }
}