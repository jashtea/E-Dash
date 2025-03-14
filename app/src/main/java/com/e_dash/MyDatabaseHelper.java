package com.e_dash;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyDatabaseHelper extends SQLiteOpenHelper {

    // database elements
    private static final String DATABASE_NAME = "Edash.db";
    private static final int DATABASE_VERSION = 1;
    private static  final String TABLE_NAME = "user";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private  static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    // Create Database

    public MyDatabaseHelper( Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    // Create Table Query
    String query = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_USERNAME + " TEXT, " +
            COLUMN_EMAIL + " TEXT UNIQUE, " +
            COLUMN_PASSWORD + " TEXT)";
    // Execute Query
    db.execSQL(query);
    android.util.Log.d("DatabaseHelper", "Database created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }
    public boolean insertUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_NAME, null, values);
        if (result == -1) {
            android.util.Log.d("Db_Debug", "User registration failed");
        }else{
            android.util.Log.d("Db_Debug", "User registered successfully! D:" +
                    result);
        }

        db.close();
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?", new String[]{username, password});

        boolean exist = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exist;
    }

    // To get the User Details
    public Cursor  getUserInfo(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT username, email FROM user WHERE username = ?", new String[]{username});

    }
}
