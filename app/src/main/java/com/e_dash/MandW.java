package com.e_dash;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class MandW extends AppCompatActivity {

    private LineChart lineChart1, lineChart2;
    private TextView weeklyText, monthlyText;
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mand_w);

        lineChart1 = findViewById(R.id.lineChart1); // Weekly chart
        lineChart2 = findViewById(R.id.lineChart2); // Monthly chart

        weeklyText = findViewById(R.id.Week);
        monthlyText = findViewById(R.id.Month);

        dbHelper = new MyDatabaseHelper(this);

        showWeeklyChart();
        showMonthlyChart();
    }

    private void showWeeklyChart() {
        List<String> dayLabels = new ArrayList<>();
        List<Entry> entries = dbHelper.getWeeklySales(dayLabels);

        LineDataSet dataSet = new LineDataSet(entries, "Weekly Sales");
        dataSet.setColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
        dataSet.setValueTextSize(12f);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);

        String[] labelsArray = dayLabels.toArray(new String[0]);
        setupChart(lineChart1, dataSet, labelsArray);
    }

    private void showMonthlyChart() {
        List<Entry> entries = dbHelper.getMonthlySales();

        LineDataSet dataSet = new LineDataSet(entries, "Monthly Sales");
        dataSet.setColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark));
        dataSet.setValueTextSize(12f);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);

        String[] months = {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
        setupChart(lineChart2, dataSet, months);
    }

    private void setupChart(LineChart chart, LineDataSet dataSet, String[] labels) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.length);
        xAxis.setDrawGridLines(false);

        chart.getDescription().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setGranularity(1f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.animateX(1000);
        chart.invalidate();
    }
}
