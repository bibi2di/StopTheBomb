package com.example.stopthebomb.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.stopthebomb.R;
import com.example.stopthebomb.database.GameProgressManager;
import com.example.stopthebomb.models.Achievement;
import com.example.stopthebomb.models.Ending;

import java.util.ArrayList;
import java.util.List;

public class WinnerBoardActivity extends BaseActivity {

    private GameProgressManager progressManager;
    private RecyclerView combinedRecyclerView;
    private CombinedProgressAdapter combinedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combined_progress);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.progress_title);
        }

        // Initialize progress manager
        progressManager = GameProgressManager.getInstance(this);

        // Setup RecyclerView
        combinedRecyclerView = findViewById(R.id.combinedRecyclerView);
        combinedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load both achievements and endings
        List<Achievement> achievements = progressManager.getAllAchievements();
        List<Ending> endings = progressManager.getAllEndings();

        // Create and set the adapter
        combinedAdapter = new CombinedProgressAdapter(achievements, endings);
        combinedRecyclerView.setAdapter(combinedAdapter);

        // Display overall progress
        TextView overallProgressText = findViewById(R.id.overallProgressText);
        int achievementPercentage = progressManager.getAchievementProgressPercentage();
        int endingPercentage = progressManager.getEndingProgressPercentage();
        int overallPercentage = (achievementPercentage + endingPercentage) / 2;
        overallProgressText.setText(getString(R.string.overall_progress_format, overallPercentage));

        // Display total score
        TextView scoreText = findViewById(R.id.totalScoreText);
        int totalScore = progressManager.getTotalPlayerScore();
        scoreText.setText(getString(R.string.score_format, totalScore));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}