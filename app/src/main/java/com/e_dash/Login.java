package com.e_dash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class Login extends AppCompatActivity {

    private EditText userName;
    private EditText userPass;
    private Button login;
    private TextView reg;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("userSession", Context.MODE_PRIVATE);

        // Check if already login
        if(sharedPreferences.getBoolean("isLoggedIn", false)){
            startActivity(new Intent(Login.this, Home.class));
            finish();
            return;
        }

        login = findViewById(R.id.log_in);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });


        reg = findViewById(R.id.register);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, MainActivity.class));
            }
        });


    }


    public void login(){

        MyDatabaseHelper db = new MyDatabaseHelper(Login.this);

        userName = findViewById(R.id.username);
        userPass = findViewById(R.id.password);
        login = findViewById(R.id.log_in);

        String user_name = userName.getText().toString();
        String pass_word = userPass.getText().toString();

        if(db.checkUser(user_name, pass_word)) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putString("username", user_name);
            editor.apply();

            Toast.makeText(Login.this, "Login Successful ", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Login.this, Home.class));
            finish();
        } else if (user_name.isEmpty() || pass_word.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
        }
    }

}