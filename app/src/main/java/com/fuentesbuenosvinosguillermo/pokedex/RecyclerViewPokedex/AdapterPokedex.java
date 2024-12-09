package com.fuentesbuenosvinosguillermo.pokedex.RecyclerViewPokedex;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.ViewGroup;


import androidx.annotation.NonNull;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokemonResult;


import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.SharedViewModel;


import com.fuentesbuenosvinosguillermo.pokedex.databinding.PokedexCardviewBinding;



import java.util.List;

/**
 * Esta clase es parte de la configuracion del recyclerview
 * */
public class AdapterPokedex extends RecyclerView.Adapter<ViewHolderPokedex> {
    private final List<PokemonResult> pokemonList;
    private final Context context;
    private final FragmentActivity activity;
    private PokedexCardviewBinding binding;

    // Constructor
    public AdapterPokedex(List<PokemonResult> pokemonList,  Context context, FragmentActivity activity) {
        this.pokemonList = pokemonList;

        this.context = context;
        this.activity = activity;

    }

    /**
     * Inflar el layout para cada item del RecyclerView.
     * Este método es llamado cuando el RecyclerView necesita crear una nueva vista para un ítem.
     *
     * @param parent El grupo de vistas padre al que el ViewHolder pertenece.
     * @param viewType El tipo de vista que se debe crear (no se usa en este caso).
     * @return Un nuevo ViewHolder con el binding de cada item del RecyclerView.
     */
    @NonNull
    @Override
    public ViewHolderPokedex onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout utilizando View Binding
        binding = PokedexCardviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolderPokedex(binding);
    }

    /**
     * Vincula los datos de un Pokémon a un ViewHolder. Este método es llamado para cada ítem visible.
     *
     * @param holder El ViewHolder donde se mostrará el Pokémon.
     * @param position La posición del Pokémon dentro de la lista de Pokémon.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolderPokedex holder, int position) {
        // Obtener el Pokémon de la lista en la posición actual
        PokemonResult pokemonResult = pokemonList.get(position);

        // Obtener el ViewModel para manejar los Pokémon
        SharedViewModel sharedViewModel = new ViewModelProvider(activity).get(SharedViewModel.class);

        // Hacer la solicitud para obtener los detalles del Pokémon
        sharedViewModel.fetchPokemons(pokemonResult.getName());

        // Observar los cambios en el Pokémon seleccionado
        sharedViewModel.getSelectedPokemon().observe(activity, pokemon -> {
            // Verificar si el Pokémon observado corresponde al de la lista
            if (pokemon != null && pokemon.getName().equals(pokemonResult.getName())) {
                // Vincular los datos del Pokémon al ViewHolder
                holder.bind(pokemon);

                // Establecer un listener de clic en el item para capturar el Pokémon
                holder.itemView.setOnClickListener(v -> handlePokemonCapture(pokemon));
            }
        });
    }

    /**
     * Maneja la captura de un Pokémon cuando el usuario hace clic en un ítem.
     * Llama al ViewModel para registrar la captura del Pokémon.
     *
     * @param pokemon El Pokémon que fue capturado.
     */
    private void handlePokemonCapture(Pokemon pokemon) {
        // Obtener el ViewModel para manejar los Pokémon
        SharedViewModel viewModel = new ViewModelProvider(activity).get(SharedViewModel.class);

        // Registrar la captura del Pokémon
        viewModel.capturePokemon(pokemon, context);
    }

    /**
     * Devuelve el número total de ítems en la lista de Pokémon.
     *
     * @return El tamaño de la lista de Pokémon.
     */
    @Override
    public int getItemCount() {
        // Retornar el tamaño de la lista de Pokémon
        return pokemonList.size();
    }
}
