package com.example.stopthebomb.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stopthebomb.R;
import com.example.stopthebomb.database.DatabaseHelper;
import com.example.stopthebomb.models.Achievement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.List;

public class AchievementsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AchievementsAdapter adapter;
    private TextView totalPointsTextView;
    private ExecutorService executorService;
    private Handler uiHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);


        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.achievements_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize total points TextView
        totalPointsTextView = findViewById(R.id.total_points_text_view);

        // Get database helper
        executorService = Executors.newSingleThreadExecutor();

        // Initialize default achievements if needed
        uiHandler = new Handler(Looper.getMainLooper());
        loadAchievementsAndTotalPoints();


        Button btnBackToMain = findViewById(R.id.btnBackToMain);
        btnBackToMain.setOnClickListener(v -> finish());
    }

    private void loadAchievementsAndTotalPoints() {
        executorService.execute(() -> {
            DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
            dbHelper.initDefaultAchievements();
            // Get all achievements in the background
            List<Achievement> achievements = dbHelper.getAllAchievements();
            // Get all achievements

            int totalPoints = dbHelper.getTotalAchievementPoints();

            // Update the UI on the main thread
            runOnUiThread(() -> {
                // Set up adapter
                adapter = new AchievementsAdapter(achievements);
                recyclerView.setAdapter(adapter);

                // Display total points
                totalPointsTextView.setText("Total Points: " + totalPoints);
            });
        });
    }


    // Adapter for the Achievements RecyclerView
    private class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.ViewHolder> {
        private List<Achievement> achievementList;

        public AchievementsAdapter(List<Achievement> achievementList) {
            this.achievementList = achievementList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_achievement, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Achievement achievement = achievementList.get(position);

            holder.nameTextView.setText(achievement.name);
            holder.descriptionTextView.setText(achievement.description);
            holder.pointsTextView.setText(achievement.points + " pts");

            // Set the icon from drawable resources
            if (achievement.iconResource != null && !achievement.iconResource.isEmpty()) {
                int resourceId = getResources().getIdentifier(
                        achievement.iconResource, "drawable", getPackageName());
                if (resourceId != 0) {
                    holder.iconImageView.setImageResource(resourceId);
                }
            }

            // Adjust visibility based on whether the achievement is unlocked
            if (achievement.unlocked) {
                holder.lockedOverlayView.setVisibility(View.GONE);
                holder.dateTextView.setText("Unlocked: " + achievement.unlockedDate);
                holder.dateTextView.setVisibility(View.VISIBLE);
            } else {
                // Show placeholder for undiscovered endings
                holder.nameTextView.setText("????");
                holder.descriptionTextView.setText("This achievement hasn't been unlocked");
                holder.dateTextView.setText("");
                holder.lockedOverlayView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return achievementList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView iconImageView;
            TextView nameTextView;
            TextView descriptionTextView;
            TextView pointsTextView;
            TextView dateTextView;
            View lockedOverlayView;

            ViewHolder(View itemView) {
                super(itemView);
                iconImageView = itemView.findViewById(R.id.achievement_icon);
                nameTextView = itemView.findViewById(R.id.achievement_name);
                descriptionTextView = itemView.findViewById(R.id.achievement_description);
                pointsTextView = itemView.findViewById(R.id.achievement_points);
                dateTextView = itemView.findViewById(R.id.achievement_date);
                lockedOverlayView = itemView.findViewById(R.id.locked_overlay);
            }
        }
    }
}
