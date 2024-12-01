package com.fuentesbuenosvinosguillermo.pokedex.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.SharedViewModel;
import com.fuentesbuenosvinosguillermo.pokedex.LoginAndRegister.login;
import com.fuentesbuenosvinosguillermo.pokedex.R;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.FragmentAjustesBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;
import java.util.Objects;


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

        binding.cerrarSesion.setOnClickListener(v -> {
            // Crear un AlertDialog
            new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.confirmar_cierre_sesion)) // Título del dialogo
                    .setMessage(getString(R.string.desea_cerrar_sesion)) // Mensaje del dialogo
                    .setPositiveButton(getString(R.string.si), (dialogInterface, i) -> {
                        // Lógica para cerrar sesión si el usuario selecciona "Sí"
                        logOut();
                    })
                    .setNegativeButton(getString(R.string.no), (dialogInterface, i) -> {
                        // Solo cerramos el AlertDialog si selecciona "No"
                        dialogInterface.dismiss();
                    })
                    .show();
        });


        SharedPreferences preferences = requireActivity().getSharedPreferences("PokedexPrefs", Context.MODE_PRIVATE);
        boolean isEnabled = preferences.getBoolean("eliminacion_enabled", false);
        binding.habilitarEliminacion.setChecked(isEnabled);

        binding.habilitarEliminacion.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("eliminacion_enabled", isChecked);
            editor.apply();
        });
        return binding.getRoot();
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
        // Obtener el SharedViewModel
        SharedViewModel sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // Limpiar los datos del ViewModel
        sharedViewModel.clearCapturedPokemons();
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), login.class);
        startActivity(intent);
        requireActivity().finish();

    }
}