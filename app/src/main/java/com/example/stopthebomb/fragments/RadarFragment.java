package com.example.stopthebomb.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.stopthebomb.R;
import com.example.stopthebomb.database.DatabaseHelper;

import java.io.IOException;
import java.util.List;

public class RadarFragment extends Fragment {

    private static final double LATITUD_UBICACION_PREDEFINIDA = 55.751666666667;  //Kremlin
    private static final double LONGITUD_UBICACION_PREDEFINIDA = 37.617777777778;
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

        // Acción del botón Localización (abrir ubicación fija)
        btnLocalizacion.setOnClickListener(v -> abrirUbicacionPredefinida());

        // Acción del botón Establecer Destino (introducir dirección)
        btnEstablecerDestino.setOnClickListener(v -> abrirDestinoIntroducido());

        btnClose.setOnClickListener(v -> closeFragment());

        return rootView;
    }

    // Función para abrir Google Maps con una ubicación predefinida
    private void abrirUbicacionPredefinida() {
        // Coordenadas de la Torre Eiffel, por ejemplo
        String location = LATITUD_UBICACION_PREDEFINIDA + "," + LONGITUD_UBICACION_PREDEFINIDA;
        Uri uri = Uri.parse("geo:" + location);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    // Función para abrir Google Maps con una dirección personalizada
    private void abrirDestinoIntroducido() {
        String direccion = editDestino.getText().toString().trim();

        if (!direccion.isEmpty()) {
            if (direccion.equalsIgnoreCase("Kremlin") || direccion.equalsIgnoreCase("Moscú")) {
                // Si la dirección es Kremlin o Moscú, mostrar el Toast correspondiente
                Toast.makeText(getActivity(), "Kremlin establecido", Toast.LENGTH_SHORT).show();
            } else {
                // Aquí verificamos la distancia con la ubicación predefinida
                Location ubicacionPredefinida = new Location("predefinedLocation");
                ubicacionPredefinida.setLatitude(LATITUD_UBICACION_PREDEFINIDA);
                ubicacionPredefinida.setLongitude(LONGITUD_UBICACION_PREDEFINIDA);

                // Obtener la distancia entre la ubicación predefinida y la dirección introducida
                float distancia = calcularDistancia(ubicacionPredefinida, direccion);
                if (distancia > 5000) {
                    Toast.makeText(getActivity(), "Destino desviado", Toast.LENGTH_SHORT).show();
                    // Unlock the achievement in the database
                    DatabaseHelper dbHelper = DatabaseHelper.getInstance(getContext());
                    dbHelper.unlockAchievement(5);
                } else {
                    Toast.makeText(getActivity(), "Destino no válido", Toast.LENGTH_SHORT).show();

                }
            }
        } else {
            // Si no se introduce una dirección válida, mostrar un mensaje
            editDestino.setError("Por favor, ingresa una dirección.");
        }
    }

    // Método para calcular la distancia entre la ubicación predefinida y la dirección ingresada
    private float calcularDistancia(Location ubicacionPredefinida, String direccion) {
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> addresses = null;
        Location destino = new Location("dummyProvider");

        try {
            // Intenta obtener la lista de direcciones para la dirección introducida
            addresses = geocoder.getFromLocationName(direccion, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                destino.setLatitude(address.getLatitude());  // Obtiene la latitud
                destino.setLongitude(address.getLongitude()); // Obtiene la longitud
            } else {
                // Si no se pudo obtener la dirección, puedes manejar este caso como quieras
                Toast.makeText(getActivity(), "No se pudo encontrar la dirección", Toast.LENGTH_SHORT).show();
                return -1;  // Devuelve -1 en caso de error
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error al geocodificar la dirección", Toast.LENGTH_SHORT).show();
            return -1;  // Devuelve -1 en caso de error
        }

        // Calcula la distancia en kilómetros y devuelve el valor
        return ubicacionPredefinida.distanceTo(destino) / 1000;  // Distancia en kilómetros
    }
    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();

        // Restablecer el fondo del contenedor para quitar la opacidad
        View fragmentContainer = requireActivity().findViewById(R.id.fragmentContainer);
        if (fragmentContainer != null) {
            fragmentContainer.setBackgroundColor(Color.TRANSPARENT);
        }
    }
}
