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

import com.example.stopthebomb.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PlansFragment extends Fragment {

    private boolean isNameCorrect;  // Variable para almacenar el estado

    public PlansFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_plans, container, false);

        // Recuperamos el valor de isNameCorrect desde los argumentos del fragmento
        if (getArguments() != null) {
            isNameCorrect = getArguments().getBoolean("isNameCorrect", false);
        }

        // Referencias a los botones en el layout
        Button btnPlanR = view.findViewById(R.id.btnPlanR);
        Button btnPlanL = view.findViewById(R.id.btnPlanL);
        Button btnBack = view.findViewById(R.id.btnBack);

        // Configurar los listeners para los botones
        //btnPlanR.setOnClickListener(v -> showPlanDialog());
        btnPlanL.setOnClickListener(v -> showPlanDialog());

        btnBack.setOnClickListener(v -> {
            // Volver al fragmento anterior
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    private void showPlanDialog() {
        String message = loadMessageBasedOnFlag();  // Cargar el mensaje dependiendo del flag

        // Crear un AlertDialog para mostrar el mensaje
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Plan");

        // Usamos un ScrollView para que el mensaje largo sea desplazable
        ScrollView scrollView = new ScrollView(getContext());
        TextView textView = new TextView(getContext());
        textView.setText(message);
        textView.setPadding(20, 20, 20, 20);  // Optional, to give some padding to the text
        scrollView.addView(textView);

        builder.setView(scrollView);  // Establecemos el ScrollView en el AlertDialog

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            // Puedes agregar aquí alguna lógica si es necesario
        });

        builder.show();
    }

    private String loadMessageBasedOnFlag() {
        // Determinamos el archivo que se va a cargar dependiendo de 'isNameCorrect'
        String fileName = isNameCorrect ? "file_unlocked.txt" : "file_russian.txt";

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

