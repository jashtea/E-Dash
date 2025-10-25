package com.e_dash;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MandW extends AppCompatActivity {

    LineChart lineChart1;
    MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mand_w);

        lineChart1 = findViewById(R.id.lineChart1);
        dbHelper = new MyDatabaseHelper(this);

        showMonthlyPrediction(lineChart1);
    }

    private void showMonthlyPrediction(LineChart chart) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        HashMap<Integer, Double> monthlyTotals = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Cursor cursor = db.rawQuery("SELECT date, SUM(sold) AS total_sold FROM sales GROUP BY date", null);
        try {
            while (cursor.moveToNext()) {
                String dateStr = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                double totalSold = cursor.getDouble(cursor.getColumnIndexOrThrow("total_sold"));
                Date date = sdf.parse(dateStr);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);
                int monthIndex = year * 12 + month;
                monthlyTotals.put(monthIndex, monthlyTotals.getOrDefault(monthIndex, 0.0) + totalSold);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        ArrayList<Integer> monthIndices = new ArrayList<>(monthlyTotals.keySet());
        monthIndices.sort(Integer::compareTo);

        // ✅ Prevent crash if there's no data
        if (monthIndices.isEmpty()) {
            android.widget.Toast.makeText(this, "No sales data available for prediction.", android.widget.Toast.LENGTH_LONG).show();
            chart.clear();
            return;
        }

        ArrayList<Entry> actualEntries = new ArrayList<>();
        ArrayList<Double> xData = new ArrayList<>();
        ArrayList<Double> yData = new ArrayList<>();
        ArrayList<String> monthLabels = new ArrayList<>();

        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

        int firstMonth = monthIndices.get(0);
        int lastMonth = monthIndices.get(monthIndices.size() - 1);

        for (int i = 0; i < monthIndices.size(); i++) {
            int idx = monthIndices.get(i);
            double total = monthlyTotals.get(idx);
            actualEntries.add(new Entry(i, (float) total));
            xData.add((double) i);
            yData.add(total);

            int month = idx % 12;
            int year = idx / 12;
            monthLabels.add(months[month] + "-" + year);
        }

        int n = xData.size();
        double sumX=0, sumY=0, sumXY=0, sumX2=0;
        for(int i=0;i<n;i++){
            double x = xData.get(i), y = yData.get(i);
            sumX += x;
            sumY += y;
            sumXY += x*y;
            sumX2 += x*x;
        }

        double slope = (n*sumXY - sumX*sumY)/(n*sumX2 - sumX*sumX);
        double intercept = (sumY - slope*sumX)/n;

        ArrayList<Entry> predictedEntries = new ArrayList<>();
        for (int i = n; i < n + 3; i++) {
            double yPred = slope*i + intercept;
            if (yPred < 0) yPred = 0;
            predictedEntries.add(new Entry(i, (float)yPred));

            int futureMonth = (lastMonth + (i - n + 1)) % 12;
            int futureYear = (lastMonth + (i - n + 1)) / 12;
            monthLabels.add(months[futureMonth] + "-" + futureYear);
        }

        LineDataSet actualSet = new LineDataSet(actualEntries, "Actual Sales");
        actualSet.setColor(Color.BLUE);
        actualSet.setCircleColor(Color.BLUE);
        actualSet.setLineWidth(2f);

        LineDataSet predictedSet = new LineDataSet(predictedEntries, "Predicted Sales");
        predictedSet.setColor(Color.RED);
        predictedSet.enableDashedLine(10f,5f,0f);
        predictedSet.setCircleColor(Color.TRANSPARENT);
        predictedSet.setLineWidth(2f);

        LineData lineData = new LineData(actualSet, predictedSet);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(monthLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(10f);

        chart.getAxisRight().setEnabled(false);
        Description desc = new Description();
        desc.setText("Monthly Sales + Predicted Next Months");
        chart.setDescription(desc);
        chart.animateY(1000);
        chart.invalidate();
    }

}
