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
import android.util.Log;
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
    private static final String TAG = "AjustesFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAjustesBinding.inflate(inflater, container, false);

        // Inicializamos FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        // Obtener idioma guardado
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String savedLanguage = prefs.getString("language", "es"); // Por defecto, español
        // Establecer el idioma de la aplicación según la configuración guardada
        setLocale(savedLanguage);
        // Configurar la posición inicial del Switch según el idioma guardado
        boolean isLanguageSpanish = savedLanguage.equals("es");
        binding.cambiarIdioma.setChecked(!isLanguageSpanish); // Inglés: true, Español: false

        // Listener para manejar el cambio de idioma
        binding.cambiarIdioma.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String newLanguage = isChecked ? "en" : "es"; // Inglés si está activado, Español si no esta activado
            if (!newLanguage.equals(savedLanguage)) {
                // Cambiar idioma
                setLocale(newLanguage);

                // Guardar el nuevo idioma en las preferencias
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("language", newLanguage);
                editor.apply();

                // Reiniciar actividad para aplicar el cambio de idioma
                requireActivity().recreate();
            }
        });
        SharedPreferences preferences = requireActivity().getSharedPreferences("PokedexPrefs", Context.MODE_PRIVATE);
        boolean isEnabled = preferences.getBoolean("eliminacion_enabled", false);
        binding.habilitarEliminacion.setChecked(isEnabled);

        binding.habilitarEliminacion.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("eliminacion_enabled", isChecked);
            editor.apply();
        });

        setupUI(prefs);

        return binding.getRoot();
    }


    private void setLocale(String language) {
        Log.d(TAG, "Estableciendo idioma: " + language);

        // Cambia el idioma solo para la aplicación, no afecta al sistema global.
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = requireActivity().getResources();
        Configuration config = resources.getConfiguration();

        // Cambia la configuración solo para la aplicación
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        Log.d(TAG, "Configuración de idioma actualizada para la aplicación.");
    }


    private void setupUI(SharedPreferences prefs) {
        // Configuración para cerrar sesión
        binding.cerrarSesion.setOnClickListener(v -> {
            Log.d(TAG, "Botón cerrar sesión presionado.");
            new AlertDialog.Builder(requireActivity())
                    .setTitle(getString(R.string.confirmar_cierre_sesion))
                    .setMessage(getString(R.string.desea_cerrar_sesion))
                    .setPositiveButton(getString(R.string.si), (dialogInterface, i) -> {
                        Log.d(TAG, "Confirmado: cerrar sesión.");
                        logOut();
                    })
                    .setNegativeButton(getString(R.string.no), (dialogInterface, i) -> {
                        Log.d(TAG, "Cancelado: no cerrar sesión.");
                        dialogInterface.dismiss();
                    })
                    .show();
        });

        // Configuración para "Acerca de"
        binding.acercade.setOnClickListener(v -> {

            new AlertDialog.Builder(requireActivity())
                    .setTitle(getString(R.string.Acercade))
                    .setMessage(getString(R.string.develop))
                    .setPositiveButton(getString(R.string.si), (dialogInterface, i) -> {
                        Log.d(TAG, "Dialogo Acerca de cerrado.");
                        dialogInterface.dismiss();
                    })
                    .show();
        });

        // Configuración para habilitar eliminación
        boolean isEnabled = prefs.getBoolean("eliminacion_enabled", false);
        binding.habilitarEliminacion.setChecked(isEnabled);


        binding.habilitarEliminacion.setOnCheckedChangeListener((buttonView, isChecked) -> {

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("eliminacion_enabled", isChecked);
            editor.apply();

        });
    }

    private void logOut() {

        // Obtener el SharedViewModel
        SharedViewModel sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        // Limpiar los datos del ViewModel
        sharedViewModel.clearCapturedPokemons();
        // Cerrar sesión en Firebase
        mAuth.signOut();

        // Navegar a la pantalla de inicio de sesión
        Intent intent = new Intent(getActivity(), login.class);
        startActivity(intent);
        requireActivity().finish();
        Log.d(TAG, "Navegando a la pantalla de inicio de sesión.");
    }

}