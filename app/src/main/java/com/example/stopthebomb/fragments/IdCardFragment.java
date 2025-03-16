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
        TextView tvBirth = view.findViewById(R.id.tvBirth);
        Button btnBack = view.findViewById(R.id.btnBack);

        String name = "Andrew";
        String lastName = "Yelnats";
        String idNumber = "3458";
        String nationality = "EEUU";
        String birthday = "12/09/1924";
        int profileImage = R.drawable.ic_profile; // Puedes asignar una imagen específica aquí

        // Establecer los valores a las vistas
        imgProfile.setImageResource(profileImage);
        tvName.setText(getString(R.string.name_label) + " " + name);
        tvLastName.setText(getString(R.string.lastname_label) + " " +lastName);
        tvIdNumber.setText(getString(R.string.id_label) + " " + idNumber);
        tvNationality.setText(getString(R.string.nationality_label) + " " + nationality);
        tvBirth.setText(getString(R.string.birthday_label) + " " + birthday);

        // Acción para el botón Atrás
        btnBack.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());

        return view;
    }
}