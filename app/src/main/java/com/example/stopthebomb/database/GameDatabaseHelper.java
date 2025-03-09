package com.example.stopthebomb.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.stopthebomb.models.Achievement;
import com.example.stopthebomb.models.Ending;

import java.util.ArrayList;
import java.util.List;

public class GameDatabaseHelper extends SQLiteOpenHelper {
    // Database Info
    private static final String DATABASE_NAME = "gameProgress.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_ACHIEVEMENTS = "achievements";
    private static final String TABLE_ENDINGS = "endings";
    private static final String TABLE_PLAYER_ACHIEVEMENTS = "player_achievements";
    private static final String TABLE_PLAYER_ENDINGS = "player_endings";

    // Common Columns
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // Achievements Table Columns
    private static final String KEY_ACHIEVEMENT_NAME = "name";
    private static final String KEY_ACHIEVEMENT_DESCRIPTION = "description";
    private static final String KEY_ACHIEVEMENT_ICON = "icon";
    private static final String KEY_ACHIEVEMENT_POINTS = "points";

    // Endings Table Columns
    private static final String KEY_ENDING_NAME = "name";
    private static final String KEY_ENDING_DESCRIPTION = "description";
    private static final String KEY_ENDING_IMAGE = "image";

    // Player Achievements Table Columns
    private static final String KEY_PLAYER_ACHIEVEMENT_ID = "achievement_id";
    private static final String KEY_PLAYER_ACHIEVEMENT_UNLOCKED_DATE = "unlocked_date";

    // Player Endings Table Columns
    private static final String KEY_PLAYER_ENDING_ID = "ending_id";
    private static final String KEY_PLAYER_ENDING_DISCOVERED_DATE = "discovered_date";

    // Singleton instance
    private static GameDatabaseHelper instance;

    // Get singleton instance
    public static synchronized GameDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new GameDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private GameDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ACHIEVEMENTS_TABLE = "CREATE TABLE " + TABLE_ACHIEVEMENTS +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_ACHIEVEMENT_NAME + " TEXT NOT NULL," +
                KEY_ACHIEVEMENT_DESCRIPTION + " TEXT NOT NULL," +
                KEY_ACHIEVEMENT_ICON + " TEXT," +
                KEY_ACHIEVEMENT_POINTS + " INTEGER DEFAULT 0," +
                KEY_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";

        String CREATE_ENDINGS_TABLE = "CREATE TABLE " + TABLE_ENDINGS +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_ENDING_NAME + " TEXT NOT NULL," +
                KEY_ENDING_DESCRIPTION + " TEXT NOT NULL," +
                KEY_ENDING_IMAGE + " TEXT," +
                KEY_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";

        String CREATE_PLAYER_ACHIEVEMENTS_TABLE = "CREATE TABLE " + TABLE_PLAYER_ACHIEVEMENTS +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_PLAYER_ACHIEVEMENT_ID + " INTEGER NOT NULL," +
                KEY_PLAYER_ACHIEVEMENT_UNLOCKED_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (" + KEY_PLAYER_ACHIEVEMENT_ID + ") REFERENCES " +
                TABLE_ACHIEVEMENTS + "(" + KEY_ID + ")" +
                ")";

        String CREATE_PLAYER_ENDINGS_TABLE = "CREATE TABLE " + TABLE_PLAYER_ENDINGS +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_PLAYER_ENDING_ID + " INTEGER NOT NULL," +
                KEY_PLAYER_ENDING_DISCOVERED_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (" + KEY_PLAYER_ENDING_ID + ") REFERENCES " +
                TABLE_ENDINGS + "(" + KEY_ID + ")" +
                ")";

        db.execSQL(CREATE_ACHIEVEMENTS_TABLE);
        db.execSQL(CREATE_ENDINGS_TABLE);
        db.execSQL(CREATE_PLAYER_ACHIEVEMENTS_TABLE);
        db.execSQL(CREATE_PLAYER_ENDINGS_TABLE);

        // Populate default achievements and endings
        populateDefaultAchievements(db);
        populateDefaultEndings(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYER_ACHIEVEMENTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYER_ENDINGS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACHIEVEMENTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENDINGS);
            onCreate(db);
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Initialize default achievements
    private void populateDefaultAchievements(SQLiteDatabase db) {
        // Sample achievements - replace with your actual achievements
        addDefaultAchievement(db, "First Steps", "Complete the tutorial", "ic_achievement_tutorial", 10);
        addDefaultAchievement(db, "Explorer", "Discover all areas of the game", "ic_achievement_explorer", 25);
        addDefaultAchievement(db, "Master", "Complete the game with all endings", "ic_achievement_master", 100);
        // Add more achievements as needed
    }

    private void addDefaultAchievement(SQLiteDatabase db, String name, String description, String icon, int points) {
        ContentValues values = new ContentValues();
        values.put(KEY_ACHIEVEMENT_NAME, name);
        values.put(KEY_ACHIEVEMENT_DESCRIPTION, description);
        values.put(KEY_ACHIEVEMENT_ICON, icon);
        values.put(KEY_ACHIEVEMENT_POINTS, points);
        db.insert(TABLE_ACHIEVEMENTS, null, values);
    }

    // Initialize default endings
    private void populateDefaultEndings(SQLiteDatabase db) {
        // Sample endings - replace with your actual endings
        addDefaultEnding(db, "Happy Ending", "You saved the day and everyone lived happily ever after", "ending_happy");
        addDefaultEnding(db, "Sad Ending", "Not everyone made it, but your sacrifice wasn't in vain", "ending_sad");
        addDefaultEnding(db, "True Ending", "You discovered the truth behind everything", "ending_true");
        addDefaultEnding(db, "Secret Ending", "Only the most dedicated players find this ending", "ending_secret");
        addDefaultEnding(db, "Bad Ending", "Things didn't go as planned...", "ending_bad");
        // Add more endings as needed
    }

    private void addDefaultEnding(SQLiteDatabase db, String name, String description, String image) {
        ContentValues values = new ContentValues();
        values.put(KEY_ENDING_NAME, name);
        values.put(KEY_ENDING_DESCRIPTION, description);
        values.put(KEY_ENDING_IMAGE, image);
        db.insert(TABLE_ENDINGS, null, values);
    }

    // ACHIEVEMENT METHODS

    // Get all achievements (both unlocked and locked)
    public List<Achievement> getAllAchievements() {
        List<Achievement> achievements = new ArrayList<>();

        String ACHIEVEMENTS_SELECT_QUERY =
                "SELECT a.*, CASE WHEN pa." + KEY_ID + " IS NULL THEN 0 ELSE 1 END as unlocked, " +
                        "pa." + KEY_PLAYER_ACHIEVEMENT_UNLOCKED_DATE + " FROM " + TABLE_ACHIEVEMENTS + " a " +
                        "LEFT JOIN " + TABLE_PLAYER_ACHIEVEMENTS + " pa " +
                        "ON a." + KEY_ID + " = pa." + KEY_PLAYER_ACHIEVEMENT_ID;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(ACHIEVEMENTS_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Achievement achievement = new Achievement();
                    achievement.id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                    achievement.name = cursor.getString(cursor.getColumnIndex(KEY_ACHIEVEMENT_NAME));
                    achievement.description = cursor.getString(cursor.getColumnIndex(KEY_ACHIEVEMENT_DESCRIPTION));
                    achievement.iconResource = cursor.getString(cursor.getColumnIndex(KEY_ACHIEVEMENT_ICON));
                    achievement.points = cursor.getInt(cursor.getColumnIndex(KEY_ACHIEVEMENT_POINTS));
                    achievement.unlocked = cursor.getInt(cursor.getColumnIndex("unlocked")) == 1;
                    achievement.unlockedDate = achievement.unlocked ?
                            cursor.getString(cursor.getColumnIndex(KEY_PLAYER_ACHIEVEMENT_UNLOCKED_DATE)) : null;

                    achievements.add(achievement);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return achievements;
    }

    // Get only unlocked achievements
    public List<Achievement> getUnlockedAchievements() {
        List<Achievement> achievements = new ArrayList<>();

        String ACHIEVEMENTS_SELECT_QUERY =
                "SELECT a.*, pa." + KEY_PLAYER_ACHIEVEMENT_UNLOCKED_DATE + " FROM " +
                        TABLE_ACHIEVEMENTS + " a, " + TABLE_PLAYER_ACHIEVEMENTS + " pa " +
                        "WHERE a." + KEY_ID + " = pa." + KEY_PLAYER_ACHIEVEMENT_ID;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(ACHIEVEMENTS_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Achievement achievement = new Achievement();
                    achievement.id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                    achievement.name = cursor.getString(cursor.getColumnIndex(KEY_ACHIEVEMENT_NAME));
                    achievement.description = cursor.getString(cursor.getColumnIndex(KEY_ACHIEVEMENT_DESCRIPTION));
                    achievement.iconResource = cursor.getString(cursor.getColumnIndex(KEY_ACHIEVEMENT_ICON));
                    achievement.points = cursor.getInt(cursor.getColumnIndex(KEY_ACHIEVEMENT_POINTS));
                    achievement.unlocked = true;
                    achievement.unlockedDate = cursor.getString(cursor.getColumnIndex(KEY_PLAYER_ACHIEVEMENT_UNLOCKED_DATE));

                    achievements.add(achievement);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return achievements;
    }

    // Unlock an achievement
    public boolean unlockAchievement(String achievementName) {
        SQLiteDatabase db = getWritableDatabase();

        // First, check if the achievement exists
        String ACHIEVEMENT_SELECT_QUERY = "SELECT * FROM " + TABLE_ACHIEVEMENTS +
                " WHERE " + KEY_ACHIEVEMENT_NAME + " = ?";

        Cursor cursor = db.rawQuery(ACHIEVEMENT_SELECT_QUERY, new String[]{achievementName});

        try {
            if (cursor.moveToFirst()) {
                int achievementId = cursor.getInt(cursor.getColumnIndex(KEY_ID));

                // Now check if it's already unlocked
                String PLAYER_ACHIEVEMENT_SELECT_QUERY = "SELECT * FROM " + TABLE_PLAYER_ACHIEVEMENTS +
                        " WHERE " + KEY_PLAYER_ACHIEVEMENT_ID + " = ?";

                Cursor paCursor = db.rawQuery(PLAYER_ACHIEVEMENT_SELECT_QUERY,
                        new String[]{String.valueOf(achievementId)});

                try {
                    // If not already unlocked, unlock it
                    if (!paCursor.moveToFirst()) {
                        ContentValues values = new ContentValues();
                        values.put(KEY_PLAYER_ACHIEVEMENT_ID, achievementId);
                        db.insert(TABLE_PLAYER_ACHIEVEMENTS, null, values);
                        return true;
                    }
                } finally {
                    if (paCursor != null && !paCursor.isClosed()) {
                        paCursor.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return false;
    }

    // ENDING METHODS

    // Get all endings (both discovered and undiscovered)
    public List<Ending> getAllEndings() {
        List<Ending> endings = new ArrayList<>();

        String ENDINGS_SELECT_QUERY =
                "SELECT e.*, CASE WHEN pe." + KEY_ID + " IS NULL THEN 0 ELSE 1 END as discovered, " +
                        "pe." + KEY_PLAYER_ENDING_DISCOVERED_DATE + " FROM " + TABLE_ENDINGS + " e " +
                        "LEFT JOIN " + TABLE_PLAYER_ENDINGS + " pe " +
                        "ON e." + KEY_ID + " = pe." + KEY_PLAYER_ENDING_ID;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(ENDINGS_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Ending ending = new Ending();
                    ending.id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                    ending.name = cursor.getString(cursor.getColumnIndex(KEY_ENDING_NAME));
                    ending.description = cursor.getString(cursor.getColumnIndex(KEY_ENDING_DESCRIPTION));
                    ending.imageResource = cursor.getString(cursor.getColumnIndex(KEY_ENDING_IMAGE));
                    ending.discovered = cursor.getInt(cursor.getColumnIndex("discovered")) == 1;
                    ending.discoveredDate = ending.discovered ?
                            cursor.getString(cursor.getColumnIndex(KEY_PLAYER_ENDING_DISCOVERED_DATE)) : null;

                    endings.add(ending);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return endings;
    }

    // Get only discovered endings
    public List<Ending> getDiscoveredEndings() {
        List<Ending> endings = new ArrayList<>();

        String ENDINGS_SELECT_QUERY =
                "SELECT e.*, pe." + KEY_PLAYER_ENDING_DISCOVERED_DATE + " FROM " +
                        TABLE_ENDINGS + " e, " + TABLE_PLAYER_ENDINGS + " pe " +
                        "WHERE e." + KEY_ID + " = pe." + KEY_PLAYER_ENDING_ID;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(ENDINGS_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Ending ending = new Ending();
                    ending.id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                    ending.name = cursor.getString(cursor.getColumnIndex(KEY_ENDING_NAME));
                    ending.description = cursor.getString(cursor.getColumnIndex(KEY_ENDING_DESCRIPTION));
                    ending.imageResource = cursor.getString(cursor.getColumnIndex(KEY_ENDING_IMAGE));
                    ending.discovered = true;
                    ending.discoveredDate = cursor.getString(cursor.getColumnIndex(KEY_PLAYER_ENDING_DISCOVERED_DATE));

                    endings.add(ending);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return endings;
    }

    // Discover an ending
    public boolean discoverEnding(String endingName) {
        SQLiteDatabase db = getWritableDatabase();

        // First, check if the ending exists
        String ENDING_SELECT_QUERY = "SELECT * FROM " + TABLE_ENDINGS +
                " WHERE " + KEY_ENDING_NAME + " = ?";

        Cursor cursor = db.rawQuery(ENDING_SELECT_QUERY, new String[]{endingName});

        try {
            if (cursor.moveToFirst()) {
                int endingId = cursor.getInt(cursor.getColumnIndex(KEY_ID));

                // Now check if it's already discovered
                String PLAYER_ENDING_SELECT_QUERY = "SELECT * FROM " + TABLE_PLAYER_ENDINGS +
                        " WHERE " + KEY_PLAYER_ENDING_ID + " = ?";

                Cursor peCursor = db.rawQuery(PLAYER_ENDING_SELECT_QUERY,
                        new String[]{String.valueOf(endingId)});

                try {
                    // If not already discovered, mark it as discovered
                    if (!peCursor.moveToFirst()) {
                        ContentValues values = new ContentValues();
                        values.put(KEY_PLAYER_ENDING_ID, endingId);
                        db.insert(TABLE_PLAYER_ENDINGS, null, values);
                        return true;
                    }
                } finally {
                    if (peCursor != null && !peCursor.isClosed()) {
                        peCursor.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return false;
    }

    // Get achievement progress percentage
    public int getAchievementProgressPercentage() {
        SQLiteDatabase db = getReadableDatabase();

        int totalAchievements = 0;
        int unlockedAchievements = 0;

        // Get total count
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_ACHIEVEMENTS;
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor.moveToFirst()) {
            totalAchievements = cursor.getInt(0);
        }
        cursor.close();

        // Get unlocked count
        String unlockedQuery = "SELECT COUNT(*) FROM " + TABLE_PLAYER_ACHIEVEMENTS;
        cursor = db.rawQuery(unlockedQuery, null);
        if (cursor.moveToFirst()) {
            unlockedAchievements = cursor.getInt(0);
        }
        cursor.close();

        if (totalAchievements == 0) return 0;
        return (unlockedAchievements * 100) / totalAchievements;
    }

    // Get ending discovery percentage
    public int getEndingProgressPercentage() {
        SQLiteDatabase db = getReadableDatabase();

        int totalEndings = 0;
        int discoveredEndings = 0;

        // Get total count
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_ENDINGS;
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor.moveToFirst()) {
            totalEndings = cursor.getInt(0);
        }
        cursor.close();

        // Get discovered count
        String discoveredQuery = "SELECT COUNT(*) FROM " + TABLE_PLAYER_ENDINGS;
        cursor = db.rawQuery(discoveredQuery, null);
        if (cursor.moveToFirst()) {
            discoveredEndings = cursor.getInt(0);
        }
        cursor.close();

        if (totalEndings == 0) return 0;
        return (discoveredEndings * 100) / totalEndings;
    }

    // Get total player score (sum of all unlocked achievement points)
    public int getTotalPlayerScore() {
        SQLiteDatabase db = getReadableDatabase();

        String scoreQuery =
                "SELECT SUM(a." + KEY_ACHIEVEMENT_POINTS + ") FROM " +
                        TABLE_ACHIEVEMENTS + " a, " + TABLE_PLAYER_ACHIEVEMENTS + " pa " +
                        "WHERE a." + KEY_ID + " = pa." + KEY_PLAYER_ACHIEVEMENT_ID;

        Cursor cursor = db.rawQuery(scoreQuery, null);
        int score = 0;

        if (cursor.moveToFirst()) {
            score = cursor.getInt(0);
        }
        cursor.close();

        return score;
    }

    // Reset player progress (for testing or new game)
    public void resetProgress() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PLAYER_ACHIEVEMENTS, null, null);
        db.delete(TABLE_PLAYER_ENDINGS, null, null);
    }
}