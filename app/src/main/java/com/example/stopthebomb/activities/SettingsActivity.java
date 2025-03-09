package com.example.stopthebomb.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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

    // This is redundant with onSupportNavigateUp, but keeping it for completeness
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
}
    /*@SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchSounds, switchVibration;
    private Button btnSaveSettings, btnBackToMain;
    private Spinner spinnerLanguage;
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
        spinnerLanguage = findViewById(R.id.spinnerLanguage);

        // Load previous settings
        loadSettings();

        // Save settings button
        btnSaveSettings.setOnClickListener(v -> saveSettings());

        // Back to main menu
        btnBackToMain.setOnClickListener(v -> finish());

        setupLanguageSpinner();
    }

    private void setupLanguageSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.languages_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        String savedLanguage = sharedPreferences.getString("selectedLanguage", "es");
        switch (savedLanguage) {
            case "en":
                spinnerLanguage.setSelection(0);
                break;
            case "eu":
                spinnerLanguage.setSelection(1);
                break;
            default:
                spinnerLanguage.setSelection(2);
                break;
        }
        // Añadir el listener para el Spinner

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

        String selectedLanguage = "es"; // Predeterminado: Castellano
        int selectedPosition = spinnerLanguage.getSelectedItemPosition();
        if (selectedPosition == 0) selectedLanguage = "en"; // Inglés
        else if (selectedPosition == 1) selectedLanguage = "eu"; // Euskera

        editor.putString("selectedLanguage", selectedLanguage);
        editor.apply();

        //Toast.makeText(this, getString(R.string.settings_saved), Toast.LENGTH_SHORT).show();

        // Recargar la actividad actual para aplicar el nuevo idioma
        //recreate();

        applyLanguage(selectedLanguage);
        //recreate();

        Toast.makeText(this, getString(R.string.settings_saved), Toast.LENGTH_SHORT).show();

        // Regresa al MainActivity para ver el cambio de idioma en tiempo real
        //Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //startActivity(intent);
        //finish();
        finish();
        startActivity(getIntent());
    }
    private void applyLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        //Configuration config = new Configuration();
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.setLocale(locale);
        //config.setLocale(locale);
        Context context = getBaseContext().createConfigurationContext(config);
        getBaseContext().getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
*/
