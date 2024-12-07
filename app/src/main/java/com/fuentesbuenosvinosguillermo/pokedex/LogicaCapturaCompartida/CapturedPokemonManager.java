package com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import java.util.ArrayList;
import java.util.List;
/**
 * Clasee que contiene los metodos necesarios para decidir si un pokemon a sido capturado o no
 *
 * */
public class CapturedPokemonManager {
    //Lista estatica que guarda el estado de si un pokemon a sido capturado o no, a esta lista se le pasa un objeto de tipo Pokemon para poder
    //extraer datos como el nombre del pokemon.
    private static List<Pokemon> capturedPokemons = new ArrayList<>();
    /**
     * Metodo que verifica si el pokemon a sido o no capturado, a este metodo se le pasa un parametro
     * @param pokemon este es un objeto de tipo Pokemon que este objeto tiene los datos del pokemon
     *                el dato que se usa para averiguar si ha sido o no capturado es el nombre del pokemon
     * */
    public static boolean isCaptured(Pokemon pokemon) {
     //Recorre la lista que gurda los estados de si un pokemon a sido o no capturado
        for (Pokemon captured : capturedPokemons) {
            //Dependiendo del nombre del pokemon dice si esta o no capturado
            if (captured.getName().equals(pokemon.getName())) {
                return true;
            }
        }
        return false;
    }
    /**
     * Metodo parecido al anterior pero este agrega un pokemon a la lista de capturedPokemons si este no ha sido capturado,
     * cuenta con un parametro
     * @param  pokemon este objeto se encarga de mantener la informacion del pokemon
     *
     * */
    public static void addCapturedPokemon(Pokemon pokemon) {
        if (!isCaptured(pokemon)) {
            capturedPokemons.add(pokemon);
        }
    }
    /**
     * Metodo que elimina un pokemon de la lista de capturados en base la nombre
     * */
    public static void removeCapturedPokemon(Pokemon pokemon) {
        // Busca el Pokémon en la lista y lo elimina si está presente
        capturedPokemons.removeIf(captured -> captured.getName().equals(pokemon.getName()));
    }


}
