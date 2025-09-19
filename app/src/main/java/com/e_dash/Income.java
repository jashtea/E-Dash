package com.e_dash;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Income extends AppCompatActivity {
    MyDatabaseHelper dbHelper;
    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        dbHelper = new MyDatabaseHelper(this);
        tableLayout = findViewById(R.id.tableSales);

        // ✅ Load data from database
        displaySales();
    }

    private void displaySales() {
        Cursor cursor = dbHelper.getReadableDatabase()
                .rawQuery("SELECT product_name, price, sold, date FROM sales", null);

        if (cursor == null || cursor.getCount() == 0) {
            // If no records
            TableRow row = new TableRow(this);
            TextView tv = new TextView(this);
            tv.setText("No Sales Data Available");
            tv.setPadding(16, 8, 16, 8);
            row.addView(tv);
            tableLayout.addView(row);
            return;
        }

        while (cursor.moveToNext()) {
            TableRow row = new TableRow(this);

            // Product Name
            TextView tv1 = new TextView(this);
            tv1.setText(cursor.getString(cursor.getColumnIndexOrThrow("product_name")));
            tv1.setPadding(16, 8, 16, 8);
            row.addView(tv1);

            // Date
            TextView tv2 = new TextView(this);
            tv2.setText(cursor.getString(cursor.getColumnIndexOrThrow("date")));
            tv2.setPadding(16, 8, 16, 8);
            row.addView(tv2);

            // Price
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
            TextView tv3 = new TextView(this);
            tv3.setText(String.valueOf(price));
            tv3.setPadding(16, 8, 16, 8);
            row.addView(tv3);

            // Sold
            int sold = cursor.getInt(cursor.getColumnIndexOrThrow("sold"));
            TextView tv4 = new TextView(this);
            tv4.setText(String.valueOf(sold));
            tv4.setPadding(16, 8, 16, 8);
            row.addView(tv4);

            // ✅ Total Sales (price × sold)
            double totalSales = price * sold;
            TextView tv5 = new TextView(this);
            tv5.setText(String.valueOf(totalSales));
            tv5.setPadding(16, 8, 16, 8);
            row.addView(tv5);

            tableLayout.addView(row);
        }

        cursor.close();
    }
}
