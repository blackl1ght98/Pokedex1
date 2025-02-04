package com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.ConfiguracionRetrofit;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokeApiService;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokemonListResponse;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokemonResult;
import com.fuentesbuenosvinosguillermo.pokedex.PokedexRepository;
import com.fuentesbuenosvinosguillermo.pokedex.Services.FirestoreService;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Esta clase se encarga de compartir los datos entre los fragmentos, usa la arquitectura
 * MVV (Model-View-Viewmodel) que esta arquitectura se utiliza para separar las responsabilidades
 * dentro de una aplicación.
 * Lo que más destaca de esta clase es que permite tener centralizada la lógica de manejo de datos
 * en un único sitio.
 */

public class SharedViewModel extends ViewModel {
    /**MutableLiveData: Es un tipo de dato reactivo en Android que permite observar cambios en su valor y notificar automáticamente a sus observadores.
     estos observadores es cuando llamamos a un metodo de esta clase y ponemos un .observe()
     Aqui los datos de tipo MutableLiveData solo pueden ser modificados por la propia clase.
     Aqui lo usamos para modificar la lista de pokemon capturados
     * */
    private final MutableLiveData<List<Pokemon>> capturedPokemons = new MutableLiveData<>(new ArrayList<>());
   /**
    * Dependiendo del pokemon que se seleccione muestra la informacion de ese pokemon
    * */
    private final MutableLiveData<Pokemon> selectedPokemon = new MutableLiveData<>();
    //Inicializacion de la configuracion para solicitar datos al api
    private PokedexRepository repository= new PokedexRepository();
    private PokeApiService apiService= ConfiguracionRetrofit.getRetrofitInstance().create(PokeApiService.class);
    //Mapeo local que almacena los datos de los pokemon obtenidos
    private final Map<String, Pokemon> cachedPokemonDetails = new HashMap<>();
    private FirestoreService firestoreService;
    private MutableLiveData<List<PokemonResult>> pokemonList = new MutableLiveData<>(new ArrayList<>());
    public SharedViewModel() {
        
        firestoreService = new FirestoreService(FirebaseFirestore.getInstance());
    }

    /**
     * Método que obtiene la lista de Pokémon desde la API, con un límite y un offset definidos por el usuario.
     * Este método se utiliza en la clase Pokedex para obtener la lista paginada de Pokémon.
     *
     * Nota importante:
     * Aunque los métodos `fetchPokemons()` y `getPokemonList()` residen en clases diferentes (AdapterPokedex y Pokedex, respectivamente),
     * trabajan en conjunto de manera sincronizada como si fueran parte de un único flujo de ejecución:
     *
     * - `getPokemonList()` es llamado en la clase Pokedex para obtener la lista paginada de Pokémon (limitado por el offset y limit)
     *   mediante el uso del repositorio, y devuelve un objeto `LiveData<PokemonListResponse>`.
     *
     * - `fetchPokemons()` se encarga de obtener información adicional sobre cada Pokémon individual, como su nombre y detalles específicos.
     *   Este método es utilizado dentro del `AdapterPokedex` para obtener los detalles de cada Pokémon en la lista.
     *   Si los detalles ya están almacenados en caché, no hace la llamada a la API; de lo contrario, realiza una llamada para obtener los detalles
     *   y los guarda en caché para futuras consultas.
     *
     * En resumen, `getPokemonList()` obtiene la lista de Pokémon y `fetchPokemons()` obtiene detalles de cada Pokémon individualmente
     * para completar la información necesaria para mostrar en la interfaz.
     */
    public LiveData<PokemonListResponse> getPokemonList(int offset, int limit) {
        return repository.fetchPokemonList(offset, limit);
    }



    /**
     * Metodo que realiza la peticion a la api y trae toda la informacion del pokemon en cuestion
     *
     * */
    public void fetchPokemons(String pokemonName) {
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
    /**
     * Metodo que actualiza la lista de pokemons capturados al agregar uno
     * */
    public void updateCapturedPokemons(List<Pokemon> pokemons) {
        if (pokemons != null) {
            capturedPokemons.setValue(pokemons);
        }
    }

/**
 * Metodo que agrega el pokemon capturado a firestore
 * */

    public void capturePokemon(Pokemon pokemon, Context context) {
        // Llamamos al servicio FirestoreService desde el ViewModel
        firestoreService.capturePokemon(pokemon,context,this);

    }
/**
 * Metodo que obtiene los pokemons que existan en firestore
 * */
    public void fetchCapturedPokemons() {
        // Llamamos al servicio FirestoreService desde el ViewModel
        firestoreService.fetchCapturedPokemons(this);

    }


    /**
     * Este método devuelve la lista de Pokémon capturados que está almacenada en `capturedPokemons`.
     *
     * Nota:
     * La lista de Pokémon capturados no se obtiene directamente desde la API, sino que se obtiene de Firestore a través del
     * método `fetchCapturedPokemons()`. Cuando se llama a `fetchCapturedPokemons()`, se realiza una consulta a Firestore
     * para obtener los datos de los Pokémon capturados y luego se actualiza la variable `capturedPokemons` (de tipo `LiveData<List<Pokemon>>`).
     *
     * Este método `getCapturedPokemons()` simplemente devuelve la referencia a la `LiveData` que contiene la lista de Pokémon capturados,
     * que ya ha sido cargada desde Firestore. No es necesario hacer otra llamada a la API, ya que los datos ya están disponibles en la
     * `LiveData` y pueden ser observados para reflejar automáticamente cualquier cambio en la UI.
     *
     * Como `LiveData` se ajusta al ciclo de vida de los componentes que lo observan, cualquier cambio en la lista de Pokémon capturados
     * (por ejemplo, al agregar o eliminar un Pokémon) actualizará automáticamente la interfaz de usuario sin necesidad de intervención manual.
     */
    public LiveData<List<Pokemon>> getCapturedPokemons() {

        return capturedPokemons;
    }
    /**
     * Metodo que es usado en la clase 'AdapterCapturados' este metodo en esa clase forma parte de la logica de que ocurre si se le hace clic
     * al pokemon lo que hace es almacenar la información del pokemon seleccionado para luego mostrar dicha información.
     * */
    public void setSelectedPokemon(Pokemon pokemon) {
        selectedPokemon.setValue(pokemon);
    }
    /**
     * Este metodo es encargado de encontrar un pokemon por el nombre, este metodo es usado en la clase DetallesPokemonCapturado
     * este metodo es parte del proceso de eliminacion
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

    public void deletePokemonFromFirestore(Pokemon pokemon, SharedViewModelInterface.OnDeleteCallback callback) {
        // Llamamos al servicio FirestoreService desde el ViewModel
        firestoreService.deletePokemonFromFirestore(pokemon, callback,this);
        removeCapturedPokemon(pokemon);
    }

    /**
     * Metodo que es usado cuando un pokemon se elimina de firestore para que este se elimine localmente tambien
     * esto evita que los datos sean inconsistentes se usa en el metodo llamado deletePokemonFromFirestore()
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

    /**
     * Metodo que averigua si hay mas pokemon despues de eliminar este metodo es usado en DetallesPokemonCapturado
     * */
    public boolean hasPokemons() {
        List<Pokemon> currentList = capturedPokemons.getValue();
        return currentList != null && !currentList.isEmpty();
    }

    /**
     * Método que obtiene el siguiente Pokémon en la lista de Pokémon capturados.
     *
     * @param currentIndex Índice actual del Pokémon en la lista.
     * @param callback Callback que recibe el siguiente Pokémon y su nuevo índice.
     *
     * Funcionamiento:
     * - Obtiene la lista actual de Pokémon capturados desde `capturedPokemons`.
     * - Si la lista no está vacía ni es nula, calcula el índice del siguiente Pokémon
     *   de manera circular (si llega al final, vuelve al inicio).
     * - Llama al método `onNextPokemon` del callback, pasándole el siguiente Pokémon y su nuevo índice.
     */
    public void getNextPokemon(int currentIndex, SharedViewModelInterface.OnNextPokemonCallback callback) {
        // Obtiene la lista actual de Pokémon capturados
        List<Pokemon> currentList = capturedPokemons.getValue();

        // Verifica si la lista no es nula y contiene elementos
        if (currentList != null && !currentList.isEmpty()) {
            // Calcula el índice del siguiente Pokémon de manera circular
            int nextIndex = (currentIndex + 1) % currentList.size();

            // Llama al callback pasando el siguiente Pokémon y el nuevo índice
            callback.onNextPokemon(currentList.get(nextIndex), nextIndex);
        }
    }

    /**
     * Método que obtiene el Pokémon anterior en la lista de Pokémon capturados.
     *
     * @param currentIndex Índice actual del Pokémon en la lista.
     * @param callback Callback que recibe el Pokémon anterior y su nuevo índice.
     *
     * Funcionamiento:
     * - Obtiene la lista actual de Pokémon capturados desde `capturedPokemons`.
     * - Si la lista no está vacía ni es nula, calcula el índice del Pokémon anterior
     *   de manera circular (si el índice es 0, vuelve al final de la lista).
     * - Llama al método `onNextPokemon` del callback, pasándole el Pokémon anterior y su nuevo índice.
     *
     * Nota:
     * - Se usa la misma interfaz `OnNextPokemonCallback` que en `getNextPokemon`,
     *   permitiendo reutilizar el mismo callback para avanzar o retroceder en la lista.
     */
    public void getPreviousPokemon(int currentIndex, SharedViewModelInterface.OnNextPokemonCallback callback) {
        // Obtiene la lista actual de Pokémon capturados
        List<Pokemon> currentList = capturedPokemons.getValue();

        // Verifica si la lista no es nula y contiene elementos
        if (currentList != null && !currentList.isEmpty()) {
            // Calcula el índice del Pokémon anterior de manera circular
            int previousIndex = (currentIndex - 1 + currentList.size()) % currentList.size();

            // Llama al callback pasando el Pokémon anterior y su nuevo índice
            callback.onNextPokemon(currentList.get(previousIndex), previousIndex);
        }
    }



    /**
     * Metodo que obtiene la información del pokemon que se ha seleccionado para marcarlo como capturado o mostrar sus detalles
     * se usa en las clases 'AdapterPokedex' y 'DetallesPokemonCapturado'
     * */
    public LiveData<Pokemon> getSelectedPokemon() {
        return selectedPokemon;
    }

    /**
     * Metodo para limpiar la lista local, este metodo es llamado solo cuando se cierra sesion
     * */
    public void clearCapturedPokemons() {
        capturedPokemons.setValue(new ArrayList<>());
        Log.d("SharedViewModel", "Lista de Pokémon capturados limpiada.");
    }
}