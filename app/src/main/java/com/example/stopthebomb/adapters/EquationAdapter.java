package com.example.stopthebomb.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.stopthebomb.R;
import com.example.stopthebomb.models.CardItem;

import java.util.List;

public class EquationAdapter extends RecyclerView.Adapter<EquationAdapter.EquationViewHolder> {

    private List<CardItem> cardItems;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(CardItem cardItem);
    }

    public EquationAdapter(List<CardItem> cardItems, OnItemClickListener listener) {
        this.cardItems = cardItems;
        this.onItemClickListener = listener;
    }

    @Override
    public EquationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_equation, parent, false);
        return new EquationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EquationViewHolder holder, int position) {
        CardItem cardItem = cardItems.get(position);
        holder.textView.setText(cardItem.getEquation());
    }

    @Override
    public int getItemCount() {
        return cardItems.size();
    }

    public class EquationViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public EquationViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView); // Asegúrate de que este ID existe en item_equation.xml
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(cardItems.get(getAdapterPosition())));
        }
    }
}
