package com.fuentesbuenosvinosguillermo.pokedex.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fuentesbuenosvinosguillermo.pokedex.CapturedPokemonManager;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.ConfiguracionRetrofit;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokeApiService;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokemonListResponse;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokemonResult;
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



    private void setupRecyclerView() {
        // Crear una instancia de PokedexRepository
        PokedexRepository repository = new PokedexRepository();  // No necesitas llamar a getPokeApiService

        // Configurar el LayoutManager y el Adapter
        binding.pokedexRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

//        // Pasa el apiService de la instancia de repository junto con los datos y el listener
//        adapter = new Adapter(pokemonList, pokemon -> {
//            // Acción al hacer clic en un Pokémon
//            Toast.makeText(getContext(), "Seleccionaste: " + pokemon.getName(), Toast.LENGTH_SHORT).show();
//        }, repository.apiService);  // Aquí pasas el apiService directamente desde PokedexRepository
        // Pokedex.java
        // Pokedex.java
        adapterPokedex = new AdapterPokedex(pokemonList, pokemonResult -> {
            // Obtener el nombre del Pokémon del PokémonResult
            String pokemonName = pokemonResult.getName();

            // Realizar la solicitud para obtener los detalles completos del Pokémon
            PokeApiService apiService = ConfiguracionRetrofit.getRetrofitInstance().create(PokeApiService.class);
            apiService.getPokemonDetails(pokemonName).enqueue(new Callback<Pokemon>() {
                @Override
                public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Obtener el Pokémon completo
                        Pokemon pokemon = response.body();

                        // Verificar si el Pokémon ya ha sido capturado
                        if (!CapturedPokemonManager.isCaptured(pokemon)) {
                            // Agregar Pokémon capturado
                            CapturedPokemonManager.addCapturedPokemon(pokemon);
                            Toast.makeText(getContext(), pokemon.getName() + " ha sido capturado", Toast.LENGTH_SHORT).show();

                            // Actualizar el RecyclerView de Pokémon Capturados
                            pokemonCapturados fragment = (pokemonCapturados) getParentFragmentManager().findFragmentByTag("capturadosFragment");
                            if (fragment != null) {
                                fragment.addCapturedPokemon(pokemon); // Llama al método para agregar al RecyclerView
                            }
                        } else {
                            Toast.makeText(getContext(), pokemon.getName() + " ya está capturado", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error al obtener detalles del Pokémon", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Pokemon> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }, repository.apiService);




        binding.pokedexRecyclerview.setAdapter(adapterPokedex);


        binding.pokedexRecyclerview.setAdapter(adapterPokedex);
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
                    adapterPokedex.notifyDataSetChanged();
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
