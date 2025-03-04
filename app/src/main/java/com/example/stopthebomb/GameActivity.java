package com.example.stopthebomb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private int score = 0;
    private TextView tvScore, tvGameInstruction;
    private ImageView ivGamePanel;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        tvScore = findViewById(R.id.tvScore);
        tvGameInstruction = findViewById(R.id.tvGameInstruction);
        ivGamePanel = findViewById(R.id.ivGamePanel);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // Game logic: touch detection
        ivGamePanel.setOnClickListener(v -> {
            // Simulate game over or penalty
            score--;
            updateScore();
            showRandomPenalty();
        });
    }

    private void updateScore() {
        tvScore.setText("Score: " + score);
    }

    private void showRandomPenalty() {
        String[] penalties = {
                "Oops! Don't touch!",
                "Wrong move!",
                "Stay calm!",
                "Careful now!"
        };

        String penalty = penalties[new Random().nextInt(penalties.length)];
        Toast.makeText(this, penalty, Toast.LENGTH_SHORT).show();
    }
}
