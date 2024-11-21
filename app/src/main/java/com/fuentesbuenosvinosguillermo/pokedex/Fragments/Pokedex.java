package com.fuentesbuenosvinosguillermo.pokedex.Fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokeApiService;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokemonListResponse;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokemonResult;
import com.fuentesbuenosvinosguillermo.pokedex.PokedexRepository;
import com.fuentesbuenosvinosguillermo.pokedex.R;
import com.fuentesbuenosvinosguillermo.pokedex.RecyclerViewPokedex.Adapter;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.FragmentPokedexBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Pokedex extends Fragment {

    private FragmentPokedexBinding binding;
    private Adapter adapter;
    private List<PokemonResult> pokemonList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Configurar View Binding
        binding = FragmentPokedexBinding.inflate(inflater, container, false);

        // Configurar RecyclerView
        setupRecyclerView();

        // Cargar datos de Pokémon
        fetchPokemonList();

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getContext().getString(R.string.pokedex));
        }
    }

    private void setupRecyclerView() {
        // Crear una instancia de PokedexRepository
        PokedexRepository repository = new PokedexRepository();  // No necesitas llamar a getPokeApiService

        // Configurar el LayoutManager y el Adapter
        binding.pokedexRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        // Pasa el apiService de la instancia de repository junto con los datos y el listener
        adapter = new Adapter(pokemonList, pokemon -> {
            // Acción al hacer clic en un Pokémon
            Toast.makeText(getContext(), "Seleccionaste: " + pokemon.getName(), Toast.LENGTH_SHORT).show();
        }, repository.apiService);  // Aquí pasas el apiService directamente desde PokedexRepository

        binding.pokedexRecyclerview.setAdapter(adapter);
    }



    private void fetchPokemonList() {
        // Llamar al repositorio para obtener datos
        PokedexRepository repository = new PokedexRepository();
        repository.fetchPokemonList(0, 150, new Callback<PokemonListResponse>() {
            @Override
            public void onResponse(Call<PokemonListResponse> call, Response<PokemonListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Agregar resultados al adaptador y actualizar RecyclerView
                    pokemonList.addAll(response.body().getResults());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al cargar los Pokémon", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PokemonListResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}
