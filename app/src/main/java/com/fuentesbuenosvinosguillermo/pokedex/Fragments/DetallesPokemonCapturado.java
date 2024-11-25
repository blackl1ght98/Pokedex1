package com.fuentesbuenosvinosguillermo.pokedex.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.CapturedPokemonManager;
import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.SharedViewModel;
import com.fuentesbuenosvinosguillermo.pokedex.MainActivity;
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
            // Configurar botón de eliminación
            binding.eliminarPokemon.setOnClickListener(v -> eliminarPokemon(sharedViewModel));
        });

        return binding.getRoot();
    }


    private void mostrarPokemon(int index) {
        if (index < 0 || index >= pokemons.size()) {
            Toast.makeText(requireContext(), "Índice fuera de rango", Toast.LENGTH_SHORT).show();
            return;
        }

        Pokemon pokemon = pokemons.get(index);
        binding.nombreDetallePokemon.setText(pokemon.getName());
        binding.pesoPesoPokemon.setText(String.valueOf(pokemon.getWeight()));
        binding.ordenDetallePokedex.setText(String.valueOf(pokemon.orderPokedex()));
        binding.alturaDetalleKemon.setText(String.valueOf(pokemon.getHeight()));
        Glide.with(requireContext())
               .load(pokemon.getSprites().getFrontDefault())  // URL de la imagen
               .into(binding.imagepokemon);
    }
    private void eliminarPokemon(SharedViewModel sharedViewModel) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("PokedexPrefs", Context.MODE_PRIVATE);
        MainActivity mainActivity = (MainActivity) getActivity();
        boolean eliminacionHabilitada = prefs.getBoolean("eliminacion_enabled", false);

        if (eliminacionHabilitada) {
            if (pokemons.isEmpty()) {
                Toast.makeText(requireContext(), "No quedan Pokémon para eliminar", Toast.LENGTH_SHORT).show();
                return;
            }

            Pokemon pokemonAEliminar = pokemons.get(currentIndex);

            // Eliminar el Pokémon de la lista del ViewModel
            sharedViewModel.removeCapturedPokemon(pokemonAEliminar);

            // Actualizar la lista local
            pokemons.remove(pokemonAEliminar);

            if (!pokemons.isEmpty()) {
                // Ajustar el índice
                if (currentIndex >= pokemons.size()) {
                    currentIndex = pokemons.size() - 1; // Ir al último Pokémon si el índice actual excede el tamaño
                }
                mostrarPokemon(currentIndex);
                mainActivity.redirectToFragment(1);
                CapturedPokemonManager.removeCapturedPokemon(pokemonAEliminar);
            } else {
                // Si no quedan Pokémon, limpiar la vista
                limpiarVistaPokemon();
                Toast.makeText(requireContext(), "No quedan Pokémon capturados", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "La eliminación está deshabilitada", Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarVistaPokemon() {
        binding.nombreDetallePokemon.setText("");
        binding.pesoPesoPokemon.setText("");
        binding.ordenDetallePokedex.setText("");
        binding.alturaDetalleKemon.setText("");
        binding.imagepokemon.setImageResource(0); // Limpia la imagen
    }



}
