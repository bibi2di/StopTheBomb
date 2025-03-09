package com.example.stopthebomb.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stopthebomb.R;
import com.example.stopthebomb.models.Achievement;
import com.example.stopthebomb.models.Ending;

import java.util.ArrayList;
import java.util.List;

public class CombinedProgressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_SECTION_HEADER = 0;
    private static final int VIEW_TYPE_ACHIEVEMENT = 1;
    private static final int VIEW_TYPE_ENDING = 2;

    private List<Object> combinedItems = new ArrayList<>();

    public CombinedProgressAdapter(List<Achievement> achievements, List<Ending> endings) {
        // Add Achievements section header
        combinedItems.add("Achievements");
        combinedItems.addAll(achievements);

        // Add Endings section header
        combinedItems.add("Endings");
        combinedItems.addAll(endings);
    }

    @Override
    public int getItemViewType(int position) {
        Object item = combinedItems.get(position);
        if (item instanceof String) {
            return VIEW_TYPE_SECTION_HEADER;
        } else if (item instanceof Achievement) {
            return VIEW_TYPE_ACHIEVEMENT;
        } else {
            return VIEW_TYPE_ENDING;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_SECTION_HEADER:
                View headerView = inflater.inflate(R.layout.item_section_header, parent, false);
                return new SectionHeaderViewHolder(headerView);

            case VIEW_TYPE_ACHIEVEMENT:
                View achievementView = inflater.inflate(R.layout.item_achievement, parent, false);
                return new AchievementViewHolder(achievementView);

            case VIEW_TYPE_ENDING:
                View endingView = inflater.inflate(R.layout.item_ending, parent, false);
                return new EndingViewHolder(endingView);

            default:
                throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = combinedItems.get(position);

        if (holder instanceof SectionHeaderViewHolder) {
            ((SectionHeaderViewHolder) holder).headerText.setText((String) item);
        }
        else if (holder instanceof AchievementViewHolder) {
            Achievement achievement = (Achievement) item;
            AchievementViewHolder achievementHolder = (AchievementViewHolder) holder;

            achievementHolder.titleTextView.setText(achievement.name);
            achievementHolder.descriptionTextView.setText(achievement.description);
            achievementHolder.pointsTextView.setText(String.valueOf(achievement.points));

            if (achievement.unlocked) {
                achievementHolder.lockIcon.setVisibility(View.GONE);
                achievementHolder.unlockedTextView.setVisibility(View.VISIBLE);
                achievementHolder.unlockedTextView.setText(achievement.unlockedDate);
                achievementHolder.itemView.setAlpha(1.0f);
            } else {
                achievementHolder.lockIcon.setVisibility(View.VISIBLE);
                achievementHolder.unlockedTextView.setVisibility(View.GONE);
                achievementHolder.itemView.setAlpha(0.5f);
            }

            // Load icon
            if (achievement.iconResource != null && !achievement.iconResource.isEmpty()) {
                int resourceId = holder.itemView.getContext().getResources().getIdentifier(
                        achievement.iconResource, "drawable", holder.itemView.getContext().getPackageName());
                if (resourceId != 0) {
                    achievementHolder.iconImageView.setImageResource(resourceId);
                }
            }
        }
        else if (holder instanceof EndingViewHolder) {
            Ending ending = (Ending) item;
            EndingViewHolder endingHolder = (EndingViewHolder) holder;

            endingHolder.titleTextView.setText(ending.title);

            if (ending.unlocked) {
                endingHolder.descriptionTextView.setText(ending.description);
                endingHolder.lockIcon.setVisibility(View.GONE);
                endingHolder.unlockedTextView.setVisibility(View.VISIBLE);
                endingHolder.unlockedTextView.setText(ending.unlockedDate);
                endingHolder.itemView.setAlpha(1.0f);
            } else {
                endingHolder.descriptionTextView.setText(R.string.ending_locked_description);
                endingHolder.lockIcon.setVisibility(View.VISIBLE);
                endingHolder.unlockedTextView.setVisibility(View.GONE);
                endingHolder.itemView.setAlpha(0.5f);
            }

            // Load image
            if (ending.thumbnailResource != null && !ending.thumbnailResource.isEmpty()) {
                int resourceId = holder.itemView.getContext().getResources().getIdentifier(
                        ending.thumbnailResource, "drawable", holder.itemView.getContext().getPackageName());
                if (resourceId != 0) {
                    endingHolder.thumbnailImageView.setImageResource(resourceId);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return combinedItems.size();
    }

    static class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerText;

        public SectionHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerText = itemView.findViewById(R.id.sectionHeaderText);
        }
    }

    static class AchievementViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView titleTextView;
        TextView descriptionTextView;
        TextView pointsTextView;
        ImageView lockIcon;
        TextView unlockedTextView;

        public AchievementViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.achievementIcon);
            titleTextView = itemView.findViewById(R.id.achievementTitle);
            descriptionTextView = itemView.findViewById(R.id.achievementDescription);
            pointsTextView = itemView.findViewById(R.id.achievementPoints);
            lockIcon = itemView.findViewById(R.id.achievementLockIcon);
            unlockedTextView = itemView.findViewById(R.id.achievementUnlockedDate);
        }
    }

    static class EndingViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnailImageView;
        TextView titleTextView;
        TextView descriptionTextView;
        ImageView lockIcon;
        TextView unlockedTextView;

        public EndingViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.endingThumbnail);
            titleTextView = itemView.findViewById(R.id.endingTitle);
            descriptionTextView = itemView.findViewById(R.id.endingDescription);
            lockIcon = itemView.findViewById(R.id.endingLockIcon);
            unlockedTextView = itemView.findViewById(R.id.endingUnlockedDate);
        }
    }
}
