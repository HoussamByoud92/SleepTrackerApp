package com.example.sleeptrackerapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import android.text.format.DateFormat;

import com.example.sleeptrackerapp.database.DBHelper;
import com.example.sleeptrackerapp.R;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView welcomeMsg, avgSleepText, sleepLogText;
    Button recordSleepBtn;
    DBHelper db;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeMsg = findViewById(R.id.welcomeText);
        avgSleepText = findViewById(R.id.avgSleepText);
        sleepLogText = findViewById(R.id.sleepLogText);
        recordSleepBtn = findViewById(R.id.recordSleepBtn);

        db = new DBHelper(this);
        userId = getIntent().getIntExtra("user_id", -1);

        welcomeMsg.setText("Welcome back! User ID: " + userId);
        updateStats();

        recordSleepBtn.setOnClickListener(v -> showSleepInputDialog());
    }

    private void showSleepInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_sleep_input, null);
        EditText hoursInput = view.findViewById(R.id.hoursInput);
        builder.setView(view);

        builder.setTitle("Record Sleep");
        builder.setPositiveButton("Save", (dialog, which) -> {
            String hoursStr = hoursInput.getText().toString();
            if (!hoursStr.isEmpty()) {
                double hours = Double.parseDouble(hoursStr);
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                saveSleepData(userId, hours, currentDate);
                updateStats();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
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
        Cursor cursor = readableDB.rawQuery("SELECT sleep_hours, date FROM sleep WHERE user_id = ?", new String[]{String.valueOf(userId)});

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
