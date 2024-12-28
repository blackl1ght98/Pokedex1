package com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;


import java.util.List;

public interface SharedViewModelInterface {
/**
 * Interfaz usada en la clase sharedviewmodel
 * */
    interface OnNextPokemonCallback {
        void onNextPokemon(Pokemon pokemon, int nextIndex);
    }
    interface OnDeleteCallback {
        void onDelete(boolean success);
    }


}
