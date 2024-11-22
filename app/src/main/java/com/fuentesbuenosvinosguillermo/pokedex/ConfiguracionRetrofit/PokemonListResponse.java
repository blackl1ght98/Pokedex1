package com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit;

import java.util.List;

public class PokemonListResponse {


    private List<PokemonResult> results;
    /**
     * Como en la clase PokemonResult esta pensada para solo recibir el nombre de un solo pokemon esto tenemos que arreglarlo para
     * que se pueda manejar varios nombres de varios pokemons esta es la razon por la cual se coloca en una lista que va almacenando
     * todos los nombres de los pokemon y esto permite poder mostrar varios de ellos
     *
     * */
    /*
    * @GET("pokemon")
    Call<PokemonListResponse> getPokemonList(
            @Query("offset") int offset,
            @Query("limit") int limit
    );
    * Cuando la llamada es realizada almacena todos los pokemon en la lista PokemonListResponse y los manda a esta clase que es la que estamos
    * luego desde esta clase lo mandamos a otra para obtener el nombre de todos los pokemons que esa clase es PokemonResult en el cual solo hay
    * un unico get que es el del nombre.*/
    //En esta lista es donde se almacenan todos los pokemons devueltos por la api
    public List<PokemonResult> getResults() {
        return results;
    }
}
