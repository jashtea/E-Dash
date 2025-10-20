package com.e_dash;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Calendar;
import android.database.Cursor;

public class StockOutActivity extends AppCompatActivity {

    Spinner spinnerIngredientOut;
    EditText editQuantityOut, editStockOutDate;
    TextView textUnitOut;
    Button btnSaveStockOut;
    MyDatabaseHelper dbHelper;

    ArrayList<String> ingredientList = new ArrayList<>();
    ArrayAdapter<String> ingredientAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_stock_out);

        dbHelper = new MyDatabaseHelper(this);

        spinnerIngredientOut = findViewById(R.id.spinnerIngredientOut);
        editQuantityOut = findViewById(R.id.editQuantityOut);
        editStockOutDate = findViewById(R.id.editStockOutDate);
        textUnitOut = findViewById(R.id.textUnitOut);
        btnSaveStockOut = findViewById(R.id.btnSaveStockOut);

        // Populate ingredient spinner
        loadIngredients();

        // Update unit when ingredient changes
        spinnerIngredientOut.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedIngredient = ingredientList.get(position);
                String unit = dbHelper.getUnit(selectedIngredient);
                textUnitOut.setText(unit);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Date picker for stock-out date
        editStockOutDate.setOnClickListener(v -> showDatePicker());

        // Save button click
        btnSaveStockOut.setOnClickListener(v -> saveStockOut());
    }

    private void loadIngredients() {
        ingredientList.clear();
        Cursor cursor = dbHelper.getAllIngredients();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String ingredient = cursor.getString(cursor.getColumnIndexOrThrow("ingredient_name"));
                ingredientList.add(ingredient);
            } while (cursor.moveToNext());
            cursor.close();
        }

        if (ingredientList.isEmpty()) {
            ingredientList.add("No ingredients available");
        }

        ingredientAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ingredientList);
        ingredientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIngredientOut.setAdapter(ingredientAdapter);
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    editStockOutDate.setText(date);
                }, year, month, day);
        datePicker.show();
    }

    private void saveStockOut() {
        String ingredient = spinnerIngredientOut.getSelectedItem().toString();
        String quantityStr = editQuantityOut.getText().toString().trim();
        String unit = textUnitOut.getText().toString();
        String date = editStockOutDate.getText().toString().trim();

        if (ingredient.isEmpty() || quantityStr.isEmpty() || date.isEmpty() || ingredient.equals("No ingredients available")) {
            Toast.makeText(this, "Please select ingredient and enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(quantityStr);
            if (quantity <= 0) {
                Toast.makeText(this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        // Optional: check remaining stock
        double remaining = dbHelper.getRemainingStock(ingredient);
        if (quantity > remaining) {
            Toast.makeText(this, "Quantity exceeds remaining stock (" + remaining + " " + unit + ")", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = dbHelper.addStockOut(ingredient, quantity, unit, date);

        if (success) {
            Toast.makeText(this, "Stock-Out saved successfully", Toast.LENGTH_SHORT).show();
            editQuantityOut.setText("");
            editStockOutDate.setText("");
        } else {
            Toast.makeText(this, "Failed to record stock-out", Toast.LENGTH_SHORT).show();
        }
    }
}
