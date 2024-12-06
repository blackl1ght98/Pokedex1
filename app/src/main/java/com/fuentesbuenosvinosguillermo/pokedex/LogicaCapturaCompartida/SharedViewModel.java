package com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
/**
 * Una clase que extiende ViewModel, lo que permite compartir datos entre diferentes componentes de la UI (por ejemplo, Fragmentos)
 * de forma segura mientras se mantiene el ciclo de vida de los datos independiente de las vistas.
 *
 * */
public class SharedViewModel extends ViewModel {
    /**MutableLiveData: Es un tipo de dato reactivo en Android que permite observar cambios en su valor y notificar automáticamente a sus observadores.
     * aclaración de notificar automáticamente a sus observadores: esto es cuando lo llamamos desde otra clase
     * List<Pokemon>: El MutableLiveData contiene una lista de objetos Pokemon. Esto permite mantener y gestionar dinámicamente una colección de Pokémon capturados en la aplicación.
     * */
    private final MutableLiveData<List<Pokemon>> capturedPokemons = new MutableLiveData<>(new ArrayList<>());
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    /**
     * Devuelve el LiveData que representa la lista de Pokémon capturados.
     * Permite a otras clases (como fragmentos) observar los cambios en la lista
     * sin permitirles modificar directamente los datos.
     *
     * Esto garantiza la encapsulación: solo el ViewModel puede actualizar
     * la lista a través de métodos específicos como addCapturedPokemon().
     */
    public LiveData<List<Pokemon>> getCapturedPokemons() {

        if (capturedPokemons.getValue() != null) {
            Log.d("SharedViewModel", "getCapturedPokemons: Lista actual: " + capturedPokemons.getValue().toString());
        } else {
            Log.d("SharedViewModel", "getCapturedPokemons: La lista está vacía o es nula.");
        }

        return capturedPokemons;
    }
   //Metodo que agrega un pokemon a la lista
   public void addCapturedPokemon(Pokemon pokemon) {
       if (pokemon == null) return;

       List<Pokemon> currentList = capturedPokemons.getValue();
       if (currentList == null) {
           currentList = new ArrayList<>();
       }
       currentList.add(pokemon);
       Log.d("SharedViewModel", "Pokemon añadido: " + pokemon.getName() + " - Total: " + currentList.size());
       capturedPokemons.setValue(currentList); // Notifica cambios
   }

    public void removeCapturedPokemon(Pokemon pokemon) {
        List<Pokemon> currentList = capturedPokemons.getValue();
        if (currentList != null) {
            currentList.remove(pokemon); // Elimina el Pokémon de la lista
            capturedPokemons.setValue(currentList); // Notifica los cambios
            Log.d("SharedViewModel", "Pokemon eliminado: " + pokemon.getName());
        } else {
            Log.d("SharedViewModel", "La lista de Pokémon está vacía o nula.");
        }
    }

    public void clearCapturedPokemons() {
        capturedPokemons.setValue(new ArrayList<>()); // Limpia la lista
        Log.d("SharedViewModel", "Lista de Pokémon capturados limpiada.");
    }
    public void fetchCapturedPokemons() {
        db.collection("captured_pokemons")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Pokemon> pokemons = new ArrayList<>();
                    for (com.google.firebase.firestore.DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Pokemon pokemon = new Pokemon();

                        // Mapear los datos de Firestore al objeto Pokémon
                        pokemon.setName(document.getString("name"));

                        // Convertir Double a int para height y weight
                        pokemon.setWeight(document.getDouble("weight") != null ? document.getDouble("weight").intValue() : 0);
                        pokemon.setHeight(document.getDouble("height") != null ? document.getDouble("height").intValue() : 0);
                        pokemon.setOrder(document.getDouble("orderPokedex") != null ? document.getDouble("orderPokedex").intValue():0);
                        // Manejo de sprites
                        Pokemon.Sprites sprites = new Pokemon.Sprites();
                        sprites.setFrontDefault(document.getString("image"));
                        pokemon.setSprites(sprites);

                        // Manejo de tipos
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

                    capturedPokemons.setValue(pokemons);
                })
                .addOnFailureListener(e -> {
                    // En caso de error, establece la lista como vacía
                    capturedPokemons.setValue(new ArrayList<>());
                });
    }



}
