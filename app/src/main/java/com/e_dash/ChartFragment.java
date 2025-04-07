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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;

public class ChartFragment extends Fragment {

    private PieChart pieChart;

    public ChartFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        pieChart = view.findViewById(R.id.pieChart);
        setupPieChart();
        return view;
    }

    private void setupPieChart() {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.getSalesData();

        ArrayList<PieEntry> entries = new ArrayList<>();

        int totalSales =  0;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String productName = cursor.getString(0);
                int sold = cursor.getInt(1);
                entries.add(new PieEntry(sold, productName));
                totalSales += sold;
            } while (cursor.moveToNext());

            cursor.close();
        }
        db.close();

        if (entries.isEmpty()) {
            pieChart.setNoDataText("No sales data available");
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "Sales Report");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(14f);

        // Display total sales in the center of the pie chart
        pieChart.setCenterText("Total Sales:\n" + totalSales);
        pieChart.setCenterTextSize(16f); // Adjust text size
        pieChart.setCenterTextColor(Color.BLACK); // Set text color

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        // UI Improvements
        pieChart.getDescription().setEnabled(false); // Hide description
        pieChart.setUsePercentValues(false);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.animateY(1000); // Add animation
        pieChart.invalidate();
    }
}
