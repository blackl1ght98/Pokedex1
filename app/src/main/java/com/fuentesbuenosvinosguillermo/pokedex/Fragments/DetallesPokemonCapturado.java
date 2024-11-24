package com.fuentesbuenosvinosguillermo.pokedex.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.SharedViewModel;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.FragmentDetalleBinding;

import java.util.List;


public class DetallesPokemonCapturado extends Fragment {
    private FragmentDetalleBinding binding;
    private List<Pokemon> pokemons;  // Lista de Pokémon capturados
    private int currentIndex = 0;  // Índice del Pokémon actual

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetalleBinding.inflate(inflater, container, false);

        // Obtener el SharedViewModel
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observar los datos de los Pokémon capturados
        sharedViewModel.getCapturedPokemons().observe(getViewLifecycleOwner(), pokemons -> {
            if (pokemons != null && !pokemons.isEmpty()) {
                this.pokemons = pokemons;
                Log.d("PruebaFragment", "Pokémon observados: " + pokemons.toString());

                // Mostrar el primer Pokémon en la lista (o el que tenga el índice actual)
                mostrarPokemon(currentIndex);

                // Configurar botones para navegar entre los Pokémon
                binding.botonAnterior.setOnClickListener(v -> {
                    if (currentIndex > 0) {
                        currentIndex--;  // Reducir el índice para mostrar el anterior
                        mostrarPokemon(currentIndex);
                    }
                });

                binding.botonSiguiente.setOnClickListener(v -> {
                    if (currentIndex < pokemons.size() - 1) {
                        currentIndex++;  // Aumentar el índice para mostrar el siguiente
                        mostrarPokemon(currentIndex);
                    }
                });
            } else {
                Log.d("PruebaFragment", "No hay Pokémon capturados.");
            }
        });

        return binding.getRoot();
    }

    // Método para mostrar un Pokémon específico
    private void mostrarPokemon(int index) {
        Pokemon pokemon = pokemons.get(index);

        // Hacer el binding directo a los TextViews
        binding.nombreDetallePokemon.setText(pokemon.getName());
        binding.pesoPesoPokemon.setText(String.valueOf(pokemon.getWeight()));
        binding.ordenDetallePokedex.setText(String.valueOf(pokemon.orderPokedex()));
        binding.alturaDetalleKemon.setText(String.valueOf(pokemon.getHeight()));

        // Cargar la imagen del Pokémon usando Glide
        Glide.with(requireContext())
                .load(pokemon.getSprites().getFrontDefault())  // URL de la imagen
                .into(binding.imagepokemon);
    }
}
