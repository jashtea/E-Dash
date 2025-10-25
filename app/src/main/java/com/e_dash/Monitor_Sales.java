package com.e_dash;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Monitor_Sales extends AppCompatActivity {

    private TableLayout table;
    private Button addProduct, getData, clearData;
    private ArrayList<String> productData = new ArrayList<>();
    private MyDatabaseHelper dbHelper;
    private String todayDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor_sales);

        table = findViewById(R.id.monitor_sales);
        addProduct = findViewById(R.id.addproduct);
        getData = findViewById(R.id.getData);
        clearData = findViewById(R.id.clearData);

        dbHelper = new MyDatabaseHelper(this);
        todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        addProduct.setOnClickListener(v -> {
            addProduct dialog = new addProduct();
            dialog.show(getSupportFragmentManager(), "addProduct");
        });

        getData.setOnClickListener(v -> loadSalesFromDatabase());
        clearData.setOnClickListener(v -> clearTodaySales());

        loadSalesFromDatabase();
    }

    // Load all sales for today's date
    private void loadSalesFromDatabase() {
        productData.clear();

        // Remove all rows except the header row
        if (table.getChildCount() > 1) {
            table.removeViews(1, table.getChildCount() - 1);
        }

        Cursor cursor = dbHelper.getSalesByDate(todayDate);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
                int price = cursor.getInt(cursor.getColumnIndexOrThrow("price"));
                int qty = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                int sold = cursor.getInt(cursor.getColumnIndexOrThrow("sold"));

                productData.add(name + "," + price + "," + qty + "," + sold);

                addTableRow(name, price, qty, sold);
            } while (cursor.moveToNext());
            cursor.close();

            // Show success toast only once
            Toast.makeText(this, "Sales data loaded successfully!", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "No sales found for today!", Toast.LENGTH_SHORT).show();
        }
    }


    // Add new product to list (from dialog)
    public void addProductToList(String productName, int price, int quantity, int quantitySold) {
        boolean inserted = dbHelper.insertSale(productName, price, quantity, quantitySold);
        if (inserted) {
            Toast.makeText(this, "Added successfully!", Toast.LENGTH_SHORT).show();
            loadSalesFromDatabase();
        } else {
            Toast.makeText(this, "Failed to add product!", Toast.LENGTH_SHORT).show();
        }
    }

    // Create a table row dynamically
    private void addTableRow(String productName, int price, int qty, int sold) {
        TableRow row = new TableRow(this);

        TextView nameTextView = createCell(productName);
        TextView priceTextView = createCell("₱" + price);

        int remaining = Math.max(qty - sold, 0);
        TextView qtyTextView = createCell(String.valueOf(remaining));
        qtyTextView.setBackgroundResource(android.R.drawable.edit_text);
        qtyTextView.setOnClickListener(v -> showQuantityDialog(qtyTextView, productName, qty));

        TextView soldTextView = createCell(String.valueOf(sold));
        soldTextView.setBackgroundResource(android.R.drawable.edit_text);
        soldTextView.setOnClickListener(v -> showSoldQuantityDialog(soldTextView, qtyTextView, productName, qty));

        row.addView(nameTextView);
        row.addView(priceTextView);
        row.addView(qtyTextView);
        row.addView(soldTextView);

        table.addView(row);
    }

    private TextView createCell(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(Color.BLACK);
        tv.setPadding(8, 8, 8, 8);
        return tv;
    }

    // Add quantity (restock)
    private void showQuantityDialog(TextView qtyTextView, String productName, int currentQty) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add to Quantity");

        final EditText input = new EditText(this);
        input.setHint("Enter quantity to add");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Save", null);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String value = input.getText().toString().trim();
            if (!value.isEmpty()) {
                int addQty = Integer.parseInt(value);
                if (addQty > 0) {
                    int newTotal = currentQty + addQty;
                    boolean updated = dbHelper.updateQuantityValue(productName, newTotal, todayDate);
                    if (updated) {
                        qtyTextView.setText(String.valueOf(newTotal));
                        Toast.makeText(this, "Stock increased by " + addQty + "!", Toast.LENGTH_SHORT).show();
                        loadSalesFromDatabase();
                    } else {
                        Toast.makeText(this, "Failed to update quantity!", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Enter a valid number!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Saving sold no longer subtracts qty in database
    private void showSoldQuantityDialog(TextView soldTextView, TextView qtyTextView, String productName, int totalQty) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Sold Quantity");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(soldTextView.getText().toString());
        builder.setView(input);

        builder.setPositiveButton("Save", null);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String value = input.getText().toString().trim();
            if (!value.isEmpty()) {
                int soldQty = Integer.parseInt(value);
                if (soldQty >= 0 && soldQty <= totalQty) {
                    boolean updated = dbHelper.updateSoldValue(productName, soldQty, todayDate);
                    if (updated) {
                        soldTextView.setText(String.valueOf(soldQty));

                        int remaining = Math.max(totalQty - soldQty, 0);
                        qtyTextView.setText(String.valueOf(remaining));

                        Toast.makeText(this, "Sold saved! Remaining (display): " + remaining, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to save sold!", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Invalid quantity! Must be 0–" + totalQty, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void clearTodaySales() {
        boolean cleared = dbHelper.deleteSalesByDate(todayDate);
        if (cleared) {
            table.removeViews(1, table.getChildCount() - 1);
            Toast.makeText(this, "Today's sales cleared!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No data to clear!", Toast.LENGTH_SHORT).show();
        }
    }
}
