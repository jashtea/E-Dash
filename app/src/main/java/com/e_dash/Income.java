package com.e_dash;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class Income extends AppCompatActivity {
    MyDatabaseHelper dbHelper;
    TableLayout tableLayout;
    TextView tvTotalRevenue, tvSelectedDate;
    Button btnPickDate;

    String selectedDate = ""; // format: YYYY-MM-DD

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        dbHelper = new MyDatabaseHelper(this);
        tableLayout = findViewById(R.id.tableSales);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        btnPickDate = findViewById(R.id.btnPickDate);

        dbHelper.saveAllDailyTotals();

        // Default load all sales
        displaySales(null);

        // Button pick date
        btnPickDate.setOnClickListener(v -> pickDate());
    }

    private void pickDate() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            // Format YYYY-MM-DD (DB-friendly)
            selectedDate = String.format("%04d-%02d-%02d", year, (month + 1), day);
            tvSelectedDate.setText("Selected Date: " + selectedDate);

            // Load filtered sales
            displaySales(selectedDate);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void displaySales(String filterDate) {
        // clear old rows except header
        int childCount = tableLayout.getChildCount();
        if (childCount > 1) {
            tableLayout.removeViews(1, childCount - 1);
        }

        String query;
        String[] args = null;

        if (filterDate == null) {
            query = "SELECT product_name, price, sold, date FROM sales";
        } else {
            query = "SELECT product_name, price, sold, date FROM sales WHERE date = ?";
            args = new String[]{filterDate};
        }

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(query, args);

        if (cursor == null || cursor.getCount() == 0) {
            TableRow row = new TableRow(this);
            TextView tv = new TextView(this);
            tv.setText("No Sales Data Available");
            tv.setPadding(16, 8, 16, 8);
            row.addView(tv);
            tableLayout.addView(row);
            if (filterDate != null) {
                tvTotalRevenue.setText("Total Revenue: 0.0");
            }
            return;
        }

        double grandTotal = 0.0;

        while (cursor.moveToNext()) {
            TableRow row = new TableRow(this);

            // Product Name
            TextView tv1 = new TextView(this);
            tv1.setText(cursor.getString(cursor.getColumnIndexOrThrow("product_name")));
            tv1.setTextColor(Color.WHITE);
            tv1.setGravity(Gravity.CENTER);
            tv1.setPadding(16, 8, 16, 8);
            row.addView(tv1);

            // Date
            TextView tv2 = new TextView(this);
            tv2.setText(cursor.getString(cursor.getColumnIndexOrThrow("date")));
            tv2.setTextColor(Color.WHITE);
            tv2.setGravity(Gravity.CENTER);
            tv2.setPadding(16, 8, 16, 8);
            row.addView(tv2);

            // Price
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
            TextView tv3 = new TextView(this);
            tv3.setText(String.valueOf(price));
            tv3.setTextColor(Color.WHITE);
            tv3.setGravity(Gravity.CENTER);
            tv3.setPadding(16, 8, 16, 8);
            row.addView(tv3);

            // Sold
            int sold = cursor.getInt(cursor.getColumnIndexOrThrow("sold"));
            TextView tv4 = new TextView(this);
            tv4.setText(String.valueOf(sold));
            tv4.setTextColor(Color.WHITE);
            tv4.setGravity(Gravity.CENTER);
            tv4.setPadding(16, 8, 16, 8);
            row.addView(tv4);

            // Total Sales
            double totalSales = price * sold;
            grandTotal += totalSales;
            TextView tv5 = new TextView(this);
            tv5.setText(String.valueOf(totalSales));
            tv5.setTextColor(Color.WHITE);
            tv5.setGravity(Gravity.CENTER);
            tv5.setPadding(16, 8, 16, 8);
            row.addView(tv5);

            tableLayout.addView(row);
        }

        tvTotalRevenue.setText("Total Revenue: " + grandTotal);
        cursor.close();
    }
}
