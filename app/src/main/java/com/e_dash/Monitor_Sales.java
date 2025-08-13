package com.e_dash;

import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Monitor_Sales extends AppCompatActivity {
    private TableLayout table;
    private Button addProduct, getData, clearData;
    private ArrayList<String> productData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor_sales);

        table = findViewById(R.id.monitor_sales);
        addProduct = findViewById(R.id.addproduct);
        getData = findViewById(R.id.getData);

        clearData = findViewById(R.id.clearData);

        addProduct.setOnClickListener(v -> {
            addProduct dialog = new addProduct();
            dialog.show(getSupportFragmentManager(), "addProduct");
        });

        getData.setOnClickListener(v -> {
            MyDatabaseHelper dbHelper = new MyDatabaseHelper(Monitor_Sales.this);
            String todayDate = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());

            for (String product : productData) {
                String[] parts = product.split(",");
                if (parts.length == 4) {
                    String name = parts[0];
                    int price = Integer.parseInt(parts[1]);
                    int qty = Integer.parseInt(parts[2]);
                    int sold = Integer.parseInt(parts[3]);

                    if (dbHelper.productExists(name, todayDate)) {
                        boolean updated = dbHelper.updateSoldValue(name, sold, todayDate);
                        if (updated) {
                            Toast.makeText(this, name + " updated for today!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to update: " + name, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        boolean inserted = dbHelper.insertSale(name, price, qty, sold);
                        if (!inserted) {
                            Toast.makeText(this, "Failed to insert: " + name, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            Toast.makeText(Monitor_Sales.this, "Data synced with database!", Toast.LENGTH_SHORT).show();
        });

        // clear table
        clearData.setOnClickListener(v -> clearSalesData());


        loadData(); // Load existing data when activity starts
    }

    public void addProductToList(String productName, int price, int quantity, int quantitySold) {
        // Avoid adding duplicate products to productData
        for (String product : productData) {
            if (product.startsWith(productName + ",")) {
//                Toast.makeText(this, productName + " already in list!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String item = productName + "," + price + "," + quantity + "," + quantitySold;
        productData.add(item);
        saveData();
        addTableRow(productName, price, quantity, quantitySold, productData.size() - 1);
    }


    private void addTableRow(String productName, int price, int quantity, int quantitySold, int index) {
        TableRow row = new TableRow(this);

        TextView nameTextView = createCell(productName);
        TextView priceTextView = createCell("Php" + price);
        TextView quantityTextView = createCell(String.valueOf(quantity));
        TextView soldTextView = createCell(String.valueOf(quantitySold));
        soldTextView.setBackgroundResource(android.R.drawable.edit_text);

        soldTextView.setOnClickListener(v ->
                showSoldQuantityDialog(soldTextView, quantity, index)
        );

        row.addView(nameTextView);
        row.addView(priceTextView);
        row.addView(quantityTextView);
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

    private void showSoldQuantityDialog(TextView soldTextView, int maxQuantity, int index) {
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
            String value = input.getText().toString();
            if (!value.isEmpty()) {
                int soldQty = Integer.parseInt(value);
                if (soldQty >= 0 && soldQty <= maxQuantity) {
                    soldTextView.setText(value);

                    // Update productData at correct index
                    String[] parts = productData.get(index).split(",");
                    if (parts.length == 4) {
                        String updated = parts[0] + "," + parts[1] + "," + parts[2] + "," + soldQty;
                        productData.set(index, updated);
                        saveData();
                    }

                    dialog.dismiss();
                } else {
                    Toast.makeText(Monitor_Sales.this, "Invalid quantity!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveData() {
        SharedPreferences prefs = getSharedPreferences("sales_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("product_list", new HashSet<>(productData));
        editor.apply();
    }

    private void loadData() {
        SharedPreferences prefs = getSharedPreferences("sales_data", MODE_PRIVATE);
        Set<String> set = prefs.getStringSet("product_list", new HashSet<>());
        productData.clear();
        productData.addAll(set);

        // Remove all rows except header (row 0)
        if (table.getChildCount() > 1) {
            table.removeViews(1, table.getChildCount() - 1);
        }

        for (int i = 0; i < productData.size(); i++) {
            String product = productData.get(i);
            String[] parts = product.split(",");
            if (parts.length == 4) {
                String name = parts[0];
                int price = Integer.parseInt(parts[1]);
                int qty = Integer.parseInt(parts[2]);
                int sold = Integer.parseInt(parts[3]);
                addTableRow(name, price, qty, sold, i);
            }
        }
    }


    private void clearSalesData(){
        SharedPreferences prefs = getSharedPreferences("sales_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("product_list");
        editor.apply();

        productData.clear();

        if (table.getChildCount() > 1) {
            table.removeViews(1, table.getChildCount() - 1);
        }

        Toast.makeText(this, "Sales cleared. Ready for next day!", Toast.LENGTH_SHORT).show();
    }


}
