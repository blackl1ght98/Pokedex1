package com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit;

public class PokemonResult {
    private String name;
    private String url;
//Gracias a que en la clase PokemonListResponse almacenamos los pokemons en una lista y esa lista es de tipo PokemonResult esto es lo
    public String getName() {
        return name;
    }
    public String getUrl(){
        return url;
    }

}
