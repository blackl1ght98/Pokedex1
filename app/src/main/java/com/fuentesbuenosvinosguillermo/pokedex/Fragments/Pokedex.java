package com.fuentesbuenosvinosguillermo.pokedex.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import com.fuentesbuenosvinosguillermo.pokedex.SharedViewModel;
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

        adapterPokedex = new AdapterPokedex(pokemonList, pokemonResult -> {
            String pokemonName = pokemonResult.getName();

            // Obtener detalles completos del Pokémon
            //El motivo por el cual se hace otra vez la llamada a la api es para obtener los detalles especificos de cada pokemon
            PokeApiService apiService = ConfiguracionRetrofit.getRetrofitInstance().create(PokeApiService.class);
            //Esta vez la llamada se realiza a la segunda peticion get de PokeApiService esta peticion es la que devuelve informacion especifica de cada pokemon
            apiService.getPokemonDetails(pokemonName).enqueue(new Callback<Pokemon>() {
                @Override
                //Metodo que maneja tanto la llamada como la respuesta del api y esa respuesta lo pasa a la clase Pokemon que tiene metodos especificos para
                //acceder a cada elemento del pokemon
                public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                    //Si la respuesta del servidor es exitosa
                    if (response.isSuccessful() && response.body() != null) {
                        //El cuerpo de esa peticion de respuesta se guarda en la variable pokemon de tipo Pokemon
                        Pokemon pokemon = response.body();
                        //Hacemos una llamada a la clase que se encarga de decidir si un pokemon esta capturado o no y si no lo esta lo captura
                        //y notifica al SharedViewModel para que actualice la vista en tiempo real de pokemon capturados
                        if (!CapturedPokemonManager.isCaptured(pokemon)) {
                            // Agregar al CapturedPokemonManager
                            CapturedPokemonManager.addCapturedPokemon(pokemon);

                            // Notificar al SharedViewModel
                            SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
                            viewModel.addCapturedPokemon(pokemon);

                            Toast.makeText(getContext(), pokemon.getName() + " ha sido capturado", Toast.LENGTH_SHORT).show();
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
