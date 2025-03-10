package com.example.stopthebomb.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.stopthebomb.R;

public class DrawerFragment extends Fragment {

    public DrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer, container, false);

        Button btnIdCard = view.findViewById(R.id.btnIdCard);
        Button btnInstructions = view.findViewById(R.id.btnInstructions);
        Button btnPlans = view.findViewById(R.id.btnPlans);
        Button btnClose = view.findViewById(R.id.btnClose);


        btnIdCard.setOnClickListener(v -> openFragment(new IdCardFragment()));
        //btnInstructions.setOnClickListener(v -> openFragment(new InstructionsFragment()));
        //btnPlans.setOnClickListener(v -> openFragment(new PlansFragment()));
        btnClose.setOnClickListener(v -> closeFragment());
        return view;
    }

    private void openFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null) // Allows back navigation
                .commit();
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