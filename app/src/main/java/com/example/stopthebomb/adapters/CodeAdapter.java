package com.example.stopthebomb.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stopthebomb.R;
import com.example.stopthebomb.models.CodeCard;
import com.example.stopthebomb.models.GameViewModel;

import java.util.List;

public class CodeAdapter extends RecyclerView.Adapter<CodeAdapter.CodeViewHolder> {
    private List<CodeCard> cards;
    private GameViewModel gameViewModel;

    public CodeAdapter(List<CodeCard> cards, GameViewModel gameViewModel) {
        this.cards = cards;
        this.gameViewModel = gameViewModel;
    }

    @NonNull
    @Override
    public CodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_code_card, parent, false);
        return new CodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CodeViewHolder holder, int position) {
        CodeCard card = cards.get(position);
        holder.bind(card);
    }

    @Override
    public int getItemCount() {
        return cards != null ? cards.size() : 0;
    }

    // ViewHolder class
    public class CodeViewHolder extends RecyclerView.ViewHolder {
        private TextView tvLetter;
        private Button btnUp, btnDown;

        public CodeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLetter = itemView.findViewById(R.id.tvLetter);
            btnUp = itemView.findViewById(R.id.btnUp);
            btnDown = itemView.findViewById(R.id.btnDown);
        }

        public void bind(CodeCard card) {
            // Set current letter
            tvLetter.setText(card.getLetter());

            // Up button logic
            btnUp.setOnClickListener(v -> {
                gameViewModel.onCardUpClicked(card);
                tvLetter.setText(card.getLetter());
                gameViewModel.pauseInactivityTimer();
            });

            // Down button logic
            btnDown.setOnClickListener(v -> {
                gameViewModel.onCardDownClicked(card);
                tvLetter.setText(card.getLetter());
                gameViewModel.pauseInactivityTimer();
            });
        }
    }
}
