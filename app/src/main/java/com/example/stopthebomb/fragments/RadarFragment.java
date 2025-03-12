package com.example.stopthebomb.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.stopthebomb.R;

public class RadarFragment extends Fragment {

    private Button btnLocalizacion;
    private Button btnEstablecerDestino;
    private EditText editDestino;

    public RadarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout del fragmento
        View rootView = inflater.inflate(R.layout.fragment_radar, container, false);

        // Referencias a los elementos del layout
        btnLocalizacion = rootView.findViewById(R.id.btnLocalizacion);
        btnEstablecerDestino = rootView.findViewById(R.id.btnEstablecerDestino);
        Button btnClose = rootView.findViewById(R.id.btnClose);
        editDestino = rootView.findViewById(R.id.editDestino);

        // Acción del botón Localización (abir ubicación fija)
        btnLocalizacion.setOnClickListener(v -> abrirUbicacionPredefinida());

        // Acción del botón Establecer Destino (introducir dirección)
        btnEstablecerDestino.setOnClickListener(v -> abrirDestinoIntroducido());

        btnClose.setOnClickListener(v -> closeFragment());

        return rootView;
    }

    // Función para abrir Google Maps con una ubicación predefinida
    private void abrirUbicacionPredefinida() {
        String location = "55.751666666667,37.617777777778"; // Coordenadas de la Torre Eiffel, por ejemplo
        Uri uri = Uri.parse("geo:" + location);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    // Función para abrir Google Maps con una dirección personalizada
    private void abrirDestinoIntroducido() {
        String direccion = editDestino.getText().toString().trim();
        if (!direccion.isEmpty()) {
            Uri uri = Uri.parse("google.navigation:q=" + direccion); // Usamos 'google.navigation' para navegación
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        } else {
            // Si no se introduce una dirección válida, mostrar un mensaje (opcional)
            editDestino.setError("Por favor, ingresa una dirección.");
        }
    }

    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();

        // Restablecer el fondo del contenedor para quitar la opacidad
        View fragmentContainer = requireActivity().findViewById(R.id.fragmentContainer);
        if (fragmentContainer != null) {
            fragmentContainer.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        }
    }
}