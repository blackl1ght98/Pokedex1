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

public class Pokedex extends Fragment {

    private FragmentPokedexBinding binding;
    private AdapterPokedex adapterPokedex;
    private List<PokemonResult> pokemonList = new ArrayList<>();//Le pasamos este objeto ya que solo se mostrara el nombre
    private PokeApiService apiService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Configurar View Binding
        binding = FragmentPokedexBinding.inflate(inflater, container, false);
        //Inicializamos retrofit
        apiService = ConfiguracionRetrofit.getRetrofitInstance().create(PokeApiService.class);
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        // Configurar RecyclerView
        setupRecyclerView();
        // Observar los cambios en la lista de Pokémon desde el ViewModel
        sharedViewModel.getPokemonList(0, 150).observe(getViewLifecycleOwner(), pokemonListResponse -> {
            if (pokemonListResponse != null && pokemonListResponse.getResults() != null) {
                // Actualiza la lista de Pokémon
                pokemonList.clear(); // Limpiar la lista antes de agregar los nuevos datos
                pokemonList.addAll(pokemonListResponse.getResults());

                // Notificar al adaptador que los datos han cambiado
                adapterPokedex.notifyDataSetChanged();
            } else {
                // Manejo del error (si pokemonListResponse es null)
                Toast.makeText(getContext(), "Error al cargar los Pokémon", Toast.LENGTH_SHORT).show();
            }
        });



        return binding.getRoot();
    }

    private void setupRecyclerView() {
        // Crear una instancia del Adapter y pasarlo al RecyclerView
        /**
         * A la variable adapter le pasamos varios parametros
         * @param pokemonList este primer parametro es la lista de pokemon
         * @param apiService este parametro es para realizar la llamada a la api
         * @param getContext() obtiene el contexto de la actividad actual
         * @param requireActivity() le decimos que requiere la actividad actual para funcionar
         * */
        adapterPokedex = new AdapterPokedex(pokemonList, apiService, getContext(), requireActivity());
        binding.pokedexRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.pokedexRecyclerview.setAdapter(adapterPokedex);
    }



}
