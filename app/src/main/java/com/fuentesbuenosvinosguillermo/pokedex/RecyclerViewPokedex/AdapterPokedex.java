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


import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase es parte de la configuracion del recyclerview
 * */

public class AdapterPokedex extends RecyclerView.Adapter<ViewHolderPokedex> {
    private final Context context;
    private final FragmentActivity activity;
    private List<PokemonResult> pokemonList = new ArrayList<>(); // Ahora se actualiza dinámicamente
    private SharedViewModel sharedViewModel;

    // Constructor
    public AdapterPokedex(Context context, FragmentActivity activity) {
        this.context = context;
        this.activity = activity;
        this.sharedViewModel = new ViewModelProvider(activity).get(SharedViewModel.class);
    }

    @NonNull
    @Override
    public ViewHolderPokedex onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PokedexCardviewBinding binding = PokedexCardviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolderPokedex(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPokedex holder, int position) {
        // Obtener el Pokémon de la lista en la posición actual
        PokemonResult pokemonResult = pokemonList.get(position);

        // Hacer la solicitud para obtener los detalles del Pokémon
        sharedViewModel.fetchPokemons(pokemonResult.getName());

        // Observar cambios en el Pokémon seleccionado
        sharedViewModel.getSelectedPokemon().observe(activity, pokemon -> {
            if (pokemon != null && pokemon.getName().equals(pokemonResult.getName())) {
                // Vincular los datos del Pokémon al ViewHolder
                holder.bind(pokemon);

                // Establecer un listener de clic en el item para capturar el Pokémon
                holder.itemView.setOnClickListener(v -> handlePokemonCapture(pokemon));
            }
        });
    }

    /**
     * Método para actualizar la lista de Pokémon cuando se obtienen nuevos datos del ViewModel.
     */
    public void updateList(List<PokemonResult> newPokemonList) {
        this.pokemonList = newPokemonList;
        notifyDataSetChanged();
    }

    private void handlePokemonCapture(Pokemon pokemon) {
        sharedViewModel.capturePokemon(pokemon, context);
    }

    @Override
    public int getItemCount() {
        return pokemonList.size();
    }
}
