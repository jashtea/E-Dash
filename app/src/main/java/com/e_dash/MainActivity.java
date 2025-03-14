package com.e_dash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText user_name;
    private EditText user_email;
    private EditText user_password;
    private EditText confirm_password;

    private Button sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Initialize sign up button
        sign_up = findViewById(R.id.signup);

        // Initialize login
        TextView login = findViewById(R.id.login);

        //Add listener to login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method();
            }
        });

    }

    public void method(){
        // Initialize database helper
        MyDatabaseHelper db = new MyDatabaseHelper(MainActivity.this);

        //Initialize user information
        user_name = findViewById(R.id.Username);
        user_email = findViewById(R.id.email);
        user_password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);

        // Get user input
        String userPassword = user_password.getText().toString();
        String confirmPassword = confirm_password.getText().toString();
        String userName = user_name.getText().toString();
        String userEmail = user_email.getText().toString();

        if(userName.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty() || confirmPassword.isEmpty()){
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();

        }else if(!userPassword.equals(confirmPassword)){
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        }else{
            boolean isRegistered = db.insertUser(userName, userEmail, userPassword);
            if(isRegistered){
                Toast.makeText(MainActivity.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
            }else{
                Toast.makeText(MainActivity.this, "Register Failed", Toast.LENGTH_SHORT).show();
            }


        }

    }
}