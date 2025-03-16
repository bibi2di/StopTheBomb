package com.example.stopthebomb.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stopthebomb.NotificationHelper;
import com.example.stopthebomb.adapters.CodeAdapter;
import com.example.stopthebomb.database.DatabaseHelper;
import com.example.stopthebomb.fragments.DrawerFragment;
import com.example.stopthebomb.fragments.PlansFragment;
import com.example.stopthebomb.fragments.RadarFragment;
import com.example.stopthebomb.models.Achievement;
import com.example.stopthebomb.adapters.NumberAdapter;
import com.example.stopthebomb.models.GameViewModel;
import com.example.stopthebomb.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameActivity extends BaseActivity {
    private GameViewModel gameViewModel;
    private RecyclerView rvCodePanel;
    private RecyclerView rvNumberPanel;
    private CodeAdapter codeAdapter;
    private NumberAdapter numberAdapter;
    private DatabaseHelper dbHelper;
    private AlertDialog activeDialog;
    private NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        dbHelper = DatabaseHelper.getInstance(this);
        notificationHelper = new NotificationHelper();

        initializeViews();
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
        setupButtons();
        setupObservers();
        setupRecyclerViews();
        gameViewModel.loadDialogues(this);
        showDialogsSequentially();
    }

    private void initializeViews() {
        rvCodePanel = findViewById(R.id.rvCodePanel);
        rvNumberPanel = findViewById(R.id.rvNumberPanel);
    }

    private void setupButtons() {
        Button btnBack = findViewById(R.id.btnBack);
        Button btnRadar = findViewById(R.id.btnRadar);
        Button btnRadio = findViewById(R.id.btnRadio);
        Button btnCajon = findViewById(R.id.btnCajon);
        Button btnAchievements = findViewById(R.id.btnAchievements);
        Button btnAction = findViewById(R.id.btnAction);

        btnBack.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.warning))
                    .setMessage(getString(R.string.progress_lost))
                    .setPositiveButton(getString(R.string.confirm), (dialog, which) -> finish())
                    .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss())
                    .show();
        });

        btnRadar.setOnClickListener(v -> {
            showFragment("radar");
            gameViewModel.pauseInactivityTimer();
        });

        btnRadio.setOnClickListener(v -> {
            if (gameViewModel.isCodeMatching("EFG")) {
                // Show special message when code is EFG
                Toast.makeText(this, getString(R.string.correct_freq), Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.all_units))
                        .setMessage(getString(R.string.validatePlanR) + ": 7294")
                        .setPositiveButton(getString(R.string.accept), (dialog, which) -> {
                            // Logic can be added if needed
                        })
                        .show();
            } else {
                // Show original message
                Toast.makeText(this, getString(R.string.radio_synth), Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.codes))
                        .setMessage(getString(R.string.transmissions)+ ": EFG")
                        .setPositiveButton(getString(R.string.accept), (dialog, which) -> {})
                        .show();
            }
            gameViewModel.pauseInactivityTimer();
        });

        btnCajon.setOnClickListener(v -> {
            showFragment("drawer");
            gameViewModel.pauseInactivityTimer();
        });

        btnAchievements.setOnClickListener(v -> {
            Intent achievementsIntent = new Intent(GameActivity.this, AchievementsActivity.class);
            startActivity(achievementsIntent);
        });

        btnAction.setOnClickListener(v -> {
            if (btnAction.getText().equals("JUMP")) {
                gameViewModel.discoverEnding(3);
                gameViewModel.unlockAchievement(3);
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.ending_discovered)+": LoveBomb")
                        .setPositiveButton(getString(R.string.accept), (dialog, which) -> {
                            finish();
                        })
                        .show();
            } else {
                Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null && vibrator.hasVibrator()) {
                    vibrator.vibrate(300); // Vibrate for 300 milliseconds
                }

                // Use ViewModel to handle bomb click count logic

                int bombClickCount = gameViewModel.getBombClickCount();
                gameViewModel.incrementBombClickCount();

                switch (bombClickCount) {
                    case 1:
                        // First click
                        new AlertDialog.Builder(this)
                                .setMessage(getString(R.string.cowboy))
                                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                                    gameViewModel.unlockAchievement(4);
                                    Toast.makeText(this, "Yeeha!", Toast.LENGTH_SHORT).show();
                                })
                                .setNegativeButton(getString(R.string.no), null)
                                .show();
                        break;

                    case 2:
                        // Second click
                        new AlertDialog.Builder(this)
                                .setMessage(getString(R.string.onemore))
                                .setPositiveButton("OK", null)
                                .show();
                        break;

                    case 3:
                        // Third click - unlock ending and achievements
                        gameViewModel.discoverEnding(2);
                        gameViewModel.unlockAchievement(6);
                        new AlertDialog.Builder(this)
                                .setMessage(getString(R.string.ending_discovered)+ ": D.M.A")
                                .setPositiveButton(getString(R.string.accept), (dialog, which) -> {
                                    finish();
                                })
                                .show();

                        break;
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack();
                    } else {
                        finish();
                    }
                }
            });
        }
    }

    private void setupObservers() {
        gameViewModel.getInactivityStatus().observe(this, isInactive -> {
            if (isInactive) {
                showInactivityDialog();
            }
        });

        gameViewModel.getCodeCards().observe(this, codeCards -> {
            codeAdapter.notifyDataSetChanged();
        });

        gameViewModel.getNumberCards().observe(this, numberCards -> {
            if (numberCards.size() == 4) {
                // Let ViewModel check the sequence instead
                gameViewModel.checkNumberSequence();
            }
        });

        gameViewModel.getAchievementUnlocked().observe(this, id -> {
            if (id != null) {
                if (dbHelper.unlockAchievement(id)) {
                    Achievement achievement = dbHelper.getAllAchievements().get(id - 1);
                    notificationHelper.showAchievementNotification(this, achievement);
                }
            }
        });

        gameViewModel.getEndingDiscovered().observe(this, id -> {
            if (id != null) {
                dbHelper.discoverEnding(id);
            }
        });

        gameViewModel.getShowNameInputDialog().observe(this, shouldShow -> {
            if (shouldShow) {
                showNameInputDialog();
            }
        });

        gameViewModel.getDialogToShow().observe(this, dialogText -> {
            showDialog(dialogText);
        });

        gameViewModel.getHiddenMessage().observe(this, message -> {
            showSecretDialog(message);
        });

        gameViewModel.getAchievementToNotify().observe(this, achievement -> {
            if (achievement != null) {
                notificationHelper.showAchievementNotification(this, achievement);
            }
        });
    }

    private void showFragment(String type) {
        FrameLayout fragmentContainer = findViewById(R.id.fragmentContainer);
        fragmentContainer.setVisibility(View.VISIBLE);

        if (Objects.equals(type, "drawer")) {
            DrawerFragment drawerFragment = new DrawerFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.fade_out)
                    .replace(R.id.fragmentContainer, drawerFragment)
                    .addToBackStack(null)
                    .commit();
        }
        else if (Objects.equals(type, "radar")) {
            RadarFragment radarFragment = new RadarFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.fade_out)
                    .replace(R.id.fragmentContainer, radarFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void showNameInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.hidden_name));

        // Create EditText
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(getString(R.string.send), (dialog, which) -> {
            String enteredName = input.getText().toString().trim();

            // Use ViewModel to validate name
            gameViewModel.validateName(enteredName);

            if (!enteredName.equalsIgnoreCase("Stanley")) {
                Toast.makeText(this, getString(R.string.incorrect_name), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showSecretDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Mensaje Secreto");
        builder.setMessage(message);
        builder.setPositiveButton("Aceptar", (dialog, which) -> {});
        builder.show();
    }


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

    private void showDialog(String dialogText) {
        if (isFinishing() || isDestroyed()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.tennant));
        builder.setMessage(dialogText);

        builder.setPositiveButton(getString(R.string.nod), (dialog, which) -> {
            // Continuar al siguiente diálogo.
            gameViewModel.showNextDialog();
        });
        // Botón con el texto largo
        builder.setNeutralButton(getString(R.string.talkative),
                (dialog, which) -> {
                    // Acción cuando se presiona el botón
                    endDialog();
                    gameViewModel.unlockAchievement(7);
                });
        // Store reference to dismiss if needed
        activeDialog = builder.create();
        activeDialog.show();
    }

    private void endDialog() {
        if (isFinishing() || isDestroyed()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.tennant));
        builder.setMessage("...");
        builder.setPositiveButton(getString(R.string.bye), (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog longMessageDialog = builder.create();
        longMessageDialog.show();
    }


    private void showDialogsSequentially() {
        List<String> dialogues = loadDialogues();
        if (!dialogues.isEmpty()) {
            showDialog(dialogues.get(0));
        }
    }

    private List<String> loadDialogues() {
        List<String> dialogues = new ArrayList<>();
        try {
            // Open the text file in the "assets" folder
            InputStream is = getAssets().open("dialogs.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;

            // Read all lines from the file and add them to the dialogues list
            while ((line = reader.readLine()) != null) {
                dialogues.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dialogues;
    }

    private void showInactivityDialog() {
        // Final 1, achievement 1
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        dbHelper.discoverEnding(1);
        if (dbHelper.unlockAchievement(1)) {
            Achievement achievement = dbHelper.getAllAchievements().get(0);
            notificationHelper.showAchievementNotification(this, achievement);
        }
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.tennant))
                .setMessage(getString(R.string.enhorabuena_end))
                .setPositiveButton(getString(R.string.nod), null)
                .setCancelable(false)
                .show();
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.ending_discovered) +": "+ getString(R.string.obedient))
                .setPositiveButton(getString(R.string.accept), (dialog, which) -> {
                    finish();
                })
                .show();
    }

    public void showPlansFragment() {
        // Create a new instance of PlansFragment
        PlansFragment plansFragment = new PlansFragment();

        // Create a Bundle to pass the value of 'isNameCorrect'
        Bundle bundle = new Bundle();
        bundle.putBoolean("isNameCorrect", gameViewModel.isNameCorrect()); // Use ViewModel for this value
        plansFragment.setArguments(bundle);

        // Perform the transaction to replace the current fragment
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.fade_out)
                .replace(R.id.fragmentContainer, plansFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Notify ViewModel that activity has returned to foreground
        gameViewModel.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Notify ViewModel that activity has left foreground
        gameViewModel.onPause();
    }

    @Override
    protected void onDestroy() {
        if (activeDialog != null && activeDialog.isShowing()) {
            activeDialog.dismiss();
        }
        super.onDestroy();
    }
}
