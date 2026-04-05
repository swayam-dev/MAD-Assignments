package com.example.convertcurrency;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        SwitchCompat darkModeSwitch = findViewById(R.id.darkModeSwitch);

        // 1. Load the saved setting (Default is False/Light Mode)
        SharedPreferences sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);

        // Set the toggle to the correct position based on saved data
        darkModeSwitch.setChecked(isDarkMode);

        // 2. Handle the toggle click
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Turn ON Dark Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                saveThemeState(true);
            } else {
                // Turn OFF Dark Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                saveThemeState(false);
            }
        });
    }

    // Helper method to save the choice to the phone's memory
    private void saveThemeState(boolean isDark) {
        SharedPreferences sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isDarkMode", isDark);
        editor.apply();
    }
}
