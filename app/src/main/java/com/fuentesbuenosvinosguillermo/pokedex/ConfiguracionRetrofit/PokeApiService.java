package com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
/**
 * Interfaz de Retrofit que define las dos únicas peticiones que se pueden hacer a la API de PokeAPI:
 * 1. `getPokemonList()`: Obtiene una lista de Pokémon con paginación.
 * 2. `getPokemonDetails()`: Obtiene la información específica de un Pokémon en particular.
 *
 * Retrofit utiliza el objeto `Call` para realizar solicitudes HTTP y obtener respuestas del servidor.
 * Este objeto permite ejecutar las solicitudes de manera **sincrónica** o **asincrónica**.
 *
 * Cada petición puede incluir parámetros que ayudan a personalizar la respuesta de la API.
 */
public interface PokeApiService {


    /**
     * Realiza una solicitud GET para obtener una lista de Pokémon desde la API.
     *
     * - La URL base está definida en la configuración de Retrofit, y se le añade el endpoint `"pokemon"`.
     * - Se utilizan los parámetros `offset` y `limit` para controlar la paginación.
     * - La respuesta se encapsula en un objeto `Call` de tipo `PokemonListResponse`, que contiene la lista de Pokémon obtenida.
     *
     * @param offset Indica desde qué punto de la lista comenzar a obtener los datos.
     * @param limit Define la cantidad máxima de Pokémon a devolver en la respuesta.
     * @return Un objeto `Call` con la lista de Pokémon obtenida desde la API.
     */
    @GET("pokemon")
    Call<PokemonListResponse> getPokemonList(
            @Query("offset") int offset,
            @Query("limit") int limit
    );


    /**
     * Realiza una solicitud GET para obtener los detalles de un Pokémon específico.
     *
     * - Se accede al endpoint `"pokemon/{name}"`, donde `{name}` es un parámetro dinámico que representa el nombre del Pokémon.
     * - La respuesta obtenida se mapea a la clase `Pokemon`, que contiene los atributos y métodos necesarios para gestionar la información recibida.
     *
     * @param name Nombre del Pokémon del cual se desean obtener los detalles.
     * @return Un objeto `Call` que contiene la información detallada del Pokémon solicitado.
     */
    @GET("pokemon/{name}")
    Call<Pokemon> getPokemonDetails(
            @Path("name") String name
    );
}
