package com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit;

public class PokemonResult {
    /**
     * Esta clase representa un solo resultado de la lista de Pokémon obtenida en la respuesta de la API.
     *
     * La información de esta clase proviene de la clase `PokemonListResponse`, que contiene una lista de objetos `PokemonResult`.
     * Cada objeto `PokemonResult` almacena únicamente el nombre de un Pokémon, que es utilizado para realizar consultas adicionales
     * y obtener detalles específicos de ese Pokémon.
     *
     * En resumen:
     * - Esta clase sirve como un contenedor para el nombre de un Pokémon que se extrae de la lista de resultados obtenidos de la API.
     * - El nombre almacenado en esta clase será utilizado más adelante para consultar información más detallada sobre el Pokémon seleccionado.
     *
     * Método:
     * - `getName()`: Devuelve el nombre del Pokémon almacenado en esta instancia.
     */

    private String name;

//Gracias a que en la clase PokemonListResponse almacenamos los pokemons en una lista y esa lista es de tipo PokemonResult esto es lo
    public String getName() {
        return name;
    }


}
