package com.fuentesbuenosvinosguillermo.pokedex.Services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.CapturedPokemonManager;
import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.SharedViewModel;
import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.SharedViewModelInterface;
import com.fuentesbuenosvinosguillermo.pokedex.R;
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

    public void deletePokemonFromFirestore(Pokemon pokemon, SharedViewModelInterface.OnDeleteCallback callback, SharedViewModel sharedViewModel) {
        db.collection("captured_pokemons")
                .whereEqualTo("name", pokemon.getName())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            db.collection("captured_pokemons").document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // Aquí llamamos al método removeCapturedPokemon desde el SharedViewModel
                                        sharedViewModel.removeCapturedPokemon(pokemon);
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
    public void capturePokemon(Pokemon pokemon, Context context, SharedViewModel sharedViewModel) {
        // Verificar si el Pokémon ya está capturado localmente
        if (CapturedPokemonManager.isCaptured(pokemon)) {
            showAlreadyCapturedDialog(context, pokemon);
            return;
        }

        // Verificar si el Pokémon ya está en Firestore
        db.collection("captured_pokemons")
                .whereEqualTo("name", pokemon.getName())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // El Pokémon ya está en Firestore
                        showAlreadyCapturedDialog(context, pokemon);
                    } else {
                        // Proceder con la captura y guardado en Firestore
                        capturePokemonInFirestore(pokemon, context);

                        // Aquí agregamos el Pokémon a la lista local
                        List<Pokemon> currentList = sharedViewModel.getCapturedPokemons().getValue(); // Obtener la lista actual desde el LiveData
                        if (currentList == null) {
                            currentList = new ArrayList<>();
                        }
                        currentList.add(pokemon); // Añadir el Pokémon capturado a la lista
                        sharedViewModel.updateCapturedPokemons(currentList); // Actualizar la lista en el ViewModel
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejo de errores si la consulta falla
                    Log.e("Firestore", "Error al verificar captura en Firestore: " + e.getMessage());
                    Toast.makeText(context, "Error al verificar el estado del Pokémon", Toast.LENGTH_SHORT).show();
                });
    }
    /**
     * Metodo que es llamado en el metodo capturePokemon para comunicar al usuario de que el pokemon
     * ha sido capturado
     * */
    public void showAlreadyCapturedDialog(Context context, Pokemon pokemon) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.titulo_pokemon_capturado))
                .setMessage(pokemon.getName() + " " + context.getString(R.string.mensaje_pokemon_capturado))
                .setPositiveButton(context.getString(R.string.aceptar), (dialog, which) -> dialog.dismiss())
                .show();
    }
    /**
     *  Metodo que es llamado en el metodo capturePokemon para guardar la informacion del pokemon
     *  en caso de que este no este capturado
     * */
    public void capturePokemonInFirestore(Pokemon pokemon, Context context) {
        // Añadir el Pokémon a la lista de capturados localmente
        CapturedPokemonManager.addCapturedPokemon(pokemon);

        // Crear los datos del Pokémon para Firestore
        Map<String, Object> pokemonData = new HashMap<>();
        pokemonData.put("name", pokemon.getName());
        pokemonData.put("weight", pokemon.getWeight());
        pokemonData.put("height", pokemon.getHeight());
        pokemonData.put("orderPokedex", pokemon.getorderPokedex());
        pokemonData.put("types", pokemon.getTypes().stream()
                .map(typeSlot -> typeSlot.getType().getName())
                .collect(Collectors.toList()));
        pokemonData.put("image", pokemon.getSprites().getFrontDefault());

        // Guardar el Pokémon en Firestore
        db.collection("captured_pokemons")
                .add(pokemonData)
                .addOnSuccessListener(documentReference -> {
                    String firestoreId = documentReference.getId();
                    pokemon.setFirestoreId(firestoreId);

                    // Actualizar Firestore con el firestoreId
                    db.collection("captured_pokemons")
                            .document(firestoreId)
                            .update("firestoreId", firestoreId)
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "ID de Firestore actualizado correctamente."))
                            .addOnFailureListener(e -> Log.e("Firestore", "Error al actualizar el ID de Firestore: " + e.getMessage()));

                    // Mostrar mensaje de éxito
                    Toast.makeText(context, "¡Pokémon guardado en Firestore!", Toast.LENGTH_SHORT).show();
                    showCaptureSuccessDialog(context, pokemon);
                })
                .addOnFailureListener(e -> {
                    // Manejo de errores si la captura falla
                    Toast.makeText(context, "Error al guardar en Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    /**
     * Metodo que es usado en capturePokemon que notifica al usuario de que el pokemon ha sido capturado de forma exitosa
     * */
    private void showCaptureSuccessDialog(Context context, Pokemon pokemon) {
        // Mostrar un diálogo de éxito al capturar el Pokémon
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.captura))
                .setMessage(pokemon.getName() + " " + context.getString(R.string.capturado))
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .show();
    }
    /**
     * Metodo encargado de obtener la información almacenada en firestore, la informacion que se obtendra es la de los pokemon capturados
     * se usa en la clase 'pokemonCapturados' para matener la lista actualizada
     * */
    public void fetchCapturedPokemons( SharedViewModel sharedViewModel) {

        db.collection("captured_pokemons")
                //Obtenemos los datos de esa coleccion
                .get()
                //Si no hay errores devuelve el resultado de esa consulta y se almacena en la variable queryDocumentSnapshots
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    //Se declara un array vacio de tipo Pokemon que su funcion es almacenar la informacion de los pokemon
                    List<Pokemon> pokemons = new ArrayList<>();
                    //Obtenemos la información de cada documento que exista en esta coleccion
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        //Inicializamos el objeto pokemon
                        Pokemon pokemon = new Pokemon();
                        // Mapear los datos de Firestore al objeto Pokémon
                        pokemon.setName(document.getString("name"));
                        // Convertir Double a int para height,weight y orderPokedex
                        pokemon.setWeight(document.getDouble("weight") != null ? document.getDouble("weight").intValue() : 0);
                        pokemon.setHeight(document.getDouble("height") != null ? document.getDouble("height").intValue() : 0);
                        pokemon.setOrder(document.getDouble("orderPokedex") != null ? document.getDouble("orderPokedex").intValue():0);
                        // Manejo de sprites (imagenes), en el objeto Pokemon hay una clase Sprites que alamacena los valores de la imagen
                        Pokemon.Sprites sprites = new Pokemon.Sprites();
                        //Obtenemos el enlace de la imagen de firestore
                        sprites.setFrontDefault(document.getString("image"));
                        //lo almacenamos en el metodo setSprites para su posterior uso
                        pokemon.setSprites(sprites);
                        // Manejo de tipos, en el objeto Pokemon hay una clase llamada TypeSlot la forma de acceder es la que se muestra a continuacion
                        //y de esta forma se crea un array vacio de tipo Pokemon.TypeSlot
                        List<Pokemon.TypeSlot> types = new ArrayList<>();
                        //Como un pokemon puede tener mas de un tipo esto de almacena en una lista de String y en esta lista
                        //se almacena lo que viene de firestores pero como lo que viene de firestore es de un tipo de dato se hace un casteo para
                        //convertirlo a una lista de String
                        List<String> typeNames = (List<String>) document.get("types");
                        //Una vez realizada la transformacion se verifica que la lista no sea nula
                        if (typeNames != null) {
                            //Si no es nula la lista se recorre
                            for (String typeName : typeNames) {
                                //Se obtiene el tipo o tipos de cada pokemon, esta es la forma de acceder que para que se establezca
                                //es necesario llamar a 2 clases para que se pueda acceder a la propiedad nombre porque asi es como esta
                                //en el json que devuelve pokeapi
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
                    //Se establecen los valores y esos valores se guardan en capturedPokemons
                    sharedViewModel.updateCapturedPokemons(pokemons);
                })
                .addOnFailureListener(e -> {
                    // En caso de error, establece la lista como vacía
                    sharedViewModel.updateCapturedPokemons(new ArrayList<>());
                });
    }
}


