package com.example.sleeptrackerapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.*;
import com.example.sleeptrackerapp.database.DBHelper;
import com.example.sleeptrackerapp.R;

public class RegisterActivity extends AppCompatActivity {

    EditText emailInput, passwordInput;
    Button registerBtn;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        registerBtn = findViewById(R.id.registerBtn);
        db = new DBHelper(this);

        registerBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (db.registerUser(email, password)) {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Email already registered!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
