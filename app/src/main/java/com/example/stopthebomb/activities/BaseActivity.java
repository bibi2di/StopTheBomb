package com.example.stopthebomb.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(newBase);
        String language = prefs.getString("language_preference", "es");

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Context context = createConfigurationContext(newBase);
        super.attachBaseContext(context);
    }

    private Context createConfigurationContext(Context context) {
        Resources resources = context.getResources();
        Configuration configuration = new Configuration(resources.getConfiguration());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String language = prefs.getString("language_preference", "es");
        Locale locale = new Locale(language);

        configuration.setLocale(locale);

        return context.createConfigurationContext(configuration);
    }
}