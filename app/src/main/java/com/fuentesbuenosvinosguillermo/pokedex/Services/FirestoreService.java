package com.fuentesbuenosvinosguillermo.pokedex.Services;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.SharedViewModelInterface;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FirestoreService {
    private FirebaseFirestore db;

    public FirestoreService(FirebaseFirestore db) {
        this.db = db;
    }

    public void checkPokemonExists(String pokemonName, SharedViewModelInterface.FirestoreCallback callback) {
        db.collection("captured_pokemons")
                .whereEqualTo("name", pokemonName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots ->
                        callback.onSuccess(!queryDocumentSnapshots.isEmpty()))
                .addOnFailureListener(callback::onFailure);
    }

    public void savePokemon(Pokemon pokemon, SharedViewModelInterface.SaveCallback callback) {
        Map<String, Object> pokemonData = new HashMap<>();
        pokemonData.put("name", pokemon.getName());
        pokemonData.put("weight", pokemon.getWeight());
        pokemonData.put("height", pokemon.getHeight());
        pokemonData.put("orderPokedex", pokemon.orderPokedex());
        pokemonData.put("types", pokemon.getTypes().stream()
                .map(typeSlot -> typeSlot.getType().getName())
                .collect(Collectors.toList()));
        pokemonData.put("image", pokemon.getSprites().getFrontDefault());

        db.collection("captured_pokemons")
                .add(pokemonData)
                .addOnSuccessListener(documentReference -> callback.onSuccess(documentReference.getId()))
                .addOnFailureListener(callback::onFailure);
    }
    // Método que obtiene los pokemons capturados desde Firestore
    public void fetchCapturedPokemons(SharedViewModelInterface.OnPokemonsFetchedListener listener) {
        db.collection("captured_pokemons")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Pokemon> pokemons = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Pokemon pokemon = new Pokemon();
                        pokemon.setName(document.getString("name"));
                        pokemon.setWeight(document.getDouble("weight") != null ? document.getDouble("weight").intValue() : 0);
                        pokemon.setHeight(document.getDouble("height") != null ? document.getDouble("height").intValue() : 0);
                        pokemon.setOrder(document.getDouble("orderPokedex") != null ? document.getDouble("orderPokedex").intValue() : 0);

                        Pokemon.Sprites sprites = new Pokemon.Sprites();
                        sprites.setFrontDefault(document.getString("image"));
                        pokemon.setSprites(sprites);

                        List<Pokemon.TypeSlot> types = new ArrayList<>();
                        List<String> typeNames = (List<String>) document.get("types");
                        if (typeNames != null) {
                            for (String typeName : typeNames) {
                                Pokemon.TypeDetail type = new Pokemon.TypeDetail();
                                type.setName(typeName);
                                Pokemon.TypeSlot typeSlot = new Pokemon.TypeSlot();
                                typeSlot.setType(type);
                                types.add(typeSlot);
                            }
                        }
                        pokemon.setTypes(types);

                        pokemons.add(pokemon);
                    }
                    listener.onPokemonsFetched(pokemons);
                })
                .addOnFailureListener(e -> listener.onPokemonsFetchFailed(e));
    }
    // Método para eliminar un Pokémon de Firestore
    public void deletePokemonFromFirestore(Pokemon pokemon, SharedViewModelInterface.OnDeleteCallback callback) {
        db.collection("captured_pokemons")
                .whereEqualTo("name", pokemon.getName())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            db.collection("captured_pokemons").document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        callback.onDelete(true);
                                    })
                                    .addOnFailureListener(e -> callback.onDelete(false));
                        }
                    } else {
                        callback.onDelete(false);
                    }
                })
                .addOnFailureListener(e -> callback.onDelete(false));
    }
}

