package com.e_dash;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChartFragment extends Fragment {

    private PieChart pieChart;

    public ChartFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        pieChart = view.findViewById(R.id.pieChart);
        Spinner filterSpinner = view.findViewById(R.id.filterSpinner);

        // Spinner options
        String[] filters = {"All Time", "This Month", "This Week"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, filters);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view1, int position, long id) {
                String filter = parent.getItemAtPosition(position).toString();
                setupPieChart(filter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setupPieChart("All Time");
            }
        });

        setupPieChart("All Time");
        return view;
    }

    private void setupPieChart(String filter) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.getSalesData(filter);

        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<ProductSale> allSales = new ArrayList<>();
        int totalSales = 0;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String productName = cursor.getString(0);
                int sold = cursor.getInt(1);
                allSales.add(new ProductSale(productName, sold));
                totalSales += sold;
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        if (allSales.isEmpty()) {
            pieChart.clear();
            pieChart.setNoDataText("No sales data available");
            return;
        }

        // Sort descending by sales
        Collections.sort(allSales, (a, b) -> b.sold - a.sold);

        int maxSlices = 6;
        int othersTotal = 0;

        for (int i = 0; i < allSales.size(); i++) {
            ProductSale sale = allSales.get(i);
            if (i < maxSlices) {
                entries.add(new PieEntry(sale.sold, sale.productName));
            } else {
                othersTotal += sale.sold;
            }
        }

        if (othersTotal > 0) {
            entries.add(new PieEntry(othersTotal, "Others"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Sales Report");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.BLACK);

        // Label settings
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setDrawEntryLabels(true); // show product names

        // Center Text
        pieChart.setCenterText("Total Sales:\n" + totalSales);
        pieChart.setCenterTextSize(16f);
        pieChart.setCenterTextColor(Color.BLACK);

        // Set data
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        // Appearance
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(false);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    // Helper class to store product sales
    private static class ProductSale {
        String productName;
        int sold;

        ProductSale(String productName, int sold) {
            this.productName = productName;
            this.sold = sold;
        }
    }
}
