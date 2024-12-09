package com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;

public interface SharedViewModelInterface {
/**
 * Interfaz usada en la clase sharedviewmodel
 * */
    interface OnNextPokemonCallback {
        void onNextPokemon(Pokemon pokemon);
    }
    interface OnDeleteCallback {
        void onDelete(boolean success);
    }
}
