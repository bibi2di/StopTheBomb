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

import java.util.List;


public class EndingAdapter extends RecyclerView.Adapter<EndingAdapter.EndingViewHolder> {
    private List<Ending> endings;

    public EndingAdapter(List<Ending> endings) {
        this.endings = endings;
    }

    @NonNull
    @Override
    public EndingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ending, parent, false);
        return new EndingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EndingViewHolder holder, int position) {
        Ending ending = endings.get(position);

        holder.titleTextView.setText(ending.title);

        if (ending.unlocked) {
            holder.descriptionTextView.setText(ending.description);
            holder.lockIcon.setVisibility(View.GONE);
            holder.unlockedTextView.setVisibility(View.VISIBLE);
            holder.unlockedTextView.setText(ending.unlockedDate);
            holder.itemView.setAlpha(1.0f);
        } else {
            holder.descriptionTextView.setText(R.string.ending_locked_description);
            holder.lockIcon.setVisibility(View.VISIBLE);
            holder.unlockedTextView.setVisibility(View.GONE);
            holder.itemView.setAlpha(0.5f);
        }

        // Load thumbnail image
        if (ending.thumbnailResource != null && !ending.thumbnailResource.isEmpty()) {
            int resourceId = holder.itemView.getContext().getResources().getIdentifier(
                    ending.thumbnailResource, "drawable", holder.itemView.getContext().getPackageName());
            if (resourceId != 0) {
                holder.thumbnailImageView.setImageResource(resourceId);
            }
        }
    }

    @Override
    public int getItemCount() {
        return endings.size();
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
