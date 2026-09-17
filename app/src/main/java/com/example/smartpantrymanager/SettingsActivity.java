package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Button btnBackToPantry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        btnBackToPantry = findViewById(R.id.btnBackToPantry);

        btnBackToPantry.setOnClickListener(v -> {
            finish();
        });
    }
}