package com.example.stopthebomb;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.StrictMode;


public class MyApplication extends Application {

    public static final String CHANNEL_ID = "logro_channel";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectNonSdkApiUsage()
                .penaltyLog()
                .build());
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