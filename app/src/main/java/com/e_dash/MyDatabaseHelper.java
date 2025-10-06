    package com.e_dash;

    import android.content.ContentValues;
    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;

    import com.github.mikephil.charting.data.Entry;

    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.Date;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Locale;
    import java.util.Map;
    import java.util.Random;

    public class MyDatabaseHelper extends SQLiteOpenHelper {

        // Database elements
        private static final String DATABASE_NAME = "Edash.db";
        private static final int DATABASE_VERSION = 4;

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

        // Sales Revenue
        private static final String SALES_REVENUE_TABLE = "sales_revenue";
        private static final String COLUMN_REVENUE_ID = "revenue_id";
        private static final String COLUMN_TOTAL_REVENUE = "total_revenue";
        private static final String COLUMN_DATE_SALES = "date";

        // Expenses

        private static final String EXPENSES_TABLE = "expenses_table";
        private static final String EXPENSES_ID = "expenses_id";
        private static final String EXPENSES_DESCRIPTION = "expenses_desc";
        private static final String EXPENSES_AMOUNT = "amount";


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

            // Sales revenue table
            String revenueTableQuery = "CREATE TABLE " + SALES_REVENUE_TABLE + " (" +
                    COLUMN_REVENUE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TOTAL_REVENUE + " REAL, " +
                    COLUMN_DATE_SALES + " TEXT UNIQUE)";
            db.execSQL(revenueTableQuery);

            // Expenses amount table
            String CREATE_EXPENSES_TABLE = "CREATE TABLE " + EXPENSES_TABLE + " (" +
                    EXPENSES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    EXPENSES_DESCRIPTION + " TEXT, " +
                    EXPENSES_AMOUNT + " REAL, " +
                    "date TEXT)";   // add date if you want filtering by date

            db.execSQL(CREATE_EXPENSES_TABLE);



            //        insertSampleDailySales(db);

            android.util.Log.d("DatabaseHelper", "Database created successfully");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Instead of dropping tables, consider migrating data properly
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + SALES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SALES_REVENUE_TABLE);
            onCreate(db);
        }

        //Insert Sales Revenue

        public boolean insertRevenue(String productName, double totalRevenue, String date) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_TOTAL_REVENUE, totalRevenue);
            values.put(COLUMN_DATE_SALES, date);

            long result = db.insert(SALES_REVENUE_TABLE, null, values);
            return result != -1;
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
        // Insert Sales Data (for today's date)
        public boolean insertSale(String productName, double price, int quantity, int sold) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COLUMN_PRODUCT_NAME, productName);
            values.put(COLUMN_PRICE, price);
            values.put(COLUMN_QUANTITY, quantity);
            values.put(COLUMN_SOLD, sold);

            // Get current date in format YYYY-MM-DD
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            values.put(COLUMN_DATE, currentDate); // assign current date

            // Insert without replacing previous rows (allow same product on different dates)
            long result = db.insert(SALES_TABLE, null, values);

            return result != -1;
        }


        //Update sales
        // Update sold value for a specific product on a specific date
        public boolean updateSoldValue(String name, int sold, String date) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_SOLD, sold);

            int rowsAffected = db.update(SALES_TABLE, values,
                    COLUMN_PRODUCT_NAME + " = ? AND " + COLUMN_DATE + " = ?",
                    new String[]{name, date});

            return rowsAffected > 0;
        }



        public boolean productExists(String productName, String date) {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM " + SALES_TABLE +
                    " WHERE LOWER(TRIM(" + COLUMN_PRODUCT_NAME + ")) = ? AND " + COLUMN_DATE + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{productName.trim().toLowerCase(), date});
            boolean exists = cursor.getCount() > 0;
            cursor.close();

            return exists;
        }



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


        // Filter data in Line Chart weekly
        public List<Entry> getWeeklySales(List<String> dayLabels) {
            List<Entry> entries = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();

            Map<String, Float> salesMap = new HashMap<>();

            String query = "SELECT " + COLUMN_DATE + ", SUM(" + COLUMN_SOLD + ") as total_sales " +
                    "FROM " + SALES_TABLE + " " +
                    "WHERE strftime('%W', " + COLUMN_DATE + ") = strftime('%W', 'now') " +
                    "AND strftime('%Y', " + COLUMN_DATE + ") = strftime('%Y', 'now') " +
                    "GROUP BY " + COLUMN_DATE;

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    String dateStr = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                    float total = cursor.getFloat(cursor.getColumnIndex("total_sales"));

                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date date = sdf.parse(dateStr);
                        String dayName = new SimpleDateFormat("EEE", Locale.getDefault()).format(date);
                        salesMap.put(dayName, total);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();

            String[] daysOfWeek = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            for (int i = 0; i < daysOfWeek.length; i++) {
                String day = daysOfWeek[i];
                dayLabels.add(day);
                float total = salesMap.getOrDefault(day, 0f);
                entries.add(new Entry(i, total));
            }

            return entries;
        }


        // Filter Line Chart monthly
        public List<Entry> getMonthlySales() {
            List<Entry> entries = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();

            float[] monthlyTotals = new float[12]; // default 0

            Cursor cursor = db.rawQuery(
                    "SELECT CAST(strftime('%m', " + COLUMN_DATE + ") AS INTEGER) AS month, " +
                            "SUM(" + COLUMN_SOLD + ") " +
                            "FROM " + SALES_TABLE +
                            " WHERE strftime('%Y', " + COLUMN_DATE + ") = strftime('%Y', 'now') " +
                            "GROUP BY month ORDER BY month", null);

            while (cursor.moveToNext()) {
                int monthNum = cursor.getInt(0); // Now it's guaranteed integer 1-12
                if (monthNum >= 1 && monthNum <= 12) {
                    monthlyTotals[monthNum - 1] = cursor.getFloat(1);
                }
            }
            cursor.close();

            // Always fill 12 months (Jan–Dec)
            for (int i = 0; i < 12; i++) {
                entries.add(new Entry(i, monthlyTotals[i]));
            }

            return entries;
        }

        // Save the sales total in sales_revenue table

        public void saveAllDailyTotals(){
            SQLiteDatabase db = this.getWritableDatabase();

            Cursor dateCursor = db.rawQuery("SELECT DISTINCT date FROM sales", null);
            if (dateCursor.moveToFirst()) {
                do {
                    String date = dateCursor.getString(dateCursor.getColumnIndexOrThrow("date"));

                    Cursor totalCursor = db.rawQuery(
                            "SELECT SUM(price * sold) as total FROM sales WHERE date = ?",
                            new String[]{date}
                    );

                    double total = 0;

                    if (totalCursor.moveToFirst()){
                        total = totalCursor.getDouble(totalCursor.getColumnIndexOrThrow("total"));
                    }

                    totalCursor.close();


                    ContentValues values = new ContentValues();
                    values.put(COLUMN_TOTAL_REVENUE, total);
                    values.put(COLUMN_DATE_SALES, date);

                    db.insertWithOnConflict(SALES_REVENUE_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);

                }while (dateCursor.moveToNext());
            }

            dateCursor.close();
            db.close();
        }

        public boolean addExpense(String desc, double amount, String date) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(EXPENSES_DESCRIPTION, desc);
            values.put(EXPENSES_AMOUNT, amount);
            values.put("date", date);

            long result = db.insert(EXPENSES_TABLE, null, values);
            return result != -1; // returns true if insert is successful
        }
        public double getTotalExpenses(String filterDate) {
            SQLiteDatabase db = this.getReadableDatabase();
            double total = 0.0;

            Cursor cursor;
            if (filterDate == null) {
                cursor = db.rawQuery("SELECT SUM(" + EXPENSES_AMOUNT + ") as total FROM " + EXPENSES_TABLE, null);
            } else {
                cursor = db.rawQuery("SELECT SUM(" + EXPENSES_AMOUNT + ") as total FROM " + EXPENSES_TABLE + " WHERE date = ?", new String[]{filterDate});
            }

            if (cursor.moveToFirst()) {
                total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
            }
            cursor.close();
            return total;
        }




    }
