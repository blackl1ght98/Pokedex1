package com.fuentesbuenosvinosguillermo.pokedex.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.CapturedPokemonManager;
import com.fuentesbuenosvinosguillermo.pokedex.MainActivity;
import com.fuentesbuenosvinosguillermo.pokedex.R;
import com.fuentesbuenosvinosguillermo.pokedex.RecyclerViewCapturados.AdapterCapturados;
import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.SharedViewModel;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.FragmentPokemonCapturadosBinding;

import java.util.List;
import java.util.Objects;

/**
Este fragmento es usado para mostrar los datos al usuario de los pokemon que va capturando en el layout asociado
 a este fragmento solo se mostrara el nombre del pokemon.
 Ademas los pokemon se veran en un CardView que a su vez estan en un RecyclerView al estar de esta forma permite
 que se gestione de forma eficiente
 */

public class pokemonCapturados extends Fragment {
    private FragmentPokemonCapturadosBinding binding;
    private AdapterCapturados adapterCapturados;
    private SharedViewModel sharedViewModel;
    private final Handler handler = new Handler();
    private Runnable updateRunnable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPokemonCapturadosBinding.inflate(inflater, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();

        // Obtener el SharedViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Inicializa el adaptador sin lista local
        adapterCapturados = new AdapterCapturados(mainActivity);

        // Configuración del RecyclerView
        setupRecyclerView();
        setupSwipeToDelete();
        // Observar cambios en los Pokémon capturados
        sharedViewModel.getCapturedPokemons().observe(getViewLifecycleOwner(), capturedPokemons -> {
            // Notificar al adaptador que los datos han cambiado
            adapterCapturados.notifyDataSetChanged();
        });

        // Inicia la recuperación inicial desde Firestore
        sharedViewModel.fetchCapturedPokemons();

        // Iniciar actualización periódica
        startPeriodicUpdate();

        return binding.getRoot();
    }

    /**
     * Método encargado de la configuración del RecyclerView.
     */
    private void setupRecyclerView() {
        binding.pokemonsCapturadosRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.pokemonsCapturadosRecyclerview.setAdapter(adapterCapturados);
    }

    /**
     * Método encargado de realizar una actualización periódica para obtener los datos actuales.
     */
    private void startPeriodicUpdate() {
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (isConnected()) {
                    // Solo intenta actualizar si hay conexión
                    sharedViewModel.fetchCapturedPokemons();
                } else {
                    // Muestra un mensaje al usuario si no hay conexión
                    Toast.makeText(getContext(), "Sin conexión a Internet", Toast.LENGTH_SHORT).show();
                }

                // Reprograma la próxima ejecución
                handler.postDelayed(this, 3000);
            }
        };

        handler.post(updateRunnable);
    }

    /**
     * Método que comprueba si hay conexión a Internet.
     */
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    /**
     * Método que destruye la vista cuando se deja de usar.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Detener la actualización periódica cuando el fragmento ya no esté visible
        if (updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }
    /**
     * Método encargado de configurar la acción de deslizar para eliminar un Pokémon capturado.
     */
    private void setupSwipeToDelete() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // No necesitamos movimiento en este caso
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                // Obtener el Pokémon en la posición
                List<Pokemon> currentList = sharedViewModel.getCapturedPokemons().getValue();
                if (currentList != null && position >= 0 && position < currentList.size()) {
                    Pokemon pokemonAEliminar = currentList.get(position);

                    // Llamar al método de eliminación con confirmación
                    eliminarPokemon(sharedViewModel, getContext(), pokemonAEliminar, position);
                    sharedViewModel.removeCapturedPokemon(pokemonAEliminar);
                } else {
                    Toast.makeText(getContext(), "Error al eliminar Pokémon", Toast.LENGTH_SHORT).show();
                    adapterCapturados.notifyItemChanged(position); // Restaurar la vista si hay error
                }
            }
        });

        // Adjuntar el helper al RecyclerView
        itemTouchHelper.attachToRecyclerView(binding.pokemonsCapturadosRecyclerview);
    }

    /**
     * Método encargado de eliminar un Pokémon capturado con validaciones.
     * @param sharedViewModel Clase compartida entre fragmentos que gestiona los datos en tiempo real.
     * @param context Contexto actual para mostrar mensajes.
     * @param pokemonAEliminar Pokémon que se eliminará.
     * @param position Posición del Pokémon en la lista del RecyclerView.
     */

    private void eliminarPokemon(SharedViewModel sharedViewModel, Context context, Pokemon pokemonAEliminar, int position) {
        // Se comprueba si el switch está habilitado para eliminar un Pokémon
        SharedPreferences prefs = requireActivity().getSharedPreferences("PokedexPrefs", Context.MODE_PRIVATE);
        boolean eliminacionHabilitada = prefs.getBoolean("eliminacion_enabled", false);

        // Si la eliminación no está habilitada, mostrar mensaje y restaurar la vista en el RecyclerView
        if (!eliminacionHabilitada) {
            new AlertDialog.Builder(requireContext())
                    .setTitle(context.getString(R.string.titulo_eliminacion_deshabilitada))
                    .setMessage(context.getString(R.string.mensaje_eliminacion_deshabilitada))
                    .setPositiveButton(context.getString(R.string.aceptar), (dialog, which) -> {
                        dialog.dismiss();
                        adapterCapturados.notifyItemChanged(position);
                    })
                    .show();
            return;
        }

        // Confirmación antes de eliminar
        new AlertDialog.Builder(requireContext())
                .setTitle(context.getString(R.string.titulo_confirmar_eliminacion))
                .setMessage(String.format(context.getString(R.string.mensaje_eliminacion), pokemonAEliminar.getName()))
                .setPositiveButton(context.getString(R.string.aceptar), (dialog, which) -> {
                    sharedViewModel.deletePokemonFromFirestore(pokemonAEliminar, success -> {
                        if (success) {
                            // Eliminar de la lista de capturados
                            CapturedPokemonManager.removeCapturedPokemon(pokemonAEliminar);

                            // Notificar cambios al adaptador
                            adapterCapturados.notifyItemRemoved(position);
                            Toast.makeText(context, pokemonAEliminar.getName() + " eliminado correctamente", Toast.LENGTH_SHORT).show();

                            // Verificar si hay más Pokémon capturados
                            if (!sharedViewModel.hasPokemons()) {
                                Toast.makeText(requireContext(), "No quedan Pokémon capturados", Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            Toast.makeText(context, "Error al eliminar Pokémon", Toast.LENGTH_SHORT).show();
                            adapterCapturados.notifyItemChanged(position); // Restaurar la vista si hay error
                        }
                    });

                })
                .setNegativeButton(context.getString(R.string.cancelar), (dialog, which) -> {
                    dialog.dismiss();
                    adapterCapturados.notifyItemChanged(position); // Restaurar la vista si el usuario cancela
                })
                .show();
    }

}

