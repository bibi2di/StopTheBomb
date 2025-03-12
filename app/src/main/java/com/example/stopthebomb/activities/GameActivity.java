package com.example.stopthebomb.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stopthebomb.MyApplication;
import com.example.stopthebomb.adapters.CodeAdapter;
import com.example.stopthebomb.database.DatabaseHelper;
import com.example.stopthebomb.fragments.DrawerFragment;
import com.example.stopthebomb.fragments.PlansFragment;
import com.example.stopthebomb.fragments.RadarFragment;
import com.example.stopthebomb.models.CodeCard;
import com.example.stopthebomb.adapters.NumberAdapter;
import com.example.stopthebomb.models.GameViewModel;
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

public class GameActivity extends BaseActivity {
    private GameViewModel gameViewModel;
    private TextView tvScore;
    private RecyclerView rvCodePanel;
    private RecyclerView rvNumberPanel;
    private CodeAdapter codeAdapter;
    private NumberAdapter numberAdapter;

    private boolean isNameCorrect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Inicializar vistas:
        initializeViews();
        // Inicializar botones:
        setupButtons();
        // Inicializar ViewModel:
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
        // Configurar observers:
        setupObservers();
        // Inicializar RecyclerViews:
        setupRecyclerViews();
        // Mostrar diálogos:
        showDialogsSequentially();
        isNameCorrect = false;

    }

    private void initializeViews() {
        tvScore = findViewById(R.id.tvScore);
        rvCodePanel = findViewById(R.id.rvCodePanel);
        rvNumberPanel = findViewById(R.id.rvNumberPanel);
    }

    private void setupButtons() {
        Button btnBack = findViewById(R.id.btnBack);
        Button btnRadar = findViewById(R.id.btnRadar);
        Button btnRadio = findViewById(R.id.btnRadio);
        Button btnCajon = findViewById(R.id.btnCajon);

        btnBack.setOnClickListener(v -> finish());
        btnRadar.setOnClickListener(v -> {
            openRadarFragment();
            gameViewModel.pauseInactivityTimer();
        });

        btnRadio.setOnClickListener(v -> {
            Toast.makeText(this, "Radio sintonizada", Toast.LENGTH_SHORT).show();
            gameViewModel.pauseInactivityTimer();
        });

        btnCajon.setOnClickListener(v -> {
            Toast.makeText(this, "Cajón abierto", Toast.LENGTH_SHORT).show();
            showFragment();
            gameViewModel.pauseInactivityTimer();
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    // Aquí defines la lógica que quieres cuando el usuario presione atrás
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        // Si hay fragmentos en la pila de retroceso, se hace pop
                        getSupportFragmentManager().popBackStack();
                    } else {
                        // Si no, se llama a finish para cerrar la actividad
                        finish();
                    }
                }
            });
        }

    }

    private void setupObservers() {
        gameViewModel.getInactivityStatus().observe(this, isInactive -> {
            if (isInactive) {
                // Mostrar el mensaje de inactividad (por ejemplo, un diálogo)
                showInactivityDialog();
            }
        });
        gameViewModel.getScore().observe(this, score -> {
            tvScore.setText("Score: " + score);
        });

        gameViewModel.getCodeCards().observe(this, codeCards -> {
            codeAdapter.notifyDataSetChanged();
        });

        gameViewModel.getNumberCards().observe(this, numberCards -> {
            numberAdapter.notifyItemChanged(numberCards.size() - 1);
            Log.d("GameActivity", "Numbers updated: " + numberCards);

            if (numberCards.size() == 5) {
                checkNumberCombination(numberCards);
            }
        });

    }

    public void openRadarFragment() {
        RadarFragment radarFragment = new RadarFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, radarFragment)
                .addToBackStack(null) // Si quieres que el fragmento sea apilable
                .commit();
    }


    private void checkNumberCombination(List<NumberCard> numberCards) {
        StringBuilder enteredCode = new StringBuilder();

        Log.d("GameActivity", "Checking entered numbers:");
        for (NumberCard card : numberCards) {
            Log.d("GameActivity", "Number: " + card.getNumber());
            enteredCode.append(card.getNumber());
        }

        Log.d("GameActivity", "Final entered code: " + enteredCode.toString());

        if (enteredCode.toString().equals("34582")) {
            Log.d("GameActivity", "Code matched! Showing dialog...");
            showNameInputDialog();
        }
    }


    private void showNameInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Introduce el nombre oculto:");

        // Create an EditText field
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the submit button
        builder.setPositiveButton("Enviar", (dialog, which) -> {
            String enteredName = input.getText().toString().trim();

            // Validate name (case insensitive)
            if (enteredName.equalsIgnoreCase("Stanley")) {
                showSecretDialog(); // Show next dialog if correct
                isNameCorrect = true;
            } else {
                Toast.makeText(this, "Nombre incorrecto. Intenta de nuevo.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showSecretDialog() {
        String hiddenMessage = loadHiddenMessage(); // Load text from file

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Mensaje Secreto");
        builder.setMessage(hiddenMessage);

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            // You can add additional logic here if needed
        });

        builder.show();
    }

    // Method to read the hidden message from assets
    private String loadHiddenMessage() {
        StringBuilder message = new StringBuilder();
        try {
            InputStream is = getAssets().open("dialog_hidden.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                message.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message.toString();
    }

    private void setupRecyclerViews() {
        codeAdapter = new CodeAdapter(gameViewModel.getCodeCards().getValue(), gameViewModel);
        rvCodePanel.setLayoutManager(new LinearLayoutManager(this));
        rvCodePanel.setAdapter(codeAdapter);

        numberAdapter = new NumberAdapter(gameViewModel.getNumberCards().getValue(), gameViewModel);
        rvNumberPanel.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvNumberPanel.setAdapter(numberAdapter);
    }

    private void showFragment() {
        FrameLayout fragmentContainer = findViewById(R.id.fragmentContainer);
        fragmentContainer.setVisibility(View.VISIBLE); // Make container visible

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.fade_out)
                .replace(R.id.fragmentContainer, new DrawerFragment())
                .addToBackStack(null)
                .commit();
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

    private void showInactivityDialog() {
        // Unlock the "Patient Master" ending in the database
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        dbHelper.discoverEnding(1); // Assuming ID 1 is the "Patient Master" ending

        new AlertDialog.Builder(this)
                .setTitle("Teniente Rogers")
                .setMessage("Hmmmm, veo que me has hecho caso. Enhorabuena cadete, siga por este camino y llegará muy lejos...")
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    // Lógica para finalizar el juego o mostrar el WinnerBoard
                    finish(); // O redirigir a la pantalla del WinnerBoard
                })
                .setCancelable(false)
                .show();

        // Show notification and unlock achievement
        mostrarNotificacionDeLogro();
    }


    private String loadMessageBasedOnFlag() {
        // El archivo que se cargará dependiendo del estado de 'isNameCorrect'
        String fileName = isNameCorrect ? "file_unlocked.txt" : "file_russian.txt";

        StringBuilder message = new StringBuilder();
        try {
            // Abrimos el archivo adecuado según el estado de 'isNameCorrect'
            InputStream is = getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                message.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message.toString();
    }

    private void showPlanDialog() {
        String message = loadMessageBasedOnFlag(); // Cargar el mensaje dependiendo del flag

        // Crear un AlertDialog para mostrar el mensaje
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Plan");

        // Usamos un ScrollView para que el mensaje largo sea desplazable
        ScrollView scrollView = new ScrollView(this);
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setPadding(20, 20, 20, 20); // Optional, to give some padding to the text
        scrollView.addView(textView);

        builder.setView(scrollView); // Establecemos el ScrollView en el AlertDialog

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            // Puedes agregar aquí alguna lógica si es necesario
        });

        builder.show();
    }

    public void showPlansFragment() {
        // Crear una nueva instancia del fragmento PlansFragment
        PlansFragment plansFragment = new PlansFragment();

        // Crear un Bundle para pasar el valor de 'isNameCorrect'
        Bundle bundle = new Bundle();
        bundle.putBoolean("isNameCorrect", isNameCorrect); // Aquí pasamos el valor de isNameCorrect
        plansFragment.setArguments(bundle);

        // Realizar la transacción para reemplazar el fragmento actual
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.fade_out)
                .replace(R.id.fragmentContainer, plansFragment)
                .addToBackStack(null)
                .commit();
    }


    private void mostrarNotificacionDeLogro() {
        // Check for permission first
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                return;
            }
        }

        // Unlock the achievement in the database
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        dbHelper.unlockAchievement(1); // Assuming ID 1 is the "Listen to Lieutenant" achievement

        // Create an intent for when the notification is clicked
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Create the notification with the achievement icon
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_final1)
                .setContentTitle("Logro Desbloqueado")
                .setContentText("Has hecho caso al teniente")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mostrarNotificacionDeLogro();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Notificar al ViewModel que la actividad ha vuelto al primer plano
        gameViewModel.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Notificar al ViewModel que la actividad ha salido del primer plano
        gameViewModel.onPause();
    }
}
    // CodeCard model class


    // RecyclerView Adapter
