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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


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

            binding.botonSiguiente.setOnClickListener(v -> mostrarSiguientePokemon());
            binding.botonAnterior.setOnClickListener(v -> mostrarPokemonAnterior());
            binding.eliminarPokemon.setOnClickListener(v -> eliminarPokemon(sharedViewModel));


        }
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




private void eliminarPokemon(SharedViewModel sharedViewModel) {
    SharedPreferences prefs = requireActivity().getSharedPreferences("PokedexPrefs", Context.MODE_PRIVATE);
    MainActivity mainActivity = (MainActivity) getActivity();
    boolean eliminacionHabilitada = prefs.getBoolean("eliminacion_enabled", false);
    //Verifica si la eliminacion esta habilitada
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
    //Establecemos el objeto pokemon a null para que no traiga nada y a continuacion agregarle informacion del pokemon a eliminar
    Pokemon pokemonAEliminar = null;

    // Encuentra el Pokémon correspondiente en la lista por nombre
    for (Pokemon pokemon : pokemons) {
        //Si lo encuentra obtiene el nombre de ese pokemon que sera el pokemon que el usuario seleccione
        if (pokemon.getName().equals(pokemonSeleccionadoNombre)) {
            //La informacion de ese pokemon almacenalo en la variable pokemonAEliminar
            pokemonAEliminar = pokemon;
            break;
        }
    }
//Verificamos que pokemonAEliminar no sea null antes de proceder con la eliminacion
    if (pokemonAEliminar == null) {
        Toast.makeText(requireContext(), "Pokémon seleccionado no encontrado", Toast.LENGTH_SHORT).show();
        return;
    }

    // Copia el objeto en una variable  final para usarlo en la funcion lambda
    final Pokemon pokemonFinalAEliminar = pokemonAEliminar;

    // Log para depuración, para ver el nombre del pokemon
    Log.d("EliminarPokemon", "Intentando eliminar Pokémon: " + pokemonFinalAEliminar.getName());

    // Consulta Firestore para encontrar el documento correspondiente, mediante la id de firestore y el nombre del pokemon
    //esto asegura que se elimine el correcto
    db.collection("captured_pokemons")
            //Para eliminar un pokemon necesitaremos un campo unico para ello usamos el nombre del pokemon junto a la id de firestore
            .whereEqualTo("name", pokemonFinalAEliminar.getName())
            //Obtiene la informacion que devuelve la consulta anterior
            .get()
            //Manejamos esa consulta
            .addOnSuccessListener(queryDocumentSnapshots -> {
                //Si la consulta no viene vacia
                if (!queryDocumentSnapshots.isEmpty()) {
                    //Recorre la coleccion de firestore hasta llegar al nombre del pokemon en cuestion
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        //Obtener el id de ese pokemon ademas de su nombre
                        String firestoreId = document.getId();

                        // Elimina el documento en Firestore, lo primero es proporcionar el nombre de la coleccion
                        db.collection("captured_pokemons")
                                //Una vez obtenido el nombre del pokemon y la id aqui usamos el id que firestore le ha asignado
                                .document(firestoreId)
                                //Llamamos al metodo eliminar de firestore, para que lo elimine
                                .delete()
                                //Si todo ha ido bien
                                .addOnSuccessListener(aVoid -> {
                                    // Elimina el Pokémon localmente
                                    sharedViewModel.removeCapturedPokemon(pokemonFinalAEliminar);
                                    pokemons.remove(pokemonFinalAEliminar);

                                    // Marca al Pokémon como no capturado
                                    CapturedPokemonManager.removeCapturedPokemon(pokemonFinalAEliminar);

                                    Toast.makeText(requireContext(), pokemonFinalAEliminar.getName() + " eliminado con éxito de Firestore", Toast.LENGTH_SHORT).show();

                                    // Actualiza la interfaz, este if solo se ejecuta cuando al menos quede pokemons
                                    if (!pokemons.isEmpty()) {
                                        if (currentIndex >= pokemons.size()) {
                                            currentIndex = pokemons.size() - 1;
                                            mostrarPokemon(pokemons.get(currentIndex));
                                        }
                                        requireActivity().onBackPressed();

                                    } else {
                                      //Cuando ya no queden pokemons se muestra este toast y retrocede
                                        Toast.makeText(requireContext(), "No quedan Pokémon capturados", Toast.LENGTH_SHORT).show();
                                       requireActivity().onBackPressed();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    //En caso de fallo muestra este mensaje
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


}

