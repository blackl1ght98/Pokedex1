package com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.ConfiguracionRetrofit;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokeApiService;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokemonListResponse;
import com.fuentesbuenosvinosguillermo.pokedex.PokedexRepository;
import com.fuentesbuenosvinosguillermo.pokedex.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Una clase que extiende ViewModel, lo que permite es compartir datos entre diferentes componentes de la UI (por ejemplo, Fragmentos)
 * de forma segura mientras se mantiene el ciclo de vida de los datos independiente de las vistas.
 *
 * */
public class SharedViewModel extends ViewModel {
    /**MutableLiveData: Es un tipo de dato reactivo en Android que permite observar cambios en su valor y notificar automáticamente a sus observadores.
     estos observadores es cuando llamamos a un metodo de esta clase y ponemos un .observe()
     * */
    private final MutableLiveData<List<Pokemon>> capturedPokemons = new MutableLiveData<>(new ArrayList<>());
    //Variable que inicializa firestore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<Pokemon> selectedPokemon = new MutableLiveData<>();
    private PokedexRepository repository= new PokedexRepository();
    private PokeApiService apiService= ConfiguracionRetrofit.getRetrofitInstance().create(PokeApiService.class);
    private final Map<String, Pokemon> cachedPokemonDetails = new HashMap<>();



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
   /**
    * Metodo que agrega el pokemon que ha sido capturado a la lista, una vez agregado nos permite obtener los datos almacenados,
    * pero los datos almacenados a nivel local
    * */
   public void addCapturedPokemon(Pokemon pokemon) {
       //Si el objeto pokemon es nulo sale de la ejecucion del metodo
       if (pokemon == null) return;
        //Como capturedPokemons es una lista tenemos que declarar una variable de tipo lista para almacenar el valor de cada elemento de la lista
       List<Pokemon> currentList = capturedPokemons.getValue();
       //Si la lista que hemos declarado es nula
       if (currentList == null) {
           //Crea una nueva lista
           currentList = new ArrayList<>();
       }
       //Agrega el pokemon a esa lista
       currentList.add(pokemon);
       Log.d("SharedViewModel", "Pokemon añadido: " + pokemon.getName() + " - Total: " + currentList.size());
       //Una vez el pokemon es agregado notificamos el cambio a los observadores
       capturedPokemons.setValue(currentList);
   }
/**
 * Metodo que elimina un pokemon de la lista de capturados, pero ¿porque se coloca aqui de nuevo el metodo pero con logica distinta?
 * el motivo es para actualizar los fragmentos involucrados en tiempo real, este metodo recibe por parametro un objeto
 * @param  pokemon este objeto contiene la informacion del pokemon
 *
 * */
    public void removeCapturedPokemon(Pokemon pokemon) {
        //Lista que almacena los valores de la lista capturedPokemons
        List<Pokemon> currentList = capturedPokemons.getValue();
        if (currentList != null) {
            currentList.remove(pokemon); // Elimina el Pokémon de la lista
            capturedPokemons.setValue(currentList); // Notifica los cambios
            Log.d("SharedViewModel", "Pokemon eliminado: " + pokemon.getName());
        } else {
            Log.d("SharedViewModel", "La lista de Pokémon está vacía o nula.");
        }
    }
//Metodo encargado de limpiar la lista
    public void clearCapturedPokemons() {
        capturedPokemons.setValue(new ArrayList<>());
        Log.d("SharedViewModel", "Lista de Pokémon capturados limpiada.");
    }
    //Metodo encargado de traer toda la información de los pokemons que hay en firestore
    public void fetchCapturedPokemons() {
        //Llamamos a la coleccion que tenemos en firestore
        db.collection("captured_pokemons")
                //Obtenemos los datos de esa coleccion
                .get()
                //Si no hay errores devuelve el resultado de esa consulta y se almacena en la variable queryDocumentSnapshots
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    //Se declara un array vacio de tipo Pokemon que lo que hace es almacenar la informacion de los pokemon
                    List<Pokemon> pokemons = new ArrayList<>();
                    //Como firestore es una base de datos de tipo NoSQL lo que recorremos es el documento que crea para cada pokemon y obtenemos
                    //la informacion de cada pokemon
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
                    capturedPokemons.setValue(pokemons);
                })
                .addOnFailureListener(e -> {
                    // En caso de error, establece la lista como vacía
                    capturedPokemons.setValue(new ArrayList<>());
                });
    }
    /**
     * Este metodo es encargado de encontrar un pokemon por el nombre este pokemon se busca en la lista compartida
     * */
    public Pokemon findPokemonByName(String name) {
        List<Pokemon> currentList = capturedPokemons.getValue();
        if (currentList != null) {
            for (Pokemon pokemon : currentList) {
                if (pokemon.getName().equals(name)) {
                    return pokemon;
                }
            }
        }
        return null;
    }
    /**
     * Elimina un Pokémon capturado de la colección "captured_pokemons" en Firestore.
     * Utiliza un callback (OnDeleteCallback) para notificar al llamador cuando la operación
     * asincrónica se completa, ya sea con éxito o fallo.
     *
     * @param pokemon El Pokémon que se desea eliminar.
     * @param callback Interfaz que define la acción a realizar al finalizar la operación:
     *                 - callback.onDelete(true): Eliminación exitosa.
     *                 - callback.onDelete(false): Fallo en la eliminación.
     *
     * El callback permite que el llamador maneje la lógica posterior (como mostrar mensajes
     * o actualizar la interfaz) sin acoplar esa lógica dentro de este método.
     * Ademas este metodo de eliminar un pokemon lo elimina en base al nombre y la id de firestore
     */
    public void deletePokemonFromFirestore(Pokemon pokemon, OnDeleteCallback callback) {
        db.collection("captured_pokemons")
                .whereEqualTo("name", pokemon.getName())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            db.collection("captured_pokemons").document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        removeCapturedPokemon(pokemon);
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

    public interface OnDeleteCallback {
        void onDelete(boolean success);
    }
    /**
     * Metodo que verifija si hay pokemon capturados
     *
     * */
    public boolean hasPokemons() {
        List<Pokemon> currentList = capturedPokemons.getValue();
        return currentList != null && !currentList.isEmpty();
    }
    /**
     * Obtiene el siguiente Pokémon de la lista de pokémon capturados según el índice actual.
     * Usa un callback (OnNextPokemonCallback) para enviar el Pokémon recuperado
     * al llamador de manera flexible y desacoplada.
     *
     * @param currentIndex Índice actual del Pokémon en la lista.
     * @param callback Interfaz que define la acción a realizar con el Pokémon recuperado:
     *                 - callback.onNextPokemon(pokemon): Devuelve el siguiente Pokémon de la lista.
     *
     * Usar un callback permite que este método sea más genérico, dejando la lógica
     * posterior (como actualizar la interfaz) al llamador.
     */
    public void getNextPokemon(int currentIndex, OnNextPokemonCallback callback) {
        // Obtiene la lista actual de pokémon capturados.
        List<Pokemon> currentList = capturedPokemons.getValue();

        // Verifica que la lista no esté vacía o nula.
        if (currentList != null && !currentList.isEmpty()) {
            // Calcula el índice del próximo Pokémon a retornar.
            int nextIndex = Math.min(currentIndex, currentList.size() - 1);

            // Llama al callback pasando el Pokémon encontrado.
            callback.onNextPokemon(currentList.get(nextIndex));
        }
    }


    public interface OnNextPokemonCallback {
        void onNextPokemon(Pokemon pokemon);
    }

    public void setSelectedPokemon(Pokemon pokemon) {
        selectedPokemon.setValue(pokemon);
    }

    public LiveData<Pokemon> getSelectedPokemon() {
        return selectedPokemon;
    }
    // Método para capturar Pokémon desde el ViewModel
    public void capturePokemon(Pokemon pokemon, Context context) {
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
                        // Aquí agregamos el Pokémon a la lista local (suponiendo que 'currentList' es la lista de Pokémon capturados)
                        List<Pokemon> currentList = capturedPokemons.getValue(); // Obtener la lista actual desde el LiveData
                        if (currentList == null) {
                            currentList = new ArrayList<>();
                        }
                        currentList.add(pokemon); // Añadir el Pokémon capturado a la lista
                        capturedPokemons.setValue(currentList);
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejo de errores si la consulta falla
                    Log.e("Firestore", "Error al verificar captura en Firestore: " + e.getMessage());
                    Toast.makeText(context, "Error al verificar el estado del Pokémon", Toast.LENGTH_SHORT).show();
                });
    }

    private void showAlreadyCapturedDialog(Context context, Pokemon pokemon) {
        new AlertDialog.Builder(context)
                .setTitle("Pokémon ya Capturado")
                .setMessage(pokemon.getName() + " ya está capturado.")
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void capturePokemonInFirestore(Pokemon pokemon, Context context) {
        // Añadir el Pokémon a la lista de capturados localmente
        CapturedPokemonManager.addCapturedPokemon(pokemon);

        // Crear los datos del Pokémon para Firestore
        Map<String, Object> pokemonData = new HashMap<>();
        pokemonData.put("name", pokemon.getName());
        pokemonData.put("weight", pokemon.getWeight());
        pokemonData.put("height", pokemon.getHeight());
        pokemonData.put("orderPokedex", pokemon.orderPokedex());
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

    private void showCaptureSuccessDialog(Context context, Pokemon pokemon) {
        // Mostrar un diálogo de éxito al capturar el Pokémon
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.captura))
                .setMessage(pokemon.getName() + " " + context.getString(R.string.capturado))
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .show();
    }
    public LiveData<PokemonListResponse> getPokemonList(int offset, int limit) {
        return repository.fetchPokemonList(offset, limit);
    }
    public void fetchPokemonDetails(String pokemonName) {
        if (cachedPokemonDetails.containsKey(pokemonName)) {
            selectedPokemon.setValue(cachedPokemonDetails.get(pokemonName));
            return;
        }

        apiService.getPokemonDetails(pokemonName).enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Pokemon pokemon = response.body();
                    cachedPokemonDetails.put(pokemonName, pokemon); // Cachear los detalles
                    selectedPokemon.setValue(pokemon);
                }
            }

            @Override
            public void onFailure(Call<Pokemon> call, Throwable t) {
                selectedPokemon.setValue(null);
            }
        });
    }

}
