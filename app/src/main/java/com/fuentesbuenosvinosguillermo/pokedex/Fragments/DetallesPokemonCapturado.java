package com.fuentesbuenosvinosguillermo.pokedex.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.activity.OnBackPressedCallback;
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

import com.fuentesbuenosvinosguillermo.pokedex.R;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.FragmentDetalleBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class DetallesPokemonCapturado extends Fragment {
    private FragmentDetalleBinding binding;
    private List<Pokemon> pokemons;  // Lista de Pokémon capturados
    private int currentIndex =0;
    public DetallesPokemonCapturado() {
        // Constructor vacío requerido para los fragmentos
    }
@Override
public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentDetalleBinding.inflate(inflater, container, false);
    SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

    sharedViewModel.getSelectedPokemon().observe(getViewLifecycleOwner(), pokemon -> {
        if (pokemon != null) {
            // Actualiza la UI con los datos del Pokémon seleccionado
            binding.nombreDetallePokemon.setText(pokemon.getName());
            binding.pesoPokemon.setText(String.valueOf(pokemon.getWeight()));
            binding.ordenDetallePokedex.setText(String.valueOf(pokemon.orderPokedex()));
            binding.alturaDetallePokemon.setText(String.valueOf(pokemon.getHeight()));

            StringBuilder tipos = new StringBuilder();
            for (Pokemon.TypeSlot typeSlot : pokemon.getTypes()) {
                if (typeSlot.getType() != null && typeSlot.getType().getName() != null) {
                    tipos.append(typeSlot.getType().getName()).append(", ");
                }
            }

            // Eliminar la última coma
            if (tipos.length() > 0) {
                tipos.setLength(tipos.length() - 2);
            }
            binding.tipoPokemon.setText(tipos.toString());

            if (!pokemon.getSprites().getFrontDefault().isEmpty()) {
                Glide.with(requireContext())
                        .load(pokemon.getSprites().getFrontDefault())
                        .into(binding.imagepokemon);
            }
        }
    });

    // Configurar los botones de navegación
    binding.botonSiguiente.setOnClickListener(v -> mostrarSiguientePokemon());
    binding.botonAnterior.setOnClickListener(v -> mostrarPokemonAnterior());
    binding.eliminarPokemon.setOnClickListener(v -> eliminarPokemon(sharedViewModel));

    return binding.getRoot();
}

    private void mostrarSiguientePokemon() {
        if (pokemons != null && !pokemons.isEmpty()) {
            currentIndex++; // Avanza al siguiente Pokémon
            if (currentIndex >= pokemons.size()) {
                currentIndex = 0; // Vuelve al primer Pokémon si está al final de la lista
            }
            mostrarPokemon(pokemons.get(currentIndex));
        }
    }

    private void mostrarPokemonAnterior() {
        if (pokemons != null && !pokemons.isEmpty()) {
            currentIndex--; // Retrocede al Pokémon anterior
            if (currentIndex < 0) {
                currentIndex = pokemons.size() - 1; // Vuelve al último Pokémon si está al principio de la lista
            }
            mostrarPokemon(pokemons.get(currentIndex));
        }
    }
    /**
     * Metodo encargado de eliminar un pokemon recibe un unico parametro que es
     * @param sharedViewModel esta clase es la compartida y al usarla permite hacer todo en tiempo real
     *
     * */
private void eliminarPokemon(SharedViewModel sharedViewModel) {
     //Se comprueba si la eliminacion esta o no habilitada
    SharedPreferences prefs = requireActivity().getSharedPreferences("PokedexPrefs", Context.MODE_PRIVATE);
    boolean eliminacionHabilitada = prefs.getBoolean("eliminacion_enabled", false);

    if (!eliminacionHabilitada) {
        Toast.makeText(requireContext(), "La eliminación está deshabilitada", Toast.LENGTH_SHORT).show();
        return;
    }
    //Se obtiene el nombre del pokemon que el usuario a seleccionado
    String pokemonSeleccionadoNombre = binding.nombreDetallePokemon.getText().toString();
    //Se busca ese pokemon por el nombre
    Pokemon pokemonAEliminar = sharedViewModel.findPokemonByName(pokemonSeleccionadoNombre);
    //Si el pokemon no se encuentra muestra este mensaje
    if (pokemonAEliminar == null) {
        Toast.makeText(requireContext(), "Pokémon seleccionado no encontrado", Toast.LENGTH_SHORT).show();
        return;
    }
/**
 * Si el pokemon se encuentra se procede con la eliminacion, en la clase sharedviewmodel hemos dicho que se maneja la logica del llamador
 * pues llamador es desde donde llamas a ese metodo al llamarlo se le pasan 2 parametros
 * @param pokemonAEliminar que este almacenaria el nombre del pokemon, y success es si ha tenido exito o no al eliminar
 *
 * */
    sharedViewModel.deletePokemonFromFirestore(pokemonAEliminar, success -> {
        //Si la eliminacion ha sido exitosa
        if (success) {
            //Muestra este mensaje de toast si se elimia con exito
            Toast.makeText(requireContext(), pokemonAEliminar.getName() + " eliminado con éxito", Toast.LENGTH_SHORT).show();
            //Verifica si hay mas pokemon
            if (sharedViewModel.hasPokemons()) {
                //Si hay mas pokemon se desplaza al siguiente indice de la lista pokemons y muestra los datos del siguiente pokemon con una funcion lambda
                sharedViewModel.getNextPokemon(currentIndex, pokemons -> mostrarPokemon(pokemons));
            } else {
                //Si ya no quedan pokemon capturados vuelve hacia atras
                Toast.makeText(requireContext(), "No quedan Pokémon capturados", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        } else {
            //Si se produce un error en la eliminacion muestra este mensaje
            Toast.makeText(requireContext(), "Error al eliminar Pokémon", Toast.LENGTH_SHORT).show();
        }
    });
}
    /**
 * Metodo que muestra los detalles del pokemon en caso de haberlos
 *
 * */
    private void mostrarPokemon(Pokemon pokemon) {
        binding.nombreDetallePokemon.setText(pokemon.getName());
        binding.pesoPokemon.setText(String.valueOf(pokemon.getWeight()));
        binding.alturaDetallePokemon.setText(String.valueOf(pokemon.getHeight()));
        binding.tipoPokemon.setText(pokemon.getTypes().stream()
                .map(typeSlot -> typeSlot.getType().getName())
                .collect(Collectors.joining(", ")));

        Glide.with(requireContext())
                .load(pokemon.getSprites().getFrontDefault())
                .into(binding.imagepokemon);
    }

}