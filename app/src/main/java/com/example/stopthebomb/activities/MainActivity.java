package com.example.stopthebomb.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatDelegate;
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

        //Configuraciones:
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        cargarPreferencias();
        configurarListenerPreferencias();

        // Solicitar permiso de notificaciones:
        solicitarPermisoNotificaciones();

        // Inicializar botones:
        Button btnPlay = findViewById(R.id.btnPlay);
        Button btnAchievements = findViewById(R.id.btnAchievements);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnTutorial = findViewById(R.id.btnFinales);
        Button btnComments = findViewById(R.id.btnComments);

        // Listeners:
        btnPlay.setOnClickListener(v -> {
            Intent gameIntent = new Intent(MainActivity.this, GameActivity.class);
            gameIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(gameIntent);
        });

        btnAchievements.setOnClickListener(v -> {
            Intent winnerBoardIntent = new Intent(MainActivity.this, AchievementsActivity.class);
            startActivity(winnerBoardIntent);
        });

        btnSettings.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        });

        btnComments.setOnClickListener(v -> {
            Intent commentsIntent = new Intent(MainActivity.this, CommentsActivity.class);
            startActivity(commentsIntent);
        });


        btnTutorial.setOnClickListener(v -> {
            Intent tutorialIntent = new Intent(MainActivity.this, EndingsActivity.class);
            startActivity(tutorialIntent);
        });

        ImageView bgAtomBomb = findViewById(R.id.bgAtomBomb);
        ObjectAnimator animator = ObjectAnimator.ofFloat(bgAtomBomb, "translationY", -50f, 50f);
        animator.setDuration(3000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();

    }

    private void solicitarPermisoNotificaciones() {
        // Permisos de notificaciones:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    // Aplicar el Locale guardado:
    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(newBase);
        String language = prefs.getString("language_preference", "es");

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Context context = createConfigurationContext(newBase);
        super.attachBaseContext(context);
    }

    // Crear un context de configuración:
    private Context createConfigurationContext(Context context) {
        Resources resources = context.getResources();
        Configuration configuration = new Configuration(resources.getConfiguration());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String language = prefs.getString("language_preference", "es");
        Locale locale = new Locale(language);

        configuration.setLocale(locale);

        return context.createConfigurationContext(configuration);
    }

    private void cargarPreferencias() {
        String idioma = sharedPreferences.getString("language_preference", "es");
        boolean sonidoActivo = sharedPreferences.getBoolean("sound_preference", true);
        boolean vibracionActiva = sharedPreferences.getBoolean("vibration_preference", true);
        boolean darkModeActivo = sharedPreferences.getBoolean("dark_mode", false); // Nuevo

        Log.d("Preferencias", "Idioma: " + idioma);
        Log.d("Preferencias", "Sonido Activo: " + sonidoActivo);
        Log.d("Preferencias", "Vibración Activa: " + vibracionActiva);
        Log.d("Preferencias", "Modo Oscuro: " + darkModeActivo);

        if (darkModeActivo) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void configurarListenerPreferencias() {
        preferenceChangeListener = (sharedPreferences, key) -> {
            if (key.equals("language_preference")) {
                String nuevoIdioma = sharedPreferences.getString(key, "es");
                Log.d("Preferencias", "Nuevo Idioma: " + nuevoIdioma);
                recreate();
            } else if (key.equals("sound_preference")) {
                boolean sonidoActivo = sharedPreferences.getBoolean(key, true);
                Log.d("Preferencias", "Sonido Activo: " + sonidoActivo);
            } else if (key.equals("vibration_preference")) {
                boolean vibracionActiva = sharedPreferences.getBoolean(key, true);
                Log.d("Preferencias", "Vibración Activa: " + vibracionActiva);
            }
            else if (key.equals("dark_mode")) { // Nuevo
                boolean darkModeActivo = sharedPreferences.getBoolean(key, false);
                Log.d("Preferencias", "Modo Oscuro Activado: " + darkModeActivo);
                aplicarModoOscuro(darkModeActivo);
            }
        };

        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    private void aplicarModoOscuro(boolean activar) {
        if (activar) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        // Reiniciamos actividad para aplicar los cambios:
        recreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (preferenceChangeListener != null) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        }
    }
}
