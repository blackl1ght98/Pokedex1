package com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit;

public class PokemonResult {
    private String name;

    //Gracias a que en la clase PokemonListResponse almacenamos los pokemons en una lista y esa lista es de tipo PokemonResult esto es lo que nos permite ver el
    //nombre de cada pokemon
    public String getName() {
        return name;
    }


}
