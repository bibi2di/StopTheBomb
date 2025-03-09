package com.example.stopthebomb.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stopthebomb.MyApplication;
import com.example.stopthebomb.models.CodeCard;
import com.example.stopthebomb.adapters.NumberAdapter;
import com.example.stopthebomb.models.NumberCard;
import com.example.stopthebomb.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GameActivity extends AppCompatActivity {
    private int score = 0;
    private TextView tvScore;
    private RecyclerView rvCodePanel;
    private RecyclerView rvNumberPanel;
    private CodeAdapter codeAdapter;
    private NumberAdapter numberAdapter;
    private List<CodeCard> codeCards;
    private List<NumberCard> numberCards;

    private static final long INACTIVITY_TIME_LIMIT = 2 * 60 * 1000; // 2 minutos en milisegundos

    private Handler inactivityHandler = new Handler();

    private Runnable inactivityRunnable = this::onInactivityTimeout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);



        // Initialize codeCards
        /*codeCards = new ArrayList<>();
        codeCards.add(new CodeCard("A"));
        codeCards.add(new CodeCard("B"));
        codeCards.add(new CodeCard("C"));*/
        // Initialize database helper


        // Initialize views
        tvScore = findViewById(R.id.tvScore);
        rvCodePanel = findViewById(R.id.rvCodePanel);
        rvNumberPanel = findViewById(R.id.rvNumberPanel);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnRadar = findViewById(R.id.btnRadar);
        Button btnRadio = findViewById(R.id.btnRadio);
        Button btnCajon = findViewById(R.id.btnCajon);


        // Setup back button
        btnBack.setOnClickListener(v -> finish());

        // Initialize code cards
        initializeCodeCards();

        // Initialize number cards
        initializeNumberCards();

        // Setup RecyclerView
        codeAdapter = new CodeAdapter(codeCards);
        rvCodePanel.setLayoutManager(new LinearLayoutManager(this));
        rvCodePanel.setAdapter(codeAdapter);

        // Setup Number RecyclerView
        numberAdapter = new NumberAdapter(numberCards, this);
        rvNumberPanel.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvNumberPanel.setAdapter(numberAdapter);

        // Setup additional buttons
        btnRadar.setOnClickListener(v -> {
            Toast.makeText(this, "Radar activado", Toast.LENGTH_SHORT).show();
            resetInactivityTimer();
        });

        btnRadio.setOnClickListener(v -> {
            Toast.makeText(this, "Radio sintonizada", Toast.LENGTH_SHORT).show();
            resetInactivityTimer();
        });

        btnCajon.setOnClickListener(v -> {
            Toast.makeText(this, "Cajón abierto", Toast.LENGTH_SHORT).show();
            resetInactivityTimer();
        });


        // Update initial score
        updateScore();

        // Mostrar diálogo de inicio:
        showDialogsSequentially();
    }

    // Method to get current code combination
    private String getCurrentCodeCombination() {
        // Combine the letters from your CodeCards
        return codeCards.stream()
                .map(CodeCard::getLetter)
                .collect(Collectors.joining());
    }

    // Method to check for achievements
    private boolean checkForAchievement() {
        // Implement your achievement logic
        return score > 100; // Example condition
    }

    private void initializeCodeCards() {
        codeCards = new ArrayList<>();
        codeCards.add(new CodeCard("A"));
        codeCards.add(new CodeCard("B"));
        codeCards.add(new CodeCard("C"));
    }

    private void initializeNumberCards() {
        numberCards = new ArrayList<>();
        Random random = new Random();

        // Add 5 random numbers between 0-9
        for (int i = 0; i < 5; i++) {
            numberCards.add(new NumberCard(random.nextInt(10)));
        }
    }

    private void updateScore() {
        tvScore.setText("Score: " + score);
    }
    private void updateUI() {
        // Update score TextView
        tvScore.setText("Score: " + score);

        // Update RecyclerView with restored code cards
        if (codeAdapter != null) {
            // Notify adapter of data changes
            codeAdapter.notifyDataSetChanged();
        }
    }

    // Show a dialog with a question and multiple choice options
    private void showFirstDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Teniente Cletus");
        builder.setMessage("¡Eh tú! ¡Sí, tú! Ven aquí un momento");

        // Agregar un solo botón
        builder.setPositiveButton("Vale", (dialog, which) -> {
            handleResponse("Vale");
        });

        builder.create().show();
    }

    private void handleResponse(String response) {
        // Handle the selected response (e.g., update score, show feedback, etc.)
        Toast.makeText(this, "Seleccionaste: " + response, Toast.LENGTH_SHORT).show();

        // Example: Increase score based on the answer (you can change this logic)
        if (response.equals("Vale")) {
            score += 10;
        } else {
            score -= 5;
        }

        // Update the UI with the new score
        updateScore();
    }

    private List<String> loadDialogues() {
        List<String> dialogues = new ArrayList<>();
        try {
            // Abrir el archivo de texto dentro de la carpeta "assets"
            InputStream is = getAssets().open("dialogs.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;

            // Leer todas las líneas del archivo y añadirlas a la lista de diálogos
            while ((line = reader.readLine()) != null) {
                dialogues.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dialogues;
    }

    private void showDialogsSequentially() {
        List<String> dialogues = loadDialogues();  // Cargar los diálogos desde el archivo
        showNextDialog(dialogues, 0);  // Mostrar el primer diálogo
    }

    private void showNextDialog(List<String> dialogues, int index) {
        if (index >= dialogues.size()) {
            return;  // No hay más diálogos
        }

        String dialogText = dialogues.get(index);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Teniente Rogers");
        builder.setMessage(dialogText);

        builder.setPositiveButton("Asentir", (dialog, which) -> {
            showNextDialog(dialogues, index + 1);  // Mostrar el siguiente diálogo
        });

        builder.create().show();  // Mostrar el diálogo
    }

    private void onInactivityTimeout() {
        // Mostrar diálogo de victoria por inactividad
        new AlertDialog.Builder(this)
                .setTitle("¡Has ganado!")
                .setMessage("Nadie ha tocado nada en 2 minutos. ¡Eres el maestro de la paciencia!")
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    // Lógica para finalizar el juego o mostrar el WinnerBoard
                    finish(); // o redirigir a la pantalla del WinnerBoard
                })
                .setCancelable(false)
                .show();
        mostrarNotificacionDeLogro();
    }

    private void mostrarNotificacionDeLogro() {

        // Check for permission first
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                return;
            }
        }
        // Crear un intent para cuando se haga clic en la notificación (puede abrir una actividad o hacer otra acción)
        Intent intent = new Intent(this, MainActivity.class); // O la actividad que prefieras
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Crear la notificación con el ícono del logro
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_final1) // Icono del logro (debe estar en res/drawable)
                .setContentTitle("Logro Desbloqueado")
                .setContentText("Has hecho caso al teniente")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // Abre la actividad al hacer clic
                .setAutoCancel(true);

        // Mostrar la notificación
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          //  if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
              //  return;
           // }
        //}

        notificationManager.notify(1, builder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mostrarNotificacionDeLogro();
        }
    }

    private void resetInactivityTimer() {
        inactivityHandler.removeCallbacks(inactivityRunnable); // Detiene el temporizador anterior
        inactivityHandler.postDelayed(inactivityRunnable, INACTIVITY_TIME_LIMIT); // Inicia uno nuevo
    }

    private void initializeNewGame() {
        // Initialize default game state
        score = 0;

        // Create new code cards with default letters
        codeCards = new ArrayList<>();
        codeCards.add(new CodeCard("A"));
        codeCards.add(new CodeCard("B"));
        codeCards.add(new CodeCard("C"));

        // Update UI with new state
        updateUI();

        // If you're using a RecyclerView, you might need to reset the adapter
        codeAdapter = new CodeAdapter(codeCards);
        rvCodePanel.setAdapter(codeAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetInactivityTimer(); // Reiniciar el temporizador al volver a la actividad
    }

    @Override
    protected void onPause() {
        super.onPause();
        inactivityHandler.removeCallbacks(inactivityRunnable); // Detener el temporizador al pausar la actividad
    }

    // CodeCard model class


    // RecyclerView Adapter
    public class CodeAdapter extends RecyclerView.Adapter<CodeAdapter.CodeViewHolder> {
        private List<CodeCard> cards;

        public CodeAdapter(List<CodeCard> cards) {
            this.cards = cards;
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
            holder.bind(card, position);
        }

        @Override
        public int getItemCount() {
            return cards.size();
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

            public void bind(CodeCard card, int position) {
                // Set current letter
                tvLetter.setText(card.getLetter());

                // Up button logic
                btnUp.setOnClickListener(v -> {
                    char currentLetter = card.getLetter().charAt(0);
                    char newLetter = (char) (currentLetter == 'Z' ? 'A' : currentLetter + 1);
                    card.setLetter(String.valueOf(newLetter));
                    tvLetter.setText(card.getLetter());

                    // Decrease score for changing letter
                    score--;
                    updateScore();
                    //showRandomPenalty();
                });

                // Down button logic
                btnDown.setOnClickListener(v -> {
                    char currentLetter = card.getLetter().charAt(0);
                    char newLetter = (char) (currentLetter == 'A' ? 'Z' : currentLetter - 1);
                    card.setLetter(String.valueOf(newLetter));
                    tvLetter.setText(card.getLetter());

                    // Decrease score for changing letter
                    score--;
                    updateScore();
                    //showRandomPenalty();
                });
            }
        }
    }

}