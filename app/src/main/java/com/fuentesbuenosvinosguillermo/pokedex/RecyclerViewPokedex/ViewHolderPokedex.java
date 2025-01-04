package com.fuentesbuenosvinosguillermo.pokedex.RecyclerViewPokedex;

import android.graphics.Color;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;

import com.fuentesbuenosvinosguillermo.pokedex.R;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.PokedexCardviewBinding;

public class ViewHolderPokedex extends RecyclerView.ViewHolder {
    private final PokedexCardviewBinding binding;

    // Constructor
    public ViewHolderPokedex(PokedexCardviewBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    // Método bind para vincular los datos al layout
    public void bind(Pokemon pokemon) {
        // Establecer el nombre del Pokémon
        binding.nombrepokemonpokedek.setText(pokemon.getName());

        // Cargar la imagen del Pokémon (usando Glide) con la URL de las imágenes
        Glide.with(binding.getRoot().getContext())
                .load(pokemon.getSprites().getFrontDefault()) // Usar la URL de la imagen por defecto
                .into(binding.imagepokedex);

        // Ejecutar binding pendiente (si es necesario)
        binding.executePendingBindings();
    }


}
