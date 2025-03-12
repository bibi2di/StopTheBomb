package com.example.stopthebomb.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stopthebomb.R;
import com.example.stopthebomb.database.DatabaseHelper;
import com.example.stopthebomb.models.Ending;

import java.util.List;

public class EndingsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EndingsAdapter adapter;
    private TextView discoveredCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endings);

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.endings_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize discovered count TextView
        discoveredCountTextView = findViewById(R.id.discovered_count_text_view);

        // Get database helper
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);

        // Initialize default endings if needed
        dbHelper.initDefaultEndings();

        // Get all endings
        List<Ending> endings = dbHelper.getAllEndings();

        // Set up adapter
        adapter = new EndingsAdapter(endings);
        recyclerView.setAdapter(adapter);

        // Display discovered count
        int discoveredCount = 0;
        for (Ending ending : endings) {
            if (ending.discovered) {
                discoveredCount++;
            }
        }
        discoveredCountTextView.setText("Discovered: " + discoveredCount + "/" + endings.size());
    }

    // Adapter for the Endings RecyclerView
    private class EndingsAdapter extends RecyclerView.Adapter<EndingsAdapter.ViewHolder> {
        private List<Ending> endingList;

        public EndingsAdapter(List<Ending> endingList) {
            this.endingList = endingList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_ending, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Ending ending = endingList.get(position);

            // Set visibility based on whether the ending is discovered
            if (ending.discovered) {
                holder.nameTextView.setText(ending.name);
                holder.descriptionTextView.setText(ending.description);
                holder.dateTextView.setText("Discovered: " + ending.discoveredDate);

                // Set the image from drawable resources
                if (ending.imageResource != null && !ending.imageResource.isEmpty()) {
                    int resourceId = getResources().getIdentifier(
                            ending.imageResource, "drawable", getPackageName());
                    if (resourceId != 0) {
                        holder.imageView.setImageResource(resourceId);
                    }
                }

                holder.lockedOverlayView.setVisibility(View.GONE);
            } else {
                // Show placeholder for undiscovered endings
                holder.nameTextView.setText("????");
                holder.descriptionTextView.setText("This ending hasn't been discovered yet");
                holder.dateTextView.setText("");
                holder.lockedOverlayView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return endingList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView nameTextView;
            TextView descriptionTextView;
            TextView dateTextView;
            View lockedOverlayView;

            ViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.ending_image);
                nameTextView = itemView.findViewById(R.id.ending_name);
                descriptionTextView = itemView.findViewById(R.id.ending_description);
                dateTextView = itemView.findViewById(R.id.ending_date);
                lockedOverlayView = itemView.findViewById(R.id.locked_overlay);
            }
        }
    }
}