package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat switchDarkMode;
    private SwitchCompat switchShowExpiryDates;
    private SwitchCompat switchConfirmDelete;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchShowExpiryDates = findViewById(R.id.switchShowExpiryDates);
        switchConfirmDelete = findViewById(R.id.switchConfirmDelete);

        preferences = getSharedPreferences(
                "SmartPantrySettings",
                MODE_PRIVATE
        );

        // Load saved settings
        boolean darkModeEnabled =
                preferences.getBoolean("dark_mode", false);

        boolean showExpiryDates =
                preferences.getBoolean("show_expiry_dates", true);

        boolean confirmDelete =
                preferences.getBoolean("confirm_before_delete", true);

        switchDarkMode.setChecked(darkModeEnabled);
        switchShowExpiryDates.setChecked(showExpiryDates);
        switchConfirmDelete.setChecked(confirmDelete);

        // Dark Mode
        switchDarkMode.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    preferences.edit()
                            .putBoolean("dark_mode", isChecked)
                            .apply();

                    if (isChecked) {

                        AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_YES
                        );

                    } else {

                        AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_NO
                        );
                    }
                }
        );

        // Show or hide expiry dates in the pantry
        switchShowExpiryDates.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    preferences.edit()
                            .putBoolean(
                                    "show_expiry_dates",
                                    isChecked
                            )
                            .apply();
                }
        );

        // Save the delete confirmation setting
        switchConfirmDelete.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    preferences.edit()
                            .putBoolean(
                                    "confirm_before_delete",
                                    isChecked
                            )
                            .apply();
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}