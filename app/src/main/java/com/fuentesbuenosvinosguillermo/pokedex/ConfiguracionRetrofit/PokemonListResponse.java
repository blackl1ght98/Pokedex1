package com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit;

import java.util.List;

public class PokemonListResponse {


    private List<PokemonResult> results;
    /**
     * Esta clase representa la respuesta de la API que contiene una lista de resultados con los Pokémon.
     *
     * En la interfaz `PokeApiService`, la primera petición GET a la ruta "pokemon" devuelve una lista de objetos JSON, cada uno representando
     * un Pokémon. Esta lista de resultados se almacena en la propiedad `results`, que es una lista de objetos de tipo `PokemonResult`.
     *
     * La clase `PokemonResult` es responsable de contener solo los datos esenciales de cada Pokémon, como su nombre,
     * que posteriormente se utilizará para hacer otra solicitud a la API con el fin de obtener información más detallada
     * sobre cada uno de los Pokémon.
     *
     * En resumen:
     * - `PokemonListResponse` actúa como un contenedor para la lista de Pokémon obtenidos en la consulta inicial.
     * - Los Pokémon se almacenan como instancias de la clase `PokemonResult`, que solo contiene el nombre del Pokémon.
     *
     * Métodos:
     * - `getResults()`: Devuelve la lista de resultados (Pokémon) que contiene los nombres de todos los Pokémon obtenidos.
     */

    //En esta lista es donde se almacenan todos los pokemons devueltos por la api
    public List<PokemonResult> getResults() {
        return results;
    }
}
