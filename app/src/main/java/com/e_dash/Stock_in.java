package com.e_dash;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class Stock_in extends AppCompatActivity {

    EditText editIngredientName, editQuantityIn, editStockInDate;
    Spinner spinnerUnit;
    Button btnSaveStockIn;
    MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_in);

        dbHelper = new MyDatabaseHelper(this);

        editIngredientName = findViewById(R.id.editIngredientName);
        editQuantityIn = findViewById(R.id.editQuantityIn);
        editStockInDate = findViewById(R.id.editStockInDate);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        btnSaveStockIn = findViewById(R.id.btnSaveStockIn);

        // Initialize unit spinner
        String[] units = {"kg", "g", "liters", "pcs"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(adapter);

        // Date picker for stock-in date
        editStockInDate.setOnClickListener(v -> showDatePicker());

        // Save button
        btnSaveStockIn.setOnClickListener(v -> saveStockIn());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    editStockInDate.setText(date);
                }, year, month, day);
        datePicker.show();
    }

    private void saveStockIn() {
        String ingredient = editIngredientName.getText().toString().trim();
        String quantityStr = editQuantityIn.getText().toString().trim();
        String unit = spinnerUnit.getSelectedItem().toString();
        String date = editStockInDate.getText().toString().trim();

        if (ingredient.isEmpty() || quantityStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
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

        boolean success = dbHelper.addStockIn(ingredient, quantity, unit, date);

        if (success) {
            Toast.makeText(this, "Stock-In saved successfully", Toast.LENGTH_SHORT).show();
            editIngredientName.setText("");
            editQuantityIn.setText("");
            editStockInDate.setText("");
            spinnerUnit.setSelection(0);
        } else {
            Toast.makeText(this, "Failed to save stock-in", Toast.LENGTH_SHORT).show();
        }
    }
}
