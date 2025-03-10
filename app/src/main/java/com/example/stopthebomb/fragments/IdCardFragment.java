package com.example.stopthebomb.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.stopthebomb.R;

public class IdCardFragment extends Fragment {

    public IdCardFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Infla el layout del fragmento
        View view = inflater.inflate(R.layout.fragment_id_card, container, false);

        // Vínculos con las vistas del layout
        ImageView imgProfile = view.findViewById(R.id.imgProfile);
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvLastName = view.findViewById(R.id.tvLastName);
        TextView tvIdNumber = view.findViewById(R.id.tvIdNumber);
        TextView tvNationality = view.findViewById(R.id.tvNationality);
        Button btnBack = view.findViewById(R.id.btnBack);

        // Datos ficticios, deberías obtenerlos del modelo o base de datos
        String name = "Andrew";
        String lastName = "Yelnats";
        String idNumber = "22334";
        String nationality = "EEUU";
        int profileImage = R.drawable.ic_profile; // Puedes asignar una imagen específica aquí

        // Establecer los valores a las vistas
        imgProfile.setImageResource(profileImage);
        tvName.setText(name);
        tvLastName.setText(lastName);
        tvIdNumber.setText("ID: " + idNumber);
        tvNationality.setText("Nacionalidad: " + nationality);

        // Acción para el botón Atrás
        btnBack.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());

        return view;
    }
}