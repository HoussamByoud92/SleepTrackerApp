package com.example.sleeptrackerapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.widget.*;
import com.example.sleeptrackerapp.database.DBHelper;
import com.example.sleeptrackerapp.R;

public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri = null;

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
        EditText usernameInput = findViewById(R.id.usernameInput);
        ImageView profileImage = findViewById(R.id.profileImage);
        Button selectImageBtn = findViewById(R.id.selectImageBtn);

        selectImageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });


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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            ImageView profileImage = findViewById(R.id.profileImage);
            profileImage.setImageURI(selectedImageUri);
        }
    }

}
