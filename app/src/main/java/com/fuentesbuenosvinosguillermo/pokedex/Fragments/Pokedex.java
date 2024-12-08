package com.fuentesbuenosvinosguillermo.pokedex.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.ConfiguracionRetrofit;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokeApiService;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokemonListResponse;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokemonResult;
import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.SharedViewModel;
import com.fuentesbuenosvinosguillermo.pokedex.PokedexRepository;
import com.fuentesbuenosvinosguillermo.pokedex.RecyclerViewPokedex.AdapterPokedex;

import com.fuentesbuenosvinosguillermo.pokedex.databinding.FragmentPokedexBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Este fragmento se encarga de mostrar la lista de Pokémon obtenida desde la API de PokeAPI y permite su visualización
 * en un RecyclerView, utilizando View Binding y un ViewModel compartido para gestionar los datos.
 *
 * Flujo principal:
 * 1. Se configura el RecyclerView con un adaptador (AdapterPokedex) que muestra una lista de Pokémon.
 * 2. Se utiliza Retrofit para realizar las llamadas a la API y obtener los datos de los Pokémon.
 * 3. Se observa un LiveData en el SharedViewModel que emite la lista de Pokémon desde el repositorio.
 * 4. Cuando los datos cambian (nueva lista de Pokémon), se actualiza automáticamente el RecyclerView.
 *
 * Componentes principales:
 * - `FragmentPokedexBinding`: Para interactuar con las vistas del layout de manera eficiente.
 * - `SharedViewModel`: Gestiona la lista de Pokémon y permite compartir datos entre diferentes fragmentos.
 * - `AdapterPokedex`: Controla cómo se muestran los datos en el RecyclerView.
 * - `ConfiguracionRetrofit`: Clase de configuración de Retrofit para la interacción con la API.
 *
 * Métodos destacados:
 * - `onCreateView`: Configura el binding, el RecyclerView y la observación del ViewModel.
 * - `setupRecyclerView`: Inicializa el RecyclerView con el adaptador y un LayoutManager.
 *
 * Notas importantes:
 * - Se utiliza un ArrayList para almacenar los resultados obtenidos de la API y se actualiza dinámicamente.
 * - Si hay un error al cargar los datos, se muestra un Toast con un mensaje de error.
 */
public class Pokedex extends Fragment {

    private FragmentPokedexBinding binding;
    // Adaptador para gestionar los datos del RecyclerView.
    private AdapterPokedex adapterPokedex;
    // Lista de Pokémon a mostrar.
    private List<PokemonResult> pokemonList = new ArrayList<>();
    // Servicio de API para interactuar con PokeAPI.
    private PokeApiService apiService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /**
         * Configuración inicial del fragmento:
         * - Configurar View Binding para acceder a las vistas definidas en el layout XML.
         * - Inicializar Retrofit para gestionar las llamadas a la API.
         * - Configurar el RecyclerView para mostrar los datos.
         * - Observar el LiveData del ViewModel compartido para actualizar la lista de Pokémon.
         */
        binding = FragmentPokedexBinding.inflate(inflater, container, false);
        apiService = ConfiguracionRetrofit.getRetrofitInstance().create(PokeApiService.class);
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Configurar el RecyclerView
        setupRecyclerView();

        /**
         * Observar los cambios en la lista de Pokémon desde el ViewModel.
         * Cuando la lista se actualiza, se limpia la lista local y se agregan los nuevos datos.
         */
        sharedViewModel.getPokemonList(0, 150).observe(getViewLifecycleOwner(), pokemonListResponse -> {
            if (pokemonListResponse != null && pokemonListResponse.getResults() != null) {
                pokemonList.clear(); // Limpiar la lista antes de agregar los nuevos datos.
                pokemonList.addAll(pokemonListResponse.getResults()); // Agregar los datos nuevos.
                adapterPokedex.notifyDataSetChanged(); // Notificar cambios al adaptador.
            } else {
                // Mostrar un mensaje de error si no se pueden cargar los datos.
                Toast.makeText(getContext(), "Error al cargar los Pokémon", Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot(); // Retornar la vista raíz para mostrarla.
    }

    /**
     * Configura el RecyclerView del fragmento.
     * - Asigna un adaptador personalizado (AdapterPokedex) para mostrar los datos.
     * - Configura un LayoutManager para manejar el diseño en lista vertical.
     */
    private void setupRecyclerView() {
        /**
         * Parámetros utilizados en el adaptador:
         * - `pokemonList`: Lista de Pokémon que se mostrará en el RecyclerView.
         * - `apiService`: Servicio de API para cargar datos adicionales si es necesario.
         * - `getContext()`: Contexto de la actividad actual.
         * - `requireActivity()`: Actividad requerida para gestionar interacciones.
         */
        adapterPokedex = new AdapterPokedex(pokemonList, apiService, getContext(), requireActivity());
        binding.pokedexRecyclerview.setLayoutManager(new LinearLayoutManager(getContext())); // Diseño en lista vertical.
        binding.pokedexRecyclerview.setAdapter(adapterPokedex); // Asignar el adaptador al RecyclerView.
    }
}
