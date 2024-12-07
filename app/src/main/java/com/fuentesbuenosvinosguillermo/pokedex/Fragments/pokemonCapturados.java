package com.fuentesbuenosvinosguillermo.pokedex.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


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
        // Runnable que actualizará la lista de Pokémon capturados cada cierto tiempo
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                // Llamada para actualizar los Pokémon capturados desde Firestore
                sharedViewModel.fetchCapturedPokemons();

                // Programar la próxima actualización en 10 segundos, el tiempo se puede ajustar al que queramos
                handler.postDelayed(this, 10000);
            }
        };

        // Ejecutar el Runnable por primera vez
        handler.post(updateRunnable);
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
