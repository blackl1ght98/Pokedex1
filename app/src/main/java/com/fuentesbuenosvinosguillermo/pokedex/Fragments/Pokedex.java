package com.fuentesbuenosvinosguillermo.pokedex.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.CapturedPokemonManager;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.ConfiguracionRetrofit;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokeApiService;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokemonListResponse;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokemonResult;
import com.fuentesbuenosvinosguillermo.pokedex.PokedexRepository;
import com.fuentesbuenosvinosguillermo.pokedex.RecyclerViewPokedex.AdapterPokedex;
import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.SharedViewModel;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.FragmentPokedexBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Pokedex extends Fragment {

    private FragmentPokedexBinding binding;
    private AdapterPokedex adapterPokedex;
    private List<PokemonResult> pokemonList = new ArrayList<>();
    private PokeApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Configurar View Binding
        binding = FragmentPokedexBinding.inflate(inflater, container, false);
        apiService = ConfiguracionRetrofit.getRetrofitInstance().create(PokeApiService.class);
        // Configurar RecyclerView
        setupRecyclerView();

        // Cargar datos de Pokémon
        fetchPokemonList();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        // Crear una instancia de PokedexRepository
        PokedexRepository repository = new PokedexRepository();  // No necesitas llamar a getPokeApiService

        // Configurar el LayoutManager y el Adapter
        binding.pokedexRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        //Inicializa el adapter con la lista de pokemon
        adapterPokedex = new AdapterPokedex(pokemonList, pokemonResult -> {
            String pokemonName = pokemonResult.getName();


            /**
             * La llamada se realiza a la segunda peticion get de PokeApiService esta peticion es la que devuelve informacion especifica de cada pokemon
             * cuando se realiza la llamada a 'getPokemonDetails' se le pasa un parametro:
             * @param pokemonName este parametro es el nombre del pokemo
             * seguidamente con el metodo .enqueue() hace que se ejecute de forma asincrona la peticion, este metodo recibe un parametro
             * @param Callback<Pokemon> que la clase callback se comunica con la respuesta del servidor y esa respuesta se pasa a una clase Pokemon
             *                          que previamente hemos creado que cuenta con lo necesario para manejar la respuesta del servidor
             * */
            apiService.getPokemonDetails(pokemonName).enqueue(new Callback<Pokemon>() {
                /**Metodo onResponse
                 * este metodo es el encargado de manejar la llamada y respuesta del servidor y la aplicacion y recibe dos parametros
                 * @param call que es el que realiza la llamada a la api de pokeapi y lo pasa al la clase Pokemon esto se consigue con
                 *             Call<Pokemon> y esta llamada a la api es pasada al segundo parametro.
                 * @param  response esta es la respuesta del api y esta respuesta la manejamos en la clase Pokemon para ello se ha puesto
                 *                  Response<Pokemon>, un punto a destacar que antes de hacer todo esto se ha analizado la respuesta que da
                 *                  la api y se ha montado una clase adaptada a esa respuesta
                 * */
                @Override
                public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                    //Si la respuesta del servidor es exitosa, y ademas la respuesta tiene contenido en el cuerpo de la peticion
                    if (response.isSuccessful() && response.body() != null) {
                        //La respuesta se guarda en la variable pokemon de tipo Pokemon
                        Pokemon pokemon = response.body();
                        //Hacemos una llamada a la clase que se encarga de decidir si un pokemon esta capturado o no y si no lo esta lo captura
                        //y notifica al SharedViewModel para que actualice la vista en tiempo real de pokemon capturados
                        if (!CapturedPokemonManager.isCaptured(pokemon)) {
                            // Agregar al CapturedPokemonManager
                            CapturedPokemonManager.addCapturedPokemon(pokemon);

                            // Notificar al SharedViewModel
                            /**¿Que es un ViewModel?
                             * Un ViewModel es una clase diseñada para gestionar y almacenar datos relacionados con la interfaz de usuario (UI) de una aplicación.
                             *¿Que es un ViewModelProvider?
                             * Es la clase encargada de crear y obtener el ViewModel, necesita pasarle esto requireActivity()).get(SharedViewModel.class
                             * el metodo requireActivity() esto lo que hace es quese conecte con la actividad padre y el segundo metodo al cual se le
                             * pasa es get(SharedViewModel.class) el viewmodel en si,
                             *
                             * */
                            SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
                            //Dentro del viewmodel tenemos un metodo que se encarga de agregar los pokemon capturados  y lo que recibe es el
                            //pokemon con toda la informacion
                            viewModel.addCapturedPokemon(pokemon);

                            Toast.makeText(getContext(), pokemon.getName() + " ha sido capturado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), pokemon.getName() + " ya está capturado", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error al obtener detalles del Pokémon", Toast.LENGTH_SHORT).show();
                    }
                }

                //En caso de fallo se ejecuta este metodo
                @Override
                public void onFailure(Call<Pokemon> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }, repository.apiService);
        //Pasamos al adapter los pokemon
        binding.pokedexRecyclerview.setAdapter(adapterPokedex);


    }

    private void fetchPokemonList() {
        // Llamar al repositorio para obtener datos
        PokedexRepository repository = new PokedexRepository();
        repository.fetchPokemonList(0, 150, new Callback<PokemonListResponse>() {
            @Override
            public void onResponse(Call<PokemonListResponse> call, Response<PokemonListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Agregar resultados al adaptador y actualizar RecyclerView
                    pokemonList.addAll(response.body().getResults());
                    adapterPokedex.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al cargar los Pokémon", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PokemonListResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
