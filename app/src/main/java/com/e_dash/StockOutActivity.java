package com.e_dash;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class StockOutActivity extends AppCompatActivity {

    EditText editIngredientUsed, editQuantityOut;
    Button btnSaveStockOut;
    MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_stock_out);

        dbHelper = new MyDatabaseHelper(this);

        editIngredientUsed = findViewById(R.id.editIngredientUsed);
        editQuantityOut = findViewById(R.id.editQuantityOut);
        btnSaveStockOut = findViewById(R.id.btnSaveStockOut);

        btnSaveStockOut.setOnClickListener(v -> saveStockOut());
    }

    private void saveStockOut() {
        String ingredient = editIngredientUsed.getText().toString().trim();
        String quantityStr = editQuantityOut.getText().toString().trim();

        if (ingredient.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "Please enter ingredient and quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        double quantity = Double.parseDouble(quantityStr);
        boolean success = dbHelper.addStockOut(ingredient, quantity);

        if (success) {
            Toast.makeText(this, "Stock-Out saved successfully", Toast.LENGTH_SHORT).show();
            editIngredientUsed.setText("");
            editQuantityOut.setText("");
        } else {
            Toast.makeText(this, "Failed to record stock-out", Toast.LENGTH_SHORT).show();
        }
    }
}
