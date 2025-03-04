package com.example.stopthebomb;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class WinnerBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner_board);

        ListView lvWinnerBoard = findViewById(R.id.lvWinnerBoard);
        Button btnBackToMain = findViewById(R.id.btnBackToMain);

        // Sample winner data (replace with actual data storage/retrieval)
        List<String> winners = new ArrayList<>();
        winners.add("Player 1 - Score: 100");
        winners.add("Player 2 - Score: 95");
        winners.add("Player 3 - Score: 90");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                winners
        );

        lvWinnerBoard.setAdapter(adapter);

        btnBackToMain.setOnClickListener(v -> finish());
    }
}