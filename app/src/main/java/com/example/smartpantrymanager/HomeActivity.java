package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class HomeActivity extends AppCompatActivity {

    private Button btnHomePantry;
    private Button btnHomeRecipes;
    private Button btnHomeSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences =
                getSharedPreferences(
                        "SmartPantrySettings",
                        MODE_PRIVATE
                );

        boolean darkModeEnabled =
                preferences.getBoolean("dark_mode", false);

        if (darkModeEnabled) {

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
            );

        } else {

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
            );
        }

        setContentView(R.layout.activity_home);

        btnHomePantry = findViewById(R.id.btnHomePantry);
        btnHomeRecipes = findViewById(R.id.btnHomeRecipes);
        btnHomeSettings = findViewById(R.id.btnHomeSettings);

        // Open the pantry screen
        btnHomePantry.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    MainActivity.class
            );

            startActivity(intent);
        });

        // Open the suggested recipes screen
        btnHomeRecipes.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    SuggestedRecipesActivity.class
            );

            startActivity(intent);
        });

        // Open the settings screen
        btnHomeSettings.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    SettingsActivity.class
            );

            startActivity(intent);
        });
    }
}