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

public class MandW extends AppCompatActivity {

    private LineChart lineChart1, lineChart2;
    private TextView weeklyText, monthlyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mand_w);

        // Correct IDs from XML
        lineChart1 = findViewById(R.id.lineChart1); // weekly
        lineChart2 = findViewById(R.id.lineChart2); // monthly

        weeklyText = findViewById(R.id.Week);
        monthlyText = findViewById(R.id.Month);

        // Populate both charts at once
        showWeeklyChart();
        showMonthlyChart();
    }

    private void showWeeklyChart() {
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 10));
        entries.add(new Entry(1, 12));
        entries.add(new Entry(2, 15));
        entries.add(new Entry(3, 8));
        entries.add(new Entry(4, 20));
        entries.add(new Entry(5, 18));
        entries.add(new Entry(6, 25));

        LineDataSet dataSet = new LineDataSet(entries, "Weekly Sales");
        dataSet.setColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
        dataSet.setValueTextSize(12f);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);

        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        setupChart(lineChart1, dataSet, days);
    }

    private void showMonthlyChart() {
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 100));
        entries.add(new Entry(1, 150));
        entries.add(new Entry(2, 130));
        entries.add(new Entry(3, 170));
        entries.add(new Entry(4, 200));
        entries.add(new Entry(5, 180));
        entries.add(new Entry(6, 210));
        entries.add(new Entry(7, 230));
        entries.add(new Entry(8, 220));
        entries.add(new Entry(9, 190));
        entries.add(new Entry(10, 240));
        entries.add(new Entry(11, 250));

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
