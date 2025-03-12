package com.example.stopthebomb.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import com.example.stopthebomb.R;

import java.util.Locale;

public class MainActivity extends BaseActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        cargarPreferencias();
        configurarListenerPreferencias();

        // Solicitar permiso de notificaciones:
        solicitarPermisoNotificaciones();

        // Inicializar botones:
        Button btnPlay = findViewById(R.id.btnPlay);
        Button btnWinnerBoard = findViewById(R.id.btnWinnerBoard);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnTutorial = findViewById(R.id.btnTutorial);

        // Set click listeners
        btnPlay.setOnClickListener(v -> {
            Intent gameIntent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(gameIntent);
        });

        btnWinnerBoard.setOnClickListener(v -> {
            Intent winnerBoardIntent = new Intent(MainActivity.this, AchievementsActivity.class);
            startActivity(winnerBoardIntent);
        });

        btnSettings.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        });

        btnTutorial.setOnClickListener(v -> {
            Intent tutorialIntent = new Intent(MainActivity.this, EndingsActivity.class);
            startActivity(tutorialIntent);
        });
    }

    private void solicitarPermisoNotificaciones() {
        /// Check for permission first
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                return;
            }
        }
    }

    // Override attachBaseContext to apply saved locale
    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(newBase);
        String language = prefs.getString("language_preference", "es");

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Context context = createConfigurationContext(newBase);
        super.attachBaseContext(context);
    }

    // Helper method to create a configuration context with the user's preferred locale
    private Context createConfigurationContext(Context context) {
        Resources resources = context.getResources();
        Configuration configuration = new Configuration(resources.getConfiguration());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String language = prefs.getString("language_preference", "es");
        Locale locale = new Locale(language);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }

        return context.createConfigurationContext(configuration);
    }

    private void cargarPreferencias() {
        String idioma = sharedPreferences.getString("language_preference", "es");
        boolean sonidoActivo = sharedPreferences.getBoolean("sound_preference", true);
        boolean vibracionActiva = sharedPreferences.getBoolean("vibration_preference", true);

        Log.d("Preferencias", "Idioma: " + idioma);
        Log.d("Preferencias", "Sonido Activo: " + sonidoActivo);
        Log.d("Preferencias", "Vibración Activa: " + vibracionActiva);
    }

    private void configurarListenerPreferencias() {
        preferenceChangeListener = (sharedPreferences, key) -> {
            if (key.equals("language_preference")) {
                String nuevoIdioma = sharedPreferences.getString(key, "es");
                Log.d("Preferencias", "Nuevo Idioma: " + nuevoIdioma);
                // No need to apply language here as we'll recreate the activity
                recreate();
            } else if (key.equals("sound_preference")) {
                boolean sonidoActivo = sharedPreferences.getBoolean(key, true);
                Log.d("Preferencias", "Sonido Activo: " + sonidoActivo);
                // Apply sound settings
            } else if (key.equals("vibration_preference")) {
                boolean vibracionActiva = sharedPreferences.getBoolean(key, true);
                Log.d("Preferencias", "Vibración Activa: " + vibracionActiva);
                // Apply vibration settings
            }
        };

        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (preferenceChangeListener != null) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        }
    }
}
    /*private void applySavedLanguage() {
        SharedPreferences sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
        String selectedLanguage = sharedPreferences.getString("selectedLanguage", "es");

        Locale locale = new Locale(selectedLanguage);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }*/
