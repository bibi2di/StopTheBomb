package com.example.stopthebomb;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.res.Configuration;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import java.util.Locale;

public class MyApplication extends Application {

    public static final String CHANNEL_ID = "logro_channel";
    @Override
    public void onCreate() {
        super.onCreate();

        // Apply language globally from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String idioma = sharedPreferences.getString("language_preference", "es");
        setLocale(idioma);
        createNotificationChannel();
    }

    // Improved method for changing locale across the app
    public void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = new Configuration(resources.getConfiguration());

        config.setLocale(locale);

        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    private void createNotificationChannel() {
        CharSequence name = "Logros";
        String description = "Canal para notificaciones de logros";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }
}