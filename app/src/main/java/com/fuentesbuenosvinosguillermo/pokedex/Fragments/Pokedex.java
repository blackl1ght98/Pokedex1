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
 Se usa retrofit para realizar las peticiones a la api y tambien se hace uso de un viewmodel que es el encargado de compartir
 los datos
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

        binding = FragmentPokedexBinding.inflate(inflater, container, false);
        //Inicializa retrofir para hacer peticiones
        apiService = ConfiguracionRetrofit.getRetrofitInstance().create(PokeApiService.class);
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Configurar el RecyclerView
        setupRecyclerView();

        /**
         * Observar los cambios en la lista de Pokémon desde el ViewModel.
         * Cuando la lista se actualiza, se limpia la lista local y se agregan los nuevos datos.
         * Aqui vemos que recibe 2 parametros:
         * @param getViewLifecycleOwner() que se encarga de obtener el ciclo de vida del fragmento
         * @param pokemonListResponse que contiene la respuesta de la api
         */
        sharedViewModel.getPokemonList(0, 150).observe(getViewLifecycleOwner(), pokemonListResponse -> {
            if (pokemonListResponse != null && pokemonListResponse.getResults() != null) {
                // Limpiar la lista antes de agregar los nuevos datos.
                pokemonList.clear();
                // Agregar los datos nuevos.
                pokemonList.addAll(pokemonListResponse.getResults());
                // Notificar cambios al adaptador.
                adapterPokedex.notifyDataSetChanged();
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
         * - `getContext()`: Contexto de la actividad actual.
         * - `requireActivity()`: Actividad requerida para gestionar interacciones.
         */
        adapterPokedex = new AdapterPokedex(pokemonList,  getContext(), requireActivity());
        binding.pokedexRecyclerview.setLayoutManager(new LinearLayoutManager(getContext())); // Diseño en lista vertical.
        binding.pokedexRecyclerview.setAdapter(adapterPokedex); // Asignar el adaptador al RecyclerView.
    }
}
