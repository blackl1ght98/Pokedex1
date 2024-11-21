package com.fuentesbuenosvinosguillermo.pokedex.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fuentesbuenosvinosguillermo.pokedex.LoginAndRegister.login;
import com.fuentesbuenosvinosguillermo.pokedex.R;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.FragmentAjustesBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;


public class Ajustes extends Fragment {
private FragmentAjustesBinding binding;
    private FirebaseAuth mAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding= FragmentAjustesBinding.inflate(inflater,container,false);
        // Inicializamos FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        SharedPreferences prefs= requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String language = prefs.getString("Spanish","es");
        binding.cambiarIdioma.setChecked(language.equals("es"));
        binding.cambiarIdioma.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setLocale("es"); // Cambiar a español
            } else {
                setLocale("en"); // Cambiar a inglés
            }
        });
        // Asociar el botón de cerrar sesión con el OnClickListener
        binding.cerrarSesion.setOnClickListener(v -> logOut());
        return binding.getRoot();
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getContext().getString(R.string.ajustes));

        }
    }
    public void setLocale(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        config.setLocale(locale);
        resources.updateConfiguration(config,dm);

        SharedPreferences.Editor editor = requireActivity().getSharedPreferences("AppSettings",Context.MODE_PRIVATE).edit();
        editor.putString("Spanish",lang);
        editor.apply();
        requireActivity().recreate();

    }
    private void logOut(){
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), login.class);
        startActivity(intent);
        requireActivity().finish();

    }
}