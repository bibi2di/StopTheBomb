package com.example.stopthebomb.models;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameViewModel extends ViewModel {
    private MutableLiveData<Integer> score = new MutableLiveData<>(0);
    private MutableLiveData<List<CodeCard>> codeCards = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<NumberCard>> numberCards = new MutableLiveData<>(new ArrayList<>());    private MutableLiveData<Boolean> isInactive = new MutableLiveData<>(false);

    private static final long INACTIVITY_TIME_LIMIT = 2 * 60 * 1000; // 2 minutos en milisegundos
    private Handler inactivityHandler = new Handler();
    private Runnable inactivityRunnable = this::onInactivityTimeout;

    private boolean isTimerRunning = false; // Indicador de si el temporizador está en ejecución

    public GameViewModel() {
        initializeCodeCards();
        initializeNumberCards();
    }

    public LiveData<Integer> getScore() {
        return score;
    }

    public LiveData<List<CodeCard>> getCodeCards() {
        return codeCards;
    }

    public LiveData<List<NumberCard>> getNumberCards() {
        return numberCards;
    }

    private void initializeCodeCards() {
        List<CodeCard> cards = new ArrayList<>();
        cards.add(new CodeCard("A"));
        cards.add(new CodeCard("B"));
        cards.add(new CodeCard("C"));
        codeCards.setValue(cards);
    }

    private void initializeNumberCards() {
        List<NumberCard> cards = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            cards.add(new NumberCard(random.nextInt(10)));
        }
        numberCards.setValue(cards);
    }

    public void updateNumber(int position, int newNumber) {
        List<NumberCard> currentList = new ArrayList<>(numberCards.getValue());
        currentList.get(position).setNumber(newNumber);
        numberCards.setValue(currentList); // Notify observers about the change
    }

    public void updateNumberCard(int position, NumberCard updatedCard) {
        List<NumberCard> numberCardsList = numberCards.getValue();
        if (numberCardsList != null && position >= 0 && position < numberCardsList.size()) {
            numberCardsList.set(position, updatedCard);  // Modify the list
            numberCards.setValue(numberCardsList);  // Notify observers about the change
        }
        pauseInactivityTimer();  // Pausa el temporizador al tocar algo
    }


    public void onCardUpClicked(CodeCard card) {
        char currentLetter = card.getLetter().charAt(0);
        char newLetter = (char) (currentLetter == 'Z' ? 'A' : currentLetter + 1);
        card.setLetter(String.valueOf(newLetter));
        score.setValue(score.getValue() - 1);
    }

    public void onCardDownClicked(CodeCard card) {
        char currentLetter = card.getLetter().charAt(0);
        char newLetter = (char) (currentLetter == 'A' ? 'Z' : currentLetter - 1);
        card.setLetter(String.valueOf(newLetter));
        score.setValue(score.getValue() - 1);
    }

    public LiveData<Boolean> getInactivityStatus() {
        return isInactive;
    }

    // Llamado cuando la actividad entra al primer plano
    public void onResume() {
        if (!isTimerRunning) startInactivityTimer();  // Si el temporizador no está corriendo, se inicia
    }

    private void startInactivityTimer() {
        inactivityHandler.postDelayed(inactivityRunnable, INACTIVITY_TIME_LIMIT);  // Empieza el temporizador
        isTimerRunning = true; // Establece que el temporizador está corriendo
    }

    // Llamado cuando la actividad sale al segundo plano
    public void onPause() {
        inactivityHandler.removeCallbacks(inactivityRunnable); // Detener el temporizador
    }

    // Llamado cuando el usuario realiza una acción
    public void resetInactivityTimer() {
        inactivityHandler.removeCallbacks(inactivityRunnable); // Detiene el temporizador anterior
        inactivityHandler.postDelayed(inactivityRunnable, INACTIVITY_TIME_LIMIT); // Reinicia el temporizador
        isInactive.setValue(false); // Restablece el estado de inactividad
    }

    public void pauseInactivityTimer() {
        inactivityHandler.removeCallbacks(inactivityRunnable); // Detener el temporizador si se toca algo
        isTimerRunning = false; // Establece que el temporizador está detenido
    }

    // Lógica cuando el tiempo de inactividad se ha alcanzado
    private void onInactivityTimeout() {
        // Se ejecuta cuando el tiempo de inactividad se alcanza (2 minutos)
        isInactive.setValue(true); // Notificamos a la Vista que el usuario está inactivo
    }



}
