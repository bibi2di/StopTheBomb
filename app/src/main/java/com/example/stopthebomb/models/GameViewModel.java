package com.example.stopthebomb.models;

import android.content.Context;
import android.os.Handler;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameViewModel extends ViewModel {
    private MutableLiveData<List<CodeCard>> codeCards = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<NumberCard>> numberCards = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<Boolean> isInactive = new MutableLiveData<>(false);

    private final MutableLiveData<Integer> achievementUnlocked = new MutableLiveData<>();
    private final MutableLiveData<Integer> endingDiscovered = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final long INACTIVITY_TIME_LIMIT = 2 * 60 * 1000; // 2 minutes in milliseconds
    private Handler inactivityHandler = new Handler();
    private Runnable inactivityRunnable = this::onInactivityTimeout;
    private boolean isTimerRunning = false;

    private int bombClickCount = 0;
    private boolean isNameCorrect = false;

    private MutableLiveData<String> dialogToShow = new MutableLiveData<>();
    private MutableLiveData<String> hiddenMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> showNameInputDialog = new MutableLiveData<>(false);
    private MutableLiveData<Achievement> achievementToNotify = new MutableLiveData<>();

    private List<String> dialogSequence = new ArrayList<>();
    private int currentDialogIndex = 0;

    public GameViewModel() {
        initializeCodeCards();
        initializeNumberCards();
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

    public boolean isCodeMatching(String targetCode) {
        List<CodeCard> cards = codeCards.getValue();
        if (cards == null || cards.size() < targetCode.length()) {
            return false;
        }

        StringBuilder currentCode = new StringBuilder();
        for (int i = 0; i < targetCode.length(); i++) {
            currentCode.append(cards.get(i).getLetter());
        }

        return currentCode.toString().equals(targetCode);
    }

    private void initializeNumberCards() {
        List<NumberCard> cards = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            cards.add(new NumberCard(random.nextInt(10)));
        }
        numberCards.setValue(cards);
    }

    public void updateNumberCard(int position, NumberCard updatedCard) {
        List<NumberCard> numberCardsList = numberCards.getValue();
        if (numberCardsList != null && position >= 0 && position < numberCardsList.size()) {
            numberCardsList.set(position, updatedCard);
            numberCards.setValue(numberCardsList);
        }
        pauseInactivityTimer();
    }

    public boolean isNumberSequenceMatching(String targetSequence) {
        List<NumberCard> cards = numberCards.getValue();
        if (cards == null || cards.size() < targetSequence.length()) {
            return false;
        }

        StringBuilder currentSequence = new StringBuilder();
        for (int i = 0; i < targetSequence.length(); i++) {
            currentSequence.append(cards.get(i).getNumber());
        }

        return currentSequence.toString().equals(targetSequence);
    }

    public void onCardUpClicked(CodeCard card) {
        char currentLetter = card.getLetter().charAt(0);
        char newLetter = (char) (currentLetter == 'Z' ? 'A' : currentLetter + 1);
        card.setLetter(String.valueOf(newLetter));
        pauseInactivityTimer();
    }

    public void onCardDownClicked(CodeCard card) {
        char currentLetter = card.getLetter().charAt(0);
        char newLetter = (char) (currentLetter == 'A' ? 'Z' : currentLetter - 1);
        card.setLetter(String.valueOf(newLetter));
        pauseInactivityTimer();
    }

    public LiveData<Boolean> getInactivityStatus() {
        return isInactive;
    }

    // Called when activity enters foreground
    public void onResume() {
        if (!isTimerRunning) startInactivityTimer();
    }

    private void startInactivityTimer() {
        inactivityHandler.postDelayed(inactivityRunnable, INACTIVITY_TIME_LIMIT);
        isTimerRunning = true;
    }

    // Called when activity leaves foreground
    public void onPause() {
        inactivityHandler.removeCallbacks(inactivityRunnable);
        isTimerRunning = false;
    }

    // Called when user performs an action

    public void pauseInactivityTimer() {
        inactivityHandler.removeCallbacks(inactivityRunnable);
        isTimerRunning = false;
    }

    // Logic when inactivity timeout is reached
    private void onInactivityTimeout() {
        isInactive.setValue(true);
        isTimerRunning = false;
    }

    public void unlockAchievement(int id) {
        executorService.execute(() -> {
            // Lógica para desbloquear un logro (simulada como un ejemplo)
            Integer currentValue = achievementUnlocked.getValue();
            if (currentValue == null || currentValue != id) {
                achievementUnlocked.postValue(id); // Actualiza el valor en segundo plano
            }
        });
    }

    public LiveData<Integer> getAchievementUnlocked() {
        return achievementUnlocked;
    }

    public void discoverEnding(int id) {
        executorService.execute(() -> {
            // Lógica para descubrir un final (simulada como ejemplo)
            Integer currentValue = endingDiscovered.getValue();
            if (currentValue == null || currentValue != id) {
                endingDiscovered.postValue(id); // Actualiza el valor en segundo plano
            }
        });
    }

    public LiveData<Integer> getEndingDiscovered() {
        return endingDiscovered;
    }

    public void loadDialogues(Context context) {
        dialogSequence.clear();
        try {
            InputStream is = context.getAssets().open("dialogs.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                dialogSequence.add(line);
            }
            reader.close();
            currentDialogIndex = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showNextDialog() {
        currentDialogIndex++;
        if (currentDialogIndex < dialogSequence.size()) {
            dialogToShow.setValue(dialogSequence.get(currentDialogIndex));
        }
    }

    public void checkNumberSequence() {
        if (isNumberSequenceMatching("3458")) {
            showNameInputDialog.setValue(true);
        }
    }

    public void validateName(String name) {
        if (name.equalsIgnoreCase("Stanley")) {
            isNameCorrect = true;
            loadHiddenMessage();
            unlockAchievement(2);
        }
    }

    private void loadHiddenMessage() {
        try {
            hiddenMessage.setValue("Ahora podrás entender el Plan L...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void incrementBombClickCount() {
        bombClickCount++;
    }

    public int getBombClickCount() {
        return bombClickCount;
    }

    public boolean isNameCorrect() {
        return isNameCorrect;
    }

    // Expose LiveData
    public LiveData<String> getDialogToShow() { return dialogToShow; }
    public LiveData<String> getHiddenMessage() { return hiddenMessage; }
    public LiveData<Boolean> getShowNameInputDialog() { return showNameInputDialog; }
    public LiveData<Achievement> getAchievementToNotify() { return achievementToNotify; }
}
