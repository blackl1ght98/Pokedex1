package com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit;

import java.util.List;

public class PokemonListResponse {
    /**
     * Este es el objeto que implementa el metodo Call que esta  en la interfaz PokeApiService si se decidiese implementar la paginacion
     * esta clase se podria usar para tal fin
     * */
    private int count;
    private String next;
    private String previous;
    private List<PokemonResult> results;

    public int getCount() {
        return count;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

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
