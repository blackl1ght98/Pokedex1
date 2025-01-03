package com.fuentesbuenosvinosguillermo.pokedex.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.fuentesbuenosvinosguillermo.pokedex.MainActivity;
import com.fuentesbuenosvinosguillermo.pokedex.RecyclerViewCapturados.AdapterCapturados;
import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.SharedViewModel;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.FragmentPokemonCapturadosBinding;

import java.util.Objects;

/**
Este fragmento es usado para mostrar los datos al usuario de los pokemon que va capturando en el layout asociado
 a este fragmento solo se mostrara el nombre del pokemon.
 Ademas los pokemon se veran en un CardView que a su vez estan en un RecyclerView al estar de esta forma permite
 que se gestione de forma eficiente
 */

public class pokemonCapturados extends Fragment {
    private FragmentPokemonCapturadosBinding binding;
    private AdapterCapturados adapterCapturados;
    private SharedViewModel sharedViewModel;
    private final Handler handler = new Handler();
    private Runnable updateRunnable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPokemonCapturadosBinding.inflate(inflater, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();

        // Obtener el SharedViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Inicializa el adaptador sin lista local
        adapterCapturados = new AdapterCapturados(mainActivity);

        // Configuración del RecyclerView
        setupRecyclerView();

        // Observar cambios en los Pokémon capturados
        sharedViewModel.getCapturedPokemons().observe(getViewLifecycleOwner(), capturedPokemons -> {
            // Notificar al adaptador que los datos han cambiado
            adapterCapturados.notifyDataSetChanged();
        });

        // Inicia la recuperación inicial desde Firestore
        sharedViewModel.fetchCapturedPokemons();

        // Iniciar actualización periódica
        startPeriodicUpdate();

        return binding.getRoot();
    }

    /**
     * Método encargado de la configuración del RecyclerView.
     */
    private void setupRecyclerView() {
        binding.pokemonsCapturadosRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.pokemonsCapturadosRecyclerview.setAdapter(adapterCapturados);
    }

    /**
     * Método encargado de realizar una actualización periódica para obtener los datos actuales.
     */
    private void startPeriodicUpdate() {
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (isConnected()) {
                    // Solo intenta actualizar si hay conexión
                    sharedViewModel.fetchCapturedPokemons();
                } else {
                    // Muestra un mensaje al usuario si no hay conexión
                    Toast.makeText(getContext(), "Sin conexión a Internet", Toast.LENGTH_SHORT).show();
                }

                // Reprograma la próxima ejecución
                handler.postDelayed(this, 3000);
            }
        };

        handler.post(updateRunnable);
    }

    /**
     * Método que comprueba si hay conexión a Internet.
     */
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    /**
     * Método que destruye la vista cuando se deja de usar.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Detener la actualización periódica cuando el fragmento ya no esté visible
        if (updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }
}

