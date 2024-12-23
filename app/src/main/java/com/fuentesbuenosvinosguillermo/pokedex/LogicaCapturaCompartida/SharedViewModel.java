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
import com.fuentesbuenosvinosguillermo.pokedex.ExitoOFracaso.CaptureResult;
import com.fuentesbuenosvinosguillermo.pokedex.PokedexRepository;
import com.fuentesbuenosvinosguillermo.pokedex.Services.FirestoreService;
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
 * Esta clase se encarga de compartir los datos entre los fragmentos, usa la arquitectura
 * MVV (Model-View-Viewmodel) que esta arquitectura se utiliza para separar las responsabilidades
 * dentro de una aplicación.
 * Lo que más destaca de esta clase es que permite tener centralizada la lógica de manejo de datos
 * en un único sitio.
 */

public class SharedViewModel extends ViewModel {
    /**MutableLiveData: Es un tipo de dato reactivo en Android que permite observar cambios en su valor y notificar automáticamente a sus observadores.
     estos observadores es cuando llamamos a un metodo de esta clase y ponemos un .observe()
     Aqui los datos de tipo MutableLiveData solo pueden ser modificados por la propia clase
     * */
    private final MutableLiveData<List<Pokemon>> capturedPokemons = new MutableLiveData<>(new ArrayList<>());
    //Variable que inicializa firestore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<Pokemon> selectedPokemon = new MutableLiveData<>();
    //Inicializacion de la configuracion para solicitar datos al api
    private PokedexRepository repository= new PokedexRepository();
    private PokeApiService apiService= ConfiguracionRetrofit.getRetrofitInstance().create(PokeApiService.class);
    //Mapeo local que almacena los datos de los pokemon obtenidos
    private final Map<String, Pokemon> cachedPokemonDetails = new HashMap<>();
    private FirestoreService firestoreService= new FirestoreService(db);
    private final MutableLiveData<CaptureResult> captureResultLiveData = new MutableLiveData<>();

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
     * Metodo que devuelve caracteristicas de un pokemon
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

public void capturePokemon(Pokemon pokemon) {
    // Verificar si el Pokémon ya está capturado localmente
    if (CapturedPokemonManager.isCaptured(pokemon)) {
        captureResultLiveData.postValue(new CaptureResult(false, pokemon));
        return;
    }

    // Verificar si el Pokémon ya está en Firestore
    firestoreService.checkPokemonExists(pokemon.getName(), new SharedViewModelInterface.FirestoreCallback() {
        @Override
        public void onSuccess(boolean exists) {
            if (exists) {
                captureResultLiveData.postValue(new CaptureResult(false, pokemon));
            } else {
                capturePokemonInFirestore(pokemon);
            }
        }

        @Override
        public void onFailure(Exception e) {
            captureResultLiveData.postValue(new CaptureResult(false, null));
        }
    });
}

    private void capturePokemonInFirestore(Pokemon pokemon) {
        CapturedPokemonManager.addCapturedPokemon(pokemon);

        firestoreService.savePokemon(pokemon, new SharedViewModelInterface.SaveCallback() {
            @Override
            public void onSuccess(String firestoreId) {
                pokemon.setFirestoreId(firestoreId);
                captureResultLiveData.postValue(new CaptureResult(true, pokemon));
            }

            @Override
            public void onFailure(Exception e) {
                captureResultLiveData.postValue(new CaptureResult(false, null));
            }
        });
    }
    public LiveData<CaptureResult> getCaptureResultLiveData() {
        return captureResultLiveData;
    }

    // Método para obtener los Pokémon capturados
    public void fetchCapturedPokemons() {
        firestoreService.fetchCapturedPokemons(new SharedViewModelInterface.OnPokemonsFetchedListener() { // Asegúrate de usar la interfaz correctamente
            @Override
            public void onPokemonsFetched(List<Pokemon> pokemons) {
                capturedPokemons.setValue(pokemons); // Actualizar el LiveData con los pokemons
            }

            @Override
            public void onPokemonsFetchFailed(Exception e) {
                capturedPokemons.setValue(new ArrayList<>()); // En caso de error, establecer lista vacía
            }
        });
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

    // Método para eliminar un Pokémon
    public void deletePokemonFromFirestore(Pokemon pokemon, SharedViewModelInterface.OnDeleteCallback callback) {
        firestoreService.deletePokemonFromFirestore(pokemon, new SharedViewModelInterface.OnDeleteCallback() {
            @Override
            public void onDelete(boolean success) {
                if (success) {
                    // Si la eliminación es exitosa, puedes actualizar la lista de Pokémon capturados
                    // Por ejemplo, eliminando el Pokémon de la lista local
                    removeCapturedPokemon(pokemon); // Actualiza tu lista local
                }
                callback.onDelete(success); // Notifica al callback del resultado
            }
        });
    }

    /**
     * Metodo que es usado cuando un pokemon se elimina de firestore para que este se elimine localmente tambien
     * esto evita que los datos sean inconsistentes se usa en el metodo llamado deletePokemonFromFirestore()
     * */
    private void removeCapturedPokemon(Pokemon pokemon) {
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
     * Metodo que va al siguiente pokemon en caso de haberlo este metodo es usado en la clase DetallesPokemonCapturado
     *
     * */
    public void getNextPokemon(int currentIndex, SharedViewModelInterface.OnNextPokemonCallback callback) {
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
