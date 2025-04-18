package com.example.sleeptrackerapp.activities;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import android.content.SharedPreferences;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.example.sleeptrackerapp.database.DBHelper;
import com.example.sleeptrackerapp.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView welcomeMsg, avgSleepText, sleepLogText, sleepTimerText;
    Button timerSleepBtn;
    DBHelper db;
    int userId;

    long sleepStartTime = -1;
    boolean isTimerRunning = false;
    SharedPreferences prefs;

    Handler timerHandler = new Handler();
    Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeMsg = findViewById(R.id.welcomeText);
        avgSleepText = findViewById(R.id.avgSleepText);
        sleepLogText = findViewById(R.id.sleepLogText);
        sleepTimerText = findViewById(R.id.sleepTimerText);
        timerSleepBtn = findViewById(R.id.timerSleepBtn);
        Button logoutBtn = findViewById(R.id.logoutBtn);
        Button profileBtn = findViewById(R.id.profileBtn);

        logoutBtn.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears backstack
            startActivity(intent);
            finish();
        });

        profileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });


        db = new DBHelper(this);
        userId = getIntent().getIntExtra("user_id", -1);
        String username = getIntent().getStringExtra("username");
        welcomeMsg.setText("Welcome back " +username+" !" );

        prefs = getSharedPreferences("SleepTrackerPrefs", MODE_PRIVATE);
        sleepStartTime = prefs.getLong("sleepStartTime", -1);
        isTimerRunning = (sleepStartTime != -1);

        updateButtonText();
        updateStats();

        if (isTimerRunning) {
            startSleepTimer();
        }

        timerSleepBtn.setOnClickListener(v -> {
            if (!isTimerRunning) {
                // Start timer
                sleepStartTime = System.currentTimeMillis();
                prefs.edit().putLong("sleepStartTime", sleepStartTime).apply();
                isTimerRunning = true;
                updateButtonText();
                startSleepTimer();
                Toast.makeText(this, "Sleep tracking started.", Toast.LENGTH_SHORT).show();
            } else {
                // Stop timer
                long endTime = System.currentTimeMillis();
                double hoursSlept = (endTime - sleepStartTime) / (1000.0 * 60 * 60); // ms to hours
                DecimalFormat df = new DecimalFormat("#.##");

                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                saveSleepData(userId, Double.parseDouble(df.format(hoursSlept)), currentDate);
                updateStats();

                prefs.edit().remove("sleepStartTime").apply();
                sleepStartTime = -1;
                isTimerRunning = false;
                stopSleepTimer();
                updateButtonText();

                Toast.makeText(this, "Sleep recorded: " + df.format(hoursSlept) + " hrs", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateButtonText() {
        timerSleepBtn.setText(isTimerRunning ? "Stop & Save Sleep" : "Start Sleep");
    }

    private void startSleepTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsed = System.currentTimeMillis() - sleepStartTime;
                sleepTimerText.setText("Timer: " + formatDuration(elapsed));
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.post(timerRunnable);
    }

    private void stopSleepTimer() {
        timerHandler.removeCallbacks(timerRunnable);
        sleepTimerText.setText("Timer: 00:00:00");
    }

    private String formatDuration(long durationMillis) {
        long seconds = durationMillis / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs);
    }

    private void saveSleepData(int userId, double hours, String date) {
        SQLiteDatabase writableDB = db.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("sleep_hours", hours);
        values.put("date", date);
        writableDB.insert("sleep", null, values);
    }

    private void updateStats() {
        SQLiteDatabase readableDB = db.getReadableDatabase();
        Cursor cursor = readableDB.rawQuery(
                "SELECT sleep_hours, date FROM sleep WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );

        StringBuilder logBuilder = new StringBuilder();
        double totalHours = 0;
        int count = 0;

        while (cursor.moveToNext()) {
            double hours = cursor.getDouble(0);
            String date = cursor.getString(1);
            logBuilder.append("🛌 ").append(date).append(" - ").append(hours).append(" hrs\n");
            totalHours += hours;
            count++;
        }

        if (count > 0) {
            double avg = totalHours / count;
            DecimalFormat df = new DecimalFormat("#.##");
            avgSleepText.setText("Average Sleep: " + df.format(avg) + " hrs");
        } else {
            avgSleepText.setText("No sleep data yet.");
        }

        sleepLogText.setText(logBuilder.toString());
        cursor.close();
    }
}
