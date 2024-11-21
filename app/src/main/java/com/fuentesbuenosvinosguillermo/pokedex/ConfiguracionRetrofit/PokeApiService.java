package com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PokeApiService {
    //Obtener lista de Pokemon
    @GET("pokemon")
    Call<PokemonListResponse> getPokemonList(
            @Query("offset") int offset,
            @Query("limit") int limit
    );
    //Obtener informacion especifica de un Pokemon
    @GET("pokemon/{name}")
    Call<Pokemon> getPokemonDetails(
            @Path("name") String name
    );
}
