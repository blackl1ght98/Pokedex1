package com.fuentesbuenosvinosguillermo.pokedex.RecyclerViewCapturados;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.PokemonCapturadosCardviewBinding;

public class ViewHolderCapturados extends RecyclerView.ViewHolder {
    private final PokemonCapturadosCardviewBinding binding;

    // Constructor
    public ViewHolderCapturados(PokemonCapturadosCardviewBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    // Método bind para vincular los datos al layout
    public void bind(Pokemon pokemon) {
        // Establecer el nombre del Pokémon
        binding.nombrepokemon.setText(pokemon.getName());
        //Mover a un fragment de detalles
        binding.ordenPokedex.setText(String.valueOf(pokemon.orderPokedex()));
        binding.pesopokemon.setText(String.valueOf(pokemon.getWeight()));
        binding.alturakemon.setText(String.valueOf(pokemon.getHeight()));
        // Cargar la imagen del Pokémon (usando Glide)
        Glide.with(binding.getRoot().getContext())
                .load(pokemon.getSprites().getFrontDefault()) // URL de la imagen
                .into(binding.imagepokemon);
    }
}
