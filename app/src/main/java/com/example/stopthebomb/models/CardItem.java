package com.example.stopthebomb.models;

public class CardItem {
    private int imageResId; // Referencia a la imagen del físico
    private String equation; // Ecuación asociada
    private boolean isMatched; // Si la carta está emparejada

    public CardItem(int imageResId, String equation) {
        this.imageResId = imageResId;
        this.equation = equation;
        this.isMatched = false; // Inicialmente no está emparejada
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getEquation() {
        return equation;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }
}
