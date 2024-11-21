package com.fuentesbuenosvinosguillermo.pokedex.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fuentesbuenosvinosguillermo.pokedex.CapturedPokemonManager;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.R;
import com.fuentesbuenosvinosguillermo.pokedex.RecyclerViewCapturados.AdapterCapturados;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.FragmentPokemonCapturadosBinding;

import java.util.ArrayList;
import java.util.List;

public class pokemonCapturados extends Fragment {
    private FragmentPokemonCapturadosBinding binding;
    private AdapterCapturados adapterCapturados;
    private final List<Pokemon> pokemonCapturadosList = new ArrayList<>(); // Lista de capturados





    // Asegúrate de cargar la lista de Pokémon capturados
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPokemonCapturadosBinding.inflate(inflater, container, false);

        // Cargar los Pokémon capturados desde CapturedPokemonManager

        pokemonCapturadosList.addAll(CapturedPokemonManager.getCapturedPokemons());

        // Configurar RecyclerView
        setupRecyclerView();

        // Notificar al adaptador que la lista ha cambiado
        adapterCapturados.notifyDataSetChanged();  // Esto asegura que la vista se actualice

        return binding.getRoot();
    }



    private void setupRecyclerView() {
        // Configurar LayoutManager
        binding.pokemonsCapturadosRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializar el adaptador con la lista de capturados
        adapterCapturados = new AdapterCapturados(pokemonCapturadosList);

        // Configurar el adaptador en el RecyclerView
        binding.pokemonsCapturadosRecyclerview.setAdapter(adapterCapturados);
    }

    public void addCapturedPokemon(Pokemon pokemon) {
        if (!CapturedPokemonManager.isCaptured(pokemon)) {
            // Agregar al gestor de capturados
            CapturedPokemonManager.addCapturedPokemon(pokemon);

            // Actualizar la lista local desde el gestor compartido
            pokemonCapturadosList.clear();
            pokemonCapturadosList.addAll(CapturedPokemonManager.getCapturedPokemons());

            // Notificar cambios al adaptador
            adapterCapturados.notifyDataSetChanged();
        }
    }

}
