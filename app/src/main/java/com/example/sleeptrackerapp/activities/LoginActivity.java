package com.example.sleeptrackerapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.*;
import com.example.sleeptrackerapp.database.DBHelper;
import com.example.sleeptrackerapp.R;

public class LoginActivity extends AppCompatActivity {

    EditText emailInput, passwordInput;
    Button loginBtn, registerLink;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        registerLink = findViewById(R.id.registerLink);
        db = new DBHelper(this);

        loginBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (db.checkUser(email, password)) {
                int userId = db.getUserId(email);
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                i.putExtra("user_id", userId);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }
}
