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

public class StockOutActivity extends AppCompatActivity {

    EditText editIngredientUsed, editQuantityOut, editStockOutDate;
    Spinner spinnerUnitOut;
    Button btnSaveStockOut;
    MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_stock_out);

        dbHelper = new MyDatabaseHelper(this);

        editIngredientUsed = findViewById(R.id.editIngredientUsed);
        editQuantityOut = findViewById(R.id.editQuantityOut);
        editStockOutDate = findViewById(R.id.editStockOutDate);
        spinnerUnitOut = findViewById(R.id.spinnerUnitOut);
        btnSaveStockOut = findViewById(R.id.btnSaveStockOut);

        // Initialize unit spinner
        String[] units = {"kg", "g", "liters", "pcs"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnitOut.setAdapter(adapter);

        // Date picker for stock-out date
        editStockOutDate.setOnClickListener(v -> showDatePicker());

        // Save button click
        btnSaveStockOut.setOnClickListener(v -> saveStockOut());
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
        String ingredient = editIngredientUsed.getText().toString().trim();
        String quantityStr = editQuantityOut.getText().toString().trim();
        String unit = spinnerUnitOut.getSelectedItem().toString();
        String date = editStockOutDate.getText().toString().trim();

        if (ingredient.isEmpty() || quantityStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please enter all required fields", Toast.LENGTH_SHORT).show();
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

        boolean success = dbHelper.addStockOut(ingredient, quantity, unit, date);

        if (success) {
            Toast.makeText(this, "Stock-Out saved successfully", Toast.LENGTH_SHORT).show();
            editIngredientUsed.setText("");
            editQuantityOut.setText("");
            editStockOutDate.setText("");
            spinnerUnitOut.setSelection(0);
        } else {
            Toast.makeText(this, "Failed to record stock-out", Toast.LENGTH_SHORT).show();
        }
    }
}
