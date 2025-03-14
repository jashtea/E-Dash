package com.e_dash;

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
        // Initialize PieChart
        pieChart = view.findViewById(R.id.pieChart);
        setupPieChart();
        return view;
    }

    private void setupPieChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(40f, "Age"));
        entries.add(new PieEntry(30f, "Products"));
        entries.add(new PieEntry(20f, "Gender"));
        entries.add(new PieEntry(10f, "Ratings"));

        PieDataSet dataSet = new PieDataSet(entries, "Analytics");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(14f);

        PieData pieData = new PieData(dataSet);

        pieChart.setData(pieData);
        pieChart.invalidate();  // ✅ Refresh the chart

        // 🔹 Additional settings to ensure it works
        pieChart.getDescription().setEnabled(false); // Hide "Description Label"
        pieChart.setDrawHoleEnabled(false);  // No empty center
        pieChart.setUsePercentValues(true);  // Show as percentage
        pieChart.getLegend().setEnabled(true); // Enable legend
        pieChart.animateY(1000);  // Animate chart
    }

}