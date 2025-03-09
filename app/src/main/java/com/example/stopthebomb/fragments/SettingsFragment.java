package com.example.stopthebomb.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.stopthebomb.MyApplication;
import com.example.stopthebomb.R;
import com.example.stopthebomb.activities.MainActivity;

import java.util.Locale;

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
    }

    private void updateLocale(String languageCode) {
        // Save the language preference
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("language_preference", languageCode);
        editor.apply();

        // Apply the locale change to the application
        if (getActivity() != null && getActivity().getApplication() instanceof MyApplication) {
            ((MyApplication) getActivity().getApplication()).setLocale(languageCode);
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
