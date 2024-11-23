package com.fuentesbuenosvinosguillermo.pokedex;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.ConfiguracionRetrofit;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokeApiService;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokemonListResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PokedexRepository {

    public PokeApiService apiService;

    public PokedexRepository() {
        // Se crea la instancia del servicio API utilizando Retrofit
        apiService = ConfiguracionRetrofit.getRetrofitInstance().create(PokeApiService.class);
    }

    /**
     * Realiza una llamada al servidor para obtener la lista de Pokémon.
     *
     * @param offset El número inicial de Pokémon que se va a devolver.
     * @param limit El número máximo de Pokémon que se va a devolver.
     * @param callback La respuesta del servidor se maneja aquí, donde se recibirán los datos.
     */
    public void fetchPokemonList(int offset, int limit, Callback<PokemonListResponse> callback) {
        // Realiza la llamada al servidor pasándole el offset y el limit.
        Call<PokemonListResponse> call = apiService.getPokemonList(offset, limit);

        // Se ejecuta la llamada de forma asincrónica con el método enqueue de Retrofit
        call.enqueue(new Callback<PokemonListResponse>() {
            @Override
            public void onResponse(Call<PokemonListResponse> call, Response<PokemonListResponse> response) {
                // Si la respuesta fue exitosa y el cuerpo no es nulo
                if (response.isSuccessful() && response.body() != null) {
                    // Se pasa la respuesta al callback para que se maneje fuera de este método.
                    callback.onResponse(call, response);
                } else {
                    // En caso de que la respuesta no sea exitosa, se maneja el error.
                    callback.onFailure(call, new Throwable("Error al cargar la lista de Pokémon"));
                }
            }

            @Override
            public void onFailure(Call<PokemonListResponse> call, Throwable t) {
                // Si hubo un error al intentar realizar la llamada al servidor, se maneja aquí.
                callback.onFailure(call, t);
            }
        });
    }
    public void fetchPokemonDetails(String pokemonName, Callback<Pokemon> callback) {
        // Realiza la llamada al servidor para obtener detalles del Pokémon
        Call<Pokemon> call = apiService.getPokemonDetails(pokemonName);

        // Ejecuta la llamada asincrónicamente
        call.enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Si la respuesta es exitosa, pasa los datos al callback
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new Throwable("Error al obtener los detalles del Pokémon"));
                }
            }

            @Override
            public void onFailure(Call<Pokemon> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

}
