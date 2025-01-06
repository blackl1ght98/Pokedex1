package com.fuentesbuenosvinosguillermo.pokedex.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokemonResult;
import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.SharedViewModel;

import com.fuentesbuenosvinosguillermo.pokedex.RecyclerViewPokedex.AdapterPokedex;

import com.fuentesbuenosvinosguillermo.pokedex.databinding.FragmentPokedexBinding;

import java.util.ArrayList;
import java.util.List;



/**
 * Este fragmento se encarga de mostrar la lista de Pokémon obtenida desde la API de PokeAPI y permite su visualización
 * en un RecyclerView, utilizando View Binding y un ViewModel compartido para gestionar los datos.
 * <p>
 Se usa retrofit para realizar las peticiones a la api y tambien se hace uso de un viewmodel que es el encargado de compartir
 los datos
 */

public class Pokedex extends Fragment {
    private FragmentPokedexBinding binding;
    private AdapterPokedex adapterPokedex;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPokedexBinding.inflate(inflater, container, false);

        // Inicializa el ViewModel
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Configurar RecyclerView
        setupRecyclerView();

        // Observar los cambios en la lista de Pokémon desde el ViewModel
        sharedViewModel.getPokemonList(0, 150).observe(getViewLifecycleOwner(), pokemonListResponse -> {
            if (pokemonListResponse != null && pokemonListResponse.getResults() != null) {
                // Actualizar el adaptador con los nuevos datos
                adapterPokedex.updateList(pokemonListResponse.getResults());
            } else {
                // Mostrar mensaje de error si no se pueden cargar los datos
                Toast.makeText(getContext(), "Error al cargar los Pokémon", Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot(); // Retornar la vista raíz
    }

    /**
     * Configura el RecyclerView del fragmento.
     */
    private void setupRecyclerView() {
        adapterPokedex = new AdapterPokedex(getContext(), requireActivity());
        binding.pokedexRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.pokedexRecyclerview.setAdapter(adapterPokedex);
    }
}
