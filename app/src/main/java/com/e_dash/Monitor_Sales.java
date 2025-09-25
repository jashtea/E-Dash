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
                    int initialQty = Integer.parseInt(parts[2]);
                    int sold = Integer.parseInt(parts[3]);

                    if (dbHelper.productExists(name, todayDate)) {
                        boolean updated = dbHelper.updateSoldValue(name, sold, todayDate);
                        if (updated) {
                            Toast.makeText(this, name + " updated for today!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to update: " + name, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        boolean inserted = dbHelper.insertSale(name, price, initialQty, sold);
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
                return;
            }
        }

        // Here we store as: name,price,initialQty,sold
        String item = productName + "," + price + "," + quantity + "," + quantitySold;
        productData.add(item);
        saveData();
        addTableRow(productName, price, quantity, quantitySold, productData.size() - 1);
    }

    private void addTableRow(String productName, int price, int initialQty, int quantitySold, int index) {
        TableRow row = new TableRow(this);

        TextView nameTextView = createCell(productName);
        TextView priceTextView = createCell("Php" + price);

        // displayed quantity = initialQty - sold
        int displayedQty = initialQty - quantitySold;
        if (displayedQty < 0) displayedQty = 0; // safety
        TextView quantityTextView = createCell(String.valueOf(displayedQty));

        TextView soldTextView = createCell(String.valueOf(quantitySold));
        soldTextView.setBackgroundResource(android.R.drawable.edit_text);

        // on click -> edit sold; pass initialQty as max allowed
        soldTextView.setOnClickListener(v ->
                showSoldQuantityDialog(soldTextView, initialQty, index)
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

    private void showSoldQuantityDialog(TextView soldTextView, int initialQty, int index) {
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
                if (soldQty >= 0 && soldQty <= initialQty) {
                    // Update sold in productData but keep initialQty the same
                    String[] parts = productData.get(index).split(",");
                    if (parts.length == 4) {
                        String name = parts[0];
                        String price = parts[1];

                        String updated = name + "," + price + "," + initialQty + "," + soldQty;
                        productData.set(index, updated);
                        saveData();

                        // update table row cells
                        TableRow row = (TableRow) table.getChildAt(index + 1); // +1 if header row exists
                        TextView qtyCell = (TextView) row.getChildAt(2); // 0=name,1=price,2=qty,3=sold
                        qtyCell.setText(String.valueOf(initialQty - soldQty));

                        soldTextView.setText(String.valueOf(soldQty));

                        dialog.dismiss();
                    } else {
                        // malformed entry
                        Toast.makeText(Monitor_Sales.this, "Data error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Monitor_Sales.this, "Invalid quantity! Must be 0–" + initialQty, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Use a serialized string to preserve order (do not use Set)
    private void saveData() {
        SharedPreferences prefs = getSharedPreferences("sales_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < productData.size(); i++) {
            sb.append(productData.get(i));
            if (i < productData.size() - 1) sb.append(";;"); // separator unlikely to appear in product string
        }
        editor.putString("product_list_serialized", sb.toString());
        editor.apply();
    }

    // Load + normalize old-format entries if necessary
    private void loadData() {
        SharedPreferences prefs = getSharedPreferences("sales_data", MODE_PRIVATE);
        String serialized = prefs.getString("product_list_serialized", "");
        productData.clear();

        if (!serialized.isEmpty()) {
            String[] items = serialized.split(java.util.regex.Pattern.quote(";;"));
            for (String item : items) {
                productData.add(item);
            }
        }

        // Remove all rows except header (row 0)
        if (table.getChildCount() > 1) {
            table.removeViews(1, table.getChildCount() - 1);
        }

        // Normalize and render rows
        boolean changed = false;
        for (int i = 0; i < productData.size(); i++) {
            String product = productData.get(i);
            String[] parts = product.split(",");
            if (parts.length == 4) {
                String name = parts[0].trim();
                int price = Integer.parseInt(parts[1].trim());
                int qtyStored = Integer.parseInt(parts[2].trim());
                int sold = Integer.parseInt(parts[3].trim());

                // Normalize: compute initialQty.
                // Works for older format where parts[2] was remaining and parts[3] was sold:
                // initialQty = remaining + sold.
                // For fresh entries (sold==0) initialQty = qtyStored.
                int initialQty = qtyStored + sold;
                if (sold == 0) initialQty = qtyStored;

                // Replace with normalized format (name,price,initialQty,sold)
                String normalized = name + "," + price + "," + initialQty + "," + sold;
                if (!normalized.equals(product)) {
                    productData.set(i, normalized);
                    changed = true;
                }

                addTableRow(name, price, initialQty, sold, i);
            }
        }

        // If we altered productData during normalization, save back
        if (changed) saveData();
    }

    private void clearSalesData() {
        SharedPreferences prefs = getSharedPreferences("sales_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("product_list_serialized");
        editor.apply();

        productData.clear();

        if (table.getChildCount() > 1) {
            table.removeViews(1, table.getChildCount() - 1);
        }

        Toast.makeText(this, "Sales cleared. Ready for next day!", Toast.LENGTH_SHORT).show();
    }
}
