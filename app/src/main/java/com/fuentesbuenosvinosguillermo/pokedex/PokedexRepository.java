package com.fuentesbuenosvinosguillermo.pokedex;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.ConfiguracionRetrofit;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokeApiService;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokemonListResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PokedexRepository {

    private PokeApiService apiService;

    public PokedexRepository() {
        // Se crea la instancia del servicio API utilizando Retrofit
        apiService = ConfiguracionRetrofit.getRetrofitInstance().create(PokeApiService.class);
    }

    /**
     * Realiza una llamada al servidor para obtener la lista de Pokémon.
     *
     * @param offset El número inicial de Pokémon que se va a devolver.
     * @param limit El número máximo de Pokémon que se va a devolver.
     * @return LiveData que contiene la respuesta de la API.
     */
    public LiveData<PokemonListResponse> fetchPokemonList(int offset, int limit) {
        MutableLiveData<PokemonListResponse> liveData = new MutableLiveData<>();

        // Realiza la llamada al servidor pasándole el offset y el limit.
        Call<PokemonListResponse> call = apiService.getPokemonList(offset, limit);

        // Se ejecuta la llamada de forma asincrónica con el método enqueue de Retrofit
        call.enqueue(new Callback<PokemonListResponse>() {
            @Override
            public void onResponse(Call<PokemonListResponse> call, Response<PokemonListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Se pasa la respuesta a LiveData para que se observe en la UI
                    liveData.setValue(response.body());
                } else {
                    // Manejo de errores
                    liveData.setValue(null); // O podrías usar una clase personalizada de error
                }
            }

            @Override
            public void onFailure(Call<PokemonListResponse> call, Throwable t) {
                // Manejo de fallos en la llamada
                liveData.setValue(null); // O puedes definir un objeto de error
            }
        });

        return liveData;
    }
}
