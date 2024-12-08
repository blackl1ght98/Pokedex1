package com.fuentesbuenosvinosguillermo.pokedex.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.fuentesbuenosvinosguillermo.pokedex.MainActivity;
import com.fuentesbuenosvinosguillermo.pokedex.RecyclerViewCapturados.AdapterCapturados;
import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.SharedViewModel;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.FragmentPokemonCapturadosBinding;

import java.util.ArrayList;
/**
 * Este fragmento se encarga de mostrar la lista de Pokémon capturados por el usuario en un RecyclerView,
 * utilizando View Binding y un ViewModel compartido para gestionar los datos de manera dinámica.
 *
 * Flujo principal:
 * 1. Configura el RecyclerView con un adaptador (AdapterCapturados) para mostrar la lista de Pokémon capturados.
 * 2. Se utiliza un SharedViewModel para observar y gestionar los datos de los Pokémon capturados desde Firestore.
 * 3. Los datos observados en el LiveData del ViewModel se reflejan automáticamente en el RecyclerView.
 * 4. Implementa una verificación periódica de conexión a Internet para actualizar la lista de Pokémon capturados en tiempo real.
 *
 * Componentes principales:
 * - `FragmentPokemonCapturadosBinding`: Proporciona acceso eficiente a las vistas del layout mediante View Binding.
 * - `SharedViewModel`: Centraliza la lógica para obtener y observar los datos de los Pokémon capturados.
 * - `AdapterCapturados`: Controla cómo se visualizan los datos de los Pokémon en el RecyclerView.
 *
 * Métodos destacados:
 * - `onCreateView`: Configura el binding, inicializa el RecyclerView y vincula el ViewModel compartido.
 * - `setupRecyclerView`: Configura el RecyclerView con un LayoutManager y el adaptador correspondiente.
 * - `startPeriodicUpdate`: Inicia un mecanismo de actualización periódica que verifica la conexión a Internet antes de actualizar los datos.
 * - `isConnected`: Comprueba si hay conexión a Internet disponible.
 * - `onDestroyView`: Detiene la tarea periódica al destruirse el fragmento para evitar fugas de memoria.
 *
 * Notas importantes:
 * - La actualización de datos desde Firestore se realiza cada 10 segundos, siempre que haya conexión a Internet.
 * - Si no hay conexión, se notifica al usuario mediante un Toast.
 * - La periodicidad de actualización garantiza que la lista de Pokémon capturados esté siempre actualizada.
 */

public class pokemonCapturados extends Fragment {
    private FragmentPokemonCapturadosBinding binding;
    private AdapterCapturados adapterCapturados;
    private SharedViewModel sharedViewModel;
    private Handler handler = new Handler();
    private Runnable updateRunnable;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPokemonCapturadosBinding.inflate(inflater, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        // Inicializa el adaptador
        adapterCapturados = new AdapterCapturados(new ArrayList<>(), mainActivity);
        // Configurar RecyclerView
        setupRecyclerView();

        // Obtener el SharedViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observar cambios en los Pokémon capturados
        sharedViewModel.getCapturedPokemons().observe(getViewLifecycleOwner(), capturedPokemons -> {
            // Actualizamos los datos en tiempo real cuando haya cambios
            adapterCapturados.updateData(capturedPokemons);
        });

        // Inicia la recuperación inicial desde Firestore
        sharedViewModel.fetchCapturedPokemons();

        // Iniciar actualización periódica
        startPeriodicUpdate();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.pokemonsCapturadosRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.pokemonsCapturadosRecyclerview.setAdapter(adapterCapturados);
    }

    // Método para iniciar la actualización periódica
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
                handler.postDelayed(this, 10000);
            }
        };

        handler.post(updateRunnable);
    }
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Detener la actualización periódica cuando el fragmento ya no esté visible
        if (handler != null && updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }

}
