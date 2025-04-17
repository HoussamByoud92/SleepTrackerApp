package com.example.sleeptrackerapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("SleepTrackerPrefs", MODE_PRIVATE);
        boolean isFirstLaunch = prefs.getBoolean("isFirstLaunch", true);

        if (isFirstLaunch) {
            // Mark as launched
            prefs.edit().putBoolean("isFirstLaunch", false).apply();
            startActivity(new Intent(this, RegisterActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish(); // Prevent going back to this screen
    }
}
