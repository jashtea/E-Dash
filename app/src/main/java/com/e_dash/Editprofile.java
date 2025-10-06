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

        SharedPreferences sharedPreferences = getSharedPreferences("userSession", MODE_PRIVATE);
        String user_name = sharedPreferences.getString("username", null);

        // Views
        username = findViewById(R.id.username);
        useremail = findViewById(R.id.Email);
        Save = findViewById(R.id.Save);

        // Load user info from DB
        getUserDetail(user_name);

        // 🔹 Save Button - update username/email
        Save.setOnClickListener(v -> {
            String name = username.getText().toString().trim();
            String email = useremail.getText().toString().trim();

            boolean isUpdated = dbHelper.updateUserDetails(user_name, email, name);

            if (isUpdated) {
                Toast.makeText(this, "Details updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load username and email from database
    private void getUserDetail(String userName) {
        Cursor cursor = dbHelper.getUserDetails(userName);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String userEmail = cursor.getString(cursor.getColumnIndexOrThrow("email"));

            username.setText(name);
            useremail.setText(userEmail);
        }
        cursor.close();
    }
}
