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

    private ImageView chart;
    private ImageView profile, addProduct;
    private FrameLayout fragmentContainer;
    private ImageView analytics, monitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);


        chart = findViewById(R.id.analytics);
//        fragmentContainer = findViewById(R.id.fragmentContainer);

        profile = findViewById(R.id.Profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, Profile.class));
            }
        });

//        analytics = findViewById(R.id.analytics);
//        analytics.setOnClickListener(new View.OnClickListener() {
//                                         @Override
//                                         public void onClick(View v) {
//                                             startActivity(
//                                                     new Intent(Home.this, Login.class));
//                                         }
//                                     });


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

    }

    private void loadChartFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, new ChartFragment());
        transaction.commit();
    }
}
