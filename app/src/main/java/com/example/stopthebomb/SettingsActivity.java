package com.example.stopthebomb;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchSounds, switchVibration;
    private Button btnSaveSettings, btnBackToMain;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);

        // Find views
        switchSounds = findViewById(R.id.switchSounds);
        switchVibration = findViewById(R.id.switchVibration);
        btnSaveSettings = findViewById(R.id.btnSaveSettings);
        btnBackToMain = findViewById(R.id.btnBackToMain);

        // Load previous settings
        loadSettings();

        // Save settings button
        btnSaveSettings.setOnClickListener(v -> saveSettings());

        // Back to main menu
        btnBackToMain.setOnClickListener(v -> finish());
    }

    private void loadSettings() {
        boolean soundEnabled = sharedPreferences.getBoolean("soundEnabled", true);
        boolean vibrationEnabled = sharedPreferences.getBoolean("vibrationEnabled", true);

        switchSounds.setChecked(soundEnabled);
        switchVibration.setChecked(vibrationEnabled);
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("soundEnabled", switchSounds.isChecked());
        editor.putBoolean("vibrationEnabled", switchVibration.isChecked());

        editor.apply();

        Toast.makeText(this, "Settings Saved!", Toast.LENGTH_SHORT).show();
    }
}