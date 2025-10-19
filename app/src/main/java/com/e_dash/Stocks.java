package com.e_dash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class Stocks extends AppCompatActivity {

    Button stock_in, stock_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocks);

        // Link buttons from XML
        stock_in = findViewById(R.id.btnStockIn);
        stock_out = findViewById(R.id.btnStockOut);

        // Stock In button listener
        stock_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Stocks.this, Stock_in.class));
            }
        });

        // Stock Out button listener
        stock_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the StockOutActivity when clicked
                startActivity(new Intent(Stocks.this, StockOutActivity.class));
            }
        });
    }
}
