package com.e_dash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;

public class Profile extends AppCompatActivity {

    private TextView log_out, user_name, user_email;
    private MyDatabaseHelper myDatabaseHelper;

    private Button Practice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // Initialize logout button
        log_out = findViewById(R.id.logout);


        // Add listener to logout button
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logout();
            }
        });

        // to download csv
        ImageView csv = findViewById(R.id.csv);

        csv.setOnClickListener(v -> exportAllTables());

        myDatabaseHelper = new MyDatabaseHelper(this);


        user_name = findViewById(R.id.username);
        user_email = findViewById(R.id.Email);

        SharedPreferences sharedPreferences = getSharedPreferences("userSession", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        if (username != null) {
            getInfo(username);

        }

        Button editProfile = findViewById(R.id.editProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this, Editprofile.class));
            }
        });


    }

    public void getInfo(String username) {
        Cursor cursor = myDatabaseHelper.getUserInfo(username);

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));

            user_name.setText(name);
            user_email.setText(email);


        }

    }


    public void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("userSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        finishAffinity();
        startActivity(new Intent(Profile.this, Login.class));
    }

    // To download csv file
    private void exportAllTables() {

        try {

            SQLiteDatabase db = openOrCreateDatabase("Edash.db", MODE_PRIVATE, null);

            File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File exportFolder = new File(downloadsFolder, "Edash_CSV");

            if (!exportFolder.exists()) {
                exportFolder.mkdirs();
            }

            Cursor tables = db.rawQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'",
                    null
            );

            while (tables.moveToNext()) {

                String tableName = tables.getString(0);

                File file = new File(exportFolder, tableName + ".csv");
                FileWriter writer = new FileWriter(file);

                Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);

                // Write column names
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    writer.append(cursor.getColumnName(i));
                    if (i < cursor.getColumnCount() - 1) writer.append(",");
                }
                writer.append("\n");

                // Write table rows
                while (cursor.moveToNext()) {
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        writer.append(cursor.getString(i));
                        if (i < cursor.getColumnCount() - 1) writer.append(",");
                    }
                    writer.append("\n");
                }

                cursor.close();
                writer.flush();
                writer.close();
            }

            tables.close();

            Toast.makeText(this, "CSV files saved in Downloads/Edash_CSV", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

