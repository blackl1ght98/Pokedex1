package com.fuentesbuenosvinosguillermo.pokedex.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.RecyclerViewCapturados.AdapterCapturados;
import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.SharedViewModel;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.FragmentPokemonCapturadosBinding;

import java.util.ArrayList;

public class pokemonCapturados extends Fragment {
    private FragmentPokemonCapturadosBinding binding;
    private AdapterCapturados adapterCapturados;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPokemonCapturadosBinding.inflate(inflater, container, false);

        // Configurar RecyclerView
        setupRecyclerView();

        // Observar cambios en los Pokémon capturados usando el SharedViewModel
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getCapturedPokemons().observe(getViewLifecycleOwner(), capturedPokemons -> {
            adapterCapturados.updateData(capturedPokemons);
        });
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.pokemonsCapturadosRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        // Configurar el adaptador sin listener
        adapterCapturados = new AdapterCapturados(new ArrayList<>());

        binding.pokemonsCapturadosRecyclerview.setAdapter(adapterCapturados);
    }

}
