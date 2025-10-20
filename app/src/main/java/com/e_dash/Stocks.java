package com.e_dash;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import android.database.Cursor;

public class Stocks extends AppCompatActivity {

    Button stock_in, stock_out;
    ListView listStocks;
    MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocks);

        stock_in = findViewById(R.id.btnStockIn);
        stock_out = findViewById(R.id.btnStockOut);
        listStocks = findViewById(R.id.listStocks);
        dbHelper = new MyDatabaseHelper(this);

        // Open Stock In activity
        stock_in.setOnClickListener(v ->
                startActivity(new Intent(Stocks.this, Stock_in.class))
        );

        // Open Stock Out activity
        stock_out.setOnClickListener(v ->
                startActivity(new Intent(Stocks.this, StockOutActivity.class))
        );

        // Load initial stock data
        loadStockData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the stock list after returning from Stock In/Out
        loadStockData();
    }

    /**
     * Load stock summary from the database and display it
     */
    private void loadStockData() {
        ArrayList<StockItem> stockList = new ArrayList<>();
        Cursor cursor = dbHelper.getAllIngredients();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String ingredient = cursor.getString(cursor.getColumnIndexOrThrow("ingredient_name"));
                double inQty = dbHelper.getTotalStockIn(ingredient);
                double outQty = dbHelper.getTotalStockOut(ingredient);
                double remaining = dbHelper.getRemainingStock(ingredient);
                String unit = dbHelper.getUnit(ingredient);

                stockList.add(new StockItem(ingredient, inQty, outQty, remaining, unit));

            } while (cursor.moveToNext());
            cursor.close();
        }

        StockAdapter adapter = new StockAdapter(this, stockList);
        listStocks.setAdapter(adapter);
    }


}
