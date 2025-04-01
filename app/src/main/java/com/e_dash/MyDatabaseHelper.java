package com.e_dash;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    // Database elements
    private static final String DATABASE_NAME = "Edash.db";
    private static final int DATABASE_VERSION = 2;

    // User table
    private static final String TABLE_NAME = "user";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    // Sales table
    private static final String SALES_TABLE = "sales";
    private static final String COLUMN_SALES_ID = "sales_id";
    private static final String COLUMN_PRODUCT_NAME = "product_name";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_SOLD = "sold";

    // Constructor
    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create User Table
        String userTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(userTableQuery);

        // Create Sales Table
        String salesTableQuery = "CREATE TABLE " + SALES_TABLE + " (" +
                COLUMN_SALES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PRODUCT_NAME + " TEXT, " +
                COLUMN_PRICE + " REAL, " +
                COLUMN_QUANTITY + " INTEGER, " +
                COLUMN_SOLD + " INTEGER)";
        db.execSQL(salesTableQuery);

        android.util.Log.d("DatabaseHelper", "Database created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Instead of dropping tables, consider migrating data properly
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SALES_TABLE);
        onCreate(db);
    }

    // Insert User
    public boolean insertUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result != -1;
    }

    // Check User Credentials
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?", new String[]{username, password});

        boolean exist = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exist;
    }

    // Get User Info
    public Cursor getUserInfo(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT username, email FROM " + TABLE_NAME + " WHERE username = ?", new String[]{username});
        return cursor;
    }

    // Insert Sales Data
    public boolean insertSale(String productName, double price, int quantity, int sold) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_PRODUCT_NAME, productName);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_SOLD, sold);

        long result = db.insert(SALES_TABLE, null, values);
        db.close();
        return result != -1;
    }

    //Update sales
    public boolean updateSale(String productName, double price, int quantity, int sold){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_PRODUCT_NAME, productName);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_SOLD, sold);

        int rowsAffected = db.update(SALES_TABLE, values, COLUMN_PRODUCT_NAME + " =?", new String[]{productName});
        db.close();
        return rowsAffected > 0;
    }

    // Fetch Sales Data for PieChart
    public Cursor getSalesData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_PRODUCT_NAME + ", " + COLUMN_SOLD + " FROM " + SALES_TABLE, null);
        return cursor;
    }
}
