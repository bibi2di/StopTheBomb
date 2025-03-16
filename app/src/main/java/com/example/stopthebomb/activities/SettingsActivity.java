package com.example.stopthebomb.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.example.stopthebomb.R;
import com.example.stopthebomb.fragments.SettingsFragment;

import java.util.Locale;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    //Redundante pero:
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // Add this method to refresh the activity after a language change
    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(newBase);
        String language = prefs.getString("language_preference", "es");

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Context context = createConfigurationContext(newBase);
        super.attachBaseContext(context);
    }

    // Helper method to create a context with the user's preferred locale
    private Context createConfigurationContext(Context context) {
        Resources resources = context.getResources();
        Configuration configuration = new Configuration(resources.getConfiguration());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String language = prefs.getString("language_preference", "es");
        Locale locale = new Locale(language);

        configuration.setLocale(locale);

        return context.createConfigurationContext(configuration);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}