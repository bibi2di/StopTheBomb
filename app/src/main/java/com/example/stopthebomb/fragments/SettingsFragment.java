package com.example.stopthebomb.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

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

            // Configure listener for notification permission toggle
            CheckBoxPreference notificationPermissionPreference = findPreference("notification_permission");
            if (notificationPermissionPreference != null) {
                notificationPermissionPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean enableNotifications = (boolean) newValue;
                    if (enableNotifications) {
                        requestNotificationPermission();
                    } else {
                        disableNotifications();
                    }
                    return true;
                });
            }
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

        // Apply the locale change to the application
        if (getActivity() != null && getActivity().getApplication() instanceof MyApplication) {
            ((MyApplication) getActivity().getApplication()).setLocale(languageCode);
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

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            } else {
                // Notification permission already granted, proceed to enable notifications
                Toast.makeText(getActivity(), "Notification permission granted!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void disableNotifications() {
        // Handle the case when notifications are disabled
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
        notificationManager.cancelAll(); // This will cancel all notifications.

        Toast.makeText(getActivity(), "Notifications disabled!", Toast.LENGTH_SHORT).show();
    }
}
