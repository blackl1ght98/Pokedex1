package com.fuentesbuenosvinosguillermo.pokedex.RecyclerViewPokedex;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokemonResult;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokeApiService;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.PokedexCardviewBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Adapter extends RecyclerView.Adapter<ViewHolder> {
    private final List<PokemonResult> pokemonList;
    private final OnItemClickListener listener;
    private final PokeApiService pokeApiService;  // Agregar servicio de API

    // Constructor para recibir la lista de Pokémon, un listener y el servicio de la API
    public Adapter(List<PokemonResult> pokemonList, OnItemClickListener listener, PokeApiService pokeApiService) {
        this.pokemonList = pokemonList;
        this.listener = listener;
        this.pokeApiService = pokeApiService;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout utilizando View Binding
        PokedexCardviewBinding binding = PokedexCardviewBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Obtener el Pokémon de la lista
        PokemonResult pokemonResult = pokemonList.get(position);

        // Realizar la llamada a la API para obtener los detalles del Pokémon
        pokeApiService.getPokemonDetails(pokemonResult.getName()).enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Obtener el objeto Pokemon completo con la información de la imagen
                    Pokemon pokemon = response.body();

                    // Vincular los datos del Pokémon al ViewHolder
                    holder.bind(pokemon);
                }
            }

            @Override
            public void onFailure(Call<Pokemon> call, Throwable t) {
                // Manejar el error si la llamada a la API falla
            }
        });

        // Configurar el clic en el ítem
        holder.itemView.setOnClickListener(v -> listener.onItemClick(pokemonResult));
    }

    @Override
    public int getItemCount() {
        return pokemonList.size();
    }

    // Interfaz para manejar clics en los ítems
    public interface OnItemClickListener {
        void onItemClick(PokemonResult pokemon);
    }
}
