package com.e_dash;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Editprofile extends AppCompatActivity {

    MyDatabaseHelper dbHelper;
    EditText username, useremail;
    Button Save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        dbHelper = new MyDatabaseHelper(this);

        // 🔹 Get username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("userSession", MODE_PRIVATE);
        String user_name = sharedPreferences.getString("username", null);

        // If user session not found, close activity
        if (user_name == null) {
            Toast.makeText(this, "User session not found. Please login again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 🔹 Initialize views
        username = findViewById(R.id.username);
        useremail = findViewById(R.id.Email);
        Save = findViewById(R.id.Save);

        // 🔹 Load user info from database
        getUserDetail(user_name);

        // 🔹 Save button click listener
        Save.setOnClickListener(v -> {
            String name = username.getText().toString().trim();
            String email = useremail.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please enter both username and email.", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isUpdated = dbHelper.updateUserDetails(user_name, email, name);

            if (isUpdated) {
                Toast.makeText(this, "Details updated successfully", Toast.LENGTH_SHORT).show();

                // Optional: Update SharedPreferences with new username
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", name);
                editor.apply();
            } else {
                Toast.makeText(this, "Failed to update details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Load username and email from database
     */
    private void getUserDetail(String userName) {
        if (userName == null) return; // Safety check

        Cursor cursor = dbHelper.getUserDetails(userName);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String userEmail = cursor.getString(cursor.getColumnIndexOrThrow("email"));

                username.setText(name);
                useremail.setText(userEmail);
            } else {
                Toast.makeText(this, "User not found in database.", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        }
    }
}
