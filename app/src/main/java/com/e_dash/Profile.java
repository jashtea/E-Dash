package com.e_dash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Profile extends AppCompatActivity {

    private TextView log_out,user_name,user_email;
    private MyDatabaseHelper myDatabaseHelper;

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

            myDatabaseHelper = new MyDatabaseHelper(this);


            user_name = findViewById(R.id.username);
            user_email = findViewById(R.id.Email);

        SharedPreferences sharedPreferences = getSharedPreferences("userSession", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        if (username != null) {
             getInfo(username);

        }


    }

    public void getInfo(String username){
        Cursor cursor = myDatabaseHelper.getUserInfo(username);

        if(cursor.moveToFirst()){
            String name = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));

            user_name.setText(name);
            user_email.setText(email);


        }

    }


    public void logout(){
        SharedPreferences sharedPreferences = getSharedPreferences("userSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        finishAffinity();
        startActivity(new Intent(Profile.this, Login.class));
    }

}