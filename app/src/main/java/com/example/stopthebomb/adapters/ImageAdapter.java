package com.example.stopthebomb.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.stopthebomb.R;
import com.example.stopthebomb.models.CardItem;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<CardItem> cardItems;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(CardItem cardItem);
    }

    public ImageAdapter(List<CardItem> cardItems, OnItemClickListener listener) {
        this.cardItems = cardItems;
        this.onItemClickListener = listener;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        CardItem cardItem = cardItems.get(position);
        // Cargar la imagen de la tarjeta
        holder.imageView.setImageResource(cardItem.getImageResId());
    }

    @Override
    public int getItemCount() {
        return cardItems.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView); // Asegúrate de que este ID existe en item_image.xml
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(cardItems.get(getAdapterPosition())));
        }
    }
}
