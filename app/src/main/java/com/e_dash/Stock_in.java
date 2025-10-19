package com.e_dash;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Stock_in extends AppCompatActivity {

    EditText editIngredientName, editQuantityIn;
    Button btnSaveStockIn;
    MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_in);

        dbHelper = new MyDatabaseHelper(this);

        editIngredientName = findViewById(R.id.editIngredientName);
        editQuantityIn = findViewById(R.id.editQuantityIn);
        btnSaveStockIn = findViewById(R.id.btnSaveStockIn);

        btnSaveStockIn.setOnClickListener(v -> saveStockIn());
    }

    private void saveStockIn() {
        String ingredient = editIngredientName.getText().toString().trim();
        String quantityStr = editQuantityIn.getText().toString().trim();

        if (ingredient.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "Please enter ingredient and quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        double quantity = Double.parseDouble(quantityStr);
        boolean success = dbHelper.addStockIn(ingredient, quantity);

        if (success) {
            Toast.makeText(this, "Stock-In saved successfully", Toast.LENGTH_SHORT).show();
            editIngredientName.setText("");
            editQuantityIn.setText("");
        } else {
            Toast.makeText(this, "Failed to save stock-in", Toast.LENGTH_SHORT).show();
        }
    }
}
