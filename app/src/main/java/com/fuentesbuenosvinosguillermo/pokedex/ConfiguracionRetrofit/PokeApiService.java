package com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PokeApiService {
    //Obtener lista de Pokemon
    /**
     * Cuando ya hemos configurado retrofit para poder usuarlo lo siguiente que hacemos es poner las 2 unicas peticiones que se pueden hacer a la api
     * que la primera get lo que hace es traerte todos los pokemon y la segunda get es para la informacion especifica de cada uno de esos pokemon.
     *
     * El método `Call` es utilizado por la librería Retrofit para realizar una petición HTTP a un servidor web y obtener una respuesta.
     * Este método puede ser llamado múltiples veces para hacer múltiples solicitudes al servidor. En este caso el servidor que nos responde es el
     * que contenga la API de pokeapi
     *
     * Las solicitudes pueden incluir parámetros que se pueden usar varias veces en una misma llamada.
     * El método `Call` se puede ejecutar de manera **sincrónica** o **asincrónica** dependiendo de cómo se gestione la respuesta.
     *
     * En este caso, la llamada realiza una solicitud GET a la ruta "pokemon" del servidor y obtiene una lista de Pokémon.
     *
     * @param offset El parámetro `offset` indica desde qué punto comenzar a obtener los datos.
     * @param limit El parámetro `limit` define la cantidad máxima de resultados a devolver.
     * @return Un objeto `Call` que contiene la respuesta de tipo `PokemonListResponse`.
     */
    @GET("pokemon")
    Call<PokemonListResponse> getPokemonList(
            @Query("offset") int offset,
            @Query("limit") int limit
    );

    //Obtener informacion especifica de un Pokemon
    /**
     * Para cada pokemon se obtienen los detalles de ese pokemon y se manda a la clase Pokemon que es donde esta la gran mayoria de getter
     * y se le pasa a esta peticion get el nombre del pokemon como parametro
     *
     * */
    @GET("pokemon/{name}")
    Call<Pokemon> getPokemonDetails(
            @Path("name") String name
    );
}
