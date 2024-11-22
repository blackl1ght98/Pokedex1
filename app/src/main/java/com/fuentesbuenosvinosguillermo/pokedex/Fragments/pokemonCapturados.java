package com.fuentesbuenosvinosguillermo.pokedex.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fuentesbuenosvinosguillermo.pokedex.CapturedPokemonManager;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.R;
import com.fuentesbuenosvinosguillermo.pokedex.RecyclerViewCapturados.AdapterCapturados;
import com.fuentesbuenosvinosguillermo.pokedex.SharedViewModel;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.FragmentPokemonCapturadosBinding;

import java.util.ArrayList;
import java.util.List;

public class pokemonCapturados extends Fragment {
    private FragmentPokemonCapturadosBinding binding;
    private AdapterCapturados adapterCapturados;
    private final List<Pokemon> pokemonCapturadosList = new ArrayList<>(); // Lista de capturados





    // Asegúrate de cargar la lista de Pokémon capturados
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPokemonCapturadosBinding.inflate(inflater, container, false);

        // Configurar RecyclerView
        setupRecyclerView();

        // Observar cambios en los Pokémon capturados usando el SharedViewModel
        //Inicializa SharedViewModel
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        //llama a un metodo que hemos creado en sharedViewModel el cual es una lista dinamica y a esta lista
        //le pasamos un observador 'observe' que detectara los cambios en dicha lista ha este obsevador le pasamos
        //el ciclo de vida de la vista con getViewLifecycleOwner() que puede tener estos ciclos -->
        /*
        * Namely, the lifecycle of the Fragment's View is:
            created after onViewStateRestored(Bundle) --> creado
            started after onStart()--> empezado
            resumed after onResume()-->mostrado
            paused before onPause()-->pausado
            stopped before onStop()-->parado
            destroyed before onDestroyView()-->destruido
        *
        * */
        //luego le pasamos el nombre de la variable que retorna getCapturedPokemons() que es capturedPokemons y finalmente
        //esto va al adaptador y actualiza los datos con los nuevos pokemons
        sharedViewModel.getCapturedPokemons().observe(getViewLifecycleOwner(), capturedPokemons -> {
            adapterCapturados.updateData(capturedPokemons);
        });

        return binding.getRoot();
    }



    private void setupRecyclerView() {
        binding.pokemonsCapturadosRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterCapturados = new AdapterCapturados(new ArrayList<>()); // Inicializa con una lista vacía
        binding.pokemonsCapturadosRecyclerview.setAdapter(adapterCapturados);
    }



}
