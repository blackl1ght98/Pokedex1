package com.fuentesbuenosvinosguillermo.pokedex.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class DetallesPokemonCapturado extends Fragment {
    private FragmentDetalleBinding binding;
    private List<Pokemon> pokemons;  // Lista de Pokémon capturados
    private int currentIndex =0;

    public DetallesPokemonCapturado() {
        // Constructor vacío requerido para los fragmentos
    }

    // Método estático para crear una nueva instancia del fragmento con los datos
    public static DetallesPokemonCapturado newInstance(Bundle bundle) {
        DetallesPokemonCapturado fragment = new DetallesPokemonCapturado();
        fragment.setArguments(bundle); // Establecer el Bundle con los datos
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetalleBinding.inflate(inflater, container, false);
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getCapturedPokemons().observe(getViewLifecycleOwner(), pokemons -> {
            if (pokemons != null && !pokemons.isEmpty()) {
                this.pokemons = pokemons;
                Log.d("DetallesFragment", "Pokémon observados: " + pokemons.toString());
            }

        });
        // Obtener los valores del Bundle
        if (getArguments() != null) {
            String pokemonName = getArguments().getString("pokemonName", "Pokémon desconocido");
            int pokemonPeso = getArguments().getInt("pokemonPeso", 0);
            int ordenPokedex = getArguments().getInt("pokemonIndice", 0);
            int pokemonAltura = getArguments().getInt("pokemonAltura", 0);
            String imagenUrl = getArguments().getString("imagenPokemon", "");
            String pokemonTipos = getArguments().getString("pokemonTipos", "");

            // Crear el objeto Pokémon con los datos recuperados del Bundle
            Pokemon pokemonAEliminar = new Pokemon();
            pokemonAEliminar.setName(pokemonName);
            pokemonAEliminar.setWeight(pokemonPeso);
            pokemonAEliminar.setHeight(pokemonAltura);

            Pokemon.Sprites sprites = new Pokemon.Sprites();
            sprites.setFrontDefault(imagenUrl);
            pokemonAEliminar.setSprites(sprites);

            // Aquí, puedes manejar los tipos de Pokémon como desees
            List<Pokemon.TypeSlot> types = new ArrayList<>();
            // Aquí se hace de forma básica, asumiendo que pokemonTipos es una lista de tipos.
            // Asegúrate de que `pokemonTipos` esté formateado adecuadamente.
            if (!pokemonTipos.isEmpty()) {
                String[] tiposArray = pokemonTipos.split(","); // Separar los tipos por coma
                for (String tipo : tiposArray) {
                    Pokemon.TypeDetail type = new Pokemon.TypeDetail();
                    type.setName(tipo.trim()); // Eliminar espacios alrededor del tipo
                    Pokemon.TypeSlot typeSlot = new Pokemon.TypeSlot();
                    typeSlot.setType(type);
                    types.add(typeSlot);
                }
            }
            pokemonAEliminar.setTypes(types);

            // Mostrar los valores en la UI
            binding.nombreDetallePokemon.setText(pokemonName);
            binding.pesoPesoPokemon.setText(String.valueOf(pokemonPeso));
            binding.ordenDetallePokedex.setText(String.valueOf(ordenPokedex));
            binding.alturaDetalleKemon.setText(String.valueOf(pokemonAltura));
            binding.tipoKemon.setText(pokemonTipos);
            if (!imagenUrl.isEmpty()) {
                Glide.with(requireContext())
                        .load(imagenUrl)
                        .into(binding.imagepokemon);
            }



            binding.eliminarPokemon.setOnClickListener(v->eliminarPokemon(sharedViewModel));
        }

        return binding.getRoot();
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

            Pokemon pokemonAEliminar = pokemons.get(0);

            // Eliminar el Pokémon de la lista del ViewModel
            sharedViewModel.removeCapturedPokemon(pokemonAEliminar);

            // Actualizar la lista local
            pokemons.remove(pokemonAEliminar);

            if (!pokemons.isEmpty()) {

                if (currentIndex >= pokemons.size()) {
                    currentIndex = pokemons.size() - 1;
                }

                if (mainActivity != null) {
                    mainActivity.redirectToFragment(1);
                }
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



    // Limpiar la vista cuando no hay Pokémon
    private void limpiarVistaPokemon() {
        MainActivity mainActivity = (MainActivity) getActivity();

        if (mainActivity != null) {
            mainActivity.redirectToFragment(0);
        }
    }
}

