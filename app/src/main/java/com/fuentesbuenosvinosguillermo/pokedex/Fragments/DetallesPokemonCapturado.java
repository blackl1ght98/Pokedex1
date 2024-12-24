package com.fuentesbuenosvinosguillermo.pokedex.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

/**
 * Este fragmento se encarga de mostrar los detalles de un Pokémon capturado.
 */

public class DetallesPokemonCapturado extends Fragment {
    private FragmentDetalleBinding binding;
    private int currentIndex =0;
    private Runnable updateRunnable;
    private SharedViewModel sharedViewModel;
    public DetallesPokemonCapturado() {
        // Constructor vacío requerido para los fragmentos
    }
    /**
     * Método que se ejecuta cuando se crea la vista del fragmento.
     * Configura el como se va a ver un pokemon.
     *
     * @param inflater El objeto LayoutInflater para inflar la vista del fragmento.
     * @param container El contenedor padre donde se inflará la vista.
     * @param savedInstanceState El estado guardado de la actividad anterior.
     * @return La vista del fragmento con los elementos configurados.
     */
@Override
public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentDetalleBinding.inflate(inflater, container, false);
     sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    //Observador que observa el pokemon que se ha seleccionado
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

     // Configurar los botones de navegación, estos botones reciben el sharedviewmodel que como dijimos es la clase que se encarga de
    //compartir los datos
    binding.botonSiguiente.setOnClickListener(v -> mostrarSiguientePokemon(sharedViewModel));
    binding.botonAnterior.setOnClickListener(v -> mostrarPokemonAnterior(sharedViewModel));
    //Configuracion del boton de eliminacion
    binding.eliminarPokemon.setOnClickListener(v -> eliminarPokemon(sharedViewModel));

    return binding.getRoot();
}
/**
 * Metodo que muestra el siguiente pokemon al hacer clic en el boton siguien recibe como parametro
 * el sharedviewmodel que es la clase encargada de compartir los datos
 * */
    private void mostrarSiguientePokemon(SharedViewModel sharedViewModel) {
        sharedViewModel.getCapturedPokemons().observe(getViewLifecycleOwner(), pokemons -> {
            if (pokemons != null && !pokemons.isEmpty()) {
                // Avanza al siguiente Pokémon
                currentIndex++;
                if (currentIndex >= pokemons.size()) {
                    // Vuelve al primer Pokémon si está al final de la lista
                    currentIndex = 0;
                }
                mostrarPokemon(pokemons.get(currentIndex));
            }
        });
    }
/**
 * Parecido al metodo anterior pero muestra el pokemon anterior
 * */
    private void mostrarPokemonAnterior(SharedViewModel sharedViewModel) {
        sharedViewModel.getCapturedPokemons().observe(getViewLifecycleOwner(), pokemons -> {
            if (pokemons != null && !pokemons.isEmpty()) {
                // Retrocede al Pokémon anterior
                currentIndex--;
                if (currentIndex < 0) {
                    // Vuelve al último Pokémon si está al principio de la lista
                    currentIndex = pokemons.size() - 1;
                }
                mostrarPokemon(pokemons.get(currentIndex));
            }
        });
    }
    /**
     * Metodo encargado de eliminar un pokemon recibe un unico parametro que es
     * @param sharedViewModel esta clase compartida entre otros fragmentos y es la encargada de manejar
     *                        los datos que se comparten en tiempo real
     * */
    private void eliminarPokemon(SharedViewModel sharedViewModel) {
         //Se comprueba si el switch esta habilitado o no para eliminar un pokemon
        SharedPreferences prefs = requireActivity().getSharedPreferences("PokedexPrefs", Context.MODE_PRIVATE);
        boolean eliminacionHabilitada = prefs.getBoolean("eliminacion_enabled", false);
        //Si la eliminacion no esta habilitada se muestra el siguiente AlertDialog
        if (!eliminacionHabilitada) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Eliminacion deshabilitada")
                    .setMessage("La eliminacion no esta habilitada por favor habilitala")
                    .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                    .show();
            return;
        }
        //Se obtiene el nombre del pokemon que el usuario a seleccionado
        String pokemonSeleccionadoNombre = binding.nombreDetallePokemon.getText().toString();
        //Se busca ese pokemon por el nombre, usando la clase compartida
        Pokemon pokemonAEliminar = sharedViewModel.findPokemonByName(pokemonSeleccionadoNombre);
        //Si el pokemon no se encuentra muestra este AlertDialog
        if (pokemonAEliminar == null) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Pokemon no encontrado")
                    .setMessage("El pokemon seleccionado no se ha encontrado")
                    .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                    .show();
            return;
        }
    /**
    * Si el pokemon existe se llama al metodo deletePokemonFromFirestore que este metodo lo que recibe son 2 parametos
    * @param pokemonAEliminar que lo unico que recibe aquí es el nombre del pokemon
     * @param succes si esa eliminacion ha tenido exito o no
     * Como lo estamos llamando desde la clase compartida en cuanto un pokemon es eliminado se actualiza de forma inmediata la interfaz
    * */
        sharedViewModel.deletePokemonFromFirestore(pokemonAEliminar, success -> {
            //Si la eliminacion ha sido exitosa
            if (success) {
                //Muestra este AlertDialog de que el pokemon se elimino de forma exitosa
                new AlertDialog.Builder(requireContext())
                        .setTitle("Eliminacion exitosa")
                        .setMessage(pokemonAEliminar.getName() +" eliminado con exito")
                        .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                        .show();

                //Verifica si hay mas pokemon
                if (sharedViewModel.hasPokemons()) {
                    /*
                    * Si hay mas pokemon se desplaza al siguiente pokemon en base a su posicion(currentIndex) y muestra los detalles
                    * de ese pokemon en cuestion
                    * */
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
//    private void eliminarPokemon(SharedViewModel sharedViewModel) {
//        // Se comprueba si el switch esta habilitado o no para eliminar un pokemon
//        SharedPreferences prefs = requireActivity().getSharedPreferences("PokedexPrefs", Context.MODE_PRIVATE);
//        boolean eliminacionHabilitada = prefs.getBoolean("eliminacion_enabled", false);
//
//        // Si la eliminacion no esta habilitada se muestra el siguiente AlertDialog
//        if (!eliminacionHabilitada) {
//            new AlertDialog.Builder(requireContext())
//                    .setTitle("Eliminación deshabilitada")
//                    .setMessage("La eliminación no está habilitada. Por favor habilítala.")
//                    .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
//                    .show();
//            return;
//        }
//
//        // Se obtiene el nombre del pokemon que el usuario ha seleccionado
//        String pokemonSeleccionadoNombre = binding.nombreDetallePokemon.getText().toString();
//
//        // Se busca ese pokemon por el nombre, usando la clase compartida
//        Pokemon pokemonAEliminar = sharedViewModel.findPokemonByName(pokemonSeleccionadoNombre);
//
//        // Si el pokemon no se encuentra muestra este AlertDialog
//        if (pokemonAEliminar == null) {
//            new AlertDialog.Builder(requireContext())
//                    .setTitle("Pokemon no encontrado")
//                    .setMessage("El pokemon seleccionado no se ha encontrado")
//                    .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
//                    .show();
//            return;
//        }
//
//        // Llamamos al método para eliminar el pokemon de Firestore
//        sharedViewModel.deletePokemonFromFirestore(pokemonAEliminar);
//
//        // Observamos el LiveData deleteSuccess
//        sharedViewModel.getDeleteSuccess().observe(getViewLifecycleOwner(), success -> {
//           // Verifica si el fragmento sigue adjunto a la actividad
//                if (success) {
//                    // Si la eliminación ha sido exitosa
//                    new AlertDialog.Builder(requireContext())
//                            .setTitle("Eliminación exitosa")
//                            .setMessage(pokemonAEliminar.getName() + " eliminado con éxito")
//                            .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
//                            .show();
//
//                    // Verifica si hay más Pokémon
//                    if (sharedViewModel.hasPokemons()) {
//                        // Si hay más Pokémon, muestra el siguiente
//                        sharedViewModel.getNextPokemon(currentIndex, pokemons -> mostrarPokemon(pokemons));
//                    } else {
//                        // Si no quedan Pokémon capturados, vuelve hacia atrás
//                        Toast.makeText(requireContext(), "No quedan Pokémon capturados", Toast.LENGTH_SHORT).show();
//                        requireActivity().onBackPressed();
//                    }
//                } else {
//                    // Si se produce un error en la eliminación, muestra este mensaje
//                    Toast.makeText(requireContext(), "Error al eliminar Pokémon", Toast.LENGTH_SHORT).show();
//                }
//
//        });
//    }

    /**
     * Metodo que muestra los detalles del pokemon en caso de haberlos
     * recibe un parametro que es
         * @param pokemon  a partir de este objeto se obtiene los detalles de cada pokemon
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
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

}