package com.e_dash;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Owners_Income extends AppCompatActivity {

    EditText expenseDesc, expenseAmount;
    Button saveExpenseBtn;
    TextView txtRevenue, txtExpenses, txtOwnerIncome;

    TextView txtStartDate, txtEndDate;
    Button btnPickStartDate, btnPickEndDate;

    MyDatabaseHelper dbHelper;

    Calendar startCal, endCal;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owners_income);

        // Initialize database helper
        dbHelper = new MyDatabaseHelper(this);

        // Bind UI components
        expenseDesc = findViewById(R.id.expenseDesc);
        expenseAmount = findViewById(R.id.expenseAmount);
        saveExpenseBtn = findViewById(R.id.saveExpenseBtn);
        txtRevenue = findViewById(R.id.txtRevenue);
        txtExpenses = findViewById(R.id.txtExpenses);
        txtOwnerIncome = findViewById(R.id.txtOwnerIncome);
        txtStartDate = findViewById(R.id.txtStartDate);
        btnPickStartDate = findViewById(R.id.btnPickStartDate);
        txtEndDate = findViewById(R.id.txtEndDate);
        btnPickEndDate = findViewById(R.id.btnPickEndDate);

        // Default date range: past 7 days
        setDefaultDates();

        // Date pickers
        btnPickStartDate.setOnClickListener(v -> showDatePickerDialog(true));
        btnPickEndDate.setOnClickListener(v -> showDatePickerDialog(false));

        // Save expense button
        saveExpenseBtn.setOnClickListener(v -> saveExpense());

        // Load summary for default range
        loadSummary();
    }

    private void setDefaultDates() {
        Calendar today = Calendar.getInstance();
        endCal = (Calendar) today.clone();
        startCal = (Calendar) today.clone();
        startCal.add(Calendar.DAY_OF_MONTH, -7);

        txtStartDate.setText(sdf.format(startCal.getTime()));
        txtEndDate.setText(sdf.format(endCal.getTime()));
    }

    private void showDatePickerDialog(final boolean isStart) {
        final Calendar c = Calendar.getInstance();
        int yr = c.get(Calendar.YEAR);
        int mth = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dp = new DatePickerDialog(this, (DatePicker view, int year, int month, int dayOfMonth) -> {
            Calendar sel = Calendar.getInstance();
            sel.set(year, month, dayOfMonth);
            String s = sdf.format(sel.getTime());

            if (isStart) {
                startCal = sel;
                txtStartDate.setText(s);
            } else {
                endCal = sel;
                txtEndDate.setText(s);
            }

            if (startCal != null && endCal != null) {
                loadSummary();
            }
        }, yr, mth, day);
        dp.show();
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
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use today’s date for expense entry
        String todayStr = sdf.format(new Date());
        boolean inserted = dbHelper.addExpense(desc, amount, todayStr);

        if (inserted) {
            Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show();
            expenseDesc.setText("");
            expenseAmount.setText("");
            loadSummary();
        } else {
            Toast.makeText(this, "Failed to save expense", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSummary() {
        if (startCal == null || endCal == null) {
            txtExpenses.setText("Total Expenses: Php 0.00");
            txtRevenue.setText("Total Revenue: Php 0.00");
            txtOwnerIncome.setText("Owner Income: Php 0.00");
            return;
        }

        String startStr = sdf.format(startCal.getTime());
        String endStr = sdf.format(endCal.getTime());

        double totalExpenses = dbHelper.getTotalExpensesBetween(startStr, endStr);
        double totalRevenue = dbHelper.getTotalRevenueBetween(startStr, endStr);
        double ownerIncome = totalRevenue - totalExpenses;

        txtExpenses.setText(String.format(Locale.getDefault(),
                "Total Expenses: Php %.2f", totalExpenses));
        txtRevenue.setText(String.format(Locale.getDefault(),
                "Total Revenue: Php %.2f", totalRevenue));
        txtOwnerIncome.setText(String.format(Locale.getDefault(),
                "Owner Income: Php %.2f", ownerIncome));
    }
}
