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


/**
 * Este fragmento se encarga de manejar la configuración de la aplicación,
 * como el cambio de idioma, la gestión de la sesión y la habilitación de
 * opciones adicionales como la eliminación de elementos.
 *
 * En el método 'onCreateView', se inicializan los elementos de la interfaz
 * de usuario (UI) como los controles para cambiar el idioma, cerrar sesión
 * y habilitar o deshabilitar la eliminación de elementos.

 */
public class Ajustes extends Fragment {
    private FragmentAjustesBinding binding;
    private FirebaseAuth mAuth;
    private static final String TAG = "AjustesFragment";

    /**
     * Método que se ejecuta cuando se crea la vista del fragmento.
     * Configura el idioma inicial, el estado del switch y los botones de la UI.
     *
     * @param inflater El objeto LayoutInflater para inflar la vista del fragmento.
     * @param container El contenedor padre donde se inflará la vista.
     * @param savedInstanceState El estado guardado de la actividad anterior.
     * @return La vista del fragmento con los elementos configurados.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAjustesBinding.inflate(inflater, container, false);

        // Inicializamos FirebaseAuth para manejar la autenticación.
        mAuth = FirebaseAuth.getInstance();

        // Obtener idioma guardado de las preferencias de la aplicación.
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String savedLanguage = prefs.getString("language", null);

        // Establecer el idioma de la aplicación según la configuración guardada.
        setLocale(savedLanguage);

        // Configurar el estado inicial del switch de cambio de idioma.
        boolean isLanguageSpanish = savedLanguage.equals("es");
        binding.cambiarIdioma.setChecked(!isLanguageSpanish); // Inglés: true, Español: false

        // Listener para manejar el cambio de idioma.
        binding.cambiarIdioma.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String newLanguage = isChecked ? "en" : "es"; // Inglés si está activado, Español si no lo está.
            if (!newLanguage.equals(savedLanguage)) {
                setLocale(newLanguage); // Cambiar idioma.
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("language", newLanguage);
                editor.apply(); // Guardar nuevo idioma.
                if (!savedLanguage.equals(newLanguage)) {
                    requireActivity().recreate(); // Reiniciar la actividad para aplicar el cambio.
                }
            }
        });

        // Configurar la interfaz de usuario para manejar la lógica de la sesión y otras opciones.
        setupUI();

        return binding.getRoot();
    }

    /**
     * Método que cambia el idioma de la aplicación sin afectar la configuración global del sistema.
     *
     * @param language El código del idioma (por ejemplo, "es" para español, "en" para inglés).
     */
    private void setLocale(String language) {
        Log.d(TAG, "Estableciendo idioma: " + language);
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = requireActivity().getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    /**
     * Configura la interfaz de usuario con los botones y las acciones correspondientes,
     * como cerrar sesión y habilitar eliminación de elementos.
     */
    private void setupUI() {
        // Configuración para cerrar sesión.
        binding.cerrarSesion.setOnClickListener(v -> {
            Log.d(TAG, "Botón cerrar sesión presionado.");
            new AlertDialog.Builder(requireActivity())
                    .setTitle(getString(R.string.confirmar_cierre_sesion))
                    .setMessage(getString(R.string.desea_cerrar_sesion))
                    .setPositiveButton(getString(R.string.si), (dialogInterface, i) -> {
                        Log.d(TAG, "Confirmado: cerrar sesión.");
                        logOut(); // Llamada para cerrar sesión.
                    })
                    .setNegativeButton(getString(R.string.no), (dialogInterface, i) -> {
                        Log.d(TAG, "Cancelado: no cerrar sesión.");
                        dialogInterface.dismiss(); // Cancelar la acción.
                    })
                    .show();
        });

        // Configuración para mostrar información sobre la aplicación.
        binding.acercade.setOnClickListener(v -> {
            new AlertDialog.Builder(requireActivity())
                    .setTitle(getString(R.string.Acercade))
                    .setMessage(getString(R.string.develop))
                    .setPositiveButton(getString(R.string.si), (dialogInterface, i) -> {
                        Log.d(TAG, "Dialogo Acerca de cerrado.");
                        dialogInterface.dismiss(); // Cerrar el diálogo.
                    })
                    .show();
        });

        // Configuración para habilitar eliminación de elementos.
        SharedPreferences prefs = requireActivity().getSharedPreferences("PokedexPrefs", Context.MODE_PRIVATE);
        boolean isEnabled = prefs.getBoolean("eliminacion_enabled", false);
        binding.habilitarEliminacion.setChecked(isEnabled);

        // Listener para manejar el cambio en la opción de habilitar eliminación.
        binding.habilitarEliminacion.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("eliminacion_enabled", isChecked);
            editor.apply(); // Guardar el cambio de configuración.
        });
    }

    /**
     * Método para cerrar sesión en Firebase, limpiar los datos y redirigir a la pantalla de inicio de sesión.
     */
    private void logOut() {
        // Obtener el SharedViewModel para limpiar los datos.
        SharedViewModel sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.clearCapturedPokemons(); // Limpiar Pokémon capturados.

        // Cerrar sesión en Firebase.
        mAuth.signOut();

        // Navegar a la pantalla de inicio de sesión.
        Intent intent = new Intent(getActivity(), login.class);
        startActivity(intent);
        requireActivity().finish(); // Finalizar la actividad actual.
        Log.d(TAG, "Navegando a la pantalla de inicio de sesión.");
    }
}
