package com.e_dash;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    // Database elements
    private static final String DATABASE_NAME = "Edash.db";
    private static final int DATABASE_VERSION = 3;

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

    private static final String COLUMN_DATE = "date";

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
                COLUMN_SOLD + " INTEGER, " +
                COLUMN_DATE + " TEXT)";
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

        // Get current date in format YYYY-MM-DD
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        values.put(COLUMN_DATE, currentDate); // add date to values

        // Insert or update if product exists (use CONFLICT_REPLACE to avoid duplicates)
        long result = db.insertWithOnConflict(SALES_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
//        db.close();
        return result != -1;
    }


    //Update sales
    public boolean updateSoldValue(String name, int sold) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SOLD, sold);
        int rowsAffected = db.update(SALES_TABLE, values, COLUMN_PRODUCT_NAME + " = ?", new String[]{name});
        db.close();
        return rowsAffected > 0;
    }




    public boolean productExists(String productName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + SALES_TABLE + " WHERE LOWER(TRIM(" + COLUMN_PRODUCT_NAME + ")) = ?";
        Cursor cursor = db.rawQuery(query, new String[]{productName.trim().toLowerCase()});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }



    // Fetch Sales Data for PieChart
//    public Cursor getSalesData() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT " + COLUMN_PRODUCT_NAME + ", " + COLUMN_SOLD + " FROM " + SALES_TABLE, null);
//        return cursor;
//    }

    // Filter data for pie chart

    public Cursor getSalesData(String filter) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query;

        if (filter.equals("This Month")) {
            query = "SELECT product_name, SUM(sold) FROM sales " +
                    "WHERE strftime('%Y-%m', date) = strftime('%Y-%m', 'now') " +
                    "GROUP BY product_name";
        } else if (filter.equals("This Week")) {
            query = "SELECT product_name, SUM(sold) FROM sales " +
                    "WHERE strftime('%W', date) = strftime('%W', 'now') " +
                    "AND strftime('%Y', date) = strftime('%Y', 'now') " +
                    "GROUP BY product_name";
        } else { // All Time
            query = "SELECT product_name, SUM(sold) FROM sales GROUP BY product_name";
        }

        return db.rawQuery(query, null);
    }

    //retrieve user detail
    public Cursor getUserDetails(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT username, email FROM user WHERE username=?", new String[]{username});
    }

    //update user detail
    public boolean updateUserDetails(String oldUsername, String newEmail,String newUsername){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", newUsername);
        values.put("email", newEmail);

        int rowsAffected = db.update("user", values, "username=?", new String[]{oldUsername});
        return rowsAffected > 0;
    }




}
