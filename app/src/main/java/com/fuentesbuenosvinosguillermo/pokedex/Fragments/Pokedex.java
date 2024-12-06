package com.fuentesbuenosvinosguillermo.pokedex.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.ConfiguracionRetrofit;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokeApiService;

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
    private List<PokemonResult> pokemonList = new ArrayList<>();//Le pasamos este objeto ya que solo se mostrara el nombre
    private PokeApiService apiService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Configurar View Binding
        binding = FragmentPokedexBinding.inflate(inflater, container, false);
        //Inicializamos retrofit
        apiService = ConfiguracionRetrofit.getRetrofitInstance().create(PokeApiService.class);

        // Configurar RecyclerView
        setupRecyclerView();

        // Cargar datos de Pokémon
        fetchPokemonList();

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
//Metodo que obtiene la lista de pokemons hasta 150 pokemon
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
