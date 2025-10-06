package com.e_dash;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Owners_Income extends AppCompatActivity {

    EditText expenseDesc, expenseAmount;
    Button saveExpenseBtn;
    TextView txtRevenue, txtExpenses, txtOwnerIncome;

    MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owners_income);

        // Initialize DB
        dbHelper = new MyDatabaseHelper(this);

        // Bind UI components
        expenseDesc = findViewById(R.id.expenseDesc);
        expenseAmount = findViewById(R.id.expenseAmount);
        saveExpenseBtn = findViewById(R.id.saveExpenseBtn);
        txtRevenue = findViewById(R.id.txtRevenue);
        txtExpenses = findViewById(R.id.txtExpenses);
        txtOwnerIncome = findViewById(R.id.txtOwnerIncome);



        // Load summary when activity starts
        loadSummary();

        // Save expense button click
        saveExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExpense();
            }
        });
    }

    private void saveExpense() {
        String desc = expenseDesc.getText().toString().trim();
        String amountStr = expenseAmount.getText().toString().trim();

        if (desc.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get today's date in yyyy-MM-dd format
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Use the correct method from your helper
        boolean inserted = dbHelper.addExpense(desc, amount, today);

        if (inserted) {
            Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show();
            expenseDesc.setText("");
            expenseAmount.setText("");
            loadSummary(); // refresh totals
        } else {
            Toast.makeText(this, "Failed to save expense", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSummary() {
        // Get today’s date
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Fetch data from DB
//        double totalRevenue = dbHelper.getTotalRevenueByDate(today); // you need to implement this method
//        double totalExpenses = dbHelper.getTotalExpenses(today);     // already exists
//        double ownerIncome = totalRevenue - totalExpenses;

        // Update UI
//        txtRevenue.setText("Total Revenue: Php " + String.format(Locale.getDefault(), "%.2f", totalRevenue));
//        txtExpenses.setText("Total Expenses: Php " + String.format(Locale.getDefault(), "%.2f", totalExpenses));
//        txtOwnerIncome.setText("Owner Income: Php " + String.format(Locale.getDefault(), "%.2f", ownerIncome));
    }
}
