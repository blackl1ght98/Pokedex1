package com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit;

import java.util.List;

public class PokemonListResponse {


    private List<PokemonResult> results;
   /**
    * Si nos vamos a la interfaz llamada 'PokeApiService' vemos que el primer get que tenemos devuelve una lista que contiene todos los
    * pokemon al ser una lista lo que devuelve hay que tratarlo y para ello esos pokemon se almacenan en una lista y esta lista de pokemon
    * es pasada a otra clase llamada PokemonResult que lo unico que hara esta clase es 'guardar' el nombre de cada pokemon para usarlo en
    * la consulta para obtener informacion especifica de cada pokemon
    *
    * */
    //En esta lista es donde se almacenan todos los pokemons devueltos por la api
    public List<PokemonResult> getResults() {
        return results;
    }
}
