package com.fuentesbuenosvinosguillermo.pokedex.ExitoOFracaso;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;

public class CaptureResult {
    private boolean success;
    private Pokemon pokemon;

    public CaptureResult(boolean success, Pokemon pokemon) {
        this.success = success;
        this.pokemon = pokemon;
    }

    public boolean isSuccess() {
        return success;
    }

    public Pokemon getPokemon() {
        return pokemon;
    }
}

