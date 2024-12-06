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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class DetallesPokemonCapturado extends Fragment {
    private FragmentDetalleBinding binding;
    private List<Pokemon> pokemons;  // Lista de Pokémon capturados
    private int currentIndex =0;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MainActivity activity;
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
            binding.pesoPokemon.setText(String.valueOf(pokemonPeso));
            binding.ordenDetallePokedex.setText(String.valueOf(ordenPokedex));
            binding.alturaDetallePokemon.setText(String.valueOf(pokemonAltura));
            binding.tipoPokemon.setText(pokemonTipos);
            if (!imagenUrl.isEmpty()) {
                Glide.with(requireContext())
                        .load(imagenUrl)
                        .into(binding.imagepokemon);
            }



            binding.eliminarPokemon.setOnClickListener(v->eliminarPokemon(sharedViewModel));
        }

        return binding.getRoot();
    }




//    private void eliminarPokemon(SharedViewModel sharedViewModel) {
//        //Verifica si esta habilitada o no la eliminacion
//        SharedPreferences prefs = requireActivity().getSharedPreferences("PokedexPrefs", Context.MODE_PRIVATE);
//        boolean eliminacionHabilitada = prefs.getBoolean("eliminacion_enabled", false);
//
//        if (!eliminacionHabilitada) {
//            Toast.makeText(requireContext(), "La eliminación está deshabilitada", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        //Si no hay pokemon que eliminar muestra este mensaje
//        if (pokemons == null || pokemons.isEmpty()) {
//            Toast.makeText(requireContext(), "No hay Pokémon para eliminar", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Obtiene el nombre del pokemon que ha seleccionado el usuario
//        String pokemonSeleccionadoNombre = binding.nombreDetallePokemon.getText().toString();
//        //Establece a null el objeto Pokemon para manejar los datos del pokemon seleccionado
//        Pokemon pokemonAEliminar = null;
//
//        // Encuentra el Pokémon a eliminar basado en el nombre
//        for (Pokemon pokemon : pokemons) {
//            if (pokemon.getName().equals(pokemonSeleccionadoNombre)) {
//                pokemonAEliminar = pokemon;
//                break;
//            }
//        }
//
//        if (pokemonAEliminar == null) {
//            Toast.makeText(requireContext(), "No se encontró el Pokémon para eliminar", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Log para depuración
//        Log.d("EliminarPokemon", "Intentando eliminar: " + pokemonAEliminar.getName());
//
//        // Elimina el Pokémon de la lista y del ViewModel
//        sharedViewModel.removeCapturedPokemon(pokemonAEliminar);
//        pokemons.remove(pokemonAEliminar);
//
//        Toast.makeText(requireContext(), pokemonAEliminar.getName() + " eliminado con éxito", Toast.LENGTH_SHORT).show();
//
//        // Si la lista queda vacía, limpia la vista
//        if (pokemons.isEmpty()) {
//            limpiarVistaPokemon();
//        } else {
//            // Ajusta el índice actual si es necesario
//            if (currentIndex >= pokemons.size()) {
//                currentIndex = pokemons.size() - 1;
//            }
//
//            // Actualiza la vista para mostrar el próximo Pokémon
//            mostrarPokemon(pokemons.get(currentIndex));
//        }
//
//        CapturedPokemonManager.removeCapturedPokemon(pokemonAEliminar);
//    }
private void eliminarPokemon(SharedViewModel sharedViewModel) {
    SharedPreferences prefs = requireActivity().getSharedPreferences("PokedexPrefs", Context.MODE_PRIVATE);
    MainActivity mainActivity = (MainActivity) getActivity();
    boolean eliminacionHabilitada = prefs.getBoolean("eliminacion_enabled", false);

    if (!eliminacionHabilitada) {
        Toast.makeText(requireContext(), "La eliminación está deshabilitada", Toast.LENGTH_SHORT).show();
        return;
    }

    // Verifica si hay Pokémon en la lista
    if (pokemons == null || pokemons.isEmpty()) {
        Toast.makeText(requireContext(), "No quedan Pokémon para eliminar", Toast.LENGTH_SHORT).show();
        return;
    }

    // Obtiene el nombre del Pokémon seleccionado en la interfaz
    String pokemonSeleccionadoNombre = binding.nombreDetallePokemon.getText().toString();
    Pokemon pokemonAEliminar = null;

    // Encuentra el Pokémon correspondiente en la lista por nombre
    for (Pokemon pokemon : pokemons) {
        if (pokemon.getName().equals(pokemonSeleccionadoNombre)) {
            pokemonAEliminar = pokemon;
            break;
        }
    }

    if (pokemonAEliminar == null) {
        Toast.makeText(requireContext(), "Pokémon seleccionado no encontrado", Toast.LENGTH_SHORT).show();
        return;
    }

    // Copia el objeto en una variable final para usarlo en la lambda
    final Pokemon pokemonFinalAEliminar = pokemonAEliminar;

    // Log para depuración
    Log.d("EliminarPokemon", "Intentando eliminar Pokémon: " + pokemonFinalAEliminar.getName());

    // Consulta Firestore para encontrar el documento correspondiente
    db.collection("captured_pokemons")
            .whereEqualTo("name", pokemonFinalAEliminar.getName()) // Filtra por nombre (o un campo único)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String firestoreId = document.getId();

                        // Elimina el documento en Firestore
                        db.collection("captured_pokemons")
                                .document(firestoreId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    // Elimina el Pokémon localmente
                                    sharedViewModel.removeCapturedPokemon(pokemonFinalAEliminar);
                                    pokemons.remove(pokemonFinalAEliminar);

                                    // Marca al Pokémon como no capturado
                                    CapturedPokemonManager.removeCapturedPokemon(pokemonFinalAEliminar);

                                    Toast.makeText(requireContext(), pokemonFinalAEliminar.getName() + " eliminado con éxito de Firestore", Toast.LENGTH_SHORT).show();

                                    // Actualiza la interfaz
                                    if (!pokemons.isEmpty()) {
                                        if (currentIndex >= pokemons.size()) {
                                            currentIndex = pokemons.size() - 1;



                                        }
                                        mostrarPokemon(pokemons.get(currentIndex));
                                        //requireActivity().onBackPressed();
                                        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                                            @Override
                                            public void handleOnBackPressed() {
                                                // Aquí defines lo que pasa al retroceder
                                                if (!pokemons.isEmpty()) {
                                                    mostrarPokemon(pokemons.get(currentIndex)); // Ejemplo
                                                } else {
                                                    requireActivity().getSupportFragmentManager().popBackStack(); // Navega hacia atrás en la pila de fragmentos
                                                }
                                            }
                                        });



                                    } else {
                                        //limpiarVistaPokemon();

                                        Toast.makeText(requireContext(), "No quedan Pokémon capturados", Toast.LENGTH_SHORT).show();
                                        //requireActivity().onBackPressed();
                                        requireActivity().getSupportFragmentManager().popBackStack();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(requireContext(), "Error al eliminar Pokémon de Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("Firestore", "Error eliminando Pokémon de Firestore", e);
                                });
                    }
                } else {
                    // No se encontró el Pokémon en Firestore
                    Toast.makeText(requireContext(), "Pokémon no encontrado en Firestore", Toast.LENGTH_SHORT).show();
                    Log.d("Firestore", "Pokémon no encontrado en Firestore");
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(requireContext(), "Error al obtener datos de Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Firestore", "Error al obtener datos de Firestore", e);
            });
}



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






    // Limpiar la vista cuando no hay Pokémon
    private void limpiarVistaPokemon() {
        MainActivity mainActivity = (MainActivity) getActivity();

        if (mainActivity != null) {
            mainActivity.redirectToFragment(0);
        }
    }
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        // Liberar la referencia del binding para evitar fugas de memoria
//        binding = null;
//    }

}

