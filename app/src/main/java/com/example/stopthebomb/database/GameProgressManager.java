package com.example.stopthebomb.database;

import android.content.Context;

import com.example.stopthebomb.models.Achievement;
import com.example.stopthebomb.models.Ending;

import java.util.List;

public class GameProgressManager {
    private static GameProgressManager instance;
    private GameDatabaseHelper dbHelper;

    private GameProgressManager(Context context) {
        dbHelper = GameDatabaseHelper.getInstance(context);
    }

    public static synchronized GameProgressManager getInstance(Context context) {
        if (instance == null) {
            instance = new GameProgressManager(context.getApplicationContext());
        }
        return instance;
    }

    // Achievement methods
    public List<Achievement> getAllAchievements() {
        return dbHelper.getAllAchievements();
    }

    public List<Achievement> getUnlockedAchievements() {
        return dbHelper.getUnlockedAchievements();
    }

    public boolean unlockAchievement(String achievementName) {
        boolean result = dbHelper.unlockAchievement(achievementName);
        if (result) {
            // Here you could trigger notifications, animations, etc.
            // For example:
            // showAchievementUnlockedNotification(context, achievementName);
        }
        return result;
    }

    // Ending methods
    public List<Ending> getAllEndings() {
        return dbHelper.getAllEndings();
    }

    public List<Ending> getDiscoveredEndings() {
        return dbHelper.getDiscoveredEndings();
    }

    public boolean discoverEnding(String endingName) {
        boolean result = dbHelper.discoverEnding(endingName);
        if (result) {
            // Here you could trigger notifications, animations, etc.
            // For example:
            // showEndingDiscoveredNotification(context, endingName);

            // Check if all endings are discovered
            if (getEndingProgressPercentage() == 100) {
                // Unlock a special achievement for discovering all endings
                unlockAchievement("Master");
            }
        }
        return result;
    }

    // Progress tracking methods
    public int getAchievementProgressPercentage() {
        return dbHelper.getAchievementProgressPercentage();
    }

    public int getEndingProgressPercentage() {
        return dbHelper.getEndingProgressPercentage();
    }

    public int getTotalPlayerScore() {
        return dbHelper.getTotalPlayerScore();
    }

    // For debugging or new game
    public void resetProgress() {
        dbHelper.resetProgress();
    }
}

