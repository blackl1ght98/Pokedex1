package com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import java.util.ArrayList;
import java.util.List;

public class CapturedPokemonManager {
    private static List<Pokemon> capturedPokemons = new ArrayList<>(); // Lista estática para mantener el estado

    public static void addCapturedPokemon(Pokemon pokemon) {
        if (!isCaptured(pokemon)) {
            capturedPokemons.add(pokemon); // Agrega el Pokémon si no está capturado
        }
    }

    public static boolean isCaptured(Pokemon pokemon) {
        // Verifica si el Pokémon ya está en la lista
        for (Pokemon captured : capturedPokemons) {
            if (captured.getName().equals(pokemon.getName())) {
                return true;
            }
        }
        return false;
    }
    public static void removeCapturedPokemon(Pokemon pokemon) {
        // Busca el Pokémon en la lista y lo elimina si está presente
        capturedPokemons.removeIf(captured -> captured.getName().equals(pokemon.getName()));
    }


}
