package com.example.stopthebomb.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.stopthebomb.models.Achievement;
import com.example.stopthebomb.models.Ending;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "game.db";
    private static final int DATABASE_VERSION = 1;

    // Endings table
    private static final String TABLE_ENDINGS = "endings";
    private static final String COLUMN_ENDING_ID = "id";
    private static final String COLUMN_ENDING_NAME = "name";
    private static final String COLUMN_ENDING_DESCRIPTION = "description";
    private static final String COLUMN_ENDING_IMAGE = "image_resource";
    private static final String COLUMN_ENDING_DISCOVERED = "discovered";
    private static final String COLUMN_ENDING_DISCOVERED_DATE = "discovered_date";

    // Achievements table
    private static final String TABLE_ACHIEVEMENTS = "achievements";
    private static final String COLUMN_ACHIEVEMENT_ID = "id";
    private static final String COLUMN_ACHIEVEMENT_NAME = "name";
    private static final String COLUMN_ACHIEVEMENT_DESCRIPTION = "description";
    private static final String COLUMN_ACHIEVEMENT_ICON = "icon_resource";
    private static final String COLUMN_ACHIEVEMENT_POINTS = "points";
    private static final String COLUMN_ACHIEVEMENT_UNLOCKED = "unlocked";
    private static final String COLUMN_ACHIEVEMENT_UNLOCKED_DATE = "unlocked_date";

    private static DatabaseHelper instance;

    // Singleton pattern to prevent multiple database connections
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create endings table
        String CREATE_ENDINGS_TABLE = "CREATE TABLE " + TABLE_ENDINGS + "("
                + COLUMN_ENDING_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_ENDING_NAME + " TEXT,"
                + COLUMN_ENDING_DESCRIPTION + " TEXT,"
                + COLUMN_ENDING_IMAGE + " TEXT,"
                + COLUMN_ENDING_DISCOVERED + " INTEGER,"
                + COLUMN_ENDING_DISCOVERED_DATE + " TEXT"
                + ")";
        db.execSQL(CREATE_ENDINGS_TABLE);

        // Create achievements table
        String CREATE_ACHIEVEMENTS_TABLE = "CREATE TABLE " + TABLE_ACHIEVEMENTS + "("
                + COLUMN_ACHIEVEMENT_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_ACHIEVEMENT_NAME + " TEXT,"
                + COLUMN_ACHIEVEMENT_DESCRIPTION + " TEXT,"
                + COLUMN_ACHIEVEMENT_ICON + " TEXT,"
                + COLUMN_ACHIEVEMENT_POINTS + " INTEGER,"
                + COLUMN_ACHIEVEMENT_UNLOCKED + " INTEGER,"
                + COLUMN_ACHIEVEMENT_UNLOCKED_DATE + " TEXT"
                + ")";
        db.execSQL(CREATE_ACHIEVEMENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // On upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENDINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACHIEVEMENTS);

        // Create new tables
        onCreate(db);
    }

    // Initialize default endings if needed
    public void initDefaultEndings() {
        if (getAllEndings().isEmpty()) {
            // Add your default endings here
            addEnding(new Ending(1, "Obediente", "Has obedecido al teniente y no has tocado nada", "ic_final1", false, null));
            addEnding(new Ending(2, "D.M.A", "Has soltado la bomba en el Kremlin. ¿Tendrán una máquina del juicio final?", "ic_final1", false, null));
            addEnding(new Ending(3, "Lovebomb", "Has evitado la catástrofe, ¡enhorabuena!", "ic_final1", false, null));

            // Add more default endings as needed
        }
    }

    // Initialize default achievements if needed
    public void initDefaultAchievements() {
        if (getAllAchievements().isEmpty()) {
            // Add your default achievements here
            addAchievement(new Achievement(1, "Paciencia", "No has tocado nada", "ic_final1", 10, false, null));
            addAchievement(new Achievement(2, "Identidad descubierta", "Resulta que sabes hablar ruso", "ic_final1", 10, false, null));
            addAchievement(new Achievement(3, "Pacifista", "Desviación de la bomba exitosa", "ic_final1", 10, false, null));
            addAchievement(new Achievement(4, "T.J. Kong", "¡Cabalga esa bomba!", "ic_final1", 10, false, null));
            addAchievement(new Achievement(5, "Ruta desviada", "No se atreverán a bombardear este otro sitio, ¿verdad?", "ic_final1", 10, false, null));
            addAchievement(new Achievement(6, "Genocida", "Esto te pasa por hacerles caso a los imperialistas", "ic_final1", 10, false, null));
            addAchievement(new Achievement(7, "Genocida", "Esto te pasa por hacerles caso a los imperialistas", "ic_final1", 10, false, null));

        }
    }

    // Methods for Endings
    public void addEnding(Ending ending) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_ENDING_ID, ending.id);
        values.put(COLUMN_ENDING_NAME, ending.name);
        values.put(COLUMN_ENDING_DESCRIPTION, ending.description);
        values.put(COLUMN_ENDING_IMAGE, ending.imageResource);
        values.put(COLUMN_ENDING_DISCOVERED, ending.discovered ? 1 : 0);
        values.put(COLUMN_ENDING_DISCOVERED_DATE, ending.discoveredDate);

        db.insert(TABLE_ENDINGS, null, values);
    }

    public List<Ending> getAllEndings() {
        List<Ending> endingList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ENDINGS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Ending ending = new Ending();
                ending.id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ENDING_ID));
                ending.name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENDING_NAME));
                ending.description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENDING_DESCRIPTION));
                ending.imageResource = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENDING_IMAGE));
                ending.discovered = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ENDING_DISCOVERED)) == 1;
                ending.discoveredDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENDING_DISCOVERED_DATE));

                endingList.add(ending);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return endingList;
    }

    public void discoverEnding(int endingId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateTime = dateFormat.format(new Date());

        values.put(COLUMN_ENDING_DISCOVERED, 1);
        values.put(COLUMN_ENDING_DISCOVERED_DATE, currentDateTime);

        db.update(TABLE_ENDINGS, values, COLUMN_ENDING_ID + " = ?",
                new String[]{String.valueOf(endingId)});
    }

    // Methods for Achievements
    public void addAchievement(Achievement achievement) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_ACHIEVEMENT_ID, achievement.id);
        values.put(COLUMN_ACHIEVEMENT_NAME, achievement.name);
        values.put(COLUMN_ACHIEVEMENT_DESCRIPTION, achievement.description);
        values.put(COLUMN_ACHIEVEMENT_ICON, achievement.iconResource);
        values.put(COLUMN_ACHIEVEMENT_POINTS, achievement.points);
        values.put(COLUMN_ACHIEVEMENT_UNLOCKED, achievement.unlocked ? 1 : 0);
        values.put(COLUMN_ACHIEVEMENT_UNLOCKED_DATE, achievement.unlockedDate);

        db.insert(TABLE_ACHIEVEMENTS, null, values);
    }

    public List<Achievement> getAllAchievements() {
        List<Achievement> achievementList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ACHIEVEMENTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Achievement achievement = new Achievement();
                achievement.id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACHIEVEMENT_ID));
                achievement.name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACHIEVEMENT_NAME));
                achievement.description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACHIEVEMENT_DESCRIPTION));
                achievement.iconResource = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACHIEVEMENT_ICON));
                achievement.points = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACHIEVEMENT_POINTS));
                achievement.unlocked = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACHIEVEMENT_UNLOCKED)) == 1;
                achievement.unlockedDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACHIEVEMENT_UNLOCKED_DATE));

                achievementList.add(achievement);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return achievementList;
    }

    public void unlockAchievement(int achievementId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateTime = dateFormat.format(new Date());

        values.put(COLUMN_ACHIEVEMENT_UNLOCKED, 1);
        values.put(COLUMN_ACHIEVEMENT_UNLOCKED_DATE, currentDateTime);

        db.update(TABLE_ACHIEVEMENTS, values, COLUMN_ACHIEVEMENT_ID + " = ?",
                new String[]{String.valueOf(achievementId)});
    }

    // Get total achievement points
    public int getTotalAchievementPoints() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COLUMN_ACHIEVEMENT_POINTS + ") FROM " + TABLE_ACHIEVEMENTS +
                        " WHERE " + COLUMN_ACHIEVEMENT_UNLOCKED + " = 1", null);

        int totalPoints = 0;
        if (cursor.moveToFirst()) {
            totalPoints = cursor.getInt(0);
        }
        cursor.close();
        return totalPoints;
    }
}