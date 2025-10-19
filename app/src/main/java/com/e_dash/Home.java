package com.e_dash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Home extends AppCompatActivity {

    private ImageView Income;

    private ImageView chart;
    private ImageView profile, addProduct;
    private FrameLayout fragmentContainer;
    private ImageView analytics, monitor;
    private ImageView stocks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home)   ;

        ImageView analyzing = findViewById(R.id.MandW);
        ImageView salary = findViewById(R.id.salary);

        salary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, Owners_Income.class));
            }
        });

        analyzing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, MandW.class));
            }
        });

        ImageView topSales = findViewById(R.id.topSales);
        topSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, Top_Products.class));
            }
        });

        chart = findViewById(R.id.analytics);
//        fragmentContainer = findViewById(R.id.fragmentContainer);

        profile = findViewById(R.id.Profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, Profile.class));
            }
        });

        stocks = findViewById(R.id.stocks);

        stocks = findViewById(R.id.stocks);
        stocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            startActivity(new Intent (Home.this, Stocks.class));

            }
        });


        monitor = findViewById(R.id.monitor);

        monitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(
                        new Intent(Home.this, Monitor_Sales.class));
            }
        });


        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadChartFragment();
            }
        });

        Income = findViewById(R.id.Income);
        Income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, Income.class));
            }

        });

    }

    private void loadChartFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, new ChartFragment());
        transaction.commit();
    }

}


