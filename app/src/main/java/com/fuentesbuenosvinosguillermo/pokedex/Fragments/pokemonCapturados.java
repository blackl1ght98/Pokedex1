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

import java.util.ArrayList;

public class pokemonCapturados extends Fragment {
    private FragmentPokemonCapturadosBinding binding;
    private AdapterCapturados adapterCapturados;
    private SharedViewModel sharedViewModel;
    private Handler handler = new Handler();
    private Runnable updateRunnable;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPokemonCapturadosBinding.inflate(inflater, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        // Inicializa el adaptador
        adapterCapturados = new AdapterCapturados(new ArrayList<>(), mainActivity);
        // Configurar RecyclerView
        setupRecyclerView();

        // Obtener el SharedViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observar cambios en los Pokémon capturados
        sharedViewModel.getCapturedPokemons().observe(getViewLifecycleOwner(), capturedPokemons -> {
            // Actualizamos los datos en tiempo real cuando haya cambios
            adapterCapturados.updateData(capturedPokemons);
        });

        // Inicia la recuperación inicial desde Firestore
        sharedViewModel.fetchCapturedPokemons();

        // Iniciar actualización periódica
        startPeriodicUpdate();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.pokemonsCapturadosRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.pokemonsCapturadosRecyclerview.setAdapter(adapterCapturados);
    }

    // Método para iniciar la actualización periódica
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
                handler.postDelayed(this, 10000);
            }
        };

        handler.post(updateRunnable);
    }
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Detener la actualización periódica cuando el fragmento ya no esté visible
        if (handler != null && updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }

}
