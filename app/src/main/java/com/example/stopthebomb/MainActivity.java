package com.example.stopthebomb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize buttons
        Button btnPlay = findViewById(R.id.btnPlay);
        Button btnWinnerBoard = findViewById(R.id.btnWinnerBoard);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnTutorial = findViewById(R.id.btnTutorial);


        // Set click listeners
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Navigate to the game activity
                Intent gameIntent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(gameIntent);
            }
        });

        btnWinnerBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Navigate to the winner board activity
                Intent winnerBoardIntent = new Intent(MainActivity.this, WinnerBoardActivity.class);
                startActivity(winnerBoardIntent);
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Navigate to the settings activity
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });

        btnTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Navigate to the tutorial activity
                Intent tutorialIntent = new Intent(MainActivity.this, TutorialActivity.class);
                startActivity(tutorialIntent);
            }
        });


    }

}