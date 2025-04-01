package com.e_dash;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class Monitor_Sales extends AppCompatActivity {
    private TableLayout table;
    private Button addProduct, getData;
    private ArrayList<String> data = new ArrayList<>();
    private ArrayList<String> productData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor_sales);
        table = findViewById(R.id.monitor_sales);

        addProduct= findViewById(R.id.addproduct);
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct addProduct = new addProduct();
                addProduct.show(getSupportFragmentManager(), "addProduct");
            }
        });

        getData = findViewById(R.id.getData);
        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTableData();
            }
        });

    }

    public void addProductToList(String productName, int price, int quantity, int quantitySold){
        data.add(productName);
        data.add(String.valueOf(quantity));
        data.add(String.valueOf(quantitySold));

        addTableRow(productName, price, quantity, quantitySold);
    }

    private void addTableRow(String productName, double price, int quantity, int quantitySold) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView nameTextView = new TextView(this);
        nameTextView.setText(productName);
        nameTextView.setTextColor(Color.BLACK);
        nameTextView.setPadding(8, 8, 8, 8);
        nameTextView.setGravity(Gravity.CENTER);
        nameTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));

        TextView priceTextView = new TextView(this);
        priceTextView.setTextColor(Color.BLACK);
        priceTextView.setText(String.format("Php%.2f", price));
        priceTextView.setPadding(8, 8, 8, 8);
        priceTextView.setGravity(Gravity.CENTER);
        priceTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

        TextView quantityTextView = new TextView(this);
        quantityTextView.setTextColor(Color.BLACK);
        quantityTextView.setText(String.valueOf(quantity));
        quantityTextView.setPadding(8, 8, 8, 8);
        quantityTextView.setGravity(Gravity.CENTER);
        quantityTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

        // Change EditText to TextView (Click to Open Dialog)
        TextView soldTextView = new TextView(this);
        soldTextView.setText(String.valueOf(quantitySold));
        soldTextView.setTextColor(Color.BLACK);
        soldTextView.setPadding(8, 8, 8, 8);
        soldTextView.setGravity(Gravity.CENTER);
        soldTextView.setBackgroundResource(android.R.drawable.edit_text); // Make it look like an input field
        soldTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

        // Add Click Listener to Open Dialog
        soldTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSoldQuantityDialog(soldTextView, quantity);
            }
        });

        row.addView(nameTextView);
        row.addView(priceTextView);
        row.addView(quantityTextView);
        row.addView(soldTextView);

        table.addView(row);
    }


    private void showSoldQuantityDialog(TextView soldTextView, int maxQuantity) {
        // Create a pop-up dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Sold Quantity");

        // Add an input field
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(soldTextView.getText().toString()); // Set current value
        builder.setView(input);

        // "Save" button
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = input.getText().toString();

                // Check if input is valid
                if (!value.isEmpty()) {
                    int soldQty = Integer.parseInt(value);
                    if (soldQty >= 0 && soldQty <= maxQuantity) {
                        soldTextView.setText(value); // Update text
                        Toast.makeText(Monitor_Sales.this, "Updated Sold: " + soldQty, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Monitor_Sales.this, "Invalid quantity!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // "Cancel" button
        builder.setNegativeButton("Cancel", null);

        // Show the dialog
        builder.show();
    }

    //to get the data or sales
    private void getTableData() {
        TableLayout table = findViewById(R.id.monitor_sales);
        int rowCount = table.getChildCount();
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);

        // Start from 1 to skip the header row
        for (int i = 1; i < rowCount; i++) {
            TableRow row = (TableRow) table.getChildAt(i);

            String productName = ((TextView) row.getChildAt(0)).getText().toString();
            double price = Double.parseDouble(((TextView) row.getChildAt(1)).getText().toString().replace("Php", ""));
            int quantity = Integer.parseInt(((TextView) row.getChildAt(2)).getText().toString());
            int sold = Integer.parseInt(((TextView) row.getChildAt(3)).getText().toString());

            // Insert data into the database
            dbHelper.insertSale(productName, price, quantity, sold);


        }

        Toast.makeText(this, "Sales Data Saved!", Toast.LENGTH_SHORT).show();
    }




}