package com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit;

public class PokemonResult {
    /**
     * A esta clase le llega la información de la clase 'PokemonListResponse' que como dijimos esta solo se encar de almacenar el nombre del pokemon
     * para poder hacer uso de el mas adelante realmente solo almacena un nombre y dependiendo de lo que el usuario seleccione se ppondra aqui un valor u otro.
     * */
    private String name;

//Gracias a que en la clase PokemonListResponse almacenamos los pokemons en una lista y esa lista es de tipo PokemonResult esto es lo
    public String getName() {
        return name;
    }


}
