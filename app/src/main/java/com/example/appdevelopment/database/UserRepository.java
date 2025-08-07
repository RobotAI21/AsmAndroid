package com.example.appdevelopment.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class UserRepository extends DbHelper{

    public UserRepository(@Nullable Context context) {
        super(context);
    }
    //Insert account user
    public long saveUserAccount(
            String username,
            String password,
            String email,
            String phone
    ){
        //lay ngay thang hien tai
        @SuppressLint({"NewApi", "LocalSuppress"}) DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
        @SuppressLint({"NewApi", "LocalSuppress"}) ZonedDateTime zoneDt = ZonedDateTime.now();
        @SuppressLint({"NewApi", "LocalSuppress"}) String currentDate = dtf.format(zoneDt);

        ContentValues values = new ContentValues();
        values.put(DbHelper.COL_USERNAME, username);//save date into COL_USERNAME
        values.put(DbHelper.COL_PASSWORD, password);
        values.put(DbHelper.COL_EMAIL, email);
        values.put(DbHelper.COL_PHONE, phone);
        values.put(DbHelper.COL_ROLE, 0);
        values.put(DbHelper.COL_CREATED_AT, currentDate);
        SQLiteDatabase db = this.getWritableDatabase();
        long insert = db.insert(DbHelper.TABLE_USERS, null, values);
        db.close();
        return insert;
    }

    @SuppressLint("Range")
    public UserModel getInfoAccountByUsername(String username, String password){
        UserModel userAccount = new UserModel();
        try {
            SQLiteDatabase db = this.getReadableDatabase(); //cau lenh select
            //tao mang chua cot dl muon thao tac
            //select id,username,email,phone,role from user where username = ? and password = ?
            String[] cols = {DbHelper.COL_ID, DbHelper.COL_USERNAME, DbHelper.COL_EMAIL, DbHelper.COL_PHONE, DbHelper.COL_ROLE, DbHelper.COL_CREATED_AT};
            String condition = DbHelper.COL_USERNAME + " =? AND " + DbHelper.COL_PASSWORD + " =? ";
            String[] params = { username, password };
            Cursor data = db.query(DbHelper.TABLE_USERS, cols, condition, params, null, null, null);

            if(data.getCount()>0){
                //co data in table
                data.moveToFirst();
                //do data vao model
                userAccount.setId(data.getInt(data.getColumnIndex(DbHelper.COL_ID)));
                userAccount.setUsername(data.getString(data.getColumnIndex(DbHelper.COL_USERNAME)));
                userAccount.setEmail(data.getString(data.getColumnIndex(DbHelper.COL_EMAIL)));
                userAccount.setPhone(data.getString(data.getColumnIndex(DbHelper.COL_PHONE)));
                userAccount.setRole(data.getInt(data.getColumnIndex(DbHelper.COL_ROLE)));
                userAccount.setCreatedAt(data.getString(data.getColumnIndex(DbHelper.COL_CREATED_AT)));
            }
            data.close();
            db.close();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        //Log.i("DATA_USER", userAccount.getUsername());
        return userAccount;
    }

    public boolean updatePassword(int userId, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);

        int rows = db.update("users", values, "id = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rows > 0;
    }


    public UserModel getUserByUsernameAndPhone(String username, String phone) {
        Cursor cursor = this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + DbHelper.TABLE_USERS + " WHERE username = ? AND phone = ?",
                new String[]{username, phone}
        );

        if (cursor != null && cursor.moveToFirst()) {
            UserModel user = new UserModel();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("username")));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            user.setRole(cursor.getInt(cursor.getColumnIndexOrThrow("role")));
            user.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
            cursor.close();
            return user;
        }
        return null;
    }
}
