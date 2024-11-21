package com.fuentesbuenosvinosguillermo.pokedex;


import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.ConfiguracionRetrofit;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokeApiService;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokemonListResponse;

import retrofit2.Call;

import retrofit2.Callback;
import retrofit2.Response;
public class PokedexRepository {
    public PokeApiService apiService;
    public PokedexRepository(){
        apiService= ConfiguracionRetrofit.getRetrofitInstance().create(PokeApiService.class);
    }
    public void fetchPokemonList(int offset, int limit, Callback<PokemonListResponse> callback) {
        Call<PokemonListResponse> call = apiService.getPokemonList(offset, limit);
        call.enqueue(new Callback<PokemonListResponse>() {
            @Override
            public void onResponse(Call<PokemonListResponse> call, Response<PokemonListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new Throwable("Error al cargar la lista de Pokémon"));
                }
            }

            @Override
            public void onFailure(Call<PokemonListResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }
}
