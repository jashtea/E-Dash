package com.e_dash;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Top_Products extends AppCompatActivity {

    private Spinner timeFrameSpinner;
    private ListView topProductsListView;

    private List<Product> allProducts;
    private ArrayAdapter<String> productAdapter;
    private List<String> displayList;

    private MyDatabaseHelper dbHelper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_products);

        initializeViews();
        dbHelper = new MyDatabaseHelper(this);
        loadProductsFromDatabase();
        setupTimeFrameSpinner();
        setupProductsList();
    }

    private void initializeViews() {
        timeFrameSpinner = findViewById(R.id.timeFrameSpinner);
        topProductsListView = findViewById(R.id.topProductsListView);
        displayList = new ArrayList<>();
    }

    private void loadProductsFromDatabase() {
        allProducts = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT product_name, sold, date FROM sales"; // ✅ Fix here
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                int sold = cursor.getInt(1);
                String saleDate = cursor.getString(2);

                allProducts.add(new Product(name, sold, saleDate)); // 👈 each sale record is stored separately
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }


    private void setupTimeFrameSpinner() {
        String[] timeFrames = {"This Week", "This Month", "Last 3 Months", "This Year"};

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                timeFrames
        );
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        timeFrameSpinner.setAdapter(spinnerAdapter);

        timeFrameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateProductsList(timeFrames[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupProductsList() {
        productAdapter = new ArrayAdapter<>(
                this,
                R.layout.list_item_product,
                displayList
        );
                                                                                                                                                                                                                                                                                                                                                                                                        topProductsListView.setAdapter(productAdapter);
        updateProductsList("This Week"); // Default
    }

    private void updateProductsList(String timeFrame) {
        List<Product> filteredProducts = filterProductsByTimeFrame(timeFrame);

        // ✅ Summarize sales per product
        HashMap<String, Integer> salesMap = new HashMap<>();
        for (Product p : filteredProducts) {
            int count = salesMap.getOrDefault(p.getName(), 0);
            salesMap.put(p.getName(), count + p.getSalesCount());
        }

        // ✅ Convert back to Product list with summarized totals
        List<Product> summarized = new ArrayList<>();
        for (String name : salesMap.keySet()) {
            summarized.add(new Product(name, salesMap.get(name), "")); // date no longer needed here
        }

        Collections.sort(summarized, (p1, p2) -> Integer.compare(p2.getSalesCount(), p1.getSalesCount()));

        displayList.clear();
        int maxItems = Math.min(5, summarized.size());

        for (int i = 0; i < maxItems; i++) {
            Product product = summarized.get(i);
            String displayText = String.format("#%d - %s (%d sold)",
                    i + 1, product.getName(), product.getSalesCount());
            displayList.add(displayText);
        }

        if (displayList.isEmpty()) {
            displayList.add("No sales data for this period");
        }

        productAdapter.notifyDataSetChanged();
    }


    private List<Product> filterProductsByTimeFrame(String timeFrame) {
        List<Product> filtered = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Product product : allProducts) {
            LocalDate productDate;
            try {
                productDate = LocalDate.parse(product.getSaleDate(), formatter);
            } catch (Exception e) {
                continue; // Skip invalid dates
            }

            boolean include = false;

            switch (timeFrame) {
                case "This Week":
                    LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
                    include = !productDate.isBefore(startOfWeek) && !productDate.isAfter(today);
                    break;

                case "This Month":
                    YearMonth currentMonth = YearMonth.from(today);
                    include = YearMonth.from(productDate).equals(currentMonth);
                    break;

                case "Last 3 Months":
                    LocalDate threeMonthsAgo = today.minusMonths(3);
                    include = !productDate.isBefore(threeMonthsAgo) && !productDate.isAfter(today);
                    break;

                case "This Year":
                    include = productDate.getYear() == today.getYear();
                    break;
            }

            if (include) {
                filtered.add(product);
            }
        }

        return filtered;
    }

    public static class Product {
        private final String name;
        private final int salesCount;
        private final String saleDate;

        public Product(String name, int salesCount, String saleDate) {
            this.name = name;
            this.salesCount = salesCount;
            this.saleDate = saleDate;
        }

        public String getName() { return name; }
        public int getSalesCount() { return salesCount; }
        public String getSaleDate() { return saleDate; }
    }
}
