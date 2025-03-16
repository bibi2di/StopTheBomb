package com.example.stopthebomb.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.stopthebomb.R;
import com.example.stopthebomb.models.GameViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PlansFragment extends Fragment {

    private boolean isNameCorrect;  // Variable para almacenar el estado
    private Button btnPlanR;
    private GameViewModel gameViewModel;

    public PlansFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_plans, container, false);
        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        // Recuperamos el valor de isNameCorrect desde los argumentos del fragmento
        if (getArguments() != null) {
            isNameCorrect = getArguments().getBoolean("isNameCorrect", false);
        }

        // Referencias a los botones en el layout
        btnPlanR = view.findViewById(R.id.btnPlanR);
        Button btnPlanL = view.findViewById(R.id.btnPlanL);
        Button btnBack = view.findViewById(R.id.btnBack);

        btnPlanR.setVisibility(View.INVISIBLE);

        // Observe number cards changes to update button visibility
        gameViewModel.getNumberCards().observe(getViewLifecycleOwner(), numberCards -> {
            if (gameViewModel.isNumberSequenceMatching("7294")) {
                btnPlanR.setVisibility(View.VISIBLE);
                // Optionally unlock an achievement
                gameViewModel.unlockAchievement(2);
            }
        });

        // Configurar los listeners para los botones
        btnPlanR.setOnClickListener(v -> showPlanDialog("PlanR"));
        btnPlanL.setOnClickListener(v -> showPlanDialog("PlanL"));

        btnBack.setOnClickListener(v -> {
            // Volver al fragmento anterior
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    private void showPlanDialog(String planName) {
        String message = loadMessage(planName);  // Cargar el mensaje dependiendo del flag

        // Crear un AlertDialog para mostrar el mensaje
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(planName);
        // Usamos un ScrollView para que el mensaje largo sea desplazable
        ScrollView scrollView = new ScrollView(getContext());
        TextView textView = new TextView(getContext());
        textView.setText(message);
        textView.setPadding(20, 20, 20, 20);  // Optional, to give some padding to the text
        scrollView.addView(textView);

        builder.setView(scrollView);  // Establecemos el ScrollView en el AlertDialog

        builder.setPositiveButton(getString(R.string.accept), (dialog, which) -> {
            // Puedes agregar aquí alguna lógica si es necesario
        });

        builder.show();
    }

    private String loadMessage(String planName) {
        String fileName;
        if (planName.equals("PlanL")) {
            fileName = isNameCorrect ? "file_unlocked.txt" : "file_russian.txt";
        }
        else {
            fileName = "planR.txt";
        }
        StringBuilder message = new StringBuilder();
        try {
            // Abrimos el archivo adecuado según el estado de 'isNameCorrect'
            InputStream is = getActivity().getAssets().open(fileName);
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
}


