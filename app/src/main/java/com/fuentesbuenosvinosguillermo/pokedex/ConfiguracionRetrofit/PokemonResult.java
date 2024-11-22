package com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit;

public class PokemonResult {
    //Esta clase es necesaria porque si se analiza el json que devuelve pokeapi la propiedad name esta "sola" en el json sin encapsular las propiedades solas
    //me refiero que no esten dentro de otras se ponen aqui
    private String name;

//Gracias a que en la clase PokemonListResponse almacenamos los pokemons en una lista y esa lista es de tipo PokemonResult esto es lo
    public String getName() {
        return name;
    }


}
