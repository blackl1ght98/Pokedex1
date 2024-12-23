package com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.ExitoOFracaso.CaptureResult;

import java.util.List;

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
    interface CaptureCallback {
        void onResult(CaptureResult result);
    }
    interface SaveCallback {
        void onSuccess(String documentId); // Llamado si la operación es exitosa
        void onFailure(Exception e);      // Llamado si hay un error
    }
    interface FirestoreCallback {
        void onSuccess(boolean exists); // Manejar éxito y si el Pokémon existe o no
        void onFailure(Exception e);   // Manejar errores
    }
    interface OnPokemonsFetchedListener {
        void onPokemonsFetched(List<Pokemon> pokemons);
        void onPokemonsFetchFailed(Exception e);
    }
}
