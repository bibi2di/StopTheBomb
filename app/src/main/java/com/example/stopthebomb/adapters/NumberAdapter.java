package com.example.stopthebomb.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stopthebomb.models.GameViewModel;
import com.example.stopthebomb.models.NumberCard;
import com.example.stopthebomb.R;
import com.example.stopthebomb.activities.GameActivity;

import java.util.List;

public class NumberAdapter extends RecyclerView.Adapter<NumberAdapter.NumberViewHolder> {
    private List<NumberCard> numbers;
    private GameViewModel gameViewModel;

    public NumberAdapter(List<NumberCard> numbers, GameViewModel gameViewModel) {
        this.numbers = numbers;
        this.gameViewModel = gameViewModel;
    }

    @NonNull
    @Override
    public NumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_number_card, parent, false);
        return new NumberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NumberViewHolder holder, int position) {
        NumberCard card = numbers.get(position);
        holder.bind(card, position);
    }

    @Override
    public int getItemCount() {
        return numbers != null ? numbers.size() : 0;
    }

    // ViewHolder class
    public class NumberViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNumber;
        private Button btnUp, btnDown;

        public NumberViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            btnUp = itemView.findViewById(R.id.btnUp);
            btnDown = itemView.findViewById(R.id.btnDown);
        }

        public void bind(NumberCard card, int position) {
            // Set current number
            tvNumber.setText(String.valueOf(card.getNumber()));

            // Up button logic
            btnUp.setOnClickListener(v -> {
                int currentNumber = card.getNumber();
                int newNumber = (currentNumber == 9) ? 0 : currentNumber + 1;
                card.setNumber(newNumber);

                // Update the UI
                tvNumber.setText(String.valueOf(newNumber));

                // Update the ViewModel and pause inactivity timer
                gameViewModel.updateNumberCard(position, card);
                gameViewModel.pauseInactivityTimer();
            });

            // Down button logic
            btnDown.setOnClickListener(v -> {
                int currentNumber = card.getNumber();
                int newNumber = (currentNumber == 0) ? 9 : currentNumber - 1;
                card.setNumber(newNumber);

                // Update the UI
                tvNumber.setText(String.valueOf(newNumber));

                // Update the ViewModel and pause inactivity timer
                gameViewModel.updateNumberCard(position, card);
                gameViewModel.pauseInactivityTimer();
            });
        }
    }
}