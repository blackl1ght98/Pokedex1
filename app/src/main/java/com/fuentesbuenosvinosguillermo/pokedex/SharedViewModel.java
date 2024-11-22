package com.fuentesbuenosvinosguillermo.pokedex;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;

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
    /**
     * Devuelve el LiveData que representa la lista de Pokémon capturados.
     * Permite a otras clases (como fragmentos) observar los cambios en la lista
     * sin permitirles modificar directamente los datos.
     *
     * Esto garantiza la encapsulación: solo el ViewModel puede actualizar
     * la lista a través de métodos específicos como addCapturedPokemon().
     */
    public LiveData<List<Pokemon>> getCapturedPokemons() {
        return capturedPokemons;
    }
//Metodo que agrega un pokemon a la lista
    public void addCapturedPokemon(Pokemon pokemon) {
        List<Pokemon> currentList = new ArrayList<>(capturedPokemons.getValue());
        currentList.add(pokemon);
        capturedPokemons.setValue(currentList); // Notifica a los observadores que la lista ha cambiado
    }
}
