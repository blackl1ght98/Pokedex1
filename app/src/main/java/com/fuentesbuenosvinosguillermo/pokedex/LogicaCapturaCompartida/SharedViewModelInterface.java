package com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;

public interface SharedViewModelInterface {

    interface OnNextPokemonCallback {
        void onNextPokemon(Pokemon pokemon);
    }
    interface OnDeleteCallback {
        void onDelete(boolean success);
    }
}
