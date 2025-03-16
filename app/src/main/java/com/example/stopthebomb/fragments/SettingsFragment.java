package com.example.stopthebomb.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.example.stopthebomb.R;
import com.example.stopthebomb.activities.MainActivity;


public class SettingsFragment extends PreferenceFragmentCompat {
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        // Configure listener for language preference changes
        ListPreference languagePreference = findPreference("language_preference");
        if (languagePreference != null) {
            languagePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                String newLanguage = (String) newValue;

                // Apply the new language
                updateLocale(newLanguage);

                // Restart all activities to apply changes globally
                restartApp();

                return true;
            });

        }
        SwitchPreferenceCompat darkModePreference = findPreference("dark_mode");
        if (darkModePreference != null) {
            darkModePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isDarkModeEnabled = (boolean) newValue;

                // Save preference
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("dark_mode", isDarkModeEnabled);
                editor.apply();

                // Apply theme change
                applyDarkMode(isDarkModeEnabled);

                return true;
            });
        }
    }

    private void updateLocale(String languageCode) {
        // Save the language preference
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("language_preference", languageCode);
        editor.apply();

        // Instead of calling setLocale on MyApplication, recreate the activity
        if (getActivity() != null) {
            // Recreate the current activity to apply the locale change
            // This will trigger attachBaseContext in BaseActivity with the new locale
            getActivity().recreate();
        }
    }

    private void applyDarkMode(boolean enableDarkMode) {
        if (enableDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Restart activity to apply the theme change
        if (getActivity() != null) {
            getActivity().recreate();
        }
    }

    // Method to restart the app to apply language change fully
    private void restartApp() {
        if (getActivity() == null) return;

        // Create intent for the main activity with flags to clear the task
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Start the activity
        startActivity(intent);

        // Optional: add a fade animation
        if (getActivity() != null) {
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            getActivity().finish();
        }
    }

}
