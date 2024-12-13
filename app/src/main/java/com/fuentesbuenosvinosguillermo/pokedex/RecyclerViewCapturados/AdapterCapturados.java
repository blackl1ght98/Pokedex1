package com.fuentesbuenosvinosguillermo.pokedex.RecyclerViewCapturados;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.Fragments.DetallesPokemonCapturado;
import com.fuentesbuenosvinosguillermo.pokedex.Fragments.Pokedex;
import com.fuentesbuenosvinosguillermo.pokedex.Fragments.pokemonCapturados;
import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.SharedViewModel;
import com.fuentesbuenosvinosguillermo.pokedex.MainActivity;
import com.fuentesbuenosvinosguillermo.pokedex.R;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.PokemonCapturadosCardviewBinding;

import java.util.List;
/**
 * Clase encargada de configurar el RecyclerView
 * */
public class AdapterCapturados extends RecyclerView.Adapter<ViewHolderCapturados> {
    private SharedViewModel sharedViewModel;
    private MainActivity activity;
/**
 * Se crea un constructor que inicializa el sharedviewmodel y la actividad principal
 * */
    public AdapterCapturados(MainActivity activity) {
        this.activity = activity;
        // Obtener la referencia del ViewModel
        sharedViewModel = new ViewModelProvider(activity).get(SharedViewModel.class);
    }

    @NonNull
    @Override
    public ViewHolderCapturados onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PokemonCapturadosCardviewBinding binding = PokemonCapturadosCardviewBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolderCapturados(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCapturados holder, int position) {
        // Obtener el Pokémon de la lista en el ViewModel
        List<Pokemon> pokemons = sharedViewModel.getCapturedPokemons().getValue();
        if (pokemons != null) {
            //Si en la lista hay pokemon se obtiene el pokemon en base a la posicion
            Pokemon pokemon = pokemons.get(position);

            // Vincular los datos del Pokémon al ViewHolder
            holder.bind(pokemon);

            // Configurar el clic para seleccionar un Pokémon
            holder.itemView.setOnClickListener(v -> {
                sharedViewModel.setSelectedPokemon(pokemon);

                // Navegar al fragmento de detalles
                DetallesPokemonCapturado detallesFragment = new DetallesPokemonCapturado();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, detallesFragment, "DetallesFragment") // Etiqueta para identificarlo
                        .addToBackStack(null)
                        .commit();
                activity.findViewById(R.id.viewPager).setVisibility(View.GONE);

            });
        }
    }

    @Override
    public int getItemCount() {
        // Retornar el tamaño de la lista directamente desde el ViewModel
        List<Pokemon> pokemons = sharedViewModel.getCapturedPokemons().getValue();
        return (pokemons != null) ? pokemons.size() : 0;
    }
}

